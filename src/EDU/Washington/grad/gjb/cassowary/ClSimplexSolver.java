// $Id: ClSimplexSolver.java,v 1.1 2008/08/12 22:32:43 larrymelia Exp $
//
// Cassowary Incremental Constraint Solver
// Original Smalltalk Implementation by Alan Borning
// This Java Implementation by Greg J. Badros, <gjb@cs.washington.edu>
// http://www.cs.washington.edu/homes/gjb
// (C) 1998, 1999 Greg J. Badros and Alan Borning
// See ../LICENSE for legal details regarding this software
//
// ClSimplexSolver
// 

package EDU.Washington.grad.gjb.cassowary;

import java.util.*;

import net.cscott.sdr.util.Fraction;

public class ClSimplexSolver extends ClTableau {
    // Ctr initializes the fields, and creates the objective row
    public ClSimplexSolver() {
        _stayMinusErrorVars = new ArrayList<ClSlackVariable>();
        _stayPlusErrorVars = new ArrayList<ClSlackVariable>();
        _errorVars = new LinkedHashMap<ClConstraint, Set<ClAbstractVariable>>();
        _markerVars = new LinkedHashMap<ClConstraint, ClAbstractVariable>();

        _resolve_pair = new ArrayList<ClFractionWrapper>(2);
        _resolve_pair.add(new ClFractionWrapper(Fraction.ZERO));
        _resolve_pair.add(new ClFractionWrapper(Fraction.ZERO));

        _objective = new ClObjectiveVariable("Z");

        _editVarMap = new LinkedHashMap<ClVariable, ClEditInfo>();

        _slackCounter = 0;
        _artificialCounter = 0;
        _dummyCounter = 0;
        _epsilon = Fraction.valueOf(1e-8);

        _fOptimizeAutomatically = true;
        _fNeedsSolving = false;

        ClLinearExpression e = new ClLinearExpression();
        _rows.put(_objective, e);
        _stkCedcns = new Stack<Integer>();
        _stkCedcns.push(new Integer(0));

        if (fTraceOn)
            traceprint("objective expr == " + rowExpression(_objective));
    }

    // Convenience function for creating a linear inequality constraint
    public final ClSimplexSolver addLowerBound(ClAbstractVariable v,
            Fraction lower) throws ExCLRequiredFailure, ExCLInternalError {
        ClLinearInequality cn = new ClLinearInequality(v, CL.Op.GEQ,
                new ClLinearExpression(lower));
        return addConstraint(cn);
    }

    // Convenience function for creating a linear inequality constraint
    public final ClSimplexSolver addUpperBound(ClAbstractVariable v,
            Fraction upper) throws ExCLRequiredFailure, ExCLInternalError {
        ClLinearInequality cn = new ClLinearInequality(v, CL.Op.LEQ,
                new ClLinearExpression(upper));
        return addConstraint(cn);
    }

    // Convenience function for creating a pair of linear inequality constraint
    public final ClSimplexSolver addBounds(ClAbstractVariable v, Fraction lower,
            Fraction upper) throws ExCLRequiredFailure, ExCLInternalError {
        addLowerBound(v, lower);
        addUpperBound(v, upper);
        return this;
    }

    // Add constraint "cn" to the solver
    public final ClSimplexSolver addConstraint(ClConstraint cn)
            throws ExCLRequiredFailure, ExCLInternalError {
        if (fTraceOn)
            fnenterprint("addConstraint: " + cn);

        List<ClSlackVariable> eplus_eminus = new ArrayList<ClSlackVariable>(2);
        ClFractionWrapper prevEConstant = new ClFractionWrapper();
        ClLinearExpression expr = newExpression(cn, /* output to: */
                eplus_eminus, prevEConstant);
        boolean fAddedOkDirectly = false;

        try {
            fAddedOkDirectly = tryAddingDirectly(expr);
            if (!fAddedOkDirectly) {
                // could not add directly
                addWithArtificialVariable(expr);
            }
        } catch (ExCLRequiredFailure err) {
            // /try {
            // / removeConstraint(cn); // FIXGJB
            // } catch (ExCLConstraintNotFound errNF) {
            // This should not possibly happen
            // / System.err.println("ERROR: could not find a constraint just
            // added\n");
            // /}
            throw err;
        }

        _fNeedsSolving = true;

        if (cn.isEditConstraint()) {
            int i = _editVarMap.size();
            ClEditConstraint cnEdit = (ClEditConstraint) cn;
            ClSlackVariable clvEplus = eplus_eminus.get(0);
            ClSlackVariable clvEminus = eplus_eminus.get(1);
            _editVarMap.put(cnEdit.variable(), new ClEditInfo(cnEdit, clvEplus,
                    clvEminus, prevEConstant.getValue(), i));
        }

        if (_fOptimizeAutomatically) {
            optimize(_objective);
            setExternalVariables();
        }

        return this;
    }

    // Same as addConstraint, except returns false if the constraint
    // resulted in an unsolvable system (instead of throwing an exception)
    public final boolean addConstraintNoException(ClConstraint cn)
            throws ExCLInternalError {
        if (fTraceOn)
            fnenterprint("addConstraintNoException: " + cn);

        try {
            addConstraint(cn);
            return true;
        } catch (ExCLRequiredFailure e) {
            return false;
        }
    }

