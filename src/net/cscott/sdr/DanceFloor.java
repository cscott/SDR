package net.cscott.sdr;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import net.cscott.sdr.calls.Dancer;
import net.cscott.sdr.calls.DancerBezierPath;
import net.cscott.sdr.util.Fraction;

/** The {@link DanceFloor} path contains the current computed locations for
 *  all dancers; it is synchronized so that it can be asynchronously queried
 *  and updated from various threads.
 * @author C. Scott Ananian
 */
public class DanceFloor {
    private final Map<Dancer,Queue<DancerBezierPath>> pathQueueMap =
        new HashMap<Dancer,Queue<DancerBezierPath>>();

    /** Current (interpolated) path for the given dancer.  Will return
     *  <code>null</code> if the dancer is not currently on the floor. */
    public synchronized DancerBezierPath location(Dancer d, Fraction beatTime) {
        Queue<DancerBezierPath> pathQueue = pathQueue(d);
        if (pathQueue.isEmpty()) return null;
        DancerBezierPath dbp = pathQueue.poll();
        if (dbp == null) return dbp;
        DancerBezierPath next = pathQueue.peek();
        if (next != null && beatTime.compareTo(next.startTime) >= 0)
            return location(d, beatTime);
        pathQueue.add(dbp);
        return dbp;
    }
    /** Add a path for the given dancer. */
    public synchronized void addPath(Dancer d, DancerBezierPath dbp) {
        assert dbp != null;
        Queue<DancerBezierPath> pathQueue = pathQueue(d);
        pathQueue.add(dbp);
    }
    /** Remove the given dancer from the floor. */
    public synchronized void clearPaths(Dancer d) {
        pathQueueMap.remove(d);
    }
    private synchronized Queue<DancerBezierPath> pathQueue(Dancer d) {
        if (!pathQueueMap.containsKey(d))
            pathQueueMap.put(d, new PriorityQueue<DancerBezierPath>());
        return pathQueueMap.get(d);
    }

    /// ------------------ sync to the beat --------------
    private final BlockingQueue<Fraction> beatQueue =
        new LinkedBlockingQueue<Fraction>();

    /** Notify the choreography engine that the given beat is now occurring.
     *  Thread-safe. */
    public void notifyBeat(Fraction beat) {
        beatQueue.add(beat);
    }

    /** Block waiting until notifyBeat() is called with a beat greater than
     *  or equal to that specified.  Thread-safe. */
    public Fraction waitForBeat(Fraction beat) {
        while (true) {
            Fraction time;
            try {
                time = beatQueue.take();
                if (time.compareTo(beat) >= 0)
                    return time;
            } catch (InterruptedException e) {
                /* try again */
            }
        }
    }
}
