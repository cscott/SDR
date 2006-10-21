package net.cscott.sdr.calls.grm;

import java.util.*;

import net.cscott.sdr.calls.Call;
import net.cscott.sdr.calls.CallDB;
import net.cscott.sdr.calls.Program;
import net.cscott.sdr.calls.grm.Grm.Alt;
import net.cscott.sdr.calls.grm.Grm.Concat;
import net.cscott.sdr.calls.grm.Grm.Mult;
import net.cscott.sdr.calls.grm.Grm.Nonterminal;
import net.cscott.sdr.calls.grm.Grm.Terminal;
import net.cscott.sdr.util.Fraction;

/** Build speech/plain-text grammars for the various programs. */
public class BuildGrammars {

    /**
     * @param args
     */
    public static void main(String[] args) {
        final Program program = Program.C4;
        // collect all the grammar rules & make actions.
        List<RuleAndAction> rules = new ArrayList<RuleAndAction>();
        for (Call c : CallDB.INSTANCE.allCalls)
            if (c.getRule()!=null)
                // filter out the appropriate level
                if (program.includes(c.getProgram()))
                    rules.addAll(mkAction(c));

        // collect all the precedence levels.
        Set<Fraction> precSet = new LinkedHashSet<Fraction>();
        for (RuleAndAction ra : rules)
            precSet.add(ra.rule.prec);
        List<Fraction> precList = new ArrayList<Fraction>(precSet);
        Collections.sort(precList);
        // now renumber them.
        Map<Fraction,Integer> precLevel = new HashMap<Fraction,Integer>();
        for (int i=0; i<precList.size(); i++)
            precLevel.put(precList.get(i), i);
        int highestPrec = precList.size();
        // okay, now rewrite each rule to implement proper precedence level.
        for (RuleAndAction ra : rules) {
            int prec = precLevel.get(ra.rule.prec);
            if (!hasNontermRefs(ra.rule.rhs)) prec = highestPrec;
            ra.rule = rewriteForPrec(ra.rule, prec); 
        }
        // XXX add level-bridging rules
        // for (int i=0; i<highestPrec; i++)
        // XXX add leftable/reversable rules
        // TEST ME
        for (RuleAndAction ra : rules)
            System.out.println(ra);
        // XXX remove left recursion
        // XXX make/invoke actions.
        // XXX emit as ANTLR grammar
        // XXX emit as JSSAPI grammar.
    }
    
