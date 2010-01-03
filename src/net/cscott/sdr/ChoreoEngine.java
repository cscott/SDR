package net.cscott.sdr;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import net.cscott.jutil.MultiMap;
import net.cscott.sdr.calls.BadCallException;
import net.cscott.sdr.calls.CallDB;
import net.cscott.sdr.calls.DanceProgram;
import net.cscott.sdr.calls.DanceState;
import net.cscott.sdr.calls.Dancer;
import net.cscott.sdr.calls.DancerBezierPath;
import net.cscott.sdr.calls.DancerPath;
import net.cscott.sdr.calls.Evaluator;
import net.cscott.sdr.calls.Formation;
import net.cscott.sdr.calls.Program;
import net.cscott.sdr.calls.TimedFormation;
import net.cscott.sdr.calls.ast.Apply;
import net.cscott.sdr.calls.ast.Expr;
import net.cscott.sdr.calls.ast.Seq;
import net.cscott.sdr.util.Fraction;

/** 
 * {@link ChoreoEngine} specifies the interface through which the choreography
 * engine communicates with the rest of the SDR application.
 * @author C. Scott Ananian
 * @version $Id: ChoreoEngine.java,v 1.2 2006-11-10 00:56:07 cananian Exp $
 */
public class ChoreoEngine {
    private AttractThread at;
    private DanceState ds;
    public ChoreoEngine(DanceProgram dp, Formation f, DanceFloor danceFloor) {
        this.ds = new DanceState(dp, f);
        this.at = new AttractThread(danceFloor);
        at.start();
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

    class AttractThread extends Thread {
        private final Fraction MARGIN = Fraction.TWO; // two beats ahead
        final DanceFloor danceFloor;
        AttractThread(DanceFloor danceFloor) { this.danceFloor = danceFloor; }
        @Override
        public void run() {
            Evaluator e = new Evaluator.Standard
                (new Seq(new Apply(Expr.literal("_attract"))));
            ConcurrentMap<String,String> props =
                new ConcurrentHashMap<String,String>();
            props.put("call-pending", "false");
            DanceState ds = new DanceState
                (new DanceProgram(Program.PLUS), Formation.SQUARED_SET, props);
            Fraction offsetTime = danceFloor.waitForBeat(Fraction.ZERO);
            // round to multiple of 8 beats so we start on a phrase.
            offsetTime = Fraction.valueOf((offsetTime.intValue()/8)*8 + 8);

            while (e != null) {
                e = e.evaluate(ds);
                ds.syncDancers();
                for (Dancer d : ds.dancers()) {
                    Fraction startTime = offsetTime;
                    for (DancerPath dp : ds.movements(d)) {
                        DancerBezierPath dbp = dp.bezier(startTime);
                        startTime = startTime.add(dp.time);
                        danceFloor.addPath(d, dbp);
                    }
                }
                offsetTime = offsetTime.add(ds.currentTime());
                danceFloor.waitForBeat(offsetTime.subtract(MARGIN));
                ds = ds.cloneAndClear();
            }
        }
    }
}
