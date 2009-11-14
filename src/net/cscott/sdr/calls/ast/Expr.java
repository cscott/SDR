package net.cscott.sdr.calls.ast;

import static net.cscott.sdr.calls.transform.AstTokenTypes.EXPR;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.cscott.sdr.calls.DanceState;
import net.cscott.sdr.calls.ExprList;
import net.cscott.sdr.calls.ExprFunc.EvaluationException;
import net.cscott.sdr.calls.transform.TransformVisitor;
import net.cscott.sdr.calls.transform.ValueVisitor;

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
    // AST just represents the computation; actual evaluation is done in
    // ExprList
    public final <T> T evaluate(Class<T> type, DanceState ds)
        throws EvaluationException {
        return ExprList.evaluate(this.atom, type, ds, args);
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
    /** Factory: creates new Condition only if it would differ from this. */
    public Expr build(String atom, List<Expr> args) {
        if (this.atom.equals(atom) && this.args.equals(args))
            return this;
        return new Expr(atom, args);
    }
}
