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
 * @version $Id: Call.java,v 1.1 2006-10-11 01:44:23 cananian Exp $
 */
public abstract class Call {
    /** The name of this call, in our internal jargon.  This is not
     * guaranteed to be identical to the wording a caller would use;
     * we do some reordering and rewording to make our internal
     * representation regular and unambigous. */
    public abstract String getName();
    /** The program to which this particular call or concept belongs. */
    public abstract String getProgram();
    /** Evaluates this call with the arguments given in the <code>Apply</code>
     * node, returning a <code>Comp</code>.  Note that the first child of the
     * <code>Apply</code> should be a CALLNAME leaf which matches the
     * result of <code>this.getName()</code>.
     */
    public abstract Comp apply(Apply ast);

    /** Create a Call object for a 'simple call' which takes no arguments. */
    public static Call makeSimpleCall(final String name, final String program,
                                      final Comp def) {
        return new Call() {
            public String getName() { return name; }
            public String getProgram() { return program; }
            public Comp apply(Apply ast) {
                assert ast.getFirstChild().getText().equals(name);
                return def;
            }
        };
    }
}
