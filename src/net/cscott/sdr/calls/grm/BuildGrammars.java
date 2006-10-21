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
        // collect all the grammar rules.
        Map<Call,Rule> rules = new LinkedHashMap<Call,Rule>();
        for (Call c : CallDB.INSTANCE.allCalls)
            if (c.getRule()!=null)
                // filter out the appropriate level
                if (program.includes(c.getProgram()))
                    rules.put(c, c.getRule());
        // collect all the precedence levels.
        Set<Fraction> precSet = new LinkedHashSet<Fraction>();
        for (Rule r : rules.values())
            precSet.add(r.prec);
        List<Fraction> precList = new ArrayList<Fraction>(precSet);
        Collections.sort(precList);
        // now renumber them.
        Map<Fraction,Integer> precLevel = new HashMap<Fraction,Integer>();
        for (int i=0; i<precList.size(); i++)
            precLevel.put(precList.get(i), i);
        int highestPrec = precList.size();
        // okay, now rewrite each rule to implement proper precedence level.
        for (Call c : rules.keySet()) {
            Rule r = rules.get(c);
            int prec = precLevel.get(r.prec);
            if (!hasNontermRefs(r.rhs)) prec = highestPrec;
            r = rewriteForPrec(r, prec);
            rules.put(c, r); // replace in map.
        }
        // XXX add level-bridging rules
        // for (int i=0; i<highestPrec; i++)
        // XXX add leftable/reversable rules
        // TEST ME
        for (Call c : rules.keySet())
            System.out.println(rules.get(c));
        // XXX remove left recursion
        // XXX make/invoke actions.
        // XXX emit as ANTLR grammar
        // XXX emit as JSSAPI grammar.
    }

    private static boolean hasNontermRefs(Grm g) {
        return g.accept(new GrmVisitor<Boolean>() {
            @Override
            public Boolean visit(Alt alt) {
                for (Grm g : alt.alternates)
                    if (g.accept(this)) return true;
                return false;
            }
            @Override
            public Boolean visit(Concat concat) {
                for (Grm g : concat.sequence)
                    if (g.accept(this)) return true;
                return false;
            }
            @Override
            public Boolean visit(Mult mult) {
                return mult.operand.accept(this);
            }
            @Override
            public Boolean visit(Nonterminal nonterm) {
                return true;
            }
            @Override
            public Boolean visit(Terminal term) {
                return false;
            }
        });
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
