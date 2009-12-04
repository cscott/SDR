package net.cscott.sdr.calls.ast;

import static net.cscott.sdr.calls.parser.CallFileLexer.SELECT;
import net.cscott.sdr.calls.BadCallException;
import net.cscott.sdr.calls.DanceState;
import net.cscott.sdr.calls.Selector;
import net.cscott.sdr.calls.ExprFunc.EvaluationException;
import net.cscott.sdr.calls.transform.TransformVisitor;
import net.cscott.sdr.calls.transform.ValueVisitor;

/** <code>ParCall</code> bundles a selector with a
 * {@link Comp}.  A {@link ParCall} applies
 * the child to dancers which match the given
 * {@link Selector}s.
 * @author C. Scott Ananian
 */
public class ParCall extends AstNode {
    public final Expr selector;
    public final Comp child;

    // use parseTags if you want to make a ParCall from a list of strings.
    public ParCall(Expr selector, Comp child) {
        super(SELECT, "Select");
        this.selector = selector;
        this.child = child;
    }
    public Selector evaluate(DanceState ds)  {
        try {
            return selector.evaluate(Selector.class, ds);
        } catch (EvaluationException ee) {
            assert false : "shouldn't happen";
            throw new BadCallException("Couldn't evaluate");
        }
    }
    @Override
    public <T> ParCall accept(TransformVisitor<T> v, T t) {
        return v.visit(this, t);
    }
    @Override
    public <RESULT,CLOSURE>
    RESULT accept(ValueVisitor<RESULT,CLOSURE> v, CLOSURE cl) {
        return v.visit(this, cl);
    }
    /** Factory: creates new ParCall only if it would differ from this. */
    public ParCall build(Expr selector, Comp child) {
        if (selector==this.selector && this.child==child)
            return this;
        return new ParCall(selector, child);
    }
    @Override
    public String argsToString() {
        StringBuilder sb = new StringBuilder();
        sb.append(selector);
        sb.append(' ');
        sb.append(child);
        return sb.toString();
    }
}
