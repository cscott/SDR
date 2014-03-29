package net.cscott.sdr.calls.lists;

import static net.cscott.sdr.util.Tools.l;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import net.cscott.sdr.calls.BadCallException;
import net.cscott.sdr.calls.Breather;
import net.cscott.sdr.calls.Call;
import net.cscott.sdr.calls.DanceState;
import net.cscott.sdr.calls.Dancer;
import net.cscott.sdr.calls.DancerPath;
import net.cscott.sdr.calls.Evaluator;
import net.cscott.sdr.calls.ExactRotation;
import net.cscott.sdr.calls.Formation;
import net.cscott.sdr.calls.FormationList;
import net.cscott.sdr.calls.Matcher;
import net.cscott.sdr.calls.MatcherList;
import net.cscott.sdr.calls.NoMatchException;
import net.cscott.sdr.calls.Position;
import net.cscott.sdr.calls.Program;
import net.cscott.sdr.calls.Rotation;
import net.cscott.sdr.calls.TaggedFormation;
import net.cscott.sdr.calls.TaggedFormation.Tag;
import net.cscott.sdr.calls.TimedFormation;
import net.cscott.sdr.calls.ast.Apply;
import net.cscott.sdr.calls.ast.Expr;
import net.cscott.sdr.calls.grm.Grm;
import net.cscott.sdr.calls.grm.Rule;
import net.cscott.sdr.calls.lists.A1List.SolidEvaluator;
import net.cscott.sdr.calls.lists.A1List.SolidMatch;
import net.cscott.sdr.calls.lists.A1List.SolidType;
import net.cscott.sdr.calls.transform.Finish;
import net.cscott.sdr.calls.transform.Finish.PartSelectorCall;
import net.cscott.sdr.util.Box;
import net.cscott.sdr.util.Fraction;

import org.junit.runner.RunWith;

import net.cscott.jdoctest.JDoctestRunner;

/**
 * The <code>C1List</code> class contains complex call
 * and concept definitions which are on the 'C1' program.
 * Note that "simple" calls and concepts are defined in
 * the resource file at
 * <a href="doc-files/c1.calls"><code>net/cscott/sdr/calls/lists/c1.calls</code></a>;
 * this class contains only those definitions for which an
 * executable component is required.
 * @author C. Scott Ananian
 */
@RunWith(value=JDoctestRunner.class)
public abstract class C1List {
    // hide constructor.
    private C1List() { }

    private static abstract class C1Call extends Call {
        private final String name;
        C1Call(String name) { this.name = name; }
        @Override
        public final String getName() { return name; }
        @Override
        public final Program getProgram() { return Program.C1; }
        @Override
        public List<Expr> getDefaultArguments() {
            return Collections.emptyList();
        }
    }

    /** The "finish" concept.
     * @doc.test
     *  Evaluate FINISH SWING THRU.
     *  js> importPackage(net.cscott.sdr.calls)
     *  js> importPackage(net.cscott.sdr.calls.ast)
     *  js> ds = new DanceState(new DanceProgram(Program.C1), Formation.FOUR_SQUARE); undefined;
     *  js> a1 = Expr.literal("swing thru")
     *  'swing thru
     *  js> a = new Expr("finish", a1)
     *  (Expr finish 'swing thru)
     *  js> C1List.FINISH.getEvaluator(ds, a.args).simpleExpansion()
     *  (Seq (Part 'DIVISIBLE '1 (Opt (From 'ANY (Seq (Part 'DIVISIBLE '1 (Seq (Apply (Expr _those who can turn left not grand '1/2)))))))))
     * @doc.test
     *  Evaluate FINISH RECYCLE.
     *  js> importPackage(net.cscott.sdr.calls)
     *  js> importPackage(net.cscott.sdr.calls.ast)
     *  js> ds = new DanceState(new DanceProgram(Program.C1), Formation.FOUR_SQUARE); undefined;
     *  js> Evaluator.parseAndEval(ds, "touch 1/2")
     *  js> a = new Expr("finish", Expr.literal("recycle"))
     *  (Expr finish 'recycle)
     *  js> C1List.FINISH.getEvaluator(ds, a.args).simpleExpansion()
     *  (Opt (From 'ANY (Seq (Part 'DIVISIBLE '1 (Opt (From 'ANY (Seq (Apply (Expr _box counter rotate '1/4)) (Apply 'roll))))))))
     */
    public static final Call FINISH = new PartSelectorCall
            ("finish", Program.C1, "finish (a|an)? <0=anything>") {
        @Override
        protected Finish getPartsVisitor(DanceState ds) {
            return new Finish(ds);
        }
    };

