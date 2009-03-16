package net.cscott.sdr.calls;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import net.cscott.sdr.util.Fraction;
import net.cscott.sdr.util.Tools.ListMultiMap;
import static net.cscott.sdr.util.Tools.mml;

/**
 * {@link DanceState} captures all the <i>dynamic</i> information about a
 * dance: the current formation and the queues of dancer actions and calls. It
 * includes a link to a {@link DanceProgram} which is the static information
 * about the dance.
 *
 * @author C. Scott Ananian
 */
public class DanceState {
    public final DanceProgram dance;
    private final NavigableMap<Fraction, Formation> formations;
    private final ListMultiMap<Dancer, DancerPath> movements;
    private final ListMultiMap<Dancer, TimedAction> actions; // XXX?
    private final Map<Dancer, Fraction> timeOfLastMovement;

    public DanceState(DanceProgram dance, Formation f) {
        this.dance = dance;
        this.formations = new TreeMap<Fraction,Formation>();
        this.formations.put(Fraction.ZERO, f);
        this.movements = mml();
        this.actions = mml();
        this.timeOfLastMovement =
            new HashMap<Dancer,Fraction>(f.dancers().size());
        // initialize timeOfLastMovement
        for (Dancer d: f.dancers())
            this.timeOfLastMovement.put(d, Fraction.ZERO);
        // xxx: initialize actions?
    }

    /** Return the last in the list of timed formations. */
    public Formation currentFormation() {
        return formations.lastEntry().getValue();
    }
    /** Return the time of the last formation. */
    public Fraction currentTime() {
        return formations.lastKey();
    }

    /**
     * Return a new independent dance state with the same static state and
     * current formation, but empty movements and actions. The timing of the
     * current formation is reset to zero. This is used for independently
     * evaluation subcalls, when we then want to apply a transformation to the
     * timing, positions, movements, formations, etc, before adding them to the
     * parent state.
     *
     * @return a new clean DanceState
     */
    public DanceState cloneAndClear() {
        return cloneAndClear(currentFormation());
    }

    /**
     * Similar to {@link #cloneAndClear()}, but allows you to specify the new
     * 'current formation' of the result. This is useful for concepts which
     * alter the shape of the current formation (say, creating a mirror image)
     * before applying the subcall.
     *
     * @param formation
     *            what {@code result.currentFormation()} should return.
     * @return a new clean DanceState.
     */
    public DanceState cloneAndClear(Formation formation) {
        // XXX: should revisit this?
        return new DanceState(dance, formation);
    }

    /**
     * Move the given dancer along the specified dancer path. This will alter
     * the {@link #currentFormation()}, and may create additional intermediate
     * formations, if the {@link DancerPath#time} falls in between existing
     * formations.
     *
     * @param d
     * @param dp
     */
    public void add(Dancer d, DancerPath dp) {
        // add to list of dancer paths
        this.movements.add(d, dp);
        // adjust formations.
        Fraction last = this.timeOfLastMovement.get(d);
        Fraction next = last.add(dp.time);
        // get formation with time == next, or "just before"
        if (!this.formations.containsKey(next)) {
            // clone the formation "just before"
            this.formations.put(next,
                                this.formations.floorEntry(next).getValue());
        }
        // okay, now iterate from 'next' forward, adjusting this dancer's
        // location
        for (Map.Entry<Fraction, Formation> me:
             this.formations.tailMap(next).entrySet()) {
            me.setValue(me.getValue().move(d, dp.to));
        }
        // done!
    }
    public List<DancerPath> movements(Dancer d) {
        return Collections.unmodifiableList(this.movements.getValues(d));
    }

    /**
     * Return a sorted list of {@link TimedFormation}s describing the net
     * effect of the {@link DancerPath}s in this {@link DanceState}. The first
     * element in the list will match the starting formation provided when this
     * {@link DanceState} was constructed, will have an absolute time of
     * {@link Fraction#ZERO}, and will have all dancers selected. The final
     * element in the list will represent the final state. Formations should not
     * be considered animation targets: not all dancers will have moved in all
     * formations. For example, if dancer #1 has a movement which takes 2 beats,
     * and dancer #2 has two movements which take 1 each, the result list will
     * include the starting formation, with time 0, an intermediate formation
     * with time 1 where dancer #1 is in their starting position and dancer #2
     * has taken their first movement, and a final position with time 2 where
     * both dancer #1 and dancer #2 have moved.  Consult the list of
     * {@link DancerPath}s for accurate animation information.
     */
    public List<TimedFormation> formations() {
        List<TimedFormation> result =
            new ArrayList<TimedFormation>(this.formations.size());
        for (Map.Entry<Fraction,Formation> me: this.formations.entrySet()) {
            result.add(new TimedFormation(me.getValue(), me.getKey(), true));
        }
        return Collections.unmodifiableList(result);
    }
}
