package net.cscott.sdr.calls;

import net.cscott.sdr.calls.Formation.DancerInfo;
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
	       Position.getGrid(0,+3,"e"),
	       Position.getGrid(0,+1,"w"),
	       Position.getGrid(0,-1,"e"),
	       Position.getGrid(0,-3,"w"),
	       // points
	       Position.getGrid(-3,+2,"n"),
	       Position.getGrid(-3,-2,"n"),
	       Position.getGrid(+3,+2,"s"),
	       Position.getGrid(+3,-2,"s"));
    public static final Formation GENERAL_DIAMONDS = // too permissive?
	create(// centers
	       Position.getGrid(0,+3,"-"),
	       Position.getGrid(0,+1,"-"),
	       Position.getGrid(0,-1,"-"),
	       Position.getGrid(0,-3,"-"),
	       // points
	       Position.getGrid(-3,+2,"|"),
	       Position.getGrid(-3,-2,"|"),
	       Position.getGrid(+3,+2,"|"),
	       Position.getGrid(+3,-2,"|"));
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
	create(Position.getGrid(0,-1,"w"),
	       Position.getGrid(0,-3,"e"),
	       Position.getGrid(+1,0,"s"),
	       Position.getGrid(+3,0,"n"),
	       Position.getGrid(0,+1,"e"),
	       Position.getGrid(0,+3,"w"),
	       Position.getGrid(-1,0,"n"),
	       Position.getGrid(-3,0,"s"));
    public static final Formation WRONG_WAY_THAR =
	create(Position.getGrid(0,-1,"e"),
	       Position.getGrid(0,-3,"w"),
	       Position.getGrid(+1,0,"n"),
	       Position.getGrid(+3,0,"s"),
	       Position.getGrid(0,+1,"w"),
	       Position.getGrid(0,+3,"e"),
	       Position.getGrid(-1,0,"s"),
	       Position.getGrid(-3,0,"n"));
    public static final Formation TIDAL_WAVE =
	create("e","w","e","w","e","w","e","w");
    public static final Formation TIDAL_TWO_FACED_LINE =
	create("e","e","w","w","e","e","w","w");
    public static final Formation Z_FORMATION =
	create("s ","sn","sn","sn"," n");
    public static final Formation TRADE_BY =
	create("nn","ss","nn","ss");
    public static final Formation PROMENADE =
	create(Position.getGrid(0,-1,"e"),
	       Position.getGrid(0,-3,"e"),
	       Position.getGrid(+1,0,"n"),
	       Position.getGrid(+3,0,"n"),
	       Position.getGrid(0,+1,"w"),
	       Position.getGrid(0,+3,"w"),
	       Position.getGrid(-1,0,"s"),
	       Position.getGrid(-3,0,"s"));
    public static final Formation WRONG_WAY_PROMENADE =
	create(Position.getGrid(0,-1,"w"),
	       Position.getGrid(0,-3,"w"),
	       Position.getGrid(+1,0,"s"),
	       Position.getGrid(+3,0,"s"),
	       Position.getGrid(0,+1,"e"),
	       Position.getGrid(0,+3,"e"),
	       Position.getGrid(-1,0,"n"),
	       Position.getGrid(-3,0,"n"));

    private static Formation create(Position... pl) {
	assert pl.length==8;
	List<DancerInfo> dil = new ArrayList<DancerInfo>(pl.length);
	for (Position p: pl)
	    dil.add(new DancerInfo(new PhantomDancer(), p, true));
	Formation f = new Formation(dil.toArray(new DancerInfo[dil.size()]));
	_formations.add(f);// add to our static list.
	return f;
    }
    // first string is 'top' of diagram (closest to caller)
    private static Formation create(String... sa) {
	List<Position> pl = new ArrayList<Position>(8);
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
		Rotation r = (sa[y].charAt(x)=='o') ? null:
		    Rotation.valueOf(sa[y].substring(x,x+1));
		pl.add(new Position(Fraction.valueOf(x,1).add(xoff),
				    Fraction.valueOf(y,1).add(yoff).negate(),
				    r));
	    }
	return create(pl.toArray(new Position[pl.size()]));
    }

    /** Self-test: spit out all formations to System.out. */
    public static void main(String[] args) {
	for (Formation f : all)
	    System.out.println(f);
    }
}
