package net.cscott.sdr.calls;

import net.cscott.sdr.util.Fraction;
import org.apache.commons.lang.builder.*;

/** Position objects represent the position and orientation of a dancer.
 *  The (0,0) coordinate represents the center of the square (or formation),
 *  and dancers
 *  are nominally at least two units away from each other (although breathing
 *  may change this).  A zero rotation for 'facing direction' means
 *  "facing away from the caller".  Positive y is "away from the caller".  Positive
 *  x is "toward the caller's right".  The boy in couple number one
 *  starts out at <code>(-1, -3)</code> facing <code>0</code>.
 *  The <code>facing</code> field may not be <code>null</code>;
 *  to indicate "rotation unspecified" (for example, for phantoms
 *  or when specifying "general lines") use a {@link Rotation} with a
 *  modulus of 0.
 */
public class Position {
    /** Location. Always non-null. */
    public final Fraction x, y;
    /** Facing direction. Note that {@code facing} should always be an
     * {@link ExactRotation} for real (non-phantom) dancers. */
    public final Rotation facing;
    /** Create a Position object from the given x and y coordinates
     * and {@link Rotation}. */
    public Position(Fraction x, Fraction y, Rotation facing) {
	assert x!=null; assert y!=null; assert facing!=null;
	this.x = x; this.y = y; this.facing = facing;
    }
    /** Create a Position object with integer-valued x and y coordinates. */
    public Position(int x, int y, Rotation facing) {
        this(Fraction.valueOf(x),Fraction.valueOf(y),facing);
    }
    /** Move the given distance in the facing direction.
     * Requires that the {@code facing} direction be an
     * {@link ExactRotation}.
     * @doc.test Move the couple #1 boy forward (in to the center) two steps:
     *  js> importPackage(net.cscott.sdr.util)
     *  js> Position.getGrid(-1,-3,"n").forwardStep(Fraction.TWO)
     *  -1,-1,0
     */
    public Position forwardStep(Fraction distance) {
	assert facing!=null : "rotation unspecified!";
	Fraction dx = ((ExactRotation)facing).toX().multiply(distance);
	Fraction dy = ((ExactRotation)facing).toY().multiply(distance);
	return new Position(x.add(dx), y.add(dy), facing);
    }
    /**
     * Move the given distance perpendicular to the facing direction.
     * Requires that the {@code facing} direction be an
     * {@link ExactRotation}.
     * @doc.test Couple #1 boy truck:
     *  js> importPackage(net.cscott.sdr.util)
     *  js> Position.getGrid(-1,-3,"n").sideStep(Fraction.mONE)
     *  -2,-3,0
     * @doc.test Couple #2 girl truck:
     *  js> importPackage(net.cscott.sdr.util)
     *  js> Position.getGrid(3,1,"w").sideStep(Fraction.ONE)
     *  3,2,3/4
     */
    public Position sideStep(Fraction distance) {
        assert facing!=null : "rotation unspecified!";
        ExactRotation f = (ExactRotation) facing.add(Fraction.ONE_QUARTER);
        Fraction dx = f.toX().multiply(distance);
        Fraction dy = f.toY().multiply(distance);
        return new Position(x.add(dx), y.add(dy), facing);
    }
    /** Turn in place the given amount.
     * @doc.test Exercise the turn method:
     *  js> ONE_HALF = net.cscott.sdr.util.Fraction.ONE_HALF
     *  1/2
     *  js> p = Position.getGrid(0,0,"n").turn(ONE_HALF)
     *  0,0,1/2
     *  js> p = p.turn(ONE_HALF)
     *  0,0,1
     */
    public Position turn(Fraction amount) {
	assert facing!=null : "rotation unspecified!";
	if (amount.equals(Fraction.ZERO)) return this;
	return new Position(x, y, facing.add(amount));
    }
    /** Rotate this position around the origin by the given amount.
     * @doc.test Rotating the #1 boy by 1/4 gives the #4 boy position:
     *  js> p = Position.getGrid(-1,-3,0)
     *  -1,-3,0
     *  js> p.rotateAroundOrigin(ExactRotation.ONE_QUARTER)
     *  -3,1,1/4
     */
    public Position rotateAroundOrigin(ExactRotation rot) {
        // x' =  x*cos(rot) + y*sin(rot)
        // y' = -x*sin(rot) + y*cos(rot)
        // where sin(rot) = rot.toX() and cos(rot) = rot.toY()
        Fraction cos = rot.toY(), sin = rot.toX();
        Fraction nx = this.x.multiply(cos).add(this.y.multiply(sin));
        Fraction ny = this.y.multiply(cos).subtract(this.x.multiply(sin));
        return new Position(nx, ny, facing.add(rot.amount));
    }
    /** Normalize (restrict to 0-modulus) the rotation of the given position.
     * @doc.test Show normalization after two 180-degree turns:
     *  js> importPackage(net.cscott.sdr.util)
     *  js> p = Position.getGrid(0,0,"e").turn(Fraction.ONE_HALF)
     *  0,0,3/4
     *  js> p = p.turn(Fraction.ONE_HALF)
     *  0,0,1 1/4
     *  js> p.normalize()
     *  0,0,1/4
     */
    public Position normalize() {
        return new Position(x, y, facing.normalize());
    }

    // positions in the standard 4x4 grid.
    /** Returns a position corresponding to the standard square
     *  dance grid.  0,0 is the center of the set, and odd coordinates
     *  between -3 and 3 correspond to the standard 4x4 grid.
     *  Remember <code>null</code> IS a legal value for the
     *  <code>ExactRotation</code>.
     * @doc.test Some sample grid locations:
     *  js> Position.getGrid(0,0,ExactRotation.ZERO)
     *  0,0,0
     *  js> Position.getGrid(-3,3,ExactRotation.WEST)
     *  -3,3,3/4
     */
    public static Position getGrid(int x, int y, ExactRotation r) {
	return new Position
	    (Fraction.valueOf(x), Fraction.valueOf(y), r);
    }
    /** Returns a position corresponding to the standard square
     *  dance grid.  0,0 is the center of the set, and odd coordinates
     *  between -3 and 3 correspond to the standard 4x4 grid.
     *  For convenience, the direction is specified as a string
     *  valid for <code>ExactRotation.valueOf(String)</code>.
     * @doc.test Some sample grid locations:
     *  js> Position.getGrid(0,0,"n")
     *  0,0,0
     *  js> Position.getGrid(1,2,"e")
     *  1,2,1/4
     */
    public static Position getGrid(int x, int y, String direction) {
	return getGrid(x,y,ExactRotation.fromAbsoluteString(direction));
    }

    // utility functions.
    @Override
    public boolean equals(Object o) {
	if (!(o instanceof Position)) return false;
	Position p = (Position) o;
	return new EqualsBuilder()
	    .append(x, p.x)
	    .append(y, p.y)
	    .append(facing, p.facing)
	    .isEquals();
    }
    @Override
    public int hashCode() {
        if (hashCode==0)
            hashCode = new HashCodeBuilder()
            .append(x).append(y).append(facing)
            .toHashCode();
        return hashCode;
    }
    private transient int hashCode = 0;
    @Override
    public String toString() {
	return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE)
	    .append("x", x.toProperString())
	    .append("y", y.toProperString())
	    .append("facing", facing)
	    .toString();
    }
}
