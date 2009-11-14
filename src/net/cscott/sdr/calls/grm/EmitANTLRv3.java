package net.cscott.sdr.calls.grm;

import java.util.List;

import net.cscott.jutil.MultiMap;
import net.cscott.sdr.calls.grm.Grm.Mult;
import net.cscott.sdr.calls.grm.Grm.Nonterminal;
import net.cscott.sdr.calls.grm.Grm.Terminal;

/** Emit an ANTLRv3 "natural language" grammar for calls. */
public class EmitANTLRv3 extends AbstractEmit {
    private NumberParams np=null;
    private EmitANTLRv3() { }
    public static String emit(String parserName,
                              List<RuleAndAction> l) {
        EmitANTLRv3 ea = new EmitANTLRv3();
        // collect all the rules with the same LHS
        MultiMap<String,RuleAndAction> mm = collectLHS(l);
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
        return "'"+t.literal+"'";
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
}
