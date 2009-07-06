package net.cscott.sdr.calls.ast;

import static net.cscott.sdr.calls.transform.AstTokenTypes.IN;
import net.cscott.sdr.calls.transform.TransformVisitor;
import net.cscott.sdr.calls.transform.ValueVisitor;
import net.cscott.sdr.util.Fraction;

/** <code>In</code> alters the timing of its child such that it
 * executes in exactly <code>count</code> beats.
 * @author C. Scott Ananian
 * @version $Id: In.java,v 1.6 2006-10-17 16:29:05 cananian Exp $
 */
public class In extends Comp {
    public final Fraction count;
    public final Comp child;
    
    public In(Fraction count, Comp child) {
        super(IN);
        this.count = count;
        this.child = child;
    }
    public In(int i, Seq seq) {
        this(Fraction.valueOf(i), seq);
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
        return count.toProperString()+" "+child.toString();
    }
    /** Factory: creates new In only if it would differ from this. */
    public In build(Fraction count, Comp child) {
        if (this.count.equals(count) && this.child==child)
            return this;
        return new In(count, child);
    }
}