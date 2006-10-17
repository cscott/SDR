package net.cscott.sdr.calls.ast;

import net.cscott.sdr.calls.transform.TransformVisitor;
import antlr.CommonAST;

/**
 * A <code>Comp</code> is a composition operator: either a sequence,
 * an option list, a parallel split, or a restriction operator.
 * A top-level <code>Comp</code> is a call definition.  Lower-level
 * <code>Comp</code>s you can think of as call "comp"onents.
 * @author C. Scott Ananian
 * @version $Id: Comp.java,v 1.2 2006-10-17 01:53:57 cananian Exp $
 */
public abstract class Comp extends CommonAST {
    public Comp(int type) {
        super();
        initialize(type, this.getClass().getName().replaceAll("[^.]+[.]",""));
    }
    public abstract <T> Comp accept(TransformVisitor<T> v, T t);
}
