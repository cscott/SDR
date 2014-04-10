package net.cscott.sdr.calls.transform;

import static net.cscott.sdr.calls.parser.CallFileLexer.APPLY;

import java.util.List;
import java.util.Set;

import net.cscott.sdr.calls.BadCallException;
import net.cscott.sdr.calls.DanceState;
import net.cscott.sdr.calls.Evaluator;
import net.cscott.sdr.calls.ExactRotation;
import net.cscott.sdr.calls.ExprFunc.EvaluationException;
import net.cscott.sdr.calls.ast.Apply;
import net.cscott.sdr.calls.ast.AstNode;
import net.cscott.sdr.calls.ast.Comp;
import net.cscott.sdr.calls.ast.Expr;
import net.cscott.sdr.calls.ast.If;
import net.cscott.sdr.calls.ast.In;
import net.cscott.sdr.calls.ast.Opt;
import net.cscott.sdr.calls.ast.OptCall;
import net.cscott.sdr.calls.ast.Par;
import net.cscott.sdr.calls.ast.ParCall;
import net.cscott.sdr.calls.ast.Part;
import net.cscott.sdr.calls.ast.Prim;
import net.cscott.sdr.calls.ast.Seq;
import net.cscott.sdr.calls.ast.SeqCall;
import net.cscott.sdr.util.Fraction;

import org.junit.runner.RunWith;

import net.cscott.jdoctest.JDoctestRunner;

/**
 * Count the number of parts in a given call.  Returns the number of parts
 * if it is defined; throws a {@link BadCallException} if it is ambiguous
 * or ill-defined.  Returns {@link Fraction#ZERO} for the stand-still
 * "nothing" call.
 * @author C. Scott Ananian
 * @doc.test
 *  Count the number of parts in swing thru:
 *  js> importPackage(net.cscott.sdr.calls)
 *  js> ds = new DanceState(new DanceProgram(Program.PLUS), Formation.SQUARED_SET); undefined;
 *  js> call = CallDB.INSTANCE.lookup("swing thru")
 *  swing thru[basic]
 *  js> comp = call.getEvaluator(null, java.util.Arrays.asList()).simpleExpansion(); undefined
 *  js> comp.accept(new PartsCounter(ds), null).toProperString()
 *  2
 * @doc.test
 *  "Little more" has a controversial number of parts; it should throw a
 *  BadCallException.  (Are the two parts "little" and "centers circulate",
 *  or is the first part "centers step and fold while ends quarter right"
 *  and the second part "centers circulate while ends counter rotate 1/4"?)
 *  js> importPackage(net.cscott.sdr.calls)
 *  js> ds = new DanceState(new DanceProgram(Program.C2), Formation.SQUARED_SET); undefined;
 *  js> call = CallDB.INSTANCE.lookup("little more")
 *  little more[c2]
 *  js> comp = call.getEvaluator(null, java.util.Arrays.asList()).simpleExpansion(); undefined
 *  js> try {
 *    >   comp.accept(new PartsCounter(ds), null)
 *    > } catch (e) {
 *    >   print(e.javaException.getMessage())
 *    > }
 *  Controversial division into parts.
 */
@RunWith(value=JDoctestRunner.class)
public class PartsCounter extends ValueVisitor<Fraction, Void> {
    static class CantCountPartsException extends BadCallException {
        CantCountPartsException(String msg) { super(msg); }
    }

    final DanceState ds;
    public PartsCounter(DanceState ds) { this.ds = ds; }

