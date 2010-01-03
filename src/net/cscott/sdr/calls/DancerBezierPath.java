package net.cscott.sdr.calls;

import net.cscott.sdr.util.Fraction;
import net.cscott.sdr.util.Bezier.Bezier2D;
import net.cscott.sdr.webapp.client.Position;

/** Low-level version of {@link DancerPath} which explicitly represents
 *  the Bezier paths for the dancer to follow.  This is more useful than
 *  the usual {@link DancerPath}, which is {@link Position}-based, when
 *  doing low-level affine path transformations like hexagon/octagon and
 *  size expansion, since  it separates the direction from the location.
 *  {@link DancerBezierPath} compares to others based on start time, to
 *  make it easy to keep a list sorted by start time.
 */
public class DancerBezierPath implements Comparable<DancerBezierPath> {
    public final Fraction startTime, duration;
    public final Bezier2D location, direction;
    public DancerBezierPath(Fraction startTime, Fraction duration,
                            Bezier2D location, Bezier2D direction) {
        this.startTime = startTime;
        this.duration = duration;
        this.location = location;
        this.direction = direction;
    }
    public int compareTo(DancerBezierPath dbp) {
        return this.startTime.compareTo(dbp.startTime);
    }
    public double evaluateX(double time) {
        return location.evaluateX(t(time));
    }
    public double evaluateY(double time) {
        return location.evaluateY(t(time));
    }
    public double evaluateAngle(double time) {
        double t = t(time);
        double x = direction.evaluateX(t), y = direction.evaluateY(t);
        return Math.atan2(x, y);
    }
    private double t(double time) {
        double t = (time - startTime.doubleValue()) / duration.doubleValue();
        if (t < 0) return 0;
        if (t > 1) return 1;
        return t;
    }
}