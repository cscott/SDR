package net.cscott.sdr.calls.ast;

import static net.cscott.sdr.calls.parser.AstTokenTypes.PART;
import net.cscott.sdr.calls.transform.TransformVisitor;
import net.cscott.sdr.calls.transform.ValueVisitor;
import net.cscott.sdr.util.Fraction;
/**
 * {@link Part} denotes an fractional part of a call.  The {@code Part} also denotes
 * timing: each part executes in the same amount of time (unless modified by
 * {@link In}. <code>Part</code> has exactly one child, which is a
 * {@link Comp}.  The <code>howMany</code> argument is usually 1 but
 * allows non-standard divisions: for example, "swing and mix" is a three-part
 * call of which the final "mix" counts for two parts.
 * <p>
 * The {@link Part#divisibility} argument has three values.  If it is
 * {@link Divisibility#DIVISIBLE}, calls inside this {@code Part} can be
 * further fractionalized.  In the "swing and mix" example, both the "swing"
 * and the "mix" can be further divided, allowing "1/6 swing and mix" and
 * "2/3 swing and mix" to be valid.  If {@link Part#divisibility} is
 * {@link Divisibility#INDIVISIBLE}, then the {@link Part#child} can not be
 * further divided.  For example, "partner swing" can not be divided; it makes
 * no sense to "half swing your partner".  Indivisible parts are also used
 * around {@link Prim}s composing a call, to avoid exposing the implementation
 * of the dancer's path, where this is not well standardized, for example in
 * the definition of "flutterwheel".
 * <p>
 * The final possible value for {@link Part#divisibility} is
 * {@link Divisibility#INDETERMINATE}.  This
 * allows the specification of calls with clear first and/or last parts, but
 * which can't be otherwise fractionalized.  For example, a call which is
 * defined as:
 * <pre>(Part 'DIVISIBLE '1 ...) (Part 'INDETERMINATE '1 ...) (Part 'INDIVISIBLE '1 ...)</pre>
 * can be used with "finish &lt;anything&gt;" and "like an
 * &lt;anything&gt;", but "1/3 &lt;anything&gt;" and
 * "interrupt after each part with &lt;anything&gt;" would both be
 * invalid, because the number of parts in the middle is not defined.
 * The {@link Part#howMany} argument is still used internally to divide up
 * the timing of the call, but is otherwise overridden by
 * {@link Divisibility#INDETERMINATE}.
 * @author C. Scott Ananian
 */
public class Part extends SeqCall {
    /** Whether this {@link Part} can be fractionalized. */
    public enum Divisibility { INDIVISIBLE, INDETERMINATE, DIVISIBLE };
    public final Divisibility divisibility;
    public final Expr howMany;
    public final Comp child;
    public Part(Divisibility divisibility, Expr howMany, Comp child) {
        super(PART);
        this.child = child;
        this.howMany = howMany;
        this.divisibility = divisibility;
    }
    public Part(Divisibility divisibility, Fraction howMany, Comp child) {
        this(divisibility, Expr.literal(howMany), child);
        assert howMany.compareTo(Fraction.ZERO) > 0;
    }
    @Override
    public Expr parts() { return howMany; }
    @Override
    public boolean isIndeterminate() {
        return divisibility==Divisibility.INDETERMINATE;
    }

    @Override
    public <T> SeqCall accept(TransformVisitor<T> v, T t) {
        return v.visit(this, t);
    }
    @Override
    public <RESULT,CLOSURE>
    RESULT accept(ValueVisitor<RESULT,CLOSURE> v, CLOSURE cl) {
        return v.visit(this, cl);
    }
    /** Factory: creates new Part only if it would differ from this. */
    public Part build(Divisibility divisibility, Expr howMany, Comp child) {
        if (divisibility==this.divisibility &&
            howMany==this.howMany &&
            child==this.child)
            return this;
        return new Part(divisibility, howMany, child);
    }
    @Override
    public String argsToString() {
        return "'"+divisibility+" "+howMany+" "+child.toString();
    }
}
