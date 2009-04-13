package net.cscott.sdr.calls.lists;

import static net.cscott.sdr.calls.transform.AstTokenTypes.PART;

import java.util.ArrayList;
import java.util.List;

import net.cscott.sdr.calls.BadCallException;
import net.cscott.sdr.calls.Call;
import net.cscott.sdr.calls.DanceState;
import net.cscott.sdr.calls.Dancer;
import net.cscott.sdr.calls.DancerPath;
import net.cscott.sdr.calls.Formation;
import net.cscott.sdr.calls.Program;
import net.cscott.sdr.calls.ast.*;
import net.cscott.sdr.calls.grm.Grm;
import net.cscott.sdr.calls.grm.Rule;
import net.cscott.sdr.calls.transform.Evaluator;
import net.cscott.sdr.calls.transform.Fractional;
import net.cscott.sdr.util.Fraction;

/** 
 * The <code>BasicList</code> class contains complex call
 * and concept definitions which are on the 'basic' program.
 * Note that "simple" calls and concepts are defined in
 * the resource file at
 * <a href="doc-files/basic.calls"><code>net/cscott/sdr/calls/lists/basic.calls</code></a>;
 * this class contains only those definitions for which an
 * executable component is required.
 * @author C. Scott Ananian
 * @version $Id: BasicList.java,v 1.20 2009-02-05 06:13:31 cananian Exp $
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
        @Override
        public Rule getRule() { return null; }
        @Override
        public Evaluator getEvaluator(Apply ast) {
            return null; // use standard evaluator
        }
    }
    
    // simple combining concept.
    public static final Call AND = new BasicCall("and") {
        @Override
        public Comp apply(Apply ast) {
            assert ast.callName.equals(getName());
            assert ast.args.size()>=1;
            List<SeqCall> l = new ArrayList<SeqCall>(ast.args);
            return new Seq(l);
        }
        @Override
        public int getMinNumberOfArguments() { return 1; }
        @Override
        public Rule getRule() {
            // this introduces ambiguities into the grammar; ban 'and'
            // as a simple connector.
	    if (true) return null;
            Grm g = Grm.parse("<0=anything> and <1=anything>");
            return new Rule("anything", g, Fraction.valueOf(-30));
        }
    };
    // kludges for simple arithmetic.
    private static abstract class MathCall extends BasicCall {
        MathCall(String name) { super(name); }
        abstract Fraction getIdentity();
        abstract Fraction doOp(Fraction f1, Fraction f2);
        @Override
        public final Comp apply(Apply ast) {
            Fraction result = getIdentity();
            assert ast.args.size()>=1;
            for (int i=0; i<ast.args.size(); i++)
                result = doOp(result, ast.getNumberArg(i));
            // here's the kludge
            return new Seq(Apply.makeApply(result.toProperString()));
        }
        @Override
        public int getMinNumberOfArguments() { return 1; }
        @Override
        public Evaluator getEvaluator(Apply ast) {
            return null;
        }
    }
    /**
     * Simple math: addition.
     * @doc.test
     *  js> c=net.cscott.sdr.calls.ast.AstNode.valueOf("(Apply _add_num (Apply 2) (Apply 1))")
     *  (Apply _add_num (Apply 2) (Apply 1))
     *  js> c.expand()
     *  (Seq (Apply 3))
     */
    public static final Call _ADD_NUM = new MathCall("_add_num") {
        @Override
        Fraction getIdentity() { return Fraction.ZERO; }
        @Override
        Fraction doOp(Fraction f1, Fraction f2) { return f1.add(f2); }
    };
    /**
     * Simple math: subtraction.
     * @doc.test
     *  js> c=net.cscott.sdr.calls.ast.AstNode.valueOf("(Apply _subtract_num (Apply 3) (Apply 2))")
     *  (Apply _subtract_num (Apply 3) (Apply 2))
     *  js> c.expand()
     *  (Seq (Apply 1))
     */
    public static final Call _SUBTRACT_NUM = new BasicCall("_subtract_num") {
        @Override
        public final Comp apply(Apply ast) {
            assert ast.args.size()==2;
            Fraction result = ast.getNumberArg(0).subtract(ast.getNumberArg(1));
            // here's the kludge
            return new Seq(Apply.makeApply(result.toProperString()));
        }
        @Override
        public int getMinNumberOfArguments() { return 2; }
    };
    /**
     * Simple math: multiplication.
     * @doc.test
     *  js> c=net.cscott.sdr.calls.ast.AstNode.valueOf("(Apply _multiply_num (Apply 3) (Apply 2))")
     *  (Apply _multiply_num (Apply 3) (Apply 2))
     *  js> c.expand()
     *  (Seq (Apply 6))
     */
    public static final Call _MULTIPLY_NUM = new MathCall("_multiply_num") {
        @Override
        Fraction getIdentity() { return Fraction.ONE; }
        @Override
        Fraction doOp(Fraction f1, Fraction f2) { return f1.multiply(f2); }
    };
    /**
     * Simple math: division.
     * @doc.test
     *  js> c=net.cscott.sdr.calls.ast.AstNode.valueOf("(Apply _divide_num (Apply 3) (Apply 2))")
     *  (Apply _divide_num (Apply 3) (Apply 2))
     *  js> c.expand()
     *  (Seq (Apply 1 1/2))
     */
    public static final Call _DIVIDE_NUM = new BasicCall("_divide_num") {
        @Override
        public final Comp apply(Apply ast) {
            assert ast.args.size()==2;
            Fraction result = ast.getNumberArg(0).divide(ast.getNumberArg(1));
            // here's the kludge
            return new Seq(Apply.makeApply(result.toProperString()));
        }
        @Override
        public int getMinNumberOfArguments() { return 2; }
    };
    // LEFT means 'do each part LEFT' but collisions are still resolved to
    // right hands. (opposed to MIRROR, where collisions are to left hands).
    public static final Call LEFT = new BasicCall("left") {
        @Override
        public Comp apply(Apply ast) {
            assert false; /* should use custom evaluator */
            return null;
        }
        @Override
        public int getMinNumberOfArguments() { return 1; }
        @Override
        public Rule getRule() {
            Grm g = Grm.parse("left <0=leftable_anything>");
            return new Rule("anything", g, Fraction.TWO); // bind tight
        }
        @Override
        public Evaluator getEvaluator(Apply ast) {
            assert ast.callName.equals(getName());
            return new LRMEvaluator(LRMType.LEFT, ast);
        }
    };

    public static final Call REVERSE = new BasicCall("reverse") {
        @Override
        public Comp apply(Apply ast) {
            assert false; /* should use custom evaluator */
            return null;
        }
        @Override
        public int getMinNumberOfArguments() { return 1; }
        @Override
        public Rule getRule() {
            Grm g = Grm.parse("reverse <0=reversable_anything>");
            return new Rule("anything", g, Fraction.TWO); // bind tight
        }
        @Override
        public Evaluator getEvaluator(Apply ast) {
            assert ast.callName.equals(getName());
            return new LRMEvaluator(LRMType.REVERSE, ast);
        }
    };

    /** Enumeration: left, reverse, or mirror. */
    public static enum LRMType { LEFT, MIRROR, REVERSE; }
    /** Evaluator for left, reverse, and mirror. */
    public static class LRMEvaluator extends Evaluator {
        private final LRMType which;
        private final Comp comp;
        public LRMEvaluator(LRMType which, Apply ast) {
            assert ast.args.size()==1;
            this.which = which;
            this.comp = new Seq(ast.getArg(0));
        }
        @Override
        public Evaluator evaluate(DanceState ds) {
            boolean mirrorShoulderPass = (which != LRMType.MIRROR);
            // Mirror the current formation.
            Formation nf = ds.currentFormation().mirror(mirrorShoulderPass);
            DanceState nds = ds.cloneAndClear(nf);
            // do the call in the mirrored formation
            new Evaluator.Standard(this.comp).evaluateAll(nds);
            // now re-mirror the resulting paths.
            for (Dancer d : nds.dancers()) {
                for (DancerPath dp : nds.movements(d)) {
                    ds.add(d, dp.mirror(mirrorShoulderPass));
                }
            }
            // no more to evaluate
            return null;
        }
    };

    // complex concept -- not sure correct program here?
    // XXX: support further subdivision of DOSADO 1 1/2 by allowing an
    //      integer argument to Part which specifies how many parts
    //      it should be considered as?
    /**
     * The "fractional" concept.
     * @doc.test
     *  Evaluate TWICE DOSADO and the DOSADO 1 1/2.  Note that we prohibit
     *  further subdivision of the DOSADO 1 1/2.
     *  js> importPackage(net.cscott.sdr.calls.ast)
     *  js> a1 = Apply.makeApply("dosado")
     *  (Apply dosado)
     *  js> a = Apply.makeApply("_fractional", Apply.makeApply("2"), a1)
     *  (Apply _fractional (Apply 2) (Apply dosado))
     *  js> BasicList._FRACTIONAL.apply(a)
     *  (Seq (Part true (Seq (Apply dosado))) (Part true (Seq (Apply dosado))))
     *  js> a = Apply.makeApply("_fractional", Apply.makeApply("1 1/2"), a1)
     *  (Apply _fractional (Apply 1 1/2) (Apply dosado))
     *  js> BasicList._FRACTIONAL.apply(a)
     *  (Seq (Part false (Seq (Part true (Seq (Apply dosado))) (Part true (In 3 (Opt (From [FACING DANCERS] (Seq (Prim -1, 1, none, 1, SASHAY_START) (Prim 1, 1, none, 1, SASHAY_FINISH)))))))))
     */
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
        @Override
        public int getMinNumberOfArguments() { return 2; }
        @Override
        public Rule getRule() {
            String rule = "do <0=fraction> (of (a)?)? <1=anything>" +
            "| <1=anything> <0=cardinal>";
        Grm g = Grm.parse(rule);
        return new Rule("anything", g, Fraction.valueOf(-10));
        }
    };
    // grammar tweak: allow "do half of a ..." in addition to the
    // longer-winded "do one half of a..." or "do a half of a..."
    public static final Call _HALF = new BasicCall("_half") {
        @Override
        public Comp apply(Apply ast) {
            return _FRACTIONAL.apply
               (Apply.makeApply("_fractional", Fraction.ONE_HALF,
                                ast.args.get(0)));
        }
        @Override
        public int getMinNumberOfArguments() { return 1; }
        @Override
        public Rule getRule() {
            String rule = "do half of (a)? <0=anything>";
            Grm g = Grm.parse(rule);
            return new Rule("anything", g, Fraction.valueOf(-10));
        }
    };
}
