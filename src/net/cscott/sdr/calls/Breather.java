package net.cscott.sdr.calls;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.cscott.jdoctest.JDoctestRunner;
import net.cscott.sdr.util.Box;
import net.cscott.sdr.util.Fraction;
import net.cscott.sdr.util.Point;
import net.cscott.sdr.util.SdrToString;
import net.cscott.sdr.util.Tools.ListMultiMap;
import net.cscott.sdr.util.Tools.F; // list comprehension helper
import static net.cscott.sdr.util.Tools.foreach; // list comprehension
import static net.cscott.sdr.util.Tools.l;//list constructor
import static net.cscott.sdr.util.Tools.m;//map constructor
import static net.cscott.sdr.util.Tools.mml;//listmultimap constructor
import static net.cscott.sdr.util.Tools.p;//pair constructor

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.junit.runner.RunWith;

import EDU.Washington.grad.gjb.cassowary.CL;
import EDU.Washington.grad.gjb.cassowary.ClBooleanVariable;
import EDU.Washington.grad.gjb.cassowary.ClBranchAndBound;
import EDU.Washington.grad.gjb.cassowary.ClConstraint;
import EDU.Washington.grad.gjb.cassowary.ClLinearEquation;
import EDU.Washington.grad.gjb.cassowary.ClLinearExpression;
import EDU.Washington.grad.gjb.cassowary.ClLinearInequality;
import EDU.Washington.grad.gjb.cassowary.ClSimplexSolver;
import EDU.Washington.grad.gjb.cassowary.ClStrength;
import EDU.Washington.grad.gjb.cassowary.ClVariable;
import EDU.Washington.grad.gjb.cassowary.ExCLError;
import EDU.Washington.grad.gjb.cassowary.ExCLInternalError;
import EDU.Washington.grad.gjb.cassowary.ExCLNonlinearExpression;
import EDU.Washington.grad.gjb.cassowary.ExCLRequiredFailure;

/**
 * The {@link Breather} class contains methods to reassemble and
 * breathe formations.
 *
 * <p>The {@link #insert(Formation,Map) insert()} method pushes
 * sub-formations into a meta-formation after performing (say) a four
 * person call &mdash; ie, starting with a tidal wave, {@link
 * Selector} will pull out two four-person waves as a mini-wave as the
 * meta-formation.  We do a crossfire (say) from the mini-waves to get
 * boxes.  Now {@link #insert(Formation,Map)} will shove the boxes
 * into the mini-wave meta-formation to get parallel ocean waves.</p>
 *
 * <p>The {@link #breathe(List) breathe()} method is a part of {@link
 * #insert(Formation,Map) insert()} which is useful in its own right:
 * it takes a {@link Formation} (or a list of {@link FormationPiece}s)
 * and breathes it in or out to normalize the spacing between dancers.
 * For example, after "trailers extend" from boxes, we need to make
 * room for the resulting mini-wave in the center.  If the ends then
 * u-turn back and everyone extends again, the formation has to
 * squeeze in again to erase the space.</p>
 *
 * <h3>Theory of breathing</h3>
 * <p>First: identify collisions.  Collided dancers are
 * inserted into a miniwave which replaces them in the remainder of the
 * algorithm.  Second: resolve overlaps.  Dancers which overlap have their
 * boundaries adjusted so that they share a boundary, ideally at the midpoint
 * of the overlap.  We now use a mixed integer programming solver to perform
 * an optimal adjustment, keeping handholds and making stars when possible.
 * Third: Sort and order the boundary coordinates, and then allocate space
 * between boundaries so that it is "just enough" to fit the dancers between
 * them.  If a dancer spans multiple boundary points, their allocation is
 * divided equally between them.  We use linear programming here to find
 * an optimal expansion.  Finally, the output formations are
 * relocated so that they are centered between their new boundaries.
 *
 * @author C. Scott Ananian
 * @version $Id: Breather.java,v 1.10 2006-10-30 22:09:29 cananian Exp $
 */
@RunWith(value=JDoctestRunner.class)
public class Breather {
    private Breather() { }

    /**
     * Insert formations into a meta-formation.  This reassembles the
     * formation after we've decomposed it into (say) boxes to do a
     * four-person call.
     *
     * @doc.test Insert COUPLEs, then TANDEMs into a RH_OCEAN_WAVE.  Then, for
     *  a challenge, insert TANDEMs into a DIAMOND to give a t-bone column:
     *  js> FormationList = FormationListJS.initJS(this); undefined;
     *  js> function xofy(meta, f) {
     *    >   var i=0
     *    >   var m=new java.util.LinkedHashMap()
     *    >   for (d in Iterator(meta.sortedDancers())) {
     *    >     var mm=new java.util.LinkedHashMap()
     *    >     for (dd in Iterator(f.sortedDancers())) {
     *    >       mm.put(dd, StandardDancer.values()[i++])
     *    >     }
     *    >     m.put(d, f.map(mm))
     *    >     print(m.get(d).toStringDiagram())
     *    >   }
     *    >   return m
     *    > }
     *  js> meta = FormationList.RH_OCEAN_WAVE ; meta.toStringDiagram()
     *  ^    v    ^    v
     *  js> m = xofy(meta, FormationList.COUPLE); undefined
     *  1B^  1G^
     *  2B^  2G^
     *  3B^  3G^
     *  4B^  4G^
     *  js> Breather.insert(meta, m).toStringDiagram()
     *  1B^  1G^  2Gv  2Bv  3B^  3G^  4Gv  4Bv
     *  js> m = xofy(meta, FormationList.TANDEM); undefined
     *  1B^
     *  
     *  1G^
     *  2B^
     *  
     *  2G^
     *  3B^
     *  
     *  3G^
     *  4B^
     *  
     *  4G^
     *  js> Breather.insert(meta, m).toStringDiagram()
     *  1B^  2Gv  3B^  4Gv
     *  
     *  1G^  2Bv  3G^  4Bv
     *  js> meta = FormationList.RH_DIAMOND ; meta.toStringDiagram("|", Formation.dancerNames)
     *  |  >
     *  |
     *  |
     *  |^    v
     *  |
     *  |
     *  |  <
     *  js> m = xofy(meta, FormationList.TANDEM); undefined
     *  1B^
     *  
     *  1G^
     *  2B^
     *  
     *  2G^
     *  3B^
     *  
     *  3G^
     *  4B^
     *  
     *  4G^
     *  js> Breather.insert(meta, m).toStringDiagram()
     *  1G>  1B>
     *  
     *  2B^  3Gv
     *  
     *  2G^  3Bv
     *  
     *  4B<  4G<
     */
    public static Formation insert(final Formation meta,
                                   final Map<Dancer,Formation> components) {
        List<FormationPiece> l =
            new ArrayList<FormationPiece>(meta.dancers().size());
        for (Dancer d : meta.dancers()) {
            assert meta.location(d).facing.isExact();
            l.add(new FormationPiece(meta.select(d).onlySelected(),
                                     components.get(d),
                                     (ExactRotation) meta.location(d).facing));
        }
        return breathe(l);
    }

