package net.cscott.sdr.calls;

import net.cscott.sdr.util.Fraction;

/** A <code>Warp</code> denotes a time-dependent transformation of the
 * dancers' positions.  Time runs from 0 (at the start of the call) to
 * 1 (at the end); it is the responsibility of the caller to scale
 * the time argument according to the actual length of the warped calls.
 * As an example, the <code>Warp</code> for a stretched
 * box formation starts with boxes overlapping at time t=0, and
 * results in an undistorted setup at time t=1.
 * @author C. Scott Ananian
 * @version $Id: Warp.java,v 1.1 2006-10-09 21:41:21 cananian Exp $
 */
public abstract class Warp {
    // XXX: not sure how best to implement this generally yet.
    // Maybe as a set of point/line correspondences at given times?
    // To warp a point, identify the corners of a box containing the
    // point among the 'input' points, and then linearly-interpolate its
    // position within the same box in 'output' points.  Do this for the
    // two closest time positions, then linearlly-interpolate between them.
    // Warps should probably include rotation as well as position, to allow
    // 'mirror' warp (for example)
    private Warp() { }

    public abstract Position warp(Position p, Fraction time);

    public static final Warp NONE = new Warp() {
        public Position warp(Position p, Fraction time) { return p; }
    };
    
    public static final Warp MIRROR = new Warp() {
        public Position warp(Position p, Fraction time) {
            return new Position(p.x.negate(), p.y, p.facing.negate());
        }
    };
}
