package net.cscott.sdr.calls;

import net.cscott.sdr.util.Fraction;

/**
 * A {@link TimedFormation} combines a {@link Formation} with a timestamp
 * indicating when that particular formation should be reached.
 * @author C. Scott Ananian
 * @version $Id: TimedFormation.java,v 1.1 2006-10-25 20:33:59 cananian Exp $
 */
public class TimedFormation implements Comparable<TimedFormation> {
    public final Formation formation;
    public final Fraction time;
    public TimedFormation(Formation formation, Fraction time) {
        this.formation = formation; this.time = time;
    }
    /** {@link TimedFormation}s are compared to each other on the basis
     * of their {@link TimedFormation#time time} fields.
     * Earlier {@code TimedFormation}s are before later ones.
     */
    public int compareTo(TimedFormation o) {
        return this.time.compareTo(((TimedFormation)o).time);
    }
}
