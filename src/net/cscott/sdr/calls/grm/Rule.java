package net.cscott.sdr.calls.grm;

import net.cscott.sdr.calls.Call;
import net.cscott.sdr.util.Fraction;

/** Grammar rule: a right-hand side, left-hand side,
 *  and a precedence level. */
public class Rule {
    public final String lhs;
    public final Grm rhs;
    public final Fraction prec; // precedence level
    public final Call call; // action for this rule.
    
    public Rule(String lhs, Grm rhs, Fraction prec, Call call) {
        this.lhs = lhs;
        this.rhs = rhs;
        this.prec = prec;
        this.call = call;
    }
    
    public String toString() {
        return lhs+" -> "+rhs+" // prec "+prec.toProperString()+", yields '"+call.getName()+"'";
    }
}
