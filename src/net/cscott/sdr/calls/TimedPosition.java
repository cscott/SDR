package net.cscott.sdr.calls;

import net.cscott.sdr.util.Fraction;

/**
 * A {@link TimedPosition} combines a {@link Position} with
 * a timestamp indicating when that particular position
 * should be reached.  The timestamp is always absolute.
 * @author C. Scott Ananian
 * @version $Id: TimedPosition.java,v 1.2 2006-10-26 18:33:19 cananian Exp $
 */
public class TimedPosition {
    /** The dancer position called for at the given time. */
    public final Position position;
    /** If {@link TimedPosition#isAbsolute isAbsolute} is true, then the
     * absolute time at which this formation should appear.  Otherwise, the
     * relative amount of time <i>after the previous {@code TimedPosition}</i>
     * at which this formation should appear. */
    public final Fraction time;
    /** Whether times are absolute, or relative to the previous
     * {@code TimedPosition}. */
    public final boolean isAbsolute;
    
    public TimedPosition(Position position, Fraction time, boolean isAbsolute) {
        this.position = position; this.time = time;
        this.isAbsolute = isAbsolute;
    }
}
