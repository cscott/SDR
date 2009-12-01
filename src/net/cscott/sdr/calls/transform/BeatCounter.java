// Remove 'In's from a call tree by pushing them down and adjusting Prim timing
// Step 1: Compute the 'inherent' timing of the subtrees; this will be used
//         to proportionally allocate the number of beats we will be given.
package net.cscott.sdr.calls.transform;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.cscott.jdoctest.JDoctestRunner;
import net.cscott.sdr.calls.DanceState;
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
import static net.cscott.sdr.util.Tools.s;

import org.junit.runner.RunWith;

/**
 * Propagate 'inherent' time bottom-up: where prim and part = 1, and IN resets
 * to its spec, whatever that is.  This is used by {@link RemoveIn} to
 * allocate beats appropriately when rescaling.
 * @doc.test Simple SEQ:
 *  js> importPackage(net.cscott.sdr.calls)
 *  js> importPackage(net.cscott.sdr.calls.ast)
 *  js> s = AstNode.valueOf("(Seq (Prim -1, 1, none, 1 1/2) (Prim 1, 1, none, 1 1/2))")
 *  (Seq (Prim -1, 1, none, 1 1/2) (Prim 1, 1, none, 1 1/2))
 *  js> ds = new DanceState(new DanceProgram(Program.C4), Formation.SQUARED_SET); undefined;
 *  js> bc = new BeatCounter(ds); undefined
 *  js> // invoking this package-scope method is going to be a little painful
 *  js> astcls = java.lang.Class.forName('net.cscott.sdr.calls.ast.AstNode')
 *  class net.cscott.sdr.calls.ast.AstNode
 *  js> m=bc.getClass().getMethod('getBeats', astcls); undefined
 *  js> m.setAccessible(true) // this is the key
 *  js> m.invoke(bc, s).toProperString() // bc.getBeats(s).toProperString()
 *  3
 */
@RunWith(value=JDoctestRunner.class)
class BeatCounter extends ValueVisitor<Fraction,Void> {
    static class CantCountBeatsException extends RuntimeException {
        CantCountBeatsException(String msg) { super(msg); }
    }
    private final DanceState ds;
    public BeatCounter(DanceState ds) { this.ds = ds; }
    private final Map<AstNode,Fraction> inherent = new HashMap<AstNode,Fraction>();
    // Math.max for Fractions.
    private Fraction max(Fraction a, Fraction b) {
        return (a.compareTo(b) < 0) ? b : a;
    }
    public Fraction getBeats(AstNode ast) {
        if (!inherent.containsKey(ast))
            inherent.put(ast, ast.accept(this, null));
        return inherent.get(ast);
    }
    @Override
    public Fraction visit(Apply apply, Void v) {
        // careful with recursive calls here!
        Evaluator e = apply.evaluator(ds);
        if (e.hasSimpleExpansion() &&
            !callBlacklist.contains(apply.call.atom))
            return getBeats(e.simpleExpansion());
        throw new CantCountBeatsException("can't expand fancy-pants calls");
    }
    @Override
    public Fraction visit(Expr e, Void v) {
        throw new CantCountBeatsException("shouldn't traverse conditions");
    }
    @Override
    public Fraction visit(If iff, Void v) {
        return getBeats(iff.child);
    }
    @Override
    public Fraction visit(In in, Void v) {
        // ignore child's length, and use the length of the 'in' instead.
        try {
            return in.count.evaluate(Fraction.class, ds);
        } catch (EvaluationException e) {
            throw new CantCountBeatsException("can't evaluate in");
        }
    }
    @Override
    public Fraction visit(Opt opt, Void v) {
        Fraction oneBranch = null;
        for (OptCall oc : opt.children) {
            Fraction f = getBeats(oc);
            if (oneBranch==null)
                oneBranch = f;
            else if (!oneBranch.equals(f))
                throw new CantCountBeatsException("options differ in duration");
        }
        return oneBranch;
    }
    @Override
    public Fraction visit(OptCall oc, Void v) {
        return getBeats(oc.child);
    }
    @Override
    public Fraction visit(Par p, Void v) {
        Fraction f = Fraction.ZERO;
        for (ParCall pc : p.children)
            f = max(f, getBeats(pc));
        return f;
    }
    @Override
    public Fraction visit(ParCall pc, Void v) {
        return getBeats(pc.child);
    }
    @Override
    public Fraction visit(Part p, Void v) {
        // note that we ignore length of parts
        return Fraction.ONE;
    }
    @Override
    public Fraction visit(Prim p, Void v) {
        return p.time;
    }
    @Override
    public Fraction visit(Seq s, Void v) {
        Fraction f = Fraction.ZERO;
        for (SeqCall sc : s.children)
            f = f.add(getBeats(sc));
        return f;
    }
    @Override
    public Fraction visit(Comp c, Void t) {
        assert false : "Unhandled Comp";
        return null;
    }
    @Override
    public Fraction visit(SeqCall s, Void t) {
        assert false : "Unhandled SeqCall";
        return null;
    }
    /** A list of recursive calls which we shouldn't try to expand. */
    private static Set<String> callBlacklist = s(
            "anyone while others", "_promenade", "_promenade check"
    );
}
