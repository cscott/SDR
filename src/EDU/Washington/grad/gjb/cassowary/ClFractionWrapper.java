// $Id: ClDouble.java,v 1.1 2008/08/12 22:32:43 larrymelia Exp $
//
// Cassowary Incremental Constraint Solver
// Original Smalltalk Implementation by Alan Borning
// This Java Implementation by Greg J. Badros, <gjb@cs.washington.edu>
// http://www.cs.washington.edu/homes/gjb
// (C) 1998, 1999 Greg J. Badros and Alan Borning
// See ../LICENSE for legal details regarding this software
//
// ClDouble
//

package EDU.Washington.grad.gjb.cassowary;

import net.cscott.sdr.util.Fraction;

public class ClFractionWrapper {
    public ClFractionWrapper(Fraction val) {
        value = val;
    }

    public ClFractionWrapper() {
        this(Fraction.ZERO);
    }

    public final Object clone() {
        return new ClFractionWrapper(value);
    }

    public final Fraction getValue() {
        return value;
    }

    public final void setValue(Fraction val) {
        value = val;
    }

    public final String toString() {
        return value.toProperString();
    }

    public final boolean equals(Object o) {
        try {
            return value.equals(((ClFractionWrapper) o).value);
        } catch (Exception err) {
            return false;
        }
    }

    public final int hashCode() {
        System.err.println("ClDouble.hashCode() called!");
        assert false;
        return value.hashCode();
    }

    private Fraction value;
}
