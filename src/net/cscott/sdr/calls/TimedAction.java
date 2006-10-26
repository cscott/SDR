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
public class TimedAction {
    /** The dancer action called for at the given time. */
    public final Action action;
    /** If {@link TimedAction#isAbsolute isAbsolute} is true, then the
     * absolute time at which this formation should appear.  Otherwise, the
     * relative amount of time <i>after the previous {@code TimedAction}</i>
     * at which this formation should appear. */
    public final Fraction time;
    /** Whether times are absolute, or relative to the previous
     * {@code TimedAction}. */
    public final boolean isAbsolute;

    public TimedAction(Action action, Fraction time, boolean isAbsolute) {
        this.action = action; this.time = time;
        this.isAbsolute = isAbsolute;
    }
}
