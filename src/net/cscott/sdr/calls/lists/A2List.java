package net.cscott.sdr.calls.lists;

import java.util.Collections;
import java.util.List;

import org.junit.runner.RunWith;

import net.cscott.jdoctest.JDoctestRunner;
import net.cscott.sdr.calls.BadCallException;
import net.cscott.sdr.calls.Call;
import net.cscott.sdr.calls.DanceState;
import net.cscott.sdr.calls.Dancer;
import net.cscott.sdr.calls.EvalPrim;
import net.cscott.sdr.calls.Evaluator;
import net.cscott.sdr.calls.ExactRotation;
import net.cscott.sdr.calls.Formation;
import net.cscott.sdr.calls.Position;
import net.cscott.sdr.calls.Program;
import net.cscott.sdr.calls.ast.Expr;
import net.cscott.sdr.calls.ast.Prim;
import net.cscott.sdr.calls.grm.Rule;
import net.cscott.sdr.util.Fraction;

/**
 * The <code>A2List</code> class contains complex call
 * and concept definitions which are on the 'A2' program.
 * Note that "simple" calls and concepts are defined in
 * the resource file at
 * <a href="doc-files/a2.calls"><code>net/cscott/sdr/calls/lists/a2.calls</code></a>;
 * this class contains only those definitions for which an
 * executable component is required.
 * @author C. Scott Ananian
 */
@RunWith(value=JDoctestRunner.class)
public abstract class A2List {
    // hide constructor.
    private A2List() { }

    private static abstract class A2Call extends Call {
        private final String name;
        A2Call(String name) { this.name = name; }
        @Override
        public final String getName() { return name; }
        @Override
        public final Program getProgram() { return Program.A2; }
        @Override
        public List<Expr> getDefaultArguments() {
            return Collections.emptyList();
        }
    }

    /** "Primitive" counter rotate.
     * @doc.test
     *  js> importPackage(net.cscott.sdr.calls)
     *  js> importPackage(net.cscott.sdr.calls.ast)
     *  js> ds = new DanceState(new DanceProgram(Program.C4), Formation.SQUARED_SET); undefined;
     *  js> a = Apply.makeApply('_counter rotate 1/4')
     *  (Apply '_counter rotate 1/4)
     *  js> a.evaluator(ds).evaluateAll(ds)
     *  js> ds.currentFormation().toStringDiagram('|')
     *  |     2Gv  4Bv
     *  |
     *  |1B>            1G<
     *  |
     *  |3G>            3B<
     *  |
     *  |     2B^  4G^
     */
    public static final Call _COUNTER_ROTATE = new A2Call("_counter rotate 1/4") {
        @Override
        public int getMinNumberOfArguments() { return 0; }
        @Override
        public Rule getRule() { return null; /* internal call */ }
        @Override
        public Evaluator getEvaluator(DanceState ds, List<Expr> args) {
            assert args.size() == 0;
            return new Evaluator() {
                @Override
                public Evaluator evaluate(DanceState ds) {
                    Formation f = ds.currentFormation();
                    for (Dancer d : f.dancers()) {
                        Position from = f.location(d);
                        Prim cw = path2prim(from, ExactRotation.ONE_QUARTER);
                        Prim ccw = path2prim(from, ExactRotation.mONE_QUARTER);
                        // pick the one with more forward progress
                        int c = cw.y.compareTo(ccw.y);
                        if (c==0)
                            throw new BadCallException
                            ("Can't counter rotate if dancer is looking at center");
                        ds.add(d, EvalPrim.apply(d, f, (c < 0) ? ccw : cw));
                    }
                    return null;
                }
            };
        }
        private Prim path2prim(Position from, ExactRotation rot) {
            Position to = from.rotateAroundOrigin(rot);
            Fraction xDelta = to.x.subtract(from.x);
            Fraction yDelta = to.y.subtract(from.y);
            // create a vector from the x/y deltas and rotate it to be relative
            // to the dancer's starting facing direction.
            ExactRotation facing = (ExactRotation) from.facing;
            Position p = new Position(xDelta, yDelta, ExactRotation.ZERO)
                .rotateAroundOrigin(facing.negate());
            return new Prim(Prim.Direction.ASIS, p.x,
                            Prim.Direction.ASIS, p.y,
                            Prim.Direction.IN, ExactRotation.ONE_QUARTER,
                            Fraction.ONE);
        }
    };
}
