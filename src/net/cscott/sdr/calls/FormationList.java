package net.cscott.sdr.calls;

import static net.cscott.sdr.calls.TaggedFormation.Tag.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.cscott.jutil.Factories;
import net.cscott.jutil.GenericMultiMap;
import net.cscott.jutil.MultiMap;
import net.cscott.sdr.calls.TaggedFormation.Tag;
import net.cscott.sdr.calls.TaggedFormation.TaggedDancerInfo;
import net.cscott.sdr.util.Fraction;

/** A list of common formations, specified with phantoms.
 */
// can use xxxx.yyyy() to associate phantoms with real dancers.
public abstract class FormationList {


    // from http://www.penrod-sq-dancing.com/form2.html
    // see also http://www.penrod-sq-dancing.com/fars0.html
    // 'create' should really be the constructor for a formation subclass.
    // things like 'three and one' lines really want another programming
    // "check formation" method which can be overridden??
/*
    public static final Formation SQUARE =
	create(" ss ","e  w","e  w"," nn ");
    public static final Formation BACK_TO_BACK_LINES =
	create("nnnn","ssss");
    public static final Formation RIGHT_HAND_COLUMN =
	create("ns","ns","ns","ns");
    public static final Formation LEFT_HAND_COLUMN =
	create("sn","sn","sn","sn");
    public static final Formation COMPLETED_DOUBLE_PASS_THRU =
	create("nn","nn","ss","ss");
    public static final Formation DIAMONDS = // only one version of these
	create(// centers
	       d(0,+3,"e", CENTER, BEAU),
	       d(0,+1,"w", CENTER, VERY_CENTER, BEAU),
	       d(0,-1,"e", CENTER, VERY_CENTER, BEAU),
	       d(0,-3,"w", CENTER, BEAU),
	       // points
	       d(-3,+2,"n", POINT, END),
	       d(-3,-2,"n", POINT, END),
	       d(+3,+2,"s", POINT, END),
	       d(+3,-2,"s", POINT, END));
    public static final Formation GENERAL_DIAMONDS = // too permissive?
	create(// centers
	       d(0,+3,"-", CENTER),
	       d(0,+1,"-", CENTER, VERY_CENTER),
	       d(0,-1,"-", CENTER, VERY_CENTER),
	       d(0,-3,"-", CENTER),
	       // points
	       d(-3,+2,"|", END),
	       d(-3,-2,"|", END),
	       d(+3,+2,"|", END),
	       d(+3,-2,"|", END));
    public static final Formation DOUBLE_PASS_THRU =
	create("ss","ss","nn","nn");
    public static final Formation GENERAL_TAG =
	create(" - ","---","---"," - ");
    public static final Formation QUARTER_TAG =
	create(" e ","eww","eew"," w ");
    public static final Formation PARALLEL_WAVES = // also "half tag"
	create("ee","ww","ee","ww");
    public static final Formation THREE_QUARTER_TAG =
	create(" e ","wwe","wee"," w ");
    public static final Formation EIGHT_CHAIN_THRU =
	create("ss","nn","ss","nn");
    public static final Formation THAR =
	create(d(0,-1,"w", BEAU, CENTER),
	       d(0,-3,"e", BEAU, END, OUTSIDE4),
	       d(+1,0,"s", BEAU, CENTER),
	       d(+3,0,"n", BEAU, END, OUTSIDE4),
	       d(0,+1,"e", BEAU, CENTER),
	       d(0,+3,"w", BEAU, END, OUTSIDE4),
	       d(-1,0,"n", BEAU, CENTER),
	       d(-3,0,"s", BEAU, END, OUTSIDE4));
    public static final Formation WRONG_WAY_THAR =
	create(d(0,-1,"e", BEAU, CENTER),
	       d(0,-3,"w", BEAU, END, OUTSIDE4),
	       d(+1,0,"n", BEAU, CENTER),
	       d(+3,0,"s", BEAU, END, OUTSIDE4),
	       d(0,+1,"w", BEAU, CENTER),
	       d(0,+3,"e", BEAU, END, OUTSIDE4),
	       d(-1,0,"s", BEAU, CENTER),
	       d(-3,0,"n", BEAU, END, OUTSIDE4));
    public static final Formation TIDAL_WAVE =
	create("e","w","e","w","e","w","e","w");
    public static final Formation TIDAL_TWO_FACED_LINE =
	create("e","e","w","w","e","e","w","w");
    public static final Formation Z_FORMATION =
	create("s ","sn","sn","sn"," n");
    public static final Formation TRADE_BY =
	create("nn","ss","nn","ss");
    public static final Formation PROMENADE =
	create(d(0,-1,"e", BEAU),
	       d(0,-3,"e", BELLE),
	       d(+1,0,"n", BEAU),
	       d(+3,0,"n", BELLE),
	       d(0,+1,"w", BEAU),
	       d(0,+3,"w", BELLE),
	       d(-1,0,"s", BEAU),
	       d(-3,0,"s", BELLE));
    public static final Formation WRONG_WAY_PROMENADE =
	create(d(0,-1,"w",BELLE),
	       d(0,-3,"w",BEAU),
	       d(+1,0,"s",BELLE),
	       d(+3,0,"s",BEAU),
	       d(0,+1,"e",BELLE),
	       d(0,+3,"e",BEAU),
	       d(-1,0,"n",BELLE),
	       d(-3,0,"n",BEAU));
*/
    // labelled calls named as per the "Callerlab Approved Formations"
    // from April 1980
    
