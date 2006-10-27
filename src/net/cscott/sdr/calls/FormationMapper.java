package net.cscott.sdr.calls;

import java.util.*;

import net.cscott.sdr.util.*;
import net.cscott.jutil.*;

/** The {@link FormationMapper} class contains methods to disassemble
 * a square, given component formations (ie, get a diamond back from
 * a siamese diamond, breathing in), and to reassemble a square given components
 * (given a diamond and the various tandems and couples, put them
 * together, breathing out).
 * @author C. Scott Ananian
 * @version $Id: FormationMapper.java,v 1.6 2006-10-27 20:43:26 cananian Exp $
 */
public class FormationMapper {
    /** This method is just for testing. */
    public static void main(String[] args) {
        Map<Dancer,Formation> m = new HashMap<Dancer,Formation>();
        Formation meta = FormationList.RH_WAVE;
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
        System.out.println(insert(meta, m));
        
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
        System.out.println(insert(meta, m));
    }
    
    
    /** Insert formations into a meta-formation.  Note that
     * rotations in the meta formation must be exact. */
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
        for (Dancer d : meta.dancers()) {
            Box b = meta.bounds(d);
            if (b.ll.x.compareTo(Fraction.ZERO)>0)
                xiB.add(b.ll.x);
            if (b.ur.x.compareTo(Fraction.ZERO)<0)
                xiB.add(b.ur.x);
            if (b.ll.y.compareTo(Fraction.ZERO)>0)
                yiB.add(b.ll.y);
            if (b.ur.y.compareTo(Fraction.ZERO)<0)
                yiB.add(b.ur.y);
        }
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
        // XXX WARPED IS INCORRECT
        Map<Dancer,Position> nf = new HashMap<Dancer,Position>();
        for (Dancer d : meta.dancers()) {
            Position center = meta.location(d);
            Formation f = sub.get(d);
            // XXX need to rotate facing direction below
            for (Dancer dd : f.dancers()) {
                Position relative = f.location(dd);
                Position p = new Position
                    (warped(xExpand,xBound,center.x).add(relative.x),
                     warped(yExpand,yBound,center.y).add(relative.y),
                     relative.facing);
                nf.put(dd, p);
            }
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
    // XXX this isn't yet correct: need to place center based on mapping
    // of the two edges & the new size (since outside edges are allowed
    // to expand without limit
    private static Fraction warped(List<Fraction> expansion, List<Fraction> bounds, Fraction center) {
        int index = Collections.binarySearch(bounds, center);
        if (index<0) index = -index-1; // now index is first element >= center.
        for (int i=0; i<index; i++)
            center = center.add(expansion.get(i));
        return center;
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
    
    /** Create canonical formation by compressing components of a given
     * formation. */
    public static Formation compress(Formation f, List<Formation> components) {
        return null;
    }
}
