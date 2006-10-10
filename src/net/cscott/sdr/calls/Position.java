package net.cscott.sdr.calls;

import net.cscott.sdr.util.Fraction;
import org.apache.commons.lang.builder.*;

/** Position objects represent the position and orientation of a dancer.
 *  The (0,0) coordinate represents the center of the square, and dancers
 *  are nominally at least one unit away from each other (although breathing
 *  may change this).  A zero rotation for 'facing direction' means
 *  "facing the caller".  Positive y is "towards the caller".  Positive
 *  x is "toward the caller's left/heads' right".  Dancer number one
 *  starts out at <code>(-1/2, -1 1/2)</code> facing <code>0</code>.
 *  Note that the <code>facing</code> field MAY be <code>null</code>
 *  to indicate "rotation unspecified" -- for example, for phantoms
 *  or when specifying "general lines".
 */
public class Position {
    /** Location. Always non-null. */
    public final Fraction x, y;
    /** Facing direction. Note that <code>null</code> IS a legal value,
     * which represents "rotation unspecified" (eg for phantoms). */
    public final Rotation facing;
    /** Create a Position object. Note that <code>facing</code> may
     *  be <code>null</code> to indicate 'rotation unspecified'. */
    public Position(Fraction x, Fraction y, Rotation facing) {
	assert x!=null; assert y!=null;
	this.x = x; this.y = y; this.facing = facing;
    }
    /** Move the given distance in the facing direction. */
    public Position forwardStep(Fraction distance) {
	assert facing!=null : "rotation unspecified!";
	Fraction dx = facing.toX().multiply(distance);
	Fraction dy = facing.toY().multiply(distance);
	return new Position(x.add(dx), y.add(dy), facing);
    }
    /** Move the given distance perpendicular to the facing direction. */
    public Position sideStep(Fraction distance) {
        assert facing!=null : "rotation unspecified!";
        Rotation f = facing.add(Fraction.ONE_HALF);
        Fraction dx = f.toX().multiply(distance);
        Fraction dy = f.toY().multiply(distance);
        return new Position(x.add(dx), y.add(dy), facing);
    }
    /** Rotate the given amount from the current position. */
    public Position rotate(Fraction amount) {
	assert facing!=null : "rotation unspecified!";
	if (amount.equals(Fraction.ZERO)) return this;
	return new Position(x, y, facing.add(amount));
    }
    /** Normalize the rotation of the given position. */
    public Position normalize() {
        return new Position(x, y, facing.normalize());
    }

    // positions in the standard 4x4 grid.
    /** Returns a position corresponding to the standard square
     *  dance grid.  0,0 is the center of the set, and odd coordinates
     *  between -3 and 3 correspond to the standard 4x4 grid.
     *  Remember <code>null</code> IS a legal value for the
     *  <code>Rotation</code>. */
    public static Position getGrid(int x, int y, Rotation r) {
	return new Position
	    (Fraction.valueOf(x,2), Fraction.valueOf(y,2), r);
    }
    /** Returns a position corresponding to the standard square
     *  dance grid.  0,0 is the center of the set, and odd coordinates
     *  between -3 and 3 correspond to the standard 4x4 grid.
     *  For convenience, the direction is specified as a string
     *  valid for <code>Rotation.valueOf(String)</code>. */
    public static Position getGrid(int x, int y, String direction) {
	return getGrid(x,y,Rotation.fromAbsoluteString(direction));
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
