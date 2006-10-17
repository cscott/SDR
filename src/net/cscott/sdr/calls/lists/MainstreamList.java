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
 * @version $Id: MainstreamList.java,v 1.3 2006-10-17 16:29:06 cananian Exp $
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

    // cast 3/4 is mainstream -- what level is "cast 1/4"
    // and/or "cast 1/2"?  We'll follow SD in calling these
    // mainstream as well.
    public static final Call CAST = new MSCall("cast") {
        @Override
        public Comp apply(Apply ast) {
            assert ast.callName.equals(getName());
            assert ast.args.size()==1;
            Fraction n = ast.getNumberArg(0);
            return new Seq
                     (Apply.makeApply
                      ("_fractional", n, "_cast 4/4"));
        }
    };
}
