// $Id: ClLinearExpression.java,v 1.1 2008/08/12 22:32:42 larrymelia Exp $
//
// Cassowary Incremental Constraint Solver
// Original Smalltalk Implementation by Alan Borning
// This Java Implementation by Greg J. Badros, <gjb@cs.washington.edu>
// http://www.cs.washington.edu/homes/gjb
// (C) 1998, 1999 Greg J. Badros and Alan Borning
// See ../LICENSE for legal details regarding this software
//
// ClLinearExpression
//

package EDU.Washington.grad.gjb.cassowary;

import java.util.*;

import net.cscott.sdr.util.Fraction;

public class ClLinearExpression extends CL {

    public ClLinearExpression(ClAbstractVariable clv, Fraction value,
            Fraction constant) {
        if (CL.fGC)
            System.err.println("new ClLinearExpression");

        _constant = new ClFractionWrapper(constant);
        _terms = new Hashtable<ClAbstractVariable, ClFractionWrapper>(1);
        if (clv != null)
            _terms.put(clv, new ClFractionWrapper(value));
    }

    public ClLinearExpression(Fraction num) {
        this(null, Fraction.ZERO, num);
    }

    public ClLinearExpression() {
        this(Fraction.ZERO);
    }

    public ClLinearExpression(ClAbstractVariable clv, Fraction value) {
        this(clv, value, Fraction.ZERO);
    }

    public ClLinearExpression(ClAbstractVariable clv) {
        this(clv, Fraction.ONE, Fraction.ZERO);
    }

    // for use by the clone method
    protected ClLinearExpression(ClFractionWrapper constant,
            Hashtable<ClAbstractVariable, ClFractionWrapper> terms) {
        if (CL.fGC)
            System.err.println("clone ClLinearExpression");
        _constant = (ClFractionWrapper) constant.clone();
        _terms = new Hashtable<ClAbstractVariable, ClFractionWrapper>();
        // need to unalias the ClDouble-s that we clone (do a deep clone)
        for (Enumeration<ClAbstractVariable> e = terms.keys(); e
                .hasMoreElements();) {
            ClAbstractVariable clv = e.nextElement();
            _terms.put(clv, (ClFractionWrapper) terms.get(clv).clone());
        }
    }

    public ClLinearExpression multiplyMe(Fraction x) {
        _constant.setValue(_constant.getValue().multiply(x));

        for (Enumeration<ClAbstractVariable> e = _terms.keys(); e
                .hasMoreElements();) {
            ClAbstractVariable clv = e.nextElement();
            ClFractionWrapper cld = _terms.get(clv);
            cld.setValue(cld.getValue().multiply(x));
        }
        return this;
    }

    public final Object clone() {
        return new ClLinearExpression(_constant, _terms);
    }

    public final ClLinearExpression times(Fraction x) {
        return ((ClLinearExpression) clone()).multiplyMe(x);
    }

    public final ClLinearExpression times(ClLinearExpression expr)
            throws ExCLNonlinearExpression {
        if (isConstant()) {
            return expr.times(_constant.getValue());
        } else if (!expr.isConstant()) {
            throw new ExCLNonlinearExpression();
        }
        return times(expr._constant.getValue());
    }

    public final ClLinearExpression plus(ClLinearExpression expr) {
        return ((ClLinearExpression) clone()).addExpression(expr, Fraction.ONE);
    }

    public final ClLinearExpression plus(ClVariable var)
            throws ExCLNonlinearExpression {
        return ((ClLinearExpression) clone()).addVariable(var, Fraction.ONE);
    }

    public final ClLinearExpression minus(ClLinearExpression expr) {
        return ((ClLinearExpression) clone()).addExpression(expr, Fraction.mONE);
    }

    public final ClLinearExpression minus(ClVariable var)
            throws ExCLNonlinearExpression {
        return ((ClLinearExpression) clone()).addVariable(var, Fraction.mONE);
    }

