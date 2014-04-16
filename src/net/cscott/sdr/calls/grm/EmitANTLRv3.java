package net.cscott.sdr.calls.grm;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.cscott.jutil.MultiMap;
import net.cscott.sdr.calls.grm.Grm.Mult;
import net.cscott.sdr.calls.grm.Grm.Nonterminal;
import net.cscott.sdr.calls.grm.Grm.Terminal;

/** Emit an ANTLRv3 "natural language" grammar for calls. */
public class EmitANTLRv3 extends AbstractEmit {
    // productions above this size will be broken up
    private static int MAXIMUM_ALTERNATES = 250;

    private NumberParams np=null;
    private EmitANTLRv3() { }
    public static String emit(String parserName,
                              List<RuleAndAction> l) {
        EmitANTLRv3 ea = new EmitANTLRv3();
        // collect all the rules with the same LHS
        MultiMap<String,RuleAndAction> mm = collectLHS(l);
        // break up any rules with too many alternates in order to
        // avoid "method too large" errors from the java compiler
        splitLargeProductions(mm);
        // now emit the rules.
        String NL = System.getProperty("line.separator");
        StringBuilder sb = new StringBuilder();
        for (String lhs : sorted(mm.keySet())) {
            if (lhs.endsWith("_suffix"))
                sb.append("fragment "+lhs+"[Expr a]");
            else
                sb.append(lhs);
            sb.append(" returns [Expr r]");
            sb.append(NL);
            boolean first = true;
            for (RuleAndAction ra : mm.getValues(lhs)) {
                if (first) { sb.append("\t: "); first = false; }
                else { sb.append("\t| "); }
                ea.np = new NumberParams(ra.rule.rhs,
                                         lhs.endsWith("_suffix") ? 1 : 0);
                // look for a _suffix; if present then emit only the first part
                Grm[] g = matchSuffix(ra.rule.rhs);
                sb.append(g[0].accept(ea)); // head part before suffix
                sb.append(" { "+ra.action+" }");
                if (g[1] != null) { // suffix part here, special treatment
                    sb.append(NL);
                    sb.append("\t  ( rr=");
                    String suf = ((Grm.Nonterminal) g[1]).ruleName;
                    sb.append(suf);
                    sb.append("[r] { r=rr; })*");
                }
                sb.append(NL);
            }
            sb.append("\t;"+NL);
        }
        // substitute the rules & the classname into the skeleton
        String result = ea.subst("antlr-v3.skel", sb.toString(), parserName);
        // done.
        return result;
    }
    private static void splitLargeProductions(final MultiMap<String,RuleAndAction> rules) {
        List<String> toSplit = new ArrayList<String>();
        for (String lhs : rules.keySet())
            if (rules.getValues(lhs).size() > MAXIMUM_ALTERNATES)
                toSplit.add(lhs);
        for (String lhs: toSplit) {
            List<RuleAndAction> rhs =
                new ArrayList<RuleAndAction>(rules.getValues(lhs));
            // split list in half
            List<RuleAndAction> rhs1 = rhs.subList(0, rhs.size()/2);
            List<RuleAndAction> rhs2 = rhs.subList(rhs1.size(), rhs.size());
            // name new part
            String nlhs = uniqueLHS(lhs, rules.keySet());
            // tie new lhs to old lhs
            rules.remove(lhs);
            rules.addAll(lhs, rhs1);
            rules.add(lhs, new RuleAndAction
                      (new Rule(lhs, new Nonterminal(nlhs, null, 0), null),
                       "r=a;"));
            // rewrite rhs2 to use new lhs
            for (RuleAndAction ra : rhs2) {
                Rule r = ra.rule;
                rules.add(nlhs, new RuleAndAction
                          (new Rule(nlhs, r.rhs, r.prec, r.options),
                           ra.action));
            }
        }
        // recurse to split again in case we're still not small enough.
        if (!toSplit.isEmpty())
            splitLargeProductions(rules);
    }
    private static String uniqueLHS(String s, Set<String> lhs) {
        s = s.replaceFirst("(_split)+$", "");
        for(int i = 1; ; i++) {
            String candidate = s + "_split" + i;
            if (!lhs.contains(candidate))
                return candidate;
        }
    }
    private static Grm[] matchSuffix(Grm rhs) {
        // oh, for a pattern-matching language, oh
        // Grm.Concat(....., Grm.Mult(Grm.NonTerminal(..."_suffix"), STAR))
        if (!(rhs instanceof Grm.Concat)) return nomatch(rhs);
        Grm.Concat c = (Grm.Concat) rhs;
        if (c.sequence.size() < 2)
            return matchSuffix(c.sequence.get(0));
        List<Grm> headseq = c.sequence.subList(0, c.sequence.size()-1);
        Grm head=(headseq.size()==1) ? headseq.get(0) : new Grm.Concat(headseq);
        Grm tail = c.sequence.get(c.sequence.size()-1);
        if (!(tail instanceof Grm.Mult)) return nomatch(rhs);
        Grm.Mult m = (Grm.Mult) tail;
        if (m.type != Grm.Mult.Type.STAR) return nomatch(rhs);
        if (!(m.operand instanceof Grm.Nonterminal)) return nomatch(rhs);
        Grm.Nonterminal nt = (Grm.Nonterminal) m.operand;
        if (!nt.ruleName.endsWith("_suffix")) return nomatch(rhs);
        // A match! (whew!)
        return new Grm[] { head, nt };
    }
    private static Grm[] nomatch(Grm g) { return new Grm[] { g, null }; }

    @Override
    public String visit(Terminal t) {
        // quote literals.
        return quote(t.literal);
    }
    @Override
    public String visit(Mult mult) {
        // always parenthesize multiplicity markers.
        return "("+mult.operand.accept(this)+")"+mult.type;
    }
    @Override
    public String visit(Nonterminal nt) {
        StringBuilder sb = new StringBuilder();
        if (nt.param>=0) {
            sb.append((char)('a'+np.paramToOrder.get(nt.param)));
            sb.append('=');
        }
        sb.append(nt.ruleName);
        return sb.toString();
    }
    private static String quote(String s) {
        // XXX use full ANTLR escaping if we ever need it
        return "'" + s.replace("'", "\\'") + "'";
    }
}
