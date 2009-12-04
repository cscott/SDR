package net.cscott.sdr.calls.transform;

import static net.cscott.sdr.calls.transform.AstTokenTypes.PART;

import java.util.*;

import net.cscott.sdr.calls.BadCallException;
import net.cscott.sdr.calls.ast.*;

/**
 * {@link TransformVisitor} is a superclass to eliminate
 * common code when writing tree transformations.
 * @author C. Scott Ananian
 * @version $Id: TransformVisitor.java,v 1.4 2006-10-18 21:14:44 cananian Exp $
 */
public abstract class TransformVisitor<T> {
    public SeqCall visit(Apply apply, T t) {
        return apply.build(apply.call.accept(this, t));
    }
//  public Comp visit(Comp c, T t) { }
    public Expr visit(Expr e, T t) {
        List<Expr> l = new ArrayList<Expr>(e.args.size());
        for (Expr ee : e.args) {
            l.add(ee.accept(this, t));
        }
        return e.build(e.atom, l);
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
        return oc.build(oc.matcher.accept(this, t), oc.child.accept(this, t));
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
            ("No dancer matchers left: "+bce.getMessage());
        return p.build(l);
    }
    public ParCall visit(ParCall pc, T t) {
        return pc.build(pc.selector.accept(this, t), pc.child.accept(this, t));
    }
    public SeqCall visit(Part p, T t) {
        return p.build(p.divisibility, p.howMany.accept(this, t), p.child.accept(this, t));
    }
    public SeqCall visit(Prim p, T t) {
        return p; // leaf
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
}
