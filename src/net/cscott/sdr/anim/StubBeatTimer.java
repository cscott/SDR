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
 * @version $Id: StubBeatTimer.java,v 1.1 2006-11-08 05:18:44 cananian Exp $
 */
public class StubBeatTimer implements BeatTimer {
    private final Timer timer = Timer.getTimer();
    private final float initialTime;
    /**
     * Create a {@link StubBeatTimer}.
     */
    public StubBeatTimer() {
        this.initialTime = timer.getTimeInSeconds();
    }

    public Fraction getCurrentBeat() {
        return Fraction.valueOf((timer.getTimeInSeconds()-initialTime)*2);
    }
}
