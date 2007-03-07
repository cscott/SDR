package net.cscott.sdr.calls;

import net.cscott.sdr.util.Fraction;
import net.cscott.sdr.util.Point;

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
    /** The rollDir and sweepDir may be zero. */
    public final Rotation rollDir, sweepDir; // these can be none.
    // XXX: lateral translation?
    /**
     * Create an immutable {@link DancerPath} object.
     */
    public DancerPath(Position from, Position to, Point arcCenter, Fraction time, PointOfRotation pointOfRotation, Rotation rollDir, Rotation sweepDir) {
        this.from = from;
        this.to = to;
        this.arcCenter = arcCenter;
        this.time = time;
        this.pointOfRotation = pointOfRotation;
        this.rollDir = rollDir;
        this.sweepDir = sweepDir;
        assert from != null && to != null && time != null;
        assert rollDir != null && sweepDir != null;
        assert from.facing.isExact() && to.facing.isExact();
        assert rollDir.isExact() && sweepDir.isExact();
        assert time.compareTo(Fraction.ZERO) >= 0;
    }
}
