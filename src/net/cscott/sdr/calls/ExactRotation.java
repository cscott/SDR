package net.cscott.sdr.calls;

import org.junit.runner.RunWith;

import net.cscott.jdoctest.JDoctestRunner;
import net.cscott.sdr.util.Fraction;

/** Rotations are represented as fractions, where '0' is facing north
 *  (away from the caller),
 *  and '1/4' is facing east.  Positive denotes clockwise rotation. */
@RunWith(value=JDoctestRunner.class)
public class ExactRotation extends Rotation implements Comparable<ExactRotation> {
    /** Constructor from a <code>Fraction</code> object. */
    public ExactRotation(Fraction amount) {
        super(amount, Fraction.ONE);
    }
    @Override
    public boolean isExact() {
        assert this.modulus.equals(Fraction.ONE);
        return true;
    }
    /** Add the given amount to this rotation direction. */
    @Override
    public ExactRotation add(Fraction f) {
	return (ExactRotation) super.add(f);
    }
    /** Subtract the given amount from this rotation direction. */
    @Override
    public ExactRotation subtract(Fraction f) {
	return (ExactRotation) super.subtract(f);
    }
    /** Negate this rotation (mirror image). */
    @Override
    public ExactRotation negate() {
	return (ExactRotation) super.negate();
    }
    /** Normalize rotation to the range 0-1. */
    @Override
    public ExactRotation normalize() {
	return (ExactRotation) super.normalize();
    }
    /** Compare unnormalized rotation amounts. */
    public int compareTo(ExactRotation r) {
	return this.amount.compareTo(r.amount);
    }
    /** Compute the minimum angle (in absolute value) between {@code this}
     *  heading and the given heading {@code er}.  The result is positive or
     *  negative, and can be added to this heading to yield a rotation
     *  equivalent to the given heading {@code er}.
     * @doc.test
     *  js> ExactRotation.ONE_EIGHTH.minSweep(ExactRotation.SEVEN_EIGHTHS);
     *  -1/4
     *  js> ExactRotation.SEVEN_EIGHTHS.minSweep(ExactRotation.ONE_EIGHTH);
     *  1/4
     */
    public Fraction minSweep(ExactRotation er) {
        Fraction aa = this.normalize().amount, bb = er.normalize().amount;
        boolean aaSmaller = aa.compareTo(bb) < 0;
        Fraction s1 = (aaSmaller) ? bb.subtract(aa) : aa.subtract(bb);
        Fraction s2 = Fraction.ONE.subtract(s1);
        boolean s1Smaller = s1.compareTo(s2) < 0;
        boolean isCW = !(aaSmaller ^ s1Smaller);
        Fraction minSweep = (s1Smaller ? s1 : s2);
        return isCW ? minSweep : minSweep.negate();
    }
    /** Returns a human-readable description of the rotation.  The output
     *  is a valid input to <code>ExactRotation.fromAbsoluteString(String)</code>. */
    @Override
    public String toAbsoluteString() {
        for (int i=0; i<eighths.length; i++)
            if (this.equals(eighths[i]))
                return eighthNames[i];
        return toString();
    }
    /** Returns a human-readable description of the rotation.  The output
     *  is a valid input to <code>ExactRotation.fromAbsoluteString(String)</code>. */
    public String toRelativeString() {
        if (ExactRotation.ONE_QUARTER.equals(this)) return "right";
        if (ExactRotation.ZERO.equals(this)) return "none";
        if (ExactRotation.mONE_QUARTER.equals(this)) return "left";
        return toString();
    }
    /** Converts a string (one of n/s/e/w, ne/nw/se/sw) to the
     * appropriate rotation object. 'n' is facing away from the caller. */
    public static ExactRotation fromAbsoluteString(String s) {
	for (int i=0; i<eighthNames.length; i++)
	    if (eighthNames[i].equalsIgnoreCase(s))
		return eighths[i];
	return new ExactRotation(Fraction.valueOf(s));
    }
    public String repr() {
        if (this.equals(ExactRotation.NORTH))
            return "ExactRotation.NORTH";
        if (this.equals(ExactRotation.EAST))
            return "ExactRotation.EAST";
        if (this.equals(ExactRotation.SOUTH))
            return "ExactRotation.SOUTH";
        if (this.equals(ExactRotation.WEST))
            return "ExactRotation.WEST";
        if (this.equals(ExactRotation.ONE_EIGHTH))
            return "ExactRotation.ONE_EIGHTH";
        if (this.equals(ExactRotation.THREE_EIGHTHS))
            return "ExactRotation.THREE_EIGHTHS";
        if (this.equals(ExactRotation.FIVE_EIGHTHS))
            return "ExactRotation.FIVE_EIGHTHS";
        if (this.equals(ExactRotation.SEVEN_EIGHTHS))
            return "ExactRotation.SEVEN_EIGHTHS";
        return "ExactRotation.fromAbsoluteString(\""+toAbsoluteString()+"\")";
    }
    /** Returns a ExactRotation corresponding to one of the strings "right", "left",
     * or "none".
     * @param s
     * @return ExactRotation.ZERO if s is "none", ExactRotation.ONE_QUARTER if s is
     *  "right", or ExactRotation.mONE_QUARTER if s is "left".
     * @throws IllegalArgumentException if s is not one of "right", "left", 
     *  "none", or a number.
     */
    public static ExactRotation fromRelativeString(String s) {
        s = s.intern();
        if (s=="right") return ExactRotation.ONE_QUARTER;
        if (s=="none") return ExactRotation.ZERO;
        if (s=="left") return ExactRotation.mONE_QUARTER;
        return new ExactRotation(Fraction.valueOf(s));
    }
    private static ExactRotation create(int num, int denom) {
        return (ExactRotation) Rotation.create(Fraction.valueOf(num,denom),Fraction.ONE);
    }
    /** Common rotations. */
    public static final ExactRotation
        mONE_QUARTER = create(-1,4),
        ZERO = create(0,8),
	ONE_EIGHTH = create(1,8),
	ONE_QUARTER = create(2,8),
	THREE_EIGHTHS = create(3,8),
	ONE_HALF = create(4,8),
	FIVE_EIGHTHS = create(5,8),
	THREE_QUARTERS = create(6,8),
	SEVEN_EIGHTHS = create(7,8),
	ONE = create(8,8);
    /** Common absolute rotations. */
    public static final ExactRotation
        NORTH=ZERO, EAST=ONE_QUARTER, SOUTH=ONE_HALF, WEST=THREE_QUARTERS;
    /** A list of rotations in 1/8 turn increments. */
    private static ExactRotation[] eighths = new ExactRotation[]
	{ ZERO, ONE_EIGHTH, ONE_QUARTER, THREE_EIGHTHS,
	  ONE_HALF, FIVE_EIGHTHS, THREE_QUARTERS, SEVEN_EIGHTHS };
    /** Names for the rotations in the <code>eighths</code> list. */
    private static String[] eighthNames = new String[]
	{ "n", "ne", "e", "se", "s", "sw", "w", "nw" };
    
