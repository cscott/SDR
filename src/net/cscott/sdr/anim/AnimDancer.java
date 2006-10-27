package net.cscott.sdr.anim;

import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

import com.jme.scene.Node;
import com.jme.scene.SwitchNode;

import net.cscott.sdr.BeatTimer;
import net.cscott.sdr.calls.Action;
import net.cscott.sdr.calls.Position;
import net.cscott.sdr.calls.StandardDancer;
import net.cscott.sdr.calls.TimedPosition;
import net.cscott.sdr.util.Fraction;

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
    /** Internal queue for position updates. */
    private final Queue<TimedPosition> posQueue =
        new PriorityBlockingQueue<TimedPosition>();
    /** The last position of this dancer. */
    private TimedPosition lastPos = null;
    
    protected AnimDancer(StandardDancer dancer) {
        this.dancer = dancer;
        this.node = new SwitchNode(dancer.toString());
    }

    /** Update the node based on the current beat time.
     * This method is called from the rendering thread. */
    public abstract void update(Fraction time);

    /** Add a new position target.  This method is called from the
     * choreography thread. */
    // NOTE: should always add a TimedPosition corresponding to the
    // 'current formation' at the *start* of the call, in case there
    // was a delay between the end of the last call and this.  This
    // ensures that the dancers do not "jump" into the new position.
    public void addPosition(Fraction time, Position target) {
            posQueue.add(new TimedPosition(target, time, true));
    }
    
    /** Add a new timed action.  This method is called from the choreography
     * thread, as well as from the personality thread. */
    public void addAction(Fraction time, Action target) {
        assert false : "unimplemented"; // XXX unimplemented
    }
    
    /** Get the next position with time greater than or equal to the given
     * time.  May update the value returned by
     * {@link AnimDancer#getLastPosition()}.  Returns null if there is no
     * position more recent than that returned by
     * {@link AnimDancer#getLastPosition()}.
     */
    public TimedPosition getNextPosition(Fraction time) {
        while (true) {
            TimedPosition nextPos = posQueue.peek();
            if (nextPos==null || nextPos.time.compareTo(time) > 0)
                return nextPos; // done!
            // otherwise, update lastPos & try again.
            // safe race here: an even earlier pos may have been added
            lastPos = posQueue.poll();
        }
    }
    public TimedPosition getLastPosition() {
        return lastPos;
    }
}
