package net.cscott.sdr.calls.transform;

import java.util.*;
import net.cscott.sdr.calls.ast.*;

/**
 * {@link TransformVisitor} is a superclass to eliminate
 * common code when writing tree transformations.
 * @author C. Scott Ananian
 * @version $Id: TransformVisitor.java,v 1.1 2006-10-17 01:54:00 cananian Exp $
 */
public abstract class TransformVisitor<T> {
    public SeqCall visit(Apply apply, T t) {
        List<Apply> l = new ArrayList<Apply>(apply.getNumberOfChildren());
        for (Apply firstChild=(Apply)apply.getFirstChild();
            firstChild!=null;
            firstChild=(Apply)firstChild.getNextSibling()) {
            l.add((Apply)firstChild.accept(this, t));
        }
        return apply.build(apply.callName, l);
    }
//  public Comp visit(Comp c, T t) { }
    public Condition visit(Condition c, T t) {
        List<Condition> l = new ArrayList<Condition>(c.getNumberOfChildren());
        for (Condition firstChild=(Condition)c.getFirstChild();
            firstChild!=null;
            firstChild=(Condition)firstChild.getNextSibling()) {
            l.add(firstChild.accept(this, t));
        }
        return c.build(c.predicate, l);
    }
    public Comp visit(If iff, T t) {
        return iff.build(iff.getCondition().accept(this,t),
                    iff.getChild().accept(this,t));
    }
    public Comp visit(In in, T t) {
        Comp c = (Comp) in.getFirstChild();
        return in.build(in.count, c.accept(this, t));
    }
    public Comp visit(Opt opt, T t) {
        List<OptCall> l = new ArrayList<OptCall>(opt.getNumberOfChildren());
        for (OptCall firstChild=(OptCall)opt.getFirstChild();
            firstChild!=null;
            firstChild=(OptCall)firstChild.getNextSibling()) {
            l.add(firstChild.accept(this, t));
        }
        return opt.build(l);
    }
    public OptCall visit(OptCall oc, T t) { 
        Comp c = (Comp) oc.getFirstChild();
        return oc.build(oc.getSelectors(), c.accept(this, t));
    }
    public Comp visit(Par p, T t) {
        List<ParCall> l = new ArrayList<ParCall>(p.getNumberOfChildren());
        for (ParCall firstChild=(ParCall)p.getFirstChild();
            firstChild!=null;
            firstChild=(ParCall)firstChild.getNextSibling()) {
            l.add(firstChild.accept(this, t));
        }
        return p.build(l);
    }
    public ParCall visit(ParCall pc, T t) {
        Comp c = (Comp) pc.getFirstChild();
        return pc.build(pc.tags, c.accept(this, t));
    }
    public SeqCall visit(Part p, T t) {
        Comp c = (Comp)p.getFirstChild();
        return p.build(p.isDivisible, c.accept(this, t));
    }
    public SeqCall visit(Prim p, T t) {
        return p.build(p.x, p.y, p.rot, p.time);
    }
    public Comp visit(Seq s, T t) {
        List<SeqCall> l = new ArrayList<SeqCall>(s.getNumberOfChildren());
        for (SeqCall firstChild=(SeqCall)s.getFirstChild();
            firstChild!=null;
            firstChild=(SeqCall)firstChild.getNextSibling()) {
            l.add(firstChild.accept(this, t));
        }
        return s.build(l);
    }
    //public SeqCall visit(SeqCall sc, T t) { }
    public Comp visit(Warped w, T t) {
        Comp c = (Comp)w.getFirstChild();
        return w.build(w.warp, c.accept(this, t));
    }
}