    // Add an edit constraint for "v" with given strength
    public final ClSimplexSolver addEditVar(ClVariable v, ClStrength strength)
            throws ExCLInternalError {
        try {
            ClEditConstraint cnEdit = new ClEditConstraint(v, strength);
            return addConstraint(cnEdit);
        } catch (ExCLRequiredFailure e) {
            // should not get this
            throw new ExCLInternalError(
                    "Required failure when adding an edit variable");
        }
    }

    // default to strength = strong
    public final ClSimplexSolver addEditVar(ClVariable v)
            throws ExCLInternalError {
        return addEditVar(v, ClStrength.strong);
    }

    // Remove the edit constraint previously added for variable v
    public final ClSimplexSolver removeEditVar(ClVariable v)
            throws ExCLInternalError, ExCLConstraintNotFound {
        ClEditInfo cei = _editVarMap.get(v);
        ClConstraint cn = cei.Constraint();
        removeConstraint(cn);
        return this;
    }

    // beginEdit() should be called before sending
    // resolve() messages, after adding the appropriate edit variables
    public final ClSimplexSolver beginEdit() throws ExCLInternalError {
        assert _editVarMap.size() > 0 : "_editVarMap.size() > 0";
        // may later want to do more in here
        _infeasibleRows.clear();
        resetStayConstants();
        _stkCedcns.addElement(new Integer(_editVarMap.size()));
        return this;
    }

    // endEdit should be called after editing has finished
    // for now, it just removes all edit variables
    public final ClSimplexSolver endEdit() throws ExCLInternalError {
        assert _editVarMap.size() > 0 : "_editVarMap.size() > 0";
        resolve();
        _stkCedcns.pop();
        int n = _stkCedcns.peek().intValue();
        removeEditVarsTo(n);
        // may later want to do more in here
        return this;
    }

    // removeAllEditVars() just eliminates all the edit constraints
    // that were added
    public final ClSimplexSolver removeAllEditVars() throws ExCLInternalError {
        return removeEditVarsTo(0);
    }

    // remove the last added edit vars to leave only n edit vars left
    public final ClSimplexSolver removeEditVarsTo(int n)
            throws ExCLInternalError {
        try {
            for (ClVariable v: new ArrayList<ClVariable>(_editVarMap.keySet())){
                ClEditInfo cei = _editVarMap.get(v);
                if (cei.Index() >= n) {
                    removeEditVar(v);
                }
            }
            assert _editVarMap.size() == n : "_editVarMap.size() == n";
            return this;
        } catch (ExCLConstraintNotFound e) {
            // should not get this
            throw new ExCLInternalError(
                    "Constraint not found in removeEditVarsTo");
        }
    }

    // Add weak stays to the x and y parts of each point. These have
    // increasing weights so that the solver will try to satisfy the x
    // and y stays on the same point, rather than the x stay on one and
    // the y stay on another.
    public final ClSimplexSolver addPointStays(List<ClPoint> listOfPoints)
            throws ExCLRequiredFailure, ExCLInternalError {
        if (fTraceOn)
            fnenterprint("addPointStays" + listOfPoints);
        Fraction weight = Fraction.ONE;
        final Fraction multiplier = Fraction.TWO;
        for (int i = 0; i < listOfPoints.size(); i++) {
            addPointStay(listOfPoints.get(i), weight);
            weight = weight.multiply(multiplier);
        }
        return this;
    }

    public final ClSimplexSolver addPointStay(ClVariable vx, ClVariable vy,
            Fraction weight) throws ExCLRequiredFailure, ExCLInternalError {
        addStay(vx, ClStrength.weak, weight);
        addStay(vy, ClStrength.weak, weight);
        return this;
    }

    public final ClSimplexSolver addPointStay(ClVariable vx, ClVariable vy)
            throws ExCLRequiredFailure, ExCLInternalError {
        addPointStay(vx, vy, Fraction.ONE);
        return this;
    }

    public final ClSimplexSolver addPointStay(ClPoint clp, Fraction weight)
            throws ExCLRequiredFailure, ExCLInternalError {
        addStay(clp.X(), ClStrength.weak, weight);
        addStay(clp.Y(), ClStrength.weak, weight);
        return this;
    }

    public final ClSimplexSolver addPointStay(ClPoint clp)
            throws ExCLRequiredFailure, ExCLInternalError {
        addPointStay(clp, Fraction.ONE);
        return this;
    }

    // Add a stay of the given strength (default to weak) of v to the tableau
    public final ClSimplexSolver addStay(ClVariable v, ClStrength strength,
            Fraction weight) throws ExCLRequiredFailure, ExCLInternalError {
        ClStayConstraint cn = new ClStayConstraint(v, strength, weight);
        return addConstraint(cn);
    }

    // default to weight == 1.0
    public final ClSimplexSolver addStay(ClVariable v, ClStrength strength)
            throws ExCLRequiredFailure, ExCLInternalError {
        addStay(v, strength, Fraction.ONE);
        return this;
    }

    // default to strength = weak
    public final ClSimplexSolver addStay(ClVariable v)
            throws ExCLRequiredFailure, ExCLInternalError {
        addStay(v, ClStrength.weak, Fraction.ONE);
        return this;
    }