    // 2-person formations
    public static final TaggedFormation COUPLE = // callerlab #1
        create("COUPLE",
                d(-1,0,"n",BEAU),
                d(+1,0,"n",BELLE));
    public static final TaggedFormation FACING_DANCERS = // callerlab #2
        create("FACING DANCERS",
                d(0,+1,"s",TRAILER),
                d(0,-1,"n",TRAILER));
    public static final TaggedFormation BACK_TO_BACK_DANCERS = // callerlab #3
        create("BACK TO BACK DANCERS",
                d(0,+1,"n",LEADER),
                d(0,-1,"s",LEADER));
    public static final TaggedFormation TANDEM =
        create("TANDEM",
               d(0,+1,"n",LEADER),
               d(0,-1,"n",TRAILER));
    public static final TaggedFormation RH_MINIWAVE = // callerlab #4
        create("RH MINIWAVE",
               d(-1,0,"n",BEAU),
               d(+1,0,"s",BEAU));
    public static final TaggedFormation LH_MINIWAVE = // callerlab #5
        create("LH MINIWAVE",
               d(-1,0,"s",BELLE),
               d(+1,0,"n",BELLE));
    // 4-person formations
    public static final Formation GENERAL_LINE =
        create("GENERAL LINE", f("||||"), t(0, END), t(1,CENTER), t(2,CENTER), t(3,END));
    public static final TaggedFormation FACING_COUPLES = // callerlab #6
        xofy("FACING COUPLES", FACING_DANCERS, COUPLE);
    public static final TaggedFormation BACK_TO_BACK_COUPLES = // callerlab #7
        xofy("BACK TO BACK COUPLES", BACK_TO_BACK_DANCERS, COUPLE);
    public static final TaggedFormation RH_OCEAN_WAVE = // callerlab #8
        xofy("RH OCEAN WAVE", COUPLE, RH_MINIWAVE); // XXX label centers/end
    public static final TaggedFormation LH_OCEAN_WAVE = // callerlab #9
        xofy("LH OCEAN WAVE", COUPLE, LH_MINIWAVE);
    public static final TaggedFormation RH_BOX = // callerlab #10
        xofy("RH BOX", RH_MINIWAVE, TANDEM);
    public static final TaggedFormation LH_BOX = // callerlab #11
        xofy("LH BOX", LH_MINIWAVE, TANDEM);
    public static final TaggedFormation RH_TWO_FACED_LINE = // callerlab #12
        xofy("RH TWO-FACED LINE", RH_MINIWAVE, COUPLE);
    public static final TaggedFormation LH_TWO_FACED_LINE = // callerlab #13
        xofy("LH_TWO_FACED_LINE", LH_MINIWAVE, COUPLE);
    public static final TaggedFormation LH_SINGLE_PROMENADE =
        create("LH SINGLE PROMENADE",
                d( 0, 1, "w"),
                d(-1, 0, "s"),
                d( 0,-1, "e"),
                d( 1, 0, "n")); // this is a star: is that correct?
    public static final TaggedFormation RH_SINGLE_PROMENADE =
        create("RH SINGLE PROMENADE",
                d( 0, 1, "e"),
                d(-1, 0, "n"),
                d( 0,-1, "w"),
                d( 1, 0, "s")); // this is a star: is that correct?
    public static final TaggedFormation PROMENADE = // callerlab #18
        xofy("PROMENADE", LH_SINGLE_PROMENADE, COUPLE);
    public static final TaggedFormation WRONG_WAY_PROMENADE =
        xofy("WRONG WAY PROMENADE", RH_SINGLE_PROMENADE, COUPLE);
    public static final TaggedFormation THAR =
        xofy("THAR", LH_SINGLE_PROMENADE, LH_MINIWAVE); // XXX CHECK ME
    public static final TaggedFormation WRONG_WAY_THAR =
        xofy("WRONG WAY THAR", LH_SINGLE_PROMENADE, RH_MINIWAVE); // XXX CHECK ME
    public static final TaggedFormation FACING_LINES = // callerlab #22
        xofy("FACING LINES", FACING_COUPLES, COUPLE);
    

    public static final TaggedFormation RH_DIAMOND =
        create("RH DIAMOND",
               d( 0, 3,"e",POINT),
               d(-1, 0,"n",BEAU,CENTER),
               d(+1, 0,"s",BEAU,CENTER),
               d( 0,-3,"w",POINT));
    public static final TaggedFormation LH_DIAMOND =
        create("LH DIAMOND",
               d( 0, 3,"w",POINT),
               d(-1, 0,"s",BELLE,CENTER),
               d(+1, 0,"n",BELLE,CENTER),
               d( 0,-3,"e",POINT));

