package net.cscott.sdr.calls.ast;

import net.cscott.sdr.calls.transform.TransformVisitor;

/**
 * A <code>Comp</code> is a composition operator: either a sequence,
 * an option list, a parallel split, or a restriction operator.
 * A top-level <code>Comp</code> is a call definition.  Lower-level
 * <code>Comp</code>s you can think of as call "comp"onents.
 * @author C. Scott Ananian
 * @version $Id: Comp.java,v 1.3 2006-10-17 16:29:05 cananian Exp $
 */
public abstract class Comp extends AstNode {
    public Comp(int type) {
        super(type);
    }
    @Override
    public abstract <T> Comp accept(TransformVisitor<T> v, T t);
}