    // Remove the constraint cn from the tableau
    // Also remove any error variable associated with cn
    public final ClSimplexSolver removeConstraint(ClConstraint cn)
            throws ExCLConstraintNotFound, ExCLInternalError {
        if (fTraceOn)
            fnenterprint("removeConstraint: " + cn);
        if (fTraceOn)
            traceprint(this.toString());

        _fNeedsSolving = true;

        resetStayConstants();

        ClLinearExpression zRow = rowExpression(_objective);

        Set<ClAbstractVariable> eVars = _errorVars.get(cn);
        if (fTraceOn)
            traceprint("eVars == " + eVars);

        if (eVars != null) {
            for (ClAbstractVariable clv : eVars) {
                final ClLinearExpression expr = rowExpression(clv);
                if (expr == null) {
                    zRow.addVariable(clv, cn.weight().negate().multiply
                            (cn.strength().symbolicWeight().asFraction()),
                            _objective, this);
                } else { // the error variable was in the basis
                    zRow.addExpression(expr, cn.weight().negate().multiply
                            (cn.strength().symbolicWeight().asFraction()),
                            _objective, this);
                }
            }
        }

        ClAbstractVariable marker = _markerVars.remove(cn);
        if (marker == null) {
            throw new ExCLConstraintNotFound();
        }

        if (fTraceOn)
            traceprint("Looking to remove var " + marker);

        if (rowExpression(marker) == null) {
            // not in the basis, so need to do some work
            Set<ClAbstractVariable> col = _columns.get(marker);

            if (fTraceOn)
                traceprint("Must pivot -- columns are " + col);

            ClAbstractVariable exitVar = null;
            Fraction minRatio = Fraction.ZERO;
            for (ClAbstractVariable v : col) {
                if (v.isRestricted()) {
                    final ClLinearExpression expr = rowExpression(v);
                    Fraction coeff = expr.coefficientFor(marker);
                    if (fTraceOn)
                        traceprint("Marker " + marker + "'s coefficient in "
                                + expr + " is " + coeff);
                    if (coeff.compareTo(Fraction.ZERO) < 0) {
                        Fraction r = expr.constant().negate().divide(coeff);
                        if (exitVar == null || r.compareTo(minRatio) < 0) {
                            minRatio = r;
                            exitVar = v;
                        }
                    }
                }
            }
            if (exitVar == null) {
                if (fTraceOn)
                    traceprint("exitVar is still null");
                for (ClAbstractVariable v : col) {
                    if (v.isRestricted()) {
                        final ClLinearExpression expr = rowExpression(v);
                        Fraction coeff = expr.coefficientFor(marker);
                        Fraction r = expr.constant().divide(coeff);
                        if (exitVar == null || r.compareTo(minRatio) < 0) {
                            minRatio = r;
                            exitVar = v;
                        }
                    }
                }
            }

            if (exitVar == null) {
                // exitVar is still null
                if (col.size() == 0) {
                    removeColumn(marker);
                } else {
                    exitVar = col.iterator().next();
                }
            }

            if (exitVar != null) {
                pivot(marker, exitVar);
            }
        }

        if (rowExpression(marker) != null) {
            /*
             * LM -- removed because this doesn't force JIT to purge the
             * reference ClLinearExpression expr = removeRow(marker); expr =
             * null;
             */
            removeRow(marker);
        }

        if (eVars != null) {
            for (ClAbstractVariable v : eVars) {
                // FIXGJBNOW != or equals?
                if (v != marker) {
                    removeColumn(v);
                    v = null;
                }
            }
        }

        if (cn.isStayConstraint()) {
            if (eVars != null) {
                for (int i = 0; i < _stayPlusErrorVars.size(); i++) {
                    eVars.remove(_stayPlusErrorVars.get(i));
                    eVars.remove(_stayMinusErrorVars.get(i));
                }
            }
        } else if (cn.isEditConstraint()) {
            assert eVars != null : "eVars != null";
            ClEditConstraint cnEdit = (ClEditConstraint) cn;
            ClVariable clv = cnEdit.variable();
            ClEditInfo cei = _editVarMap.get(clv);
            ClSlackVariable clvEditMinus = cei.ClvEditMinus();
            // ClSlackVariable clvEditPlus = cei.ClvEditPlus();
            // the clvEditPlus is a marker variable that is removed elsewhere
            removeColumn(clvEditMinus);
            _editVarMap.remove(clv);
        }

        // FIXGJB do the remove at top
        if (eVars != null) {
            _errorVars.remove(eVars);
        }
        marker = null;

        if (_fOptimizeAutomatically) {
            optimize(_objective);
            setExternalVariables();
        }

        return this;
    }

    // Re-initialize this solver from the original constraints, thus
    // getting rid of any accumulated numerical problems. (Actually, we
    // haven't definitely observed any such problems yet)
    public final void reset() throws ExCLInternalError {
        if (fTraceOn)
            fnenterprint("reset");
        throw new ExCLInternalError("reset not implemented");
    }

    // Re-solve the current collection of constraints for new values for
    // the constants of the edit variables.
    // DEPRECATED: use suggestValue(...) then resolve()
    // If you must use this, be sure to not use it if you
    // remove an edit variable (or edit constraint) from the middle
    // of a list of edits and then try to resolve with this function
    // (you'll get the wrong answer, because the indices will be wrong
    // in the ClEditInfo objects)
    public final void resolve(List<ClFractionWrapper> newEditConstants)
            throws ExCLInternalError {
        if (fTraceOn)
            fnenterprint("resolve" + newEditConstants);
        for (ClVariable v : _editVarMap.keySet()) {
            ClEditInfo cei = _editVarMap.get(v);
            int i = cei.Index();
            try {
                if (i < newEditConstants.size())
                    suggestValue(v, newEditConstants.get(i).getValue());
            } catch (ExCLError err) {
                throw new ExCLInternalError("Error during resolve");
            }
        }
        resolve();
    }

