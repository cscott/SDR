package net.cscott.sdr.calls.lists;

import static net.cscott.sdr.calls.ast.Part.Divisibility.DIVISIBLE;
import static net.cscott.sdr.calls.parser.CallFileLexer.PART;

import java.util.Collections;
import java.util.List;

import net.cscott.sdr.calls.Call;
import net.cscott.sdr.calls.DanceState;
import net.cscott.sdr.calls.Evaluator;
import net.cscott.sdr.calls.Program;
import net.cscott.sdr.calls.ast.Apply;
import net.cscott.sdr.calls.ast.Comp;
import net.cscott.sdr.calls.ast.Expr;
import net.cscott.sdr.calls.ast.Part;
import net.cscott.sdr.calls.ast.Seq;
import net.cscott.sdr.calls.ast.SeqCall;
import net.cscott.sdr.calls.grm.Grm;
import net.cscott.sdr.calls.grm.Rule;
import net.cscott.sdr.calls.transform.LikeA;
import net.cscott.sdr.util.Fraction;

/**
 * The <code>C4List</code> class contains complex call
 * and concept definitions which are on the 'C4' program.
 * Note that "simple" calls and concepts are defined in
 * the resource file at
 * <a href="doc-files/c4.calls"><code>net/cscott/sdr/calls/lists/c4.calls</code></a>;
 * this class contains only those definitions for which an
 * executable component is required.
 * @author C. Scott Ananian
 */
public abstract class C4List {
    // hide constructor.
    private C4List() { }

    private static abstract class C4Call extends Call {
        private final String name;
        C4Call(String name) { this.name = name; }
        @Override
        public final String getName() { return name; }
        @Override
        public final Program getProgram() { return Program.C4; }
        @Override
        public List<Expr> getDefaultArguments() {
            return Collections.emptyList();
        }
    }

    public static final Call LIKE_A = new C4Call("like a") {
        @Override
        public int getMinNumberOfArguments() { return 1; }
        @Override
        public Rule getRule() {
            String rule = "like (a|an)? <0=anything>";
            Grm g = Grm.parse(rule);
            return new Rule("anything", g, Fraction.valueOf(-9));
        }
        @Override
        public Evaluator getEvaluator(DanceState ds, List<Expr> args)
                throws EvaluationException {
            LikeA fv = new LikeA(ds);
            assert args.size()==1;
            Apply a = new Apply(args.get(0));
            SeqCall sc = a.accept(fv, null);
            Comp result = new Seq(sc);
            // OPTIMIZATION: SEQ(PART(c)) = c
            if (sc.type==PART) {
                Part p = (Part) sc;
                if (p.divisibility==DIVISIBLE)
                    result = p.child;
            }
            return new Evaluator.Standard(result);
        }
    };
}
