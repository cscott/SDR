/**
 * 
 */
package net.cscott.sdr.anim;

import com.jme.util.Timer;

import net.cscott.sdr.BeatTimer;
import net.cscott.sdr.util.Fraction;

/**
 * The {@link SilentBeatTimer} generates beats from a wall clock: it is not
 * synchronized to any music source.
 * @author C. Scott Ananian
 * @version $Id: SilentBeatTimer.java,v 1.1 2006-11-08 19:04:39 cananian Exp $
 */
public class SilentBeatTimer implements BeatTimer {
    /** Beats per minute. */
    private final int BPM;
    /** The wall-clock time we will use to generate the beat timing. */
    private final Timer timer = Timer.getTimer();
    /** The "zero beat" time. */
    private final float initialTime;
    /**
     * Create a {@link SilentBeatTimer} with the specified beat rate (in
     * "beats per minute").
     */
    public SilentBeatTimer(int bpm) {
        this.initialTime = timer.getTimeInSeconds();
        this.BPM = bpm;
    }
    /** Create a {@link SilentBeatTimer} which generates beats at 120bpm. */
    public SilentBeatTimer() {
        this(120);
    }

    public Fraction getCurrentBeat() {
        float t = timer.getTimeInSeconds()-initialTime;
        
        return Fraction.valueOf(t*BPM/60f);
    }
}
