package net.cscott.sdr.calls;

import java.util.ArrayList;
import java.util.List;

import net.cscott.sdr.util.Fraction;

/**
 * Abstract superclass for {@link TimedAction}, {@link TimedPosition}, and
 * {@link TimedFormation}, which pair a {@link Action}, {@link Position}, or
 * {@link Formation} with a specific time at which it occurs.
 * @author C. Scott Ananian
 */
public abstract class Timed<T extends Timed> implements Comparable<T> {

    /** If {@link Timed#isAbsolute isAbsolute} is true, then the
     * absolute time at which this formation should appear.  Otherwise, the
     * relative amount of time <i>after the previous {@link Timed}</i>
     * at which this formation should appear. */
    public final Fraction time;
    /** Whether times are absolute, or relative to the previous
     * {@link Timed}. */
    public final boolean isAbsolute;

    protected Timed(Fraction time, boolean isAbsolute) {
        this.time = time;
        this.isAbsolute = isAbsolute;
    }
    /** {@link Timed}s are compared to each other on the basis
     * of their {@link Timed#time} fields.
     * Earlier {@link Timed}s are before later ones.
     * @throws IllegalArgumentException if the position times are not
     *   absolute.
     */
    public int compareTo(T t) throws IllegalArgumentException {
        if (this.isAbsolute && t.isAbsolute)
            return this.time.compareTo(t.time);
        throw new IllegalArgumentException("Comparison is meaningless unless"+
                " times are absolute.");
    }
    /**
     * Given a reference, make this {@link Timed} absolute.  Returns
     * {@code this} if the {@link Timed} is already absolute.  A null
     * reference should be treated as one with an absolute time of zero.
     * @param reference The previous {@link Timed} (must be absolute)
     */
    public abstract T makeAbsolute(Timed reference);
    /**
     * Take a list of possibly-relative times and make them all absolute.
     * If the first element of the list is not absolute, all times will
     * be taken relative to a reference of zero.
     */
    public static <T extends Timed<T>> List<T> makeAbsolute(List<T> l) {
        List<T> result = new ArrayList<T>(l.size());
        T last = null;
        for (T t : l) {
            last = t.makeAbsolute(last);
            result.add(last);
        }
        return result;
    }
}