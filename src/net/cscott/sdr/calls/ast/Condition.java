package net.cscott.sdr.calls.ast;

import static net.cscott.sdr.calls.transform.AstTokenTypes.CONDITION;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.cscott.sdr.calls.Predicate;
import net.cscott.sdr.calls.transform.TransformVisitor;
import net.cscott.sdr.calls.transform.ValueVisitor;
import net.cscott.sdr.util.Fraction;

/** <code>Condition</code> represents an invocation of a {@link Predicate}
 * with zero or more arguments.  The arguments can be text strings, numbers, or
 * sub-conditions; see the {@link Apply} class for the basic idea.
 * String and number arguments are stored as zero-argument conditions.
 * @author C. Scott Ananian
 * @version $Id: Condition.java,v 1.6 2006-10-17 16:29:05 cananian Exp $
 */
public class Condition extends AstNode {
    public final String predicate;
    public final List<Condition> args;
    public Condition(String predicate, List<Condition> args) {
        super(CONDITION);
        this.predicate = predicate;
        this.args = Collections.unmodifiableList
        (Arrays.asList(args.toArray(new Condition[args.size()])));
    }
    public <T> Condition accept(TransformVisitor<T> v, T t) {
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
        sb.append(predicate);
        sb.append(' ');
        for (Condition a : args) {
            sb.append(a.toString());
            sb.append(' ');
        }
        return sb.toString();
    }
    public Predicate getPredicate() {
        return Predicate.lookup(predicate);
    }
    
    public Condition getArg(int n) { return args.get(n); }

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
    public Condition build(String predicate, List<Condition> args) {
        if (this.predicate.equals(predicate) && this.args.equals(args))
            return this;
        return new Condition(predicate, args);
    }
}