    // Convenience function for resolve-s of two variables
    public final void resolve(Fraction x, Fraction y) throws ExCLInternalError {
        _resolve_pair.get(0).setValue(x);
        _resolve_pair.get(1).setValue(y);
        resolve(_resolve_pair);
    }

    // Re-solve the current collection of constraints, given the new
    // values for the edit variables that have already been
    // suggested (see suggestValue() method)
    public final void resolve() throws ExCLInternalError {
        if (fTraceOn)
            fnenterprint("resolve()");
        dualOptimize();
        setExternalVariables();
        _infeasibleRows.clear();
        resetStayConstants();
    }

    // Suggest a new value for an edit variable
    // the variable needs to be added as an edit variable
    // and beginEdit() needs to be called before this is called.
    // The tableau will not be solved completely until
    // after resolve() has been called
    public final ClSimplexSolver suggestValue(ClVariable v, Fraction x)
            throws ExCLError {
        if (fTraceOn)
            fnenterprint("suggestValue(" + v + ", " + x + ")");
        ClEditInfo cei = _editVarMap.get(v);
        if (cei == null) {
            System.err.println("suggestValue for variable " + v
                    + ", but var is not an edit variable\n");
            throw new ExCLError();
        }
        // int i = cei.Index();
        ClSlackVariable clvEditPlus = cei.ClvEditPlus();
        ClSlackVariable clvEditMinus = cei.ClvEditMinus();
        Fraction delta = x.subtract(cei.PrevEditConstant());
        cei.SetPrevEditConstant(x);
        deltaEditConstant(delta, clvEditPlus, clvEditMinus);
        return this;
    }

    // Control whether optimization and setting of external variables
    // is done automatically or not. By default it is done
    // automatically and solve() never needs to be explicitly
    // called by client code; if setAutosolve is put to false,
    // then solve() needs to be invoked explicitly before using
    // variables' values
    // (Turning off autosolve while adding lots and lots of
    // constraints [ala the addDel test in ClTests] saved
    // about 20% in runtime, from 68sec to 54sec for 900 constraints,
    // with 126 failed adds)
    public final ClSimplexSolver setAutosolve(boolean f) {
        _fOptimizeAutomatically = f;
        return this;
    }

    // Tell whether we are autosolving
    public final boolean FIsAutosolving() {
        return _fOptimizeAutomatically;
    }

    // If autosolving has been turned off, client code needs
    // to explicitly call solve() before accessing variables
    // values
    public final ClSimplexSolver solve() throws ExCLInternalError {
        if (_fNeedsSolving) {
            optimize(_objective);
            setExternalVariables();
        }
        return this;
    }

    public ClSimplexSolver setEditedValue(ClVariable v, Fraction n)
            throws ExCLInternalError {
        if (!FContainsVariable(v)) {
            v.change_value(n);
            return this;
        }

        if (!n.equals(v.value())) {
            addEditVar(v);
            beginEdit();
            try {
                suggestValue(v, n);
            } catch (ExCLError e) {
                // just added it above, so we shouldn't get an error
                throw new ExCLInternalError("Error in setEditedValue");
            }
            endEdit();
        }
        return this;
    }

    public final boolean FContainsVariable(ClVariable v)
            throws ExCLInternalError {
        return columnsHasKey(v) || (rowExpression(v) != null);
    }

    public ClSimplexSolver addVar(ClVariable v) throws ExCLInternalError {
        if (!FContainsVariable(v)) {
            try {
                addStay(v);
            } catch (ExCLRequiredFailure e) {
                // cannot have a required failure, since we add w/ weak
                throw new ExCLInternalError(
                        "Error in addVar -- required failure is impossible");
            }
            if (fTraceOn) {
                traceprint("added initial stay on " + v);
            }
        }
        return this;
    }

    // Originally from Michael Noth <noth@cs>
    public final String getInternalInfo() {
        StringBuffer retstr = new StringBuffer(super.getInternalInfo());
        retstr.append("\nSolver info:\n");
        retstr.append("Stay Error Variables: ");
        retstr.append(_stayPlusErrorVars.size() + _stayMinusErrorVars.size());
        retstr.append(" (" + _stayPlusErrorVars.size() + " +, ");
        retstr.append(_stayMinusErrorVars.size() + " -)\n");
        retstr.append("Edit Variables: " + _editVarMap.size());
        retstr.append("\n");
        return retstr.toString();
    }

    public final String getDebugInfo() {
        StringBuffer bstr = new StringBuffer(toString());
        bstr.append(getInternalInfo());
        bstr.append("\n");
        return bstr.toString();
    }

    public final String toString() {
        StringBuffer bstr = new StringBuffer(super.toString());
        bstr.append("\n_stayPlusErrorVars: ");
        bstr.append(_stayPlusErrorVars);
        bstr.append("\n_stayMinusErrorVars: ");
        bstr.append(_stayMinusErrorVars);

        bstr.append("\n");
        return bstr.toString();
    }

