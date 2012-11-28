package net.cscott.sdr.calls.transform;

import static net.cscott.sdr.calls.ast.If.When.AFTER;
import static net.cscott.sdr.calls.ast.Part.Divisibility.DIVISIBLE;
import static net.cscott.sdr.calls.parser.CallFileLexer.PART;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.cscott.jdoctest.JDoctestRunner;
import net.cscott.sdr.calls.BadCallException;
import net.cscott.sdr.calls.DanceState;
import net.cscott.sdr.calls.ExactRotation;
import net.cscott.sdr.calls.ExprFunc.EvaluationException;
import net.cscott.sdr.calls.ast.Apply;
import net.cscott.sdr.calls.ast.Comp;
import net.cscott.sdr.calls.ast.Expr;
import net.cscott.sdr.calls.ast.If;
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
 *  (In '6 (Opt (From 'RH MINIWAVE (Seq (Part 'INDIVISIBLE '1 (Seq (Apply '_finish dosado))))) (From 'ANY (Seq (Part 'INDIVISIBLE '1 (Seq (Apply (Expr _maybe circle adjust '_mixed touch)))) (Part 'DIVISIBLE '3 (Seq (Apply '_finish dosado)))))))
 *  js> comp.accept(new Fractional(ds), Fraction.ONE_QUARTER)
 *  (In (Expr _multiply num '1/4 '6) (Opt (From 'ANY (Seq (Part 'INDIVISIBLE '1 (Seq (Apply (Expr _maybe circle adjust '_mixed touch))))))))
 *  js> try {
 *    >   comp.accept(new Fractional(ds), Fraction.ONE_THIRD)
 *    > } catch (e) {
 *    >   print(e.javaException)
 *    > }
 *  net.cscott.sdr.calls.BadCallException: No formation options left: No formation options left: Primitives cannot be subdivided
 */
@RunWith(value=JDoctestRunner.class)
public class Fractional extends PartsVisitor<Fraction> {
    public Fractional(DanceState ds) {
        super("_fractional", safeConcepts, ds);
    }
    Expr applyConcept(Fraction f, Expr... args) {
        assert args.length==1;
        return new Expr(conceptName, Expr.literal(f), args[0]);
    };
    /* Keep initial conditions, but skip 'ends in' conditions. */
    @Override
    public Comp visit(If iff, Fraction f) {
        if (iff.when==AFTER)
            return iff.child.accept(this, f);
        return iff.build(iff.condition.accept(this,f),
                         iff.child.accept(this,f));
    }
    @Override
    public In visit(In in, Fraction f) {
        return in.build(new Expr("_multiply num", Expr.literal(f), in.count),
                        in.child.accept(this, f));
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
        switch (p.divisibility) {
        case DIVISIBLE:
            return (Part) super.visit(p, f);
        case INDETERMINATE:
            assert false : "should never recurse into seq containing indeterminate part";
        default:
            assert false : "case not handled in divisibility enum";
        case INDIVISIBLE:
            throw new BadCallException("Can't divide indivisible part");
        }
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
        // use superclass implementation (handle safe concepts, expansion)
        return super.visit(apply, f);
    }
    /** A list of concepts which it is safe to hoist fractionalization through.
     That is, "1/2(as couples(swing thru))" == "as couples(1/2(swing thru))". */
    static Set<String> safeConcepts = new HashSet<String>(Arrays.asList(
            "as couples","tandem","_with designated","_anyone",
            "reverse", "left", "mirror",
            "_quasi concentric", "_concentric", "_cross concentric",
            "concentric", "cross concentric"
            // xxx more fractionalization-safe calls?
            ));
    @Override
    public Comp visit(Seq s, Fraction f) {
        if (f.compareTo(Fraction.ONE)==0)
            return s;
        assert f.compareTo(Fraction.ONE) < 0;
        s = desugarAnd(s);
        // go through the children and count up parts, accounting for the
        // 'howMany' field of any Parts.
        Fraction totalParts = Fraction.ZERO;
        List<Fraction> childParts = new ArrayList<Fraction>(s.children.size());
        for (SeqCall child : s.children) {
            Fraction part;
            if (child.isIndeterminate())
                throw new BadCallException
                    ("Number of parts is not well-defined.");
            try {
                part = child.parts().evaluate(Fraction.class, ds);
            } catch (EvaluationException e) {
                assert false : "bad call definition";
                throw new BadCallException("Can't evaluate number of parts");
            }
            childParts.add(part); // list addition
            totalParts = totalParts.add(part); // numeric addition
        }

        f = f.multiply(totalParts);
        List<SeqCall> l = new ArrayList<SeqCall>(s.children.size());
        Iterator<Fraction> it = childParts.iterator();
        for (SeqCall child : s.children) {
            Fraction part = it.next();
            if (f.compareTo(part) >= 0) {
                l.add(child);
                f = f.subtract(part);
            } else if (f.compareTo(Fraction.ZERO) > 0) {
                l.add(child.accept(this, f.divide(part)));
                f = Fraction.ZERO;
            }
        }
        assert f.compareTo(Fraction.ZERO) == 0;
        if (l.isEmpty()) throw new BadCallException("Nothing left in Seq");
        // OPTIMIZATION: SEQ(PART(c)) = c
        if (l.size()==1 && l.get(0).type==PART) {
            Part p = (Part) l.get(0);
            if (p.divisibility==DIVISIBLE &&
                p.howMany.isConstant(Fraction.class)) {
                try {
                    if (p.howMany.evaluate(Fraction.class, ds)
                            .compareTo(Fraction.ONE) == 0)
                        return p.child;
                } catch (EvaluationException e) {
                    assert false : "constant shouldn't be failing to eval";
                }
            }
        }
        return s.build(l);
    }
}
