package net.cscott.sdr.calls;

import java.util.List;

import net.cscott.sdr.calls.ast.Expr;

/** An {@link ExprFunc} is responsible for evaluating the value of an
 *  {@link Expr}.  There are a list of {@link ExprFunc}s in {@link ExprList}.
 */
public abstract class ExprFunc {
    /** The atom of {@link Expr}s which this {@link ExprFunc} should be used to
     *  evaluate. */
    public abstract String getName();
    public abstract <T> T evaluate(Class<T> type, DanceState ds, List<Expr> args)
        throws EvaluationException;

    public static class EvaluationException extends Exception {
        public EvaluationException(String msg) { super(msg); }
    }
}