    public Map<ClConstraint, ClAbstractVariable> getConstraintMap() {
        return _markerVars;
    }

    // // END PUBLIC INTERFACE

    // Add the constraint expr=0 to the inequality tableau using an
    // artificial variable. To do this, create an artificial variable
    // av and add av=expr to the inequality tableau, then make av be 0.
    // (Raise an exception if we can't attain av=0.)
    protected final void addWithArtificialVariable(ClLinearExpression expr)
            throws ExCLRequiredFailure, ExCLInternalError {
        if (fTraceOn)
            fnenterprint("addWithArtificialVariable: " + expr);

        ClSlackVariable av = new ClSlackVariable(++_artificialCounter, "a");
        ClObjectiveVariable az = new ClObjectiveVariable("az");
        ClLinearExpression azRow = (ClLinearExpression) expr.clone();

        if (fTraceOn)
            traceprint("before addRows:\n" + this);

        addRow(az, azRow);
        addRow(av, expr);

        if (fTraceOn)
            traceprint("after addRows:\n" + this);
        optimize(az);

        ClLinearExpression azTableauRow = rowExpression(az);

        if (fTraceOn)
            traceprint("azTableauRow.constant() == " + azTableauRow.constant());

        if (!azTableauRow.constant().equals(Fraction.ZERO)) {
            removeRow(az);
            removeColumn(av);
            throw new ExCLRequiredFailure();
        }

        // See if av is a basic variable
        final ClLinearExpression e = rowExpression(av);

        if (e != null) {
            // find another variable in this row and pivot,
            // so that av becomes parametric
            if (e.isConstant()) {
                // if there isn't another variable in the row
                // then the tableau contains the equation av=0 --
                // just delete av's row
                removeRow(av);
                removeRow(az);
                return;
            }
            ClAbstractVariable entryVar = e.anyPivotableVariable();
            pivot(entryVar, av);
        }
        assert rowExpression(av) == null : "rowExpression(av) == null";
        removeColumn(av);
        removeRow(az);
    }

    // We are trying to add the constraint expr=0 to the appropriate
    // tableau. Try to add expr directly to the tableax without
    // creating an artificial variable. Return true if successful and
    // false if not.
    protected final boolean tryAddingDirectly(ClLinearExpression expr)
            throws ExCLRequiredFailure {
        if (fTraceOn)
            fnenterprint("tryAddingDirectly: " + expr);
        final ClAbstractVariable subject = chooseSubject(expr);
        if (subject == null) {
            if (fTraceOn)
                fnexitprint("returning false");
            return false;
        }
        expr.newSubject(subject);
        if (columnsHasKey(subject)) {
            substituteOut(subject, expr);
        }
        addRow(subject, expr);
        if (fTraceOn)
            fnexitprint("returning true");
        return true; // successfully added directly
    }

    // We are trying to add the constraint expr=0 to the tableaux. Try
    // to choose a subject (a variable to become basic) from among the
    // current variables in expr. If expr contains any unrestricted
    // variables, then we must choose an unrestricted variable as the
    // subject. Also, if the subject is new to the solver we won't have
    // to do any substitutions, so we prefer new variables to ones that
    // are currently noted as parametric. If expr contains only
    // restricted variables, if there is a restricted variable with a
    // negative coefficient that is new to the solver we can make that
    // the subject. Otherwise we can't find a subject, so return nil.
    // (In this last case we have to add an artificial variable and use
    // that variable as the subject -- this is done outside this method
    // though.)
    // 
    // Note: in checking for variables that are new to the solver, we
    // ignore whether a variable occurs in the objective function, since
    // new slack variables are added to the objective function by
    // 'newExpression:', which is called before this method.
    protected final ClAbstractVariable chooseSubject(ClLinearExpression expr)
            throws ExCLRequiredFailure {
        if (fTraceOn)
            fnenterprint("chooseSubject: " + expr);
        ClAbstractVariable subject = null; // the current best subject, if any

        boolean foundUnrestricted = false;
        boolean foundNewRestricted = false;

        final Map<ClAbstractVariable, ClFractionWrapper> terms = expr.terms();

        for (ClAbstractVariable v : terms.keySet()) {
            final Fraction c = terms.get(v).getValue();

            if (foundUnrestricted) {
                if (!v.isRestricted()) {
                    if (!columnsHasKey(v))
                        return v;
                }
            } else {
                // we haven't found an restricted variable yet
                if (v.isRestricted()) {
                    if (!foundNewRestricted && !v.isDummy() && c.compareTo(Fraction.ZERO) < 0) {
                        final Set<ClAbstractVariable> col = _columns.get(v);
                        if (col == null
                                || (col.size() == 1 && columnsHasKey(_objective))) {
                            subject = v;
                            foundNewRestricted = true;
                        }
                    }
                } else {
                    subject = v;
                    foundUnrestricted = true;
                }
            }
        }

        if (subject != null)
            return subject;

        Fraction coeff = Fraction.ZERO;

        for (ClAbstractVariable v : terms.keySet()) {
            final Fraction c = terms.get(v).getValue();
            if (!v.isDummy())
                return null; // nope, no luck
            if (!columnsHasKey(v)) {
                subject = v;
                coeff = c;
            }
        }

        if (!expr.constant().equals(Fraction.ZERO)) {
            throw new ExCLRequiredFailure();
        }
        if (coeff.compareTo(Fraction.ZERO) > 0) {
            expr.multiplyMe(Fraction.mONE);
        }
        return subject;
    }

