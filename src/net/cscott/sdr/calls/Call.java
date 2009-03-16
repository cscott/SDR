package net.cscott.sdr.calls;

import net.cscott.sdr.calls.ast.Apply;
import net.cscott.sdr.calls.ast.Comp;
import net.cscott.sdr.calls.grm.Rule;
import net.cscott.sdr.calls.transform.Evaluator;

/** The {@link Call} class includes 'simple calls' (like HINGE) which
 * take no arguments, 'complex calls' (like SQUARE THRU) which take a
 * numerical argument, and 'concepts' (like AS COUPLES) which take another
 * call or calls as arguments.  These are not distinguished here: the
 * important thing is that any call can be *applied* to zero or more
 * arguments (numbers, selectors, or other calls) to result in a
 * {@link Comp} AST tree.  Complex calls may also specify a
 * custom {@link Evaluator} to use on the result of the application.
 * @author C. Scott Ananian
 * @version $Id: Call.java,v 1.7 2006-10-21 00:54:31 cananian Exp $
 */
public abstract class Call {
    /** The name of this call, in our internal jargon.  This is not
     * guaranteed to be identical to the wording a caller would use;
     * we do some reordering and rewording to make our internal
     * representation regular and unambigous. */
    public abstract String getName();
    /** The program to which this particular call or concept belongs. */
    public abstract Program getProgram();
    /** Evaluates this call with the arguments given in the {@link Apply}
     * node, returning a {@link Comp}.  Note that the
     * {@link Apply#callName callName} field of {@code ast} should
     * match {@link Call#getName() this.getName()}, and the
     * length of the list in the {@link Apply#args args} field should 
     * be at least
     * {@link Call#getMinNumberOfArguments() this.getMinNumberOfArguments()}.
     */
    public abstract Comp apply(Apply ast);
    /**
     * Return the number of arguments which should, at minimum, be given to this
     * {@link Call}.  Usually this is the exact number of arguments required,
     * but some combining calls (like 'and') can take an arbitrary number of
     * arguments.
     */
    public abstract int getMinNumberOfArguments();
    /**
     * Returns the grammar rule applicable to this call, or
     * {@code null}, if there is none (ie, this is an internal call).
     */
    public abstract Rule getRule();
    /**
     * Returns the {@link Evaluator} to use on the result of an application,
     * or {@code null} to use the
     * {@link net.cscott.sdr.calls.transform.Evaluator.Standard} evaluator.
     * (Call processors can determine that a call is safe to
     * expand via application if the return value is null; otherwise, it
     * should be considered 'opaque' and requires use of the custom
     * {@link Evaluator}.)
     * @param ast identical to the ast argument of {@link #apply(Apply)}
     */
    public abstract Evaluator getEvaluator(Apply ast);
    
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
            public Comp apply(Apply ast) {
                assert ast.callName.equals(name);
                assert ast.args.isEmpty();
                return def;
            }
            @Override
            public Evaluator getEvaluator(Apply ast) {
                 return null; // use Standard Evaluator.
            }
            @Override
            public int getMinNumberOfArguments() { return 0; }
            @Override
            public Rule getRule() { return rule; }
        };
    }
}
