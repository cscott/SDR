package net.cscott.sdr;

import net.cscott.sdr.calls.ast.Apply;

/**
 * This interface will eventually be the means of communication between the
 * choreography engine and the game score mechanism.  Information about
 * flow, formation variety, and call repetition will be communicated
 * through calls to an object implementing this interface.  That object
 * is responsible for (a) assigning numerical weights to the different
 * scoring events/criteria, and (b) communicating these events/criteria
 * to the player (for example, by updating score meters or displaying
 * messages).
 * @author C. Scott Ananian
 * @version $Id: ScoreAccumulator.java,v 1.2 2006-11-10 15:24:20 cananian Exp $
 */
public interface ScoreAccumulator {
    // XXX: this should take something like the beatTime or a difference
    // from ideal beatTime, but at the moment we're just giving the raw
    // wall clock time (in ms since the epoch).
    public void goodCallGiven(Apply theCall, long startTime, long endTime);
    public void illegalCallGiven(String theBadCall, String message);
}
