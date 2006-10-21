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
import net.cscott.sdr.calls.grm.Grm.Terminal;

public class EmitANTLR extends ToStringVisitor {
    private NumberParams np=null;
    private EmitANTLR() { }
    public static void emit(PrintWriter pw, List<RuleAndAction> l) {
        EmitANTLR ea = new EmitANTLR();
        // collect all the rules with the same LHS
        MultiMap<String,RuleAndAction> mm = 
            new GenericMultiMap<String,RuleAndAction>
            (Factories.<RuleAndAction>arrayListFactory());
        for (RuleAndAction ra : l)
            mm.add(ra.rule.lhs, ra);
        // now print them out.
        for (String lhs : sorted(mm.keySet())) {
            pw.println(lhs+" returns [Apply r=null]");
            boolean first = true;
            for (RuleAndAction ra : mm.getValues(lhs)) {
                if (first) { pw.print("\t: "); first = false; }
                else { pw.print("\t| "); }
                ea.np = new NumberParams(ra.rule.rhs);
                pw.print(ra.rule.rhs.accept(ea));
                pw.println(" { "+ra.action+" }");
            }
            pw.println("\t;");
        }
        // done.
    }
    @Override
    public String visit(Terminal t) {
        // quote literals.
        return "\""+t.literal+"\"";
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

    private static List<String> sorted(Collection<String> s) {
        List<String> l = new ArrayList<String>(s);
        Collections.sort(l);
        return l;
    }
}
