/**
 * 
 */
package net.cscott.sdr.anim;

import com.jme.util.Timer;

import net.cscott.sdr.BeatTimer;
import net.cscott.sdr.util.Fraction;

/**
 * The {@link StubBeatTimer} generates 120bpm from a wall clock: it is not
 * synchronized to any music source.
 * @author C. Scott Ananian
 * @version $Id: StubBeatTimer.java,v 1.2 2006-11-08 18:53:44 cananian Exp $
 */
public class StubBeatTimer implements BeatTimer {
    private final int BPM = 120;
    private final Timer timer = Timer.getTimer();
    private final float initialTime;
    /**
     * Create a {@link StubBeatTimer}.
     */
    public StubBeatTimer() {
        this.initialTime = timer.getTimeInSeconds();
    }

    public Fraction getCurrentBeat() {
        float t = timer.getTimeInSeconds()-initialTime;
        
        return Fraction.valueOf(t*BPM/60f);
    }
}
