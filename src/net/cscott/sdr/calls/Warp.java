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
 * @version $Id: Warp.java,v 1.2 2006-10-10 04:32:42 cananian Exp $
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
    
    public static Warp rotateAndMove(Position from, Position to) {
        Rotation rot = to.facing.add(from.facing.amount.negate());
        Position nFrom = rotateCWAroundOrigin(from,rot);
        final Position warp = new Position
            (to.x.subtract(nFrom.x), to.y.subtract(nFrom.y),
                    rot);
        Warp w = new Warp() {
            public Position warp(Position p, Fraction time) {
                p = rotateCWAroundOrigin(p, warp.facing);
                return new Position(p.x.add(warp.x), p.y.add(warp.y), p.facing);
            }
        };
        assert to.equals(w.warp(from,Fraction.ZERO));
        return w;
    }
    // helper method for rotateAndMove
    private static Position rotateCWAroundOrigin(Position p, Rotation amt) {
        Fraction x = p.x.multiply(amt.toY()).add(p.y.multiply(amt.toX()));
        Fraction y = p.y.multiply(amt.toY()).subtract(p.x.multiply(amt.toX()));
        return new Position(x, y, p.facing.add(amt.amount));
    }
    
}
