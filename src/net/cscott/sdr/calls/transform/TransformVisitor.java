package net.cscott.sdr.calls.transform;

import java.util.*;

import net.cscott.sdr.calls.BadCallException;
import net.cscott.sdr.calls.ast.*;

/**
 * {@link TransformVisitor} is a superclass to eliminate
 * common code when writing tree transformations.
 * @author C. Scott Ananian
 * @version $Id: TransformVisitor.java,v 1.2 2006-10-17 16:29:06 cananian Exp $
 */
public abstract class TransformVisitor<T> {
    public SeqCall visit(Apply apply, T t) {
        List<Apply> l = new ArrayList<Apply>(apply.args.size());
        for (Apply arg : apply.args) {
            l.add((Apply)arg.accept(this, t));
        }
        return apply.build(apply.callName, l);
    }
//  public Comp visit(Comp c, T t) { }
    public Condition visit(Condition c, T t) {
        List<Condition> l = new ArrayList<Condition>(c.args.size());
        for (Condition cc : c.args) {
            l.add(cc.accept(this, t));
        }
        return c.build(c.predicate, l);
    }
    public Comp visit(If iff, T t) {
        return iff.build(iff.condition.accept(this,t),
                    iff.child.accept(this,t));
    }
    public Comp visit(In in, T t) {
        return in.build(in.count, in.child.accept(this, t));
    }
    public Comp visit(Opt opt, T t) {
        BadCallException bce=null;
        List<OptCall> l = new ArrayList<OptCall>(opt.children.size());
        for (OptCall oc : opt.children) {
            try {
                l.add(oc.accept(this, t));
            } catch (BadCallException ex) {
                // ignore the exception; just don't add it to the list
                bce = ex; // save the last exception for diagnostics
            }
        }
        if (l.isEmpty()) // all options have been exhausted
            throw new BadCallException
            ("No formation options left: "+bce.getMessage());
        return opt.build(l);
    }
    public OptCall visit(OptCall oc, T t) { 
        return oc.build(oc.selectors, oc.child.accept(this, t));
    }
    public Comp visit(Par p, T t) {
        BadCallException bce=null;
        List<ParCall> l = new ArrayList<ParCall>(p.children.size());
        for (ParCall pc : p.children) {
            try {
                l.add(pc.accept(this, t));
            } catch (BadCallException ex) {
                // ignore the exception; just don't add it to the list
                bce = ex; // save the last exception for diagnostics
            }
        }
        if (l.isEmpty()) // all options have been exhausted
            throw new BadCallException
            ("No dancer selectors left: "+bce.getMessage());
        return p.build(l);
    }
    public ParCall visit(ParCall pc, T t) {
        return pc.build(pc.tags, pc.child.accept(this, t));
    }
    public SeqCall visit(Part p, T t) {
        return p.build(p.isDivisible, p.child.accept(this, t));
    }
    public SeqCall visit(Prim p, T t) {
        return p.build(p.x, p.y, p.rot, p.time);
    }
    public Comp visit(Seq s, T t) {
        List<SeqCall> l = new ArrayList<SeqCall>(s.children.size());
        for (SeqCall sc : s.children) {
            // note that we don't catch BadCallException:
            // if any call in a Seq is bad, then the whole
            // thing is bad.
            l.add(sc.accept(this, t));
        }
        return s.build(l);
    }
    //public SeqCall visit(SeqCall sc, T t) { }
    public Comp visit(Warped w, T t) {
        return w.build(w.warp, w.child.accept(this, t));
    }
}
