package net.cscott.sdr.calls.ast;

import static net.cscott.sdr.calls.transform.AstTokenTypes.IF;

import org.junit.runner.RunWith;

import net.cscott.jdoctest.JDoctestRunner;
import net.cscott.sdr.calls.transform.TransformVisitor;
import net.cscott.sdr.calls.transform.ValueVisitor;
import net.cscott.sdr.util.Fraction;

/** <code>If</code> rejects its child as suitable for execution from
 * the current formation unless its condition evaluates true.
 * @author C. Scott Ananian
 * @doc.test An If with no message
 *  js> iff = new If(If.When.BEFORE, new Expr("true"), new Seq(Apply.makeApply("nothing")));
 *  (If 'BEFORE (Expr true) (Seq (Apply 'nothing)))
 * @doc.test An If with a message and the default priority:
 *  js> iff = new If(If.When.AFTER, new Expr("true"), new Seq(Apply.makeApply("nothing")), "Message!");
 *  (If 'AFTER (Expr true) (Seq (Apply 'nothing)) "Message!")
 * @doc.test An If with a user-specified priority:
 *  js> importPackage(net.cscott.sdr.util)
 *  js> iff = new If(If.When.BEFORE, new Expr("true"), new Seq(Apply.makeApply("nothing")), "Message!", Fraction.ONE_HALF);
 *  (If 'BEFORE (Expr true) (Seq (Apply 'nothing)) "Message!" 1/2)
 */
@RunWith(value=JDoctestRunner.class)
public class If extends Comp {
    /** When to evaluate an {@link If}: before or after its child. */
    public enum When { BEFORE, AFTER };
    /** Evaluate condition before or after the child is evaluated? */
    public final When when;
    /** The condition to evaluate.  Should yield a boolean. */
    public final Expr condition;
    /** The child to evaluate, iff the condition is true. */
    public final Comp child;
    /** User-friendly message to report if the condition fails, or null. */
    public final String message;
    /** Priority of the message: if a parallel operation causes failures in
     * multiple places, the one with the highest priority will be reported.
     * The default priority is 1 if a message is specified, or else 0.
     * Negative priorities can be used to specify nonfatal warnings.*/
    public final Fraction priority;
    
    public If(When when, Expr condition, Comp child) {
        this(when, condition, child, null);
    }
    public If(When when, Expr condition, Comp child, String msg) {
        this(when, condition, child, msg,
             (msg==null) ? Fraction.ZERO : Fraction.ONE/* default priority */);
    }
    public If(When when, Expr condition, Comp child, String msg, Fraction priority) {
        super(IF);
        this.when = when;
        this.condition = condition;
        this.child = child;
        this.message = msg;
        this.priority = priority;
	assert this.when != null;
        // we don't print out correctly if msg==null and priority is nonzero
        assert this.message!=null || priority.compareTo(Fraction.ZERO)==0;
    }
    @Override
    public <T> Comp accept(TransformVisitor<T> v, T t) {
        return v.visit(this, t);
    }
    @Override
    public <RESULT,CLOSURE>
    RESULT accept(ValueVisitor<RESULT,CLOSURE> v, CLOSURE cl) {
        return v.visit(this, cl);
    }
    /** Factory: creates new If only if it would differ from this. */
    public If build(Expr condition, Comp child) {
        if (this.condition==condition && this.child==child)
            return this;
        return new If(when, condition, child, message, priority);
    }
    @Override
    public String argsToString() {
        StringBuilder sb = new StringBuilder();
        switch (when) {
        case AFTER: sb.append("'AFTER "); break;
        case BEFORE: sb.append("'BEFORE "); break;
        }
        sb.append(condition.toString()+" "+child.toString());
        if (message!=null) {
            sb.append(" \""+message.toString()+"\""); // no escaping
            if (priority.compareTo(Fraction.ONE)!=0)
                sb.append(" "+priority.toProperString());
        }
        return sb.toString();
    }
}
