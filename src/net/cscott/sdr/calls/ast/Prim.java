package net.cscott.sdr.calls.ast;

import static net.cscott.sdr.calls.ast.TokenTypes.PRIM;
import net.cscott.sdr.calls.Position;
import net.cscott.sdr.calls.Rotation;
import net.cscott.sdr.calls.Warp;
import net.cscott.sdr.util.Fraction;

/**
 * A Prim represents a primitive action: a certain distance travelled
 * forward and to the side, while rotating a certain amount, performed
 * in a certain number of beats.  PRIM is a leaf node in a our AST.
 * @author C. Scott Ananian
 * @version $Id: Prim.java,v 1.3 2006-10-10 18:57:30 cananian Exp $
 */
public class Prim extends SeqCall {
    public final Fraction x, y;
    public final Rotation rot;
    public final Fraction time;
    public Prim(Fraction x, Fraction y, Rotation rot, Fraction time) {
        super(PRIM);
        this.x = x; this.y = y; this.rot = rot; this.time = time;
    }
    public String toString() {
        return "("+super.toString()+" "+x.toProperString()+" "+y.toProperString()+
            " "+rot.toRelativeString()+" "+time.toProperString()+")";
    }
    
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
        // XXX: extract x and y from wFrom and wTo
        Rotation nRot = wTo.facing.subtract(wFrom.facing.amount);
        return null; //XXX
    }
}
