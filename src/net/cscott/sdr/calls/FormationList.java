package net.cscott.sdr.calls;

import net.cscott.sdr.calls.Formation.DancerInfo;
import net.cscott.sdr.calls.TaggedFormation.TaggedDancerInfo;
import net.cscott.sdr.calls.TaggedFormation.Tag;
import static net.cscott.sdr.calls.TaggedFormation.Tag.*;
import net.cscott.sdr.util.Fraction;

import java.util.*;

/** A list of common formations, specified with phantoms.
 */
// can use xxxx.yyyy() to associate phantoms with real dancers.
public abstract class FormationList {
    /** Our private mutable list of formations. */
    private static final List<Formation> _formations =
	new ArrayList<Formation>();
    /** An immutable list of all the formations declared in this
     *  <code>FormationList</code> class. */
    public static final List<Formation> all =
	Collections.unmodifiableList(_formations);

    // from http://www.penrod-sq-dancing.com/form2.html
    // see also http://www.penrod-sq-dancing.com/fars0.html
    // 'create' should really be the constructor for a formation subclass.
    // things like 'three and one' lines really want another programming
    // "check formation" method which can be overridden??
/*
    public static final Formation SQUARE =
	create(" ss ","e  w","e  w"," nn ");
    public static final Formation FACING_LINES =
	create("ssss","nnnn");
    public static final Formation BACK_TO_BACK_LINES =
	create("nnnn","ssss");
    public static final Formation TWO_FACED_LINES =
	create("ssnn","ssnn"); // also needs a mirror
    public static final Formation GENERAL_LINES =
	create("||||","||||");
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
    // 4-person formations
    public static final TaggedFormation FACING_COUPLES =
        create("FACING COUPLES",
               d(-1,-1,"n",BEAU,TRAILER),
               d(+1,-1,"n",BELLE,TRAILER),
               d(+1,+1,"s",BEAU,TRAILER),
               d(-1,+1,"s",BELLE,TRAILER));
    // 2-person formations
    public static final TaggedFormation COUPLE =
        create("COUPLE",
               d(-1,0,"n",BEAU),
               d(+1,0,"n",BELLE));
    public static final TaggedFormation RH_MINIWAVE =
        create("RH MINIWAVE",
               d(-1,0,"n",BEAU),
               d(+1,0,"s",BEAU));
    public static final TaggedFormation LH_MINIWAVE =
        create("LH MINIWAVE",
               d(-1,0,"s",BELLE),
               d(+1,0,"n",BELLE));
    public static final TaggedFormation FACING_DANCERS =
        create("FACING DANCERS",
               d(0,-1,"n",TRAILER),
               d(0,+1,"s",TRAILER));
    public static final TaggedFormation TANDEM =
        create("TANDEM",
               d(0,-1,"n",TRAILER),
               d(0,+1,"n",LEADER));
    public static final TaggedFormation RH_WAVE =
        create("RH WAVE",
               d(-3,0,"n",BEAU,END),
               d(-1,0,"s",BEAU,CENTER),
               d(+1,0,"n",BEAU,CENTER),
               d(+3,0,"s",BEAU,END));
    public static final TaggedFormation LH_WAVE =
        create("LH WAVE",
               d(-3,0,"s",BELLE,END),
               d(-1,0,"n",BELLE,CENTER),
               d(+1,0,"s",BELLE,CENTER),
               d(+3,0,"n",BELLE,END));
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
	_formations.add(f);// add to our static list.
	return f;
    }
    // first string is 'top' of diagram (closest to caller)
    @Deprecated // doesn't include tag information
    private static Formation create(String... sa) {
	List<PositionAndTag> ptl = new ArrayList<PositionAndTag>(8);
	// check validity
	assert sa.length>0;
	for (int i=0; i<sa.length-1; i++)
	    assert sa[i].length()==sa[i+1].length();
	// okay, create formation w/ phantoms.
	Fraction yoff = Fraction.valueOf(1-sa.length,2);
	Fraction xoff = Fraction.valueOf(1-sa[0].length(),2);
	for (int y=0; y<sa.length; y++)
	    L1: for (int x=0; x<sa[y].length(); x++) {
		if (sa[y].charAt(x)==' ') continue;
		ExactRotation r = (sa[y].charAt(x)=='o') ? null:
		    ExactRotation.fromAbsoluteString(sa[y].substring(x,x+1));
		ptl.add(new PositionAndTag(new Position(Fraction.valueOf(x,1).add(xoff),
				    Fraction.valueOf(y,1).add(yoff).negate(),
				    r),Collections.<Tag>emptySet()/*XXX*/));
	    }
	return create(null, ptl.toArray(new PositionAndTag[ptl.size()]));
    }

    /** Self-test: spit out all formations to System.out. */
    public static void main(String[] args) {
	for (Formation f : all)
	    System.out.println(f);
    }
}
