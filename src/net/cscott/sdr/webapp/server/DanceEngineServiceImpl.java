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
        switch(sft) {
        case BIGON: return Fraction.TWO;
        case HEXAGON: return Fraction.TWO_THIRDS.negate();
        case OCTAGON: return Fraction.mONE;
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
        for (Dancer d : ds.dancers()) {
            Fraction f = headingMult(s.startingFormation);
            Fraction w = updateWinding(Fraction.ZERO,
                                       Position.getGrid(0, -3, "n"),
                                       startF.location(d));
            // we look for x, such that
            //  -w + fx = -fw
            // where -w is the original rotation for the dancer, and
            // -fw is the desired rotated direction, and fx (for the initial
            // x we compute here) is what the transformation will add.
            Fraction x = (f.negate().subtract(Fraction.ONE)).divide(f).multiply(w);
            winding.put(d, x);
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
                        someMoves.addAll(convert(d, s.startingFormation, startTime, dp, windingStart, windingEnd));
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
            DancerPath dp, Fraction windingStart, Fraction windingEnd) {
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
            DancerPath dpp = useNgon(sft) ? Ngon(dp, sft, windingStart, windingEnd, i) : dp;
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
                                   Fraction windStart, Fraction windEnd,
                                   int shadow) {
        return new DancerPath(Ngon(dp.from, sft, windStart, shadow),
                              Ngon(dp.to, sft, windEnd, shadow),
                              dp.time, dp.pointOfRotation,
                              dp.flags.toArray(new DancerPath.Flag[0]));
    }
    public static Position Ngon(Position p, StartingFormationType sft, Fraction winding, int shadow) {
        winding = winding.add(Fraction.valueOf(shadow));
        double x = p.x.doubleValue(), y = p.y.doubleValue();
        double theta = Math.atan2(-x, -y); // zero is towards #1 couple
        if (theta<0) theta+=2*Math.PI; // theta in [0,2*PI)
        double r = Math.hypot(x, y);
        // okay, here's the key to the transformation
        theta += 2*Math.PI*floor(winding);
        System.err.println(p+" winding "+winding+" theta "+theta);
        theta *= angleMult(sft).doubleValue();
        r *= expansion(sft);
        double nx = -Math.sin(theta) * r;
        double ny = -Math.cos(theta) * r;
        // according to Justin, we adjust our facing direction by factor*
        // the winding angle
        ExactRotation nr = ((ExactRotation) p.facing)
            .add(winding.multiply(headingMult(sft)));
        return new Position(val(nx), val(ny), nr,
                            p.flags.toArray(new Position.Flag[0]));
    }
    public static int floor(Fraction f) {
        int n = f.getNumerator(), d = f.getDenominator();
        if (n<0) n-=(d-1);
        return n/d;
    }
    public static Fraction val(double v) {
        Fraction f = Fraction.valueOf(v);
        // quantize to 32nds to avoid overflow.
        return Fraction.valueOf(floor(f.multiply(Fraction.valueOf(32).add(Fraction.ONE_HALF))), 32);
    }
}
