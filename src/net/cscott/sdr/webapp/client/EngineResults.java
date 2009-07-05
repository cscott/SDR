package net.cscott.sdr.webapp.client;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

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
    }
    /** No-arg constructor for GWT serializability. */
    public EngineResults() {
        this(0, 0, Collections.<String>emptyList(),
             Collections.<DancerPath>emptyList(),
             Collections.<Double>emptyList(), 0);
    }
    // caches for fast lookup!
    public int getCallNum(double time) {
        if (this.timingCache==null) {
            // rebuild cache
            TreeMap<Double,Integer> _timingCache = new TreeMap<Double,Integer>();
            double start=0;
            int i=0;
            for (Double duration : timing) {
                _timingCache.put(start, i);
                start+=duration;
                i++;
            }
            // add final sentinel at end.
            _timingCache.put(start, i);
            // ensure that exceptions don't leave us with an invalid cache
            this.timingCache = _timingCache;
        }
        return timingCache.floorEntry(time).getValue();
    }
    // this should be a SortedMap<Double,Integer>, but GWT doesn't support
    // TreeMap.floorEntry() (yet?)
    private transient TreeMap<Double,Integer> timingCache=null;

    static class Point implements Serializable {
        double x, y;
        /** No-arg constructor for GWT serializability. */
        Point() { this(0,0); }
        Point(double x, double y) { this.x=x; this.y=y; }
        private static final Point ZERO = new Point(0,0);
    }
    static class Bezier implements Serializable {
        Point p0, p1, p2, p3;
        /** No-arg constructor for GWT serializability. */
        Bezier() { this(Point.ZERO,Point.ZERO,Point.ZERO,Point.ZERO); }
        Bezier(Point p0, Point p1, Point p2, Point p3) {
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
    static class DancerPath implements Serializable {
        int dancerNum;
        double startTime, duration;
        Bezier location, direction;
        /** No-arg constructor for GWT serializability. */
        DancerPath() { this(-1,0,0,new Bezier(),new Bezier()); }
        DancerPath(int dancerNum, double startTime, double duration,
                   Bezier location, Bezier direction) {
            this.dancerNum = dancerNum;
            this.startTime = startTime;
            this.duration = duration;
            this.location = location;
            this.direction = direction;
        }
    }
}
