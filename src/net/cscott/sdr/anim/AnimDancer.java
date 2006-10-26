package net.cscott.sdr.anim;

import com.jme.scene.Node;

import net.cscott.sdr.BeatTimer;
import net.cscott.sdr.calls.Action;
import net.cscott.sdr.calls.Position;
import net.cscott.sdr.calls.StandardDancer;
import net.cscott.sdr.util.Fraction;

/** An {@link AnimDancer} encapsulates all the information needed to
 * display and animate a single dancer.  It is given a list of
 * {@link TimedPosition}s, and uses these and a {@link BeatTimer}
 * to update the position and animation state of the scene graph {@link Node}
 * corresponding to the dancer.
 * @author C. Scott Ananian
 * @version $Id: AnimDancer.java,v 1.1 2006-10-26 19:22:07 cananian Exp $
 */
public abstract class AnimDancer {
    /** The {@link StandardDancer} corresponding to this animated dancer. */
    public final StandardDancer dancer;
    /** The scene graph {@link Node} corresponding to this animated dancer. */
    public final Node node;
    
    protected AnimDancer(StandardDancer dancer) {
        this.dancer = dancer;
        this.node = new Node(dancer.toString());
    }

    /** Update the node based on the current beat time.
     * This method is called from the rendering thread. */
    public abstract void update(BeatTimer timer);

    /** Add a new position target.  This method is called from the
     * choreography thread. */
    public abstract void addPosition(Fraction time, Position target);
    
    /** Add a new timed action.  This method is called from the choreography
     * thread, as well as from the personality thread. */
    public abstract void addAction(Fraction time, Action target);
}
