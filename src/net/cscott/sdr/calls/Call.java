package net.cscott.sdr.calls;

import net.cscott.sdr.calls.ast.Apply;
import net.cscott.sdr.calls.ast.Comp;

/** The <code>Call</code> class includes 'simple calls' (like HINGE) which
 * take no arguments, 'complex calls' (like SQUARE THRU) which take a
 * numerical argument, and 'concepts' (like AS COUPLES) which take another
 * call or calls as arguments.  These are not distinguished here: the
 * important thing is that any call can be *applied* to zero or more
 * arguments (numbers, selectors, or other calls) to result in a
 * <code>Comp</code> AST tree.
 * @author C. Scott Ananian
 * @version $Id: Call.java,v 1.4 2006-10-11 18:50:03 cananian Exp $
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
     * match @{link Call#getName(String) this.getName()}.
     */
    public abstract Comp apply(Apply ast);
    
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
                                      final Comp def) {
        return new Call() {
            @Override
            public String getName() { return name; }
            @Override
            public Program getProgram() { return program; }
            @Override
            public Comp apply(Apply ast) {
                assert ast.getFirstChild().getText().equals(name);
                return def;
            }
        };
    }
}
