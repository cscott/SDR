package net.cscott.sdr.calls;

import java.util.*;

import net.cscott.sdr.util.*;

/** The {@link FormationMapper} class contains methods to disassemble
 * a square, given component formations (ie, get a diamond back from
 * a siamese diamond, breathing in), and to reassemble a square given components
 * (given a diamond and the various tandems and couples, put them
 * together, breathing out).
 * @author C. Scott Ananian
 * @version $Id: FormationMapper.java,v 1.10 2006-10-30 22:09:29 cananian Exp $
 */
public class FormationMapper {
    public static Formation test1=null, test2=null;
    /** This method is just for testing. */
    public static void main(String[] args) {
        Map<Dancer,Formation> m = new HashMap<Dancer,Formation>();
        Formation meta = FormationList.RH_OCEAN_WAVE;
        int i=0;
        for (Dancer d : meta.dancers()) {
            Map<Dancer,Dancer> mm = new HashMap<Dancer,Dancer>();
            Formation f = FormationList.COUPLE; 
            for (Dancer dd : f.dancers())
                mm.put(dd,StandardDancer.values()[i++]);
            f = new Formation(f, mm);
            m.put(d,f);
        }
        // okay, we've made our inputs: results should be tidal two-faced line
        test1=insert(meta, m);
        System.out.println(test1);
        
        // new formation.
        m.clear();
        meta = FormationList.RH_DIAMOND;
        i=0;
        for (Dancer d : meta.dancers()) {
            Map<Dancer,Dancer> mm = new HashMap<Dancer,Dancer>();
            Formation f = FormationList.TANDEM;
            for (Dancer dd : f.dancers())
                mm.put(dd,StandardDancer.values()[i++]);
            f = new Formation(f, mm);
            m.put(d,f);
        }
        test2=insert(meta,m);
        System.out.println(test2);
    }
    
    
    /** Insert formations into a meta-formation.  */
    public static Formation insert(final Formation meta,
            final Map<Dancer,Formation> components) {
        // Rotate components to match orientations of meta dancers.
        Map<Dancer,Formation> sub =
            new HashMap<Dancer,Formation>(components.size());
        for (Dancer d : meta.dancers())
            sub.put(d, components.get(d).rotate
                    ((ExactRotation)meta.location(d).facing));
        // Find 'inner boundaries' of meta dancers.
        Set<Fraction> xiB = new HashSet<Fraction>();
        Set<Fraction> yiB = new HashSet<Fraction>();
        for (Dancer d : meta.dancers())
            addInner(meta.bounds(d), xiB, yiB);
        xiB.add(Fraction.ZERO); yiB.add(Fraction.ZERO);
        // sort boundaries.
        List<Fraction> xBound = new ArrayList<Fraction>(xiB);
        List<Fraction> yBound = new ArrayList<Fraction>(yiB);
        Collections.sort(xBound); Collections.sort(yBound);
        // for each dancer, find its boundaries & stretch to accomodate
        // work from center out.
        // Note that expansion occurs *between* elements of the original
        // boundary list, so it is 1 element shorter than the boundary list.
        // expansion[0] is the expansion between boundary[0] and boundary[1].
        List<Fraction> xExpand = new ArrayList<Fraction>
            (Collections.nCopies(xBound.size()-1,Fraction.ZERO));
        List<Fraction> yExpand = new ArrayList<Fraction>
            (Collections.nCopies(yBound.size()-1,Fraction.ZERO));
        List<Dancer> dancers = new ArrayList<Dancer>(meta.dancers());
        // sort dancers by absolute x
        Collections.sort(dancers, new DancerLocComparator(meta,true,true)); 
        for (Dancer d : meta.dancers()) {
            Formation f = sub.get(d);
            Box b = meta.bounds(d);
            expand(xExpand, xBound, b.ll.x, b.ur.x, f.bounds().width());
        }
        // sort dancers by absolute y
        Collections.sort(dancers, new DancerLocComparator(meta,false,true)); 
        for (Dancer d : meta.dancers()) {
            Formation f = sub.get(d);
            Box b = meta.bounds(d);
            expand(yExpand, yBound, b.ll.y, b.ur.y, f.bounds().height());
        }
        // now reassemble a new formation.
        Map<Dancer,Position> nf = new HashMap<Dancer,Position>();
        for (Dancer d : meta.dancers()) {
            Formation f = sub.get(d);
            // find the boundary this formation is going to hang off
            Box b = meta.bounds(d);
            Integer[] xb = findNearest(xBound, b.ll.x, b.ur.x);
            Integer[] yb = findNearest(yBound, b.ll.y, b.ur.y);
            place(nf, f, computeCenter(f.bounds(),
                    warpPair(xExpand,xBound,xb[0],xb[1]),
                    warpPair(yExpand,yBound,yb[0],yb[1])));
        }
        Formation result = new Formation(nf);
        assert result.isCentered();
        return result.recenter(); // belt & suspenders.
    }
    private static class DancerLocComparator implements Comparator<Dancer> {
        private final Formation f;
        private final boolean isX;
        private final boolean isAbs;
        DancerLocComparator(Formation f, boolean isX, boolean isAbs) {
            this.f = f; this.isX = isX; this.isAbs = isAbs;
        }
        public int compare(Dancer d1, Dancer d2) {
            Position p1 = f.location(d1), p2 = f.location(d2);
            Fraction xy1, xy2;
            if (isX) { xy1=p1.x; xy2=p2.x; }
            else { xy1=p1.y; xy2=p2.y; }
            if (isAbs) { xy1=xy1.abs(); xy2=xy2.abs(); }
            return xy1.compareTo(xy2);
        }
    }
    private static Fraction[] warpPair(List<Fraction> expansion, List<Fraction> boundary, Integer low, Integer high) {
        return new Fraction[] {
                low==null ? null : warp(expansion, boundary, boundary.get(low)),
                high==null ? null : warp(expansion, boundary, boundary.get(high))
        };
    }
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
    private static void place(Map<Dancer,Position> nf, Formation f, Point center) {
        for (Dancer d : f.dancers())
            nf.put(d, offset(f.location(d), center));
    }
    private static Position offset(Position p, Point offset) {
        return new Position(p.x.add(offset.x), p.y.add(offset.y), p.facing);
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
    /** Add inner boundaries of the given box to the boundary sets. */
    private static void addInner(Box b, Set<Fraction> xBounds,
            Set<Fraction> yBounds) {
        if (b.ll.x.compareTo(Fraction.ZERO)>0)
            xBounds.add(b.ll.x);
        if (b.ur.x.compareTo(Fraction.ZERO)<0)
            xBounds.add(b.ur.x);
        if (b.ll.y.compareTo(Fraction.ZERO)>0)
            yBounds.add(b.ll.y);
        if (b.ur.y.compareTo(Fraction.ZERO)<0)
            yBounds.add(b.ur.y);
    }

    /*-----------------------------------------------------------------------*/
    
    public static class FormationPiece {
        /** Warped rotated formation. The input formation is a simple
         * superposition of these. */
        public final Formation f;
        /** The (typically {@link PhantomDancer Phantom}) dancer who will
         * correspond to this in the output meta formation. */
        public final Dancer d;
        /** The rotation to use for this dancer in the output meta formation
         * (typically this is the rotation of formation {@code f} from
         * whatever the 'canonical' orientation is. */
        public final ExactRotation r;
        public FormationPiece(Formation f, Dancer d, ExactRotation r) {
            this.f = f;
            this.d = d;
            this.r = r;
        }
    }
    /** Create canonical formation by compressing components of a given
     * formation. The result has the meta formation, as well as a map giving
     * the correspondence between the new phantom dancers and the input formations.*/
    public static Formation compress(List<FormationPiece> pieces) {
        // Find 'inner boundaries' of component formations.
        Set<Fraction> xiB = new HashSet<Fraction>();
        Set<Fraction> yiB = new HashSet<Fraction>();
        for (FormationPiece fp : pieces)
            addInner(fp.f.bounds(), xiB, yiB);
        xiB.add(Fraction.ZERO); yiB.add(Fraction.ZERO);
        // sort boundaries.
        List<Fraction> xBound = new ArrayList<Fraction>(xiB);
        List<Fraction> yBound = new ArrayList<Fraction>(yiB);
        Collections.sort(xBound); Collections.sort(yBound);
        
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

        // now expand bounds so that they are just big enough for a single
        // dancer.  Work from center out.
        List<FormationPiece> fpSorted = new ArrayList<FormationPiece>(pieces);
        // sort dancers by absolute x
        Collections.sort(fpSorted, new FormationPieceComparator(true,true)); 
        for (FormationPiece fp : fpSorted) {
            Box b = fp.f.bounds();
            expand(xExpand, xBound, b.ll.x, b.ur.x, Fraction.TWO);
        }
        // sort dancers by absolute y
        Collections.sort(fpSorted, new FormationPieceComparator(false,true)); 
        for (FormationPiece fp : fpSorted) {
            Box b = fp.f.bounds();
            expand(yExpand, yBound, b.ll.y, b.ur.y, Fraction.TWO);
        }
        // assemble meta formation.
        Map<Dancer,Position> nf = new HashMap<Dancer,Position>();
        Box dancerSize = new Box(new Point(Fraction.mONE,Fraction.mONE),
                new Point(Fraction.ONE,Fraction.ONE));
        for (FormationPiece fp : pieces) {
            // find the boundary this piece is going to hang off
            Box b = fp.f.bounds();
            Integer[] xb = findNearest(xBound, b.ll.x, b.ur.x);
            Integer[] yb = findNearest(yBound, b.ll.y, b.ur.y);
            Point dancerLoc = computeCenter(dancerSize,
                    warpPair(xExpand,xBound,xb[0],xb[1]),
                    warpPair(yExpand,yBound,yb[0],yb[1]));
            nf.put(fp.d, new Position(dancerLoc.x,dancerLoc.y,fp.r));
        }
        Formation result = new Formation(nf);
        return result.recenter();
    }
    private static class FormationPieceComparator implements Comparator<FormationPiece> {
        final boolean isX, isAbs; 
        public FormationPieceComparator(boolean isX, boolean isAbs) {
            this.isX = isX; this.isAbs = isAbs;
        }
        public int compare(FormationPiece fp1, FormationPiece fp2) {
            Point p1 = fp1.f.bounds().center(), p2=fp2.f.bounds().center();
            Fraction xy1, xy2;
            if (isX) { xy1=p1.x; xy2=p2.x; }
            else { xy1=p1.y; xy2=p2.y; }
            if (isAbs) { xy1=xy1.abs(); xy2=xy2.abs(); }
            return xy1.compareTo(xy2);
        }
    }
}