    public final ClLinearExpression divide(Fraction x)
            throws ExCLNonlinearExpression {
        if (x.equals(Fraction.ZERO)) {
            throw new ExCLNonlinearExpression();
        }
        return times(x.invert());
    }

    public final ClLinearExpression divide(ClLinearExpression expr)
            throws ExCLNonlinearExpression {
        if (!expr.isConstant()) {
            throw new ExCLNonlinearExpression();
        }
        return divide(expr._constant.getValue());
    }

    public final ClLinearExpression divFrom(ClLinearExpression expr)
            throws ExCLNonlinearExpression {
        if (!isConstant() || _constant.getValue().equals(Fraction.ZERO)) {
            throw new ExCLNonlinearExpression();
        }
        return expr.divide(_constant.getValue());
    }

    public final ClLinearExpression subtractFrom(ClLinearExpression expr) {
        return expr.minus(this);
    }

    // Add n*expr to this expression from another expression expr.
    // Notify the solver if a variable is added or deleted from this
    // expression.
    public final ClLinearExpression addExpression(ClLinearExpression expr,
            Fraction n, ClAbstractVariable subject, ClTableau solver) {
        incrementConstant(n.multiply(expr.constant()));

        for (Enumeration<ClAbstractVariable> e = expr.terms().keys(); e
                .hasMoreElements();) {
            ClAbstractVariable clv = e.nextElement();
            Fraction coeff = expr.terms().get(clv).getValue();
            addVariable(clv, coeff.multiply(n), subject, solver);
        }
        return this;
    }

    // Add n*expr to this expression from another expression expr.
    public final ClLinearExpression addExpression(ClLinearExpression expr,
            Fraction n) {
        incrementConstant(n.multiply(expr.constant()));

        for (Enumeration<ClAbstractVariable> e = expr.terms().keys(); e
                .hasMoreElements();) {
            ClAbstractVariable clv = e.nextElement();
            Fraction coeff = expr.terms().get(clv).getValue();
            addVariable(clv, coeff.multiply(n));
        }
        return this;
    }

    public final ClLinearExpression addExpression(ClLinearExpression expr) {
        return addExpression(expr, Fraction.ONE);
    }

    // Add a term c*v to this expression. If the expression already
    // contains a term involving v, add c to the existing coefficient.
    // If the new coefficient is approximately 0, delete v.
    public final ClLinearExpression addVariable(ClAbstractVariable v, Fraction c) { // body
        // largely
        // duplicated
        // below
        if (fTraceOn)
            fnenterprint("addVariable:" + v + ", " + c);

        ClFractionWrapper coeff = _terms.get(v);
        if (coeff != null) {
            Fraction new_coefficient = coeff.getValue().add(c);
            if (new_coefficient.equals(Fraction.ZERO)) {
                _terms.remove(v);
            } else {
                coeff.setValue(new_coefficient);
            }
        } else {
            if (!c.equals(Fraction.ZERO)) {
                _terms.put(v, new ClFractionWrapper(c));
            }
        }
        return this;
    }

    public final ClLinearExpression addVariable(ClAbstractVariable v) {
        return addVariable(v, Fraction.ONE);
    }

    public final ClLinearExpression setVariable(ClAbstractVariable v, Fraction c) {
        // assert(c != Fraction.ZERO);
        ClFractionWrapper coeff = _terms.get(v);
        if (coeff != null)
            coeff.setValue(c);
        else
            _terms.put(v, new ClFractionWrapper(c));
        return this;
    }