    private static class PositionAndTag {
        public final Position position;
        public final Set<Tag> tags;
        PositionAndTag(Position position, Set<Tag> tags) {
            this.position = position; this.tags = tags;
        }
    }
    private static PositionAndTag d(int x, int y, String facing,
                                    Tag... tags) {
        return new PositionAndTag(Position.getGrid(x,y,facing),
                TaggedFormation.mkTags(tags));
    }
    private static TaggedFormation create(final String name, PositionAndTag... ptl) {
	List<TaggedDancerInfo> dil = new ArrayList<TaggedDancerInfo>(ptl.length);
	for (PositionAndTag pt: ptl)
	    dil.add(new TaggedDancerInfo(new PhantomDancer(), pt.position, pt.tags, true));
	TaggedFormation f = new TaggedFormation(dil.toArray(new TaggedDancerInfo[dil.size()])) {
	    public String toString() { return (name==null)?super.toString():name; }
        };
	return f;
    }
    // first string is 'top' of diagram (closest to caller)
    // dancers are numbered left to right, top to bottom. (reading order)
    private static Formation create(String name, String[] sa, NumAndTags... tags) {
        Map<Dancer,Position> m = new LinkedHashMap<Dancer,Position>();
	// check validity
	assert sa.length>0;
	for (int i=0; i<sa.length-1; i++)
	    assert sa[i].length()==sa[i+1].length();
	// okay, create formation w/ phantoms.
	for (int y=0; y<sa.length; y++)
	    L1: for (int x=0; x<sa[y].length(); x++) {
		if (sa[y].charAt(x)==' ') continue;
		Rotation r = 
		    Rotation.fromAbsoluteString(sa[y].substring(x,x+1));
		m.put(new PhantomDancer(), 
		        new Position(Fraction.valueOf(x,1),
		                Fraction.valueOf(y,1).negate(),
		                r));
	    }
	Formation f = new Formation(m);
        return addTags(name, f, tags);
    }
    // helper
    private static String[] f(String... sa) { return sa; }

    /** Formation composition: create a formation with an X of Ys. */
    private static Formation _xofy(Formation x, Formation y) {
        /* create an instance of 'y' for each dancer in 'x'. */
        Map<Dancer,Formation> sub = new LinkedHashMap<Dancer,Formation>();
        for (Dancer d: x.dancers()) {
            Map<Dancer,Position> m = new LinkedHashMap<Dancer,Position>();
            for (Dancer dd : y.dancers())
                m.put(new PhantomDancer(), y.location(dd));
            sub.put(d, new Formation(m));
        }
        return FormationMapper.insert(x,sub);
    }
    /** Helper function for the above to add dancer tags. The dancers
     * are numbered left to right, top to bottom.  A null indicates
     * "no additional tags".
     */
    private static TaggedFormation xofy(final String name, Formation x, Formation y, NumAndTags... tags){
        return addTags(name, _xofy(x,y), tags);
    }
    private static TaggedFormation addTags(final String name, final Formation f, NumAndTags... tags) {
        List<Dancer> dancers = new ArrayList<Dancer>(f.dancers());
        // sort them left to right, top to bottom.
        Collections.sort(dancers, new Comparator<Dancer>() {
            public int compare(Dancer d1, Dancer d2) {
                Position p1 = f.location(d1), p2 = f.location(d2);
                // first, top to bottom.
                int c = -p1.y.compareTo(p2.y);
                if (c!=0) return c;
                // then left to right
                return p1.x.compareTo(p2.x);
            }
        });
        MultiMap<Dancer,Tag> tm = new GenericMultiMap<Dancer,Tag>
            (Factories.enumSetFactory(Tag.class));
        // add explicitly specified tags.
        for (NumAndTags nt : tags)
            tm.addAll(dancers.get(nt.dancerNum), nt.tags);
        // add implicit/automatic tags
        //Tagger.addAutomatic(f, tm);
        return new TaggedFormation(f, tm) {
            @Override
            public String toString() { return true?super.toString():name; }
        };
    }
    private static class NumAndTags {
        public final int dancerNum;
        public final Set<Tag> tags;
        NumAndTags(int dancerNum, Set<Tag> tags)
        { this.dancerNum=dancerNum; this.tags=tags; }
    }
    // helper function, for brevity
    private static NumAndTags t(int dancerNum, Tag...  tags) {
        EnumSet<Tag> t = (tags.length==0) ? EnumSet.noneOf(Tag.class) :
            EnumSet.of(tags[0], tags);
        return new NumAndTags(dancerNum, t);
    }
    public static void main(String[] args) {
        System.out.println(xofy("test formation", FACING_DANCERS, COUPLE));
    }
}
