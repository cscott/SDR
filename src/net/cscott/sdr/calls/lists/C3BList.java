package net.cscott.sdr.calls.lists;

import net.cscott.sdr.calls.Call;
import net.cscott.sdr.calls.Program;
import net.cscott.sdr.calls.ast.Apply;
import net.cscott.sdr.calls.ast.Comp;
import net.cscott.sdr.calls.grm.Grm;
import net.cscott.sdr.calls.grm.Rule;
import net.cscott.sdr.calls.lists.BasicList.LRMEvaluator;
import net.cscott.sdr.calls.lists.BasicList.LRMType;
import net.cscott.sdr.calls.transform.Evaluator;
import net.cscott.sdr.util.Fraction;

/**
 * The <code>C3BList</code> class contains complex call
 * and concept definitions which are on the 'plus' program.
 * Note that "simple" calls and concepts are defined in
 * the resource file at
 * <a href="doc-files/c3b.calls"><code>net/cscott/sdr/calls/lists/c3b.calls</code></a>;
 * this class contains only those definitions for which an
 * executable component is required.
 * @author C. Scott Ananian
 */
public abstract class C3BList {
    // hide constructor.
    private C3BList() { }

    private static abstract class C3BCall extends Call {
        private final String name;
        C3BCall(String name) { this.name = name; }
        @Override
        public final String getName() { return name; }
        @Override
        public final Program getProgram() { return Program.C3B; }
    }

    public static final Call MIRROR = new C3BCall("mirror") {
        @Override
        public Comp apply(Apply ast) {
            assert false; /* should use custom evaluator */
            return null;
        }
        @Override
        public int getMinNumberOfArguments() { return 1; }
        @Override
        public Rule getRule() {
            Grm g = Grm.parse("mirror <0=anything>");
            return new Rule("anything", g, Fraction.valueOf(-20)); // bind loose
        }
        @Override
        public Evaluator getEvaluator(Apply ast) {
            assert ast.callName.equals(getName());
            return new LRMEvaluator(LRMType.MIRROR, ast);
        }
    };
}
