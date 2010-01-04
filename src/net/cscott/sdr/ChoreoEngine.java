package net.cscott.sdr;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

import net.cscott.jutil.MultiMap;
import net.cscott.sdr.CommandInput.InputMode;
import net.cscott.sdr.CommandInput.PossibleCommand;
import net.cscott.sdr.HUD.MessageType;
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
    private DanceThread danceThread;
    private DanceState ds;
    private final DanceFloor danceFloor;
    private final ScoreAccumulator score;
    private final HUD hud;
    public final Mode mode;
    public ChoreoEngine(DanceProgram dp, Formation f, DanceFloor danceFloor,
                        ScoreAccumulator score, HUD hud,
                        CommandInput input) {
        this.danceFloor = danceFloor;
        this.score = score;
        this.hud = hud;
        this.mode = new Mode(input, this);
        this.ds = new DanceState(dp, f);
        this.danceThread = null;
        switchToMenu();
        new InputThread(input, this, score).start();
    }

    private BlockingQueue<DanceThread> danceQueue =
        new LinkedBlockingQueue<DanceThread>();

    private synchronized void waitUntilStopped() {
        if (danceThread == null) return;
        danceThread.stopDancing();
        while (true) {
            try {
                danceThread.join();
                danceThread = null;
                return;
            } catch (InterruptedException e) {
                /* keep waiting */
            }
        }
    }

    private void startNewDanceThread(DanceThread dt) {
        danceQueue.add(dt); // this defines ordering
        synchronized (this) {
            waitUntilStopped();
            assert danceThread == null;
            while (true)
                try {
                    dt = danceQueue.take();
                    break;
                } catch (InterruptedException e) { /* repeat */ }
            dt.start();
            danceThread = dt;
        }
    }

    public CountDownLatch switchToMenu() {
        final CountDownLatch done = new CountDownLatch(1);
        new Thread() {
            @Override
            public void run() {
                Evaluator e = new Evaluator.Standard
                    (new Seq(new Apply(Expr.literal("_attract"))));
                startNewDanceThread
                    (new DanceThread(new DanceProgram(Program.PLUS), e));
                done.countDown(); // signal
            }
        }.start();
        return done;
    }
    public CountDownLatch switchToDancing() {
        final CountDownLatch done = new CountDownLatch(1);
        new Thread() {
            @Override
            public void run() {
                // XXX
                Evaluator e = new Evaluator.Standard
                    (new Seq(new Apply(Expr.literal("nothing"))));
                synchronized (ChoreoEngine.this) {
                startNewDanceThread
                    (new DanceThread(new DanceProgram(Program.PLUS), e));
                score.resetScore(Program.PLUS);
                }
                //hud.setNotice("Let's go!", 5000);
                done.countDown(); // signal.
            }
        }.start();
        return done;
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

    class DanceThread extends Thread {
        private final ConcurrentMap<String,String> props =
            new ConcurrentHashMap<String,String>();
        private final Fraction MARGIN = Fraction.TWO; // dance two beats ahead
        private final DanceState initialDanceState;
        private final Evaluator initialEvaluator;
        private boolean isEnding, goingHome;
        DanceThread(DanceProgram dp, Evaluator e) {
            props.put("call-pending", "false");
            this.initialDanceState = new DanceState
                (dp, Formation.SQUARED_SET, props);
            this.initialEvaluator = e;
            this.isEnding = false;
            this.goingHome = false;
            this.setDaemon(true);
        }

        public synchronized void stopDancing() {
            this.isEnding = true;
            props.put("call-pending", "true");
        }
        private synchronized boolean isEnding() {
            return this.isEnding;
        }

        @Override
        public void run() {
            DanceState ds = initialDanceState;
            Evaluator e = initialEvaluator;
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
                if (this.isEnding()) {
                    if (!goingHome) {
                        // go home!
                        e = new Evaluator.Standard(new Seq(new Apply(Expr.literal("go home"))));
                        goingHome = true;
                    }
                } else { // !isEnding
                    if (e==null) {
                        // align dancers to the beat
                        offsetTime = Fraction.valueOf(offsetTime.floor());
                        // do nothing for a beat
                        e = new Evaluator.Standard(new Seq(new Apply(Expr.literal("nothing"))));
                        // subtract timeliness points.
                        score.dancersWaiting();
                    }
                }
            }
            danceFloor.waitForBeat(offsetTime);
        }
    }
    private class InputThread extends Thread {
        private final CommandInput input;
        private final ChoreoEngine choreo;
        private final ScoreAccumulator score;

        InputThread(CommandInput input, ChoreoEngine choreo,
                     ScoreAccumulator score) {
            this.input = input; this.choreo = choreo; this.score = score;
            this.setDaemon(true);
        }

        private void doOneInput() {
            PossibleCommand pc;
            while (true) {
                try {
                    pc = input.getNextCommand();
                    break;
                } catch (InterruptedException e) {
                    continue; // try again!
                }
            }
            InputMode im = pc.getMode();
            // XXX what if mode is new?
            for ( ; pc != null; pc = pc.next()) {
                if (processInput(pc))
                    return;
            }
            processFail();
        }
        private boolean processInput(PossibleCommand pc) {
            String thisGuess = pc.getUserInput();
            System.err.println("PROCESS INPUT "+thisGuess);
            if (thisGuess == null)
                return false;
            if (thisGuess == PossibleCommand.UNCLEAR_UTTERANCE) {
                hud.setMessage("I couldn't hear you", MessageType.ADVICE);
                return false;
            }
            // XXX look at mode, process command
            if (pc.getMode().mainMenu() && thisGuess.equals("square up"))
                mode.switchToDancing();
            return true;
        }
        private void processFail() {

        }

        private void doNextCall() throws InterruptedException {
            String bestGuess = null, message = null;
            // get the next input
            PossibleCommand pc = input.getNextCommand();
            // go through the possibilities, and see if any is a valid call.
            while (pc != null) {
                String thisGuess = pc.getUserInput();
                if (thisGuess == PossibleCommand.UNCLEAR_UTTERANCE) {
                    hud.setMessage("I couldn't hear you", MessageType.ADVICE);
                    return;
                }
                switch (mode.getMode()) {
                case MAIN_MENU:
                    if (thisGuess.equals("square up"))
                        mode.switchToDancing();
                    break;
                case DANCING:
                    try {
                        //sendResults(choreo.execute(thisGuess, score));

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
                }
                pc = pc.next();
            }
            // if we get here, then we had a bad call
            // (none of the possibilities were good)
            // XXX IF MODE IS MAIN MENU
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

        @Override
        public void run() {
            while (true) {
                doOneInput();
            }
        }
    }
}
