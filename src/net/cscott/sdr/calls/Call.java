package net.cscott.sdr.calls;

import java.util.Collections;
import java.util.List;

import net.cscott.sdr.calls.ast.Comp;
import net.cscott.sdr.calls.ast.Expr;
import net.cscott.sdr.calls.grm.Rule;
import net.cscott.sdr.calls.transform.Evaluator;

/** The {@link Call} class includes 'simple calls' (like HINGE) which
 * take no arguments, 'complex calls' (like SQUARE THRU) which take a
 * numerical argument, and 'concepts' (like AS COUPLES) which take another
 * call or calls as arguments.  These are not distinguished here: the
 * important thing is that any call can be *applied* to zero or more
 * arguments (numbers, matchers, or other calls) to result in an
 * {@link Evaluator} (and sometimes, if {@link Evaluator#hasSimpleExpansion()}
 * is true, to a {@link Comp} AST tree).
 * @author C. Scott Ananian
 * @version $Id: Call.java,v 1.7 2006-10-21 00:54:31 cananian Exp $
 */
public abstract class Call extends ExprFunc<Evaluator> {
    /** The name of this call, in our internal jargon.  This is not
     * guaranteed to be identical to the wording a caller would use;
     * we do some reordering and rewording to make our internal
     * representation regular and unambigous. */
    public abstract String getName();
    /** The program to which this particular call or concept belongs. */
    public abstract Program getProgram();
    /**
     * Return the number of arguments which should, at minimum, be given to this
     * {@link Call}.  Usually this is the exact number of arguments required,
     * but some combining calls (like 'and') can take an arbitrary number of
     * arguments.
     */
    public abstract int getMinNumberOfArguments();
    /**
     * Return argument defaults, if there are any for this call.
     * The list may contain any number of entries (including 0).  Arguments
     * off the end of the returned list, as well as arguments whose
     * corresponding entry in the list is <code>null</code>, have no default.
     */
    public abstract List<Expr> getDefaultArguments();
    /**
     * Returns the grammar rule applicable to this call, or
     * {@code null}, if there is none (ie, this is an internal call).
     */
    public abstract Rule getRule();
    /**
     * Returns the {@link Evaluator} to use on the result of an application.
     * The {@link Evaluator#hasSimpleExpansion()} method of the
     * {@link Evaluator} will return true if this call can be simply expanded;
     * use the {@link Evaluator#simpleExpansion()} method to obtain the
     * expansion.  Otherwise the call
     * should be considered 'opaque' and requires use of the custom
     * {@link Evaluator}.)
     * @param args identical to the args argument of
     *        {@link ExprFunc#evaluate(Class, DanceState, List)}.
     */
    public abstract Evaluator getEvaluator(DanceState ds, List<Expr> args)
        throws EvaluationException;

    /** Implementation of {@link ExprFunc} interface: this {@link Call} can
     *  be evaluated to yield an {@link Evaluator}.
     */
    @Override
    public final Evaluator evaluate(Class<? super Evaluator> type,
                                    DanceState ds, List<Expr> args)
        throws EvaluationException {
        return getEvaluator(ds, args);
    }
    
    @Override
    public final String toString() {
        return getName()+"["+getProgram().toString().toLowerCase()+"]";
    }
    @Override
    public final int hashCode() { return getName().hashCode(); }
    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof Call)) return false;
        Call c = (Call) o;
        return this.getName().equals(c.getName());
    }

    /** Create a Call object for a 'simple call' which takes no arguments. */
    public static Call makeSimpleCall(final String name, final Program program,
                                      final Comp def, final Rule rule) {
        return new Call() {
            @Override
            public String getName() { return name; }
            @Override
            public Program getProgram() { return program; }
            @Override
            public Evaluator getEvaluator(DanceState ds, List<Expr> args) {
                assert args.isEmpty();
                return new Evaluator.Standard(def);
            }
            @Override
            public int getMinNumberOfArguments() { return 0; }
            @Override
            public List<Expr> getDefaultArguments() {
                return Collections.emptyList();
            }
            @Override
            public Rule getRule() { return rule; }
        };
    }
}