    private static List<RuleAndAction> mkAction(Call c) {
        List<RuleAndAction> l = new ArrayList<RuleAndAction>(2);
        for (Rule r : splitTopLevelAlt(c.getRule()))
            l.add(mkAction(c.getName(), r));
        return l;
    }
    private static RuleAndAction mkAction(String callName, Rule r) {
        int numArgs = highestNontermParam(r.rhs);
        NumberParams np = new NumberParams(r.rhs);
        StringBuilder sb = new StringBuilder();
        sb.append("r=Apply.makeApply(\"");
        sb.append(callName);
        sb.append('\"');
        // now args
        for (int i=0; i<=numArgs; i++) {
            sb.append(',');
            sb.append((char)('a'+np.paramToOrder.get(i)));
        }
        // done!
        sb.append(");");
        return new RuleAndAction(r, sb.toString());
    }
    private static List<Rule> splitTopLevelAlt(Rule r) {
        List<Rule> l = new ArrayList<Rule>(2);
        for (Grm g : splitTopLevelAlt(SimplifyGrm.simplify(r.rhs), new ArrayList<Grm>(2)))
            l.add(new Rule(r.lhs, g, r.prec));
        return l;
    }
    private static List<Grm> splitTopLevelAlt(Grm g, List<Grm> l) {
        if (!(g instanceof Alt))
            l.add(g);
        else for (Grm gg : ((Alt)g).alternates)
            splitTopLevelAlt(gg, l);
        return l; // for convenience.
    }
    private static boolean hasNontermRefs(Grm g) {
        return highestNontermParam(g)!=-1;
    }
    private static int highestNontermParam(Grm g) {
        return g.accept(new GrmVisitor<Integer>() {
            @Override
            public Integer visit(Alt alt) {
                int max=-1;
                for (Grm g : alt.alternates)
                    max=Math.max(max, g.accept(this));
                return max;
            }
            @Override
            public Integer visit(Concat concat) {
                int max=-1;
                for (Grm g : concat.sequence)
                    max=Math.max(max, g.accept(this));
                return max;
            }
            @Override
            public Integer visit(Mult mult) {
                return mult.operand.accept(this);
            }
            @Override
            public Integer visit(Nonterminal nonterm) {
                return nonterm.param;
            }
            @Override
            public Integer visit(Terminal term) {
                return -1;
            }
        });
    }
    private static class NumberParams extends GrmVisitor<Void> {
        public final Map<Integer,Integer> paramToOrder =
            new HashMap<Integer,Integer>();
        private int order = 0;
        private boolean inMult=false;
        public NumberParams(Grm g) { g.accept(this); }
        @Override
        public Void visit(Alt alt) {
            int o = order;
            for (Grm g : alt.alternates) {
                order = o;
                g.accept(this);
            }
            return null;
        }
        @Override
        public Void visit(Concat concat) {
            for (Grm g : concat.sequence)
                g.accept(this);
            return null;
        }
        @Override
        public Void visit(Mult mult) {
            boolean m = inMult;
            if (mult.type!=Mult.Type.QUESTION)
                inMult=true;
            mult.operand.accept(this);
            inMult=m;
            return null;
        }
        @Override
        public Void visit(Nonterminal nonterm) {
            int o = order++;
            if (nonterm.param>=0) {
                assert !inMult : "named non-terminal can repeat";
                // check that nonterm is not already present.
                if (paramToOrder.containsKey(nonterm.param))
                    assert o == paramToOrder.get(nonterm.param) :
                        "nonterminal used with different ordering "+
                        "in alternatives";
                paramToOrder.put(nonterm.param, o);
            }
            return null;
        }
        @Override
        public Void visit(Terminal term) {
            // nothing to see here.
            return null;
        }
    }
    private static Rule rewriteForPrec(Rule r, int prec) {
        // rewrite LHS:
        String ruleName =r.lhs;
        if (ruleName.equals("anything")) ruleName+="_"+prec;
        return new Rule(ruleName, rewriteForPrec(r.rhs, prec), null);
    }
    private static Grm rewriteForPrec(Grm g, final int prec) {
        return g.accept(new GrmVisitor<Grm>() {
            private boolean isLeftmost=true;
            
            @Override
            public Grm visit(Alt alt) {
                List<Grm> l = new ArrayList<Grm>(alt.alternates.size());
                // save isLeftmost; restore it before
                // traversing each alt.
                boolean myLeft = isLeftmost;
                for (Grm g : alt.alternates) {
                    isLeftmost = myLeft;
                    l.add(g.accept(this));
                    // note: broken if empty alternatives
                }
                return new Alt(l);
            }

            @Override
            public Grm visit(Concat concat) {
                List<Grm> l = new ArrayList<Grm>(concat.sequence.size());
                for (Grm g : concat.sequence) {
                    l.add(g.accept(this));
                    isLeftmost = false; // after the first
                }
                return new Concat(l);
            }

            @Override
            public Grm visit(Mult mult) {
                Grm operand = mult.operand.accept(this);
                isLeftmost = false;// note: if operand is nullable, not accurate
                return new Mult(operand, mult.type);
            }

            @Override
            public Grm visit(Nonterminal nonterm) {
                // add precedence level to the name of the
                // nonterminal *if* it was 'anything'.
                 if (!nonterm.ruleName.equals("anything"))
                     return nonterm;
                 // if leftmost, then use prec, else use
                 // prec+1.
                 int nprec = (isLeftmost) ? prec : (prec+1);
                 String ruleName = nonterm.ruleName + "_" + nprec;
                 return new Nonterminal(ruleName, nonterm.param);
            }

            @Override
            public Grm visit(Terminal term) {
                return term;
            }
            
        });
    }
}
