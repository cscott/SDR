package net.cscott.sdr.calls;

import net.cscott.sdr.util.Fraction;

/**
 * A {@link TimedPosition} combines a {@link Position} with
 * a timestamp indicating when that particular position
 * should be reached.  The timestamp is always absolute.
 * @author C. Scott Ananian
 * @version $Id: TimedPosition.java,v 1.3 2006-10-27 05:14:34 cananian Exp $
 */
public class TimedPosition extends Timed<TimedPosition>  {
    /** The dancer position called for at the given time. */
    public final Position position;
    public TimedPosition(Position position, Fraction time, boolean isAbsolute) {
        super(time, isAbsolute);
        this.position = position;
    }
    @Override
    public TimedPosition makeAbsolute(Timed reference) {
        if (this.isAbsolute) return this;
        Fraction newTime = ((reference==null)?Fraction.ZERO:reference.time)
            .add(this.time);
        return new TimedPosition(this.position, newTime, true);
    }
}
