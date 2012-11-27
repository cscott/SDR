package net.cscott.sdr.calls.transform;

import static net.cscott.sdr.calls.ast.If.When.AFTER;
import static net.cscott.sdr.calls.ast.Part.Divisibility.DIVISIBLE;
import static net.cscott.sdr.calls.parser.CallFileLexer.APPLY;
import static net.cscott.sdr.calls.parser.CallFileLexer.PART;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.cscott.sdr.calls.BadCallException;
import net.cscott.sdr.calls.DanceState;
import net.cscott.sdr.calls.ExprFunc.EvaluationException;
import net.cscott.sdr.calls.ast.Comp;
import net.cscott.sdr.calls.ast.Expr;
import net.cscott.sdr.calls.ast.If;
import net.cscott.sdr.calls.ast.Part;
import net.cscott.sdr.calls.ast.Seq;
import net.cscott.sdr.calls.ast.SeqCall;
import net.cscott.sdr.util.Fraction;

/**
 * Transformation implementing
 * {@link net.cscott.sdr.calls.lists.C4List#_ALL_BUT_LAST_PART}
 * (not on any list).
 * Skips just the last part of the call.
 * Equivalent to "reverse order finish reverse order".
 * @author C. Scott Ananian
 */
public class AllButLastPart extends Finish {
    public AllButLastPart(DanceState ds) {
        super("_all but last part", safeConcepts, ds);
    }

    @Override
    public Part visit(Part p, Void t) {
        if (p.divisibility!=DIVISIBLE) {
            return super.visit(p, t);
        }
        Fraction howMany;
        try {
            howMany = p.parts().evaluate(Fraction.class, ds);
        } catch (EvaluationException e) {
            assert false : "bad call definition";
        throw new BadCallException("Can't evaluate number of parts");
        }
        assert howMany.getProperNumerator()==0 : "non-integral parts?!";
        if (howMany.equals(Fraction.ZERO)) {
            // take this part unmodified (starting adjustment)
            return p;
        }
        if (howMany.compareTo(Fraction.TWO) < 0) {
            throw new BadCallException("Only one part");
        }
        // okay, recurse to remove just the last part.
        return p.build(p.divisibility,
                       Expr.literal(howMany.subtract(Fraction.ONE)),
                p.child.accept(this, t));
    }

    /* (non-Javadoc)
     * @see net.cscott.sdr.calls.transform.Finish#visit(net.cscott.sdr.calls.ast.Seq, java.lang.Void)
     */
    @Override
    public Comp visit(Seq s, Void t) {
        boolean singleCall = false;
        s = desugarAnd(s);
        if (s.children.size() == 1 && s.children.get(0).type==APPLY) {
            // fall through, this is a special case.
            // (one call defined as exactly another call gets the parts of
            //  the other call, instead of a single part.  use an explicit
            //  ipart if you actually wanted to define it as a single part)
            singleCall = true;
        } else if (s.children.size() == 1 && s.children.get(0).type==PART) {
            // another special case: (Seq (Part 'DIVISIBLE '2 ...)) is
            // actually two parts.  Recurse into the Part.
            singleCall = true;
        } else if (s.children.size() < 2) {
            throw new BadCallException("Only one part");
        }
        // just look at last part, verify that 'howMany' is one
        SeqCall lastCall = s.children.get(s.children.size()-1);
        if (lastCall.isIndeterminate()) {
            throw new BadCallException("Number of parts is not well-defined");
        }
        Fraction lastParts;
        try {
            lastParts = lastCall.parts().evaluate(Fraction.class, ds);
        } catch (EvaluationException e) {
            assert false : "bad call definition";
            throw new BadCallException("Can't evaluate number of parts");
        }
        if (lastParts.equals(Fraction.ONE) && !singleCall) {
            // easy case, just drop the last part
            return s.build(s.children.subList(0, s.children.size()-1));
        } else {
            // harder case: "all but last part" the last part
            assert lastCall.type == PART || lastCall.type == APPLY;
            SeqCall nLast = lastCall.accept(this, t);
            List<SeqCall> nChildren = new ArrayList<SeqCall>(s.children);
            nChildren.set(nChildren.size()-1, nLast);
            return s.build(nChildren);
        }
    }

    /* Keep initial conditions, but skip 'ends in' conditions. */
    @Override
    public Comp visit(If iff, Void t) {
        if (iff.when==AFTER)
            return iff.child.accept(this, t);
        return iff.build(iff.condition.accept(this,t),
                         iff.child.accept(this,t));
    }

    /** A list of concepts which it is safe to hoist "_all but last part" through.
    That is, "_all but last part(as couples(swing thru))" == "as couples(_all but last part(swing thru))". */
    static Set<String> safeConcepts = Fractional.safeConcepts;
}