    // Each of the non-required edits will be represented by an equation
    // of the form
    // v = c + eplus - eminus
    // where v is the variable with the edit, c is the previous edit
    // value, and eplus and eminus are slack variables that hold the
    // error in satisfying the edit constraint. We are about to change
    // something, and we want to fix the constants in the equations
    // representing the edit constraints. If one of eplus and eminus is
    // basic, the other must occur only in the expression for that basic
    // error variable. (They can't both be basic.) Fix the constant in
    // this expression. Otherwise they are both nonbasic. Find all of
    // the expressions in which they occur, and fix the constants in
    // those. See the UIST paper for details.
    // (This comment was for resetEditConstants(), but that is now
    // gone since it was part of the screwey vector-based interface
    // to resolveing. --02/16/99 gjb)
    protected final void deltaEditConstant(Fraction delta,
            ClAbstractVariable plusErrorVar, ClAbstractVariable minusErrorVar) {
        if (fTraceOn)
            fnenterprint("deltaEditConstant :" + delta + ", " + plusErrorVar
                    + ", " + minusErrorVar);
        ClLinearExpression exprPlus = rowExpression(plusErrorVar);
        if (exprPlus != null) {
            exprPlus.incrementConstant(delta);

            if (exprPlus.constant().compareTo(Fraction.ZERO) < 0) {
                _infeasibleRows.add(plusErrorVar);
            }
            return;
        }

        ClLinearExpression exprMinus = rowExpression(minusErrorVar);
        if (exprMinus != null) {
            exprMinus.incrementConstant(delta.negate());
            if (exprMinus.constant().compareTo(Fraction.ZERO) < 0) {
                _infeasibleRows.add(minusErrorVar);
            }
            return;
        }

        Set<ClAbstractVariable> columnVars = _columns.get(minusErrorVar);

        for (ClAbstractVariable basicVar : columnVars) {
            ClLinearExpression expr = rowExpression(basicVar);
            // assert(expr != null, "expr != null" );
            final Fraction c = expr.coefficientFor(minusErrorVar);
            expr.incrementConstant(c.multiply(delta));
            if (basicVar.isRestricted() && expr.constant().compareTo(Fraction.ZERO) < 0) {
                _infeasibleRows.add(basicVar);
            }
        }
    }

    // We have set new values for the constants in the edit constraints.
    // Re-optimize using the dual simplex algorithm.
    protected final void dualOptimize() throws ExCLInternalError {
        if (fTraceOn)
            fnenterprint("dualOptimize:");
        final ClLinearExpression zRow = rowExpression(_objective);
        for (Iterator<ClAbstractVariable> it=_infeasibleRows.iterator(); it.hasNext(); ) {
            ClAbstractVariable exitVar = it.next();
            it.remove();
            ClAbstractVariable entryVar = null;
            ClLinearExpression expr = rowExpression(exitVar);
            if (expr != null) {
                if (expr.constant().compareTo(Fraction.ZERO) < 0) {
                    Fraction ratio = null;
                    Fraction r;
                    Map<ClAbstractVariable, ClFractionWrapper> terms = expr.terms();
                    for (ClAbstractVariable v : terms.keySet()) {
                        Fraction c = terms.get(v).getValue();
                        if (c.compareTo(Fraction.ZERO) > 0 && v.isPivotable()) {
                            Fraction zc = zRow.coefficientFor(v);
                            r = zc.divide(c); // FIXGJB r:= zc/c or zero, as
                            // ClSymbolicWeight-s
                            if (ratio==null || r.compareTo(ratio) < 0) {
                                entryVar = v;
                                ratio = r;
                            }
                        }
                    }
                    if (ratio == null) {
                        throw new ExCLInternalError(
                                "ratio == nil (MAX_VALUE) in dualOptimize");
                    }
                    pivot(entryVar, exitVar);
                }
            }
        }
    }

