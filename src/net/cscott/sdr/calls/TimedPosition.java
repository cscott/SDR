package net.cscott.sdr.calls;

import net.cscott.sdr.util.Fraction;

/**
 * A {@link TimedPosition} combines a {@link Position} with
 * a timestamp indicating when that particular position
 * should be reached.  The timestamp is always absolute.
 * @author C. Scott Ananian
 * @version $Id: TimedPosition.java,v 1.3 2006-10-27 05:14:34 cananian Exp $
 */
public class TimedPosition implements Comparable<TimedPosition> {
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
    /** {@link TimedPosition}s are compared to each other on the basis
     * of their {@link TimedPosition#time time} fields.
     * Earlier {@code TimedPosition}s are before later ones.
     * @throws IllegalArgumentException if the position times are not
     *   absolute.
     */
    public int compareTo(TimedPosition o) throws IllegalArgumentException {
        if (this.isAbsolute && ((TimedPosition)o).isAbsolute)
            return this.time.compareTo(((TimedPosition)o).time);
        throw new IllegalArgumentException("Comparison is meaningless unless"+
                " position times are absolute.");
    }
}
