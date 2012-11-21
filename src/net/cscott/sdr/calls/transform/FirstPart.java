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
 * {@link net.cscott.sdr.calls.lists.C4List#_FIRST_PART} (not on any list).
 * Extracts just the first part of the call.
 * Equivalent to "like a reverse order".
 * @author C. Scott Ananian
 */
public class FirstPart extends Finish {
    public FirstPart(DanceState ds) {
        super("_first part", safeConcepts, ds);
    }

    /* (non-Javadoc)
     * @see net.cscott.sdr.calls.transform.Finish#visit(net.cscott.sdr.calls.ast.Part, java.lang.Void)
     */
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
        // okay, recurse to get just the first part.
        return p.build(p.divisibility,
                Expr.literal(Fraction.ONE) /* now only one part */,
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
        // just look at first part, verify that 'howMany' is one
        List<SeqCall> zeroParts = new ArrayList<SeqCall>(2);
        SeqCall firstCall=null;
        Fraction firstParts=null;
        int i;
        for (i=0; i<s.children.size(); i++) {
            firstCall = s.children.get(i);
            if (firstCall.isIndeterminate()) {
                throw new BadCallException("Number of parts is not well-defined");
            }
            try {
                firstParts = firstCall.parts().evaluate(Fraction.class, ds);
            } catch (EvaluationException e) {
                assert false : "bad call definition";
            throw new BadCallException("Can't evaluate number of parts");
            }
            if (firstParts.equals(Fraction.ZERO)) {
                zeroParts.add(firstCall);
            } else break;
        }
        if (i==s.children.size()) {
            // hmm, whole thing is an adjustment?
            // would need to recurse up and take the first part of the second part...
            throw new BadCallException("No parts found.");
        } else if (firstParts.equals(Fraction.ONE) && !singleCall) {
            // easy case, just use the first part (and any zero parts)
            return s.build(s.children.subList(0, i+1));
        } else {
            // harder case: "_first part" the next part
            assert firstCall.type == PART || firstCall.type == APPLY;
            SeqCall nFirst = firstCall.accept(this, t);
            // add this to the zero parts and return the new Seq
            zeroParts.add(nFirst);
            return s.build(zeroParts);
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

    /** A list of concepts which it is safe to hoist "_first part" through.
    That is, "_first part(as couples(swing thru))" == "as couples(_first part(swing thru))". */
    static Set<String> safeConcepts = Fractional.safeConcepts;
}
