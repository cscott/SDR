package net.cscott.sdr.calls;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import net.cscott.jutil.Default;
import net.cscott.sdr.util.Box;
import net.cscott.sdr.util.Fraction;
import net.cscott.sdr.util.Point;
import net.cscott.sdr.util.Tools.ListMultiMap;
import static net.cscott.sdr.util.Tools.m;//map constructor
import static net.cscott.sdr.util.Tools.mml;//listmultimap constructor
import static net.cscott.sdr.util.Tools.p;//pair constructor
import static net.cscott.sdr.util.Tools.l;//list constructor

/**
 * The {@link FormationMapper} class contains methods to reassemble and
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
 * boundaries adjusted so that they share a boundary at the midpoint of the
 * overlap.  Order the resolution from "closest" overlapping dancers to
 * "furthest apart" (smallest overlap), and secondarily from center out, so
 * that extreme overlaps (ie, dancers spaced 1/4 apart) are handled sanely.
 * Third: Sort and order the boundary coordinates, and then allocate space
 * between boundaries so that it is "just enough" to fit the dancers between
 * them.  If a dancer spans multiple boundary points, their allocation is
 * divided equally between them.  Finally, the output formations are
 * relocated so that they are centered between their new boundaries.
 *
 * @author C. Scott Ananian
 * @version $Id: FormationMapper.java,v 1.10 2006-10-30 22:09:29 cananian Exp $
 */
public class FormationMapper {
    
