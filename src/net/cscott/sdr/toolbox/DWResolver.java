package net.cscott.sdr.toolbox;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.cscott.sdr.calls.Dancer;
import net.cscott.sdr.calls.Formation;
import net.cscott.sdr.calls.FormationMatch;
import net.cscott.sdr.calls.NoMatchException;
import net.cscott.sdr.calls.SelectorList;
import net.cscott.sdr.calls.StandardDancer;
import net.cscott.sdr.calls.TaggedFormation;
import net.cscott.sdr.calls.TaggedFormation.Tag;
import static net.cscott.sdr.util.Tools.*;

/** Resolve a square using a modified version of Dave Wilson's ocean wave
 *  resolution technique.
 * @author C. Scott Ananian
 */
public class DWResolver {
    private DWResolver() { }

    public static String resolveStep(Formation f) {
        // step 1: are we in RH waves?  if no, half tag
        FormationMatch fm;
        try {
            fm = SelectorList.PARALLEL_RH_WAVES.match(f);
        } catch (NoMatchException e) {
            return "Half Tag";
        }
        List<TaggedFormation> waves = new ArrayList<TaggedFormation>
            (fm.matches.values());
        assert waves.size() == 2;
        List<StandardDancer> wave0 = foreach(waves.get(0).sortedDancers(),
                standardDancerFilter);
        List<StandardDancer> wave1 = foreach(waves.get(1).sortedDancers(),
                standardDancerFilter);
        assert wave0.size()==4 && wave1.size()==4;
        // step 2: boys together or girls together? if no, centers trade
        if (wave0.get(0).isBoy() != wave0.get(1).isBoy() &&
            wave0.get(0).isBoy() != wave0.get(3).isBoy())
            return "Centers Trade";
        // step 3: boys or girls together in center? if no, half tag
        if (wave0.get(1).isBoy() != wave0.get(2).isBoy())
            return "Half Tag";
        // at this point: RH BGGB or GBBG waves
        assert wave0.get(0).isBoy() == wave0.get(3).isBoy();
        assert wave0.get(1).isBoy() == wave0.get(2).isBoy();
        assert wave0.get(0).isBoy() != wave0.get(1).isBoy();
        // step 4: at least one couple in same wave? if no, acey deucey
        Set<Integer> couplesInWave0 = new HashSet<Integer>
            (foreach(wave0, coupleNumberFilter));
        if (couplesInWave0.size()==4) // all dancers are different
            return "Acey Deucey";
        // step 5: both couples in same way? if no...
        if (couplesInWave0.size()==3) {
            // one of the matched dancers at end facing out? if no swing thru
            StandardDancer end = wave0.get(0);
            if (!waves.get(0).isTagged(end, Tag.LEADER))
                end = wave0.get(3);
            assert waves.get(0).isTagged(end, Tag.LEADER);
            if (end.coupleNumber() != wave0.get(1).coupleNumber() &&
                end.coupleNumber() != wave0.get(2).coupleNumber())
                return "Swing Thru";
            // otherwise, acey deucey
            return "Acey Deucey";
        }
        assert couplesInWave0.size()==2; // matched couples!
        // step 6: holding partner, if no swing thru
        if (wave0.get(0).coupleNumber() != wave0.get(1).coupleNumber())
            return "Swing Thru";
        // step 7: boys in the center? if not, trade
        if (wave0.get(0).isBoy())
            return "Trade";
        // step 8: in sequence? if not, swing thru (twice)
        StandardDancer leaderBoy, trailerBoy;
        if (waves.get(0).isTagged(wave0.get(1), Tag.LEADER)) {
            leaderBoy = wave0.get(1);
            trailerBoy = wave0.get(2);
        } else {
            leaderBoy = wave0.get(2);
            trailerBoy = wave0.get(1);
        }
        // possibilities:
        //   lead | trail | in seq?
        //    1       2        y
        //    1       4        n
        //    2       1        n
        //    2       3        y
        //    3       2        n
        //    3       4        y
        //    4       1        y
        //    4       3        n
        int l = leaderBoy.coupleNumber() - 1;
        int t = trailerBoy.coupleNumber() - 1;
        if (( (l+1) % 4) != t)
            return "Swing Thru";
        return "Right and Left Grand";
    }
    private static F<Dancer,StandardDancer> standardDancerFilter =
        new F<Dancer,StandardDancer>() {
        public StandardDancer map(Dancer d) { return (StandardDancer) d; }
    };
    private static F<StandardDancer,Integer> coupleNumberFilter =
        new F<StandardDancer,Integer>() {
        public Integer map(StandardDancer d) { return d.coupleNumber(); }
    };
}
