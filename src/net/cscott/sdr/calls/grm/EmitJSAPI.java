package net.cscott.sdr.calls.grm;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.cscott.jutil.Factories;
import net.cscott.jutil.GenericMultiMap;
import net.cscott.jutil.MultiMap;
import net.cscott.sdr.calls.grm.Grm.Mult;
import net.cscott.sdr.calls.grm.Grm.Nonterminal;

public class EmitJSAPI extends ToStringVisitor {
    private EmitJSAPI() { }
    public static void emit(PrintWriter pw, List<RuleAndAction> l) {
        EmitJSAPI ea = new EmitJSAPI();
        // collect all the rules with the same LHS
        MultiMap<String,RuleAndAction> mm = 
            new GenericMultiMap<String,RuleAndAction>
            (Factories.<RuleAndAction>arrayListFactory());
        for (RuleAndAction ra : l)
            mm.add(ra.rule.lhs, ra);
        // now print them out.
        for (String lhs : sorted(mm.keySet())) {
            if (lhs.equals("anything"))
                pw.print("public ");
            pw.println("<"+lhs+"> =");
            boolean first = true;
            for (RuleAndAction ra : mm.getValues(lhs)) {
                if (first) { pw.print("\t  "); first = false; }
                else { pw.print("\t| "); }
                pw.println(ra.rule.rhs.accept(ea));
            }
            pw.println("\t;");
        }
        // done.
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

    private static List<String> sorted(Collection<String> s) {
        List<String> l = new ArrayList<String>(s);
        Collections.sort(l);
        return l;
    }
}
