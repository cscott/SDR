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
 * @doc.test Partner trade:
 *  js> importPackage(net.cscott.sdr.calls)
 *  js> importPackage(net.cscott.sdr.calls.ast)
 *  js> fm = SelectorList.COUPLE.match(Formation.FOUR_SQUARE); undefined
 *  js> f=[ff for (ff in Iterator(fm.matches.values()))
 *    >    if (ff.dancers().contains(StandardDancer.COUPLE_1_BOY))][0]
 *  net.cscott.sdr.calls.TaggedFormation@1e6612c[
 *    location={COUPLE 1 BOY=-1,0,n, COUPLE 1 GIRL=1,0,n}
 *    selected=[COUPLE 1 BOY, COUPLE 1 GIRL]
 *    tags={COUPLE 1 BOY=BEAU, COUPLE 1 GIRL=BELLE}
 *  ]
 *  js> f.toStringDiagram()
 *  1B^  1G^
 *  js> // first part of partner trade
 *  js> p1b = EvalPrim.apply(StandardDancer.COUPLE_1_BOY, f,
 *    >                      AstNode.valueOf('(Prim 1, 3, right, 3)'))
 *  DancerPath[from=-1,0,n,to=0,3,e,arcCenter=0,0,time=3,pointOfRotation=TWO_DANCERS,rollDir=right,sweepDir=right]
 *  js> p1g = EvalPrim.apply(StandardDancer.COUPLE_1_GIRL, f,
 *    >                      AstNode.valueOf('(Prim -1, 1, left, 3)'))
 *  DancerPath[from=1,0,n,to=0,1,w,arcCenter=0,0,time=3,pointOfRotation=TWO_DANCERS,rollDir=left,sweepDir=left]
 *  js> f = f.move(StandardDancer.COUPLE_1_BOY, p1b.to).move(StandardDancer.COUPLE_1_GIRL, p1g.to); f.toStringDiagram()
 *  1B>
 *  
 *  1G<
 *  
 *  js> // second part of partner trade
 *  js> p1b = EvalPrim.apply(StandardDancer.COUPLE_1_BOY, f,
 *    >                      AstNode.valueOf('(Prim 3, 1, right, 3)'))
 *  DancerPath[from=0,3,e,to=1,0,s,arcCenter=0,0,time=3,pointOfRotation=TWO_DANCERS,rollDir=right,sweepDir=right]
 *  js> p1g = EvalPrim.apply(StandardDancer.COUPLE_1_GIRL, f,
 *    >                      AstNode.valueOf('(Prim -1, 1, left, 3)'))
 *  DancerPath[from=0,1,w,to=-1,0,s,arcCenter=0,0,time=3,pointOfRotation=TWO_DANCERS,rollDir=left,sweepDir=left]
 *  js> f = f.move(StandardDancer.COUPLE_1_BOY, p1b.to).move(StandardDancer.COUPLE_1_GIRL, p1g.to); f.toStringDiagram()
 *  1Gv  1Bv
 * @doc.test Check that sweep direction computation doesn't crash if
 *  a dancer ends up on the centerline:
 *  js> importPackage(net.cscott.sdr.calls)
 *  js> importPackage(net.cscott.sdr.calls.ast)
 *  js> f = Formation.FOUR_SQUARE ; undefined
 *  js> // first part of partner trade
 *  js> p1g = EvalPrim.apply(StandardDancer.COUPLE_1_GIRL, f,
 *    >                      AstNode.valueOf('(Prim -1, 1, left, 3)'))
 *  DancerPath[from=1,-1,n,to=0,0,w,arcCenter=0,0,time=3,pointOfRotation=FOUR_DANCERS,rollDir=left,sweepDir=none]
 * @doc.test Check that in/out motions are computed correctly:
 * @doc.test Check that roll/sweep work even if you turn more than 360-degrees:
 * @doc.test Trailers part of scoot back.
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
	// use the center of the formation as the center point for
        // in/out/etc.  We deal with 'pass in' (which explicitly wants
        // to reference the flagpole center of the set) by
        // re-evaluating to an 8-person formation before the quarter in.
        final Point center = new Point(Fraction.ZERO, Fraction.ZERO);
        // apply 'in/out' modifier to x/y/rotation
        Fraction dx = applyDir(prim.dirX, prim.x, from.x, center.x);
        Fraction dy = applyDir(prim.dirY, prim.y, from.y, center.y);
        Fraction dr = applyDir(prim.dirRot, prim.rot, from, center);
        // add the deltas to create a new Position.
        Position to = new Position
             (from.x.add(dx), from.y.add(dy), from.facing.add(dr));
	to = from.forwardStep(dy).sideStep(dx).turn(dr);
	// XXX THIS COMPUTATION DOESN'T HANDLE 'in' PROPERLY
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
	// XXX: por should be the center of our formation, and mapped back
	//      to a real physical center?
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
        ExactRotation sweepDir;
        if ((from.x.compareTo(center.x)==0 && from.y.compareTo(center.y)==0) ||
            (to.x.compareTo(center.x)==0 && to.y.compareTo(center.y)==0)) {
            // we start or end dead center.  probably this means that our
            // center of rotation is wrong, but it's better just to say
            // "can't sweep".
            sweepDir = ExactRotation.ZERO; // we end up dead center.
        } else {
            ExactRotation fromSweep = ExactRotation.fromXY
                (center.x.subtract(from.x), center.y.subtract(from.y));
            ExactRotation toSweep = ExactRotation.fromXY
                (center.x.subtract(to.x), center.y.subtract(to.y));
            sweepDir = toSweep.subtract(fromSweep.amount);
        }
        if (sweepDir.amount.compareTo(Fraction.ONE_HALF) >= 0)
            sweepDir = sweepDir.subtract(Fraction.ONE);
        if (sweepDir.amount.compareTo(Fraction.ONE_HALF)==0 ||
            sweepDir.amount.negate().compareTo(Fraction.ONE_HALF)==0)
            sweepDir = ExactRotation.ZERO; // XXX: can't tell sweep direction
        
        
        return new DancerPath(from, to, arcCenter, prim.time, por,
			      rollDir, sweepDir);
    }
    
    /** Negate the given fraction if necessary to be consistent with the "in"
     * direction. */
    private static Fraction applyDir(Direction d, Fraction x,
                                     Fraction from, Fraction center) {
        if (d==Direction.ASIS) return x;
        if (from.compareTo(center)==0)
            throw new BadCallException("can't go 'in' if already centered!");
        return (from.compareTo(center) > 0) ? x.negate() : x;
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
