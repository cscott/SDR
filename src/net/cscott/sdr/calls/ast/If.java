package net.cscott.sdr.calls.ast;

import static net.cscott.sdr.calls.ast.TokenTypes.IF;
import antlr.collections.AST;
import net.cscott.sdr.util.Fraction;

/** <code>If</code> rejects its child as suitable for execution from
 * the current formation unless its condition evaluates true.
 * @author C. Scott Ananian
 * @version $Id: If.java,v 1.5 2006-10-15 03:15:04 cananian Exp $
 */
public class If extends Comp {
    
    public If(Condition cond, Comp child) {
        super(IF);
        addChild(cond);
        addChild(child);
    }
    /** Factory: creates new If only if it would differ from this. */
    public If build(Condition cond, Comp child) {
        AST c = this.getFirstChild();
        if (cond==c && child==c.getNextSibling())
            return this;
        return new If(cond, child);
    }
}
