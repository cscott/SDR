package net.cscott.sdr.calls.transform;

import net.cscott.sdr.calls.ast.*;

/**
 * {@link ValueVisitor} is a superclass to eliminate
 * common code when writing tree transformations.
 * @author C. Scott Ananian
 * @version $Id: ValueVisitor.java,v 1.2 2006-10-25 20:43:28 cananian Exp $
 */
public abstract class ValueVisitor<RESULT, CLOSURE> {
    public abstract RESULT visit(Apply apply, CLOSURE t);
    public abstract RESULT visit(Comp c, CLOSURE t);
    public abstract RESULT visit(Condition c, CLOSURE t);
    public abstract RESULT visit(Expr e, CLOSURE t);
    public abstract RESULT visit(If iff, CLOSURE t);
    public abstract RESULT visit(In in, CLOSURE t);
    public abstract RESULT visit(Opt opt, CLOSURE t);
    public abstract RESULT visit(OptCall oc, CLOSURE t);
    public abstract RESULT visit(Par p, CLOSURE t);
    public abstract RESULT visit(ParCall pc, CLOSURE t);
    public abstract RESULT visit(Part p, CLOSURE t);
    public abstract RESULT visit(Prim p, CLOSURE t);
    public abstract RESULT visit(Seq s, CLOSURE t);
    public abstract RESULT visit(SeqCall s, CLOSURE t);
}
