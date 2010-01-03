package net.cscott.sdr.anim;

import net.cscott.sdr.BeatTimer;
import net.cscott.sdr.DanceFloor;
import net.cscott.sdr.calls.DancerBezierPath;
import net.cscott.sdr.calls.StandardDancer;
import net.cscott.sdr.calls.TimedPosition;
import net.cscott.sdr.util.Fraction;

import com.jme.scene.Node;
import com.jme.scene.SwitchNode;

/** An {@link AnimDancer} encapsulates all the information needed to
 * display and animate a single dancer.  It is given a list of
 * {@link TimedPosition}s, and uses these and a {@link BeatTimer}
 * to update the position and animation state of the scene graph {@link Node}
 * corresponding to the dancer.
 * @author C. Scott Ananian
 * @version $Id: AnimDancer.java,v 1.2 2006-10-27 05:16:25 cananian Exp $
 */
public abstract class AnimDancer {
    /** The {@link StandardDancer} corresponding to this animated dancer. */
    public final StandardDancer dancer;
    /** The scene graph {@link Node} corresponding to this animated dancer. */
    public final SwitchNode node;
    /** The {@link DanceFloor} containing position information for the dancers.
     */
    public final DanceFloor danceFloor;
    /** Extra speedup time, to make motion smooth. */
    private double factor;
    /** The start time of the last motion we saw. */
    private Fraction lastStart = null;

    protected AnimDancer(DanceFloor danceFloor, StandardDancer dancer) {
        this.danceFloor = danceFloor;
        this.dancer = dancer;
        this.node = new SwitchNode(dancer.toString());
    }
    /** Subclasses should implement this to draw the dancer. */
    public abstract void update(Fraction time, float x, float y, float rot);

    /** Update the node based on the current beat time.
     * This method is called from the rendering thread. */
    public void update(Fraction time) {
        DancerBezierPath dbp = danceFloor.location(dancer, time);
        if (dbp==null) {
            // dancer is not currently visible.
            this.node.disableAllChildren();
            return;
        }
        double t = time.doubleValue();
        double start = dbp.startTime.doubleValue();
        double duration = dbp.duration.doubleValue();
        // fixup t based on when we first saw this next bezier path, to
        // avoid sudden jumps in position.
        if (lastStart==null || dbp.startTime.compareTo(lastStart) != 0) {
            // new motion.
            lastStart = dbp.startTime;
            factor = t - start;
        }
        if (t < (start+duration) && (duration > factor)) {
            double progress = (t - (start+factor)) / (duration - factor);
            t = t - (factor * (1 - progress));
        }
        update(time, (float) dbp.evaluateX(t), (float) dbp.evaluateY(t),
               (float) dbp.evaluateAngle(t));
    }
}
