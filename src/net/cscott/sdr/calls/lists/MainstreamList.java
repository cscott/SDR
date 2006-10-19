package net.cscott.sdr.calls.lists;

import net.cscott.sdr.calls.Call;
import net.cscott.sdr.calls.Program;
import net.cscott.sdr.calls.ast.Apply;
import net.cscott.sdr.calls.ast.Comp;
import net.cscott.sdr.calls.ast.Seq;
import net.cscott.sdr.util.Fraction;

/** 
 * The <code>MainstreamList</code> class contains complex call
 * and concept definitions which are on the 'mainstream' program.
 * @author C. Scott Ananian
 * @version $Id: MainstreamList.java,v 1.4 2006-10-19 20:17:56 cananian Exp $
 */
public abstract class MainstreamList {
    // hide constructor.
    private MainstreamList() { }

    private static abstract class MSCall extends Call {
        private final String name;
        MSCall(String name) { this.name = name; }
        @Override
        public final String getName() { return name; }
        @Override
        public final Program getProgram() { return Program.MAINSTREAM; }
    }

}
