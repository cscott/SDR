package net.cscott.sdr;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import net.cscott.sdr.anim.Game;
import net.cscott.sdr.anim.SilentBeatTimer;
import net.cscott.sdr.calls.*;
import net.cscott.sdr.calls.ast.Apply;
import net.cscott.sdr.recog.LevelMonitor;
import net.cscott.sdr.recog.RecogThread;
import net.cscott.sdr.util.Fraction;
import net.cscott.sdr.CommandInput.PossibleCommand;

/**
 * This is the main class of the SDR application.
 * It creates three main threads:
 * one to do voice recognition (in net.cscott.sdr.recog),
 * one to do dancer animation (in net.cscott.sdr.anim),
 * and one to play music (in net.cscott.sdr.sound).
 * 
 * @author C. Scott Ananian
 * @version $Id: App.java,v 1.10 2006-11-11 03:06:21 cananian Exp $
 */
public class App {
    /**
     * The main entry point for the application.
     * @param args unused
     */
    public static void main(String[] args) {
        // This class synchronizes communication between the
        // input method, and the choreography engine.
        CommandInput input = new CommandInput();
        // This is the choreography engine.
        DanceState ds = new DanceState(Program.MAINSTREAM);
        ChoreoEngine choreo = new ChoreoEngine(ds);
        
        // TODO: create music player thread

        // Start the game thread.
        BlockingQueue<LevelMonitor> rendezvous =
            new ArrayBlockingQueue<LevelMonitor>(1);
        final Game game = new Game(new SilentBeatTimer(), rendezvous);
        new Thread() { // THIS IS THE GRAPHICS THREAD
            @Override public void run() { game.start(); }
        }.start();

        // give the 'loading' UI a chance to come up.
        try {
            System.err.print("Waiting for UI...");
            Thread.sleep(1000L);
        } catch (InterruptedException e) { /* ignore */ }
        System.err.println("done.");

        // create voice recognition thread
        RecogThread rt = new RecogThread(ds, input, rendezvous);
        rt.start();

        // Now start processing input, handing resulting formations to the
        // game thread.
        TimedFormation start = new TimedFormation
        (Formation.FOUR_SQUARE, Fraction.ZERO, true); // assuming dancer starts at time 0
        ChoreoThread ct = new ChoreoThread(input, choreo, null, start);
        ct.start();
        
        // now we should wait around until the game is over, and call
        // ct.shutdown() (at least).
    }

    private static class ChoreoThread extends Thread {
        private final CommandInput input;
        private final ChoreoEngine choreo;
        private final ScoreAccumulator score;
        private TimedFormation start;
        private boolean done = false;
        
        ChoreoThread(CommandInput input, ChoreoEngine choreo,
                     ScoreAccumulator score, TimedFormation start) {
            assert start.isAbsolute;
            this.input = input; this.choreo = choreo; this.score = score;
            this.start = start;
        }
        
        public synchronized void shutdown() {
            done = true;
            this.interrupt();
        }
        
        // At all points during the game we have a current
        // TimedFormation, start.  We grab strings from the input
        // and parse them using the CommandInput, and apply them to the
        // current TimedFormation to get a time-sorted list of
        // additional TimedFormations.  We have to go through these
        // to extract TimedPositions for each dancer, which are
        // given to the AnimDancers.
        private void doNextCall() throws InterruptedException {
            String lastCall = ""; String message = "";
            // get the next input
            PossibleCommand pc = input.getNextCommand();
            // go through the possibilities, and see if any is a valid call.
            while (pc != null) {
                try {
                    Apply a = pc.getApply();
                    if (a==null) throw new BadCallException("Parsing error");
                    start = sendResults(choreo.execute(start, a, score));
                    // this was a good call!
                    System.err.println(pc.getUserInput()); // XXX SEND TO HUD
                    score.goodCallGiven(a, pc.getStartTime(), pc.getEndTime());
                    return;
                } catch (BadCallException be) {
                    lastCall = pc.getUserInput();
                    message = be.getMessage();
                    // try the next possibility.
                }
                pc = pc.next();
            }
            // if we get here, then we had a bad call
            // (none of the possibilites were good)
            assert lastCall != null;
            score.illegalCallGiven(lastCall, message);
            return;
        }
        private TimedFormation sendResults(List<TimedFormation> l) {
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
