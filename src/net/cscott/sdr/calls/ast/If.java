package net.cscott.sdr.calls.ast;

import static net.cscott.sdr.calls.transform.AstTokenTypes.IF;
import net.cscott.sdr.calls.transform.TransformVisitor;
import net.cscott.sdr.calls.transform.ValueVisitor;

/** <code>If</code> rejects its child as suitable for execution from
 * the current formation unless its condition evaluates true.
 * @author C. Scott Ananian
 * @version $Id: If.java,v 1.7 2006-10-17 16:29:05 cananian Exp $
 */
public class If extends Comp {
    public final Condition condition;
    public final Comp child;
    
    public If(Condition condition, Comp child) {
        super(IF);
        this.condition = condition;
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
    public If build(Condition condition, Comp child) {
        if (this.condition==condition && this.child==child)
            return this;
        return new If(condition, child);
    }
    @Override
    public String argsToString() {
        return condition.toString()+" "+child.toString();
    }
}
