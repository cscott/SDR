// $Id: ClPoint.java,v 1.1 2008/08/12 22:32:43 larrymelia Exp $
//
// Cassowary Incremental Constraint Solver
// Original Smalltalk Implementation by Alan Borning
// This Java Implementation by Greg J. Badros, <gjb@cs.washington.edu>
// http://www.cs.washington.edu/homes/gjb
// (C) 1998, 1999 Greg J. Badros and Alan Borning
// See ../LICENSE for legal details regarding this software
//
// ClPoint
//

package EDU.Washington.grad.gjb.cassowary;

import net.cscott.sdr.util.Fraction;

public class ClPoint {
    public ClPoint(Fraction x, Fraction y) {
        _clv_x = new ClVariable(x);
        _clv_y = new ClVariable(y);
    }

    public ClPoint(Fraction x, Fraction y, int a) {
        _clv_x = new ClVariable("x" + a, x);
        _clv_y = new ClVariable("y" + a, y);
    }

    public ClPoint(ClVariable clv_x, ClVariable clv_y) {
        _clv_x = clv_x;
        _clv_y = clv_y;
    }

    public ClVariable X() {
        return _clv_x;
    }

    public ClVariable Y() {
        return _clv_y;
    }

    // use only before adding into the solver
    public void SetXY(Fraction x, Fraction y) {
        _clv_x.set_value(x);
        _clv_y.set_value(y);
    }

    public void SetXY(ClVariable clv_x, ClVariable clv_y) {
        _clv_x = clv_x;
        _clv_y = clv_y;
    }

    public Fraction Xvalue() {
        return X().value();
    }

    public Fraction Yvalue() {
        return Y().value();
    }

    public String toString() {
        return "(" + _clv_x.toString() + ", " + _clv_y.toString() + ")";
    }

    private ClVariable _clv_x;

    private ClVariable _clv_y;

}
