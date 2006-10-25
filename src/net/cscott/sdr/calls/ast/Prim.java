package net.cscott.sdr.calls.ast;

import static net.cscott.sdr.calls.transform.AstTokenTypes.PRIM;
import net.cscott.sdr.calls.ExactRotation;
import net.cscott.sdr.calls.transform.TransformVisitor;
import net.cscott.sdr.calls.transform.ValueVisitor;
import net.cscott.sdr.util.Fraction;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * A Prim represents a primitive action: a certain distance travelled
 * forward and to the side, while rotating a certain amount, performed
 * in a certain number of beats.  PRIM is a leaf node in a our AST.
 * @author C. Scott Ananian
 * @version $Id: Prim.java,v 1.10 2006-10-25 20:39:36 cananian Exp $
 */
public class Prim extends SeqCall {
    public static enum Direction { ASIS, IN, OUT; }
    public final Fraction x, y;
    public final ExactRotation rot;
    public final Direction dirX, dirY, dirRot;
    public final Fraction time;
    public final boolean passRight;
    public Prim(Direction dirX, Fraction x, Direction dirY, Fraction y,
            Direction dirRot, ExactRotation rot) {
        this(dirX, x, dirY, y, dirRot, rot, Fraction.ONE, true);
    }
    public Prim(Direction dirX, Fraction x, Direction dirY, Fraction y,
            Direction dirRot, ExactRotation rot, Fraction time, boolean passRight) {
        super(PRIM);
        this.x = x; this.y = y; this.rot = rot; this.time = time;
        this.dirX = dirX; this.dirY = dirY; this.dirRot = dirRot;
        this.passRight = passRight;
    }
    public static final Prim STAND_STILL =
        new Prim(Direction.ASIS,Fraction.ZERO, Direction.ASIS,Fraction.ZERO,
                Direction.ASIS,ExactRotation.ZERO);
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
        return x.toProperString()+" "+y.toProperString()+
            " "+rot.toRelativeString()+" "+time.toProperString();
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
            .append(passRight, p.passRight)
            .isEquals();
    }
    @Override
    public int hashCode() {
        if (hashCode==0)
            hashCode = new HashCodeBuilder()
            .append(x).append(y).append(rot).append(time)
            .append(dirX).append(dirY).append(dirRot).append(passRight)
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
                time.multiply(f), passRight);
    }
    /** Factory: creates new Prim only if it would differ from this. */
    public Prim build(Direction dirX, Fraction x, Direction dirY, Fraction y,
            Direction dirRot, ExactRotation rot, Fraction time, boolean passRight) {
        Prim p = new Prim(dirX, x, dirY, y, dirRot, rot, time, passRight);
        if (this.equals(p)) return this;
        return p;
    }
}