    /*-----------------------------------------------------------------------*/

    public static class FormationPiece {
        /** Input formation piece. The original formation is a simple
         * superposition of these. */
        public final Formation input;
        /** The formation which will correspond to {@link #input} in the output
         * (meta) formation.  This might be a formation of a single
         * {@link PhantomDancer Phantom}, for example.
         * @see FormationList#SINGLE_DANCER
         */
        public final Formation output;
        /**
         * Prepare an argument to the {@link #breathe} method.
         * @param input  Input formation piece.
         * @param output Output formation piece.
         * @param r
         * The rotation to use for the output formation in the eventual
         * result. Typically this is the rotation of formation {@link #input}
         * from whatever the 'canonical' orientation of {@link #output} is.
         * For example, if we are mapping single dancers to single dancers,
         * then {@link #input} is the rotated offset result of
         * {@link Formation#onlySelected()}, {@link #output} is
         * {@link FormationList#SINGLE_DANCER} (which is facing north), and
         * the rotation {@code imr} matches the rotation of the dancer in
         * {@link #input}.
         */
        public FormationPiece(Formation input, Formation output, ExactRotation r) {
            this(input, output.rotate(r));
        }
        public FormationPiece(Formation input, Formation output) {
            this.input = input;
            this.output = output;
        }
        @Override
        public String toString() {
            return new ToStringBuilder(this, SdrToString.STYLE)
            .append("input", input)
            .append("output", output)
            .toString();
        }
    }
    /**
     * Create a canonical formation by compressing the given one.  This
     * is just an invokation of {@link #breathe(List)} with
     * trivial {@link FormationPiece}s consisting of a single dancer each.
     *
     * @doc.test From couples back to back, step out; then breathe in:
     *  js> importPackage(net.cscott.sdr.util) // for Fraction
     *  js> FormationList = FormationListJS.initJS(this); undefined;
     *  js> f = FormationList.BACK_TO_BACK_COUPLES ; f.toStringDiagram()
     *  ^    ^
     *  
     *  v    v
     *  js> for (d in Iterator(f.dancers())) {
     *    >   f=f.move(d,f.location(d).forwardStep(Fraction.ONE, false));
     *    > }; f.toStringDiagram()
     *  ^    ^
     *  
     *  
     *  
     *  v    v
     *  js> Breather.breathe(f).toStringDiagram()
     *  ^    ^
     *  
     *  v    v
     * @doc.test From facing couples, take half a step in; breathe out:
     *  js> importPackage(net.cscott.sdr.util) // for Fraction
     *  js> FormationList = FormationListJS.initJS(this); undefined;
     *  js> f = FormationList.FACING_COUPLES ; f.toStringDiagram()
     *  v    v
     *  
     *  ^    ^
     *  js> for (d in Iterator(f.dancers())) {
     *    >   f=f.move(d,f.location(d).forwardStep(Fraction.ONE_HALF, false));
     *    > }; f.toStringDiagram()
     *  v    v
     *  ^    ^
     *  js> Breather.breathe(f).toStringDiagram()
     *  v    v
     *  
     *  ^    ^
     * @doc.test From single three quarter tag, step out; then breathe in:
     *  js> importPackage(net.cscott.sdr.util) // for Fraction
     *  js> FormationList = FormationListJS.initJS(this); undefined;
     *  js> f = FormationList.RH_SINGLE_THREE_QUARTER_TAG ; f.toStringDiagram()
     *    ^
     *  
     *  ^    v
     *  
     *    v
     *  js> for (d in Iterator(f.tagged(TaggedFormation.Tag.END))) {
     *    >   f=f.move(d,f.location(d).forwardStep(Fraction.ONE, false));
     *    > }; f.toStringDiagram()
     *    ^
     *  
     *  
     *  ^    v
     *  
     *  
     *    v
     *  js> Breather.breathe(f).toStringDiagram()
     *    ^
     *  
     *  ^    v
     *  
     *    v
     * @doc.test From single quarter tag, take half a step in; breathe out:
     *  js> importPackage(net.cscott.sdr.util) // for Fraction
     *  js> FormationList = FormationListJS.initJS(this); undefined;
     *  js> f = FormationList.RH_SINGLE_QUARTER_TAG ; f.toStringDiagram()
     *    v
     *  
     *  ^    v
     *  
     *    ^
     *  js> for (d in Iterator(f.tagged(TaggedFormation.Tag.END))) {
     *    >   f=f.move(d,f.location(d).forwardStep(Fraction.ONE, false));
     *    > }; f.toStringDiagram()
     *    v
     *  ^    v
     *    ^
     *  js> Breather.breathe(f).toStringDiagram()
     *    v
     *  
     *  ^    v
     *  
     *    ^
     * @doc.test From right-hand diamond, take a step and a half in; breathe out:
     *  js> importPackage(net.cscott.sdr.util) // for Fraction
     *  js> FormationList = FormationListJS.initJS(this); undefined;
     *  js> f = FormationList.RH_DIAMOND ; f.toStringDiagram("|", Formation.dancerNames)
     *  |  >
     *  |
     *  |
     *  |^    v
     *  |
     *  |
     *  |  <
     *  js> // XXX have to change this amount if we shrink diamonds
     *  js> for (d in Iterator(f.tagged(TaggedFormation.Tag.POINT))) {
     *    >   f=f.move(d,f.location(d).sideStep(Fraction.valueOf(3,2), true));
     *    > }; f.toStringDiagram("|", Formation.dancerNames)
     *  |  >
     *  |
     *  |^    v
     *  |  <
     *  js> Breather.breathe(f).toStringDiagram("|", Formation.dancerNames)
     *  |  >
     *  |
     *  |^    v
     *  |
     *  |  <
     * @doc.test From right-hand diamond, take 2 1/2 steps in; breathe out to stars:
     *  js> importPackage(net.cscott.sdr.util) // for Fraction
     *  js> FormationList = FormationListJS.initJS(this); undefined;
     *  js> f = FormationList.RH_DIAMOND ; f.toStringDiagram("|", Formation.dancerNames)
     *  |  >
     *  |
     *  |
     *  |^    v
     *  |
     *  |
     *  |  <
     *  js> // XXX have to change this amount if we shrink diamonds
     *  js> for (d in Iterator(f.tagged(TaggedFormation.Tag.POINT))) {
     *    >   f=f.move(d,f.location(d).sideStep(Fraction.valueOf(5,2), true));
     *    > }; f.toStringDiagram("|", Formation.dancerNames)
     *  |  >
     *  |^ <  v
     *  js> Breather.breathe(f).toStringDiagram("|", Formation.dancerNames)
     *  |  >
     *  |^    v
     *  |  <
     * @doc.test Facing dancers step forward; resolve collision.
     *  js> importPackage(net.cscott.sdr.util) // for Fraction
     *  js> FormationList = FormationListJS.initJS(this); undefined;
     *  js> f = FormationList.FACING_DANCERS ; f.toStringDiagram()
     *  v
     *  
     *  ^
     *  js> for (d in Iterator(f.dancers())) {
     *    >   f=f.move(d,f.location(d).forwardStep(Fraction.ONE, false));
     *    > }; f
     *  net.cscott.sdr.calls.TaggedFormation[
     *    location={<phantom@7f>=0,0,n, <phantom@7e>=0,0,s}
     *    selected=[<phantom@7f>, <phantom@7e>]
     *    tags={<phantom@7f>=TRAILER, <phantom@7e>=TRAILER}
     *  ]
     *  js> Breather.breathe(f).toStringDiagram()
     *  ^    v
     * @doc.test Facing couples step forward with a left-shoulder pass;
     *  resolve collision and breathe.
     *  js> importPackage(net.cscott.sdr.util) // for Fraction
     *  js> FormationList = FormationListJS.initJS(this); undefined;
     *  js> f = FormationList.FACING_COUPLES ; f.toStringDiagram()
     *  v    v
     *  
     *  ^    ^
     *  js> for (d in Iterator(f.dancers())) {
     *    >   f=f.move(d,f.location(d).forwardStep(Fraction.ONE, false).addFlags(Position.Flag.PASS_LEFT));
     *    > }; undefined
     *  js> Breather.breathe(f).toStringDiagram()
     *  v    ^    v    ^
     * @doc.test From EvalPrim test:
     *  js> importPackage(net.cscott.sdr.util) // for Fraction
     *  js> m = new java.util.HashMap(); undefined
     *  js> function di(p) { m.put(new PhantomDancer(), p); }
     *  js> di(new Position(Fraction.mONE, Fraction.ONE, ExactRotation.NORTH));
     *  js> di(new Position(Fraction.mONE, Fraction.ZERO, ExactRotation.NORTH));
     *  js> di(new Position(Fraction.ONE, Fraction.ZERO, ExactRotation.SOUTH));
     *  js> di(new Position(Fraction.ONE, Fraction.mONE, ExactRotation.SOUTH));
     *  js> f = new Formation(m); f.toStringDiagram();
     *  ^
     *  ^    v
     *       v
     *  js> [f.location(d) for (d in Iterator(f.sortedDancers()))].join('  ')
     *  -1,1,n  -1,0,n  1,0,s  1,-1,s
     *  js> ff = Breather.breathe(f); ff.toStringDiagram()
     *  ^
     *
     *  ^    v
     *
     *       v
     *  js> [ff.location(d) for (d in Iterator(ff.sortedDancers()))].join('  ')
     *  -1,2,n  -1,0,n  1,0,s  1,-2,s
     * @doc.test From 'do half of an ends cross run':
     *  js> importPackage(net.cscott.sdr.util) // for Fraction
     *  js> m = new java.util.HashMap(); undefined
     *  js> SD = StandardDancer; undefined
     *  js> m.put(SD.COUPLE_4_GIRL, new Position(Fraction.valueOf(-5,2), Fraction.ONE, ExactRotation.WEST)); undefined
     *  js> m.put(SD.COUPLE_1_GIRL, new Position(Fraction.valueOf(3,2), Fraction.ONE, ExactRotation.WEST)); undefined
     *  js> m.put(SD.COUPLE_4_BOY, new Position(Fraction.valueOf(-3), Fraction.ZERO, ExactRotation.NORTH, Position.Flag.ROLL_RIGHT)); undefined
     *  js> m.put(SD.COUPLE_3_BOY, new Position(Fraction.mONE, Fraction.ZERO, ExactRotation.SOUTH, Position.Flag.ROLL_RIGHT)); undefined
     *  js> m.put(SD.COUPLE_1_BOY, new Position(Fraction.ONE, Fraction.ZERO, ExactRotation.NORTH, Position.Flag.ROLL_RIGHT)); undefined
     *  js> m.put(SD.COUPLE_2_BOY, new Position(Fraction.valueOf(3), Fraction.ZERO, ExactRotation.SOUTH, Position.Flag.ROLL_RIGHT)); undefined
     *  js> m.put(SD.COUPLE_3_GIRL, new Position(Fraction.valueOf(-3,2), Fraction.mONE, ExactRotation.EAST)); undefined
     *  js> m.put(SD.COUPLE_2_GIRL, new Position(Fraction.valueOf(5,2), Fraction.mONE, ExactRotation.EAST)); undefined
     *  js> f = new Formation(m); f.toStringDiagram();
     *   4G<       1G<
     *  4B^  3Bv  1B^  2Bv
     *     3G>       2G>
     *  js> [f.location(d) for (d in Iterator(f.sortedDancers()))].join('  ')
     *  -2 1/2,1,w  1 1/2,1,w  -3,0,n,[ROLL_RIGHT]  -1,0,s,[ROLL_RIGHT]  1,0,n,[ROLL_RIGHT]  3,0,s,[ROLL_RIGHT]  -1 1/2,-1,e  2 1/2,-1,e
     *  js> ff = Breather.breathe(f); ff.toStringDiagram()
     *   4G<       1G<
     *
     *  4B^  3Bv  1B^  2Bv
     *
     *     3G>       2G>
     *  js> [ff.location(d) for (d in Iterator(ff.sortedDancers()))].join('  ')
     *  -2 1/3,2,w  1 2/3,2,w  -3,0,n,[ROLL_RIGHT]  -1,0,s,[ROLL_RIGHT]  1,0,n,[ROLL_RIGHT]  3,0,s,[ROLL_RIGHT]  -1 2/3,-2,e  2 1/3,-2,e
     */
    public static Formation breathe(Formation f) {
        List<FormationPiece> fpl = new ArrayList<FormationPiece>
            (f.dancers().size());
        for (Dancer d: f.dancers()) {
            Formation in = f.select(Collections.singleton(d)).onlySelected();
            Position p = in.location(d);
            p = p.relocate(Fraction.ZERO, Fraction.ZERO, p.facing);
            Formation out = new Formation(m(p(d, p)));
            fpl.add(new FormationPiece(in, out));
        }
        return breathe(fpl);
    }
    /**
     * Take a set of input formation pieces and substitute the
     * given output formation pieces for them, breathing the result
     * together so that the formation is compact.  (The map giving the
     * correspondence between dancers in
     * the new formation and the input formations is given by the
     * individual {@link FormationPiece} objects.)  We also resolve
     * collisions to right or left hands, depending on whether the
     * pass-left flag is set for the {@link Position}s involved.
     * @doc.test Triangle point breathes to center of the base:
     *  js> importPackage(net.cscott.sdr.util)
     *  js> FormationList = FormationListJS.initJS(this); undefined;
     *  js> f2 = new Formation(Tools.m(
     *    >         Tools.p(StandardDancer.COUPLE_2_BOY, Position.getGrid(1,-1,"n")),
     *    >         Tools.p(StandardDancer.COUPLE_2_GIRL, Position.getGrid(3,-1,"n"))))
     *  net.cscott.sdr.calls.Formation[
     *    location={COUPLE 2 BOY=1,-1,n, COUPLE 2 GIRL=3,-1,n}
     *    selected=[COUPLE 2 BOY, COUPLE 2 GIRL]
     *  ]
     *  js> f2.toStringDiagram()
     *   2B^  2G^
     *  js> fp2 = new Breather.FormationPiece(f2, FormationList.RH_MINIWAVE); undefined
     *  js> // point on far side
     *  js> f1 = new Formation(Tools.m(
     *    >         Tools.p(StandardDancer.COUPLE_1_BOY, Position.getGrid(3,1,"e"))))
     *  net.cscott.sdr.calls.Formation[
     *    location={COUPLE 1 BOY=3,1,e}
     *    selected=[COUPLE 1 BOY]
     *  ]
     *  js> fp1 = new Breather.FormationPiece(f1, FormationList.SINGLE_DANCER); undefined
     *  js> Breather.breathe(Tools.l(fp1, fp2)).toStringDiagram()
     *      ^
     *  
     *     ^    v
     *  js> // now just slightly off-center
     *  js> f1 = new Formation(Tools.m(
     *    >         Tools.p(StandardDancer.COUPLE_1_BOY, Position.getGrid(1,1,"e")
     *    >                                    .forwardStep(Fraction.ONE_HALF, false))))
     *  net.cscott.sdr.calls.Formation[
     *    location={COUPLE 1 BOY=1 1/2,1,e}
     *    selected=[COUPLE 1 BOY]
     *  ]
     *  js> fp1 = new Breather.FormationPiece(f1, FormationList.SINGLE_DANCER); undefined
     *  js> Breather.breathe(Tools.l(fp1, fp2)).toStringDiagram()
     *      ^
     *  
     *     ^    v
     *  js> // point butting up against centerline
     *  js> // NOTE doesn't float to center.  Is this correct?
     *  js> f1 = new Formation(Tools.m(
     *    >         Tools.p(StandardDancer.COUPLE_1_BOY, Position.getGrid(1,1,"e"))))
     *  net.cscott.sdr.calls.Formation[
     *    location={COUPLE 1 BOY=1,1,e}
     *    selected=[COUPLE 1 BOY]
     *  ]
     *  js> fp1 = new Breather.FormationPiece(f1, FormationList.SINGLE_DANCER); undefined
     *  js> Breather.breathe(Tools.l(fp1, fp2)).toStringDiagram()
     *     ^
     *  
     *     ^    v
     * @doc.test Middle of a run, runner breathes out slightly to make room:
     *  js> importPackage(net.cscott.sdr.util)
     *  js> f = new Formation(Tools.m(
     *    >         Tools.p(StandardDancer.COUPLE_2_BOY, Position.getGrid(0,0,"n")),
     *    >         Tools.p(StandardDancer.COUPLE_2_GIRL, Position.getGrid(0,1,"e"))))
     *  net.cscott.sdr.calls.Formation[
     *    location={COUPLE 2 GIRL=0,1,e, COUPLE 2 BOY=0,0,n}
     *    selected=[COUPLE 2 GIRL, COUPLE 2 BOY]
     *  ]
     *  js> f.toStringDiagram()
     *  2G>
     *  2B^
     *  js> f = Breather.breathe(f)
     *  net.cscott.sdr.calls.Formation[
     *    location={COUPLE 2 GIRL=0,2,e, COUPLE 2 BOY=0,0,n}
     *    selected=[COUPLE 2 GIRL, COUPLE 2 BOY]
     *  ]
     *  js> f.toStringDiagram()
     *  2G>
     *  
     *  2B^
     * @doc.test Same thing with more dancers:
     *  js> importPackage(net.cscott.sdr.util)
     *  js> f = new Formation(Tools.m(
     *    >         Tools.p(StandardDancer.COUPLE_3_BOY, Position.getGrid(-3,1,"n")),
     *    >         Tools.p(StandardDancer.COUPLE_4_GIRL,Position.getGrid(-3,0,"w")),
     *    >         Tools.p(StandardDancer.COUPLE_3_GIRL,Position.getGrid(-1,1,"e")),
     *    >         Tools.p(StandardDancer.COUPLE_4_BOY, Position.getGrid(-1,0,"s")),
     *    >         Tools.p(StandardDancer.COUPLE_2_BOY, Position.getGrid(1,1,"n")),
     *    >         Tools.p(StandardDancer.COUPLE_1_GIRL,Position.getGrid(1,0,"w")),
     *    >         Tools.p(StandardDancer.COUPLE_2_GIRL,Position.getGrid(3,1,"e")),
     *    >         Tools.p(StandardDancer.COUPLE_1_BOY, Position.getGrid(3,0,"s")))
     *    >     ).recenter();
     *  net.cscott.sdr.calls.Formation[
     *    location={COUPLE 3 BOY=-3,1/2,n, COUPLE 3 GIRL=-1,1/2,e, COUPLE 2 BOY=1,1/2,n, COUPLE 2 GIRL=3,1/2,e, COUPLE 4 GIRL=-3,-1/2,w, COUPLE 4 BOY=-1,-1/2,s, COUPLE 1 GIRL=1,-1/2,w, COUPLE 1 BOY=3,-1/2,s}
     *    selected=[COUPLE 3 BOY, COUPLE 3 GIRL, COUPLE 2 BOY, COUPLE 2 GIRL, COUPLE 4 GIRL, COUPLE 4 BOY, COUPLE 1 GIRL, COUPLE 1 BOY]
     *  ]
     *  js> f.toStringDiagram()
     *  3B^  3G>  2B^  2G>
     *  4G<  4Bv  1G<  1Bv
     *  js> f = Breather.breathe(f)
     *  net.cscott.sdr.calls.Formation[
     *    location={COUPLE 3 BOY=-3,1,n, COUPLE 3 GIRL=-1,1,e, COUPLE 2 BOY=1,1,n, COUPLE 2 GIRL=3,1,e, COUPLE 4 GIRL=-3,-1,w, COUPLE 4 BOY=-1,-1,s, COUPLE 1 GIRL=1,-1,w, COUPLE 1 BOY=3,-1,s}
     *    selected=[COUPLE 3 BOY, COUPLE 3 GIRL, COUPLE 2 BOY, COUPLE 2 GIRL, COUPLE 4 GIRL, COUPLE 4 BOY, COUPLE 1 GIRL, COUPLE 1 BOY]
     *  ]
     *  js> f.toStringDiagram()
     *  3B^  3G>  2B^  2G>
     *
     *  4G<  4Bv  1G<  1Bv
     */
    // note that we resolve collisions in input formation, but ignore any
    // present in output formation.  That ensures that we don't unnecessarily
    // breathe space-invader calls, esp if the input formation is a single
    // dancer giving the orientation only (or the match was used solely
    // to assign tags).
    public static Formation breathe(List<FormationPiece> pieces) {
        try {
            return _breathe(pieces);
        } catch (ExCLError e) {
            throw new BadCallException("Can't breathe");
        }
    }
    private static Formation _breathe(List<FormationPiece> pieces) throws ExCLError {
        // Locate collisions and resolve them to miniwaves.
        pieces = resolveCollisions(pieces);
        // center all output formations
        pieces = centerOutputPieces(pieces);
        // Trim boundaries to resolve overlaps
        List<Box> inputBounds = trimOverlap(pieces);
	// Find and sort boundaries of component formations.
        Axis x = new Axis(), y = new Axis();
        for (int i=0; i<pieces.size(); i++) {
            FormationPiece fp = pieces.get(i);
            Box inBound = inputBounds.get(i);
            Box outBound = fp.output.bounds();
            x.bounds.put(inBound.ll.x, Fraction.ZERO);
            x.bounds.put(inBound.ur.x, Fraction.ZERO);
            y.bounds.put(inBound.ll.y, Fraction.ZERO);
            y.bounds.put(inBound.ur.y, Fraction.ZERO);
            x.addBit(inBound.ll.x, inBound.ur.x, outBound.width());
            y.addBit(inBound.ll.y, inBound.ur.y, outBound.height());
        }
        // make sure there's an entry for the centerline, even if no dancer
        // is adjacent.
        x.bounds.put(Fraction.ZERO, Fraction.ZERO);
        y.bounds.put(Fraction.ZERO, Fraction.ZERO);
        // okay, now expand our formations, until all our constraints are met
        for (Axis axis: l(x, y)) {
            // use Cassowary constraint solver (basic linear programming)
            // to expand formation.

            // solver setup: create variables for each boundary point;
            //               objective function minimizes all boundaries
            ClSimplexSolver solver = new ClSimplexSolver();
            Map<Fraction, ClVariable> vars =
                new LinkedHashMap<Fraction, ClVariable>();
            for (Fraction f: axis.bounds.keySet()) {
                ClVariable v = new ClVariable(Fraction.ZERO);
                ClStrength s = f.equals(Fraction.ZERO) ?
                        ClStrength.required : ClStrength.weak;
                solver.addConstraint(new ClLinearEquation(v, Fraction.ZERO, s));
                vars.put(f, v);
            }
            assert vars.containsKey(Fraction.ZERO);
            // Constraint 1: Boundaries need to be strictly increasing
            //               (required constraint)
            Fraction last = null;
            for (Fraction f : axis.bounds.keySet()) {
                if (last!=null)
                    solver.addConstraint(new ClLinearInequality
                            (vars.get(f), CL.Op.GEQ, vars.get(last)));
                last = f;
            }
            // Constraint 2: Must fit formation (outer-inner >= size)
            //               (required constraint)
            for (Bit b : axis.bits) {
                ClVariable lo = vars.get(b.start), hi = vars.get(b.end);
                solver.addConstraint(new ClLinearInequality
                        (CL.Plus(lo, b.size), CL.Op.LEQ, hi));
            }
            // Symmetry constraint: moving from edges in, gaps should
            // be equal. (strong constraint, not required)
            for (Bit b : axis.bits) {
                // (inner and outer are actually reversed for negative coords,
                //  but it doesn't matter)
                Fraction lastInner = b.start, lastOuter = b.end;
                while(true) {
                    Fraction inner = axis.bounds.higherKey(lastInner);
                    Fraction outer = axis.bounds.lowerKey(lastOuter);
                    if (inner.compareTo(outer) >= 0) break; // done.
                    // okay, compare size of inner gap (inner-lastInner)
                    // to outer gap (lastOuter-outer).
                    ClLinearExpression innerSize =
                        CL.Minus(vars.get(inner), vars.get(lastInner));
                    ClLinearExpression outerSize =
                        CL.Minus(vars.get(lastOuter), vars.get(outer));
                    solver.addConstraintNoException(new ClLinearEquation
                            (innerSize, outerSize, ClStrength.strong));
                    lastInner = inner; lastOuter = outer;
                }
            }
            // okay, read out the results.
            for (Fraction f : axis.bounds.keySet()) {
                axis.bounds.put(f, vars.get(f).value());
            }
        }
        // assemble meta formation.
        Map<Dancer,Position> nf = new LinkedHashMap<Dancer,Position>();
        for (int i=0; i<pieces.size(); i++) {
            FormationPiece fp = pieces.get(i);
            Box origBounds = inputBounds.get(i);
            Box newBounds = new Box(new Point(x.bounds.get(origBounds.ll.x),
                                              y.bounds.get(origBounds.ll.y)),
                                    new Point(x.bounds.get(origBounds.ur.x),
                                              y.bounds.get(origBounds.ur.y)));
            Point newCenter = newBounds.center();
            // translate the output formation to this center.
            for (Dancer d: fp.output.dancers()) {
                Position oldPos = fp.output.location(d);
                nf.put(d, oldPos.relocate(oldPos.x.add(newCenter.x),
                                          oldPos.y.add(newCenter.y),
                                          oldPos.facing));
            }
        }
        return new Formation(nf);
    }
    /** Abstract representation of one dimension of a formation, used
     * for the expansion algorithm. */
    private static class Bit {
        /** Original boundary corresponding to the inner border of the
         *  formation. */
        final Fraction start;
        /** Original boundary corresponding to the outer border of the
         *  formation. */
        final Fraction end;
        /** Minimum size needed for this formation piece. */
        final Fraction size;
        public Bit(Fraction start, Fraction end, Fraction size) {
            this.start = start;
            this.end = end;
            this.size = size;
        }
        public String toString() {
            return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("start", start.toProperString())
                .append("end", end.toProperString())
                .append("size", size.toProperString())
                .toString();
        }
    }
    /** State associated with the x or y axis; since we expand each axis
     * separately, it's nice to abstract away exactly which one we're dealing
     * with. */
    private static class Axis {
        final TreeMap<Fraction,Fraction> bounds =
            new TreeMap<Fraction,Fraction>();
        final List<Bit> bits =
            new ArrayList<Bit>();
        public Axis() {}
        void addBit(Fraction start, Fraction end, Fraction size) {
            // if this bit straddles zero, add two bits of half the size
            if ((start.compareTo(Fraction.ZERO) >= 0) !=
                (end.compareTo(Fraction.ZERO) > 0) ) {
                addBit(start, Fraction.ZERO, size.divide(Fraction.TWO));
                addBit(Fraction.ZERO, end, size.divide(Fraction.TWO));
            } else {
                // otherwise, just add the bit
                bits.add(new Bit(start, end, size));
            }
        }
        public String toString() {
            return new ToStringBuilder(this, SdrToString.STYLE)
                .append("bounds", bounds)
                .append("bits", bits)
                .toString();
        }
    }
    /** Locate collisions and resolve them to miniwaves. */
    private static List<FormationPiece> resolveCollisions(List<FormationPiece>
                                                          pieces) {
        // hash to collect pieces with the same center
        ListMultiMap<Point,FormationPiece> mm = mml();
        for (FormationPiece fp : pieces)
            mm.add(fp.input.bounds().center(), fp);
        // now assemble result list of FormationPieces, merging collisions as
        // we find them.
        List<FormationPiece> result =
            new ArrayList<FormationPiece>(pieces.size());
        for (Point center: mm.keySet()) {
            List<FormationPiece> l= mm.getValues(center);
            switch(l.size()) {
            case 1:
                // no collision
                result.add(l.get(0));
                break;
            case 2:
                // collision!
                result.add(collide(l.get(0), l.get(1)));
                break;
            default:
                // illegal if more then 2 dancers collide on a spot
                throw new BadCallException("more than two dancers colliding");
            }
        }
        return result;
    }
    /* Ensure that fp.output is centered, for every formation pieces. */
    private static List<FormationPiece> centerOutputPieces(List<FormationPiece>
                                                           pieces) {
        return foreach(pieces, new F<FormationPiece,FormationPiece>() {
            @Override
            public FormationPiece map(FormationPiece fp) {
                return new FormationPiece(fp.input, fp.output.recenter());
            }
        });
    }
    /** Collide two formation pieces, creating a new FormationPiece
     * with the resulting miniwave of pieces. */
    private static FormationPiece collide(FormationPiece a, FormationPiece b) {
        boolean passLeft = isLeft(a.input);
        if (passLeft != isLeft(b.input))
            throw new BadCallException("inconsistent passing shoulder");
        Formation meta = passLeft ?
                FormationList.LH_MINIWAVE : FormationList.RH_MINIWAVE ;
        Dancer[] dd = meta.sortedDancers().toArray(new Dancer[2]);
        // use rotation of a.input and b.input to determine
        // how to rotate meta formation, such that 'a' maps to dd[0]
        // and 'b' maps to dd[1]
        ExactRotation[] rr = new ExactRotation[] {
                (ExactRotation) formationFacing(a.input, Fraction.ONE),
                (ExactRotation) formationFacing(b.input, Fraction.ONE)
        };
        if (rr[0]==null || rr[1]==null)
            throw new BadCallException("inconsistent facing direction");
        if (!rr[0].add(Fraction.ONE_HALF).equals(rr[1]))
            throw new BadCallException("collision but not facing opposite");
        meta = meta.rotate(rr[0].subtract(meta.location(dd[0]).facing.amount));
        // this is a little odd; insert wants to rotate the output formations
        // to match the facing directions in the meta formation.  But our
        // output formations are already facing the right way, so force all
        // the facing directions in the meta to 'north'
        for (Dancer d : meta.dancers()) {
            Position p = meta.location(d);
            meta = meta.move(d, p.relocate(p.x, p.y, ExactRotation.ZERO));
        }
        // okay, put the pieces together!
        Formation f = insert(meta, m(p(dd[0],a.output),p(dd[1],b.output)));
        return new FormationPiece(a/*pick one arbitrarily*/.input, f);
    }
    /** Check that the {@link Position.Flag#PASS_LEFT} flag is consistent
     * on all dancers in {@code fp.input}, and return true if it is
     * present.
     */
    private static boolean isLeft(Formation f) {
        boolean sawRight = false, sawLeft = false;
        for (Dancer d : f.dancers()) {
            if (f.location(d).flags.contains(Position.Flag.PASS_LEFT))
                sawLeft = true;
            else
                sawRight = true;
        }
        assert sawLeft || sawRight : "what, no dancers?";
        if (sawLeft && sawRight)
            throw new BadCallException("inconsistent passing shoulder");
        return sawLeft;
    }
    /** Return a consistent facing direction (modulo the modulus) if the
     * formation f has one, or else return null. */
    private static Rotation formationFacing(Formation f, Fraction modulus){
        Rotation r = null;
        // find "most exact" facing direction (largest modulus)
        for (Dancer d : f.dancers()) {
            Rotation rr = f.location(d).facing;
            if (r==null || r.modulus.compareTo(rr.modulus) < 0)
                r = rr;
        }
        assert r!=null : "what, no dancers?";
        // normalize to the desired level of (in)exactness.
        if (r.modulus.compareTo(modulus) < 0)
            return null; // formation direction is vague
        r = Rotation.create(r.amount, modulus);
        // ensure all dancers are consistent with this.
        for (Dancer d : f.dancers()) {
            Rotation rr = f.location(d).facing;
            if (!r.includes(rr))
                return null; // inconsistent facing direction
        }
        return r;
    }
    /**
     * Create a list of trimmed bounding boxes <i>which do not overlap</i>
     * from the given list of potentially-overlapping {@link FormationPiece}s.
     * We are only concerned with the {@link FormationPiece#input} formations
     * in the {@link FormationPiece}s.  This is a mess of heuristics and
     * hacks.  Currently: we prefer to trim boundaries which are not shared
     * (ie, don't break existing proper handholds), and then order by the
     * size of the overlap, trimming smallest to largest overlap.  We also
     * have a special "star" recognition algorithm, and don't attempt to
     * trim edges involved in a star.  The goal is to ensure that as you move
     * the points of a diamond inward, you never force the centers apart, until
     * you get to the point where the points and centers are equidistant --
     * at that point you have a star.  If you continue bringing the points
     * in, you should really breathe out to a star (ie, still avoid breaking
     * the centers' existing handhold), but at the moment we'll breathe out
     * to a diamond instead: we only preserve stars if they already exist,
     * we never make stars.
     */
    // test cases are contained within doctests for public breathe()
    private static List<Box> trimOverlap(List<FormationPiece> pieces) {
        try {
            return _trimOverlap(pieces);
        } catch (ExCLInternalError e) {
            assert false; // should never happen
        } catch (ExCLRequiredFailure e) {
            // fall through
        }
        throw new BadCallException("can't trim");
    }
    private static boolean hasAnyOverlaps(List<FormationPiece> pieces) {
        for (int i=0; i<pieces.size(); i++) {
            Box iBounds = pieces.get(i).input.bounds();
            for (int j=i+1; j<pieces.size(); j++) {
                Box jBounds = pieces.get(j).input.bounds();
                if (iBounds.overlaps(jBounds))
                    return true;
            }
        }
        return false;
    }
    private static List<Box> _trimOverlap(List<FormationPiece> pieces)
        throws ExCLRequiredFailure, ExCLInternalError {
        // quick out if no overlaps
        if (!hasAnyOverlaps(pieces)) {
            List<Box> bounds = new ArrayList<Box>(pieces.size());
            for (FormationPiece fp : pieces)
                bounds.add(fp.input.bounds());
            return bounds;
        }
        List<VariableBox> vars = new ArrayList<VariableBox>(pieces.size());
        // create variables and basic constraints
        ClBranchAndBound solver = new ClBranchAndBound();
        for (FormationPiece fp : pieces)
            vars.add(new VariableBox(solver, fp.input));

        // add pairwise constraints
        for (int i=0; i<pieces.size(); i++) {
            for (int j=i+1; j<pieces.size(); j++) {
                VariableBox va = vars.get(i), vb = vars.get(j);
                //              no X overlap | some X overlap | total X overlap
                // no Y overlap    skip           skip             skip
                // some Y overlap  skip        resolve X or Y     resolve Y
                // total Y overlap skip          resolve X         error
                if (va.toBox().overlaps(vb.toBox())) {
                    PairedConstraint<ClLinearInequality> xOverlap =
                        va.overlapConstraint(vb,true);
                    PairedConstraint<ClLinearInequality> yOverlap =
                        va.overlapConstraint(vb,false);
                    List<PairedConstraint<ClLinearEquation>> star =
                        va.starConstraints(vb);
                    List<ClBooleanVariable> options =
                        new ArrayList<ClBooleanVariable>(3);

                    if (star!=null) {
                        // option 1: could make a star
                        ClBooleanVariable sw = new ClBooleanVariable(solver);
                        for (PairedConstraint<ClLinearEquation> pc : star) {
                            solver.addConstraintIf(sw, pc.required);
                            if (pc.symmetry != null)
                                solver.addConstraintIf(sw, pc.symmetry);
                        }
                        options.add(sw);
                    }
                    if (xOverlap!=null) {
                        // option 2: could resolve the x overlap
                        ClBooleanVariable sw = new ClBooleanVariable(solver);
                        solver.addConstraintIf(sw, xOverlap.required);
                        solver.addConstraintIf(sw, xOverlap.symmetry);
                        options.add(sw);
                    }
                    if (yOverlap!=null) {
                        // option 3: could resolve the y overlap
                        ClBooleanVariable sw = new ClBooleanVariable(solver);
                        solver.addConstraintIf(sw, yOverlap.required);
                        solver.addConstraintIf(sw, yOverlap.symmetry);
                        options.add(sw);
                    }

                    // we can only take one of these options.
                    if (!options.isEmpty()) {
                        ClLinearExpression sum =
                            new ClLinearExpression(Fraction.mONE);
                        for (ClBooleanVariable v : options)
                            sum = sum.addVariable(v);
                        solver.addConstraint(new ClLinearEquation(sum));
                    }
                }

                // try to keep handholds (optional constraints)
                List<PairedConstraint<ClLinearEquation>> handConstraints =
                    va.handConstraints(vb);
                if (handConstraints!=null) {
                    ClBooleanVariable sw = new ClBooleanVariable(solver);
                    for (PairedConstraint<ClLinearEquation> pc:handConstraints){
                        solver.addConstraintIf(sw, pc.required);
                        if (pc.symmetry != null)
                            solver.addConstraintIf(sw, pc.symmetry);
                    }
                    // w/ medium strength, request sw to be 1
                    solver.addConstraint
                    (new ClLinearEquation(sw, Fraction.ONE, ClStrength.medium));
                }
            }
        }

        // sanity check: verify that var values haven't changed before solving
        for (int i=0; i<pieces.size(); i++)
            assert pieces.get(i).input.bounds().equals(vars.get(i).toBox());

        // solve constraints, return list of new boundaries.
        solver.solve();
        List<Box> bounds = new ArrayList<Box>(pieces.size());
        for (VariableBox v : vars)
            bounds.add(v.toBox());
        return bounds;
    }

