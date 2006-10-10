package net.cscott.sdr.calls;

import net.cscott.sdr.util.Fraction;

/** Rotations are represented as fractions, where '0' is facing north,
 *  and '1/4' is facing east.  We use getProperNumerator() to find
 *  the (mod 1) rotation amount. */
public class Rotation implements Comparable<Rotation> {
    /** The amount of the rotation. */
    public final Fraction amount;
    /** Create a rotation of n/d of a complete rotation, where positive
     *  denotes clockwise rotation. */
    private Rotation(int numerator, int denominator) {
	this(Fraction.valueOf(numerator, denominator));
    }
    /** Private constructor from a <code>Fraction</code> object. */
    private Rotation(Fraction amount) {
	this.amount = amount;
    }
    /** Add the given amount to this rotation direction. */
    public Rotation add(Fraction f) {
	return new Rotation(this.amount.add(f));
    }
    /** Negate this rotation (mirror image). */
    public Rotation negate() {
        return new Rotation(this.amount.negate());
    }
    /** Normalize rotation to the range 0-1. */
    public Rotation normalize() {
	Fraction dir = Fraction.valueOf
	    (amount.getProperNumerator(), amount.getDenominator());
	if (amount.compareTo(Fraction.ZERO) < 0)
	    dir = Fraction.ONE.subtract(dir);
	return new Rotation(dir);
    }
    /** Compare unnormalized rotation amounts. */
    public int compareTo(Rotation r) {
	return this.amount.compareTo(r.amount);
    }
    /** Rotations are equal if their normalized values are equal. */
    public boolean equals(Object o) {
	if (!(o instanceof Rotation)) return false;
	return normalize().amount.equals(((Rotation)o).normalize().amount);
    }
    /** Hashcode of the normalized value. */
    public int hashCode() {
	return normalize().amount.hashCode();
    }
    /** Returns a human-readable description of the rotation.  The output
     *  is a valid input to <code>Rotation.valueOf(String)</code>. */
    public String toString() {
	return this.amount.toProperString();
    }
    /** Returns a human-readable description of the rotation.  The output
     *  is a valid input to <code>Rotation.fromAbsoluteString(String)</code>. */
    public String toAbsoluteString() {
        for (int i=0; i<eighths.length; i++)
            if (this.equals(eighths[i]))
                return eighthNames[i];
        return toString();
    }
    /** Returns a human-readable description of the rotation.  The output
     *  is a valid input to <code>Rotation.fromAbsoluteString(String)</code>. */
    public String toRelativeString() {
        if (Rotation.ONE_QUARTER.equals(this)) return "right";
        if (Rotation.ZERO.equals(this)) return "none";
        if (Rotation.mONE_QUARTER.equals(this)) return "left";
        return toString();
    }
    /** Converts a string (one of n/s/e/w, ne/nw/se/sw) to the
     * appropriate rotation object. 'n' is facing the caller.
     * The string 'o' can be given; it represents 'Rotation unspecified'
     * and <code>null</code> will be returned. */
    public static Rotation fromAbsoluteString(String s) {
	for (int i=0; i<eighthNames.length; i++)
	    if (eighthNames[i].equalsIgnoreCase(s))
		return eighths[i];
	if (s.equalsIgnoreCase("o")) return null; // unspecified rotation
	return new Rotation(Fraction.valueOf(s));
    }
    /** Returns a Rotation corresponding to one of the strings "right", "left",
     * or "none".
     * @param s
     * @return Rotation.ZERO if s is "none", Rotation.ONE_QUARTER if s is
     *  "right", or Rotation.mONE_QUARTER if s is "left".
     * @throws IllegalArgumentException if s is not one of "right", "left", 
     *  "none", or a number.
     */
    public static Rotation fromRelativeString(String s) {
        s = s.intern();
        if (s=="right") return Rotation.ONE_QUARTER;
        if (s=="none") return Rotation.ZERO;
        if (s=="left") return Rotation.mONE_QUARTER;
        return new Rotation(Fraction.valueOf(s));
    }
    /** Common rotations. */
    public static final Rotation
        mONE_QUARTER = new Rotation(-1,4),
        ZERO = new Rotation(0,8),
	ONE_EIGHTH = new Rotation(1,8),
	ONE_QUARTER = new Rotation(2,8),
	THREE_EIGHTHS = new Rotation(3,8),
	ONE_HALF = new Rotation(4,8),
	FIVE_EIGHTHS = new Rotation(5,8),
	THREE_QUARTERS = new Rotation(6,8),
	SEVEN_EIGHTHS = new Rotation(7,8),
	ONE = new Rotation(8,8);
    /** A list of rotations in 1/8 turn increments. */
    private static Rotation[] eighths = new Rotation[]
	{ ZERO, ONE_EIGHTH, ONE_QUARTER, THREE_EIGHTHS,
	  ONE_HALF, FIVE_EIGHTHS, THREE_QUARTERS, SEVEN_EIGHTHS };
    /** Names for the rotations in the <code>eighths</code> list. */
    private static String[] eighthNames = new String[]
	{ "n", "ne", "e", "se", "s", "sw", "w", "nw" };
    
    /** Return the X offset of a one-unit step in the rotation direction.
     *  Zero indicates north (towards positive y).  Use a 'squared off'
     *  circle to avoid irrational numbers. */
    public Fraction toX() {
	Fraction EIGHT = Fraction.valueOf(8,1);
	Rotation r = normalize();
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
     *  circle to avoid irrational numbers. */
    public Fraction toY() {
	Fraction EIGHT = Fraction.valueOf(8,1);
	Rotation r = normalize();
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
    /** Return true if rotating from <code>from</code> to <code>to</code>
     *  is a clockwise movement. */
    static boolean isCW(Rotation from, Rotation to) {
	return from.compareTo(to) < 0;
    }
    /** Return true if rotating from <code>from</code> to <code>to</code>
     *  is a counter-clockwise movement. */
    static boolean isCCW(Rotation from, Rotation to) {
	return from.compareTo(to) > 0;
    }
}