    // Make a new linear expression representing the constraint cn,
    // replacing any basic variables with their defining expressions.
    // Normalize if necessary so that the constant is non-negative. If
    // the constraint is non-required give its error variables an
    // appropriate weight in the objective function.
    protected final ClLinearExpression newExpression(ClConstraint cn,
            List<ClSlackVariable> eplusEminus, ClFractionWrapper prevEConstant) {
        if (fTraceOn)
            fnenterprint("newExpression: " + cn);
        if (fTraceOn)
            traceprint("cn.isInequality() == " + cn.isInequality());
        if (fTraceOn)
            traceprint("cn.isRequired() == " + cn.isRequired());

        final ClLinearExpression cnExpr = cn.expression();
        ClLinearExpression expr = new ClLinearExpression(cnExpr.constant());
        ClSlackVariable slackVar = new ClSlackVariable();
        ClDummyVariable dummyVar = new ClDummyVariable();
        ClSlackVariable eminus = new ClSlackVariable();
        ClSlackVariable eplus = new ClSlackVariable();
        final Map<ClAbstractVariable, ClFractionWrapper> cnTerms = cnExpr.terms();
        for (ClAbstractVariable v : cnTerms.keySet()) {
            Fraction c = cnTerms.get(v).getValue();
            final ClLinearExpression e = rowExpression(v);
            if (e == null)
                expr.addVariable(v, c);
            else
                expr.addExpression(e, c);
        }

        if (cn.isInequality()) {
            ++_slackCounter;
            slackVar = new ClSlackVariable(_slackCounter, "s");
            expr.setVariable(slackVar, Fraction.mONE);
            _markerVars.put(cn, slackVar);
            if (!cn.isRequired()) {
                ++_slackCounter;
                eminus = new ClSlackVariable(_slackCounter, "em");
                expr.setVariable(eminus, Fraction.ONE);
                ClLinearExpression zRow = rowExpression(_objective);
                ClSymbolicWeight sw = cn.strength().symbolicWeight().times(
                        cn.weight());
                zRow.setVariable(eminus, sw.asFraction());
                insertErrorVar(cn, eminus);
                noteAddedVariable(eminus, _objective);
            }
        } else {
            // cn is an equality
            if (cn.isRequired()) {
                ++_dummyCounter;
                dummyVar = new ClDummyVariable(_dummyCounter, "d");
                expr.setVariable(dummyVar, Fraction.ONE);
                _markerVars.put(cn, dummyVar);
                if (fTraceOn)
                    traceprint("Adding dummyVar == d" + _dummyCounter);
            } else {
                ++_slackCounter;
                eplus = new ClSlackVariable(_slackCounter, "ep");
                eminus = new ClSlackVariable(_slackCounter, "em");

                expr.setVariable(eplus, Fraction.mONE);
                expr.setVariable(eminus, Fraction.ONE);
                _markerVars.put(cn, eplus);
                ClLinearExpression zRow = rowExpression(_objective);
                ClSymbolicWeight sw = cn.strength().symbolicWeight().times(
                        cn.weight());
                Fraction swCoeff = sw.asFraction();
                if (swCoeff.equals(Fraction.ZERO)) {
                    if (fTraceOn)
                        traceprint("sw == " + sw);
                    if (fTraceOn)
                        traceprint("cn == " + cn);
                    if (fTraceOn)
                        traceprint("adding " + eplus + " and " + eminus
                                + " with swCoeff == " + swCoeff);
                }
                zRow.setVariable(eplus, swCoeff);
                noteAddedVariable(eplus, _objective);
                zRow.setVariable(eminus, swCoeff);
                noteAddedVariable(eminus, _objective);
                insertErrorVar(cn, eminus);
                insertErrorVar(cn, eplus);
                if (cn.isStayConstraint()) {
                    _stayPlusErrorVars.add(eplus);
                    _stayMinusErrorVars.add(eminus);
                } else if (cn.isEditConstraint()) {
                    eplusEminus.add(eplus);
                    eplusEminus.add(eminus);
                    prevEConstant.setValue(cnExpr.constant());
                }
            }
        }

        if (expr.constant().compareTo(Fraction.ZERO) < 0)
            expr.multiplyMe(Fraction.mONE);

        if (fTraceOn)
            fnexitprint("returning " + expr);
        return expr;
    }

    // Minimize the value of the objective. (The tableau should already
    // be feasible.)
    protected final void optimize(ClObjectiveVariable zVar)
            throws ExCLInternalError {
        if (fTraceOn)
            fnenterprint("optimize: " + zVar);
        if (fTraceOn)
            traceprint(this.toString());

        ClLinearExpression zRow = rowExpression(zVar);
        assert zRow != null : "zRow != null";
        ClAbstractVariable entryVar = null;
        ClAbstractVariable exitVar = null;
        while (true) {
            Fraction objectiveCoeff = Fraction.ZERO;
            Map<ClAbstractVariable, ClFractionWrapper> terms = zRow.terms();
            for (ClAbstractVariable v : terms.keySet()) {
                Fraction c = terms.get(v).getValue();
                if (v.isPivotable() && c.compareTo(objectiveCoeff) < 0) {
                    objectiveCoeff = c;
                    entryVar = v;
                }
            }
            if (objectiveCoeff.compareTo(_epsilon.negate()) >= 0 || entryVar == null)
                return;
            if (fTraceOn)
                traceprint("entryVar == " + entryVar + ", objectiveCoeff == "
                        + objectiveCoeff);

            Fraction minRatio = null;
            Set<ClAbstractVariable> columnVars = _columns.get(entryVar);
            Fraction r = Fraction.ZERO;
            for (ClAbstractVariable v : columnVars) {
                if (fTraceOn)
                    traceprint("Checking " + v);
                if (v.isPivotable()) {
                    final ClLinearExpression expr = rowExpression(v);
                    Fraction coeff = expr.coefficientFor(entryVar);
                    if (fTraceOn)
                        traceprint("pivotable, coeff = " + coeff);
                    if (coeff.compareTo(Fraction.ZERO) < 0) {
                        r = expr.constant().negate().divide(coeff);
                        if (minRatio == null || r.compareTo(minRatio) < 0) {
                            if (fTraceOn)
                                traceprint("New minratio == " + r);
                            minRatio = r;
                            exitVar = v;
                        }
                    }
                }
            }
            if (minRatio == null) {
                throw new ExCLInternalError(
                        "Objective function is unbounded in optimize");
            }
            pivot(entryVar, exitVar);
            if (fTraceOn)
                traceprint(this.toString());
        }
    }

