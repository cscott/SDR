package net.cscott.sdr.calls.ast;

import static net.cscott.sdr.calls.ast.TokenTypes.PART;
import net.cscott.sdr.calls.transform.TransformVisitor;
/**
 * {@link Part} denotes an fractional part of a call.  Iff
 * {@link Part#isDivisible} is false, calls can not be
 * fractionalized below this {@code Part}.  The {@code Part} also denotes
 * timing: each part executes in the same amount of time (unless modified by
 * {@link In}. <code>Part</code> has exactly one child, which is a
 * {@link Comp}.
 * @author C. Scott Ananian
 * @version $Id: Part.java,v 1.4 2006-10-17 01:53:57 cananian Exp $
 */
public class Part extends SeqCall {
    public final boolean isDivisible;
    public final Comp child;
    public Part(boolean isDivisible, Comp child) {
        super(PART);
        this.child = child;
        this.isDivisible = isDivisible;
        addChild(child);
    }
    public <T> SeqCall accept(TransformVisitor<T> v, T t) {
        return v.visit(this, t);
    }
    
    /** Factory: creates new Part only if it would differ from this. */
    public Part build(boolean isDivisible, Comp child) {
        if (isDivisible==this.isDivisible && child==this.child)
            return this;
        return new Part(isDivisible, child);
    }
}
