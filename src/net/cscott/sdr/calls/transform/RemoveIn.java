// Remove 'In's from a call tree by pushing them down and adjusting Prim timing
// Step 2: Using the 'inherent' timings given by a BeatCounter, proportionally
//         allocate beats top-down.
package net.cscott.sdr.calls.transform;

import net.cscott.sdr.calls.ast.*;
import net.cscott.sdr.util.*;
import java.util.*;
/** Propagate 'inherent' time bottom-up: where prim and part = 1, and IN resets
 * to its spec, whatever that is. */
public class RemoveIn extends TransformVisitor<Fraction> {
    private final BeatCounter bc;
    private RemoveIn(BeatCounter bc) { this.bc = bc; }

    /** Main method: pass in a comp, and get out a Comp without In nodes. */
    public static Comp removeIn(Comp c) {
        BeatCounter bc = new BeatCounter(c);
        RemoveIn ri = new RemoveIn(bc);
        return c.accept(ri,null);
    }

    // f is target # of beats
    @Override
    public Seq visit(Seq s, Fraction f) {
        assert f != null : "Seq reached without passing outer In";
        List<SeqCall> l = new ArrayList<SeqCall>(s.children.size());
        Fraction old = bc.getBeats(s);
        Fraction scale = f.divide(old);
        for (SeqCall sc : s.children)
            l.add(sc.accept(this, scale));
        return s.build(l);
    }
    // note: parameter is scaling factor
    @Override
    public Prim visit(Prim p, Fraction scale) {
        /* apply scaling factor to prim */
        return p.scaleTime(scale); // will return same obj if scale==1
    }
    // note: parameter is scaling factor
    @Override
    public Apply visit(Apply a, Fraction scale) {
        assert false : "calls shouldn't be present in simplified tree";
        return null;
    }
    // note: parameter is scaling factor
    @Override
    public Part visit(Part part, Fraction scale) {
        /* get old inherent length of pieces */
        Fraction old = bc.getBeats(part.child);
        /* scale this to get new length */
        Fraction f = old.multiply(scale);
        return part.build(part.isDivisible, part.child.accept(this, f));
    }
    // pass timing straight down Par: this will cause all sections of the
    // par to finish at the same time; revisit this (make it more like Seq
    // if that turns out not to be the right thing to do).
    // note: f is target # of beats.
    @Override
    public Par visit(Par p, Fraction f) {
        List<ParCall> l=new ArrayList<ParCall>();
        for (ParCall pc : p.children)
            l.add(pc.accept(this, f));
        return p.build(l);
    }
    // f is target # of beats
    @Override
    public Comp visit(In in, Fraction f) {
        if (f==null) f=in.count; // only the outer In matters
        return in.child.accept(this, f);// note that this removes the 'In' node.
    }
}