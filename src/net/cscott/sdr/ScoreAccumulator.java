package net.cscott.sdr;

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
 * @version $Id: ScoreAccumulator.java,v 1.1 2006-10-26 17:33:53 cananian Exp $
 */
public interface ScoreAccumulator {
    public void illegalCallGiven(String theBadCall, String message);
}
