package net.cscott.sdr.calls.ast;

import static net.cscott.sdr.calls.ast.TokenTypes.APPLY;

import java.util.*;

import antlr.collections.AST;

import net.cscott.sdr.calls.*;
import net.cscott.sdr.util.Fraction;

/**
 * {@code Apply} represents a invocation of a call or concept, with zero or more
 * arguments. The {@link Apply#callName callName} field of the {@code Apply}
 * gives the name of the call (which can be converted to a {@link Call} object
 * with {@link CallDB#lookup(String)}). Note that 'and' is a concept used to
 * sequentially join calls: the caller's "slip and slide" would be represented
 * as:
 * 
 * <pre>
 *  #(APPLY["and"] #(APPLY["slip"]) #(APPLY["slide"]))
 * </pre>
 * 
 * The children of this node represent the arguments. Numerical or selector
 * arguments are represented as text in another Apply (since we don't know the
 * proper types); for example "twice (trade and roll)" is:
 * 
 * <pre>
 *  #(APPLY["_fractional"] #(APPLY["2"]) #(APPLY["roll"] #(APPLY["trade"])))
 * </pre>
 * 
 * Convenience methods are provided to convert numerical or string arguments
 * when implementing {@link Call#apply(Apply)}.
 * 
 * @author C. Scott Ananian
 * @version $Id: Apply.java,v 1.5 2006-10-15 03:15:04 cananian Exp $
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
        return super.toString() + "[" + callName + "]";
    }

    public Apply getArg(int n) {
        Apply a = (Apply) getFirstChild();
        for (int i = 0; i < n; i++)
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
        return new Apply(callName, Collections.<Apply> emptyList());
    }

    public static Apply makeApply(String callName, Fraction number) {
        Apply arg = makeApply(number.toString()); // sigh
        return makeApply(callName, arg);
    }

    public static Apply makeApply(String conceptName, Apply... subCalls) {
        return new Apply(conceptName, Arrays.asList(subCalls));
    }
    /** Factory: creates new Apply only if it would differ from this. */
    public Apply build(String callName, List<Apply> children) {
        if (callName==this.callName && compare(children))
            return this;
        return new Apply(callName, children);
    }
    private boolean compare(List<Apply> l) {
        if (getNumberOfChildren() != l.size()) return false;
        AST child = this.getFirstChild();
        for (Apply t: l) {
                if (t != child) return false; // reference equality
                child = child.getNextSibling();
        }
        return true;
    }
}
