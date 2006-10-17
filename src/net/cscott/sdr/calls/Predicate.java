package net.cscott.sdr.calls;

import net.cscott.sdr.calls.ast.Condition;

/** A {@link Predicate} is a boolean test on the current formation and dance
 * state, or else an operator on such boolean tests.  For example, there
 * are simple predicates such as "PROGRAM AT LEAST(foo)" which takes as an
 * argument some dance program <i>foo</i>, as well as "and" and "or" predicates
 * which combine other predicate to yield a boolean result.  The
 * {@link Condition} object in the call tree represents the unevaluated
 * arguments of a {@code Predicate} application.
 * @author C. Scott Ananian
 * @version $Id: Predicate.java,v 1.3 2006-10-17 16:29:01 cananian Exp $
 */
public abstract class Predicate {
    /** The name of this predicate, in our internal jargon.  This is meant to
     *  be human-readable, but is likely not exactly anything a
     *  caller would say. */
    public abstract String getName();

    /** Evaluates this predicate with the arguments given in the
     * {@link Condition} node, returning a boolean.  Note that the
     * {@link Condition#predicate predicate} field of {@code c} should
     * match @{link Predicate#getName(String) this.getName()}.
     */
    public abstract boolean evaluate(DanceState ds, Formation f, Condition c);
    
    @Override
    public final String toString() {
        return getName();
    }
    @Override
    public final int hashCode() { return getName().hashCode(); }
    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof Predicate)) return false;
        Predicate c = (Predicate) o;
        return this.getName().equals(c.getName());
    }
    
    /** Lookup a {@link Predicate} in the {@link PredicateList}. */
    public static Predicate lookup(String s) {
        try {
            return (Predicate) PredicateList.class.getField
            (s.toUpperCase().replace(' ','_'))
            .get(null);
        } catch (Exception e) {
            throw new IllegalArgumentException("Bad predicate: "+s);
        }
    }
}
