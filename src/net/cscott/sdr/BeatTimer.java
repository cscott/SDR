package net.cscott.sdr;

import net.cscott.sdr.util.Fraction;

/** {@link BeatTimer} is a simple interface that allows a caller to obtain
 * the current time, in units of "beats".  One beat is roughly one step
 * in square dancing; tempos are usually between 120 and 130 beats per minute.
 * @author C. Scott Ananian
 * @version $Id: BeatTimer.java,v 1.1 2006-10-26 19:22:07 cananian Exp $
 */
public interface BeatTimer {
    public Fraction getCurrentBeat();
}
