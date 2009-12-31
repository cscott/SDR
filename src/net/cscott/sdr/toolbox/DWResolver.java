package net.cscott.sdr.toolbox;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.runner.RunWith;

import net.cscott.jdoctest.JDoctestRunner;
import net.cscott.sdr.calls.Dancer;
import net.cscott.sdr.calls.Formation;
import net.cscott.sdr.calls.FormationMatch;
import net.cscott.sdr.calls.NoMatchException;
import net.cscott.sdr.calls.MatcherList;
import net.cscott.sdr.calls.StandardDancer;
import net.cscott.sdr.calls.TaggedFormation;
import net.cscott.sdr.calls.TaggedFormation.Tag;
import static net.cscott.sdr.util.Tools.*;

/**
 * Resolve a square using a modified version of Dave Wilson's
 * <a href="http://www.tiac.net/~mabaker/ocean-wave-resolution.html">ocean
 * wave resolution technique</a>.
 * @author C. Scott Ananian
 * @doc.test Show that this resolves from all RH ocean waves, and compute
 *  statistics.
 *  js> importPackage(net.cscott.sdr.calls);
 *  js> // create all possible RH ocean waves (modulo rotation)
 *  js> FormationList = FormationList.js(this); undefined;
 *  js> const SD = StandardDancer;
 *  js> f = FormationList.PARALLEL_RH_WAVES; undefined
 *  js> f = f.mapStd([SD.COUPLE_2_BOY, SD.COUPLE_2_GIRL,
 *    >               SD.COUPLE_1_GIRL, SD.COUPLE_1_BOY]
 *    >              ); f.toStringDiagram()
 *  2B^  2Gv  1G^  1Bv
 *  
 *  3B^  3Gv  4G^  4Bv
 *  js> fs = [pp.permute(f) for each (pp in Iterator
 *    >       (Permutation.generate(Permutation.IDENTITY8)))]; fs.length
 *  96
 *  js> dss = [new DanceState(new DanceProgram(Program.PLUS), f)
 *    >        for each (f in fs)]; undefined;
 *  js> // look at first step from each formation
 *  js> s = [DWResolver.resolveStep(ff) for each (ff in fs)]; s.slice(0,5)
 *  Trade,Hinge,Hinge,Acey Deucey,Half Tag
 *  js> // compute number of calls until resolve for each starting formation
 *  js> // we could memoize intermediate results, but we're lazy.
 *  js> function dance(ds, steps, verbose) {
 *    >   if (steps > 30) throw new Error("Doesn't resolve!");
 *    >   // get next call from current formation
 *    >   let nextCall = DWResolver.resolveStep(ds.currentFormation());
 *    >   if (verbose) print(nextCall);
 *    >   if (nextCall == "Right and Left Grand") return (steps+1);
 *    >   net.cscott.sdr.calls.Evaluator.parseAndEval(ds, nextCall);
 *    >   // keep dancing.
 *    >   return dance(ds, steps+1, verbose);
 *    > }
 *  js> nums = [dance(ds, 0) for each (ds in dss)]; nums.slice(0,5)
 *  2,2,5,4,5
 *  js> // the minimum ought to be one: from some formation, a RLG is possible
 *  js> Math.min.apply(null, nums)
 *  1
 *  js> // and the largest number of calls to resolve is:
 *  js> max = Math.max.apply(null, nums)
 *  8
 *  js> // average:
 *  js> let [sum,count] = [0,0]
 *  js> nums.map(function(e) { sum+=e; count+=1; }); (sum/count).toFixed(2);
 *  4.92
 *  js> // formations which require the maximum number of calls:
 *  js> // (the first here is a 'sides lead right, swing thru 1 1/4,
 *  js> //  spin chain and exchange the gears')
 *  js> for (let i=0; i<nums.length; i++)
 *    >   if (nums[i]==max) print(i+":\n"+fs[i].toStringDiagram()+"\n");
 *  30:
 *  2B^  2Gv  1B^  3Gv
 *  
 *  1G^  3Bv  4G^  4Bv
 *  
 *  45:
 *  3B^  2Bv  3G^  4Gv
 *  
 *  2G^  1Gv  4B^  1Bv
 *  
 *  80:
 *  3G^  2Bv  2G^  3Bv
 *  
 *  1B^  4Gv  4B^  1Gv
 *  
 *  95:
 *  3G^  2Gv  2B^  3Bv
 *  
 *  1B^  4Bv  4G^  1Gv
 *  js> // emit one of the 'longest resolves'
 *  js> ds = new DanceState(new DanceProgram(Program.PLUS), fs[30]); undefined;
 *  js> dance(ds, 0, true); undefined;
 *  Hinge
 *  Swing Thru
 *  Swing Thru
 *  Acey Deucey
 *  Trade
 *  Swing Thru
 *  Swing Thru
 *  Right and Left Grand
 */
@RunWith(value=JDoctestRunner.class)
public class DWResolver {
    private DWResolver() { }

    /** Return the next call needed to resolve the given formation. */
    public static String resolveStep(Formation f) {
        // step 1: are we in RH waves?  if no, half tag
        FormationMatch fm;
        TaggedFormation parallelWaves;
        try {
            // we want parallel waves
            fm = MatcherList.PARALLEL_RH_WAVES.match(f);
            assert fm.matches.values().size() == 1;
            parallelWaves = fm.matches.values().iterator().next();
            // ok, we've got them, match each wave separately
            fm = MatcherList.RH_OCEAN_WAVE.match(f);
        } catch (NoMatchException e) {
            // XXX: half tag only if we're in GENERAL LINEs
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
        // step 2: boys together or girls together? if no, hinge
        //         (this step can be skipped if you're learning the technique;
        //          one or two half tags will also get us to
        //          same-genders-in-center RH waves -- but it shortens the
        //          resolve by a call when recognized.)
        if (wave0.get(0).isBoy() != wave0.get(1).isBoy() &&
            wave0.get(0).isBoy() != wave0.get(3).isBoy())
            return "Hinge";

        // step 3: boys or girls together in center? if no, half tag
        // XXX: flow?
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
        // step 5: both couples in same wave? if no...
        if (couplesInWave0.size()==3) {
            // one of the matched dancers at end facing out? if no swing thru
            StandardDancer end = wave0.get(0);
            if (!parallelWaves.isTagged(end, Tag.LEADER))
                end = wave0.get(3);
            assert parallelWaves.isTagged(end, Tag.LEADER);
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
        if (parallelWaves.isTagged(wave0.get(1), Tag.LEADER)) {
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
