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
 * @version $Id: FormationMapper.java,v 1.4 2006-10-24 23:04:20 cananian Exp $
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
    
    
    /** Insert formations into a meta-formation. */
    public static Formation insert(final Formation meta,
            final Map<Dancer,Formation> components) {
        // Find 'inner boundaries' of dancers.
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
        List<Fraction> xB = new ArrayList<Fraction>(xiB);
        List<Fraction> yB = new ArrayList<Fraction>(yiB);
        Collections.sort(xB); Collections.sort(yB);
        System.out.println("INNER X: "+xB);
        System.out.println("INNER Y: "+yB);
        // for each dancer, find its boundaries & stretch to accomodate
        // work from center out.
        List<Fraction> nxB = new ArrayList<Fraction>
            (Collections.nCopies(xB.size(),Fraction.ZERO));
        List<Fraction> nyB = new ArrayList<Fraction>
            (Collections.nCopies(yB.size(),Fraction.ZERO));
        List<Dancer> dancers = new ArrayList<Dancer>(meta.dancers());
        Collections.sort(dancers, new Comparator<Dancer>() { // sort by abs(x)
            public int compare(Dancer d1, Dancer d2) {
                Position p1 = meta.location(d1), p2 = meta.location(d2);
                return p1.x.abs().compareTo(p2.x.abs());
            }
        });
        for (Dancer d : meta.dancers()) {
            Formation f = components.get(d);
            // XXX need to rotate formation appropriately before we get size.
            expand(nxB, xB, xBoundaries(meta.location(d)), xSize(f));
        }
        Collections.sort(dancers, new Comparator<Dancer>() { // sort by abs(y)
            public int compare(Dancer d1, Dancer d2) {
                Position p1 = meta.location(d1), p2 = meta.location(d2);
                return p1.y.abs().compareTo(p2.y.abs());
            }
        });
        for (Dancer d : meta.dancers()) {
            Formation f = components.get(d);
            // XXX need to rotate formation appropriately before we get size.
            expand(nyB, yB, yBoundaries(meta.location(d)), ySize(f));
        }
        // now reassemble a new formation.
        Map<Dancer,Position> nf = new HashMap<Dancer,Position>();
        for (Dancer d : meta.dancers()) {
            Position center = meta.location(d);
            Formation f = components.get(d);
            // XXX need to rotate formation, then rotate facing direction below
            for (Dancer dd : f.dancers()) {
                Position relative = f.location(dd);
                Position p = new Position
                    (warped(nxB,xB,center.x).add(relative.x),
                     warped(nyB,yB,center.y).add(relative.y),
                     relative.facing);
                nf.put(dd, p);
            }
        }
        Formation result = new Formation(nf);
        assert result.isCentered();
        return result.recenter(); // belt & suspenders.
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
    private static Fraction xSize(Formation f) {
        // find the maximum & minimum of the dancer's xBoundaries
        return f.bounds().width().add(Fraction.valueOf(2));
    }
    private static Fraction ySize(Formation f) {
        // find the maximum & minimum of the dancer's yBoundaries
        return f.bounds().height().add(Fraction.valueOf(2));
    }
    private static void expand(List<Fraction> expansion, List<Fraction> bounds,
                               Fraction[] dancerBounds, Fraction newSize) {
        Integer[] boundIndex = findNearest(bounds, dancerBounds);
        // check distance between boundIndices -- is it big enough?
        if (boundIndex[0]==null || boundIndex[1]==null) return; // no expansion
        assert boundIndex[0] < boundIndex[1];
        Fraction dist = Fraction.ZERO;
        for (int i=boundIndex[0]; i<boundIndex[1]; i++) {
            // add 'native' distance
            dist = dist.add(bounds.get(i+1).subtract(bounds.get(i)));
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
    private static Integer[] findNearest(List<Fraction> bounds, Fraction[] edges) {
        assert edges.length==2;
        Integer[] result = new Integer[2];
        
        int bottom = Collections.binarySearch(bounds, edges[0]);
        if (bottom>=0) result[0]=bottom;
        else if (bottom==-1) result[0]=null; // past bottom shared edge.
        else result[0]=-bottom-2;
        
        int top = Collections.binarySearch(bounds, edges[1]);
        if (top>=0) result[1]=top;
        else if (top==(-bounds.size()-1)) result[1]=null; // past top shared edge.
        else result[1]=-top-1;
        
        return result;
    }
    private static List<Fraction> onlyShared(MultiMap<Fraction,Interval> im) {
        ArrayList<Fraction> result=new ArrayList<Fraction>(im.keySet().size());
        NEXTF:
        for (Fraction f : im.keySet()) {
            List<Interval> intervals = (List<Interval>) im.getValues(f);
            Collections.sort(intervals);
            // intervals are now sorted by start; go through and attempt to
            // find an interval which is shared.
            NEXTI:
            for (int i=0; i<intervals.size(); i++) {
                for (int j=i+1; j<intervals.size(); j++) {
                    Interval ii = intervals.get(i), ij = intervals.get(j);
                    if (ij.start.compareTo(ii.end) < 0) {
                        // we overlap (note that ii.start <= ij.start because
                        // we've sorted intervals, and thus ii.start <= ij.end)
                        // add this to our results list, and move on.
                        result.add(f);
                        continue NEXTF;
                    } else continue NEXTI; // intervals are sorted.
                }
            }
        }
        // okay, done.
        Collections.sort(result);
        return result;
    }
    private static Fraction[] xBoundaries(Position dancerPosition) {
        return new Fraction[] {
                dancerPosition.x.subtract(Fraction.ONE),
                dancerPosition.x.add(Fraction.ONE)
        };
    }
    private static Fraction[] yBoundaries(Position dancerPosition) {
        return new Fraction[] {
                dancerPosition.y.subtract(Fraction.ONE),
                dancerPosition.y.add(Fraction.ONE)
        };
    }
    
    /** Create canonical formation by compressing components of a given
     * formation. */
    public static Formation compress(Formation f, List<Formation> components) {
        return null;
    }
}
