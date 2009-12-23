package net.cscott.sdr.calls.lists;

import java.util.Collections;
import java.util.List;

import net.cscott.sdr.calls.Call;
import net.cscott.sdr.calls.Program;
import net.cscott.sdr.calls.ast.Expr;

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

    @SuppressWarnings("unused")
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
}
