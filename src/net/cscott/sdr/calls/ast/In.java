package net.cscott.sdr.calls.ast;

import static net.cscott.sdr.calls.parser.AstTokenTypes.IN;
import net.cscott.sdr.calls.transform.TransformVisitor;
import net.cscott.sdr.calls.transform.ValueVisitor;
import net.cscott.sdr.util.Fraction;

/** {@link In} alters the timing of its child such that it
 * executes its {@link In#child} in exactly {@link In#count} beats.
 * @author C. Scott Ananian
 * @version $Id: In.java,v 1.6 2006-10-17 16:29:05 cananian Exp $
 */
public class In extends Comp {
    public final Expr count;
    public final Comp child;
    
    public In(Expr count, Comp child) {
        super(IN);
        this.count = count;
        this.child = child;
    }
    public In(int i, Seq seq) {
        this(Expr.literal(Fraction.valueOf(i)), seq);
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
    @Override
    public String argsToString() {
        return count.toString()+" "+child.toString();
    }
    /** Factory: creates new In only if it would differ from this. */
    public In build(Expr count, Comp child) {
        if (this.count.equals(count) && this.child==child)
            return this;
        return new In(count, child);
    }
}
