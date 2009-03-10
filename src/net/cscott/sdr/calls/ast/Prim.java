package net.cscott.sdr.calls.ast;

import static net.cscott.sdr.calls.transform.AstTokenTypes.PRIM;
import net.cscott.sdr.calls.ExactRotation;
import net.cscott.sdr.calls.transform.TransformVisitor;
import net.cscott.sdr.calls.transform.ValueVisitor;
import net.cscott.sdr.util.Fraction;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * A Prim represents a primitive action: a certain distance travelled
 * forward and to the side, while rotating a certain amount, performed
 * in a certain number of beats.  PRIM is a leaf node in a our AST.
 * @author C. Scott Ananian
 * @version $Id: Prim.java,v 1.12 2007-03-07 19:56:21 cananian Exp $
 */
public class Prim extends SeqCall {
    /** The {@link Direction} enumeration tells whether the movement
     * is relative to the center of the formation. */
    public static enum Direction {
        /** Directions are not relative to formation: positive is
         *  right/forward/cw; negative is left/backward/ccw. */
        ASIS,
        /** Directions are relative to formation: positive is "toward the
         * center" and negative is "away from the center". */
        IN,
    };
    public static enum Flag {
	/** This flag indicates whether this motion involves a right
	 * shoulder pass (as most motions do, including "cross" calls where the
	 * crossers start far apart) or else a left shoulder pass
	 * ("cross" calls which start with adjacent crossers).
	 */
	PASS_LEFT,
	/** The {@link #FORCE_ARC} flag helps distinguish between (say)
	 * "pass thru and quarter in" and "split counter rotate 1/4".  Both
	 * of these involve traveling forward and turning, but the latter
	 * is an arcing motion (hence {@link #FORCE_ARC} would be set) while
	 * the former is a straight line path (hence {@link #FORCE_ARC} would
	 * not be set).
	 */
	FORCE_ARC,
	/**
	 * Set the roll direction to the right after this Prim, even
	 * if the movement doesn't call for it.  (Useful in circle
	 * left definition, left cross chain thru, etc.)
	 */
	FORCE_ROLL_RIGHT,
	/**
	 * Set the roll direction to the left after this Prim, even
	 * if the movement doesn't call for it.  (Useful in circle
	 * right definition, cross chain thru, etc.)
	 */
	FORCE_ROLL_LEFT,
	/**
	 * In a do-sa-do, you don't join hands at the 1/4 and 3/4
	 * marks, even though you are adjacent.
	 */
	NO_HANDS,
	/**
	 * Heaven help me, someday I might implement "ladies in, men sashay."
	 */
	SKIRT_WORK;

	/**
	 * Returns true iff the given string names a valid {@link Prim.Flag}.
	 */
	public static boolean contains(String s) {
	    for (Flag f : values())
		if (f.name().equals(s)) return true;
	    return false;
	}
	/**
	 * Normalize a {@link Prim.Flag} name: convert to uppercase, and
	 * convert dashes to underscores.
	 */
	public static String canon(String s) {
	    return s.toUpperCase().replace('-','_');
	}
    };
    /** Amount of movement in the 'sashay' direction (dancer's right/left). */
    public final Fraction x;
    /** Amount of movement in the 'walking' direction (dancer forward and back). */
    public final Fraction y;
    /** Amount of rotation. */
    public final ExactRotation rot;
    /** Is the movement direction relative to the center of the formation? */
    public final Direction dirX, dirY, dirRot;
    /** The number of beats which this motion should take. */
    public final Fraction time;
    /** Flags refining this motion. */
    public final Set<Flag> flags;

    public Prim(Direction dirX, Fraction x, Direction dirY, Fraction y,
		Direction dirRot, ExactRotation rot, Fraction time,
		Flag... flags) {
        super(PRIM);
        this.x = x; this.y = y; this.rot = rot; this.time = time;
        this.dirX = dirX; this.dirY = dirY; this.dirRot = dirRot;
	// somewhat awkward initialization of read-only this.flags
	Set<Flag> es = EnumSet.noneOf(Flag.class);
	es.addAll(Arrays.asList(flags));
	this.flags = Collections.unmodifiableSet(es);
	// sanity-checking
	assert !(this.flags.contains(Flag.FORCE_ROLL_RIGHT) &&
		 this.flags.contains(Flag.FORCE_ROLL_LEFT)) :
	       "FORCE_ROLL_RIGHT and FORCE_ROLL_LEFT are exclusive.";
    }
    public static final Prim STAND_STILL =
        new Prim(Direction.ASIS,Fraction.ZERO, Direction.ASIS,Fraction.ZERO,
		 Direction.ASIS,ExactRotation.ZERO, Fraction.ONE);
    // support visitor
    @Override
    public <T> SeqCall accept(TransformVisitor<T> v, T t) {
        return v.visit(this, t);
    }
    @Override
    public <RESULT,CLOSURE>
    RESULT accept(ValueVisitor<RESULT,CLOSURE> v, CLOSURE cl) {
        return v.visit(this, cl);
    }
    // utility methods
    @Override
    public String argsToString() {
	StringBuilder sb = new StringBuilder();
	if (dirX==Direction.IN) sb.append("in ");
	sb.append(x.toProperString()); sb.append(", ");
	if (dirY==Direction.IN) sb.append("in ");
	sb.append(y.toProperString()); sb.append(", ");
	if (dirRot==Direction.IN) {
	    sb.append("in "); sb.append(rot.toString());
	} else {
	    sb.append(rot.toRelativeString());
	}
	sb.append(", ");
	sb.append(time.toProperString());
	for (Flag f: flags) {
	    sb.append(", ");
	    sb.append(f);
	}
	return sb.toString();
    }
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Prim)) return false;
        Prim p = (Prim) o;
        return new EqualsBuilder()
            .append(dirX, p.dirX)
            .append(x, p.x)
            .append(dirY, p.dirY)
            .append(y, p.y)
            .append(dirRot, p.dirRot)
            .append(rot, p.rot)
            .append(time, p.time)
	    .append(flags, p.flags)
            .isEquals();
    }
    @Override
    public int hashCode() {
        if (hashCode==0)
            hashCode = new HashCodeBuilder()
            .append(x).append(y).append(rot).append(time)
            .append(dirX).append(dirY).append(dirRot)
	    .append(flags)
            .toHashCode();
        return hashCode;
    }
    private transient int hashCode = 0;
    
    // Factory methods
    /** Create a new Prim, identical to this one except that the time
     * is scaled by the given fraction. */
    public Prim scaleTime(Fraction f) {
        if (Fraction.ONE.equals(f)) return this;
        return new Prim(dirX, x, dirY, y, dirRot, rot,
			time.multiply(f),
			flags.toArray(new Flag[flags.size()]));
    }
    /** Factory: creates new Prim only if it would differ from this. */
    public Prim build(Direction dirX, Fraction x, Direction dirY, Fraction y,
		      Direction dirRot, ExactRotation rot, Fraction time,
		      Flag... flags) {
        Prim p = new Prim(dirX, x, dirY, y, dirRot, rot, time, flags);
        if (this.equals(p)) return this;
        return p;
    }
    public static AstNode valueOf(String s) throws IllegalArgumentException {
	try {
	    return new AstParser(s).prim();
	} catch (org.antlr.runtime.RecognitionException e) {
	    throw new IllegalArgumentException("Bad Prim: "+s);
	}
    }
}
