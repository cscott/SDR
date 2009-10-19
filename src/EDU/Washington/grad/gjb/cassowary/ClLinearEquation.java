// $Id: ClLinearEquation.java,v 1.1 2008/08/12 22:32:43 larrymelia Exp $
//
// Cassowary Incremental Constraint Solver
// Original Smalltalk Implementation by Alan Borning
// This Java Implementation by Greg J. Badros, <gjb@cs.washington.edu>
// http://www.cs.washington.edu/homes/gjb
// (C) 1998, 1999 Greg J. Badros and Alan Borning
// See ../LICENSE for legal details regarding this software
//
// ClLinearEquation
//

package EDU.Washington.grad.gjb.cassowary;

import net.cscott.sdr.util.Fraction;

public class ClLinearEquation extends ClLinearConstraint {
    public ClLinearEquation(ClLinearExpression cle, ClStrength strength,
            Fraction weight) {
        super(cle, strength, weight);
    }

    public ClLinearEquation(ClLinearExpression cle, ClStrength strength) {
        super(cle, strength);
    }

    public ClLinearEquation(ClLinearExpression cle) {
        super(cle);
    }

    public ClLinearEquation(ClAbstractVariable clv, ClLinearExpression cle,
            ClStrength strength, Fraction weight) {
        super(cle, strength, weight);
        _expression.addVariable(clv, Fraction.mONE);
    }

    public ClLinearEquation(ClAbstractVariable clv, ClLinearExpression cle,
            ClStrength strength) {
        this(clv, cle, strength, Fraction.ONE);
    }

    public ClLinearEquation(ClAbstractVariable clv, ClLinearExpression cle) {
        this(clv, cle, ClStrength.required, Fraction.ONE);
    }

    public ClLinearEquation(ClAbstractVariable clv, Fraction val,
            ClStrength strength, Fraction weight) {
        super(new ClLinearExpression(val), strength, weight);
        _expression.addVariable(clv, Fraction.mONE);
    }

    public ClLinearEquation(ClAbstractVariable clv, Fraction val,
            ClStrength strength) {
        this(clv, val, strength, Fraction.ONE);
    }

    public ClLinearEquation(ClAbstractVariable clv, Fraction val) {
        this(clv, val, ClStrength.required, Fraction.ONE);
    }

    public ClLinearEquation(ClLinearExpression cle, ClAbstractVariable clv,
            ClStrength strength, Fraction weight) {
        super(((ClLinearExpression) cle.clone()), strength, weight);
        _expression.addVariable(clv, Fraction.mONE);
    }

    public ClLinearEquation(ClLinearExpression cle, ClAbstractVariable clv,
            ClStrength strength) {
        this(cle, clv, strength, Fraction.ONE);
    }

    public ClLinearEquation(ClLinearExpression cle, ClAbstractVariable clv) {
        this(cle, clv, ClStrength.required, Fraction.ONE);
    }

    public ClLinearEquation(ClLinearExpression cle1, ClLinearExpression cle2,
            ClStrength strength, Fraction weight) {
        super(((ClLinearExpression) cle1.clone()), strength, weight);
        _expression.addExpression(cle2, Fraction.mONE);
    }

    public ClLinearEquation(ClLinearExpression cle1, ClLinearExpression cle2,
            ClStrength strength) {
        this(cle1, cle2, strength, Fraction.ONE);
    }

    public ClLinearEquation(ClLinearExpression cle1, ClLinearExpression cle2) {
        this(cle1, cle2, ClStrength.required, Fraction.ONE);
    }

    public String toString() {
        return super.toString() + " = 0 )";
    }
}
