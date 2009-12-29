package net.cscott.sdr;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CyclicBarrier;

import net.cscott.jutil.MultiMap;
import net.cscott.sdr.CommandInput.PossibleCommand;
import net.cscott.sdr.anim.Game;
import net.cscott.sdr.calls.BadCallException;
import net.cscott.sdr.calls.DanceProgram;
import net.cscott.sdr.calls.Dancer;
import net.cscott.sdr.calls.DancerPath;
import net.cscott.sdr.calls.Formation;
import net.cscott.sdr.calls.Program;
import net.cscott.sdr.calls.TimedFormation;
import net.cscott.sdr.calls.ast.Apply;
import net.cscott.sdr.recog.LevelMonitor;
import net.cscott.sdr.recog.RecogThread;
import net.cscott.sdr.sound.MidiThread;

/**
 * This is the main class of the SDR application.
 * It creates three main threads:
 * one to do voice recognition (in net.cscott.sdr.recog),
 * one to do dancer animation (in net.cscott.sdr.anim),
 * and one to play music (in net.cscott.sdr.sound).
 *
 * @author C. Scott Ananian
 * @version $Id: App.java,v 1.12 2006-11-13 04:28:15 cananian Exp $
 */
public class App {
    public static final boolean DEBUG=true;
    /**
     * The main entry point for the application.
     * @param args unused
     */
    public static void main(String[] args) {
        // This class synchronizes communication between the
        // input method, and the choreography engine.
        CommandInput input = new CommandInput();
        // This is the choreography engine.
        DanceProgram ds = new DanceProgram(Program.BASIC);
        ChoreoEngine choreo = new ChoreoEngine(ds, Formation.FOUR_SQUARE);

        // Start the game thread.
        BlockingQueue<LevelMonitor> rendezvousLM =
            new ArrayBlockingQueue<LevelMonitor>(1);
        BlockingQueue<BeatTimer> rendezvousBT =
            new ArrayBlockingQueue<BeatTimer>(1);
        CyclicBarrier musicSync = new CyclicBarrier(2);
        CyclicBarrier sphinxSync = new CyclicBarrier(2);
        final Game game =
            new Game(rendezvousBT, rendezvousLM, musicSync, sphinxSync);
        new Thread() { // THIS IS THE GRAPHICS THREAD
            @Override public void run() {
                game.start();
            }
        }.start();

        // Create music player thread
        try { musicSync.await(); }
        catch (Exception e) { assert false : e; /* broken barrier! */ }
        MidiThread mt = new MidiThread(rendezvousBT);
        mt.start();

        // create voice recognition thread
        try { sphinxSync.await(); }
        catch (Exception e) { assert false : e; /* broken barrier! */ }
        RecogThread rt = new RecogThread(input, rendezvousLM);
        rt.start();

        // Now start processing input, handing resulting formations to the
        // game thread.
        ChoreoThread ct = new ChoreoThread(input, choreo, null);
        ct.start();

        // now we should wait around until the game is over, and call
        // ct.shutdown() (at least).
    }

    private static class ChoreoThread extends Thread {
        private final CommandInput input;
        private final ChoreoEngine choreo;
        private final ScoreAccumulator score;
        private boolean done = false;

        ChoreoThread(CommandInput input, ChoreoEngine choreo,
                     ScoreAccumulator score) {
            this.input = input; this.choreo = choreo; this.score = score;
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
                    score.goodCallGiven(choreo.lastCall(), pc.getStartTime(), pc.getEndTime());
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
