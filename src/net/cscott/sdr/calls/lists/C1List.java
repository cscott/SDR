package net.cscott.sdr.calls.lists;

import static net.cscott.sdr.util.Tools.l;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import net.cscott.sdr.calls.Selector;
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
import net.cscott.sdr.util.Point;

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

    private static class DistortedColumnCall extends C1Call {
        private final Apply inApply, outApply;
        DistortedColumnCall(String name,
                            String transitionIn, String transitionOut) {
            super(name);
            this.inApply = Apply.makeApply(transitionIn);
            this.outApply = Apply.makeApply(transitionOut);
        }
        @Override
        public int getMinNumberOfArguments() { return 1; }
        @Override
        public Rule getRule() { return null; /* internal call */ }
        @Override
        public Evaluator getEvaluator(DanceState ds, List<Expr> args) {
            assert args.size() == 1;
            final Apply subApply = new Apply(args.get(0));
            return new Evaluator() {
                public Evaluator evaluate(DanceState ds) {
                    // 1. do transition in
                    inApply.evaluator(ds).evaluateAll(ds);
                    // 2. do the call
                    subApply.evaluator(ds).evaluateAll(ds);
                    // 3. do the transition out
                    outApply.evaluator(ds).evaluateAll(ds);
                    // XXX smooth the transition (remove first and last steps)
                    return null; // no more to do.
                }
            };
        }
    };

    public static final Call BUTTERFLY =
        new DistortedColumnCall("_butterfly", "_start butterfly",
                                "_finish butterfly");
    public static final Call O =
        new DistortedColumnCall("_o", "_start o", "_finish o");

    public static final Call CONCENTRIC = new C1Call("_concentric") {
        @Override
        public int getMinNumberOfArguments() { return 2; }
        @Override
        public Rule getRule() { return null; /* internal call */ }
        @Override
        public Evaluator getEvaluator(DanceState ds, List<Expr> args)
            throws EvaluationException {
            assert args.size() == 2 || args.size() == 3;
            Selector who  = (args.size()==3) ?
                args.get(2).evaluate(Selector.class, ds) : null;
            return new ConcentricEvaluator(args.get(0), args.get(1), who,
                                           ConcentricType.CONCENTRIC);
        }
    };

    /**
     * Quasi-concentric variant that doesn't breathe ends in; this allows
     * us to do "ends o circulate", for example.
     * @doc.test
     *  js> importPackage(net.cscott.sdr.calls)
     *  js> importPackage(net.cscott.sdr.calls.ast)
     *  js> ds = new DanceState(new DanceProgram(Program.C2),
     *    >                     FormationList.LINES_FACING_OUT.mapStd([]));
     *    > a = new Expr("_o concentric",
     *    >              Expr.literal("nothing"), Expr.literal("press in"));
     *  (Expr _o concentric 'nothing 'press in)
     *  js> C1List.O_CONCENTRIC.getEvaluator(ds, a.args).evaluateAll(ds);
     *    > ds.currentFormation().toStringDiagram("|");
     *  |1B^  2G^
     *  |
     *  |1G^  2B^
     *  |
     *  |4Bv  3Gv
     *  |
     *  |4Gv  3Bv
     */
    public static final Call O_CONCENTRIC = new C1Call("_o concentric") {
        @Override
        public int getMinNumberOfArguments() { return 2; }
        @Override
        public Rule getRule() { return null; /* internal call */ }
        @Override
        public Evaluator getEvaluator(DanceState ds, List<Expr> args)
            throws EvaluationException {
            assert args.size() == 2 || args.size() == 3;
            Selector who  = (args.size()==3) ?
                args.get(2).evaluate(Selector.class, ds) : null;
            return new ConcentricEvaluator(args.get(0), args.get(1), who,
                                           ConcentricType.O);
        }
    };

    /** Variant of 'concentric' to do. */
    public static enum ConcentricType { QUASI, CONCENTRIC, CROSS, O };
    /** Evaluator for concentric and quasi-concentric. */
    public static class ConcentricEvaluator extends Evaluator {
        private final Expr centersPart, endsPart;
        private final ConcentricType which;
        private final Selector who;
        private static final Rotation NORTHSOUTH =
                Rotation.fromAbsoluteString("|");
        private static final boolean DEBUG = false;
        private static final Selector CENTER = new Selector() {
            @Override
            public Set<Dancer> select(TaggedFormation tf) {
                return tf.tagged(Tag.CENTER);
            }
        };
        public ConcentricEvaluator(Expr centersPart, Expr endsPart,
                                   Selector who, ConcentricType which) {
            this.centersPart = centersPart;
            this.endsPart = endsPart;
            this.which = which;
            this.who = (who != null) ? who : CENTER;
        }
        @Override
        public Evaluator evaluate(DanceState ds) {
            // step 1: pull out the centers/ends
            TaggedFormation f = TaggedFormation.coerce(ds.currentFormation());
            Set<Dancer> centerDancers = who.select(f);
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
            if (which != ConcentricType.O)
                // XXX replace with expandO to preserve phantom spots?
                endF = Breather.breathe(endF);
            else
                // breathe out O to ensure there's a 2x2 inside
                endF = expandO(endF, insideBox(endF), centerNominalBox);
            DanceState centerS = ds.cloneAndClear(centerF);
            DanceState endS = ds.cloneAndClear(endF);
            if (DEBUG) {
                System.err.println("CENTER\n"+centerF.toStringDiagram());
                System.err.println("END\n"+endF.toStringDiagram());
            }
            TreeSet<Fraction> moments = new TreeSet<Fraction>();
            new Apply(this.centersPart).evaluator(centerS).evaluateAll(centerS);
            Fraction endTime = centerS.currentTime();
            Fraction lastMovement = centerS.lastMovement();
            for (TimedFormation tf: centerS.formations())
                if (tf.time.compareTo(lastMovement) <= 0)
                    moments.add(tf.time);
            // in CROSS, have the ends wait until the centers are done
            Fraction endsOffset = Fraction.ZERO;
            if (which==ConcentricType.CROSS) {
                endsOffset = endTime;
                endS.syncDancers(endsOffset);
            }
            new Apply(this.endsPart).evaluator(endS).evaluateAll(endS);
            endTime = Fraction.max(endTime, endS.currentTime());
            lastMovement = endS.lastMovement();
            for (TimedFormation tf: endS.formations())
                if (tf.time.compareTo(lastMovement) <= 0)
                    moments.add(tf.time);
            moments.add(endTime);
            for (DanceState nds: l(centerS, endS)) {
                // add back any standing around at the end
                nds.syncDancers(endTime);
            }
            // "Do nothing" movements are divisible; for better breathing,
            // split them up at every 'moment'
            for (DanceState nds: l(centerS, endS))
                for (Fraction time : moments)
                    nds.splitTime(time);

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
                which != ConcentricType.O &&
                matches(MatcherList._2_X2, axisEndF) /* ends in 2x2 */) {
                if (matches(MatcherList._2_X2, axisStartF)/* starts in 2x2 */) {
                    // XXX really need to evaluate individually for every
                    //     dancer in T-boned formations.
                    // should be: Map<Dancer,Boolean> isWide
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
            boolean isO = (which==ConcentricType.O);
            for (Fraction t : moments) {
                boolean isCross = (which==ConcentricType.CROSS &&
                                   t.compareTo(endsOffset) >=0);
                Formation mergeF = merge(centerS.formationAt(t),
                                         endS.formationAt(t),
                                         isWide, isCross, isO);
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
        private static Formation merge(Formation center, Formation end, boolean isWide, boolean isCross, boolean isO) {
            // recurse to deal with wide/cross flags.
            if (isCross)
                return merge(end, center, isWide, !isCross, isO);
            if (!isWide && !isO)
                return merge(center.rotate(ExactRotation.ONE_QUARTER),
                             end.rotate(ExactRotation.ONE_QUARTER),
                             !isWide, isCross, isO)
                       .rotate(ExactRotation.mONE_QUARTER);
            // XXX do we need to breathe the center/end formations here?
            return mergeWide(center, end, isO);
        }
        // nominal side of "centers" in o concentric
        private static final Box centerNominalBox =
            new Box(new Point(Fraction.mTWO, Fraction.mTWO),
                    new Point(Fraction.TWO, Fraction.TWO));
        private static Formation mergeWide(Formation center, Formation end,
                                           boolean isO) {
            Map<Dancer,Position> location = new HashMap<Dancer,Position>();
            for (Dancer d : center.dancers())
                location.put(d, center.location(d));
            Box centerBounds = center.bounds();
            Box endInsideBox = insideBox(end);
            // handle "star like" ends, like: (+/-2,0) (0,+/-2)
            boolean starLike =
                endInsideBox.width().compareTo(Fraction.TWO) >= 0 &&
                endInsideBox.height().compareTo(Fraction.TWO) >= 0;
            if (starLike && !isO) {
                // shrink the center bounds to account for the empty spot
                // in the middle of the end formation
                Point ONE = new Point(Fraction.ONE, Fraction.ONE);
                centerBounds = new Box(centerBounds.ll.add(ONE),
                                       centerBounds.ur.subtract(ONE));
            } else if (isO) {
                centerBounds = centerBounds.union(centerNominalBox);
            }
            for (Dancer d : end.dancers()) {
                Position p = end.location(d);
                Fraction nx = p.x, ny = p.y;
                if (p.x.equals(Fraction.ZERO) && p.y.equals(Fraction.ZERO))
                    throw new BadCallException("Outside dancer ends up at origin!");
                if (isO) {
                    // We nominally work to spots, but consider "do 1/2 of
                    // a bits and pieces" from columns.  In addition, some
                    // of our animations result in intermediate positions
                    // within the center 4 -- for example, the "O" concept
                    // breathes in to an undistorted column, does the call,
                    // then breathes out.
                    // So ensure that outside positions are outside
                    // max(2x2, center bounds).
                    location.put(d, expandO(p, endInsideBox,
                                            centerNominalBox, centerBounds));
                    continue;
                }
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
        /** Move a {@link Position} which is supposed to be outside a
         *  <code>nominal</code> box so that it is outside an
         *  <code>actual</code> box.  If the point was actually inside
         *  the nominal box, first move it to the appropriate border
         *  or the nominal box.  This is used to keep the "centers 2x2"
         *  clear in <code>_o concentric</code>.
         * @doc.test
         *  js> importPackage(net.cscott.sdr.calls);
         *  js> importPackage(net.cscott.sdr.util);
         *  js> expandO = C1List.ConcentricEvaluator.expandO;
         *    > insideBox = C1List.ConcentricEvaluator.insideBox;
         *    > Point = Point["(int,int)"]; // help resolve overloading
         *    > undefined
         *  js> // boundary of nominal 2x2
         *  js> n = new Box(new Point(-2,-2), new Point(2,2));
         *  (-2,-2;2,2)
         *  js> // actual boundary (for example, because the centers
         *  js> // did a peel and trail)
         *  js> a = new Box(new Point(-4,-1), new Point(4,1))
         *  (-4,-1;4,1)
         *  js> // normal o spots
         *  js> f = Formation.SQUARED_SET;
         *    > ib = insideBox(f);
         *  (-2,-2;2,2)
         *  js> for each (d in StandardDancer.values()) {
         *    >   f = f.move(d, expandO(f.location(d), ib, n, a.union(n)));
         *    > }
         *    > f.toStringDiagram('|');
         *  |          3Gv  3Bv
         *  |
         *  |4B>                      2G<
         *  |
         *  |4G>                      2B<
         *  |
         *  |          1B^  1G^
         *  js> // thar spots (clear out centers)
         *  js> f = FormationList.THAR.mapStd([]);
         *    > ib = insideBox(f);
         *  (0,0;0,0)
         *  js> for each (d in StandardDancer.values()) {
         *    >   f = f.move(d, expandO(f.location(d), ib, n, a.union(n)));
         *    > }
         *    > f.toStringDiagram('|');
         *  |                 1B<
         *  |
         *  |                 1G>
         *  |
         *  |
         *  |2Bv  2G^                      4Gv  4B^
         *  |
         *  |
         *  |                 3G<
         *  |
         *  |                 3B>
         *  js> f.location(StandardDancer.COUPLE_1_GIRL);
         *  0,3,e
         *  js> f.location(StandardDancer.COUPLE_4_GIRL);
         *  5,0,s
         */
        public static Position expandO(Position p,
                                       Box inside, Box nominal, Box actual) {
            Point pt = expandO(p.toPoint(),
                               inside, nominal, actual);
            return p.relocate(pt.x, pt.y, p.facing);
        }
        public static Formation expandO(Formation f, Box nominal, Box actual) {
            Map<Dancer,Position> m = new HashMap<Dancer,Position>
                (f.dancers().size());
            Box inside = insideBox(f);
            for (Dancer d : f.dancers())
                m.put(d, expandO(f.location(d), inside, nominal, actual));
            return new Formation(m);
        }
        private static Point expandO(Point p,
                                     Box inside, Box nominal, Box actual) {
            // flip so we only have to deal with positive x
            if (p.x.compareTo(Fraction.ZERO) < 0) {
                p = expandO(new Point(p.x.negate(), p.y), inside.mirrorX(),
                            nominal.mirrorX(), actual.mirrorX());
                return new Point(p.x.negate(), p.y);
            }
            // flip so we only have to deal with positive y
            if (p.y.compareTo(Fraction.ZERO) < 0) {
                p = expandO(new Point(p.x, p.y.negate()), inside.mirrorX(),
                            nominal.mirrorY(), actual.mirrorY());
                return new Point(p.x, p.y.negate());
            }
            return expandO(p, inside.ur, nominal.ur, actual.ur);
        }
        private static Point expandO(Point p,
                                     Point inside, Point nominal, Point actual){
            // decide whether we will expand (if necessary) in x or y or both
            int c = p.x.subtract(inside.x).compareTo(p.y.subtract(inside.y));
            boolean expandX = c >= 0;
            boolean expandY = c <= 0;
            Fraction nx = p.x, ny = p.y;
            // offset bounds to account for (-1,-1;1,1) bounds of this dancer
            Point ONE = new Point(1,1);
            inside = inside.add(ONE);
            nominal = nominal.add(ONE);
            actual = actual.add(ONE);
            // expand inside to edge of nominal box if necessary
            if (expandX && inside.x.compareTo(nominal.x) < 0)
                nx = nx.add(nominal.x.subtract(inside.x));
            if (expandY && inside.y.compareTo(nominal.y) < 0)
                ny = ny.add(nominal.y.subtract(inside.y));
            // adjust nominal box to match actual
            if (expandX) nx = nx.add(actual.x.subtract(nominal.x));
            if (expandY) ny = ny.add(actual.y.subtract(nominal.y));
            return new Point(nx, ny);
        }
        /** Compute the amount of space "inside" of a given formation,
         *  centered on the origin.
         * @doc.test
         *  js> importPackage(net.cscott.sdr.calls);
         *  js> importPackage(net.cscott.sdr.util);
         *  js> C1List.ConcentricEvaluator.insideBox(Formation.SQUARED_SET);
         *  (-2,-2;2,2)
         *  js> f = FormationList.RH_BOX.mapStd([]);
         *    > for (d in Iterator(f.dancers())) {
         *    >   f = f.move(d, f.location(d).forwardStep(Fraction.TWO, false)
         *    >                              .sideStep(Fraction.mTWO, false));
         *    > }
         *    > f.toStringDiagram("|");
         *  |1B^
         *  |
         *  |3G^
         *  |
         *  |               1Gv
         *  |
         *  |               3Bv
         *  js> C1List.ConcentricEvaluator.insideBox(f);
         *  (-2,-4;2,4)
         */
        public static Box insideBox(Formation f) {
            // sort by distance from the origin, so important constraints
            // are considered first
            List<Box> sorted = new ArrayList<Box>(f.dancers().size());
            for (Dancer d : f.sortedDancers())
                sorted.add(f.bounds(d));
            Collections.sort(sorted, new Comparator<Box>() {
                @Override
                public int compare(Box a, Box b) {
                    return a.center().dist2(Point.ZERO)
                        .compareTo(b.center().dist2(Point.ZERO));
                }
            });
            return trimInside(sorted, 0, f.bounds(), null);
        }
        private static Box trimInside(List<Box> dancers, int which,
                                      Box inside, Box best) {
            if (best != null && inside.area().compareTo(best.area()) <= 0)
                return best; // we can't find anything better
            if (which >= dancers.size() || inside.ll.equals(inside.ur))
                return inside; // we're done
            Box b = dancers.get(which);
            if (!b.overlapsExcl(inside))
                // nothing to trim here, look at next dancer
                return trimInside(dancers, which+1, inside, best);
            List<Box> trims = new ArrayList<Box>(4);
            // try trimming X
            if (b.ur.x.compareTo(inside.ur.x) <= 0)
                trims.add(new Box(new Point(b.ur.x, inside.ll.y), inside.ur));
            if (b.ll.x.compareTo(inside.ll.x) >= 0)
                trims.add(new Box(inside.ll, new Point(b.ll.x, inside.ur.y)));
            // try trimming y
            if (b.ur.y.compareTo(inside.ur.y) <= 0)
                trims.add(new Box(new Point(inside.ll.x, b.ur.y), inside.ur));
            if (b.ll.y.compareTo(inside.ll.y) >= 0)
                trims.add(new Box(inside.ll, new Point(inside.ur.x, b.ll.y)));
            // try largest area first
            Collections.sort(trims, new Comparator<Box>() {
                @Override
                public int compare(Box a, Box b) {
                    return -(a.area().compareTo(b.area()));
                }
            });
            for (Box candidate : trims) {
                if (candidate.includes(Point.ZERO))
                    best = trimInside(dancers, which+1, candidate, best);
            }
            return best;
        }
    };
}
