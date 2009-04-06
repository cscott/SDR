package net.cscott.sdr.calls;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import net.cscott.sdr.calls.ast.Prim.Flag;
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
    public static enum Flag {
        /** Start movement with a sashay instead of stepping forward. */
        SASHAY_START,
        /** Finish movement with a sashay instead of stepping forward. */
        SASHAY_FINISH,
    }
    
    /** The rotations on the to and from positions should be exact. */
    public final Position from, to;
    /** The time this motion should take. */
    public final Fraction time;
    /** The point of rotation, for flow computations.  May be null for
     * straight paths. */
    public final PointOfRotation pointOfRotation; // 1,2,4,8 person, for flow
    /** Additional properties of the path. */
    public final Set<Flag> flags;

    /**
     * Create an immutable {@link DancerPath} object.
     */
    public DancerPath(Position from, Position to, Fraction time,
                      PointOfRotation pointOfRotation, Flag...flags) {
        this(from, to, time, pointOfRotation, Arrays.asList(flags));
    }
    public DancerPath(Position from, Position to, Fraction time,
                       PointOfRotation pointOfRotation, Collection<Flag> flags){
        assert from != null && to != null && time != null;
        assert from.facing.isExact() && to.facing.isExact();
        assert time.compareTo(Fraction.ZERO) >= 0;
        this.from = from;
        this.to = to;
        this.time = time;
        this.pointOfRotation = pointOfRotation;
        // somewhat awkward initialization of read-only this.flags
        Set<Flag> es = EnumSet.noneOf(Flag.class);
        es.addAll(flags);
        this.flags = Collections.unmodifiableSet(es);
    }

    /** Return the approximate "center" of this arcing {@link DancerPath}, or
     * null if this is a straight-line movement.
     */
    public Point arcCenter() {
        // xxx: compute intersection of vectors orthogonal to tangents at
        //      start and finish of movement.  If vectors do not intersect,
        //      (or intersect at every point) this is not an arcing movement;
        //      return null.  Set to dancer's location if only rotation in
        //      place is involved in this movement
        assert false : "unimplemented";
        return null;
    }
    // xxx: tangentStart() / tangentFinish() methods to return the start
    //      and finish direction vectors (based on facing direction, sashay
    //      flags, etc)

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
            (from, to, time.multiply(amount), pointOfRotation, flags);
    }
    /** Return a {@link DancerPath} like this one, except with the 'to' and
     * 'from' positions warped to those given.
     */
    public DancerPath translate(Position from, Position to) {
        return new DancerPath(from, to, this.time, this.pointOfRotation,
                              this.flags);
    }
    /** Return a {@link DancerPath} like this one, except with mirrored
     *  position.
     */
    public DancerPath mirror(boolean mirrorShoulderPass) {
        return new DancerPath(this.from.mirror(mirrorShoulderPass),
                              this.to.mirror(mirrorShoulderPass),
                              this.time, this.pointOfRotation, this.flags);
    }

    @Override
    public String toString() {
        ToStringBuilder tsb =
            new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
	    .append("from", from)
	    .append("to", to)
	    .append("time", time.toProperString())
	    .append("pointOfRotation", pointOfRotation);
        if (!flags.isEmpty())
            tsb = tsb.append("flags", flags);
        return tsb.toString();
    }
}
