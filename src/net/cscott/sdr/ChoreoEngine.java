package net.cscott.sdr;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import net.cscott.jutil.MultiMap;
import net.cscott.sdr.CommandInput.PossibleCommand;
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
    private ChoreoThread at;
    private DanceState ds;
    private final ScoreAccumulator score;
    private final HUD hud;
    private final Mode mode;
    public ChoreoEngine(DanceProgram dp, Formation f, DanceFloor danceFloor,
                        ScoreAccumulator score, HUD hud, Mode mode,
                        CommandInput input) {
        this.score = score;
        this.hud = hud;
        this.mode = mode;
        this.ds = new DanceState(dp, f);
        this.at = new ChoreoThread(danceFloor);
        at.start();
        new InputThread(input, this, score).start();
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

    class ChoreoThread extends Thread {
        private final ConcurrentMap<String,String> props =
            new ConcurrentHashMap<String,String>();
        private final Fraction MARGIN = Fraction.TWO; // dance two beats ahead
        private final DanceFloor danceFloor;
        ChoreoThread(DanceFloor danceFloor) {
            props.put("call-pending", "false");
            this.danceFloor = danceFloor;
        }
        @Override
        public void run() {
            Evaluator e = new Evaluator.Standard
                (new Seq(new Apply(Expr.literal("_attract"))));
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
    private static class InputThread extends Thread {
        private final CommandInput input;
        private final ChoreoEngine choreo;
        private final ScoreAccumulator score;
        private boolean done = false;

        InputThread(CommandInput input, ChoreoEngine choreo,
                     ScoreAccumulator score) {
            this.input = input; this.choreo = choreo; this.score = score;
        }

        public synchronized void shutdown() {
            done = true;
            this.interrupt();
        }

        private void doNextCall() throws InterruptedException {
            String bestGuess = null, message = null;
            // get the next input
            PossibleCommand pc = input.getNextCommand();
            // go through the possibilities, and see if any is a valid call.
            while (pc != null) {
                String thisGuess = pc.getUserInput();
                if (thisGuess == PossibleCommand.UNCLEAR_UTTERANCE) {
                    sendToHUD("I couldn't hear you");
                    return;
                }
                try {
                    sendResults(choreo.execute(thisGuess, score));

                    // this was a good call!
                    sendToHUD(thisGuess);
                    score.goodCallGiven(choreo.lastCall(), choreo.currentFormation(), pc.getStartTime(), pc.getEndTime());
                    return;
                } catch (BadCallException be) {
                    if (bestGuess==null ||
                        (message==null && be.getMessage()!=null)) {
                        bestGuess = thisGuess;
                        message = be.getMessage();
                    }
                    // try the next possibility.
                }
                pc = pc.next();
            }
            // if we get here, then we had a bad call
            // (none of the possibilities were good)
            assert bestGuess != null;
            if (message==null) message="Unknown problem";
            score.illegalCallGiven(bestGuess, message);
            sendToHUD(bestGuess+": "+message);
            return;
        }
        private void sendToHUD(String s) {
            // TODO: write me!
            System.err.println("HUD: "+s);
        }
        private TimedFormation sendResults(MultiMap<Dancer,DancerPath> l) {
            // TODO: step 1: adjust timing as needed if 'start' is no longer in the future.
            // TODO: step 2: go through the formations and pull out individual dancer actions
            // TODO: step 3: send the dancer actions to the AnimDancer objects.
            return null;//l.get(l.size()-1);
        }

        @Override
        public void run() {
            while (!done) {
                try {
                    doNextCall();
                } catch (InterruptedException e) {
                    /* go around again & check our 'done' flag. */
                }
            }
        }
    }

}
