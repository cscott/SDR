package net.cscott.sdr.calls.lists;

import static net.cscott.sdr.calls.transform.AstTokenTypes.PART;

import java.util.ArrayList;
import java.util.List;

import net.cscott.sdr.calls.BadCallException;
import net.cscott.sdr.calls.Call;
import net.cscott.sdr.calls.Program;
import net.cscott.sdr.calls.Warp;
import net.cscott.sdr.calls.ast.Apply;
import net.cscott.sdr.calls.ast.Comp;
import net.cscott.sdr.calls.ast.In;
import net.cscott.sdr.calls.ast.Part;
import net.cscott.sdr.calls.ast.Seq;
import net.cscott.sdr.calls.ast.SeqCall;
import net.cscott.sdr.calls.ast.Warped;
import net.cscott.sdr.calls.transform.Fractional;
import net.cscott.sdr.util.Fraction;

/** 
 * The <code>BasicList</code> class contains complex call
 * and concept definitions which are on the 'basic' program.
 * @author C. Scott Ananian
 * @version $Id: BasicList.java,v 1.10 2006-10-17 16:42:33 cananian Exp $
 */
public abstract class BasicList {
    // hide constructor.
    private BasicList() { }

    private static abstract class BasicCall extends Call {
        private final String name;
        BasicCall(String name) { this.name = name; }
        @Override
        public final String getName() { return name; }
        @Override
        public final Program getProgram() { return Program.BASIC; }
    }
    
    public static final Call SQUARE_THRU = new BasicCall("square thru") {
            @Override
            public Comp apply(Apply ast) {
                assert ast.callName.equals(getName());
                assert ast.args.size()==1;
                // one parameter: a count
                Fraction n = ast.getNumberArg(0);
                // validate.
                if (Fraction.ZERO.compareTo(n) >= 0)
                    throw new BadCallException
                        ("Can't square thru zero or fewer times");
                // square thru 1 is right pull by
                if (Fraction.ONE.compareTo(n) >= 1)
                    return new Seq(new Part
                                   (false, new In(2, new Seq
                                    (Apply.makeApply
                                     ("_fractional", n, "pull by")))));
                // square thru N is right pull by, quarter in,
                // left square thru (N-1) (even if N is fractional)
                return new Seq(new Part(false, new In(2, new Seq(
                        Apply.makeApply("pull by"),
                        Apply.makeApply("quarter in"),
                        Apply.makeApply("left",
                                Apply.makeApply("square thru",
                                        n.subtract(Fraction.ONE)))))));
            }
    };
    public static final Call TOUCH = new BasicCall("_touch") {
        @Override
        public Comp apply(Apply ast) {
            assert ast.callName.equals(getName());
            assert ast.args.size()==1;
            Fraction n = ast.getNumberArg(0);
            return new Seq
                     (Apply.makeApply
                      ("_fractional", n, "_touch 4/4"));
        }
    };
    // simple combining concept.
    public static final Call AND = new BasicCall("and") {
        @Override
        public Comp apply(Apply ast) {
            assert ast.callName.equals(getName());
            assert ast.args.size()>=1;
            List<SeqCall> l = new ArrayList<SeqCall>(ast.args);
            return new Seq(l);
        }
    };
    // this is not completely accurate?
    public static final Call LEFT = new BasicCall("left") {
        @Override
        public Comp apply(Apply ast) {
            assert ast.callName.equals(getName());
            assert ast.args.size()==1;
            Apply a = ast.getArg(0);
            Warp warp = Warp.MIRROR;
            return new Warped(warp, new Seq(a));
        }
    };
    // complex concept -- not sure correct program here?
    public static final Call _FRACTIONAL = new BasicCall("_fractional") {
        private Fractional fv = new Fractional(); // visitor singleton
        @Override
        public Comp apply(Apply ast) {
            boolean isDivisible = true;
            assert ast.callName.equals(getName());
            assert ast.args.size()==2;
            Fraction n = ast.getNumberArg(0);
            Apply a = ast.getArg(1);
            if (n.compareTo(Fraction.ZERO) <= 0)
                throw new BadCallException("0 fractions are not legal");
            int whole = n.getProperWhole();
            List<SeqCall> l = new ArrayList<SeqCall>(whole+1);
            // easy case: do the whole repetitions of the
            // call.
            for (int i=0; i<whole; i++)
                l.add(new Part(true, new Seq(a)));
            // now add the fraction, if there is one.
            // note this does not get wrapped in a PART:
            // we can't further fractionalize (say)
            // swing thru 1 1/2.
            n=Fraction.valueOf(n.getProperNumerator(),
                    n.getDenominator());
            if (!Fraction.ZERO.equals(n)) {
                l.add(fv.visit(a, n));
                if (whole!=0)
                    isDivisible=false;
            }
            Comp result = new Seq(l);
            // OPTIMIZATION: SEQ(PART(c)) = c
            if (l.size()==1 && l.get(0).type==PART) {
                Part p = (Part) l.get(0);
                if (p.isDivisible)
                    result = p.child;
            } else if (!isDivisible)
                // we don't support subdivision of "swing thru 2 1/2"
                result = new Seq(new Part(isDivisible, result));
            return result;
        }
    };
}
