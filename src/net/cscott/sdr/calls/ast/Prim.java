package net.cscott.sdr.calls.ast;

import static net.cscott.sdr.calls.CallFileParserTokenTypes.PRIM;
import net.cscott.sdr.calls.Rotation;
import net.cscott.sdr.util.Fraction;

/**
 * A Prim represents a primitive action: a certain distance travelled
 * forward and to the side, while rotating a certain amount, performed
 * in a certain number of beats.  PRIM is a leaf node in a our AST.
 * @author C. Scott Ananian
 * @version $Id: Prim.java,v 1.1 2006-10-09 19:57:12 cananian Exp $
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
}
