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
        final Map<String,Grm> m = mapFor(program);
        return new GrmDB() {
            @Override
            public Map<String, Grm> grammar() { return m; }
        };
    }
    public static final Map<String,Grm> mapFor(Program program) {
        switch (program) {
        default: assert false;
        case BASIC: return AllGrm.BASIC;
        case MAINSTREAM: return AllGrm.MAINSTREAM;
	case PLUS: return AllGrm.PLUS;
	case A1: return AllGrm.A1;
	case A2: return AllGrm.A2;
	case C1: return AllGrm.C1;
	case C2: return AllGrm.C2;
	case C3A: return AllGrm.C3A;
	case C3B: return AllGrm.C3B;
        case C4: return AllGrm.C4;
        }
    }
}
