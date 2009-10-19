// $Id: ClStayConstraint.java,v 1.1 2008/08/12 22:32:43 larrymelia Exp $
//
// Cassowary Incremental Constraint Solver
// Original Smalltalk Implementation by Alan Borning
// This Java Implementation by Greg J. Badros, <gjb@cs.washington.edu>
// http://www.cs.washington.edu/homes/gjb
// (C) 1998, 1999 Greg J. Badros and Alan Borning
// See ../LICENSE for legal details regarding this software
//
// ClStayConstraint
// 

package EDU.Washington.grad.gjb.cassowary;

import net.cscott.sdr.util.Fraction;

public class ClStayConstraint extends ClEditOrStayConstraint {

    public ClStayConstraint(ClVariable var, ClStrength strength, Fraction weight) {
        super(var, strength, weight);
    }

    public ClStayConstraint(ClVariable var, ClStrength strength) {
        super(var, strength, Fraction.ONE);
    }

    public ClStayConstraint(ClVariable var) {
        super(var, ClStrength.weak, Fraction.ONE);
    }

    public boolean isStayConstraint() {
        return true;
    }

    public String toString() {
        return "stay " + super.toString();
    }

}