    // Add a term c*v to this expression. If the expression already
    // contains a term involving v, add c to the existing coefficient.
    // If the new coefficient is approximately 0, delete v. Notify the
    // solver if v appears or disappears from this expression.
    public final ClLinearExpression addVariable(ClAbstractVariable v, Fraction c,
            ClAbstractVariable subject, ClTableau solver) { // body largely
                                                            // duplicated
        // above
        if (fTraceOn)
            fnenterprint("addVariable:" + v + ", " + c + ", " + subject
                    + ", ...");

        ClFractionWrapper coeff = _terms.get(v);
        if (coeff != null) {
            Fraction new_coefficient = coeff.getValue().add(c);
            if (new_coefficient.equals(Fraction.ZERO)) {
                solver.noteRemovedVariable(v, subject);
                _terms.remove(v);
            } else {
                coeff.setValue(new_coefficient);
            }
        } else {
            if (!c.equals(Fraction.ZERO)) {
                _terms.put(v, new ClFractionWrapper(c));
                solver.noteAddedVariable(v, subject);
            }
        }
        return this;
    }

    // Return a pivotable variable in this expression. (It is an error
    // if this expression is constant -- signal ExCLInternalError in
    // that case). Return null if no pivotable variables
    public final ClAbstractVariable anyPivotableVariable()
            throws ExCLInternalError {
        if (isConstant()) {
            throw new ExCLInternalError(
                    "anyPivotableVariable called on a constant");
        }

        for (Enumeration<ClAbstractVariable> e = _terms.keys(); e
                .hasMoreElements();) {
            ClAbstractVariable clv = e.nextElement();
            if (clv.isPivotable())
                return clv;
        }

        // No pivotable variables, so just return null, and let the caller
        // error if needed
        return null;
    }

    // Replace var with a symbolic expression expr that is equal to it.
    // If a variable has been added to this expression that wasn't there
    // before, or if a variable has been dropped from this expression
    // because it now has a coefficient of 0, inform the solver.
    // PRECONDITIONS:
    // var occurs with a non-zero coefficient in this expression.
    public final void substituteOut(ClAbstractVariable var,
            ClLinearExpression expr, ClAbstractVariable subject,
            ClTableau solver) {
        if (fTraceOn)
            fnenterprint("CLE:substituteOut: " + var + ", " + expr + ", "
                    + subject + ", ...");
        if (fTraceOn)
            traceprint("this = " + this);

        Fraction multiplier = _terms.remove(var).getValue();
        incrementConstant(multiplier.multiply(expr.constant()));

        for (Enumeration<ClAbstractVariable> e = expr.terms().keys(); e
                .hasMoreElements();) {
            ClAbstractVariable clv = e.nextElement();
            Fraction coeff = expr.terms().get(clv).getValue();
            ClFractionWrapper d_old_coeff = _terms.get(clv);
            if (d_old_coeff != null) {
                Fraction old_coeff = d_old_coeff.getValue();
                Fraction newCoeff = old_coeff.add(multiplier.multiply(coeff));
                if (newCoeff.equals(Fraction.ZERO)) {
                    solver.noteRemovedVariable(clv, subject);
                    _terms.remove(clv);
                } else {
                    d_old_coeff.setValue(newCoeff);
                }
            } else {
                // did not have that variable already
                _terms.put(clv, new ClFractionWrapper(multiplier.multiply(coeff)));
                solver.noteAddedVariable(clv, subject);
            }
        }
        if (fTraceOn)
            traceprint("Now this is " + this);
    }

    // This linear expression currently represents the equation
    // oldSubject=self. Destructively modify it so that it represents
    // the equation newSubject=self.
    //
    // Precondition: newSubject currently has a nonzero coefficient in
    // this expression.
    //
    // NOTES
    // Suppose this expression is c + a*newSubject + a1*v1 + ... + an*vn.
    //
    // Then the current equation is
    // oldSubject = c + a*newSubject + a1*v1 + ... + an*vn.
    // The new equation will be
    // newSubject = -c/a + oldSubject/a - (a1/a)*v1 - ... - (an/a)*vn.
    // Note that the term involving newSubject has been dropped.
    public final void changeSubject(ClAbstractVariable old_subject,
            ClAbstractVariable new_subject) {
        ClFractionWrapper cld = _terms.get(old_subject);
        if (cld != null)
            cld.setValue(newSubject(new_subject));
        else
            _terms.put(old_subject, new ClFractionWrapper(newSubject(new_subject)));
    }

