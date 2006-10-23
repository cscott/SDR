package net.cscott.sdr.calls;

import java.util.*;

import net.cscott.sdr.util.Fraction;

/** The {@link FormationMapper} class contains methods to disassemble
 * a square, given component formations (ie, get a diamond back from
 * a siamese diamond, breathing in), and to reassemble a square given components
 * (given a diamond and the various tandems and couples, put them
 * together, breathing out).
 * @author C. Scott Ananian
 * @version $Id: FormationMapper.java,v 1.2 2006-10-23 16:54:45 cananian Exp $
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
        // okay, we've made out inputs: results should be tidal two-faced line
        Formation f = insert(meta, m);
        System.out.println(f);
    }
    
    
    /** Insert formations into a meta-formation. */
    public static Formation insert(Formation meta,
            Map<Dancer,Formation> components) {
        // find shared dancer boundaries
        List<Fraction> xB = new ArrayList<Fraction>();
        List<Fraction> yB = new ArrayList<Fraction>();
        for (Dancer d : meta.dancers()) {
            xB.addAll(Arrays.asList(xBoundaries(meta.location(d))));
            yB.addAll(Arrays.asList(yBoundaries(meta.location(d))));
        }
        // filter out boundaries which aren't shared.
        // XXX: need to consider X and Y simultaneously.
        xB=onlyShared(xB); yB=onlyShared(yB);
        // for each dancer, find its boundaries & stretch to accomodate
        List<Fraction> nxB = new ArrayList<Fraction>
            (Collections.nCopies(xB.size(),Fraction.ZERO));
        List<Fraction> nyB = new ArrayList<Fraction>
            (Collections.nCopies(yB.size(),Fraction.ZERO));
        for (Dancer d : meta.dancers()) {
            Formation f = components.get(d);
            // XXX need to rotate formation appropriately before we get size.
            expand(nxB, xB, xBoundaries(meta.location(d)), xSize(f));
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
        return new Formation(nf).recenter();
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
    private static List<Fraction> onlyShared(List<Fraction> l) {
        Map<Fraction,Integer> m = new HashMap<Fraction,Integer>();
        for (Fraction f : l) {
            if (!m.containsKey(f)) m.put(f, 0);
            m.put(f, 1+m.get(f));
        }
        List<Fraction> r = new ArrayList<Fraction>(l.size());
        for (Fraction f : m.keySet())
            if (m.get(f) > 1)
                r.add(f);
        Collections.sort(r);
        return r;
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
