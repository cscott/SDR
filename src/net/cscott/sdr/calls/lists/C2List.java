package net.cscott.sdr.calls.lists;

import java.util.Collections;
import java.util.List;

import net.cscott.sdr.calls.Call;
import net.cscott.sdr.calls.DanceState;
import net.cscott.sdr.calls.Evaluator;
import net.cscott.sdr.calls.Program;
import net.cscott.sdr.calls.Selector;
import net.cscott.sdr.calls.ast.Expr;
import net.cscott.sdr.calls.grm.Rule;
import net.cscott.sdr.calls.lists.C1List.ConcentricEvaluator;
import net.cscott.sdr.calls.lists.C1List.ConcentricType;

/**
 * The <code>C2List</code> class contains complex call
 * and concept definitions which are on the 'C2' program.
 * Note that "simple" calls and concepts are defined in
 * the resource file at
 * <a href="doc-files/c2.calls"><code>net/cscott/sdr/calls/lists/c2.calls</code></a>;
 * this class contains only those definitions for which an
 * executable component is required.
 * @author C. Scott Ananian
 */
public abstract class C2List {
    // hide constructor.
    private C2List() { }

    private static abstract class C2Call extends Call {
        private final String name;
        C2Call(String name) { this.name = name; }
        @Override
        public final String getName() { return name; }
        @Override
        public final Program getProgram() { return Program.C2; }
        @Override
        public List<Expr> getDefaultArguments() {
            return Collections.emptyList();
        }
    }

    public static final Call CROSS_CONCENTRIC = new C2Call("_cross concentric") {
        @Override
        public int getMinNumberOfArguments() { return 2; }
        @Override
        public Rule getRule() { return null; /* internal call */ }
        @Override
        public Evaluator getEvaluator(DanceState ds, List<Expr> args)
            throws EvaluationException {
            assert args.size() == 2 || args.size() == 3;
            Selector who  = (args.size()==3) ?
                args.get(2).evaluate(Selector.class, ds) : null;
            return new ConcentricEvaluator(args.get(0), args.get(1), who,
                                           ConcentricType.CROSS);
        }
    };
}
