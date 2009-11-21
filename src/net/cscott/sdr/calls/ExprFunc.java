package net.cscott.sdr.calls;

import java.util.List;

import net.cscott.sdr.calls.ast.Expr;

/** An {@link ExprFunc} is responsible for evaluating the value of an
 *  {@link net.cscott.sdr.calls.ast.Expr}.  There are a list of
 *  {@link ExprFunc}s in {@link ExprList}.
 */
public abstract class ExprFunc<T> {
    /** The atom of {@link Expr}s which this {@link ExprFunc} should be used to
     *  evaluate. */
    public abstract String getName();
    /** Perform the operation of the {@link ExprFunc} on the given list of
     *  arguments {@code args}, yielding a value of the specified {@code type}.
     *  The {@link DanceState} {@code ds} is used as context for the
     *  evaluation.
     * @throws EvaluationException on type mismatch or other unexpected
     *         conditions.
     */
    public abstract T evaluate(Class<? super T> type, DanceState ds, List<Expr> args)
        throws EvaluationException;
    /** Indicates whether the result of the evaluation would be a constant (ie,
     *  independent of {@link DanceState}).
     */
    public boolean isConstant(Class<? super T> type, List<Expr> args) {
        return false;
    }

    /** Thrown to indicate an unexpected problem evaluating an
     *  {@link net.cscott.sdr.calls.ast.Expr}, for example a type mismatch.
     *  This shouldn't be thrown for expected issues (wrong formation, say);
     *  use {@link BadCallException} for that.
     */
    public static class EvaluationException extends Exception {
        public EvaluationException(String msg) { super(msg); }
    }
}
