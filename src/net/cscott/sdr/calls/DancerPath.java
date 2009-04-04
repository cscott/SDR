package net.cscott.sdr.calls;

import net.cscott.sdr.util.Fraction;
import net.cscott.sdr.util.Point;

import org.apache.commons.lang.builder.*;

/** A {@link DancerPath} is the result of evaluating a call for a specific
 * dancer in a formation.  Unlike a {@link net.cscott.sdr.calls.ast.Prim},
 * a {@link DancerPath} is an absolute motion, and includes computed
 * information about flow, roll, sweep, and timing.
 * <p>
 * The flow information we collect is based on Lynette Bellini's
 * <a href="http://www.lynette.org/flow.html">Rules of Flow</a>,
 * along with Dave Wilson's theories about dancer kinematics; to wit:
 * certain "bad flow" motions actually work if there is hand contact
 * between certain dancers, as it lets them push off of one another.
 * 
 * @author C. Scott Ananian
 * @version $Id: DancerPath.java,v 1.3 2007-03-07 22:05:29 cananian Exp $
 */
public class DancerPath {
    /**
     * In order to have a method of discussing the various motions encountered
     * in square dance choreography, Lynette Bellini identified several points
     * of rotation:<dl>
     * <dt>A single dancer:</dt>
     * <dd>the point of rotation is about the center of a single dancer, as in
     * the call roll.</dd>
     * <dt>Two dancers:</dt>
     * <dd>the point of rotation is about a point between two dancers.</dd>
     * <dt>Four dancers:</dt>
     * <dd>the point of rotation is about a point in the center of four
     * dancers.</dd>
     * <dt>Center of the square:</dt>
     * <dd>the point of rotation is about the center point of the square.</dd>
     * </dl> 
     * There are multiple ways to pair dancers; swing and slip are both
     * examples of the two dancer point of rotation, but they are different.
     * The same thing applies for the four dancer situations: from parallel
     * ocean waves,
     * lockit and split counter rotate operate around different points, but
     * they are each four dancers large.
     * <p>(From Lynette Bellini's
     * <a href="http://www.lynette.org/flow.html">Rules of Flow</a>.)
     */
    // XXX use integer for rotation set size, and arcCenter for the point
    //     of rotation?
    public static enum PointOfRotation {
        /** The point of rotation is about the center of a single dancer, as in
         * the call roll. */
        SINGLE_DANCER,
        /** The point of rotation is about a point between two dancers. */
        TWO_DANCERS,
        /** The point of rotation is about a point in the center of four
         *  dancers. */
        FOUR_DANCERS,
        /** The point of rotation is about the center point of the square. */
        SQUARE_CENTER
    }
    
    /** The rotations on the to and from positions should be exact. */
    public final Position from, to;
    /** The arcCenter can be null for straight paths. */
    public final Point arcCenter;
    /** The time this motion should take. */
    public final Fraction time;
    /** The point of rotation, for flow computations.  May be null for
     * straight paths. */
    public final PointOfRotation pointOfRotation; // 1,2,4,8 person, for flow
    // XXX: lateral translation?
    /**
     * Create an immutable {@link DancerPath} object.
     */
    public DancerPath(Position from, Position to, Point arcCenter,
                      Fraction time, PointOfRotation pointOfRotation) {
        assert from != null && to != null && time != null;
        assert from.facing.isExact() && to.facing.isExact();
        assert time.compareTo(Fraction.ZERO) >= 0;
        this.from = from;
        this.to = to;
        this.arcCenter = arcCenter;
        this.time = time;
        this.pointOfRotation = pointOfRotation;
    }
    /** Return true iff this {@link DancerPath} corresponds to
     *  "standing still"; ie, it just syncs time and doesn't change any of
     *  a dancer's state.
     */
    public boolean isStandStill() {
        // xxx: are we going to have to remove position flags from this
        //      comparison at some point?
        return this.from.equals(this.to);
    }
    /** Return an equivalent {@link DancerPath} that completes in an adjusted
     * amount of time.
     */
    public DancerPath scaleTime(Fraction amount) {
        if (amount.equals(Fraction.ONE)) return this; // easy case!
        return new DancerPath
            (from, to, arcCenter, time.multiply(amount), pointOfRotation);
    }
    /** Return a {@link DancerPath} like this one, except with the 'to' and
     * 'from' positions warped to those given.
     */
    public DancerPath translate(Position from, Position to) {
        // FIXME: adjust arcCenter to correspond to new from/to!
        //        or change how it is represented so it doesn't need to
        //        be updated.
        Point arcCenter = null; // XXX
        return new DancerPath(from, to, arcCenter,
                              this.time, this.pointOfRotation);
    }
    /** Return a {@link DancerPath} like this one, except with mirrored
     *  position.
     */
    public DancerPath mirror(boolean mirrorShoulderPass) {
        return new DancerPath(this.from.mirror(mirrorShoulderPass),
                              this.to.mirror(mirrorShoulderPass),
			      this.arcCenter == null ? null :
                              new Point(this.arcCenter.x.negate(),
                                        this.arcCenter.y),
                              this.time, this.pointOfRotation);
    }

    @Override
    public String toString() {
	return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
	    .append("from", from)
	    .append("to", to)
	    .append("arcCenter", arcCenter)
	    .append("time", time.toProperString())
	    .append("pointOfRotation", pointOfRotation)
	    .toString();
    }
}
