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
 * @version $Id: DancerPath.java,v 1.2 2007-03-07 19:24:00 cananian Exp $
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
     * The same thing applies for the four dancer situations: from waves,
     * lockit and split counter rotate operate around different points, but
     * they are each four dancers large.
     * <p>(From Lynette Bellini's
     * <a href="http://www.lynette.org/flow.html">Rules of Flow</a>.)
     */
    public static enum PointOfRotation {
        SINGLE_DANCER, TWO_DANCERS, FOUR_DANCERS, SQUARE_CENTER;
    }
    
    public Position from, to; // rotations should be exact
    public Point arcCenter; // arcCenter can be null for straight paths.
    public Fraction time;
    public PointOfRotation pointOfRotation; // 1,2,4,8 person, for flow
    public Rotation rollDir, sweepDir; // these can be none.
    // XXX: lateral translation?
}
