package net.cscott.sdr.calls;

import net.cscott.sdr.util.Fraction;

/**
 * A {@link TimedFormation} combines a {@link Formation} with a timestamp
 * indicating when that particular formation should be reached.  Timestamps
 * can be either absolute or relative.
 * @author C. Scott Ananian
 * @version $Id: TimedFormation.java,v 1.2 2006-10-26 17:30:25 cananian Exp $
 */
public class TimedFormation extends Timed<TimedFormation> {
    /** The dancer formation called for at the given time. */
    public final Formation formation;
    public TimedFormation(Formation formation, Fraction time, boolean isAbsolute) {
        super(time, isAbsolute);
        this.formation = formation;
    }
    @Override
    public TimedFormation makeAbsolute(Timed reference) {
        if (this.isAbsolute) return this;
        Fraction newTime = ((reference==null)?Fraction.ZERO:reference.time)
            .add(this.time);
        return new TimedFormation(this.formation, newTime, true);
    }
}
