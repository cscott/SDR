package net.cscott.sdr.calls.lists;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.cscott.sdr.calls.BadCallException;
import net.cscott.sdr.calls.Call;
import net.cscott.sdr.calls.DanceState;
import net.cscott.sdr.calls.Dancer;
import net.cscott.sdr.calls.DancerPath;
import net.cscott.sdr.calls.Evaluator;
import net.cscott.sdr.calls.Formation;
import net.cscott.sdr.calls.Position;
import net.cscott.sdr.calls.Program;
import net.cscott.sdr.calls.ast.Expr;
import net.cscott.sdr.calls.grm.Grm;
import net.cscott.sdr.calls.grm.Rule;
import net.cscott.sdr.calls.transform.AllButLastPart;
import net.cscott.sdr.calls.transform.Finish.PartSelectorCall;
import net.cscott.sdr.calls.transform.FirstPart;
import net.cscott.sdr.calls.transform.LikeA;
import net.cscott.sdr.util.Fraction;

import org.junit.runner.RunWith;

import net.cscott.jdoctest.JDoctestRunner;

/**
 * The <code>C4List</code> class contains complex call
 * and concept definitions which are on the 'C4' program.
 * Note that "simple" calls and concepts are defined in
 * the resource file at
 * <a href="doc-files/c4.calls"><code>net/cscott/sdr/calls/lists/c4.calls</code></a>;
 * this class contains only those definitions for which an
 * executable component is required.
 * @author C. Scott Ananian
 */
@RunWith(value=JDoctestRunner.class)
public abstract class C4List {
    // hide constructor.
    private C4List() { }

    private static abstract class C4Call extends Call {
        private final String name;
        C4Call(String name) { this.name = name; }
        @Override
        public final String getName() { return name; }
        @Override
        public final Program getProgram() { return Program.C4; }
        @Override
        public List<Expr> getDefaultArguments() {
            return Collections.emptyList();
        }
    }