    // Do a pivot. Move entryVar into the basis (i.e. make it a basic variable),
    // and move exitVar out of the basis (i.e., make it a parametric variable)
    protected final void pivot(ClAbstractVariable entryVar,
            ClAbstractVariable exitVar) throws ExCLInternalError {
        if (fTraceOn)
            fnenterprint("pivot: " + entryVar + ", " + exitVar);

        // the entryVar might be non-pivotable if we're doing a removeConstraint
        // --
        // otherwise it should be a pivotable variable -- enforced at call
        // sites,
        // hopefully

        ClLinearExpression pexpr = removeRow(exitVar);

        pexpr.changeSubject(exitVar, entryVar);
        substituteOut(entryVar, pexpr);
        addRow(entryVar, pexpr);
    }

    // Each of the non-required stays will be represented by an equation
    // of the form
    // v = c + eplus - eminus
    // where v is the variable with the stay, c is the previous value of
    // v, and eplus and eminus are slack variables that hold the error
    // in satisfying the stay constraint. We are about to change
    // something, and we want to fix the constants in the equations
    // representing the stays. If both eplus and eminus are nonbasic
    // they have value 0 in the current solution, meaning the previous
    // stay was exactly satisfied. In this case nothing needs to be
    // changed. Otherwise one of them is basic, and the other must
    // occur only in the expression for that basic error variable.
    // Reset the constant in this expression to 0.
    protected final void resetStayConstants() {
        if (fTraceOn)
            fnenterprint("resetStayConstants");

        for (int i = 0; i < _stayPlusErrorVars.size(); i++) {
            ClLinearExpression expr = rowExpression(_stayPlusErrorVars.get(i));
            if (expr == null)
                expr = rowExpression(_stayMinusErrorVars.get(i));
            if (expr != null)
                expr.set_constant(Fraction.ZERO);
        }
    }

    // Set the external variables known to this solver to their appropriate
    // values.
    // Set each external basic variable to its value, and set each
    // external parametric variable to 0. (It isn't clear that we will
    // ever have external parametric variables -- every external
    // variable should either have a stay on it, or have an equation
    // that defines it in terms of other external variables that do have
    // stays. For the moment I'll put this in though.) Variables that
    // are internal to the solver don't actually store values -- their
    // values are just implicit in the tableu -- so we don't need to set
    // them.
    protected final void setExternalVariables() {
        if (fTraceOn)
            fnenterprint("setExternalVariables:");
        if (fTraceOn)
            traceprint(this.toString());

        for (ClAbstractVariable vv : _externalParametricVars) {
            ClVariable v = (ClVariable) vv;
            if (rowExpression(v) != null) {
                System.err.println("Error: variable" + v
                        + " in _externalParametricVars is basic");
                continue;
            }
            v.change_value(Fraction.ZERO);
        }

        for (ClAbstractVariable vv : _externalRows) {
            ClVariable v = (ClVariable) vv;
            ClLinearExpression expr = rowExpression(v);
            if (fTraceOn)
                debugprint("v == " + v);
            if (fTraceOn)
                debugprint("expr == " + expr);
            v.change_value(expr.constant());
        }

        _fNeedsSolving = false;
    }
    Fraction objectiveValue() {
        ClLinearExpression expr = rowExpression(this._objective);
        return expr.constant();
    }

    // Protected convenience function to insert an error variable into
    // the _errorVars set, creating the mapping with put as necessary
    protected final void insertErrorVar(ClConstraint cn, ClAbstractVariable var) {
        if (fTraceOn)
            fnenterprint("insertErrorVar:" + cn + ", " + var);

        Set<ClAbstractVariable> cnset = _errorVars.get(var);
        if (cnset == null)
            _errorVars.put(cn, cnset = new LinkedHashSet<ClAbstractVariable>());
        cnset.add(var);
    }

    // // BEGIN PRIVATE INSTANCE FIELDS

    // the arrays of positive and negative error vars for the stay constraints
    // (need both positive and negative since they have only non-negative
    // values)
    private List<ClSlackVariable> _stayMinusErrorVars;
    private List<ClSlackVariable> _stayPlusErrorVars;

    // give error variables for a non required constraint,
    // maps to ClSlackVariable-s
    private Map<ClConstraint, Set<ClAbstractVariable>> _errorVars; // map
    // ClConstraint
    // to
    // Set
    // (of ClVariable)

    // Return a lookup table giving the marker variable for each
    // constraint (used when deleting a constraint).
    private Map<ClConstraint, ClAbstractVariable> _markerVars; // map
    // ClConstraint
    // to
    // ClVariable

    private ClObjectiveVariable _objective;

    // Map edit variables to ClEditInfo-s.
    // ClEditInfo instances contain all the information for an
    // edit constraint (the edit plus/minus vars, the index [for old-style
    // resolve(Vector...) interface], and the previous value.
    // (ClEditInfo replaces the parallel vectors from the Smalltalk impl.)
    private Map<ClVariable, ClEditInfo> _editVarMap; // map
    // ClVariable
    // to a
    // ClEditInfo

    private long _slackCounter;
    private long _artificialCounter;
    private long _dummyCounter;

    private List<ClFractionWrapper> _resolve_pair;

    private Fraction _epsilon;

    private boolean _fOptimizeAutomatically;
    private boolean _fNeedsSolving;

    private Stack<Integer> _stkCedcns;
}
