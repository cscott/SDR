package net.cscott.sdr.calls.grm;

import java.util.Map;

import net.cscott.sdr.calls.Program;
import net.cscott.sdr.calls.lists.BasicGrm;
import net.cscott.sdr.calls.lists.C4Grm;

/** Abstract class storing a post-processed natural grammar, as a map from
 * nonterminal name (a String) to a {@link Grm}.
 */
public abstract class GrmDB {
    public abstract Map<String,Grm> grammar();

    public static final GrmDB dbFor(Program program) {
        if (program!=Program.BASIC) program=Program.C4; // for debugging
        switch (program) {
        case BASIC: return new BasicGrm();
        //case MAINSTREAM: return new MainstreamGrm();
        default:
        case C4: return new C4Grm();
        }
    }
}
