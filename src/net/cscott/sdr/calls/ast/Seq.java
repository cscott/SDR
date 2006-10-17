package net.cscott.sdr.calls.ast;

import static net.cscott.sdr.calls.ast.TokenTypes.SEQ;

import java.util.List;

import net.cscott.sdr.calls.transform.TransformVisitor;

import antlr.collections.AST;
/**
 * <code>Seq</code> is the serial composition of primitive call pieces.
 * @author C. Scott Ananian
 * @version $Id: Seq.java,v 1.4 2006-10-17 01:53:57 cananian Exp $
 */
public class Seq extends Comp {
    public Seq(SeqCall... children) {
        super(SEQ);
        for (SeqCall sc : children)
            addChild(sc);
    }
    public <T> Comp accept(TransformVisitor<T> v, T t) {
        return v.visit(this, t);
    }
    /** Factory: creates new Seq only if it would differ from this. */
    public Seq build(List<SeqCall> children) {
        if (compare(children)) return this;
        return new Seq(children.toArray(new SeqCall[children.size()]));
    }
    private boolean compare(List<SeqCall> l) {
        if (getNumberOfChildren() != l.size()) return false;
        AST child = this.getFirstChild();
        for (SeqCall t: l) {
                if (t != child) return false; // reference equality
                child = child.getNextSibling();
        }
        return true;
    }
}
