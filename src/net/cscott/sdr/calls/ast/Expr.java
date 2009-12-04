package net.cscott.sdr.calls.ast;

import static net.cscott.sdr.calls.parser.AstTokenTypes.EXPR;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.cscott.sdr.calls.DanceState;
import net.cscott.sdr.calls.ExprList;
import net.cscott.sdr.calls.ExprFunc.EvaluationException;
import net.cscott.sdr.calls.transform.TransformVisitor;
import net.cscott.sdr.calls.transform.ValueVisitor;
import net.cscott.sdr.util.Fraction;

public class Expr extends AstNode {
    public final String atom;
    public final List<Expr> args;
    public Expr(String atom, List<Expr> args) {
        super(EXPR);
        this.atom = atom.intern();
        this.args = Collections.unmodifiableList(args);
    }
    public Expr(String atom, Expr... args) {
        this(atom, Arrays.asList(args));
    }
    // special constructors
    public static Expr literal(String s) {
        return new Expr("literal", new Expr(s));
    }
    public static Expr literal(Fraction f) {
        return literal(f.toProperString());
    }
    // AST just represents the computation; actual evaluation is done in
    // ExprList
    /** Evaluate the {@link Expr} in the given {@link DanceState} to yield
     *  a result of the requested {@code type}. */
    public final <T> T evaluate(Class<T> type, DanceState ds)
        throws EvaluationException {
        return ExprList.evaluate(this.atom, type, ds, args);
    }
    /** Returns true iff the value of this {@link Expr} is independent of the
     *  {@link DanceState}. */
    public final boolean isConstant(Class<?> type) {
        return ExprList.isConstant(this.atom, type, args);
    }

    public <T> Expr accept(TransformVisitor<T> v, T t) {
        return v.visit(this, t);
    }
    @Override
    public <RESULT,CLOSURE>
    RESULT accept(ValueVisitor<RESULT,CLOSURE> v, CLOSURE cl) {
        return v.visit(this, cl);
    }
    @Override
    public String argsToString() {
        StringBuilder sb = new StringBuilder();
        sb.append(atom);
        sb.append(' ');
        for (Expr a : args) {
            sb.append(a.toString());
            sb.append(' ');
        }
        return sb.toString();
    }
    @Override
    public String toString() {
        // abbreviate literals with a single quote
        if (atom.equals("literal"))
            return "'"+args.get(0).atom;
        return super.toString();
    }
    /** Emit an apply in the form it appears in the call definition lists.
     *  (Something like Lisp M-expressions.) */
    public String toShortString() {
        return toShortString(new StringBuilder()).toString();
    }
    StringBuilder toShortString(StringBuilder sb) {
        if (atom.equals("literal")) {
            return sb.append(args.get(0).atom);
        }
        sb.append(atom);
        // always parens in M-expression form (except for literals, which
        // were specially handled above)
        sb.append("(");
        for (int i=0; i<args.size(); i++) {
            if (i>0) sb.append(", ");
            args.get(i).toShortString(sb);
        }
        sb.append(")");
        return sb;
    }
    /** Factory: creates new Expr only if it would differ from this. */
    public Expr build(String atom, List<Expr> args) {
        if (this.atom.equals(atom) && this.args.equals(args))
            return this;
        return new Expr(atom, args);
    }
}
