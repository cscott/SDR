package net.cscott.sdr.calls.ast;

import static net.cscott.sdr.calls.ast.TokenTypes.IF;
import antlr.collections.AST;
import net.cscott.sdr.calls.transform.TransformVisitor;
import net.cscott.sdr.util.Fraction;

/** <code>If</code> rejects its child as suitable for execution from
 * the current formation unless its condition evaluates true.
 * @author C. Scott Ananian
 * @version $Id: If.java,v 1.6 2006-10-17 01:53:57 cananian Exp $
 */
public class If extends Comp {
    
    public If(Condition cond, Comp child) {
        super(IF);
        addChild(cond);
        addChild(child);
    }
    public <T> Comp accept(TransformVisitor<T> v, T t) {
        return v.visit(this, t);
    }
    /** Factory: creates new If only if it would differ from this. */
    public If build(Condition cond, Comp child) {
        AST c = this.getFirstChild();
        if (cond==c && child==c.getNextSibling())
            return this;
        return new If(cond, child);
    }
    public Condition getCondition() { return (Condition) getFirstChild(); }
    public Comp getChild() { return (Comp) getCondition().getNextSibling(); }
}
