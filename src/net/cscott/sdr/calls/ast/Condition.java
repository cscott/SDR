package net.cscott.sdr.calls.ast;

import static net.cscott.sdr.calls.ast.TokenTypes.CONDITION;

import java.util.*;

import net.cscott.sdr.calls.*;
import net.cscott.sdr.util.Fraction;

/** <code>Condition</code> represents an invocation of a {@link Predicate}
 * with zero or more arguments.  The arguments can be text strings, numbers, or
 * sub-conditions; see the {@link Apply} class for the basic idea.
 * String and number arguments are stored as zero-argument conditions.
 * @author C. Scott Ananian
 * @version $Id: Condition.java,v 1.2 2006-10-11 18:50:38 cananian Exp $
 */
public class Condition extends SeqCall {
    public final String condition;
    public Condition(String condition, List<Condition> args) {
        super(CONDITION);
        this.condition = condition;
        for (Condition c : args)
            addChild(c);
    }
    public String toString() {
        return super.toString()+"["+condition+"]";
    }

    public Condition getArg(int n) {
        Condition c = (Condition) getFirstChild();
        for (int i=0; i<n; i++)
            c = (Condition) c.getNextSibling();
        return c;
    }
    public Fraction getNumberArg(int n) {
        return Fraction.valueOf(getArg(n).condition);
    }
    public String getStringArg(int n) {
        return getArg(n).condition;
    }
    // XXX getSelectorArg, etc?
    
    // factories.
    public static Condition makeCondition(String condition) {
        return new Condition(condition, Collections.<Condition>emptyList());
    }
    public static Condition makeCondition(String condition, Fraction number) {
        Condition arg = makeCondition(number.toString()); // sigh
        return makeCondition(condition, arg);
    }
    public static Condition makeCondition(String condition, Condition... subConditions) {
        return new Condition(condition, Arrays.asList(subConditions));
    }
}
