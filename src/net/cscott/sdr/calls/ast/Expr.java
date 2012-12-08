package net.cscott.sdr.calls.ast;

import static net.cscott.sdr.calls.parser.AstTokenTypes.EXPR;
import static net.cscott.sdr.util.Tools.foreach;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.cscott.jdoctest.JDoctestRunner;
import net.cscott.sdr.calls.DanceState;
import net.cscott.sdr.calls.ExprList;
import net.cscott.sdr.calls.ExprFunc.EvaluationException;
import net.cscott.sdr.calls.transform.TransformVisitor;
import net.cscott.sdr.calls.transform.ValueVisitor;
import net.cscott.sdr.util.Fraction;
import net.cscott.sdr.util.Tools.F;

import org.junit.runner.RunWith;

/** {@link Expr} represents an expression to be computed at evaluation time.
 *  The value of an {@link Expr} may depend on the {@link DanceState} at the
 *  time it is evaluated.  Constants are encoded as special
 *  "literal" expressions.
 */
@RunWith(value=JDoctestRunner.class)
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

    /** Special constructor to make an expression representing a constant
     *  string.  The literal "function" interprets its argument as a
     *  literal constant.
     * @doc.test A literal string.
     *  js> e = Expr.literal("hi there")
     *  'hi there
     *  js> e.atom
     *  literal
     *  js> e.args.size()
     *  1
     *  js> e.args.get(0).atom
     *  hi there
     *  js> sclass = java.lang.Class.forName("java.lang.String")
     *  class java.lang.String
     *  js> e.isConstant(sclass)
     *  true
     *  js> e.evaluate(sclass, null)
     *  hi there
     */
    public static Expr literal(String s) {
        return new Expr("literal", new Expr(s));
    }
    /** Special constructor to make an expression representing a numeric
     *  constant.  The literal "function" interprets its argument as a
     *  literal constant.
     * @doc.test A literal number.
     *  js> importPackage(net.cscott.sdr.util)
     *  js> e = Expr.literal(Fraction.valueOf("1 1/2"))
     *  '1 1/2
     *  js> e.atom
     *  literal
     *  js> e.args.size()
     *  1
     *  js> e.args.get(0).atom
     *  1 1/2
     *  js> fclass = Fraction.ZERO.getClass()
     *  class net.cscott.sdr.util.Fraction
     *  js> e.isConstant(fclass)
     *  true
     *  js> e.evaluate(fclass, null).toProperString()
     *  1 1/2
     */
    public static Expr literal(Fraction f) {
        return literal(f.toProperString());
    }

    // AST just represents the computation; actual evaluation is done in
    // ExprList
    /** Evaluate the {@link Expr} in the given {@link DanceState} to yield
     *  a result of the requested {@code type}.
     * @doc.test Evaluate a simple arithmetic expression.
     *  js> importPackage(net.cscott.sdr.util)
     *  js> fclass = Fraction.ZERO.getClass()
     *  class net.cscott.sdr.util.Fraction
     *  js> e = new Expr("_add num", Expr.literal(Fraction.ONE),
     *    >              Expr.literal(Fraction.TWO))
     *  (Expr _add num '1 '2)
     *  js> e.isConstant(fclass)
     *  true
     *  js> e.evaluate(fclass, null).toProperString()
     *  3
     */
    public final <T> T evaluate(Class<T> type, DanceState ds)
        throws EvaluationException {
        return ExprList.evaluate(this.atom, type, ds, args);
    }

    /** Returns true iff the value of this {@link Expr} is independent of the
     *  {@link DanceState}.
     * @doc.test Test constant and non-constant expressions.
     *  js> importPackage(net.cscott.sdr.util)
     *  js> fclass = Fraction.ZERO.getClass()
     *  class net.cscott.sdr.util.Fraction
     *  js> e = new Expr("_add num", Expr.literal(Fraction.ONE),
     *    >              Expr.literal(Fraction.TWO))
     *  (Expr _add num '1 '2)
     *  js> e.isConstant(fclass)
     *  true
     *  js> e = new Expr("_add num", Expr.literal(Fraction.ONE),
     *    >              new Expr("num dancers"))
     *  (Expr _add num '1 (Expr num dancers))
     *  js> e.isConstant(fclass)
     *  false
     */
    public final boolean isConstant(Class<?> type) {
        return ExprList.isConstant(this.atom, type, args);
    }

    /** Accept a visitor pattern. */
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
     *  (Something like Lisp M-expressions.)
     * @doc.test
     *  js> importPackage(net.cscott.sdr.util)
     *  js> e = new Expr("_add num", Expr.literal(Fraction.ONE),
     *    >              Expr.literal(Fraction.TWO)) ; e.toString()
     *  (Expr _add num '1 '2)
     *  js> e.toShortString()
     *  _add num(1, 2)
     *  js> e = new Expr("_add num", Expr.literal(Fraction.ONE),
     *    >              new Expr("num dancers")) ; e.toString()
     *  (Expr _add num '1 (Expr num dancers))
     *  js> e.toShortString()
     *  _add num(1, num dancers())
     */
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
    /** Factory: creates new Expr only if it would differ from this.
     * @doc.test
     *  js> importPackage(net.cscott.sdr.util)
     *  js> e = new Expr("_add num", Expr.literal(Fraction.ONE),
     *    >              Expr.literal(Fraction.TWO))
     *  (Expr _add num '1 '2)
     *  js> e2 = e.build("_add num", e.args)
     *  (Expr _add num '1 '2)
     *  js> e === e2
     *  true
     *  js> l = Tools.l(e.args.get(0), e.args.get(1))
     *  ['1, '2]
     *  js> e3 = e.build("_add num", l)
     *  (Expr _add num '1 '2)
     *  js> e === e3
     *  true
     * @doc.test Note that the comparison is not deep (for that would blow up
     *  the computational complexity of build):
     *  js> importPackage(net.cscott.sdr.util)
     *  js> e = new Expr("_add num", Expr.literal(Fraction.ONE),
     *    >              Expr.literal(Fraction.TWO))
     *  (Expr _add num '1 '2)
     *  js> l = Tools.l(Expr.literal(Fraction.ONE), Expr.literal(Fraction.TWO))
     *  ['1, '2]
     *  js> e2 = e.build("_add num", l)
     *  (Expr _add num '1 '2)
     *  js> e === e2
     *  false
     */
    public Expr build(String atom, List<Expr> args) {
        if (this.atom.equals(atom) && this.args.equals(args))
            return this;
        return new Expr(atom, args);
    }

    /** Simplify <code>_apply concept</code> and <code>_apply</code>
     *  nodes in the expression.
     * @doc.test <code>_apply</code> nodes are simplified:
     *  js> importPackage(net.cscott.sdr.calls.ast)
     *  js> e = AstNode.valueOf("(Expr _apply 'tandem 'swing thru)")
     *  (Expr _apply 'tandem 'swing thru)
     *  js> e.toShortString()
     *  _apply(tandem, swing thru)
     *  js> e.simplify().toShortString()
     *  tandem(swing thru)
     * @doc.test <code>_apply concept</code> nodes are just sugar for
     *  <code>_apply</code>:
     *  js> importPackage(net.cscott.sdr.calls.ast)
     *  js> e = AstNode.valueOf("(Expr _apply concept 'tandem 'swing thru)")
     *  (Expr _apply concept 'tandem 'swing thru)
     *  js> e.toShortString()
     *  _apply concept(tandem, swing thru)
     *  js> e.simplify().toShortString()
     *  tandem(swing thru)
     * @doc.test Curried applications.
     *  Let's simplify "initially tandem swing thru":
     *  js> importPackage(net.cscott.sdr.calls.ast)
     *  js> e = AstNode.valueOf("(Expr _apply concept (Expr _curry 'initially 'tandem (Expr _arg '0)) 'swing thru)") ; e.toShortString()
     *  _apply concept(_curry(initially, tandem, _arg(0)), swing thru)
     *  js> e.simplify().toShortString()
     *  initially(tandem, swing thru)
     * @doc.test We handle nested applications correctly as well.
     *  Let's simplify "finally initially tandem hot foot spin":
     *  js> importPackage(net.cscott.sdr.calls.ast)
     *  js> e = AstNode.valueOf("(Expr _apply concept (Expr _curry 'finally (Expr _curry 'initially 'tandem (Expr _arg '0)) (Expr _arg '0)) 'hot foot spin)") ; e.toShortString()
     *  _apply concept(_curry(finally, _curry(initially, tandem, _arg(0)), _arg(0)), hot foot spin)
     *  js> e.simplify().toShortString()
     *  finally(_curry(initially, tandem, _arg(0)), hot foot spin)
     */
    public Expr simplify() {
        if (this.atom == "_apply concept") {
            return this.build("_apply", this.args).simplify();
        } else if (this.atom == "_apply" && this.args.size() >= 1) {
            Expr curry = this.args.get(0).ensureCurry(this.args.size()-1);
            final List<Expr> rest = this.args.subList(1, this.args.size());
            // first curry arg is function name
            assert curry.atom == "_curry" && !curry.args.isEmpty();
            if (!curry.args.get(0).isConstant(String.class))
                return this; // can't simplify function name
            try {
                String func = curry.args.get(0).evaluate(String.class, null);
                List<Expr> nArgs = curry.args.subList(1, curry.args.size());
                return this.build(func, foreach(nArgs, new F<Expr, Expr>() {
                    @Override
                    public Expr map(Expr e) { return e.subst(rest); }
                })).simplify();
            } catch (EvaluationException ee) {
                assert false : "should never happen";
                /* fall through */
            } catch (CantSimplifyException cse) {
                /* fall through */
            }
        }
        // leave this expr alone, but simplify its args
        return this.build(this.atom, foreach(this.args, new F<Expr,Expr>() {
            @Override
            public Expr map(Expr e) { return e.simplify(); }
        }));
    }
    private static class CantSimplifyException extends RuntimeException { }
    // this is like ExprList._APPLY.subst, except we bail (throwing
    // CantSimplifyException) if we can't evaluate args.
    private Expr subst(final List<Expr> args) throws CantSimplifyException {
        if (this.atom == "_curry") {
            // don't recurse into _curry; this limits scope of func args
            return this;
        }
        if (this.atom == "_arg") {
            // replace with appropriate curry argument, if possible
            Expr argNum = this.args.get(0);
            if (argNum.isConstant(Fraction.class)) {
                try {
                    Fraction n = argNum.evaluate(Fraction.class, null);
                    assert n.getProperNumerator()==0;
                    int index = n.floor();
                    if (index >= 0 && index < args.size()) {
                        return args.get(index);
                    }
                } catch (EvaluationException ee) { /* fall through */ }
            }
            throw new CantSimplifyException();
        }
        // recurse into args, looking for refs.
        List<Expr> nArgs = foreach(this.args, new F<Expr,Expr>() {
            @Override
            public Expr map(Expr e) { return e.subst(args); }
        });
        return this.build(this.atom, nArgs);
    }

    /** Helper: convert literals (as first argument to <code>_apply</code>) to
     *  appropriate <code>_curry</code> function invocation.
     * @doc.test
     *  js> Expr.literal("foo").ensureCurry(0)
     *  (Expr _curry 'foo)
     *  js> Expr.literal("foo").ensureCurry(3)
     *  (Expr _curry 'foo (Expr _arg '0) (Expr _arg '1) (Expr _arg '2))
     *  js> new Expr("_curry", Expr.literal("foo")).ensureCurry(0)
     *  (Expr _curry 'foo)
     */
    public Expr ensureCurry(int nArgs) {
        if (this.atom == "_curry") { return this; }
        // turn a string function name into:
        //    (Expr _curry 'name (Expr _arg '0))
        List<Expr> args = new ArrayList<Expr>(nArgs+1);
        args.add(this);
        for (int i=0; i<nArgs; i++)
            args.add(new Expr("_arg", Expr.literal(Fraction.valueOf(i))));
        return new Expr("_curry", args);
    }
}
