package net.cscott.sdr.calls.grm;

import java.util.List;

import net.cscott.jutil.MultiMap;
import net.cscott.sdr.calls.grm.Grm.Mult;
import net.cscott.sdr.calls.grm.Grm.Nonterminal;

public class EmitJSAPI extends AbstractEmit {
    private EmitJSAPI() { }
    public static String emit(String parserName, List<RuleAndAction> l) {
        EmitJSAPI ej = new EmitJSAPI();
        // collect all the rules with the same LHS
        MultiMap<String,RuleAndAction> mm = collectLHS(l);

        // now print them out.
        // now emit the rules.
        String NL = System.getProperty("line.separator");
        StringBuilder sb = new StringBuilder();
        for (String lhs : sorted(mm.keySet())) {
            if (lhs.equals("anything"))
                sb.append("public ");
            sb.append("<"+lhs+"> ="+NL);
            boolean first = true;
            for (RuleAndAction ra : mm.getValues(lhs)) {
                if (first) { sb.append("\t  "); first = false; }
                else { sb.append("\t| "); }
                sb.append(ra.rule.rhs.accept(ej));
                sb.append(NL);
            }
            sb.append("\t;"+NL);
        }
        // substitute the rules & the classname into the skeleton
        String result = ej.subst("jsapi.skel", sb.toString(), parserName);
        // done.
        return result;
    }
    @Override
    public String visit(Mult mult) {
        switch(mult.type) {
        case QUESTION:
            return "["+mult.operand.accept(this)+"]";
        default:
            assert false : "don't know how JSAPI expresses this";
            return null;
        }
    }
    @Override
    public String visit(Nonterminal nt) {
        return "<"+nt.ruleName+">";
    }
}
