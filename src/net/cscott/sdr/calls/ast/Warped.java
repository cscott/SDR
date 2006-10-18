package net.cscott.sdr.calls.ast;

import static net.cscott.sdr.calls.transform.AstTokenTypes.WARP;
import net.cscott.sdr.calls.Warp;
import net.cscott.sdr.calls.transform.TransformVisitor;
import net.cscott.sdr.calls.transform.ValueVisitor;

/** <code>Warped</code> transforms the coordinate space of its child.
 * For example, a warped "right pull by" might become a "left pull by".
 * @author C. Scott Ananian
 * @version $Id: Warped.java,v 1.5 2006-10-18 01:55:00 cananian Exp $
 */
public class Warped extends Comp {
    public final Warp warp;
    public final Comp child;
    
    public Warped(Warp warp, Comp child) {
        super(WARP);
        this.warp = warp;
        this.child = child;
    }
    @Override
    public <T> Comp accept(TransformVisitor<T> v, T t) {
        return v.visit(this, t);
    }
    @Override
    public <RESULT,CLOSURE>
    RESULT accept(ValueVisitor<RESULT,CLOSURE> v, CLOSURE cl) {
        return v.visit(this, cl);
    }
    /** Factory: creates new If only if it would differ from this. */
    public Warped build(Warp warp, Comp child) {
        if (this.warp.equals(warp) && this.child==child)
            return this;
        return new Warped(warp, child);
    }
    @Override
    public String argsToString() {
        return warp+" "+child;
    }
}
