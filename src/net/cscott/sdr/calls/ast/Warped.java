package net.cscott.sdr.calls.ast;

import static net.cscott.sdr.calls.ast.TokenTypes.WARP;
import antlr.collections.AST;
import net.cscott.sdr.calls.Warp;
import net.cscott.sdr.calls.transform.TransformVisitor;

/** <code>Warped</code> transforms the coordinate space of its child.
 * For example, a warped "right pull by" might become a "left pull by".
 * @author C. Scott Ananian
 * @version $Id: Warped.java,v 1.3 2006-10-17 01:53:57 cananian Exp $
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
    public <T> Comp accept(TransformVisitor<T> v, T t) {
        return v.visit(this, t);
    }
    /** Factory: creates new If only if it would differ from this. */
    public Comp build(Warp warp, Comp child) {
        AST c = this.getFirstChild();
        if (warp.equals(this.warp) && child==c.getNextSibling())
            return this;
        return new Warped(warp, child);
    }
    @Override
    public String toString() {
        return super.toString()+" "+warp;
    }
}
