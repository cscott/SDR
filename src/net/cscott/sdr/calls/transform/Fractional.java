package net.cscott.sdr.calls.transform;

import static net.cscott.sdr.calls.transform.CallFileLexer.PART;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.cscott.jdoctest.JDoctestRunner;
import net.cscott.sdr.calls.BadCallException;
import net.cscott.sdr.calls.DanceState;
import net.cscott.sdr.calls.ExactRotation;
import net.cscott.sdr.calls.ast.Apply;
import net.cscott.sdr.calls.ast.Comp;
import net.cscott.sdr.calls.ast.Expr;
import net.cscott.sdr.calls.ast.In;
import net.cscott.sdr.calls.ast.Part;
import net.cscott.sdr.calls.ast.Prim;
import net.cscott.sdr.calls.ast.Seq;
import net.cscott.sdr.calls.ast.SeqCall;
import net.cscott.sdr.util.Fraction;

import org.junit.runner.RunWith;

/**
 * Transformation implementing
 * {@link net.cscott.sdr.calls.lists.BasicList#_FRACTIONAL}.
 * This only handles the case where the fraction is less than one;
 * {@link net.cscott.sdr.calls.lists.BasicList#_FRACTIONAL} handles
 * the whole-number portions of the fraction.
 * @author C. Scott Ananian
 * @doc.test
 *  Use Fractional class to evaluate TWICE QUARTER RIGHT:
 *  js> importPackage(net.cscott.sdr.calls)
 *  js> importPackage(net.cscott.sdr.calls.ast)
 *  js> importPackage(net.cscott.sdr.util)
 *  js> ds = new DanceState(new DanceProgram(Program.C4), Formation.SQUARED_SET); undefined;
 *  js> callname="dosado"
 *  dosado
 *  js> call = net.cscott.sdr.calls.CallDB.INSTANCE.lookup(callname)
 *  dosado[basic]
 *  js> comp = call.getEvaluator(null, java.util.Arrays.asList()).simpleExpansion()
 *  (In 6 (Opt (From 'FACING DANCERS (Seq (Prim -1, 1, none, 1, SASHAY_START) (Prim 1, 1, none, 1, SASHAY_FINISH) (Prim 1, -1, none, 1, SASHAY_START) (Prim -1, -1, none, 1, SASHAY_FINISH)))))
 *  js> comp.accept(new Fractional(ds), Fraction.ONE_QUARTER)
 *  (In 1 1/2 (Opt (From 'FACING DANCERS (Seq (Prim -1, 1, none, 1, SASHAY_START)))))
 *  js> try {
 *    >   comp.accept(new Fractional(ds), Fraction.ONE_THIRD)
 *    > } catch (e) {
 *    >   print(e.javaException)
 *    > }
 *  net.cscott.sdr.calls.BadCallException: No formation options left: Primitives cannot be subdivided
 */
@RunWith(value=JDoctestRunner.class)
public class Fractional extends TransformVisitor<Fraction> {
    private final DanceState ds;
    public Fractional(DanceState ds) { this.ds = ds; }
    @Override
    public In visit(In in, Fraction f) {
        return in.build(in.count.multiply(f), in.child.accept(this, f));
    }
    @Override
    public Prim visit(Prim p, Fraction f) {
        // nothing can be infinitely subdivided
        if (p.x.equals(Fraction.ZERO) &&
            p.y.equals(Fraction.ZERO) &&
            p.rot.equals(ExactRotation.ZERO))
            return p.scaleTime(f);
        if (Fraction.ONE.equals(f))
            return p;
        throw new BadCallException("Primitives cannot be subdivided");
    }
    @Override
    public Part visit(Part p, Fraction f) {
        if (Fraction.ONE.equals(f))
            return p;
        if (p.isDivisible)
            return (Part) super.visit(p, f);
        throw new BadCallException("Can't divide indivisible part");
    }
    @Override
    public SeqCall visit(Apply apply, Fraction f) {
        if (f.compareTo(Fraction.ONE)==0)
            return apply;
        // optimization: 1/2(1/2(x)) = 1/4(x)
        if (apply.call.atom.equals("_fractional")) {
            // special case; just multiply fractions
            return new Apply
                (new Expr("_fractional",
                          new Expr("_multiply num",
                                   apply.call.args.get(0), Expr.literal(f)),
                          apply.call.args.get(1)));
        }
        // optimization: some concepts are safe to hoist fractionalization thru
        if (safeConcepts.contains(apply.call.atom)) {
            if (apply.call.atom.equals("_with designated"))
                // two args, subcall is last one
                return new Apply
                    (new Expr(apply.call.atom, apply.call.args.get(0),
                              new Expr("_fractional", Expr.literal(f),
                                       apply.call.args.get(1))));
            if (apply.call.atom.equals("_quasi concentric") ||
                apply.call.atom.equals("_concentric") ||
                apply.call.atom.equals("_cross concentric"))
                // two args, fractionalize each
                return new Apply
                    (new Expr(apply.call.atom,
                     new Expr("_fractional", Expr.literal(f), apply.call.args.get(0)),
                     new Expr("_fractional", Expr.literal(f), apply.call.args.get(1))));
            assert apply.call.args.size()==1;
            return new Apply(new Expr(apply.call.atom,
                     new Expr("_fractional", Expr.literal(f), apply.call.args.get(0))));
        }
        // okay, we have to expand the call in order to fractionalize the
        // contents.
        Evaluator e = apply.evaluator(ds);
        if (!e.hasSimpleExpansion())
            throw new BadCallException("Can't fractionalize complex concept");
        // okay, this concept can be simply expanded...
        Part result = new Part(true,e.simpleExpansion().accept(this, f));
        return result;
    }
    /** A list of concepts which it is safe to hoist fractionalization through.
     That is, "1/2(as couples(swing thru))" == "as couples(1/2(swing thru))". */
    private static Set<String> safeConcepts = new HashSet<String>(Arrays.asList(
            "as couples","tandem","_with designated",
            "reverse", "left", "mirror",
            "_quasi concentric", "_concentric", "_cross concentric",
            "concentric", "cross concentric"
            // xxx more fractionalization-safe calls?
            ));
    @Override
    public Comp visit(Seq s, Fraction f) {
        assert f.compareTo(Fraction.ONE) <= 0;
        f = f.multiply(Fraction.valueOf(s.children.size()));
        List<SeqCall> l = new ArrayList<SeqCall>(s.children.size());
        for (SeqCall child : s.children) {
            if (f.compareTo(Fraction.ONE) >= 0) {
                l.add(child);
                f = f.subtract(Fraction.ONE);
            } else if (f.compareTo(Fraction.ZERO) != 0) {
                l.add(child.accept(this, f));
                f = Fraction.ZERO;
            }
        }
        assert f.compareTo(Fraction.ZERO) == 0;
        if (l.isEmpty()) throw new BadCallException("Nothing left in Seq");
        // OPTIMIZATION: SEQ(PART(c)) = c
        if (l.size()==1 && l.get(0).type==PART) {
            Part p = (Part) l.get(0);
            if (p.isDivisible) return p.child;
        }
        return s.build(l);
    }
}
