package EDU.Washington.grad.gjb.cassowary;

import net.cscott.sdr.util.Fraction;
import EDU.Washington.grad.gjb.cassowary.CL.Op;

/** A {@link ClBooleanVariable} is an {@link ClIntegerVariable} restricted
 *  to [0,1].
 * @author C. Scott Ananian
 */
public class ClBooleanVariable extends ClIntegerVariable {
    public ClBooleanVariable(ClBranchAndBound bb)
        throws ExCLRequiredFailure, ExCLInternalError {
        super(bb);
        bb.addConstraint(new ClLinearInequality(this, Op.GEQ, Fraction.ZERO));
        bb.addConstraint(new ClLinearInequality(this, Op.LEQ, Fraction.ONE));
    }
    public ClBooleanVariable(ClBranchAndBound bb, String name)
        throws ExCLRequiredFailure, ExCLInternalError {
        super(bb, name);
        bb.addConstraint(new ClLinearInequality(this, Op.GEQ, Fraction.ZERO));
        bb.addConstraint(new ClLinearInequality(this, Op.LEQ, Fraction.ONE));
    }
}
