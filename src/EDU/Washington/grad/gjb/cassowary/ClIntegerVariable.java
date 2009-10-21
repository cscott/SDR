package EDU.Washington.grad.gjb.cassowary;

import net.cscott.sdr.util.Fraction;

/** A variable which may only take on integral values. */
public class ClIntegerVariable extends ClVariable {
    public ClIntegerVariable(ClBranchAndBound bb) {
        super();
        bb.addIntegerVariable(this);
    }
    public ClIntegerVariable(ClBranchAndBound bb, String name) {
        super(name);
        bb.addIntegerVariable(this);
    }
    public ClIntegerVariable(ClBranchAndBound bb, String name, Fraction value) {
        super(name, value);
        bb.addIntegerVariable(this);
    }
}