    /**
     * Slim Down is defined from arbitrary formations, as long as
     * each quadrant has one center and one end that are as if in
     * a 2x4.
     *
     * @doc.test Application from a non 2x4.
     *  js> importPackage(net.cscott.sdr.calls);
     *  js> importPackage(net.cscott.sdr.calls.ast);
     *  js> const SD = StandardDancer;
     *  js> f1 = FormationList.EIGHT_CHAIN_THRU.mapStd([SD.COUPLE_4_BOY, SD.COUPLE_1_GIRL, SD.COUPLE_4_GIRL, SD.COUPLE_1_BOY]); f1.toStringDiagram("|")
     *  |4Bv  1Gv
     *  |
     *  |4G^  1B^
     *  |
     *  |3Bv  2Gv
     *  |
     *  |3G^  2B^
     *  js> f2 = FormationList.LINES_FACING_OUT.mapStd([SD.COUPLE_4_BOY, SD.COUPLE_4_GIRL, SD.COUPLE_1_BOY, SD.COUPLE_1_GIRL]) ; f2.toStringDiagram("|")
     *  |4B^  4G^  1B^  1G^
     *  |
     *  |3Gv  3Bv  2Gv  2Bv
     *  js> // merge these formations
     *  js> f3 = f2; [SD.COUPLE_1_BOY, SD.COUPLE_1_GIRL, SD.COUPLE_3_BOY, SD.COUPLE_3_GIRL].forEach(function(d) { f3 = f3.move(d, f1.location(d)); });
     *  js> f3.toStringDiagram("|")
     *  |          1Gv
     *  |
     *  |4B^  4G^  1B^
     *  |
     *  |     3Bv  2Gv  2Bv
     *  |
     *  |     3G^
     *  js> ds = new DanceState(new DanceProgram(Program.C4), f3); undefined;
     *  js> Evaluator.parseAndEval(ds, "slim down")
     *  js> // check result and confirm there is no roll or sweep
     *  js> ds.currentFormation().toStringDiagramWithDetails("|")
     *  |     4G^                          ^
     *  |
     *  |     4B^  1Gv  1B^                ^    v    ^
     *  |
     *  |3Bv  3G^  2Bv                v    ^    v
     *  |
     *  |          2Gv                          v
     *  js> // check sashay flags
     *  js> ds.movements(SD.COUPLE_1_BOY)
     *  [DancerPath[from=1,1,n,to=3,1,n,time=2,pointOfRotation=<null>,flags=[SASHAY_START, SASHAY_FINISH]]]
     *  js> ds.movements(SD.COUPLE_1_GIRL)
     *  [DancerPath[from=1,3,s,to=1,1,s,time=2,pointOfRotation=<null>]]
     *  js> // quickly check a more typical example
     *  js> ds = new DanceState(new DanceProgram(Program.C4), FormationList.PARALLEL_RH_WAVES); undefined
     *  js> ds.currentFormation().toStringDiagram("|")
     *  |^    v    ^    v
     *  |
     *  |^    v    ^    v
     *  js> Evaluator.parseAndEval(ds, "slim down")
     *  js> ds.currentFormation().toStringDiagram("|")
     *  |v    ^
     *  |
     *  |^    v
     *  |
     *  |^    v
     *  |
     *  |v    ^
     */
    public static final Call SLIM_DOWN = new C4Call("slim down") {
        @Override
        public int getMinNumberOfArguments() { return 0; }
        @Override
        public Rule getRule() {
            Grm g = Grm.parse(getName());
            return new Rule("anything", g, Fraction.valueOf(0));
        }
        @Override
        public Evaluator getEvaluator(DanceState ds, List<Expr> args)
            throws EvaluationException {
            return new Evaluator() {
                /** How many beats for a slim down? */
                private final Fraction DURATION = Fraction.TWO;
                // helpers
                private final List<DancerPath.Flag> noSashayFlags =
                        Collections.emptyList();
                private final List<DancerPath.Flag> sashayFlags = Arrays.asList
                        (DancerPath.Flag.SASHAY_START,
                         DancerPath.Flag.SASHAY_FINISH);
                @Override
                public Evaluator evaluate(DanceState ds) {
                    Formation f = ds.currentFormation();
                    for (int x = -1; x <= 1; x+=2) {
                        for (int y = -1; y <= 1; y+=2) {
                            Dancer center = findDancerAt(f, x, y);
                            if (center==null)
                                throw new BadCallException("No center dancer.");
                            Position oldCenterPos = f.location(center);
                            Dancer end1 = findDancerAt(f, x*3, y);
                            Dancer end2 = findDancerAt(f, x, y*3);
                            Dancer end;
                            int newCenterX, newCenterY;
                            int newEndX=x, newEndY=y;
                            if (end1!=null) {
                                if (end2!=null)
                                    throw new BadCallException
                                        ("Too many end dancers.");
                                end = end1;
                                newCenterX = x; newCenterY = y*3;
                            } else if (end2!=null) {
                                end = end2;
                                newCenterX = x*3; newCenterY = y;
                            } else
                                throw new BadCallException("No end dancer.");
                            Position oldEndPos = f.location(end);
                            Position newCenterPos = new Position
                                  (newCenterX, newCenterY, oldCenterPos.facing);
                            Position newEndPos = new Position
                                  (newEndX, newEndY, oldEndPos.facing);
                            DancerPath dpCenter = new DancerPath
                                    (oldCenterPos, newCenterPos, DURATION, null,
                                     isSashay(oldCenterPos, newCenterPos) ?
                                     sashayFlags : noSashayFlags);
                            DancerPath dpEnd = new DancerPath
                                    (oldEndPos, newEndPos, DURATION, null,
                                     isSashay(oldEndPos, newEndPos) ?
                                     sashayFlags : noSashayFlags);
                            ds.add(center, dpCenter);
                            ds.add(end, dpEnd);
                        }
                    }
                    return null;
                }
                private boolean isSashay(Position from, Position to) {
                    Position f1 = from.sideStep(Fraction.TWO, false);
                    Position f2 = from.sideStep(Fraction.mTWO, false);
                    return (f1.equals(to) || f2.equals(to));
                }
                private Dancer findDancerAt(Formation f, int x, int y) {
                    Fraction fx = Fraction.valueOf(x);
                    Fraction fy = Fraction.valueOf(y);
                    for (Dancer d : f.dancers()) {
                        Position p = f.location(d);
                        if (p.x.compareTo(fx)==0 &&
                            p.y.compareTo(fy)==0)
                            return d;
                    }
                    return null; // dancer not found at this location
                }
            };
        };
    };

    public static final Call LIKE_A = new PartSelectorCall
            ("like a", Program.C4, "like (a|an)? <0=anything>") {
        @Override
        protected LikeA getPartsVisitor(DanceState ds) {
            return new LikeA(ds);
        }
    };
    public static final Call _FIRST_PART = new PartSelectorCall
            ("_first part", Program.C4, null /* not on any list */) {
        @Override
        protected FirstPart getPartsVisitor(DanceState ds) {
            return new FirstPart(ds);
        }
    };
    public static final Call _ALL_BUT_LAST_PART = new PartSelectorCall
            ("_all but last part", Program.C4, null /* not on any list */) {
        @Override
        protected AllButLastPart getPartsVisitor(DanceState ds) {
            return new AllButLastPart(ds);
        }
    };
}
