package net.cscott.sdr.calls.ast;

import static net.cscott.sdr.calls.transform.CallFileLexer.APPLY;
import net.cscott.jdoctest.JDoctestRunner;
import net.cscott.sdr.calls.BadCallException;
import net.cscott.sdr.calls.Call;
import net.cscott.sdr.calls.CallDB;
import net.cscott.sdr.calls.DanceState;
import net.cscott.sdr.calls.ExprFunc.EvaluationException;
import net.cscott.sdr.calls.transform.Evaluator;
import net.cscott.sdr.calls.transform.TransformVisitor;
import net.cscott.sdr.calls.transform.ValueVisitor;

import org.junit.runner.RunWith;

/**
 * {@code Apply} represents a invocation of a call or concept, with
 * zero or more arguments. The {@link Apply#call call} field of the
 * {@code Apply} gives an expression for the call (which can be
 * converted to a {@link Call} object with {@link CallDB#lookup(String)} or to
 * an {@link Evaluator} using {@link Expr#evaluate(Class, DanceState)}). Note
 * that 'and' is a concept used to sequentially join calls: the caller's
 * "slip and slide" would be represented as:
 * 
 * <pre>
 * and(slip, slide)
 * (Apply (Expr and 'slip 'slide))
 * </pre>
 * 
 * The children of this node represent the arguments. Numerical or selector
 * arguments are represented as text beneath a "literal" expression, to
 * distinguish them from similarly-named functions which might be
 * evaluated to yield a Call value.  For example, "twice (trade and roll)" is:
 * 
 * <pre>
 * _fractional(2, _roll(trade))
 * (Apply _fractional '2 (Expr _roll 'trade))
 * </pre>
 * 
 * @author C. Scott Ananian
 * @version $Id: Apply.java,v 1.9 2006-10-19 21:00:09 cananian Exp $
 * @doc.test Actual code for examples in the class description:
 *  js> a = new Apply(new Expr("and", Expr.literal("slip"), Expr.literal("slide")))
 *  (Apply (Expr and 'slip 'slide))
 *  js> a.toShortString()
 *  and(slip, slide)
 *  js> importPackage(net.cscott.sdr.util)
 *  js> a = new Apply(new Expr("_fractional", Expr.literal(Fraction.TWO),
 *    >                        new Expr("_roll", Expr.literal("trade"))))
 *  (Apply (Expr _fractional '2 (Expr _roll 'trade)))
 *  js> a.toShortString()
 *  _fractional(2, _roll(trade))
 */
@RunWith(value=JDoctestRunner.class)
public class Apply extends SeqCall {
    public final Expr call;

    public Apply(Expr call) {
        super(APPLY);
        this.call = call;
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
    @Override
    public String argsToString() {
        return call.toString();
    }

    public Evaluator evaluator(DanceState ds)
        throws BadCallException {
        try {
            return call.evaluate(Evaluator.class, ds);
        } catch (EvaluationException ee) {
            throw new BadCallException("Unknown call: "+this);
        }
    }

    /** Emit an apply in the form it appears in the call definition lists. */
    public String toShortString() {
        return call.toShortString();
    }

    // helper method
    public static Apply makeApply(String callName) {
        return new Apply(Expr.literal(callName));
    }

    /** Factory: creates new Apply only if it would differ from this. */
    public Apply build(Expr call) {
        if (this.call.equals(call))
            return this;
        return new Apply(call);
    }
}
