package net.cscott.sdr.calls.lists;

import java.util.Collections;
import java.util.List;

import net.cscott.sdr.calls.Call;
import net.cscott.sdr.calls.Program;
import net.cscott.sdr.calls.ast.Expr;

/**
 * The <code>C3aList</code> class contains complex call
 * and concept definitions which are on the 'C3A' program.
 * Note that "simple" calls and concepts are defined in
 * the resource file at
 * <a href="doc-files/c3a.calls"><code>net/cscott/sdr/calls/lists/c3a.calls</code></a>;
 * this class contains only those definitions for which an
 * executable component is required.
 * @author C. Scott Ananian
 */
public abstract class C3aList {
    // hide constructor.
    private C3aList() { }

    @SuppressWarnings("unused")
    private static abstract class C3aCall extends Call {
        private final String name;
        C3aCall(String name) { this.name = name; }
        @Override
        public final String getName() { return name; }
        @Override
        public final Program getProgram() { return Program.C3A; }
        @Override
        public List<Expr> getDefaultArguments() {
            return Collections.emptyList();
        }
    }
}
