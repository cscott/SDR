package net.cscott.sdr.webapp.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.cscott.sdr.calls.BadCallException;
import net.cscott.sdr.calls.Breather;
import net.cscott.sdr.calls.CallDB;
import net.cscott.sdr.calls.DanceProgram;
import net.cscott.sdr.calls.DanceState;
import net.cscott.sdr.calls.Dancer;
import net.cscott.sdr.calls.DancerPath;
import net.cscott.sdr.calls.Formation;
import net.cscott.sdr.calls.StandardDancer;
import net.cscott.sdr.calls.ast.Seq;
import net.cscott.sdr.calls.transform.Evaluator;
import net.cscott.sdr.util.Fraction;
import net.cscott.sdr.util.Point;
import net.cscott.sdr.util.Bezier.Bezier2D;
import net.cscott.sdr.webapp.client.DanceEngineService;
import net.cscott.sdr.webapp.client.EngineResults;
import net.cscott.sdr.webapp.client.Sequence;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class DanceEngineServiceImpl extends RemoteServiceServlet
    implements DanceEngineService {

    /** Attempt to dance the given sequence, and return the dancer paths
     *  which result.
     */
    public EngineResults dance(Sequence s, final int sequenceNumber) {
        // okay, first create the starting formation.
        Formation startF;
        switch (s.startingFormation) {
        case TWO_COUPLE:
            startF = Formation.FOUR_SQUARE;
            break;
        case BIGON:
        case HEXAGON:
        case OCTAGON:
            // xxx: not really supported yet.  But we'd be doing the 8-person
            //      calls from a squared set and then transforming the formation
            // fall through.
        case SQUARED_SET:
            startF = Formation.SQUARED_SET;
            break;
        default:
            throw new Error("Unknown starting formation");
        }
        DanceState ds = new DanceState(new DanceProgram(s.program), startF);
        // now dance each call
        int currentCall=0;
        List<String> messages = new ArrayList<String>
            (Collections.nCopies(s.calls.size(), (String)null));
        List<EngineResults.DancerPath> movements =
            new ArrayList<EngineResults.DancerPath>();
        List<Double> timing = new ArrayList<Double>
            (Collections.nCopies(s.calls.size(), Double.valueOf(0)));
        Fraction totalBeats = Fraction.ZERO;
        try {
            for (String call: s.calls) {
                Seq callAst = new Seq(CallDB.INSTANCE.parse(ds.dance.getProgram(), call));
                new Evaluator.Standard(callAst).evaluateAll(ds);
                List<EngineResults.DancerPath> someMoves =
                    new ArrayList<EngineResults.DancerPath>();
                Fraction duration = ds.currentTime();
                for (Dancer d : ds.dancers()) {
                    Fraction startTime = totalBeats;
                    for (DancerPath dp : ds.movements(d)) {
                        someMoves.add(convert(d, startTime, dp));
                        startTime = startTime.add(dp.time);
                    }
                }
                // breathe (XXX: should be breathing the DanceState)
                Formation f = Breather.breathe(ds.currentFormation());
                ds = ds.cloneAndClear(f);
                totalBeats = totalBeats.add(duration);
                // make sure timing and movements don't get set unless all of
                // the above succeeded.
                timing.set(currentCall, duration.doubleValue());
                movements.addAll(someMoves);
                currentCall++;
            }
        } catch (BadCallException e) {
            messages.set(currentCall, e.getMessage());
        } catch (Throwable t) {
            messages.set(currentCall, t.toString());
        }
        // construct an EngineResults
        EngineResults results = new EngineResults
            (sequenceNumber, currentCall, messages, movements, timing,
             totalBeats.doubleValue());
        return results;
    }
    /** Convert a {@link DancerPath} to a simplified JavaScript-friendly
     *  version. */
    private static EngineResults.DancerPath convert(Dancer d, Fraction startTime, DancerPath dp) {
        // eventually we'll construct a dancer->dancernum map including
        // phantoms, but XXX we don't support phantoms yet.
        assert d.primitiveTag() != null;
        int dancerNum = ((StandardDancer)d).ordinal();
        // ok, now...
        return new EngineResults.DancerPath
            (dancerNum, startTime.doubleValue(), dp.time.doubleValue(),
             convert(dp.bezierPath()), convert(dp.bezierDirection()));
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
}