    /** Return the X offset of a one-unit step in the rotation direction.
     *  Zero indicates north (towards positive y).  Use a 'squared off'
     *  circle to avoid irrational numbers.  This is roughly equivalent to
     *  sin(amount).
     * @see ExactRotation#toY()
     * @doc.test
     *  js> importPackage(net.cscott.sdr.util);
     *  js> for (n=-8; n<20; n++) {
     *    >  er = new ExactRotation(Fraction.valueOf(n, 16));
     *    >  print("toX("+er+")="+er.toX().toProperString());
     *    > }; undefined
     *  toX(-1/2)=0
     *  toX(-7/16)=-1/2
     *  toX(-3/8)=-1
     *  toX(-5/16)=-1
     *  toX(-1/4)=-1
     *  toX(-3/16)=-1
     *  toX(-1/8)=-1
     *  toX(-1/16)=-1/2
     *  toX(0)=0
     *  toX(1/16)=1/2
     *  toX(1/8)=1
     *  toX(3/16)=1
     *  toX(1/4)=1
     *  toX(5/16)=1
     *  toX(3/8)=1
     *  toX(7/16)=1/2
     *  toX(1/2)=0
     *  toX(9/16)=-1/2
     *  toX(5/8)=-1
     *  toX(11/16)=-1
     *  toX(3/4)=-1
     *  toX(13/16)=-1
     *  toX(7/8)=-1
     *  toX(15/16)=-1/2
     *  toX(1)=0
     *  toX(1 1/16)=1/2
     *  toX(1 1/8)=1
     *  toX(1 3/16)=1
     */
    public Fraction toX() {
	Fraction EIGHT = Fraction.valueOf(8,1);
	ExactRotation r = normalize();
	// 7/8 to 1/8 range from -1 to 1
	// 1/8 to 3/8 x=1
	// 3/8 to 5/8 range from 1 to -1
	// 5/8 to 7/8 x=-1
	if (r.compareTo(ONE_EIGHTH) < 0)
	    return r.amount.multiply(EIGHT);
	if (r.compareTo(THREE_EIGHTHS) < 0)
	    return Fraction.ONE;
	if (r.compareTo(FIVE_EIGHTHS) < 0)
	    return Fraction.ONE_HALF.subtract(r.amount).multiply(EIGHT);
	if (r.compareTo(SEVEN_EIGHTHS) < 0)
	    return Fraction.ONE.negate();
	else
	    return r.amount.subtract(Fraction.ONE).multiply(EIGHT);
    }
    /** Return the Y offset of a one-unit step in the rotation direction.
     *  Zero indicates north (towards positive y).  Use a 'squared off'
     *  circle to avoid irrational numbers. This is roughly equivalent to
     *  cos(amt).
     * @see ExactRotation#toX()
     * @doc.test
     *  js> importPackage(net.cscott.sdr.util);
     *  js> for (n=-8; n<20; n++) {
     *    >  er = new ExactRotation(Fraction.valueOf(n, 16));
     *    >  print("toY("+er+")="+er.toY().toProperString());
     *    > }; undefined
     *  toY(-1/2)=-1
     *  toY(-7/16)=-1
     *  toY(-3/8)=-1
     *  toY(-5/16)=-1/2
     *  toY(-1/4)=0
     *  toY(-3/16)=1/2
     *  toY(-1/8)=1
     *  toY(-1/16)=1
     *  toY(0)=1
     *  toY(1/16)=1
     *  toY(1/8)=1
     *  toY(3/16)=1/2
     *  toY(1/4)=0
     *  toY(5/16)=-1/2
     *  toY(3/8)=-1
     *  toY(7/16)=-1
     *  toY(1/2)=-1
     *  toY(9/16)=-1
     *  toY(5/8)=-1
     *  toY(11/16)=-1/2
     *  toY(3/4)=0
     *  toY(13/16)=1/2
     *  toY(7/8)=1
     *  toY(15/16)=1
     *  toY(1)=1
     *  toY(1 1/16)=1
     *  toY(1 1/8)=1
     *  toY(1 3/16)=1/2
     */
    public Fraction toY() {
	Fraction EIGHT = Fraction.valueOf(8,1);
	ExactRotation r = normalize();
	// 7/8 to 1/8 y=1
	// 1/8 to 3/8 range from 1 to -1
	// 3/8 to 5/8 y=-1
	// 5/8 to 7/8 range from -1 to 1
	if (r.compareTo(ONE_EIGHTH) < 0)
	    return Fraction.ONE;
	if (r.compareTo(THREE_EIGHTHS) < 0)
	    return Fraction.ONE_QUARTER.subtract(r.amount).multiply(EIGHT);
	if (r.compareTo(FIVE_EIGHTHS) < 0)
	    return Fraction.ONE.negate();
	if (r.compareTo(SEVEN_EIGHTHS) < 0)
	    return r.amount.subtract(Fraction.THREE_QUARTERS).multiply(EIGHT);
	else
	    return Fraction.ONE;
    }
    /** Convert an x/y displacement to a rotation, using our 'squared off'
     * circle.  Roughly equivalent to atan2().
     */
    public static ExactRotation fromXY(Fraction x, Fraction y) {
        assert !(x.equals(Fraction.ZERO) && y.equals(Fraction.ZERO));
        if (x.abs().compareTo(y.abs()) >= 0) {
            // use atan: y/x
            Fraction yOverX = y.divide(x);
            assert yOverX.compareTo(Fraction.mONE) >= 0;
            assert yOverX.compareTo(Fraction.ONE) <= 0;
            Fraction r = Fraction.ONE_QUARTER.subtract
                (yOverX.multiply(Fraction.ONE_EIGHTH));
            if (x.compareTo(Fraction.ZERO) < 0) {
                r = r.add(Fraction.ONE_HALF);
            }
            return new ExactRotation(r);
        } else {
            // use acot: x/y
            // actually, we'll get the rotation of (y,-x) and then
            // rotate ccw 90 degrees.
            ExactRotation er = fromXY(y,x.negate());
            return er.subtract(Fraction.ONE_QUARTER).normalize();
        }
    }
    /** Return true if rotating from <code>from</code> to <code>to</code>
     *  is a clockwise movement.
     * @deprecated don't really work on normalized rotations */
    // XXX careful: doesn't really work on normalized rotations
    static boolean isCW(ExactRotation from, ExactRotation to) {
	return from.compareTo(to) < 0;
    }
    /** Return true if rotating from <code>from</code> to <code>to</code>
     *  is a counter-clockwise movement.
     * @deprecated don't really work on normalized rotations */
    // XXX careful: doesn't really work on normalized rotations
    static boolean isCCW(ExactRotation from, ExactRotation to) {
	return from.compareTo(to) > 0;
    }
    @Override
    public String toString() {
        return this.amount.toProperString();
    }
    /** Convert rotation to an appropriate ascii-art representation.
     *  The character '.' is used for "unrepresentable rotations".
     *  The southeast and northwest characters are a little funny.
     * @doc.test Show rotations going CW from north:
     *  js> function c2s(c) { return String.fromCharCode(c) }
     *  js> [c2s(r.toDiagramChar()) for each (r in 
     *    >  [ExactRotation.NORTH, ExactRotation.EAST,
     *    >   ExactRotation.SOUTH, ExactRotation.WEST])]
     *  ^,>,v,<
     *  js> [c2s(r.toDiagramChar()) for each (r in 
     *    >  [ExactRotation.ONE_EIGHTH, ExactRotation.THREE_EIGHTHS,
     *    >   ExactRotation.FIVE_EIGHTHS, ExactRotation.SEVEN_EIGHTHS])]
     *  7,Q,L,`
     *  js> c2s(new ExactRotation(net.cscott.sdr.util.Fraction.valueOf(1,3))
     *    >     .toDiagramChar())
     *  .
     */
    @Override
    public char toDiagramChar() {
	// we have special character for exact rotations aligned to eighths
	Fraction f = normalize().amount.multiply(Fraction.valueOf(8));
	if (f.getProperNumerator() == 0)
	    switch (f.floor()) {
	    case 0: return '^'; // north
	    case 1: return '7'; // northeast
	    case 2: return '>'; // east
	    case 3: return 'Q'; // southeast
	    case 4: return 'v'; // south
	    case 5: return 'L'; // southwest
	    case 6: return '<'; // west
	    case 7: return '`'; // northwest
	    default: assert false : "impossible!";
	    }
	return '.'; // unrepresentable
    }
}
