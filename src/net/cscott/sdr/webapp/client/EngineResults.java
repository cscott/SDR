package net.cscott.sdr.webapp.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class encapsulates the returned data from the server, when given
 * a {@link Sequence}.
 * @author C. Scott Ananian
 */
@SuppressWarnings("serial")
public class EngineResults implements Serializable {
    /** The sequence number helps pair up responses with requests. */
    public int sequenceNumber;
    /** The index of the first invalid call in the {@link Sequence}'s call
     *  list: this is {@code Sequence.calls.size()} if all calls are valid. */
    public int firstInvalidCall;
    /** Error or warning messages for each call; may contains nulls if there
     *  are no errors or warnings.  Size should match list of calls in
     *  {@link Sequence}. */
    public List<String> messages;
    /** Movements for each {@link Dancer}, as a list of {@link DancerPath}s. */
    public List<DancerPath> movements;
    /** Duration of each valid call in the {@link Sequence}'s call list, in
     *  beats.  Length of list should match that of the call list; invalid
     *  calls should have duration 0. */
    public List<Double> timing;
    /** Total length of all valid calls in the {@link Sequence}'s call list.
     *  The sum of {@link #timing}; a convenience. */
    public double totalBeats;

    public EngineResults(int sequenceNumber, int firstInvalidCall,
                         List<String> messages, List<DancerPath> movements,
                         List<Double> timing, double totalBeats) {
        this.sequenceNumber = sequenceNumber;
        this.firstInvalidCall = firstInvalidCall;
        this.messages = messages;
        this.movements = movements;
        this.timing = timing;
        this.totalBeats = totalBeats;
        Collections.sort(movements); // should always be sorted
    }
    /** No-arg constructor for GWT serializability. */
    public EngineResults() {
        this(0, 0, Collections.<String>emptyList(),
             Collections.<DancerPath>emptyList(),
             Collections.<Double>emptyList(), 0);
    }
    // --- caches for fast lookup! ---
    public int getCallNum(double time) {
        ensureTimingCache();
        return lookupFloor(this.timingCache, time);
    }
    // this should be a SortedMap<Double,Integer>, but GWT doesn't support
    // TreeMap.floorEntry() (yet?)
    private transient List<Double> timingCache=null;
    private void ensureTimingCache() {
        if (this.timingCache != null) return; // already got it.
        // rebuild cache
        List<Double> _timingCache = new ArrayList<Double>();
        double start=0;
        for (Double duration : timing) {
            _timingCache.add(start);
            start += duration;
        }
        // add final sentinel at end.
        _timingCache.add(start);
        // ensure that exceptions don't leave us with an invalid cache
        this.timingCache = _timingCache;
    }

    public Position getPosition(int dancerNum, double time) {
        ensurePathCache();
        // binary search through movements for the given dancer list.
        List<DancerPath> someMoves = this.pathCache.get(dancerNum);
        int i = lookupFloor(someMoves, new DancerPath(dancerNum, time));
        return someMoves.get(i).evaluate(time);
    }
    public int getNumDancers() {
        ensurePathCache();
        return this.pathCache.size();
    }
    private transient List<List<DancerPath>> pathCache = null;
    private void ensurePathCache() {
        if (this.pathCache!=null) return; // we've already got it
        // rebuild cache
        List<List<DancerPath>> _pathCache = new ArrayList<List<DancerPath>>();
        Collections.sort(this.movements);
        for (DancerPath dp: this.movements) {
            while (dp.dancerNum >= _pathCache.size())
                _pathCache.add(new ArrayList<DancerPath>());
            _pathCache.get(dp.dancerNum).add(dp);
        }
        // ensure that exceptions don't leave us with an invalid cache
        this.pathCache = _pathCache;
    }

    /** Helper function: return the index of the last entry in the list
     *  less than or equal to the given key. */
    private static <T extends Comparable<? super T>>
    int lookupFloor(List<T> list, T key) {
        // we want the equivalent of SortedMap.floorEntry(time), but
        // GWT doesn't support that (yet!)
        int i = Collections.binarySearch(list, key);
        if (i>=0) return i; // exact match
        if (i==-1) return 0; // hm, time is less than the first start time.
        return -2-i; // should be 'insertion point - 1'
    }

    public static class Point implements Serializable {
        double x, y;
        /** No-arg constructor for GWT serializability. */
        Point() { this(0,0); }
        public Point(double x, double y) { this.x=x; this.y=y; }
        private static final Point ZERO = new Point(0,0);
    }
    public static class Bezier implements Serializable {
        Point p0, p1, p2, p3;
        /** No-arg constructor for GWT serializability. */
        Bezier() { this(Point.ZERO,Point.ZERO,Point.ZERO,Point.ZERO); }
        public Bezier(Point p0, Point p1, Point p2, Point p3) {
            this.p0=p0; this.p1=p1; this.p2=p2; this.p3=p3;
        }
        Point evaluate(double t) {
            double x = eval(p0.x, p1.x, p2.x, p3.x, t);
            double y = eval(p0.y, p1.y, p2.y, p3.y, t);
            return new Point(x, y);
        }
        private static double eval(double p0, double p1, double p2, double p3,
                                   double t) {
            if (t<0) t = 0;
            if (t>1) t = 1;
            double mt = 1 - t;
            return p0*mt*mt*mt + 3*p1*t*mt*mt + 3*p2*t*t*mt + p3*t*t*t;
        }
    }
    public static class DancerPath implements Serializable, Comparable<DancerPath> {
        int dancerNum;
        double startTime, duration;
        Bezier location, direction;
        /** No-arg constructor for GWT serializability. */
        DancerPath() { this(-1,0,0,new Bezier(),new Bezier()); }
        /** Stub constructor used to create keys for comparison. */
        DancerPath(int dancerNum, double time) {
            this(dancerNum, time, 0, null, null);
        }
        /** Full constructor. */
        public DancerPath(int dancerNum, double startTime, double duration,
                          Bezier location, Bezier direction) {
            this.dancerNum = dancerNum;
            this.startTime = startTime;
            this.duration = duration;
            this.location = location;
            this.direction = direction;
        }
        Position evaluate(double time) {
            double t = (time-startTime)/duration;
            Point locP = location.evaluate(t);
            Point dirP = direction.evaluate(t);
            // compute atan2 of dirP to get direction in radians
            return new Position(locP.x, locP.y, Math.atan2(dirP.x, dirP.y));
        }
        public int compareTo(DancerPath dp) {
            // first compare dancerNum
            if (this.dancerNum != dp.dancerNum)
                return (this.dancerNum < dp.dancerNum) ? -1 : +1;
            // then compare start time
            return Double.compare(this.startTime, dp.startTime);
        }
    }
}
