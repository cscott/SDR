package net.cscott.sdr.webapp.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.cscott.sdr.calls.BadCallException;
import net.cscott.sdr.calls.CallDB;
import net.cscott.sdr.calls.DanceProgram;
import net.cscott.sdr.calls.DanceState;
import net.cscott.sdr.calls.Dancer;
import net.cscott.sdr.calls.DancerPath;
import net.cscott.sdr.calls.ExactRotation;
import net.cscott.sdr.calls.Formation;
import net.cscott.sdr.calls.Position;
import net.cscott.sdr.calls.StandardDancer;
import net.cscott.sdr.calls.ast.Seq;
import net.cscott.sdr.calls.transform.Evaluator;
import net.cscott.sdr.util.Fraction;
import net.cscott.sdr.util.Point;
import net.cscott.sdr.util.Bezier.Bezier2D;
import net.cscott.sdr.webapp.client.DanceEngineService;
import net.cscott.sdr.webapp.client.EngineResults;
import net.cscott.sdr.webapp.client.Sequence;
import net.cscott.sdr.webapp.client.Sequence.StartingFormationType;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class DanceEngineServiceImpl extends RemoteServiceServlet
    implements DanceEngineService {

    /** An angle multiplier used when transforming a standard formation
     *  to this dance type. */
    public static Fraction angleMult(StartingFormationType sft) {
        switch(sft) {
        case BIGON: return Fraction.TWO;
        case HEXAGON: return Fraction.TWO_THIRDS;
        case OCTAGON: return Fraction.ONE_HALF;
        default: return Fraction.ONE;
        }
    }
    /** The heading multiplier.  This is probably related to the angle
     *  multiplier, but I can't figure out the exact relation at the moment. */
    public static Fraction headingMult(StartingFormationType sft) {
        //   bend = w*headingMult --> headingMult = bend / w
        switch(sft) {
        // bend -90 deg (-1/4) on "heads press ahead" w/ winding angle -1/4
        case BIGON: return Fraction.ONE;
        // bend +30-deg (1/12) on "heads press ahead" w/ winding angle -1/4
        case HEXAGON: return Fraction.ONE_THIRD.negate();
        // bend +45-deg (1/8) on a "heads press ahead", winding angle is -1/4
        case OCTAGON: return Fraction.ONE_HALF.negate();
        default: return Fraction.ONE;
        }
    }
    /** Expansion factor, to make it look better that we're cramming all these
     *  additional people into the square. */
    public static double expansion(StartingFormationType sft) {
        switch (sft) {
        case HEXAGON: return 1.5;
        case OCTAGON: return 2;
        default: return 1;
        }
    }
    public static boolean useNgon(StartingFormationType sft) {
        return !Fraction.ONE.equals(angleMult(sft));
    }
    public static int numShadows(StartingFormationType sft) {
        switch(sft) {
        case HEXAGON: return 3;
        case OCTAGON: return 2;
        default: return 1;
        }
    }
    public static int dancerNum(StartingFormationType sft, int dn, int shadow) {
        switch (sft) {
        case OCTAGON: return dn + 8*shadow;
        default: return dn + 4*shadow;
        }
    }
    public static Formation baseFormation(StartingFormationType sft) {
        switch (sft) {
        case TWO_COUPLE:
            return Formation.FOUR_SQUARE;
        case BIGON:
        case HEXAGON:
        case OCTAGON:
            // We do the 8-person calls from a squared set and then
            // transform the formation
            // fall through.
        case SQUARED_SET:
            return Formation.SQUARED_SET;
        default:
            throw new Error("Unknown starting formation");
        }
    }
    /** Attempt to dance the given sequence, and return the dancer paths
     *  which result.
     */
    public EngineResults dance(Sequence s, final int sequenceNumber) {
        // okay, first create the starting formation.
        Formation startF = baseFormation(s.startingFormation);
        DanceState ds = new DanceState(new DanceProgram(s.program), startF);
        // now dance each call
        int currentCall=0;
        List<String> messages = new ArrayList<String>
            (Collections.nCopies(s.calls.size(), (String)null));
        List<EngineResults.DancerPath> movements =
            new ArrayList<EngineResults.DancerPath>();
        List<Double> timing = new ArrayList<Double>
            (Collections.nCopies(s.calls.size(), Double.valueOf(0)));
        Map<Dancer,Fraction> winding = new HashMap<Dancer,Fraction>();
        Map<Dancer,Double> correction = new HashMap<Dancer,Double>();
        for (Dancer d : ds.dancers()) {
            // Winding number (modulo 1) always points to the current position
            Position p = startF.location(d);
            Fraction w = ExactRotation.fromXY(p.x, p.y).amount;
            // make winding numbers continuously decreasing as we go around
            // the square CCW from couple #1
            if (w.compareTo(Fraction.FIVE_EIGHTHS) > 0)
                w = w.subtract(Fraction.ONE);
            winding.put(d, w);
            // The original rotation is "facing towards the center" (but
            // quantized to quarters, this also makes the angle exact)
            Fraction origFacing = w.quantize(4).add(Fraction.ONE_HALF);
            // The desired rotation is "towards the center" from the
            // transformed position.
            Fraction desiredFacing = w.quantize(4)
                .multiply(angleMult(s.startingFormation))
                .add(Fraction.ONE_HALF);
            // Now compute an appropriate correction factor so that we start
            // facing the right direction, given the initial winding:
            //  desiredFacing = originalFacing + w * headingMult + correction
            // Use angle circularization so we don't square off desiredFacing
            double cc = circular(desiredFacing) - circular(origFacing) -
                (circular(w) * headingMult(s.startingFormation).doubleValue());
            correction.put(d, cc);
        }
        Fraction totalBeats = Fraction.ZERO;
        try {
            List<String> calls = s.calls;
            if (calls.isEmpty()) calls = Collections.singletonList("nothing");
            for (String call: calls) {
                Seq callAst = new Seq(CallDB.INSTANCE.parse(ds.dance.getProgram(), call));
                Evaluator.breathedEval(ds.currentFormation(), callAst)
                    .evaluateAll(ds);
                List<EngineResults.DancerPath> someMoves =
                    new ArrayList<EngineResults.DancerPath>();
                Fraction duration = ds.currentTime();
                for (Dancer d : ds.dancers()) {
                    Fraction startTime = totalBeats;
                    for (DancerPath dp : ds.movements(d)) {
                        Fraction windingStart = winding.get(d);
                        Fraction windingEnd = updateWinding(windingStart, dp);
                        double c = correction.get(d);
                        someMoves.addAll(convert(d, s.startingFormation, startTime, dp, c, windingStart, windingEnd));
                        startTime = startTime.add(dp.time);
                        winding.put(d, windingEnd);
                    }
                }
                ds = ds.cloneAndClear(ds.currentFormation());
                totalBeats = totalBeats.add(duration);
                // make sure timing and movements don't get set unless all of
                // the above succeeded.
                if (!s.calls.isEmpty())
                    timing.set(currentCall, duration.doubleValue());
                movements.addAll(someMoves);
                currentCall++;
            }
        } catch (BadCallException e) {
            messages.set(currentCall, e.getMessage());
        } catch (Throwable t) {
            messages.set(currentCall, t.toString());
        }
        if (s.calls.isEmpty()) {
            totalBeats = Fraction.ZERO;
            currentCall = 0;
        }
        // construct an EngineResults
        EngineResults results = new EngineResults
            (sequenceNumber, currentCall, messages, movements, timing,
             totalBeats.doubleValue());
        return results;
    }
    /** Convert a {@link DancerPath} to a simplified JavaScript-friendly
     *  version. */
    private static List<EngineResults.DancerPath>
    convert(Dancer d, StartingFormationType sft, Fraction startTime,
            DancerPath dp, double correction,
            Fraction windingStart, Fraction windingEnd) {
        // eventually we'll construct a dancer->dancernum map including
        // phantoms, but XXX we don't support phantoms yet.
        assert d.primitiveTag() != null;
        int dancerNum = ((StandardDancer)d).ordinal();
        if (sft==StartingFormationType.TWO_COUPLE && dancerNum > 1)
            dancerNum -= 2; // two couple we use couple 1 and 3, weird but true
        // for bigons and hexagons, everyone is couple #1 or #2
        if (dancerNum > 3 &&
            (sft==StartingFormationType.BIGON ||
             sft==StartingFormationType.HEXAGON ))
            return Collections.emptyList();
        // ok, now...
        List<EngineResults.DancerPath> result = new ArrayList<EngineResults.DancerPath>(3);
        for (int i=0; i < numShadows(sft); i++) {
            DancerPath dpp = useNgon(sft) ? Ngon(dp, sft, correction, windingStart, windingEnd, i) : dp;
            EngineResults.DancerPath dpath = new EngineResults.DancerPath
                (dancerNum(sft, dancerNum, i),
                 startTime.doubleValue(), dpp.time.doubleValue(),
                 convert(dpp.bezierPath()), convert(dpp.bezierDirection()));
            result.add(dpath);
        }
        return result;
    }
    /** Convert a {@link Bezier2D} to a simplified JavaScript-friendly
     *  version. */
    private static EngineResults.Bezier convert(Bezier2D b) {
        // we only support degree-3 beziers (cubic)
        while (b.degree()<3)
            b = b.raise();
        assert b.degree()==3;
        return new EngineResults.Bezier
            (convert(b.cp(0)), convert(b.cp(1)),
             convert(b.cp(2)), convert(b.cp(3)));
    }
    private static EngineResults.Point convert(Point p) {
        return new EngineResults.Point(p.x.doubleValue(), p.y.doubleValue());
    }

    // ---- n-gon support (bigon, hexagon, octagon)
    private static Fraction updateWinding(Fraction start, DancerPath dp) {
        return updateWinding(start, dp.from, dp.to);
    }
    private static Fraction updateWinding(Fraction start, Position fromP, Position toP) {
        ExactRotation from = ExactRotation.fromXY(fromP.x, fromP.y);
        ExactRotation to = ExactRotation.fromXY(toP.x, toP.y);
        return start.add(from.minSweep(to));
    }
    public static DancerPath Ngon(DancerPath dp, StartingFormationType sft,
                                  double correction,
                                  Fraction windStart, Fraction windEnd,
                                  int shadow) {
        return new DancerPath(Ngon(dp.from, sft, correction, windStart, shadow),
                              Ngon(dp.to, sft, correction, windEnd, shadow),
                              dp.time, dp.pointOfRotation,
                              dp.flags.toArray(new DancerPath.Flag[0]));
    }
    public static Position Ngon(Position p, StartingFormationType sft,
                                double correction, Fraction winding,
                                int shadow) {
        // add whole rotations for shadow dancers
        winding = winding.add(Fraction.valueOf(shadow));
        // invariant: winding must always exactly point to position (modulo 1)
        assert ExactRotation.fromXY(p.x,p.y).equals(new ExactRotation(winding));
        // compute distance
        double x = p.x.doubleValue(), y = p.y.doubleValue();
        double r = Math.hypot(x, y);
        // okay, here's the key to the transformation
        double theta = circular(winding);
        double thetaPrime = theta * angleMult(sft).doubleValue();
        r *= expansion(sft);
        double nx = Math.sin(thetaPrime) * r;
        double ny = Math.cos(thetaPrime) * r;
        // adjust our facing direction based on the accumulated winding angle
        double nr = ((ExactRotation) p.facing).amount.doubleValue() +
            (theta * headingMult(sft).doubleValue() + correction)/ (2*Math.PI);
        return new Position(val(nx), val(ny), new ExactRotation(val(nr)),
                            p.flags.toArray(new Position.Flag[0]));
    }
    /** Take a "squared off" angle and convert to a "circular" angle in
     *  radians. */
    public static double circular(Fraction w) {
        int whole = w.floor();
        ExactRotation er = new ExactRotation(w).normalize();
        Fraction x = er.toX(), y = er.toY();
        double theta = Math.atan2(x.doubleValue(), y.doubleValue());
        if (theta<0) theta += 2*Math.PI; // [0, 2*PI)
        // add back in the whole part
        return whole*2*Math.PI + theta;
    }
    public static Fraction val(double v) {
        // quantize to 32nds to avoid overflow.
        return Fraction.valueOf(v).quantize(64);
    }
}
