package net.cscott.sdr.calls.ast;

import static net.cscott.sdr.calls.transform.AstTokenTypes.PAR;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.cscott.sdr.calls.transform.TransformVisitor;
import net.cscott.sdr.calls.transform.ValueVisitor;
/**
 * <code>Par</code> is a list of call pieces.  Each piece has
 * an associated selector.  Every member of the formation must match
 * at least one selector.  Each person executes the piece corresponding to
 * the first selector which matches them, in parallel.
 * @author C. Scott Ananian
 * @version $Id: Par.java,v 1.5 2006-10-17 16:29:05 cananian Exp $
 */
public class Par extends Comp {
    public final List<ParCall> children;
    public Par(ParCall... children) {
        super(PAR);
        this.children = Collections.unmodifiableList
        (Arrays.asList(children.clone()));
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
    /** Factory: creates new Par only if it would differ from this. */
    public Par build(List<ParCall> children) {
        if (this.children.equals(children)) return this;
        return new Par(children.toArray(new ParCall[children.size()]));
    }
    @Override
    public String argsToString() {
        StringBuilder sb = new StringBuilder();
        for (ParCall pc : this.children) {
            sb.append(pc.toString());
            sb.append(' ');
        }
        return sb.toString();
    }
}