    /** A {@link Box}, except using {@link ClVariable}s to represent the
     *  left/right/bottom/top coordinates.
     */
    private static class VariableBox {
        final ClVariable left, bottom;
        final ClVariable right, top;
        /**
         * Formation "handhold" direction modulo 1/2, or {@code} null if it does
         * not have a consistent handhold direction.  For a dancer facing
         * north or south, the hand hold direction is "east and west"; for
         * dancers facing east or west, the hand hold direction is "north and
         * south", etc.
         */
        final Rotation handholdDir;

        /** Create a new {@link VariableBox} corresponding to the bounds of
         *  the specified input {@link Formation}.
         */
        public VariableBox(ClBranchAndBound s, Formation input)
            throws ExCLRequiredFailure, ExCLInternalError {
            Box b = input.bounds();
            this.left = new ClVariable(b.ll.x);
            this.bottom = new ClVariable(b.ll.y);
            this.right = new ClVariable(b.ur.x);
            this.top = new ClVariable(b.ur.y);
            Rotation hhD = formationFacing(input, Fraction.ONE_HALF);
            this.handholdDir = (hhD==null)?null:hhD.add(Fraction.ONE_QUARTER);
            // stays: try to keep the bounds in the same place if possible
            for (ClVariable v : l(left, bottom, right, top))
                s.addConstraint
                    (new ClLinearEquation(v, v.value(), ClStrength.weak));
            // required constraints: l<r, b<t
            s.addConstraint(new ClLinearInequality(left, CL.Op.LEQ, right));
            s.addConstraint(new ClLinearInequality(bottom, CL.Op.LEQ, top));
            // ONLY ALLOW SHRINKING REGIONS to resolve overlaps
            // this ensures that overlaps in the solution are a subset of
            // those currently present, which keeps the problem reasonable
            s.addConstraint(new ClLinearInequality(left, CL.Op.GEQ, b.ll.x));
            s.addConstraint(new ClLinearInequality(bottom, CL.Op.GEQ, b.ll.y));
            s.addConstraint(new ClLinearInequality(right, CL.Op.LEQ, b.ur.x));
            s.addConstraint(new ClLinearInequality(top, CL.Op.LEQ, b.ur.y));
        }
        public String toString() {
            return "("+left+","+bottom+";"+right+","+top+") "+handholdDir;
        }
        public Box toBox() {
            return new Box(new Point(left.value(), bottom.value()),
                           new Point(right.value(), top.value()));
        }
        public ClVariable getStart(boolean isX) {
            return isX ? left : bottom;
        }
        public ClVariable getEnd(boolean isX) {
            return isX ? right : top;
        }
        /** Return constraints needed to resolve x/y overlaps, or null if no
         *  such constraints are necessary (or possible).
         */
        public PairedConstraint<ClLinearInequality> overlapConstraint(
                VariableBox that, boolean isX) throws ExCLInternalError {
            assert this.toBox().overlaps(that.toBox());
            // flip-flop so that this has lowest start dim.
            int c = this.getStart(isX).value().compareTo(that.getStart(isX).value());
            if (c==0) c = -this.getEnd(isX).value().compareTo(that.getEnd(isX).value());
            if (c > 0) return that.overlapConstraint(this, isX);

            if (this.getEnd(isX).value().compareTo(that.getEnd(isX).value())>=0)
                return null; // complete overlap, can't resolve

            if (this.getEnd(isX).value().compareTo(that.getStart(isX).value())<=0)
                assert false; // no overlap, shouldn't happen

            // ok, need this.end <= that.start
            // we also add a weak symmetry constraint to provide some guidance
            // as to where we'd like the new edge to go; otherwise any cut
            // (from trimming 'this' only to trimming 'that' only) yields
            // the same value for the objective function.
            return new PairedConstraint<ClLinearInequality>
                (new ClLinearInequality
                        (this.getEnd(isX), CL.Op.LEQ, that.getStart(isX)),
                 new ClLinearEquation
                        (CL.Minus(this.getEnd(isX).value(), this.getEnd(isX)),
                         CL.Minus(that.getStart(isX), that.getStart(isX).value()),
                         ClStrength.weak));
        }
        /** Return constraints needed to ensure a star containing this and that,
         *  or null if no such constraints are possible.
         */
        public List<PairedConstraint<ClLinearEquation>> starConstraints(
                VariableBox that) {
            // first, the facing directions have to be star-aligned
            Rotation aR = this.handholdDir;
            Rotation bR = that.handholdDir;
            if (aR==null || bR==null)
                return null; // inconsistent facing dirs, not a star
            if ((!bR.add(Fraction.ONE_QUARTER).equals(aR)) &&
                (!aR.add(Fraction.ONE_QUARTER).equals(bR)))
                return null; // only a star if we're exactly 90 degrees off
            // now look at possible hand holds
            return _handConstraints(that, false/*possible as well as actual*/);
        }
        /** Return constraints needed to ensure this and that are holding
         *  hands, if they are currently doing so.  (Or null, if they are
         *  not holding hands currently.)
         */
        public List<PairedConstraint<ClLinearEquation>> handConstraints(
                VariableBox that) {
            // same facing direction (mod 1/2)
            if (this.handholdDir==null || that.handholdDir==null)
                return null;
            if (!this.handholdDir.equals(that.handholdDir))
                return null; // facing direction mod 1/2 isn't the same
            return _handConstraints(that, true/*only actual*/);
        }
        // shared by handConstraints and starConstraints
        private List<PairedConstraint<ClLinearEquation>> _handConstraints(
                VariableBox that, boolean onlyActual) {
            // ignore merely "possible" handholds
            VarPoint aHand = this.nearHand(that);
            VarPoint bHand = that.nearHand(this);
            if (aHand==null || bHand==null)
                return null; // hands can't reach
            if (onlyActual && !aHand.toPoint().equals(bHand.toPoint()))
                return null; // "possible" not *actual* handhold
            // well! we could hold hands!
            ClLinearEquation x = new ClLinearEquation(aHand.x, bHand.x);
            ClLinearEquation y = new ClLinearEquation(aHand.y, bHand.y);
            // Because our objective function is linear, we need to provide
            // additional guidance on *how* this constraint should be satisfied,
            // otherwise it will just arbitrarily move one endpoint, leaving an
            // ugly asymmetry.  So we add additional weak symmetry constraints,
            // which (more or less) try to make width and height equal
            // (Strictly speaking, if coefficicients of aHand don't involve both
            // start and end, then we don't have to make that dim equal. And
            // stars want width to be equal to height, etc.)
            ClLinearEquation xsym = aHand.nontrivialX() ?
                (bHand.nontrivialX() ?
                 new ClLinearEquation(this.width(), that.width(), ClStrength.weak) :
                 bHand.nontrivialY() ?
                 new ClLinearEquation(this.width(), that.height(), ClStrength.weak) :
                 null) : null;
            ClLinearEquation ysym = aHand.nontrivialY() ?
                (bHand.nontrivialY() ?
                 new ClLinearEquation(this.height(), that.height(), ClStrength.weak) :
                 bHand.nontrivialX() ?
                 new ClLinearEquation(this.height(), that.width(), ClStrength.weak) :
                 null) : null;
            return l(new PairedConstraint<ClLinearEquation>(x, xsym),
                     new PairedConstraint<ClLinearEquation>(y, ysym));
        }
        /** This is not quite right if the box's width doesn't match its height,
         *  but it's close enough -- and works for the orthogonal cases that we
         *  really care about. */
        private VarPoint boundaryPoint(ExactRotation facing) {
            try {
                ClLinearExpression x =
                    width().times(facing.toX().divide(Fraction.TWO));
                ClLinearExpression y =
                    height().times(facing.toY().divide(Fraction.TWO));
                ClLinearExpression centerx =
                    CL.Plus(this.left, this.right).divide(Fraction.TWO);
                ClLinearExpression centery =
                    CL.Plus(this.bottom, this.top).divide(Fraction.TWO);
                return new VarPoint(centerx.plus(x), centery.plus(y));
            } catch (ExCLNonlinearExpression e) {
                assert false : "Should never happen!";
                throw new RuntimeException("Can't compute boundary expression");
            }
        }
        private ClLinearExpression width() {
            return CL.Minus(this.right, this.left);
        }
        private ClLinearExpression height() {
            return CL.Minus(this.top, this.bottom);
        }
        private List<VarPoint> hands() {
            ExactRotation er= new ExactRotation(handholdDir.normalize().amount);
            // left and right labels are somewhat arbitrary, since we've
            // normalized to a modulus of 1/2
            VarPoint rightHand = boundaryPoint(er);
            VarPoint leftHand = boundaryPoint(er.add(Fraction.ONE_HALF));
            return l(rightHand, leftHand);
        }
        /** Return the handhold point which could hold hand with 'that' box,
         *  or 'null' if neither handhold could join up. */
        private VarPoint nearHand(VariableBox that) {
            // the hand point has to be contained within 'that's boundary box
            // to be a possible handhold.  If both are contained, then neither
            // is a possible handhold (this might not be perfectly accurate,
            // but it will do for now)
            VarPoint near = null;
            for (VarPoint hand : hands())
                if (that.toBox().includes(hand.toPoint()))
                    if (near==null)
                        near = hand;
                    else
                        return null; // both match, ugh
            return near;
        }
    }

