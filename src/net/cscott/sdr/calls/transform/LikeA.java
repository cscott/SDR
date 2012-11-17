package net.cscott.sdr.calls.transform;

import static net.cscott.sdr.calls.ast.Part.Divisibility.DIVISIBLE;
import static net.cscott.sdr.calls.parser.CallFileLexer.APPLY;
import static net.cscott.sdr.calls.parser.CallFileLexer.PART;

import java.util.Collections;
import java.util.Set;

import net.cscott.sdr.calls.BadCallException;
import net.cscott.sdr.calls.DanceState;
import net.cscott.sdr.calls.ExprFunc.EvaluationException;
import net.cscott.sdr.calls.ast.Comp;
import net.cscott.sdr.calls.ast.Expr;
import net.cscott.sdr.calls.ast.Part;
import net.cscott.sdr.calls.ast.Seq;
import net.cscott.sdr.calls.ast.SeqCall;
import net.cscott.sdr.util.Fraction;

public class LikeA extends Finish {
    public LikeA(DanceState ds) {
        super("like a", safeConcepts, ds);
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
            throw new BadCallException("Can't adjust to starting "+
                    "formation when doing a like a");
        }
        if (howMany.compareTo(Fraction.TWO) < 0) {
            throw new BadCallException("Only one part");
        }
        // okay, recurse to get just the last part.
        // (check that FINISH LIKE A HOT FOOT SPIN (which is 'fan the top')
        //  still works; ie this part should be optimized away)
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
            // easy case, just use the last part
            return s.build(s.children.subList(s.children.size()-1,
                                              s.children.size()));
        } else {
            // harder case: "like a" the last part
            assert lastCall.type == PART || lastCall.type == APPLY;
            SeqCall nLast = lastCall.accept(this, t);
            return s.build(Collections.singletonList(nLast));
        }
    }

    /** A list of concepts which it is safe to hoist "like a" through.
    That is, "like a(as couples(swing thru))" == "as couples(like a(swing thru))". */
    static Set<String> safeConcepts = Fractional.safeConcepts;
}
