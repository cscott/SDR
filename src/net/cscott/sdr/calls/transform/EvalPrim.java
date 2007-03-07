package net.cscott.sdr.calls.transform;

import net.cscott.sdr.calls.*;
import net.cscott.sdr.calls.DancerPath.PointOfRotation;
import net.cscott.sdr.calls.ast.*;
import net.cscott.sdr.calls.ast.Prim.Direction;
import net.cscott.sdr.util.Fraction;
import net.cscott.sdr.util.Point;
import net.cscott.jutil.Default.PairList;

/** Apply a {@link Prim} to a {@link Dancer}'s {@link Position} to yield a
 * {@link DancerPath} (which contains a new {@link Position}).
 * @author C. Scott Ananian
 * @version $Id: EvalPrim.java,v 1.1 2007-03-07 22:11:09 cananian Exp $
 */
public abstract class EvalPrim {

    /** "Dance" the given primitive for the given dancer in the given
     * formation to yield a {@link DancerPath} for that dancer. */
    public static DancerPath apply(Dancer d, Formation f, Prim p) {
        return apply(p, f.location(d), f.dancers().size());
    }
    /** "Dance" the given primitive from the given position (in a
     * formation of the given size) to yield a {@link DancerPath}. */
    public static DancerPath apply(Prim prim, final Position from, int formationSize) {
        // XXX: for now, use the center of the formation as the center point
        // for in/out/etc.  We may have to revisit this for 'pass in',
        // which explicitly wants to reference the flagpole center of
        // the set.
        final Point center = new Point(Fraction.ZERO, Fraction.ZERO);
        // apply 'in/out' modifier to x/y/rotation
        Fraction dx = applyDir(prim.dirX, from.x, center.x);
        Fraction dy = applyDir(prim.dirY, from.y, center.y);
        Fraction dr = applyDir(prim.dirRot, from.facing, from, center);
        // add the deltas to create a new Position.
        Position to = new Position
             (from.x.add(dx), from.y.add(dy), from.facing.add(dr));
        // the arc center is the center of the formation, although we should
        // set it to null if this motion doesn't involve rotation.
        Point arcCenter = null;
        if (prim.forceArc ||
            (dy.compareTo(Fraction.ZERO)>0 &&
             ((dx.compareTo(Fraction.ZERO)>0 &&
               dr.compareTo(Fraction.ZERO)>0) ||
              (dx.compareTo(Fraction.ZERO)<0 &&
               dr.compareTo(Fraction.ZERO)<0))))
            arcCenter = center;
        // set the point of rotation based on the size of the formation
        // XXX: we may need to do something smarter here eventually.
        PointOfRotation por = null;
        switch(formationSize) {
        case 1: por = PointOfRotation.SINGLE_DANCER; break;
        case 2: por = PointOfRotation.TWO_DANCERS; break;
        case 4: por = PointOfRotation.FOUR_DANCERS; break;
        case 8: por = PointOfRotation.SQUARE_CENTER; break;
        default: break;
        }
        if (arcCenter==null) por = null;
        // we'll set rolldir equal to dr.
        // XXX: we should be clever about how we translate "stand still" actions
        // so that the roll/sweep dirs are preserved.
        ExactRotation rollDir = new ExactRotation(dr);
        
        // sweep dir is set based on angle swept through center from 'from' to
        // 'to', although of course remember the 'sweep' call is only valid
        // if we end up 'as couples'
        ExactRotation fromSweep = ExactRotation.fromXY
            (center.x.subtract(from.x), center.y.subtract(from.y));
        ExactRotation toSweep = ExactRotation.fromXY
            (center.x.subtract(to.x), center.y.subtract(to.y));
        ExactRotation sweepDir = toSweep.subtract(fromSweep.amount);
        if (sweepDir.amount.compareTo(Fraction.ONE_HALF) >= 0)
            sweepDir = sweepDir.subtract(Fraction.ONE);
        if (sweepDir.amount.compareTo(Fraction.ONE_HALF)==0 ||
            sweepDir.amount.negate().compareTo(Fraction.ONE_HALF)==0)
            sweepDir = ExactRotation.ZERO; // XXX: can't tell sweep direction
        
        
        return new DancerPath(from, to, arcCenter, prim.time, por, rollDir, sweepDir);
    }
    
    /** Negate the given fraction if necessary to be consistent with the "in"
     * direction. */
    private static Fraction applyDir(Direction d, Fraction x, Fraction center) {
        if (d==Direction.ASIS) return x;
        assert x.compareTo(center) == 0;
        return (x.compareTo(center) > 0) ? x.negate() : x;
    }
    /** Adjust the given rotation to be consistent with the direction of "in"
     * rotation. */
    private static Fraction applyDir(Direction d, Rotation r, Position from, Point center) {
        assert from.facing.isExact() && r.isExact();
        Fraction dr = r.amount;
        if (d != Direction.ASIS) {
            // don't allow in/out if facing direction toward the center
            // direction from dancer to center point
            ExactRotation towardCenter = ExactRotation.fromXY
                (center.x.subtract(from.x), center.y.subtract(from.y));
            ExactRotation awayCenter = towardCenter.add(Fraction.ONE_HALF);
            
            if (from.facing.equals(towardCenter) || from.facing.equals(awayCenter))
                throw new BadCallException
                       ("Can't face in/out when already facing exactly" +
                        " toward/away from the center");

            Fraction f = from.facing.amount;
            while (f.compareTo(towardCenter.amount) < 0)
                f = f.add(Fraction.ONE);
            if (f.compareTo(awayCenter.amount) < 0)
                // for a facing direction in this range, ccw rotation
                // is 'in'
                dr = dr.negate();
        }
        return dr;
    }
}