    public static final Call TANDEM = new C1Call("tandem") {
        @Override
        public int getMinNumberOfArguments() { return 1; }
        @Override
        public Rule getRule() {
            Grm g = Grm.parse("tandem <0=anything>");
            return new Rule("anything", g, Fraction.ZERO, Rule.Option.CONCEPT);
        }
        @Override
        public Evaluator getEvaluator(DanceState ds, List<Expr> args) {
            assert args.size() == 1;
            return new SolidEvaluator(args.get(0), FormationList.TANDEM,
                                      SolidMatch.ALL, SolidType.SOLID);
        }
    };

    public static final Call SIAMESE = new C1Call("siamese") {
        @Override
        public int getMinNumberOfArguments() { return 1; }
        @Override
        public Rule getRule() {
            Grm g = Grm.parse("siamese <0=anything>");
            return new Rule("anything", g, Fraction.ZERO, Rule.Option.CONCEPT);
        }
        @Override
        public Evaluator getEvaluator(DanceState ds, List<Expr> args) {
            assert args.size() == 1;
            return new SolidEvaluator(args.get(0), "siamese",
                                      MatcherList.SIAMESE, SolidType.SOLID);
        }
    };


    public static final Call CONCENTRIC = new C1Call("_concentric") {
        @Override
        public int getMinNumberOfArguments() { return 2; }
        @Override
        public Rule getRule() { return null; /* internal call */ }
        @Override
        public Evaluator getEvaluator(DanceState ds, List<Expr> args) {
            assert args.size() == 2;
            return new ConcentricEvaluator(args.get(0), args.get(1),
                                           ConcentricType.CONCENTRIC);
        }
    };

