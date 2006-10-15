package net.cscott.sdr.calls.ast;

import static net.cscott.sdr.calls.ast.TokenTypes.CONDITION;

import java.util.*;

import antlr.collections.AST;

import net.cscott.sdr.calls.*;
import net.cscott.sdr.util.Fraction;

/** <code>Condition</code> represents an invocation of a {@link Predicate}
 * with zero or more arguments.  The arguments can be text strings, numbers, or
 * sub-conditions; see the {@link Apply} class for the basic idea.
 * String and number arguments are stored as zero-argument conditions.
 * @author C. Scott Ananian
 * @version $Id: Condition.java,v 1.4 2006-10-15 03:15:04 cananian Exp $
 */
public class Condition extends SeqCall {
    public final String predicate;
    public Condition(String predicate, List<Condition> args) {
        super(CONDITION);
        this.predicate = predicate;
        for (Condition c : args)
            addChild(c);
    }
    public String toString() {
        return super.toString()+"["+predicate+"]";
    }
    public Predicate getPredicate() {
        return Predicate.lookup(predicate);
    }
    
    public Condition getArg(int n) {
        Condition c = (Condition) getFirstChild();
        for (int i=0; i<n; i++)
            c = (Condition) c.getNextSibling();
        return c;
    }
    public Fraction getNumberArg(int n) {
        return Fraction.valueOf(getArg(n).predicate);
    }
    public String getStringArg(int n) {
        return getArg(n).predicate;
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
    /** Factory: creates new Condition only if it would differ from this. */
    public Condition build(String predicate, List<Condition> children) {
        if (predicate==this.predicate && compare(children))
            return this;
        return new Condition(predicate, children);
    }
    private boolean compare(List<Condition> l) {
        if (getNumberOfChildren() != l.size()) return false;
        AST child = this.getFirstChild();
        for (Condition t: l) {
                if (t != child) return false; // reference equality
                child = child.getNextSibling();
        }
        return true;
    }
}
