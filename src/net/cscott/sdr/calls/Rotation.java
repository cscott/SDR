package net.cscott.sdr.calls;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.junit.runner.RunWith;

import net.cscott.jdoctest.JDoctestRunner;
import net.cscott.jutil.UnmodifiableIterator;
import net.cscott.sdr.util.Fraction;
import static net.cscott.sdr.util.Tools.l;

/** Rotations are represented as fractions, where '0' is facing north
 *  (that is, away from the caller),
 *  and '1/4' is facing east.  The also have a 'modulus', since they can
 *  represent "general" directions.  For example, "1/4 modulo 1/2" means
 *  facing east or west (but not north, south, or any other direction).
 *  A rotation modulo 0 matches any direction.  A rotation modulo 1
 *  indicates an 'exact' rotation; the modulus can not exceed 1. */
@RunWith(value=JDoctestRunner.class)
public class Rotation {
    /** The amount of the rotation. */
    public final Fraction amount;
    /** The 'modulus' of the rotation: indicates the amount of uncertainty
     * in the direction.  The modulus cannot exceed 1. */
    public final Fraction modulus;
    
    /** Private constructor from a <code>Fraction</code> object. */
    protected Rotation(Fraction amount, Fraction modulo) {
	this.amount = amount;  this.modulus = modulo;
	assert this.modulus.compareTo(Fraction.ONE)==0 ?
            this instanceof ExactRotation : true;
        assert this.modulus.compareTo(Fraction.ONE) <= 0;
        assert this.modulus.compareTo(Fraction.ZERO) >= 0;
        assert this.modulus.getNumerator()==0 || this.modulus.getNumerator()==1;
        assert this.modulus.compareTo(Fraction.ZERO)==0 ?
                this.amount.compareTo(Fraction.ZERO)==0 : true;
    }
    public static final Rotation create(Fraction amount, Fraction modulo) {
        // Effective modulus is always 1/N for some N -- reduce fraction to
        // lowest terms, then use denom.  For example, a modulus of 2/3 is
        // equivalent to 1/3, since the sequence goes:
        //            2/3, 4/3 == 1/3 mod 1, 6/3 == 0 mod 1.
        if (modulo.equals(Fraction.ZERO))
            amount = Fraction.ZERO;
        else
            modulo = Fraction.valueOf(1, modulo.getDenominator());
        if (modulo.equals(Fraction.ONE)) {
            // avoid creating new objects for common values
            switch (amount.getDenominator()) {
            case 1:
                switch (amount.getNumerator()) {
                case 0: return ExactRotation.ZERO;
                case 1: return ExactRotation.ONE;
                default: break;
                }
                break;
            case 2:
                switch (amount.getNumerator()) {
                case 1: return ExactRotation.ONE_HALF;
                default: break;
                }
                break;
            case 4:
                switch (amount.getNumerator()) {
                case -1: return ExactRotation.mONE_QUARTER;
                case  1: return ExactRotation.ONE_QUARTER;
                case  3: return ExactRotation.THREE_QUARTERS;
                default: break;
                }
                break;
            case 8:
                switch (amount.getNumerator()) {
                case 1: return ExactRotation.ONE_EIGHTH;
                case 3: return ExactRotation.THREE_EIGHTHS;
                case 5: return ExactRotation.FIVE_EIGHTHS;
                case 7: return ExactRotation.SEVEN_EIGHTHS;
                default: break;
                }
                break;
            }
            return new ExactRotation(amount);
        } else {
            return new Rotation(amount, modulo);
        }
    }
    /** Return true iff this rotation is exact (that is, if the modulus is
     *  one). */
    public boolean isExact() {
        assert !this.modulus.equals(Fraction.ONE);
        return false;
    }
    /** Add the given amount to this rotation direction. */
    public Rotation add(Fraction f) {
	return create(this.amount.add(f), this.modulus);
    }
    /** Subtract the given amount from this rotation direction. */
    public Rotation subtract(Fraction f) {
        return create(this.amount.subtract(f), this.modulus);
    }
    /** Negate this rotation (mirror image). */
    public Rotation negate() {
        return create(this.amount.negate(), this.modulus);
    }
    /** Normalize rotation to the range [0, modulus). */
    public Rotation normalize() {
        if (this.modulus.compareTo(Fraction.ZERO)==0) {
            if (this.amount.compareTo(Fraction.ZERO)==0)
                return this;
            assert false : "we shouldn't create other zeros";
            return create(Fraction.ZERO, Fraction.ZERO);
        }
        // make rotation positive.
        Fraction abs = this.amount;
        if (abs.compareTo(Fraction.ZERO) < 0)
	    abs = abs.subtract(Fraction.valueOf(abs.floor()));
        assert abs.compareTo(Fraction.ZERO) >= 0;
        // quick out
        Fraction f = abs;
        if (f.compareTo(this.modulus) >= 0) {
            // reduce by modulus.
            f = abs.divide(this.modulus);
            // just want the fractional part.
            f = f.subtract(Fraction.valueOf(f.floor()))
                 .multiply(this.modulus);
        }
        assert f.compareTo(Fraction.ZERO) >= 0 && f.compareTo(this.modulus) < 0;
        if (f==this.amount) return this; // quick out
        return create(f, this.modulus);
    }
    /** Rotations are equal iff their normalized rotation amount and
     * modulus are exactly equal. */
    @Override
    public boolean equals(Object o) {
        if (this==o) return true; // common case.
	if (!(o instanceof Rotation)) return false;
        Rotation r = (Rotation) o;
        if (!this.modulus.equals(r.modulus)) return false;
        return this.normalize().amount.equals(r.normalize().amount);
    }
    /** Hashcode of the normalized amount &amp; modulus. */
    @Override
    public int hashCode() {
        Rotation r = this.normalize();
	return 51 + r.amount.hashCode() + 7*r.modulus.hashCode();
    }
    /** Returns true iff all the rotations possible with the given {@code r}
     * are included within the set of rotations possible with {@code this}.
     * For example, the Rotation {@code 0 mod 1/4} (ie, north, east, south, or
     * west, but no intermediate directions) includes {@code 3/4 mod 1}
     * (ie, exactly west), but the reverse is not true: {@code 3/4 mod 1}
     * includes {@code 7/4 mod 1}, but does not include {@code 0 mod 1/4}.
     * Formally, returns true iff the congruence class of {@code this} is
     * a superset of the congruence class of {@code r}.
     * @doc.test Demonstrate the properties described above:
     *  js> importPackage(net.cscott.sdr.util)
     *  js> r1 = Rotation.create(Fraction.ZERO, Fraction.ONE_QUARTER)
     *  0 mod 1/4
     *  js> r2 = Rotation.create(Fraction.THREE_QUARTERS, Fraction.ONE)
     *  3/4
     *  js> r3 = Rotation.create(Fraction.valueOf(7, 4), Fraction.ONE)
     *  1 3/4
     *  js> r1.includes(r2)
     *  true
     *  js> r2.includes(r1)
     *  false
     *  js> r2.includes(r3)
     *  true
     */
    public boolean includes(Rotation r) {
        Rotation r1 = this.normalize(), r2 = r.normalize();
        // check for an exact match.
        if (r1.equals(r2)) return true; // exact match.
        // "all rotations" includes everything.
        if (r1.modulus.equals(Fraction.ZERO)) return true;
        // but nothing (other than "all rotations") includes "all rotations"
        if (r2.modulus.equals(Fraction.ZERO)) return false;
        // check that moduli are compatible: this.modulo < r.modulo, etc.
        if (r2.modulus.divide(r1.modulus).getProperNumerator() != 0)
            return false; // incompatible moduli
        assert r1.modulus.compareTo(r2.modulus) <= 0;
        r2 = create(r2.amount, r1.modulus).normalize();
        return r1.equals(r2);
    }
    /**
     * Returns an {@link Iterator} over the {@link ExactRotation}s included
     * in this {@link Rotation}.
     * @doc.test
     *  js> r = Rotation.fromAbsoluteString('+')
     *  0 mod 1/4
     *  js> [x for (x in Iterator(r.included()))]
     *  0,1/4,1/2,3/4
     *  js> r = Rotation.fromAbsoluteString('x')
     *  1/8 mod 1/4
     *  js> [x for (x in Iterator(r.included()))]
     *  1/8,3/8,5/8,7/8
     */
    public Collection<ExactRotation> included() {
        final Fraction start = this.normalize().amount;
        return new AbstractCollection<ExactRotation>() {
            @Override
            public Iterator<ExactRotation> iterator() {
                return new UnmodifiableIterator<ExactRotation>() {
                    Fraction next = start;
                    @Override
                    public boolean hasNext() {
                        return next.compareTo(Fraction.ONE) < 0;
                    }
                    @Override
                    public ExactRotation next() {
                        ExactRotation er = new ExactRotation(next);
                        next = next.add(Rotation.this.modulus);
                        return er;
                    }
                };
            }
            @Override
            public int size() {
                return Rotation.this.modulus.getDenominator();
            }
        };
    }
    /**
     * Return a {@link Rotation} which includes all the directions represented
     * by {@code this} and the specified {@link Rotation}.  The operation may
     * be inexact; that is, the result may include directions which are
     * not included in either of the arguments.
     * @doc.test Exact unions:
     *  js> ExactRotation.EAST.union(ExactRotation.WEST)
     *  1/4 mod 1/2
     *  js> ExactRotation.NORTH.union(ExactRotation.SOUTH)
     *  0 mod 1/2
     *  js> Rotation.fromAbsoluteString('|').union(
     *    > Rotation.fromAbsoluteString('-'))
     *  0 mod 1/4
     * @doc.test Inexact unions:
     *  js> importPackage(net.cscott.sdr.util) // for Fraction
     *  js> ExactRotation.NORTH.union(ExactRotation.EAST)
     *  0 mod 1/4
     *  js> Rotation.fromAbsoluteString('|').union(ExactRotation.EAST)
     *  0 mod 1/4
     *  js> Rotation.create(Fraction.ONE_QUARTER, Fraction.ONE_HALF).union(
     *    >                 ExactRotation.SOUTH)
     *  0 mod 1/4
     *  js> Rotation.create(Fraction.ZERO, Fraction.ONE_HALF).union(
     *    > Rotation.create(Fraction.ZERO, Fraction.ONE_THIRD))
     *  0 mod 1/6
     *  js> Rotation.create(Fraction.ZERO, Fraction.ONE_THIRD).union(
     *    >                 ExactRotation.SOUTH)
     *  0 mod 1/6
     *  js> Rotation.create(Fraction.ZERO, Fraction.ONE_THIRD).union(
     *    > Rotation.create(Fraction.ZERO, Fraction.ONE_QUARTER))
     *  0 mod 1/12
     *  js> Rotation.create(Fraction.ONE_EIGHTH, Fraction.ONE_QUARTER).union(
     *    >                 ExactRotation.SOUTH)
     *  0 mod 1/8
     */
    public Rotation union(Rotation r) {
        // easy case!
        if (this.includes(r))
            return this;
        // sort the first two ExactRotations in each Rotation
        Rotation a = this.normalize(), b = r.normalize();
        TreeSet<Fraction> rots =
            new TreeSet<Fraction>(l(a.amount, a.amount.add(a.modulus),
                                    b.amount, b.amount.add(b.modulus)));
        // amount is first, modulus is diff of third and second.
        Fraction first = rots.first(),
                 second = rots.higher(first),
                 third = rots.higher(second);
        return create(first, third.subtract(second)).normalize();
    }
    public static Rotation union(List<Rotation> rots) {
        Rotation r = null;
        for (Rotation rr : rots) {
            r = (r==null) ? rr : r.union(rr);
        }
        assert r!=null : "nothing to union";
        return r;
    }
    /** Returns a human-readable description of the rotation.  The output
     *  is a valid input to <code>ExactRotation.valueOf(String)</code>. */
    @Override
    public String toString() {
        return this.amount.toProperString()+" mod "+this.modulus.toProperString();
    }
    /** Handle special Rotation values; return null if unrepresentable. */
    private String _toDiagramString() {
        // this helper only works on normalized rotations.
        assert (this.modulus.compareTo(Fraction.ZERO) == 0) ||
            (this.amount.compareTo(Fraction.ZERO) >= 0 &&
             this.amount.compareTo(this.modulus) < 0) : this;
        if (this.modulus.compareTo(Fraction.ZERO)==0) return "o";
        else if (this.modulus.compareTo(Fraction.ONE_EIGHTH)==0) {
            if (this.amount.compareTo(Fraction.ZERO)==0) return "*";
        } else if (this.modulus.compareTo(Fraction.ONE_QUARTER)==0) {
            if (this.amount.compareTo(Fraction.ZERO)==0) return "+";
            else if (this.amount.compareTo(Fraction.ONE_EIGHTH)==0) return "x";
        } else if (this.modulus.compareTo(Fraction.ONE_HALF)==0) {
            if (this.amount.compareTo(Fraction.ZERO)==0) return "|";
            else if (this.amount.compareTo(Fraction.ONE_QUARTER)==0) return "-";
        } else if (this.modulus.compareTo(Fraction.ONE)==0)
            assert false: "we should have invoked an override in ExactRotation";
	return null;
    }

