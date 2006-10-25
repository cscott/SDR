package net.cscott.sdr.calls;

import net.cscott.sdr.util.Fraction;

/**
 * A {@link TimedPosition} combines a {@link Position} with
 * a timestamp indicating when that particular position
 * should be reached.
 * @author C. Scott Ananian
 * @version $Id: TimedPosition.java,v 1.1 2006-10-25 20:33:59 cananian Exp $
 */
public class TimedPosition {
    public final Position position;
    public final Fraction time;
    public TimedPosition(Position position, Fraction time) {
        this.position = position; this.time = time;
    }
}