    /** The equivalent of a {@link Point}, except the x/y coordinates are
     *  represented as {@link ClLinearExpression}s.
     */
    private static class VarPoint {
        final ClLinearExpression x, y;
        VarPoint(ClLinearExpression x, ClLinearExpression y) {
            this.x=x; this.y=y;
        }
        Point toPoint() {
            return new Point(x.evaluate(), y.evaluate());
        }
        boolean nontrivialX() {
            return x.terms().size() > 1;
        }
        boolean nontrivialY() {
            return y.terms().size() > 1;
        }
    }

    /** Because our constraint solver is linear (not least squares), we need
     *  to provide additional guidance wrt how we would like the inequalities
     *  satisfied.  In general, we want boundaries moved symmetrically, instead
     *  of just yanking one boundary over (note that moving one boundary a
     *  distance X has the same linear "error" as moving two boundaries a
     *  distance X/2, which is why we need these additional terms in the
     *  objective function).  This class pairs some required constraint
     *  ({@link ClLinearInequality} or {@link ClLinearEquation}) with a
     *  weak symmetry constraint that goes with it.
     */
    private static class PairedConstraint<CONSTRAINT extends ClConstraint> {
        final CONSTRAINT required;
        final ClLinearEquation symmetry;
        PairedConstraint(CONSTRAINT required, ClLinearEquation symmetry) {
            this.required = required;
            this.symmetry = symmetry;
        }
        public String toString() {
            return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("req", required)
                .append("sym", symmetry)
                .toString();
        }
    }
}
