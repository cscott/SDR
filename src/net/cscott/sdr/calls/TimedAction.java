package net.cscott.sdr.calls;

import net.cscott.sdr.util.Fraction;

/**
 * A {@link TimedAction} combines an {@link Action} with a timestamp
 * indicating notionally when that particular action should occur, although
 * the exact timing and duration depends on the action: some actions may
 * begin at the specified time, with others (for example) may be centered
 * on the time given.  The timestamp may be absolute or relative. 
 * @author C. Scott Ananian
 * @version $Id: TimedAction.java,v 1.1 2006-10-26 18:33:19 cananian Exp $
 */
public class TimedAction extends Timed<TimedAction> {
    /** The dancer action called for at the given time. */
    public final Action action;
    public TimedAction(Action action, Fraction time, boolean isAbsolute) {
        super(time, isAbsolute);
        this.action = action;
    }
    @Override
    public TimedAction makeAbsolute(Timed reference) {
        if (this.isAbsolute) return this;
        Fraction newTime = ((reference==null)?Fraction.ZERO:reference.time)
            .add(this.time);
        return new TimedAction(this.action, newTime, true);
    }
}
