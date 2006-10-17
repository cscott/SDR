package net.cscott.sdr.calls.ast;

import static net.cscott.sdr.calls.ast.TokenTypes.PAR;

import java.util.List;

import net.cscott.sdr.calls.transform.TransformVisitor;

import antlr.collections.AST;
/**
 * <code>Par</code> is a list of call pieces.  Each piece has
 * an associated selector.  Every member of the formation must match
 * at least one selector.  Each person executes the piece corresponding to
 * the first selector which matches them, in parallel.
 * @author C. Scott Ananian
 * @version $Id: Par.java,v 1.4 2006-10-17 01:53:57 cananian Exp $
 */
public class Par extends Comp {
    private final ParCall[] children;
    public Par(ParCall... children) {
        super(PAR);
        this.children = children;
        for (ParCall pc : children)
            addChild(pc);
    }
    public <T> Comp accept(TransformVisitor<T> v, T t) {
        return v.visit(this, t);
    }
    /** Factory: creates new Par only if it would differ from this. */
    public Par build(List<ParCall> children) {
        if (compare(children)) return this;
        return new Par(children.toArray(new ParCall[children.size()]));
    }
    private boolean compare(List<ParCall> l) {
        if (getNumberOfChildren() != l.size()) return false;
        AST child = this.getFirstChild();
        for (ParCall t: l) {
                if (t != child) return false; // reference equality
                child = child.getNextSibling();
        }
        return true;
    }
}
