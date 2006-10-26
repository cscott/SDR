package net.cscott.sdr.calls;

import net.cscott.sdr.util.Fraction;

/**
 * A {@link TimedFormation} combines a {@link Formation} with a timestamp
 * indicating when that particular formation should be reached.  Timestamps
 * can be either absolute or relative.
 * @author C. Scott Ananian
 * @version $Id: TimedFormation.java,v 1.2 2006-10-26 17:30:25 cananian Exp $
 */
public class TimedFormation implements Comparable<TimedFormation> {
    /** The dancer formation called for at the given time. */
    public final Formation formation;
    /** If {@link TimedFormation#isAbsolute isAbsolute} is true, then the
     * absolute time at which this formation should appear.  Otherwise, the
     * relative amount of time <i>after the previous {@code TimedFormation}</i>
     * at which this formation should appear. */
    public final Fraction time;
    /** Whether times are absolute, or relative to the previous
     * {@code TimedFormation}. */
    public final boolean isAbsolute;
    public TimedFormation(Formation formation, Fraction time, boolean isAbsolute) {
        this.formation = formation; this.time = time; this.isAbsolute = isAbsolute;
    }
    /** {@link TimedFormation}s are compared to each other on the basis
     * of their {@link TimedFormation#time time} fields.
     * Earlier {@code TimedFormation}s are before later ones.
     * @throws IllegalArgumentException if the formation times are not
     *   absolute.
     */
    public int compareTo(TimedFormation o) throws IllegalArgumentException {
        if (this.isAbsolute && ((TimedFormation)o).isAbsolute)
            return this.time.compareTo(((TimedFormation)o).time);
        throw new IllegalArgumentException("Comparison is meaningless unless"+
                " formation times are absolute.");
    }
}
