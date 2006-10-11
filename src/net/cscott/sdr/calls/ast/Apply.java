package net.cscott.sdr.calls.ast;

import static net.cscott.sdr.calls.ast.TokenTypes.APPLY;

import java.util.*;
import java.util.concurrent.Future;

import net.cscott.sdr.util.Fraction;

/** <code>Apply</code> represents a invocation of a call, with
 * zero or more arguments.  The first child of <code>Apply</code>
 * is a CALLNAME leaf whose text gives the name of the call.  Further
 * arguments are NUMBER leaves or futher Apply trees.  Note that
 * 'and' is a concept used to sequentially join calls: the caller's
 * "slip and slide" would be represented as
 * <pre>
 * #(APPLY CALLNAME("and") #(APPLY CALLNAME("slip")) #(APPLY CALLNAME("slide")))
 * </pre>
 * @author C. Scott Ananian
 * @version $Id: Apply.java,v 1.2 2006-10-11 04:27:34 cananian Exp $
 */
public class Apply extends SeqCall {
    public final String callName;
    public Apply(String callName, List<Apply> args) {
        super(APPLY);
        this.callName = callName;
        for (Apply a : args)
            addChild(a);
    }
    public String toString() {
        return super.toString()+"["+callName+"]";
    }

    public Apply getArg(int n) {
        Apply a = (Apply) getFirstChild();
        for (int i=0; i<n; i++)
            a = (Apply) a.getNextSibling();
        return a;
    }
    public Fraction getNumberArg(int n) {
        return Fraction.valueOf(getArg(n).callName);
    }
    public String getStringArg(int n) {
        return getArg(n).callName;
    }
    // XXX getSelectorArg, etc?
    
    // factories.
    public static Apply makeApply(String callName) {
        return new Apply(callName, Collections.<Apply>emptyList());
    }
    public static Apply makeApply(String callName, Fraction number) {
        Apply arg = makeApply(number.toString()); // sigh
        return makeApply(callName, arg);
    }
    public static Apply makeApply(String conceptName, Apply... subCalls) {
        return new Apply(conceptName, Arrays.asList(subCalls));
    }
}
