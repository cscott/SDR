package net.cscott.sdr;

import net.cscott.jutil.MultiMap;
import net.cscott.sdr.calls.*;
import net.cscott.sdr.calls.ast.Apply;
import net.cscott.sdr.calls.ast.Seq;
/** 
 * {@link ChoreoEngine} specifies the interface through which the choreography
 * engine communicates with the rest of the SDR application.
 * @author C. Scott Ananian
 * @version $Id: ChoreoEngine.java,v 1.2 2006-11-10 00:56:07 cananian Exp $
 */
public class ChoreoEngine {
    private DanceState ds;
    public ChoreoEngine(DanceProgram dp, Formation f) {
        this.ds = new DanceState(dp, f);
    }
    
    /**
     * Given a {@link TimedFormation} representing the "current" dancer
     * formation, perform the given call.  The result will be a list of
     * future formations, sorted by the time at which they occur (with the
     * earliest first).  No dancer will turn more than 1 wall (90 degrees)
     * between formations. All times in the {@code TimedFormation}s will be
     * absolute.
     * @param unparsedCall  The (sequence of) call(s) to perform.
     * @return a time-stamped list of result formations, with absolute times.
     * @throws BadCallException if (some part of) the given call is impossible
     *  from the given start formation.
     */ 
    public MultiMap<Dancer,DancerPath>
    execute(String unparsedCall, ScoreAccumulator score)
    throws BadCallException {
        if (true) return null;
        Apply call = CallDB.INSTANCE.parse(ds.dance.getProgram(), unparsedCall);
        Evaluator e = new Evaluator.Standard(new Seq(call));
        // XXX: we don't actually want to do evaluateAll(); we want to
        //      return a continuation which will do evaluate() as needed,
        //      so that predicates such as "exist more calls" will work right.
        //assert false : "unimplemented"; // XXX: unimplemented
        // List<TimedFormation> returned from call evaluation will be
        // relative.  We will need to convert to an absolutely-timed list
        // before we return it.
        return null;
    }
    public Apply lastCall() { return null; }
    public Formation currentFormation() { return null; }
}
