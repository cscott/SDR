package net.cscott.sdr.calls.grm;

import java.util.Map;

import net.cscott.sdr.calls.Program;
import net.cscott.sdr.calls.lists.*;

/** Abstract class storing a post-processed natural grammar, as a map from
 * nonterminal name (a String) to a {@link Grm}.
 */
public abstract class GrmDB {
    public abstract Map<String,Grm> grammar();

    public static final GrmDB dbFor(Program program) {
        switch (program) {
        default: assert false;
        case BASIC: return new BasicGrm();
        case MAINSTREAM: return new MainstreamGrm();
	case PLUS: return new PlusGrm();
	case A1: return new A1Grm();
	case A2: return new A2Grm();
	case C1: return new C1Grm();
	case C2: return new C2Grm();
	case C3A: return new C3aGrm();
	case C3B: return new C3bGrm();
        case C4: return new C4Grm();
        }
    }
}
