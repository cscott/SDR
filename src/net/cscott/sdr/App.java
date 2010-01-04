package net.cscott.sdr;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CyclicBarrier;

import net.cscott.sdr.anim.Game;
import net.cscott.sdr.calls.DanceProgram;
import net.cscott.sdr.calls.Formation;
import net.cscott.sdr.calls.Program;
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
    /**
     * The main entry point for the application.
     * @param args unused
     */
    public static void main(String[] args) {
        // score & display
        HUD hud = new HUD();
        ScoreAccumulator score = new ScoreAccumulator(hud);
        // This class synchronizes communication between the
        // input method, and the choreography engine.
        CommandInput input = new CommandInput();
        // This class synchronizes communication between the
        // choreography engine and the animated dancers
        DanceFloor danceFloor = new DanceFloor();
        // This is the choreography engine.
        DanceProgram ds = new DanceProgram(Program.BASIC);
        ChoreoEngine choreo = new ChoreoEngine
            (ds, Formation.FOUR_SQUARE, danceFloor, score, hud, input);
        Mode mode = choreo.mode;

        // Start the game thread.
        BlockingQueue<RecogThread.Control> rendezvousRT =
            new ArrayBlockingQueue<RecogThread.Control>(1);
        BlockingQueue<BeatTimer> rendezvousBT =
            new ArrayBlockingQueue<BeatTimer>(1);
        CyclicBarrier musicSync = new CyclicBarrier(2);
        CyclicBarrier sphinxSync = new CyclicBarrier(2);
        final Game game =
            new Game(input, hud, danceFloor, mode, rendezvousBT, rendezvousRT, musicSync, sphinxSync);
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
        RecogThread rt = new RecogThread(input, rendezvousRT);
        rt.start();

        // We're ready to rumble!
        // now we should wait around until the game is over and cleanup?
    }
}
