package net.cscott.sdr.calls.lists;

import java.util.Collections;
import java.util.List;

import net.cscott.sdr.calls.Breather;
import net.cscott.sdr.calls.Call;
import net.cscott.sdr.calls.DanceState;
import net.cscott.sdr.calls.Dancer;
import net.cscott.sdr.calls.DancerPath;
import net.cscott.sdr.calls.EvalPrim;
import net.cscott.sdr.calls.Evaluator;
import net.cscott.sdr.calls.Formation;
import net.cscott.sdr.calls.Position;
import net.cscott.sdr.calls.Program;
import net.cscott.sdr.calls.ast.Apply;
import net.cscott.sdr.calls.ast.Comp;
import net.cscott.sdr.calls.ast.Expr;
import net.cscott.sdr.calls.ast.Prim;
import net.cscott.sdr.calls.ast.Seq;
import net.cscott.sdr.calls.grm.Rule;
import net.cscott.sdr.util.Fraction;

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
        @Override
        public List<Expr> getDefaultArguments() {
            return Collections.emptyList();
        }
    }

    public static final Call ROLL = new PlusCall("_roll") {
	// the lists say 2 beats for 'roll', but that's way too long.
	// try one instead.
        final Prim rightRoll = Prim.valueOf("(Prim 0, 0, right, 1, preserve-sweep)");
        final Prim leftRoll = Prim.valueOf("(Prim 0, 0, left, 1, preserve-sweep)");
        @Override
        public int getMinNumberOfArguments() { return 0; }
        @Override
        public Rule getRule() { return null; /* internal call */ }
        @Override
        public Evaluator getEvaluator(DanceState ds, final List<Expr> args) {
            /* if we've got an argument this is "<anything> and roll"
             * otherwise it's just plain "roll" (ie, nothing and roll) */
            assert args.size() == 0 || args.size() == 1;
            Evaluator rolle = new Evaluator() {
                @Override
                public Evaluator evaluate(DanceState ds) {
                    // go through the dancer's last movements and add a roll
                    //  1) remove any trailing 'stand still' actions
                    ds.unsyncDancers();
                    //  2) add rolls.
                    Formation f = ds.currentFormation();
                    //     (resolve collisions before rolling)
                    Formation bf = Breather.breathe(f);
                    for (Dancer d : f.dancers()) {
                        Position from = f.location(d);
                        Position bfrom = bf.location(d);
                        int rollDir = from.roll().compareTo(Fraction.ZERO);
                        DancerPath dp;
                        if (rollDir < 0)
                            dp = EvalPrim.apply(leftRoll, from, 1);
                        else if (rollDir > 0)
                            dp = EvalPrim.apply(rightRoll, from, 1);
                        else
                            dp = EvalPrim.apply(Prim.STAND_STILL, from, 1);
                        // move ending point to match breathed formation
                        dp = dp.translate(dp.from, dp.to.relocate
                                          (bfrom.x, bfrom.y, dp.to.facing));
                        ds.add(d, dp);
                    }
                    return null; /* ta-da! */
                }
            };
            /* if there's an arg, do that first */
            if (args.size() > 0) {
                final Evaluator sub = new Apply(args.get(0)).evaluator(ds);
                // set hasSimpleExpansion to allow expanding as
                // "and(<arg>, roll)"?
                rolle = new Evaluator.EvaluatorChain(sub, rolle) {
                    @Override
                    public boolean hasSimpleExpansion() { return true; }
                    @Override
                    public Comp simpleExpansion() {
                        return new Seq(new Apply(new Expr
                                ("and", args.get(0), Expr.literal("_roll"))));
                    }
                };
            }
            return rolle;
        }
    };
}
