// $Id: ClLinearInequality.java,v 1.1 2008/08/12 22:32:43 larrymelia Exp $
//
// Cassowary Incremental Constraint Solver
// Original Smalltalk Implementation by Alan Borning
// This Java Implementation by Greg J. Badros, <gjb@cs.washington.edu>
// http://www.cs.washington.edu/homes/gjb
// (C) 1998, 1999 Greg J. Badros and Alan Borning
// See ../LICENSE for legal details regarding this software
//
// ClLinearInequality
//

package EDU.Washington.grad.gjb.cassowary;

import net.cscott.sdr.util.Fraction;

public class ClLinearInequality extends ClLinearConstraint {

    public ClLinearInequality(ClLinearExpression cle, ClStrength strength,
            Fraction weight) {
        super(cle, strength, weight);
    }

    public ClLinearInequality(ClLinearExpression cle, ClStrength strength) {
        super(cle, strength);
    }

    public ClLinearInequality(ClLinearExpression cle) {
        super(cle);
    }

    public ClLinearInequality(ClVariable clv1, CL.Op op_enum, ClVariable clv2,
            ClStrength strength, Fraction weight) throws ExCLInternalError {
        super(new ClLinearExpression(clv2), strength, weight);
        if (op_enum == CL.Op.GEQ) {
            _expression.multiplyMe(Fraction.mONE);
            _expression.addVariable(clv1);
        } else if (op_enum == CL.Op.LEQ) {
            _expression.addVariable(clv1, Fraction.mONE);
        } else
            // the operator was invalid
            throw new ExCLInternalError(
                    "Invalid operator in ClLinearInequality constructor");
    }

    public ClLinearInequality(ClVariable clv1, CL.Op op_enum, ClVariable clv2,
            ClStrength strength) throws ExCLInternalError {
        this(clv1, op_enum, clv2, strength, Fraction.ONE);
    }

    public ClLinearInequality(ClVariable clv1, CL.Op op_enum, ClVariable clv2)
            throws ExCLInternalError {
        this(clv1, op_enum, clv2, ClStrength.required, Fraction.ONE);
    }

    public ClLinearInequality(ClVariable clv, CL.Op op_enum, Fraction val,
            ClStrength strength, Fraction weight) throws ExCLInternalError {
        super(new ClLinearExpression(val), strength, weight);
        if (op_enum == CL.Op.GEQ) {
            _expression.multiplyMe(Fraction.mONE);
            _expression.addVariable(clv);
        } else if (op_enum == CL.Op.LEQ) {
            _expression.addVariable(clv, Fraction.mONE);
        } else
            // the operator was invalid
            throw new ExCLInternalError(
                    "Invalid operator in ClLinearInequality constructor");
    }

    public ClLinearInequality(ClVariable clv, CL.Op op_enum, Fraction val,
            ClStrength strength) throws ExCLInternalError {
        this(clv, op_enum, val, strength, Fraction.ONE);
    }

    public ClLinearInequality(ClVariable clv, CL.Op op_enum, Fraction val)
            throws ExCLInternalError {
        this(clv, op_enum, val, ClStrength.required, Fraction.ONE);
    }

    public ClLinearInequality(ClLinearExpression cle1, CL.Op op_enum,
            ClLinearExpression cle2, ClStrength strength, Fraction weight)
            throws ExCLInternalError {
        super(((ClLinearExpression) cle2.clone()), strength, weight);
        if (op_enum == CL.Op.GEQ) {
            _expression.multiplyMe(Fraction.mONE);
            _expression.addExpression(cle1);
        } else if (op_enum == CL.Op.LEQ) {
            _expression.addExpression(cle1, Fraction.mONE);
        } else
            // the operator was invalid
            throw new ExCLInternalError(
                    "Invalid operator in ClLinearInequality constructor");
    }

    public ClLinearInequality(ClLinearExpression cle1, CL.Op op_enum,
            ClLinearExpression cle2, ClStrength strength)
            throws ExCLInternalError {
        this(cle1, op_enum, cle2, strength, Fraction.ONE);
    }

    public ClLinearInequality(ClLinearExpression cle1, CL.Op op_enum,
            ClLinearExpression cle2) throws ExCLInternalError {
        this(cle1, op_enum, cle2, ClStrength.required, Fraction.ONE);
    }

    public ClLinearInequality(ClAbstractVariable clv, CL.Op op_enum,
            ClLinearExpression cle, ClStrength strength, Fraction weight)
            throws ExCLInternalError {
        super(((ClLinearExpression) cle.clone()), strength, weight);
        if (op_enum == CL.Op.GEQ) {
            _expression.multiplyMe(Fraction.mONE);
            _expression.addVariable(clv);
        } else if (op_enum == CL.Op.LEQ) {
            _expression.addVariable(clv, Fraction.mONE);
        } else
            // the operator was invalid
            throw new ExCLInternalError(
                    "Invalid operator in ClLinearInequality constructor");
    }

    public ClLinearInequality(ClAbstractVariable clv, CL.Op op_enum,
            ClLinearExpression cle, ClStrength strength)
            throws ExCLInternalError {
        this(clv, op_enum, cle, strength, Fraction.ONE);
    }

    public ClLinearInequality(ClAbstractVariable clv, CL.Op op_enum,
            ClLinearExpression cle) throws ExCLInternalError {
        this(clv, op_enum, cle, ClStrength.required, Fraction.ONE);
    }

    public ClLinearInequality(ClLinearExpression cle, CL.Op op_enum,
            ClAbstractVariable clv, ClStrength strength, Fraction weight)
            throws ExCLInternalError {
        super(((ClLinearExpression) cle.clone()), strength, weight);
        if (op_enum == CL.Op.LEQ) {
            _expression.multiplyMe(Fraction.mONE);
            _expression.addVariable(clv);
        } else if (op_enum == CL.Op.GEQ) {
            _expression.addVariable(clv, Fraction.mONE);
        } else
            // the operator was invalid
            throw new ExCLInternalError(
                    "Invalid operator in ClLinearInequality constructor");
    }

    public ClLinearInequality(ClLinearExpression cle, CL.Op op_enum,
            ClAbstractVariable clv, ClStrength strength)
            throws ExCLInternalError {
        this(cle, op_enum, clv, strength, Fraction.ONE);
    }

    public ClLinearInequality(ClLinearExpression cle, CL.Op op_enum,
            ClAbstractVariable clv) throws ExCLInternalError {
        this(cle, op_enum, clv, ClStrength.required, Fraction.ONE);
    }

    public final boolean isInequality() {
        return true;
    }

    public final String toString() {
        return super.toString() + " >= 0 )";
    }
}