    /** Returns a human-readable description of the rotation, similar to the
     *  input to <code>ExactRotation.fromAbsoluteString(String)</code>. */
    public String toAbsoluteString() {
	String s = normalize()._toDiagramString();
        return s!=null ? s : toString();
    }
    /** Converts a string (one of n/s/e/w, ne/nw/se/sw) to the
     * appropriate rotation object. 'n' is facing away from the caller.
     * The string '-' means "east or west", and the string '|' means
     * "north or south".  The string "+" means "north, south, east, or west".
     * The string 'o' means "any rotation". */
    public static Rotation fromAbsoluteString(String s) {
        if (s.equals("|")) return create(Fraction.ZERO, Fraction.ONE_HALF);
        if (s.equals("-")) return create(Fraction.ONE_QUARTER, Fraction.ONE_HALF);
        if (s.equals("+")) return create(Fraction.ZERO, Fraction.ONE_QUARTER);
        if (s.equals("x")) return create(Fraction.ONE_EIGHTH, Fraction.ONE_QUARTER);
        if (s.equals("*")) return create(Fraction.ZERO, Fraction.ONE_EIGHTH);
        if (s.equalsIgnoreCase("o")) return create(Fraction.ZERO,Fraction.ZERO);
        return ExactRotation.fromAbsoluteString(s);
    }
    /** Convert rotation to an appropriate ascii-art representation.
     *  (Most of the interesting directions are in ExactRotation,
     *  not Rotation.)  The character '.' is used for "unrepresentable
     *  rotations".
     * @doc.test Show that fromAbsoluteString and toDiagramChar are inverse
     *  js> function m(s) {
     *    >   return String.fromCharCode(Rotation.fromAbsoluteString(s).toDiagramChar())
     *    > }
     *  js> Array.map("|-+xo", m)
     *  |,-,+,x,o
     * @doc.test Unrepresentable rotation:
     *  js> importPackage(net.cscott.sdr.util)
     *  js> r = Rotation.create(Fraction.ZERO, Fraction.ONE_THIRD)
     *  0 mod 1/3
     *  js> String.fromCharCode(r.toDiagramChar())
     *  .
     */
    public char toDiagramChar() {
	String s = normalize()._toDiagramString();
	if (s!=null) {
	    assert s.length()==1;
	    return s.charAt(0);
	}
	return '.'; // unrepresentable.
    }
    /** Return an executable representation of this {@link Rotation}. */
    public String repr() {
        StringBuilder sb = new StringBuilder();
        String s = normalize()._toDiagramString();
        if (s!=null) {
            sb.append("Rotation.fromAbsoluteString(\"");
            sb.append(s);
            sb.append("\")");
        } else {
            sb.append("Rotation.create(");
            sb.append(amount.repr());
            sb.append(",");
            sb.append(modulus.repr());
            sb.append(")");
        }
        return sb.toString();
    }
}
