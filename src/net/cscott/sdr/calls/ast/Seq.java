package net.cscott.sdr.calls.ast;

import static net.cscott.sdr.calls.transform.AstTokenTypes.SEQ;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.cscott.sdr.calls.transform.TransformVisitor;
import net.cscott.sdr.calls.transform.ValueVisitor;
/**
 * <code>Seq</code> is the serial composition of primitive call pieces.
 * @author C. Scott Ananian
 * @version $Id: Seq.java,v 1.5 2006-10-17 16:29:05 cananian Exp $
 */
public class Seq extends Comp {
    public final List<SeqCall> children;
    public Seq(SeqCall... children) {
        super(SEQ);
        this.children = Collections.unmodifiableList
        (Arrays.asList(children.clone()));
    }
    public Seq(List<SeqCall> children) {
        this(children.toArray(new SeqCall[children.size()]));
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
    /** Factory: creates new Seq only if it would differ from this. */
    public Seq build(List<SeqCall> children) {
        if (this.children.equals(children)) return this;
        return new Seq(children);
    }
    @Override
    public String argsToString() {
        StringBuilder sb = new StringBuilder();
        for (SeqCall sc : this.children) {
            sb.append(sc.toString());
            sb.append(' ');
        }
        return sb.toString();
    }
}
