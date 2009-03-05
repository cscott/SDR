package net.cscott.sdr.calls;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import net.cscott.jutil.Factories;
import net.cscott.jutil.GenericMultiMap;
import net.cscott.jutil.MultiMap;
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
    // XXX: we're not using this at the moment; we adjust all boundaries now,
    //      losing the idea of an "unbounded" formation.  I think this is
    //      correct: an outside triangle should stay a triangle, even if the
    //      point is "unbounded" on the outside.  But keeping this code here
    //      for the moment in case this assumption is wrong.
    private static Point computeCenter(Box naturalBounds, Fraction[] x, Fraction[] y) {
        // compute the desired center of the formation.  if both bounds are
        // given, then the center is the mean.  Otherwise, align edge to the
        // known bound. If no bounds are given, put on the centerline.
        Fraction cx, cy;
        if (x[0]==null && x[1]==null)
            cx = Fraction.ZERO;
        else if (x[0]==null)
            cx = x[1].subtract(naturalBounds.width().divide(Fraction.TWO));
        else if (x[1]==null)
            cx = x[0].add(naturalBounds.width().divide(Fraction.TWO));
        else
            cx = x[0].add(x[1]).divide(Fraction.TWO);
        if (y[0]==null && y[1]==null)
            cy = Fraction.ZERO;
        else if (y[0]==null)
            cy = y[1].subtract(naturalBounds.height().divide(Fraction.TWO));
        else if (y[1]==null)
            cy = y[0].add(naturalBounds.height().divide(Fraction.TWO));
        else
            cy = y[0].add(y[1]).divide(Fraction.TWO);
        return new Point(cx, cy);
    }
    /** Compute the 'expanded' location of the given boundary value. */
    private static Fraction warp(List<Fraction> expansion,
                                 List<Fraction> boundary, Fraction val) {
        if (val.compareTo(Fraction.ZERO) >= 0) {
            Integer[] boundIndex = findNearest(boundary, Fraction.ZERO, val);
            // this should be an exact match.
            assert boundary.get(boundIndex[0]).compareTo(Fraction.ZERO)==0;
            assert boundary.get(boundIndex[1]).compareTo(val)==0;
            // okay, starting from zero, add up all the expansion.
            // note that expansion[0] corresponds to boundary[0]-boundary[1], etc
            for (int i=boundIndex[0]; i<boundIndex[1]; i++)
                val = val.add(expansion.get(i));
        } else {
            Integer[] boundIndex = findNearest(boundary, val, Fraction.ZERO);
            // this should be an exact match.
            assert boundary.get(boundIndex[1]).compareTo(Fraction.ZERO)==0;
            assert boundary.get(boundIndex[0]).compareTo(val)==0;
            // okay, starting from zero, add up all the expansion.
            // note that expansion[0] corresponds to boundary[0]-boundary[1], etc
            for (int i=boundIndex[0]; i<boundIndex[1]; i++)
                val = val.subtract(expansion.get(i));
        }
        return val; // done!
    }
    private static Box warp(List<Fraction> xExpand, List<Fraction> yExpand,
                            List<Fraction> xBound, List<Fraction> yBound,
                            Box bounds) {
        Point ll = new Point(warp(xExpand, xBound, bounds.ll.x),
                             warp(yExpand, yBound, bounds.ll.y));
        Point ur = new Point(warp(xExpand, xBound, bounds.ur.x),
                             warp(yExpand, yBound, bounds.ur.y));
        return new Box(ll, ur);
    }
    private static void expand(List<Fraction> expansion, List<Fraction> boundary,
                               Fraction dancerMin, Fraction dancerMax,
                               Fraction newSize) {
        Integer[] boundIndex = findNearest(boundary, dancerMin, dancerMax);
        // if either of the boundIndices is null, then the formation is
        // unconstrained and we don't need to expand anything.
        if (boundIndex[0]==null || boundIndex[1]==null) return;
        // otherwise, let's compute the (expanded) distance between boundIndices
        // is it big enough?
        assert boundIndex[0] < boundIndex[1];
        assert boundary.get(boundIndex[0]).compareTo(dancerMin) <= 0;
        assert boundary.get(boundIndex[1]).compareTo(dancerMax) >= 0;
        Fraction dist = Fraction.ZERO;
        for (int i=boundIndex[0]; i<boundIndex[1]; i++) {
            // add 'native' distance
            dist = dist.add(boundary.get(i+1).subtract(boundary.get(i)));
            // add in expansion to date.
            dist = dist.add(expansion.get(i));
        }
        if (dist.compareTo(newSize) >= 0) return; // no expansion needed
        // figure out how much expansion is needed...
        Fraction inc = newSize.subtract(dist).divide
            (Fraction.valueOf(boundIndex[1]-boundIndex[0]));
        // ... and add it into the expansion list
        for (int i=boundIndex[0]; i<boundIndex[1]; i++)
            expansion.set(i, expansion.get(i).add(inc));
        // okay, we've done the necessary expansion, we're done!
        return;
    }
    private static Integer[] findNearest(List<Fraction> boundary, Fraction dancerMin, Fraction dancerMax) {
        Integer[] result = new Integer[2];
        
        int bottom = Collections.binarySearch(boundary, dancerMin);
        if (bottom>=0) result[0]=bottom;
        else if (bottom==-1) result[0]=null; // past bottom shared edge.
        else result[0]=-bottom-2;
        
        int top = Collections.binarySearch(boundary, dancerMax);
        if (top>=0) result[1]=top;
        else if (top==(-boundary.size()-1)) result[1]=null; // past top shared edge.
        else result[1]=-top-1;
        
        return result;
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
     */
    public static Formation breathe(List<FormationPiece> pieces) {
        // Locate collisions and resolve them to miniwaves.
        pieces = resolveCollisions(pieces);
        // Trim boundaries to resolve overlaps
        List<Box> inputBounds = trimOverlap(pieces);
	// Find and sort boundaries of component formations.
        TreeSet<Fraction> xBoundSet = new TreeSet<Fraction>();
        TreeSet<Fraction> yBoundSet = new TreeSet<Fraction>();
        for (Box bounds: inputBounds) {
            xBoundSet.addAll(l(bounds.ll.x, bounds.ur.x));
            yBoundSet.addAll(l(bounds.ll.y, bounds.ur.y));
        }
        // make sure there's an entry for the centerline, even if no dancer
        // is adjacent.
        xBoundSet.add(Fraction.ZERO); yBoundSet.add(Fraction.ZERO);
        List<Fraction> xBound = new ArrayList<Fraction>(xBoundSet);
        List<Fraction> yBound = new ArrayList<Fraction>(yBoundSet);
        // initalize 'expansion' list so that there is 0 space between
        // bounds.
        // Note that expansion occurs *between* elements of the original
        // boundary list, so it is 1 element shorter than the boundary list.
        // expansion[0] is the expansion between boundary[0] and boundary[1].
        List<Fraction> xExpand = new ArrayList<Fraction>(xBound.size()-1);
        List<Fraction> yExpand = new ArrayList<Fraction>(yBound.size()-1);
        for (int i=0; i<xBound.size()-1; i++)
            xExpand.add(xBound.get(i+1).subtract(xBound.get(i)).negate());
        for (int i=0; i<yBound.size()-1; i++)
            yExpand.add(yBound.get(i+1).subtract(yBound.get(i)).negate());

        // now expand bounds so that they are just big enough for the
        // appropriate output formation.  Work from center out.
        MultiMap<Fraction,Integer> xCenters =
            new GenericMultiMap<Fraction,Integer>
                (Factories.<Fraction,Collection<Integer>>treeMapFactory(),
                 Factories.<Integer>arrayListFactory());
        MultiMap<Fraction,Integer> yCenters =
            new GenericMultiMap<Fraction,Integer>
                (Factories.<Fraction,Collection<Integer>>treeMapFactory(),
		 Factories.<Integer>arrayListFactory());
        // sort input formations by absolute x and y
        for (int i=0; i<pieces.size(); i++) {
            Point center = inputBounds.get(i).center();
            xCenters.add(center.x.abs(), i);
            yCenters.add(center.y.abs(), i);
        }
        // expand the x bounds
        for (Map.Entry<Fraction,Integer> me : xCenters.entrySet()) {
            FormationPiece fp = pieces.get(me.getValue());
            Box bounds = inputBounds.get(me.getValue());
            expand(xExpand, xBound, bounds.ll.x, bounds.ur.x,
                   fp.output.bounds().width());
        }
        // expand the y bounds
        for (Map.Entry<Fraction,Integer> me : yCenters.entrySet()) {
            FormationPiece fp = pieces.get(me.getValue());
            Box bounds = inputBounds.get(me.getValue());
            expand(yExpand, yBound, bounds.ll.y, bounds.ur.y,
                   fp.output.bounds().height());
        }
        // assemble meta formation.
        Map<Dancer,Position> nf = new LinkedHashMap<Dancer,Position>();
        for (int i=0; i<pieces.size(); i++) {
            FormationPiece fp = pieces.get(i);
            Box origBounds = inputBounds.get(i);
            Box newBounds = warp(xExpand, yExpand, xBound, yBound, origBounds);
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
        // stub this out for now
        List<Box> result = new ArrayList<Box>(pieces.size());
        for (FormationPiece fp: pieces)
            result.add(fp.input.bounds());
        return result;
    }
}
