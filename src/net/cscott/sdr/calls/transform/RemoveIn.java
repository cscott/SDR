// Remove 'In's from a call tree by pushing them down and adjusting Prim timing
// Step 2: Using the 'inherent' timings given by a BeatCounter, proportionally
//         allocate beats top-down.
package net.cscott.sdr.calls.transform;

import net.cscott.sdr.calls.ast.*;
import net.cscott.sdr.util.*;
import java.util.*;
/**
 * Remove {@link In}s from a call tree by pushing them down and adjusting
 * {@link Prim} timing. We use the "inherent" times given by a
 * {@link BeatCounter} to proportionally allocate the available beats from
 * the top down.
 * @doc.test Eliminate In from 1/2 DOSADO:
 *  js> importPackage(net.cscott.sdr.calls.ast)
 *  js> a = Apply.makeApply("_fractional", Apply.makeApply("1/2"), Apply.makeApply("dosado"))
 *  (Apply _fractional (Apply 1/2) (Apply dosado))
 *  js> def = a.expand()
 *  (In 3 (Opt (From [FACING DANCERS] (Seq (Prim -1, 1, none, 1) (Prim 1, 1, none, 1)))))
 *  js> RemoveIn.removeIn(def)
 *  (Opt (From [FACING DANCERS] (Seq (Prim -1, 1, none, 1 1/2) (Prim 1, 1, none, 1 1/2))))
 */
public class RemoveIn extends TransformVisitor<Fraction> {
    private final BeatCounter bc;
    private RemoveIn(BeatCounter bc) { this.bc = bc; }

    /** Main method: pass in a {@link Comp}, and get out a {@link Comp}
     *  without {@link In} nodes. */
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
