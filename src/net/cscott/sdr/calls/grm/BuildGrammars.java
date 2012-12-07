package net.cscott.sdr.calls.grm;

import static net.cscott.sdr.util.StringEscapeUtils.escapeJava;
import static net.cscott.sdr.util.Tools.l;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.cscott.sdr.DevSettings;
import net.cscott.sdr.calls.Call;
import net.cscott.sdr.calls.CallDB;
import net.cscott.sdr.calls.Program;
import net.cscott.sdr.calls.ExprFunc.EvaluationException;
import net.cscott.sdr.calls.ast.Expr;
import net.cscott.sdr.calls.grm.Grm.Alt;
import net.cscott.sdr.calls.grm.Grm.Concat;
import net.cscott.sdr.calls.grm.Grm.Mult;
import net.cscott.sdr.calls.grm.Grm.Nonterminal;
import net.cscott.sdr.calls.grm.Grm.Terminal;
import net.cscott.sdr.util.Fraction;

/** Build speech/plain-text grammars for the various programs. */
public class BuildGrammars {

    public static void main(String[] args)
        throws IOException, EvaluationException {
        for (Program p : Program.values()) {
            if (p!=Program.C4 && DevSettings.ONLY_C4_GRAMMAR) // FOR DEBUGGING
                continue; // skip this grammar to speed up the compile
            build(p);
        }
        writeFile("src/net/cscott/sdr/calls/lists/AllGrm.java",
                  EmitJava.INSTANCE.emit());
        writeFile("resources/net/cscott/sdr/recog/sdrdict",
                  EmitDictionary.INSTANCE.emit());
        System.err.println("Done.");
    }
    public static void build(Program program)
        throws IOException, EvaluationException {
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
            if (ra.rule.prec.compareTo(Fraction.ZERO)==0 &&
		!hasNontermRefs(ra.rule.rhs))
		prec = highestPrec;
            ra.rule = rewriteForPrec(ra.rule, prec); 
        }
        // remove left recursion, step 1:
        // pull out left recursive rules; rewrite them as 'suffix' rules
        Set<String> leftRecursiveLHS = new HashSet<String>();
        for (RuleAndAction ra : rules) {
            if (!ra.rule.lhs.startsWith("anything_")) continue;
            if (!(ra.rule.rhs instanceof Grm.Concat)) continue;
            Grm.Concat c = (Grm.Concat) ra.rule.rhs;
            assert c.sequence.size() > 1;
            Grm first = c.sequence.get(0);
            Grm tail = new Grm.Concat(c.sequence.subList(1, c.sequence.size()));
            if (!(first instanceof Grm.Nonterminal)) continue;
            Grm.Nonterminal nt = (Grm.Nonterminal) first;
            if (!(ra.rule.lhs.equals(nt.ruleName))) continue;
            // ok, this sure is left recursive!
            leftRecursiveLHS.add(ra.rule.lhs);
            ra.rule = new Rule(ra.rule.lhs+"_suffix", tail,
                               ra.rule.prec, ra.rule.options);
        }
        // add level-bridging rules
        for (int i=0; i<highestPrec; i++)
            rules.add(new RuleAndAction(new Rule("anything_"+i,
                    new Nonterminal("anything_"+(i+1),null,0),null),"r=a;"));
        // left recursion removal, step 2: add 'suffix' rules at end of
        // regular productions for these nonterminals
        for (RuleAndAction ra : rules) {
            if (!leftRecursiveLHS.contains(ra.rule.lhs)) continue;
            // the null for 'prettyName' here indicates that this nonterminal
            // should never be shown to the user during call completion!
            Grm suffix = new Grm.Mult
                               (new Grm.Nonterminal(ra.rule.lhs+"_suffix", null, -1),
                                Grm.Mult.Type.STAR);
            Grm nrule = new Grm.Concat(l(ra.rule.rhs, suffix));
            ra.rule = new Rule(ra.rule.lhs, nrule,
                               ra.rule.prec, ra.rule.options);
        }
        // add leftable/reversable rules
        for (String s : new String[] { "leftable", "reversable" })
            rules.add(new RuleAndAction(new Rule("anything_"+highestPrec,
                    new Nonterminal(s+"_anything",null,0),null),"r=a;"));
        // add metaconcept rules
        Set<String> allLHS = new HashSet<String>();
        for (RuleAndAction ra: rules)
            allLHS.add(ra.rule.lhs);
        if (allLHS.contains("metaconcept")) {
            rules.add(new RuleAndAction(new Rule("concept",
                   new Nonterminal("metaconcept_concept",null,0),null),"r=a;"));
        }
        // handle case where there are no concepts or metaconcepts.
        for (String s : new String[] { "concept", "metaconcept" }) {
            if (!allLHS.contains(s)) {
                rules.add(new RuleAndAction(new Rule(s,
                   new Nonterminal("VOID",null,0),null),"r=null;"));
            }
        }
        // add parenthesization rule
        rules.add(new RuleAndAction(new Rule("anything_"+highestPrec,
            new Nonterminal("parenthesized_anything", null, 0), null), "r=a;"));
        // start rule.
        rules.add(new RuleAndAction(new Rule("anything",
                new Nonterminal("anything_0",null,0),null),"r=a;"));

