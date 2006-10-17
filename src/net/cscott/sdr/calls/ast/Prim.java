package net.cscott.sdr.calls.ast;

import static net.cscott.sdr.calls.transform.AstTokenTypes.PRIM;
import net.cscott.sdr.calls.Position;
import net.cscott.sdr.calls.Rotation;
import net.cscott.sdr.calls.Warp;
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
 * @version $Id: Prim.java,v 1.7 2006-10-17 16:29:05 cananian Exp $
 */
public class Prim extends SeqCall {
    public final Fraction x, y;
    public final Rotation rot;
    public final Fraction time;
    public final boolean passRight;
    public Prim(Fraction x, Fraction y, Rotation rot, Fraction time) {
        super(PRIM);
        this.x = x; this.y = y; this.rot = rot; this.time = time;
        this.passRight = true;
    }
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
            .append(x, p.x)
            .append(y, p.y)
            .append(rot, p.rot)
            .append(time, p.time)
            .isEquals();
    }
    @Override
    public int hashCode() {
        if (hashCode==0)
            hashCode = new HashCodeBuilder()
            .append(x).append(y).append(rot).append(time)
            .toHashCode();
        return hashCode;
    }
    private transient int hashCode = 0;
    
    // Factory methods
    /** Create a new Prim, identical to this one except that the time
     * is scaled by the given fraction. */
    public Prim scale(Fraction f) {
        if (Fraction.ONE.equals(f)) return this;
        return new Prim(x, y, rot, time.multiply(f));
    }
    /** Apply a given Warp to this Prim: the given 'from' must be the
     * unwarped absolute position from which this prim begins, since
     * Warps are absolute while Prim coordinates are relative.
     * @param from  unwarped absolute position at which this Prim begins
     * @param w  the warp to apply
     * @param time  the warp-relative time (0-1)
     * @return a new Prim, which will, when started from w.warp(from,time)
     *   end up a w.warp(to, time), where 'to' is where the prim would have
     *   ended if started from 'from'.
     */
    public Prim warp(Position from, Warp w, Fraction time) {
        Position to = from.forwardStep(y).sideStep(x).rotate(rot.amount);
        Position wFrom = w.warp(from, time);
        Position wTo = w.warp(to, time);
        // wTo - wFrom
        Fraction wX = wTo.x.subtract(wFrom.x);
        Fraction wY = wTo.y.subtract(wFrom.y);
        // get x,y components of the warped 'from' facing dir vector.
        Fraction rX = wFrom.facing.toX(); // x, y components of facing dir vector
        Fraction rY = wFrom.facing.toY();
        // now (rX,rY) is a 'forward step' (ie, y) and (rY,-rX) is a 'side step' (x)
        // use dot product to project (wTo-wFrom) onto these vectors.
        Fraction nX = wX.multiply(rY).subtract(wY.multiply(rX));
        Fraction nY = wX.multiply(rX).add(wY.multiply(rY));
        // in comparison, deriving the new rotation direction is easy
        Rotation nRot = wTo.facing.subtract(wFrom.facing.amount);
        // okay, make the result!
        Prim p = new Prim(nX,nY,nRot,this.time);
        // return old object if results were identical
        return (this.equals(p)) ? this : p;
    }
    /** Factory: creates new Prim only if it would differ from this. */
    public Prim build(Fraction x, Fraction y, Rotation rot, Fraction time) {
        Prim p = new Prim(x,y,rot,time);
        if (this.equals(p)) return this;
        return p;
    }
}
