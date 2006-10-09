package net.cscott.sdr.calls.ast;

import antlr.CommonAST;

/**
 * A <code>Comp</code> is a composition operator: either a sequence,
 * an option list, a parallel split, or a restriction operator.
 * A top-level <code>Comp</code> is a call definition.  Lower-level
 * <code>Comp</code>s you can think of as call "comp"onents.
 * @author C. Scott Ananian
 * @version $Id: Comp.java,v 1.1 2006-10-09 19:57:11 cananian Exp $
 */
public abstract class Comp extends CommonAST {
    public Comp(int type) {
        super();
        initialize(type, this.getClass().getName().replaceAll("[^.]+[.]",""));
    }
}