        String programName = program.toTitleCase();
        // emit as ANTLR v3 grammar
        writeFile("src/net/cscott/sdr/calls/lists/"+programName+"Grammar.g",
                EmitANTLRv3.emit(programName, rules));
        // emit as JSAPI grammar.
        writeFile("resources/net/cscott/sdr/recog/"+programName+".gram",
                EmitJSAPI.emit(programName, rules));
        // emit as Java source for writing completion engines
        EmitJava.INSTANCE.collect(program, rules);
        // emit trimmed pronunciation dictionary
        EmitDictionary.INSTANCE.collect(program, rules);
    }
    
    private static List<RuleAndAction> mkAction(Call c)
        throws EvaluationException {
        List<RuleAndAction> l = new ArrayList<RuleAndAction>(2);
        for (Rule r : splitTopLevelAlt(c.getRule())) {
            // handle leftable/reversable
            if (r.options.contains(Rule.Option.LEFT) ||
                r.options.contains(Rule.Option.REVERSE)) {
                assert r.options.size()==1;
                String new_lhs = r.options.contains(Rule.Option.LEFT) ?
                    "leftable_anything" : "reversable_anything";
                r = new Rule(new_lhs, r.rhs, r.prec, r.options);
            }
            // handle concepts & supercalls
            if (r.options.contains(Rule.Option.CONCEPT) ||
                r.options.contains(Rule.Option.SUPERCALL)) {
                assert r.options.size()==1;
                r = transformConcept(r);
            }
            if (r.options.contains(Rule.Option.METACONCEPT)) {
                assert r.options.size()==1;
                r = transformMetaConcept(r);
            }
            RuleAndAction ra= mkAction(c.getName(), c.getDefaultArguments(), r);
            l.add(ra);
        }
        return l;
    }
    private static Rule transformConcept(Rule r) {
        Grm g = r.rhs;
        // spoken form should be ".... <anything>"; remove "<anything>"
        // and add to 'concept' production.
        if (g instanceof Grm.Concat) {
            Grm.Concat c = (Grm.Concat) g;
            if (c.sequence.size() > 1) {
                Grm last = c.sequence.get(c.sequence.size()-1);
                if (last instanceof Grm.Nonterminal) {
                    Grm.Nonterminal nt = (Grm.Nonterminal) last;
                    if (nt.ruleName.equals("anything") && nt.param==0) {
                        // okay! munge this production!
                        g = new Grm.Concat(c.sequence.subList
                                           (0, c.sequence.size()-1));
                        return new Rule("concept", g, r.prec);
                    }
                }
            }
        }
        System.err.println("WARNING: concept option ignored for "+g);
        return r;
    }
    private static Rule transformMetaConcept(Rule r) {
        Grm g = r.rhs;
        // spoken form should be ".... <concept> <anything>"; remove
        // "<concept> <anything>" and add to 'metaconcept' production.
        if (g instanceof Grm.Concat) {
            Grm.Concat c = (Grm.Concat) g;
            if (c.sequence.size()>2) {
                Grm penult = c.sequence.get(c.sequence.size()-2);
                Grm last = c.sequence.get(c.sequence.size()-1);
                if (penult instanceof Grm.Nonterminal &&
                    last instanceof Grm.Nonterminal) {
                    Grm.Nonterminal nt1 = (Grm.Nonterminal) penult;
                    Grm.Nonterminal nt2 = (Grm.Nonterminal) last;
                    if (nt1.ruleName.equals("concept") && nt1.param==0 &&
                        nt2.ruleName.equals("anything") && nt2.param==1) {
                        // okay! munge this production!
                        g = new Grm.Concat(c.sequence.subList
                                           (0, c.sequence.size()-2));
                        return new Rule("metaconcept", g, r.prec);
                    }
                }
            }
        }
        System.err.println("WARNING: metaconcept option ignored for "+g);
        return r;
    }
    private static RuleAndAction mkAction(String callName,
                                          List<Expr> defaultArgs, Rule r)
        throws EvaluationException {
        int numArgs = highestNontermParam(r.rhs) + 1;
        NumberParams np = new NumberParams(r.rhs);
        StringBuilder sb = new StringBuilder();
        if (numArgs == 0) {
            sb.append("r=Expr.literal(\"");
            sb.append(escapeJava(callName));
            sb.append("\");");
            return new RuleAndAction(r, sb.toString());
        }
        sb.append("r=new Expr(\"");
        sb.append(escapeJava(callName));
        sb.append('\"');
        // now args
        for (int i=0; i<numArgs; i++) {
            sb.append(',');
            char v = (char)('a'+np.paramToOrder.get(i));
            sb.append(v);
            if (i<defaultArgs.size() && defaultArgs.get(i)!=null) {
                String defaultValue =
                    defaultArgs.get(i).evaluate(String.class, null);
                sb.append("!=null?"+v+":Expr.literal"+
                        "(\""+escapeJava(defaultValue)+"\")");
            }
        }
        // done!
        sb.append(");");
        return new RuleAndAction(r, sb.toString());
    }
    private static List<Rule> splitTopLevelAlt(Rule r) {
        List<Rule> l = new ArrayList<Rule>(2);
        for (Grm g : splitTopLevelAlt(SimplifyGrm.simplify(r.rhs), new ArrayList<Grm>(2)))
            l.add(new Rule(r.lhs, g, r.prec, r.options));
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
    private static Rule rewriteForPrec(Rule r, int prec) {
        // rewrite LHS:
        String ruleName = r.lhs;
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
                 // if leftmost, then use prec, else use prec+1
                 int nprec = (isLeftmost) ? prec : (prec+1);
                 String ruleName = nonterm.ruleName + "_" + nprec;
                 return new Nonterminal(ruleName, nonterm.ruleName, nonterm.param);
            }

            @Override
            public Grm visit(Terminal term) {
                return term;
            }
            
        });
    }
    private static String readFully(Reader r) throws IOException {
	StringBuilder sb = new StringBuilder();
	char buf[] = new char[4096];
	int n;
	while (true) {
	    n = r.read(buf);
	    if (n < 0) return sb.toString(); // done
	    sb.append(buf, 0, n);
	}
    }

    public static void writeFile(String filename, String contents)
    throws IOException {
	File f = new File(filename);
	// only rewrite file if the contents would be different.
	try {
	    if (f.exists() && f.length() == contents.length()) {
		// compare existing contents
		Reader r=new InputStreamReader(new FileInputStream(f), "UTF-8");
		try {
		    if (readFully(r).equals(contents)) {
			System.err.println("Already up to date: "+filename);
			return;
		    }
		} finally { r.close(); }
	    }
	} catch (Throwable t) {
	    /* ignore error during comparison; just write! */
	}
        System.err.println("Writing: "+filename);
	Writer fw = new OutputStreamWriter(new FileOutputStream(f), "UTF-8");
        fw.write(contents);
        fw.close();
    }
}
