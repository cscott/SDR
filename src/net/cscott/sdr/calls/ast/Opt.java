package net.cscott.sdr.calls.ast;

import static net.cscott.sdr.calls.parser.AstTokenTypes.OPT;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.cscott.sdr.calls.transform.TransformVisitor;
import net.cscott.sdr.calls.transform.ValueVisitor;
/**
 * {@link Opt} is a list of call options.  Each option has
 * an associated formation.  This first option whose formation is matchable
 * against the current formation is used to perform the call; the rest are
 * ignored.
 * @author C. Scott Ananian
 * @version $Id: Opt.java,v 1.6 2006-10-19 18:44:50 cananian Exp $
 */
public class Opt extends Comp {
    public final List<OptCall> children;
    public Opt(OptCall... children) {
        super(OPT);
        this.children = Collections.unmodifiableList
        (Arrays.asList(children.clone()));
    }
    public Opt(List<OptCall> children) {
        this(children.toArray(new OptCall[children.size()]));
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
    /** Factory: creates new Opt only if it would differ from this. */
    public Opt build(List<OptCall> children) {
        if (this.children.equals(children)) return this;
        return new Opt(children.toArray(new OptCall[children.size()]));
    }
    @Override
    public String argsToString() {
        StringBuilder sb = new StringBuilder();
        for (OptCall oc : this.children) {
            sb.append(oc.toString());
            sb.append(' ');
        }
        return sb.toString();
    }
}
