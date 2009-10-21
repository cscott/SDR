package EDU.Washington.grad.gjb.cassowary;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.junit.runner.RunWith;

import net.cscott.jdoctest.JDoctestRunner;
import net.cscott.jutil.BinaryHeap;
import net.cscott.jutil.Heap;
import net.cscott.sdr.util.Fraction;
import net.cscott.sdr.util.LL;

/** A mixed integer/linear programming solver, which uses
 * {@link ClSimplexSolver} to solve the linear relaxation of the problem.
 * @author C. Scott Ananian
 * @doc.test Simple example: integer approx of midpoint.
 *  js> importPackage(net.cscott.sdr.util) // for Fraction
 *  js> bb = new ClBranchAndBound(); undefined
 *  js> a = new ClVariable("a")
 *  [a:0/1]
 *  js> b = new ClVariable("b")
 *  [b:0/1]
 *  js> c = new ClIntegerVariable(bb, "c")
 *  [c:0/1]
 *  js> bb.addConstraint(new ClLinearEquation(a, Fraction.ZERO))
 *  js> bb.addConstraint(new ClLinearEquation(b, Fraction.valueOf(17, 2)))
 *  js> bb.addConstraint(
 *    >         new ClLinearEquation(CL.Times(Fraction.TWO, c), CL.Plus(a, b),
 *    >                              ClStrength.medium))
 *  js> bb.solve()
 *  js> a.value().toProperString()
 *  0
 *  js> b.value().toProperString()
 *  8 1/2
 *  js> c.value().toProperString()
 *  4
 * @doc.test Test case with infeasible branch
 *  js> importPackage(net.cscott.sdr.util) // for Fraction
 *  js> bb = new ClBranchAndBound(); undefined
 *  js> a = new ClBooleanVariable(bb, "a")
 *  [a:0/1]
 *  js> bb.addConstraint(new ClLinearInequality(a, CL.Op.GEQ, Fraction.ONE_HALF))
 *  js> bb.solve()
 *  js> a.value().toProperString()
 *  1
 */
@RunWith(value=JDoctestRunner.class)
public class ClBranchAndBound {
    private final ClSimplexSolver solver;
    private final List<ClIntegerVariable> intVars;
    private final List<ClConstraint> activeIntegerConstraints;
    public ClBranchAndBound() {
        this.solver = new ClSimplexSolver();
        this.solver.setAutosolve(false);
        this.intVars = new ArrayList<ClIntegerVariable>();
        this.activeIntegerConstraints = new ArrayList<ClConstraint>();
    }

    public void addConstraint(ClConstraint cl)
        throws ExCLRequiredFailure, ExCLInternalError {
        clearIntegerConstraints(); // don't want to cause spurious infeasibility
        solver.addConstraint(cl);
    }

    void addIntegerVariable(ClIntegerVariable v) {
        clearIntegerConstraints(); // don't want to cause spurious infeasibility
        intVars.add(v);
    }

    private void clearIntegerConstraints() {
        // quick out
        if (activeIntegerConstraints.isEmpty())
            return;
        // remove the active constraints.
        for (ClConstraint c : activeIntegerConstraints) {
            try {
                this.solver.removeConstraint(c);
            } catch (ExCLConstraintNotFound e) {
                assert false;
            } catch (ExCLInternalError e) {
                assert false;
            }
        }
        this.activeIntegerConstraints.clear();
    }

    /** Tracks "active subproblems" in the branch-and-bound algorithm.*/
    private class State {
        final LL<ClConstraint> soFar;
        public State() {
            this.soFar = LL.<ClConstraint>NULL();
        }
        private State(State s, ClIntegerVariable v, CL.Op op, Fraction val)
            throws ExCLInternalError {
            this.soFar = s.soFar.push(new ClLinearInequality(v, op, val));
        }
        public State[] branchStates(ClIntegerVariable v, Fraction value)
            throws ExCLInternalError {
            Fraction lo = Fraction.valueOf(value.floor());
            return new State[] {
              new State(this, v, CL.Op.LEQ, lo),
              new State(this, v, CL.Op.GEQ, lo.add(Fraction.ONE))
            };
        }
    }

    /** Find the first integer variable whose value is not integral */
    private ClIntegerVariable findFirstNonInteger() {
        // in theory we could use a cyclic list here to avoid looking at
        // the just-bound variable first all of the time.
        for (ClIntegerVariable v : this.intVars) {
            if (v.value().getDenominator() != 1)
                return v;
        }
        return null;
    }

    /** Solve the mixed integer linear programming problem, and set the
     *  values of the {@link ClVariable}s involved to the optimal solution.
     * @throws ExCLRequiredFailure if an integer solution is infeasible.
     */
    public void solve() throws ExCLInternalError, ExCLRequiredFailure {
        Fraction upperBound = null; // no known integer solution yet
        State bestState = null;

        // first time through we're going to solve the relaxed linear
        // programming problem without constraints.
        Heap<Fraction,State> active = new BinaryHeap<Fraction,State>();
        active.insert(Fraction.ZERO/* ignored*/, new State());
        clearIntegerConstraints();

        // process each active state
        while (!active.isEmpty()) {
            Entry<Fraction,State> e = active.extractMinimum();
            if (upperBound!=null && upperBound.compareTo(e.getKey()) <= 0)
                continue; // this solution can't be better than bestState

            State s = e.getValue();
            // add all the constraints
            List<ClConstraint> added = new ArrayList<ClConstraint>();
            try {
                for (ClConstraint c : s.soFar) {
                    solver.addConstraint(c);
                    added.add(c);
                }
                // ok, this solution is feasible!
                solver.solve();
                Fraction newObj = this.solver.objectiveValue();
                // if this is worse than the current upperBound, don't waste
                // any more time on it (bail!)
                if (upperBound!=null && upperBound.compareTo(newObj) <= 0)
                    continue;
                // find non integer var, and branch/bound it.
                ClIntegerVariable nextBranch = findFirstNonInteger();
                if (nextBranch!=null) {
                    for (State ns:s.branchStates(nextBranch,nextBranch.value()))
                        active.insert(newObj, ns);
                } else {
                    // hey, this is a real solution! no non-integer variables!
                    upperBound = newObj;
                    bestState = s;
                }
            } catch (ExCLRequiredFailure _ignore_) {
                // infeasible solution, just skip this one.
            } finally {
                for (ClConstraint c: added)
                    try {
                        solver.removeConstraint(c);
                    } catch (ExCLConstraintNotFound e1) {
                        assert false;
                    }
            }
        }
        // okay, we've either got a working solution (or not!)
        if (bestState == null)
            throw new ExCLRequiredFailure(); // infeasible
        // otherwise, add the constraints from the best state and return.
        for (ClConstraint c : bestState.soFar) {
            if (solver.addConstraintNoException(c))
                activeIntegerConstraints.add(c);
            else
                assert false;
        }
        // solve again (sigh) -- this ensures that variables are updated
        solver.solve();
    }
}