    /**
     * Insert formations into a meta-formation.  This reassembles the
     * formation after we've decomposed it into (say) boxes to do a
     * four-person call.
     *
     * @doc.test Insert COUPLEs, then TANDEMs into a RH_OCEAN_WAVE.  Then, for
     *  a challenge, insert TANDEMs into a DIAMOND to give a t-bone column:
     *  js> function xofy(meta, f) {
     *    >   var i=0
     *    >   var m=new java.util.LinkedHashMap()
     *    >   for (d in Iterator(meta.sortedDancers())) {
     *    >     var mm=new java.util.LinkedHashMap()
     *    >     for (dd in Iterator(f.sortedDancers())) {
     *    >       mm.put(dd, StandardDancer.values()[i++])
     *    >     }
     *    >     m.put(d, new Formation(f, mm))
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
     *  js> FormationMapper.insert(meta, m).toStringDiagram()
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
     *  js> FormationMapper.insert(meta, m).toStringDiagram()
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
     *  js> FormationMapper.insert(meta, m).toStringDiagram()
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
            return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE)
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
     *  js> FormationMapper.breathe(f).toStringDiagram()
     *  ^    ^
     *  
     *  v    v
     * @doc.test From facing couples, take half a step in; breathe out:
     *  js> importPackage(net.cscott.sdr.util) // for Fraction
     *  js> f = FormationList.FACING_COUPLES ; f.toStringDiagram()
     *  v    v
     *  
     *  ^    ^
     *  js> for (d in Iterator(f.dancers())) {
     *    >   f=f.move(d,f.location(d).forwardStep(Fraction.ONE_HALF, false));
     *    > }; f.toStringDiagram()
     *  v    v
     *  ^    ^
     *  js> // EXPECT FAIL
     *  js> FormationMapper.breathe(f).toStringDiagram()
     *  v    v
     *  
     *  ^    ^
     * @doc.test From single three quarter tag, step out; then breathe in:
     *  js> importPackage(net.cscott.sdr.util) // for Fraction
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
     *  js> FormationMapper.breathe(f).toStringDiagram()
     *    ^
     *  
     *  ^    v
     *  
     *    v
     * @doc.test From single quarter tag, take half a step in; breathe out:
     *  js> importPackage(net.cscott.sdr.util) // for Fraction
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
     *  js> // EXPECT FAIL
     *  js> FormationMapper.breathe(f).toStringDiagram()
     *    v
     *  
     *  ^    v
     *  
     *    ^
     * @doc.test Facing dancers step forward; resolve collision.
     *  js> importPackage(net.cscott.sdr.util) // for Fraction
     *  js> f = FormationList.FACING_DANCERS ; f.toStringDiagram()
     *  v
     *  
     *  ^
     *  js> for (d in Iterator(f.dancers())) {
     *    >   f=f.move(d,f.location(d).forwardStep(Fraction.ONE, false));
     *    > }; f
     *  net.cscott.sdr.calls.TaggedFormation@1db3e20[
     *    location={<phantom@7f>=0,0,n, <phantom@7e>=0,0,s}
     *    selected=[<phantom@7f>, <phantom@7e>]
     *    tags={<phantom@7f>=TRAILER, <phantom@7e>=TRAILER}
     *  ]
     *  js> FormationMapper.breathe(f).toStringDiagram()
     *  ^    v
     * @doc.test Facing couples step forward with a left-shoulder pass;
     *  resolve collision and breathe.
     *  js> importPackage(net.cscott.sdr.util) // for Fraction
     *  js> f = FormationList.FACING_COUPLES ; f.toStringDiagram()
     *  v    v
     *  
     *  ^    ^
     *  js> for (d in Iterator(f.dancers())) {
     *    >   f=f.move(d,f.location(d).forwardStep(Fraction.ONE, false).addFlags(Position.Flag.PASS_LEFT));
     *    > }; undefined
     *  js> FormationMapper.breathe(f).toStringDiagram()
     *  v    ^    v    ^
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
     *  js> f2 = new Formation(Tools.m(
     *    >         Tools.p(StandardDancer.COUPLE_2_BOY, Position.getGrid(1,-1,"n")),
     *    >         Tools.p(StandardDancer.COUPLE_2_GIRL, Position.getGrid(3,-1,"n"))))
     *  net.cscott.sdr.calls.Formation@94e4f4[
     *    location={COUPLE 2 BOY=1,-1,n, COUPLE 2 GIRL=3,-1,n}
     *    selected=[COUPLE 2 BOY, COUPLE 2 GIRL]
     *  ]
     *  js> f2.toStringDiagram()
     *   2B^  2G^
     *  js> fp2 = new FormationMapper.FormationPiece(f2, FormationList.RH_MINIWAVE); undefined
     *  js> // point on far side
     *  js> f1 = new Formation(Tools.m(
     *    >         Tools.p(StandardDancer.COUPLE_1_BOY, Position.getGrid(3,1,"e"))))
     *  net.cscott.sdr.calls.Formation@c028cc[
     *    location={COUPLE 1 BOY=3,1,e}
     *    selected=[COUPLE 1 BOY]
     *  ]
     *  js> fp1 = new FormationMapper.FormationPiece(f1, FormationList.SINGLE_DANCER); undefined
     *  js> FormationMapper.breathe(Tools.l(fp1, fp2)).toStringDiagram()
     *      ^
     *  
     *     ^    v
     *  js> // now just slightly off-center
     *  js> f1 = new Formation(Tools.m(
     *    >         Tools.p(StandardDancer.COUPLE_1_BOY, Position.getGrid(1,1,"e")
     *    >                                    .forwardStep(Fraction.ONE_HALF, false))))
     *  net.cscott.sdr.calls.Formation@c028cc[
     *    location={COUPLE 1 BOY=1 1/2,1,e}
     *    selected=[COUPLE 1 BOY]
     *  ]
     *  js> fp1 = new FormationMapper.FormationPiece(f1, FormationList.SINGLE_DANCER); undefined
     *  js> FormationMapper.breathe(Tools.l(fp1, fp2)).toStringDiagram()
     *      ^
     *  
     *     ^    v
     *  js> // point butting up against centerline
     *  js> // NOTE doesn't float to center.  Is this correct?
     *  js> f1 = new Formation(Tools.m(
     *    >         Tools.p(StandardDancer.COUPLE_1_BOY, Position.getGrid(1,1,"e"))))
     *  net.cscott.sdr.calls.Formation@efae3b[
     *    location={COUPLE 1 BOY=1,1,e}
     *    selected=[COUPLE 1 BOY]
     *  ]
     *  js> fp1 = new FormationMapper.FormationPiece(f1, FormationList.SINGLE_DANCER); undefined
     *  js> FormationMapper.breathe(Tools.l(fp1, fp2)).toStringDiagram()
     *     ^
     *  
     *     ^    v
     */
    public static Formation breathe(List<FormationPiece> pieces) {
        // Locate collisions and resolve them to miniwaves.
        pieces = resolveCollisions(pieces);
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
            for (boolean isPositive : l(true, false)) {
                NavigableMap<Fraction,Fraction> bound =
                    isPositive ? axis.bounds : axis.bounds.descendingMap();
                boolean madeAdjustment;
                do {
                    madeAdjustment = false;
                    Comparator<? super Fraction> c = bound.comparator();
                    if (c==null) c = Default.<Fraction>comparator();
                    // Constraint 1: Boundaries need to be strictly increasing
                    Fraction last=Fraction.ZERO;
                    for (Fraction f=Fraction.ZERO;f!=null;f=bound.higherKey(f)){
                        // if this key isn't at least as large as the last,
                        // make it equal.
                        assert bound.containsKey(f);
                        if (c.compare(last, bound.get(f)) > 0) {
                            bound.put(f, last);
                            madeAdjustment = true;
                        }
                        assert c.compare(last, bound.get(f)) <= 0;
                        last = bound.get(f);
                    }
                    // Constraint 2: Increase outer boundary as little as
                    // possible to fit formation (outer-inner >= size)
                    for (Bit b : axis.bits) {
                        Fraction curSize =
                            bound.get(b.end).subtract(bound.get(b.start));
                        if (b.size.compareTo(curSize) > 0) {
                            // increase the 'higher' edge.
                            Fraction outer = isPositive ? b.end : b.start;
                            // skip this bit if it's on the wrong side of zero.
                            if (c.compare(Fraction.ZERO, outer) >= 0)
                                continue;
                            // okay, adjust it
                            Fraction amt = b.size.subtract(curSize);
                            if (!isPositive) amt = amt.negate();
                            bound.put(outer, bound.get(outer).add(amt));
                            madeAdjustment = true;
                        }
                    }
                    // Symmetry constraint: moving from edges in, gaps should
                    // be equal.
                    for (Bit b : axis.bits) {
                        Fraction lastInner, lastOuter;
                        if (isPositive) {
                            lastInner = b.start; lastOuter = b.end;
                            if (lastOuter.compareTo(Fraction.ZERO) <= 0)
                                continue;
                        } else {
                            lastInner = b.end; lastOuter = b.start;
                            if (lastOuter.compareTo(Fraction.ZERO) >= 0)
                                continue;
                        }
                        while(true) {
                            Fraction inner = bound.higherKey(lastInner);
                            Fraction outer = bound.lowerKey(lastOuter);
                            if (c.compare(inner, outer) >= 0) break; // done.
                            // okay, compare size of inner gap (inner-lastInner)
                            // to outer gap (lastOuter-outer).  We're going
                            // to expand the outside edge of the smaller gap
                            // "just enough" to make them equal
                            // (note: if !isPositive, innerSize & outSize will
                            //  both be negative)
                            Fraction innerSize =
				bound.get(inner).subtract(bound.get(lastInner));
                            Fraction outerSize =
				bound.get(lastOuter).subtract(bound.get(outer));
                            Fraction adjAmt = outerSize.subtract(innerSize);
                            int cc = adjAmt.compareTo(Fraction.ZERO);
                            if (!isPositive) cc=-cc;
                            //XXX: i'm not certain of the methodology here.
			    //     we can adjust either the inner or outer
			    //     gap, how do we know which?  we'll adjust
			    //     both; hopefully that's the right thing.
                            if (cc > 0) {
                                // inner gap is smaller; adjust pos of 'inner'
                                bound.put(inner, bound.get(inner)
					  .add(adjAmt.divide(Fraction.TWO)));
                                bound.put(outer, bound.get(outer)
					  .add(adjAmt.divide(Fraction.TWO)));
                            } else if (cc < 0) {
                                // outer gap is smaller; adjust 'lastOuter'
                                bound.put(lastOuter, bound.get(lastOuter)
					.subtract(adjAmt.divide(Fraction.TWO)));
                                bound.put(lastInner, bound.get(lastInner)
					.subtract(adjAmt.divide(Fraction.TWO)));
                            }
                            lastInner = inner; lastOuter = outer;
                        }
                    }
                } while (madeAdjustment);
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
                formationFacing(a.input), formationFacing(b.input)
        };
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
    /** Check that the facing direction of all dancers is consistent,
     * and return that direction. */
    private static ExactRotation formationFacing(Formation f) {
        Rotation r = null;
        for (Dancer d : f.dancers()) {
            Rotation rr = f.location(d).facing;
            if (r==null) { r = rr; continue; }
            // swap r and rr so that r is exact if either is.
            if (rr.isExact()) { Rotation swp=r; r=rr; rr=swp; }
            // be generous towards inexact rotations
            if (!r.includes(rr))
                throw new BadCallException("inconsistent facing direction during collision");
        }
        assert r!=null : "what, no dancers?";
        if (!r.isExact())
            throw new BadCallException("colliding vague phantoms");
        return (ExactRotation) r;
    }
    /**
     * Create a list of trimmed bounding boxes <i>which do not overlap</i>
     * from the given list of potentially-overlapping {@link FormationPiece}s.
     * We are only concerned with the {@link FormationPiece#input} formations
     * in the {@link FormationPiece}s.
     */
    private static List<Box> trimOverlap(List<FormationPiece> pieces) {
        // XXX: stub this out for now
        List<Box> result = new ArrayList<Box>(pieces.size());
        for (FormationPiece fp: pieces)
            result.add(fp.input.bounds());
        return result;
    }
}
