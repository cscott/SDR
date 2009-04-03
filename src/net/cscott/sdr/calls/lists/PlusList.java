package net.cscott.sdr.calls.lists;

import static net.cscott.sdr.calls.transform.CallFileLexer.PART;

import java.util.ArrayList;
import java.util.List;

import net.cscott.sdr.calls.BadCallException;
import net.cscott.sdr.calls.Call;
import net.cscott.sdr.calls.DanceState;
import net.cscott.sdr.calls.Dancer;
import net.cscott.sdr.calls.DancerPath;
import net.cscott.sdr.calls.Formation;
import net.cscott.sdr.calls.Position;
import net.cscott.sdr.calls.Program;
import net.cscott.sdr.calls.DancerPath.PointOfRotation;
import net.cscott.sdr.calls.ast.Apply;
import net.cscott.sdr.calls.ast.Comp;
import net.cscott.sdr.calls.ast.Part;
import net.cscott.sdr.calls.ast.Seq;
import net.cscott.sdr.calls.ast.SeqCall;
import net.cscott.sdr.calls.grm.Grm;
import net.cscott.sdr.calls.grm.Rule;
import net.cscott.sdr.calls.transform.Evaluator;
import net.cscott.sdr.util.Fraction;
import net.cscott.sdr.util.Point;

/**
 * The <code>PlusList</code> class contains complex call
 * and concept definitions which are on the 'plus' program.
 * Note that "simple" calls and concepts are defined in
 * the resource file at
 * <a href="doc-files/plus.calls"><code>net/cscott/sdr/calls/lists/plus.calls</code></a>;
 * this class contains only those definitions for which an
 * executable component is required.
 * @author C. Scott Ananian
 */
public abstract class PlusList {
    // hide constructor.
    private PlusList() { }

    private static abstract class PlusCall extends Call {
        private final String name;
        PlusCall(String name) { this.name = name; }
        @Override
        public final String getName() { return name; }
        @Override
        public final Program getProgram() { return Program.PLUS; }
    }

    public static final Call ROLL = new PlusCall("_roll") {
        @Override
        public Comp apply(Apply ast) {
            assert false : "This call uses a custom Evaluator";
            return null;
        }
        @Override
        public int getMinNumberOfArguments() { return 0; }
        @Override
        public Rule getRule() { return null; /* internal call */ }
        @Override
        public Evaluator getEvaluator(Apply ast) {
            /* if we've got an argument this is "<anything> and roll"
             * otherwise it's just plain "roll" (ie, nothing and roll) */
            assert ast.args.size() == 0 || ast.args.size() == 1;
            Evaluator rolle = new Evaluator() {
                @Override
                public Evaluator evaluate(DanceState ds) {
                    // go through the dancer's last movements and add a roll
                    //  1) remove any trailing 'stand still' actions
                    ds.unsyncDancers();
                    //  2) add rolls.
                    Formation f = ds.currentFormation();
                    for (Dancer d : f.dancers()) {
                        Position from = f.location(d);
                        Position to = from.turn(from.roll(), false);
                        if (!from.equals(to))
                            ds.add(d, new DancerPath
                                    (from, to, new Point(from.x, from.y),
                                     Fraction.TWO/* timing for roll */,
                                     PointOfRotation.SINGLE_DANCER));
                    }
                    return null; /* ta-da! */
                }
            };
            /* if there's an arg, do that first */
            if (ast.args.size() > 0) {
                Apply arg = ast.getArg(0);
                Evaluator sub = arg.evaluator();
                if (sub==null) sub = new Evaluator.Standard(arg.expand());
                rolle =  new Evaluator.EvaluatorChain(sub, rolle);
            }
            return rolle;
        }
    };
}
