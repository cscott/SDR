package net.cscott.sdr.calls.transform;

import static net.cscott.sdr.calls.ast.Part.Divisibility.DIVISIBLE;
import static net.cscott.sdr.calls.parser.AstTokenTypes.APPLY;

import java.util.ArrayList;
import java.util.Set;

import net.cscott.sdr.calls.BadCallException;
import net.cscott.sdr.calls.DanceState;
import net.cscott.sdr.calls.Evaluator;
import net.cscott.sdr.calls.ast.Apply;
import net.cscott.sdr.calls.ast.Expr;
import net.cscott.sdr.calls.ast.Part;
import net.cscott.sdr.calls.ast.Seq;
import net.cscott.sdr.calls.ast.SeqCall;
import net.cscott.sdr.util.Fraction;

/**
 * Helper for transformations which treat separate parts of a call,
 * like {@link Fractional}, {@link Finish}, and {@link LikeA}.
 * @author C. Scott Ananian
 */
public class PartsVisitor<T> extends TransformVisitor<T> {
    final String conceptName;
    final Set<String> safeConcepts;
    final DanceState ds;
    PartsVisitor(String conceptName, Set<String> safeConcepts, DanceState ds) {
        this.conceptName = conceptName;
        this.safeConcepts = safeConcepts;
        this.ds = ds;
    }
    Expr applyConcept(T t, Expr... args) {
        /* override if you need to use the closure argument 't' */
        return new Expr(conceptName, args);
    };

    /* (non-Javadoc)
     * @see net.cscott.sdr.calls.transform.TransformVisitor#visit(net.cscott.sdr.calls.ast.Apply, java.lang.Object)
     */
    @Override
    public SeqCall visit(Apply apply, T t) {
        // optimization: some concepts are safe to hoist this concept thru
        if (safeConcepts.contains(apply.call.atom)) {
            if (apply.call.atom.equals("_with designated") ||
                apply.call.atom.equals("_anyone"))
                // two args, subcall is last one
                return new Apply
                    (new Expr(apply.call.atom, apply.call.args.get(0),
                              applyConcept(t, apply.call.args.get(1))));
            if (apply.call.atom.equals("_quasi concentric") ||
                apply.call.atom.equals("_o concentric") ||
                apply.call.atom.equals("_concentric") ||
                apply.call.atom.equals("_cross concentric"))
                // two args, apply to each
                return new Apply
                    (new Expr(apply.call.atom,
                              applyConcept(t, apply.call.args.get(0)),
                              applyConcept(t, apply.call.args.get(1))));
            assert apply.call.args.size()==1;
            return new Apply(new Expr(apply.call.atom,
                                      applyConcept(t, apply.call.args.get(0))));
        }
        // okay, we have to expand the call in order to fractionalize the
        // contents.
        Evaluator e = apply.evaluator(ds);
        if (!e.hasSimpleExpansion())
            throw new BadCallException("Can't expand complex concept to find parts");
        // okay, this concept can be simply expanded...
        Part result = new Part(DIVISIBLE, Fraction.ONE,
                               e.simpleExpansion().accept(this, t));
        return result;
    }
    // useful utility: desugar "and" concept to expose parts
    protected static Seq desugarAnd(Seq s) {
        // desugar 'and' pseudo-concept into parts
        ArrayList<SeqCall> nChildren = new ArrayList<SeqCall>(s.children.size());
        for (SeqCall sc: s.children) {
            if (sc.type==APPLY) {
                Expr call = ((Apply)sc).call;
                if (call.atom.equals("and")) {
                    for (Expr arg: call.args) {
                        nChildren.add(new Apply(arg));
                    }
                    continue;
                }
            }
            nChildren.add(sc);
        }
        return s.build(nChildren);
    }
}
