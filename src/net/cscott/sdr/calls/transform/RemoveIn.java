// Remove 'In's from a call tree by pushing them down and adjusting Prim timing
// Step 2: Using the 'inherent' timings given by a BeatCounter, proportionally
//         allocate beats top-down.
package net.cscott.sdr.calls.transform;

import net.cscott.sdr.calls.ast.*;
import net.cscott.sdr.util.*;
import java.util.*;
/**
 * Push {@link In}s down a call tree and adjust
 * {@link Prim} timing where possible.  Only push the {@link In} down one level,
 * so we don't end up doing more work than we need to on the untaken side of an
 * {@link Opt}.  If we end up at a {@link Seq} of {@link Prim}s, use the
 * "inherent" times given by a {@link BeatCounter} to proportionally allocate
 * the available beats from the top down.
 * @doc.test Eliminate In from 1/2 DOSADO:
 *  js> importPackage(net.cscott.sdr.calls.ast)
 *  js> a = Apply.makeApply("_fractional", Apply.makeApply("1/2"), Apply.makeApply("dosado"))
 *  (Apply _fractional (Apply 1/2) (Apply dosado))
 *  js> def = a.expand()
 *  (In 3 (Opt (From [FACING DANCERS] (Seq (Prim -1, 1, none, 1, SASHAY_START) (Prim 1, 1, none, 1, SASHAY_FINISH)))))
 *  js> def = RemoveIn.removeIn(def)
 *  (Opt (From [FACING DANCERS] (In 3 (Seq (Prim -1, 1, none, 1, SASHAY_START) (Prim 1, 1, none, 1, SASHAY_FINISH)))))
 *  js> def = RemoveIn.removeIn(def.children.get(0).child)
 *  (Seq (Prim -1, 1, none, 1 1/2, SASHAY_START) (Prim 1, 1, none, 1 1/2, SASHAY_FINISH))
 * @doc.test Proper handling of Part:
 *  js> importPackage(net.cscott.sdr.calls.ast)
 *  js> a = AstNode.valueOf('(In 1 (Seq (Part false (Seq (Prim 0, 1, none, 1) (Prim 0, 1, in 1/4, 1)))))')
 *  (In 1 (Seq (Part false (Seq (Prim 0, 1, none, 1) (Prim 0, 1, in 1/4, 1)))))
 *  js> RemoveIn.removeIn(a)
 *  (Seq (Part false (Seq (Prim 0, 1, none, 1/2) (Prim 0, 1, in 1/4, 1/2))))
 */
public class RemoveIn extends TransformVisitor<Fraction> {
    private final BeatCounter bc = new BeatCounter();
    private RemoveIn() { }

    /** Main method: pass in a {@link Comp}, and get out a {@link Comp}
     *  without {@link In} nodes. */
    public static Comp removeIn(In in) {
        RemoveIn ri = new RemoveIn();
        return in.child.accept(ri,in.count);
    }

    // f is target # of beats
    @Override
    public Seq visit(Seq s, Fraction f) {
        List<SeqCall> l = new ArrayList<SeqCall>(s.children.size());
        Fraction old = bc.getBeats(s);
        Fraction scale = f.divide(old);
        // can't put an in down directly here, 'cuz an In isn't a SeqCall
        for (SeqCall sc : s.children)
            l.add(sc.accept(this, bc.getBeats(sc).multiply(scale)));
        return s.build(l);
    }
    // f is target # of beats
    @Override
    public Prim visit(Prim p, Fraction f) {
        /* apply scaling factor to prim */
        return p.scaleTime(f.divide(p.time));// will return same obj if scale==1
    }
    // note: f is target # of beats.
    @Override
    public Apply visit(Apply a, Fraction f) {
        // Use the 'in' pseudo-concept; this will add an 'In' node when 'a'
        // gets expanded.
        return Apply.makeApply("_in", f, a);
    }
    // pass timing straight down Par: this will cause all sections of the
    // par to finish at the same time; revisit this (make it more like Seq
    // if that turns out not to be the right thing to do).
    // note: f is target # of beats.
    @Override
    public Par visit(Par p, Fraction f) {
        List<ParCall> l=new ArrayList<ParCall>();
        for (ParCall pc : p.children)
            l.add(new ParCall(pc.tags, new In(f, pc.child)));
        return p.build(l);
    }
    // don't recurse down Opt, so we don't do more work than we need to on
    // untaken branches
    @Override
    public Opt visit(Opt o, Fraction f) {
        List<OptCall> l=new ArrayList<OptCall>();
        for (OptCall oc : o.children)
            l.add(new OptCall(oc.selectors, new In(f, oc.child)));
        return o.build(l);
    }
    // f is target # of beats
    @Override
    public Comp visit(In in, Fraction f) {
        // only the outer In matters
        return in.child.accept(this, f);// note that this removes the 'In' node.
    }
}