    @Override
    public Fraction visit(Apply apply, Void t) {
        // optimization: some concepts are safe to hoist this concept thru
        if (safeConcepts.contains(apply.call.atom)) {
            if (apply.call.atom.equals("_with designated") ||
                apply.call.atom.equals("_anyone"))
                // two args, subcall is last one
                return new Apply(apply.call.args.get(1)).accept(this, t);
            if (apply.call.atom.equals("_quasi concentric") ||
                apply.call.atom.equals("_o concentric") ||
                apply.call.atom.equals("_concentric") ||
                apply.call.atom.equals("_cross concentric")) {
                // two args, apply to each
                Fraction cnt0=new Apply(apply.call.args.get(0)).accept(this,t);
                Fraction cnt1=new Apply(apply.call.args.get(1)).accept(this,t);
                if (cnt0.equals(cnt1)) {
                    return cnt0;
                } else if (cnt0.equals(Fraction.ZERO)) {
                    return cnt1;
                } else if (cnt1.equals(Fraction.ZERO)) {
                    return cnt0;
                } else {
                    throw new CantCountPartsException("Inconsistent # of parts");
                }
            }
            assert apply.call.args.size()==1;
            return new Apply(apply.call.args.get(0)).accept(this, t);
        }
        // okay, we have to expand the call in order to fractionalize the
        // contents.
        Evaluator e = apply.evaluator(ds);
        if (!e.hasSimpleExpansion())
            throw new CantCountPartsException("Can't expand complex concept to find parts");
        // okay, this concept can be simply expanded...
        return e.simpleExpansion().accept(this, t);
    }

    @Override
    public Fraction visit(Comp c, Void t) {
        assert false : "Unhandled Comp";
        return null;
    }

    @Override
    public Fraction visit(Expr e, Void t) {
        assert false : "shouldn't traverse conditions";
        return null;
    }

    @Override
    public Fraction visit(If iff, Void t) {
        return iff.child.accept(this, t);
    }

    @Override
    public Fraction visit(In in, Void t) {
        return in.child.accept(this, t);
    }

    @Override
    public Fraction visit(Opt opt, Void t) {
        return consistentParts(opt.children, t);
    }
    private Fraction consistentParts(List<? extends AstNode> children, Void t) {
        // list of options.  Make sure they
        // all have the same # of parts!
        Fraction howMany = null;
        for (AstNode node: children) {
            Fraction f = node.accept(this, t);
            if (f.equals(Fraction.ZERO)) {
                // don't count this as a real part
            } else if (howMany==null) {
                howMany=f;
            } else if (!howMany.equals(f)) {
                throw new CantCountPartsException("Inconsistent # of parts");
            }
        }
        return howMany == null ? Fraction.ZERO : howMany;
    }

    @Override
    public Fraction visit(OptCall oc, Void t) {
        return oc.child.accept(this, t);
    }

    @Override
    public Fraction visit(Par par, Void t) {
        // pieces of a call done in parallel
        return consistentParts(par.children, t);
    }

    @Override
    public Fraction visit(ParCall pc, Void t) {
        return pc.child.accept(this, t);
    }

    @Override
    public Fraction visit(Part p, Void t) {
        return partsInSeqCall(p, t);
    }
    private Fraction partsInSeqCall(SeqCall sc, Void t) {
        if (!sc.isIndeterminate()) {
            try {
                return sc.parts().evaluate(Fraction.class, ds);
            } catch (EvaluationException e) {
                assert false : "bug in call definition: "+e;
                // fall through
            }
        }
        throw new CantCountPartsException
            ("Controversial division into parts.");
    }

    @Override
    public Fraction visit(Prim p, Void t) {
        // nothing is a special case; it shouldn't count as an inconsistent
        // # of parts
        if (p.x.equals(Fraction.ZERO) &&
            p.y.equals(Fraction.ZERO) &&
            p.rot.equals(ExactRotation.ZERO))
            return Fraction.ZERO;
        return Fraction.ONE;
    }

    @Override
    public Fraction visit(Seq s, Void t) {
        s = PartsVisitor.desugarAnd(s);
        if (s.children.size() == 1 && s.children.get(0).type==APPLY) {
            // fall through, this is a special case.
            // (one call defined as exactly another call gets the parts of
            //  the other call, instead of a single part.  use an explicit
            //  ipart if you actually wanted to define it as a single part)
            return s.children.get(0).accept(this, t);
        }
        Fraction totalParts = Fraction.ZERO;
        for (SeqCall sc : s.children) {
            Fraction howMany = partsInSeqCall(sc, t);
            totalParts = totalParts.add(howMany);
        }
        return totalParts;
    }

    @Override
    public Fraction visit(SeqCall s, Void t) {
        assert false : "unhandled SeqCall";
        return null;
    }

    /** A list of concepts which do not affect the number of parts.
    That is, "num parts(as couples(swing thru))" == "num parts(swing thru)". */
    static Set<String> safeConcepts = Fractional.safeConcepts;
}
