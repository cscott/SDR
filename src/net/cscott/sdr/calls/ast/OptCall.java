package net.cscott.sdr.calls.ast;

import static net.cscott.sdr.calls.transform.CallFileLexer.FROM;
import net.cscott.sdr.calls.transform.TransformVisitor;
import net.cscott.sdr.calls.transform.ValueVisitor;

/** <code>OptCall</code> bundles a formation condition with a
 * <code>Comp</code>.
 * @author C. Scott Ananian
 * @version $Id: OptCall.java,v 1.7 2006-10-19 18:44:50 cananian Exp $
 */
public class OptCall extends AstNode {
    public final Expr matcher;
    public final Comp child;
    public OptCall(Expr matcher, Comp child) {
        super(FROM, "From");
        this.matcher = matcher;
        this.child = child;
    }
    @Override
    public <T> OptCall accept(TransformVisitor<T> v, T t) {
        return v.visit(this, t);
    }
    @Override
    public <RESULT,CLOSURE>
    RESULT accept(ValueVisitor<RESULT,CLOSURE> v, CLOSURE cl) {
        return v.visit(this, cl);
    }
    
    /** Factory: creates new OptCall only if it would differ from this. */
    public OptCall build(Expr matcher, Comp child) {
        if (this.matcher.equals(matcher) && this.child==child)
            return this;
        return new OptCall(matcher, child);
    }
    @Override
    public String argsToString() {
        StringBuilder sb = new StringBuilder();
        sb.append(matcher);
        sb.append(' ');
        sb.append(child);
        return sb.toString();
    }
}