    // This linear expression currently represents the equation self=0.
    // Destructively modify it so
    // that subject=self represents an equivalent equation.
    //
    // Precondition: subject must be one of the variables in this expression.
    // NOTES
    // Suppose this expression is
    // c + a*subject + a1*v1 + ... + an*vn
    // representing
    // c + a*subject + a1*v1 + ... + an*vn = 0
    // The modified expression will be
    // subject = -c/a - (a1/a)*v1 - ... - (an/a)*vn
    // representing
    // subject = -c/a - (a1/a)*v1 - ... - (an/a)*vn
    //
    // Note that the term involving subject has been dropped.
    // Returns the reciprocal, so changeSubject can use it, too
    public final Fraction newSubject(ClAbstractVariable subject) {
        if (fTraceOn)
            fnenterprint("newSubject:" + subject);
        ClFractionWrapper coeff = _terms.remove(subject);
        Fraction reciprocal = coeff.getValue().invert();
        multiplyMe(reciprocal.negate());
        return reciprocal;
    }

    // Return the coefficient corresponding to variable var, i.e.,
    // the 'ci' corresponding to the 'vi' that var is:
    // v1*c1 + v2*c2 + .. + vn*cn + c
    public final Fraction coefficientFor(ClAbstractVariable var) {
        ClFractionWrapper coeff = _terms.get(var);
        if (coeff != null)
            return coeff.getValue();
        else
            return Fraction.ZERO;
    }

    public final Fraction constant() {
        return _constant.getValue();
    }

    public final void set_constant(Fraction c) {
        _constant.setValue(c);
    }

    public final Hashtable<ClAbstractVariable, ClFractionWrapper> terms() {
        return _terms;
    }

    public final void incrementConstant(Fraction c) {
        _constant.setValue(_constant.getValue().add(c));
    }

    public final boolean isConstant() {
        return _terms.size() == 0;
    }

    public final String toString() {
        StringBuffer bstr = new StringBuffer();
        Enumeration<ClAbstractVariable> e = _terms.keys();

        if (!_constant.getValue().equals(Fraction.ZERO) || _terms.size() == 0) {
            bstr.append(_constant.toString());
        } else {
            if (_terms.size() == 0) {
                return bstr.toString();
            }
            ClAbstractVariable clv = e.nextElement();
            ClFractionWrapper coeff = _terms.get(clv);
            bstr.append(coeff.toString() + "*" + clv.toString());
        }
        for (; e.hasMoreElements();) {
            ClAbstractVariable clv = e.nextElement();
            ClFractionWrapper coeff = _terms.get(clv);
            bstr.append(" + " + coeff.toString() + "*" + clv.toString());
        }
        return bstr.toString();
    }

    public final static ClLinearExpression Plus(ClLinearExpression e1,
            ClLinearExpression e2) {
        return e1.plus(e2);
    }

    public final static ClLinearExpression Minus(ClLinearExpression e1,
            ClLinearExpression e2) {
        return e1.minus(e2);
    }

    public final static ClLinearExpression Times(ClLinearExpression e1,
            ClLinearExpression e2) throws ExCLNonlinearExpression {
        return e1.times(e2);
    }

    public final static ClLinearExpression Divide(ClLinearExpression e1,
            ClLinearExpression e2) throws ExCLNonlinearExpression {
        return e1.divide(e2);
    }

    public final static boolean FEquals(ClLinearExpression e1,
            ClLinearExpression e2) {
        return e1 == e2;
    }

    private ClFractionWrapper _constant;
    private Hashtable<ClAbstractVariable, ClFractionWrapper> _terms; // from ClVariable
    // to
    // ClDouble

}
