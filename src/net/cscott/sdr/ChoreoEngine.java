package net.cscott.sdr;

import java.util.List;

import net.cscott.sdr.calls.*;
import net.cscott.sdr.calls.ast.Apply;
/** 
 * {@link ChoreoEngine} specifies the interface through which the choreography
 * engine communicates with the rest of the SDR application.
 * @author C. Scott Ananian
 * @version $Id: ChoreoEngine.java,v 1.2 2006-11-10 00:56:07 cananian Exp $
 */
public class ChoreoEngine {
    private DanceProgram ds;
    public ChoreoEngine(DanceProgram ds) {
        this.ds = ds;
    }
    
    /**
     * Given a {@link TimedFormation} representing the "current" dancer
     * formation, perform the given call.  The result will be a list of
     * future formations, sorted by the time at which they occur (with the
     * earliest first).  No dancer will turn more than 1 wall (90 degrees)
     * between formations. All times in the {@code TimedFormation}s will be
     * absolute.
     * @param start The starting formation and time.
     * @param call  The (sequence of) call(s) to perform.
     * @return a time-stamped list of result formations, with absolute times.
     * @throws BadCallException if (some part of) the given call is impossible
     *  from the given start formation.
     */ 
    public List<TimedFormation>
    execute(TimedFormation start, Apply call, ScoreAccumulator score)
    throws BadCallException {
        //assert false : "unimplemented"; // XXX: unimplemented
        // List<TimedFormation> returned from call evaluation will be
        // relative.  We will need to convert to an absolutely-timed list
        // before we return it.
        return null;
    }
}