    /** Variant of 'concentric' to do. */
    public static enum ConcentricType { QUASI, CONCENTRIC, CROSS };
    /** Evaluator for concentric and quasi-concentric. */
    public static class ConcentricEvaluator extends Evaluator {
        private final Expr centersPart, endsPart;
        private final ConcentricType which;
        private static final Rotation NORTHSOUTH =
                Rotation.fromAbsoluteString("|");
        private static final boolean DEBUG = false;
        public ConcentricEvaluator(Expr centersPart, Expr endsPart,
                                   ConcentricType which) {
            this.centersPart = centersPart;
            this.endsPart = endsPart;
            this.which = which;
        }
        @Override
        public Evaluator evaluate(DanceState ds) {
            // step 1: pull out the centers/ends
            TaggedFormation f = TaggedFormation.coerce(ds.currentFormation());
            Set<Dancer> centerDancers = f.tagged(Tag.CENTER);
            Set<Dancer> endDancers = new HashSet<Dancer>(f.dancers());
            endDancers.removeAll(centerDancers); // all those who aren't CENTERs
            if (centerDancers.isEmpty())
                throw new BadCallException("No centers!");
            if (endDancers.isEmpty())
                throw new BadCallException("Everyone is a center!");
            Formation centerF = f.select(centerDancers).onlySelected();
            Formation endF = f.select(endDancers).onlySelected();
            // xxx should look at whether "eventual ends" (ie, the centers when
            //     doing CROSS concentric) think they are in lines or columns.
            boolean isWide = endF.bounds().width()
                                .compareTo(endF.bounds().height()) > 0;
            // do the call in each separate formation.
            // (breathe to eliminate space left by centers in end formation)
            endF = Breather.breathe(endF);
            DanceState centerS = ds.cloneAndClear(centerF);
            DanceState endS = ds.cloneAndClear(endF);
            if (DEBUG) {
                System.err.println("CENTER\n"+centerF.toStringDiagram());
                System.err.println("END\n"+endF.toStringDiagram());
            }
            TreeSet<Fraction> moments = new TreeSet<Fraction>();
            new Apply(this.centersPart).evaluator(centerS).evaluateAll(centerS);
            for (TimedFormation tf: centerS.formations())
                moments.add(tf.time);
            // in CROSS, have the ends wait until the centers are done
            Fraction endsOffset = Fraction.ZERO;
            if (which==ConcentricType.CROSS) {
                endsOffset = moments.last();
                endS.syncDancers(endsOffset);
            }
            new Apply(this.endsPart).evaluator(endS).evaluateAll(endS);
            for (TimedFormation tf: endS.formations())
                moments.add(tf.time);
            for (DanceState nds: l(centerS, endS))
                nds.syncDancers(moments.last());

            // A1. if the new outside formation is a 1x4, the long axis can
            //     only go one way
            // A2. if the new outside formation is a diamond, single 1/4 tag,
            //     or single 3/4 (generalized single 1/4 tag spots), the pieces
            //     are distributed like the points of a galaxy
            // ... this seems to work fine as is, we don't need to futz with
            //     isWide for this.

            // B1. if the outside call starts and ends in a 2x2 then
            //     lines to lines / columns to columns
            // B2. if it doesn't start in a 2x2 but ends in a 2x2, then
            //     opposite elongation rule applies.
            Formation axisStartF = endF, axisEndF = endS.currentFormation();
            if (which == ConcentricType.CROSS) {
                axisStartF = centerF; axisEndF = centerS.currentFormation();
            }
            if (which != ConcentricType.QUASI &&
                matches(MatcherList._2_X2, axisEndF) /* ends in 2x2 */) {
                if (matches(MatcherList._2_X2, axisStartF)/* starts in 2x2 */) {
                    // XXX really need to evaluate individually for every
                    //     dancer in T-boned formations.
                    Position startP = axisStartF.location
                        (axisStartF.sortedDancers().get(0));
                    Position endP = axisEndF.location
                        (axisEndF.sortedDancers().get(0));
                    // isWide && facing n/s = lines
                    // (!isWide) && facing e/w = lines
                    boolean startLines = NORTHSOUTH.includes(startP.facing) ^
                        !isWide;
                    boolean endLines = NORTHSOUTH.includes(endP.facing) ^
                        !isWide;
                    if (startLines != endLines)
                        isWide = !isWide;
                } else {
                    // opposite elongation rule
                    isWide = !isWide;
                }
            }

            // hard part! Merge the resulting dancer paths.
            // XXX this is largely cut-and-paste from MetaEvaluator; we should
            //     refactor out the common code.

            // go through all the moments in time, constructing an appropriately
            // breathed formation.
            TreeMap<Fraction,Formation> merged =
                new TreeMap<Fraction,Formation>();
            for (Fraction t : moments) {
                boolean isCross = (which==ConcentricType.CROSS &&
                                   t.compareTo(endsOffset) >=0);
                Formation mergeF = merge(centerS.formationAt(t),
                                         endS.formationAt(t),
                                         isWide, isCross);
                merged.put(t, mergeF);
            }
            // okay, now go through the individual dancer paths, adjusting the
            // 'to' and 'from' positions to match breathed.
            for (DanceState nds : l(centerS, endS)) {
                for (Dancer d : nds.dancers()) {
                    Fraction t = Fraction.ZERO;
                    for (DancerPath dp : nds.movements(d)) {
                        Position nfrom = merged.get(t).location(d);
                        t = t.add(dp.time);
                        Position nto = merged.get(t).location(d);
                        DancerPath ndp = dp.translate(nfrom, nto);
                        ds.add(d, ndp);
                    }
                }
            }
            // dancers should all be in sync at this point.
            // no more to evaluate
            return null;
        }
        private static boolean matches(Matcher m, Formation f) {
                try {
                    m.match(f);
                    return true; // yes it matches
                } catch (NoMatchException nme) {
                    return false;
                }
            }
        private static Formation merge(Formation center, Formation end, boolean isWide, boolean isCross) {
            // recurse to deal with wide/cross flags.
            if (isCross)
                return merge(end, center, isWide, !isCross);
            if (!isWide)
                return merge(center.rotate(ExactRotation.ONE_QUARTER),
                             end.rotate(ExactRotation.ONE_QUARTER),
                             !isWide, isCross)
                       .rotate(ExactRotation.mONE_QUARTER);
            // XXX do we need to breathe the center/end formations here?
            return mergeWide(center, end);
        }
        private static Formation mergeWide(Formation center, Formation end) {
            Map<Dancer,Position> location = new HashMap<Dancer,Position>();
            for (Dancer d : center.dancers())
                location.put(d, center.location(d));
            Box centerBounds = center.bounds();
            for (Dancer d : end.dancers()) {
                Position p = end.location(d);
                Fraction nx = p.x, ny = p.y;
                if (p.x.equals(Fraction.ZERO) && p.y.equals(Fraction.ZERO))
                    throw new BadCallException("Outside dancer ends up at origin!");
                if (p.x.compareTo(Fraction.ZERO) == 0) {
                    ny = ny.add((p.y.compareTo(Fraction.ZERO) < 0 ?
                                 centerBounds.ll : centerBounds.ur).y);
                } else {
                    nx = nx.add((p.x.compareTo(Fraction.ZERO) < 0 ?
                                 centerBounds.ll : centerBounds.ur).x);
                }
                location.put(d, p.relocate(nx, ny, p.facing));
            }
            return new Formation(location);
        }
    };
}
