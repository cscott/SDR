package net.cscott.sdr.calls.ast;

import static net.cscott.sdr.calls.ast.TokenTypes.WARP;
import net.cscott.sdr.calls.Warp;

/** <code>Warped</code> transforms the coordinate space of its child.
 * For example, a warped "right pull by" might become a "left pull by".
 * @author C. Scott Ananian
 * @version $Id: Warped.java,v 1.2 2006-10-11 15:39:22 cananian Exp $
 */
public class Warped extends Comp {
    public final Warp warp;
    public final Comp child;
    
    public Warped(Warp warp, Comp child) {
        super(WARP);
        this.warp = warp;
        this.child = child;
        addChild(child);
    }
    @Override
    public String toString() {
        return super.toString()+" "+warp;
    }
}
