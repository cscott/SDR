package net.cscott.sdr.calls;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.runner.RunWith;

import net.cscott.jdoctest.JDoctestRunner;
import net.cscott.jutil.Factories;
import net.cscott.jutil.GenericMultiMap;
import net.cscott.jutil.MultiMap;
import net.cscott.sdr.calls.TaggedFormation.Tag;
import static net.cscott.sdr.calls.TaggedFormation.Tag.*;
import net.cscott.sdr.calls.TaggedFormation.TaggedDancerInfo;
import net.cscott.sdr.calls.grm.BuildGrammars;
import net.cscott.sdr.util.Fraction;
import static net.cscott.sdr.util.StringEscapeUtils.escapeJava;

/**
 * Source definitions for {@link FormationList}.  This class defines the
 * formations in an inefficient but "programmer-friendly" way, using
 * automatic tagging, {@link GeneralFormationMatcher},
 * {@link Breather#insert(Formation, Map)} and other aids.  This is good for
 * conciseness, accuracy, and maintainability.  However, it (a) causes the
 * static initialization of this class to be slooow, and (b) creates dependency
 * and testing problems when doctests are defined in terms of operations on
 * formations from the list.  (For example, any bug in breather can cause
 * {@link Breather#insert(Formation, Map)} to fail, which then causes
 * the static initialization of {@link FormationList} to fail, and things
 * start going downhill quickly, making debugging the root cause difficult.)
 * <p>
 * So: what we do is post-process the "slow" formation list into an equivalent
 * "fast" formation list ({@link FormationListFast}) which is what's actually
 * used at runtime (and when trying to debug {@link Breather}!).  The
 * {@link FormationListFast} is stubbed out by simply inheriting from
 * {@link FormationListSlow} when bootstrapping.  Win, win, win!
 * <p>
 * Doctests on properties of "all formations", as well as tests confirming
 * the equivalence of {@link FormationListSlow} and {@link FormationListFast}
 * should appear on the top-level {@link FormationList} class.  This class
 * may have doctests for its helper functions, but not on the formations
 * themselves.
 */
@RunWith(value=JDoctestRunner.class)
abstract class FormationListSlow {

    // from http://www.penrod-sq-dancing.com/form2.html
    // see also http://www.penrod-sq-dancing.com/fars0.html

    // labelled calls named as per the "Callerlab Approved Formations"
    // from April 1980

    // 1-person formation
    public static final NamedTaggedFormation SINGLE_DANCER =
        create("SINGLE DANCER",
                d(0,0,"n"));
    // 2-person formations
    public static final NamedTaggedFormation GENERAL_PARTNERS =
        create("GENERAL PARTNERS", f("||"), WhetherTagger.NO_AUTO_TAGS);
    public static final NamedTaggedFormation GENERAL_TANDEM =
        create("GENERAL TANDEM", f("|","|"), WhetherTagger.NO_AUTO_TAGS);
    public static final NamedTaggedFormation _1x2 =
        create("1x2", f("++"), WhetherTagger.NO_AUTO_TAGS);
    public static final NamedTaggedFormation COUPLE = // callerlab #1
        create("COUPLE",
                d(-1,0,"n",BEAU),
                d(+1,0,"n",BELLE));
    public static final NamedTaggedFormation COUPLE_NO_TAGS =
        new NamedTaggedFormation("COUPLE NO TAGS", noTags(COUPLE));
    public static final NamedTaggedFormation FACING_DANCERS = // callerlab #2
        create("FACING DANCERS",
                d(0,+1,"s",TRAILER),
                d(0,-1,"n",TRAILER));
    public static final NamedTaggedFormation BACK_TO_BACK_DANCERS = // callerlab #3
        create("BACK-TO-BACK DANCERS",
                d(0,+1,"n",LEADER),
                d(0,-1,"s",LEADER));
    public static final NamedTaggedFormation TANDEM =
        create("TANDEM",
               d(0,+1,"n",LEADER),
               d(0,-1,"n",TRAILER));
    public static final NamedTaggedFormation RH_MINIWAVE = // callerlab #4
        create("RH MINIWAVE",
               d(-1,0,"n",BEAU),
               d(+1,0,"s",BEAU));
    public static final NamedTaggedFormation LH_MINIWAVE = // callerlab #5
        create("LH MINIWAVE",
               d(-1,0,"s",BELLE),
               d(+1,0,"n",BELLE));
    // 3-person formations
    public static final NamedTaggedFormation _1x3 =
        create("1x3", f("+++"), WhetherTagger.NO_AUTO_TAGS,
                t(0, END), t(1, CENTER), t(2, END));
    public static final NamedTaggedFormation GENERAL_LINE_OF_3 =
        create("GENERAL LINE OF 3", f("|||"), WhetherTagger.NO_AUTO_TAGS,
               t(0, END), t(1, CENTER), t(2, END));
    // 4-person formations
    public static final NamedTaggedFormation GENERAL_LINE =
        create("GENERAL LINE", f("||||"), WhetherTagger.NO_AUTO_TAGS,
                t(0, END), t(1,CENTER), t(2,CENTER), t(3,END));
    public static final NamedTaggedFormation GENERAL_COLUMN =
        create("GENERAL COLUMN", f("|","|","|","|"), WhetherTagger.NO_AUTO_TAGS,
                t(0, END), t(1,CENTER), t(2,CENTER), t(3,END));
    public static final NamedTaggedFormation _1x4 =
        create("1x4", f("++++"), WhetherTagger.NO_AUTO_TAGS,
                t(0, END), t(1,CENTER), t(2,CENTER), t(3,END));
    public static final NamedTaggedFormation _2x2 =
        create("2x2", f("++","++"), WhetherTagger.NO_AUTO_TAGS);
    public static final NamedTaggedFormation SINGLE_STATIC_SQUARE =
        create("SINGLE STATIC SQUARE",
                d( 0, 2, "s"),
                d(-2, 0, "e"),
                d( 0,-2, "n"),
                d( 2, 0, "w"));
    public static final NamedTaggedFormation FACING_COUPLES = // callerlab #6
        xofy("FACING COUPLES", FACING_DANCERS, COUPLE,
             WhetherTagger.AUTO_TAGS,
             sd(StandardDancer.COUPLE_3_GIRL,
                StandardDancer.COUPLE_3_BOY));
    public static final NamedTaggedFormation BACK_TO_BACK_COUPLES = // callerlab #7
        xofy("BACK-TO-BACK COUPLES", BACK_TO_BACK_DANCERS, COUPLE);
    public static final NamedTaggedFormation TANDEM_COUPLES =
        xofy("TANDEM COUPLES", TANDEM, COUPLE,
             WhetherTagger.AUTO_TAGS,
             sd(StandardDancer.COUPLE_3_BOY,
                StandardDancer.COUPLE_3_GIRL,
                StandardDancer.COUPLE_1_BOY,
                StandardDancer.COUPLE_1_GIRL));
    public static final NamedTaggedFormation RH_OCEAN_WAVE = // callerlab #8
        xofy("RH OCEAN WAVE", COUPLE, RH_MINIWAVE,
             WhetherTagger.AUTO_TAGS,
             sd(StandardDancer.COUPLE_1_BOY,
                StandardDancer.COUPLE_3_GIRL),
             t(0, END), t(1, CENTER), t(2, CENTER), t(3, END));
    public static final NamedTaggedFormation LH_OCEAN_WAVE = // callerlab #9
        xofy("LH OCEAN WAVE", COUPLE, LH_MINIWAVE,
             WhetherTagger.AUTO_TAGS,
             sd(StandardDancer.COUPLE_3_GIRL,
                StandardDancer.COUPLE_1_BOY),
             t(0, END), t(1, CENTER), t(2, CENTER), t(3, END));
    public static final NamedTaggedFormation RH_BOX = // callerlab #10
        xofy("RH BOX", RH_MINIWAVE, TANDEM);
    public static final NamedTaggedFormation LH_BOX = // callerlab #11
        xofy("LH BOX", LH_MINIWAVE, TANDEM);
    public static final NamedTaggedFormation INVERTED_BOX =
        create("INVERTED BOX", f("sn","ns"), WhetherTagger.AUTO_TAGS);
    public static final NamedTaggedFormation RH_IN_PINWHEEL =
	create("RH IN PINWHEEL",
	       d(-1, +1, "e", BEAU, TRAILER),
	       d(+1, +1, "s", BEAU, TRAILER),
	       d(-1, -1, "n", BEAU, TRAILER),
	       d(+1, -1, "w", BEAU, TRAILER));
    public static final NamedTaggedFormation LH_IN_PINWHEEL =
	create("LH IN PINWHEEL",
	       d(-1, +1, "s", BELLE, TRAILER),
	       d(+1, +1, "w", BELLE, TRAILER),
	       d(-1, -1, "e", BELLE, TRAILER),
	       d(+1, -1, "n", BELLE, TRAILER));
    public static final NamedTaggedFormation RH_OUT_PINWHEEL =
	create("RH OUT PINWHEEL",
	       d(-1, +1, "n", BEAU, LEADER),
	       d(+1, +1, "e", BEAU, LEADER),
	       d(-1, -1, "w", BEAU, LEADER),
	       d(+1, -1, "s", BEAU, LEADER));
    public static final NamedTaggedFormation LH_OUT_PINWHEEL =
	create("LH OUT PINWHEEL",
	       d(-1, +1, "w", BELLE, LEADER),
	       d(+1, +1, "n", BELLE, LEADER),
	       d(-1, -1, "s", BELLE, LEADER),
	       d(+1, -1, "e", BELLE, LEADER));
    public static final NamedTaggedFormation RH_SINGLE_QUARTER_ZEE =
        create("RH SINGLE 1/4 ZEE",
                d(+1, 2, "s", BEAU, TRAILER),
                d(-1, 0, "n", BEAU, CENTER, LEADER),
                d(+1, 0, "s", BEAU, CENTER, LEADER),
                d(-1,-2, "n", BEAU, TRAILER));
    public static final NamedTaggedFormation LH_SINGLE_QUARTER_ZEE =
        create("LH SINGLE 1/4 ZEE",
                d(-1, 2, "s", BELLE, TRAILER),
                d(-1, 0, "s", BELLE, CENTER, LEADER),
                d(+1, 0, "n", BELLE, CENTER, LEADER),
                d(+1,-2, "n", BELLE, TRAILER));
    public static final NamedTaggedFormation RH_SINGLE_THREE_QUARTER_ZEE =
        create("RH SINGLE 3/4 ZEE",
                d(-1, 2, "n", BEAU, LEADER),
                d(-1, 0, "n", BEAU, CENTER, TRAILER),
                d(+1, 0, "s", BEAU, CENTER, TRAILER),
                d(+1,-2, "s", BEAU, LEADER));
    public static final NamedTaggedFormation LH_SINGLE_THREE_QUARTER_ZEE =
        create("LH SINGLE 3/4 ZEE",
                d(+1, 2, "n", BELLE, LEADER),
                d(-1, 0, "s", BELLE, CENTER, TRAILER),
                d(+1, 0, "n", BELLE, CENTER, TRAILER),
                d(-1,-2, "s", BELLE, LEADER));
    public static final NamedTaggedFormation ONE_FACED_LINE =
        xofy("ONE-FACED LINE", COUPLE, COUPLE,
             t(0, END), t(1, CENTER), t(2, CENTER), t(3, END));
    public static final NamedTaggedFormation RH_TWO_FACED_LINE = // callerlab #12
        xofy("RH TWO-FACED LINE", RH_MINIWAVE, COUPLE,
                t(0, END), t(1, CENTER), t(2, CENTER), t(3, END));
    public static final NamedTaggedFormation LH_TWO_FACED_LINE = // callerlab #13
        xofy("LH TWO-FACED LINE", LH_MINIWAVE, COUPLE,
                t(0, END), t(1, CENTER), t(2, CENTER), t(3, END));
    public static final NamedTaggedFormation SINGLE_INVERTED_LINE =
        create("SINGLE INVERTED LINE", f("nssn"), WhetherTagger.AUTO_TAGS,
               sd(StandardDancer.COUPLE_1_BOY,
                  StandardDancer.COUPLE_1_GIRL,
                  StandardDancer.COUPLE_3_BOY,
                  StandardDancer.COUPLE_3_GIRL),
                t(0, END), t(1,CENTER), t(2,CENTER), t(3,END));
    public static final NamedTaggedFormation RH_THREE_AND_ONE_LINE =
        create("RH THREE-AND-ONE LINE", f("nnns"), WhetherTagger.AUTO_TAGS,
                t(0, END), t(1,CENTER), t(2,CENTER), t(3,END));
    public static final NamedTaggedFormation LH_THREE_AND_ONE_LINE =
        create("LH THREE-AND-ONE LINE", f("snnn"), WhetherTagger.AUTO_TAGS,
                t(0, END), t(1,CENTER), t(2,CENTER), t(3,END));
    // NOTE that we use Vic Ceder's position of diamond points: at the 2
    // spot, not the 3 spot.  Diamonds belong to a 2x3 formation, not a 2x4
    // This matches the output of our breathing algorithm.
    public static final NamedTaggedFormation GENERAL_DIAMOND =
        create("GENERAL DIAMOND",
               d( 0, 2,"-",POINT),
               d(-1, 0,"|",CENTER),
               d(+1, 0,"|",CENTER),
               d( 0,-2,"-",POINT));
    // A "tall diamond" is a 2x4; we match against this in order to
    // do point adjustments
    public static final NamedTaggedFormation GENERAL_TALL_DIAMOND =
        create("GENERAL TALL DIAMOND",
               d( 0, 3,"-",POINT),
               d(-1, 0,"|",CENTER),
               d(+1, 0,"|",CENTER),
               d( 0,-3,"-",POINT));
    // An "asym diamond" is a (asymmetric) diamond on quarter tag spots
    public static final NamedTaggedFormation GENERAL_ASYM_DIAMOND =
        create("GENERAL ASYM DIAMOND",
               d( 1, 2,"-",POINT),
               d(-1, 0,"|",CENTER),
               d(+1, 0,"|",CENTER),
               d( 1,-2,"-",POINT));
    public static final NamedTaggedFormation RH_DIAMOND =
        create("RH DIAMOND",
               d( 0, 2,"e",POINT),
               d(-1, 0,"n",BEAU,CENTER),
               d(+1, 0,"s",BEAU,CENTER),
               d( 0,-2,"w",POINT));
    public static final NamedTaggedFormation RH_FACING_DIAMOND =
        create("RH FACING DIAMOND",
               d( 0, 2,"w",POINT),
               d(-1, 0,"n",BEAU,CENTER),
               d(+1, 0,"s",BEAU,CENTER),
               d( 0,-2,"e",POINT));
    public static final NamedTaggedFormation LH_DIAMOND =
        create("LH DIAMOND",
               d( 0, 2,"w",POINT),
               d(-1, 0,"s",BELLE,CENTER),
               d(+1, 0,"n",BELLE,CENTER),
               d( 0,-2,"e",POINT));
    public static final NamedTaggedFormation LH_FACING_DIAMOND =
        create("LH FACING DIAMOND",
               d( 0, 2,"e",POINT),
               d(-1, 0,"s",BELLE,CENTER),
               d(+1, 0,"n",BELLE,CENTER),
               d( 0,-2,"w",POINT));
    public static final NamedTaggedFormation RH_STAR =
        create("RH STAR",
                d( 0, 1, "e"),
                d(-1, 0, "n"),
                d( 0,-1, "w"),
                d( 1, 0, "s"));
    public static final NamedTaggedFormation LH_STAR =
        create("LH STAR",
                d( 0, 1, "w"),
                d(-1, 0, "s"),
                d( 0,-1, "e"),
                d( 1, 0, "n"));
    public static final NamedTaggedFormation RH_SINGLE_PROMENADE =
        create("RH SINGLE PROMENADE", // not a star in the center
                d( 0, 2, "e"),
                d(-2, 0, "n"),
                d( 0,-2, "w"),
                d( 2, 0, "s"));
    public static final NamedTaggedFormation LH_SINGLE_PROMENADE =
        create("LH SINGLE PROMENADE", // not a star in the center
                d( 0, 2, "w"),
                d(-2, 0, "s"),
                d( 0,-2, "e"),
                d( 2, 0, "n"));
    public static final NamedTaggedFormation GENERAL_SINGLE_QUARTER_TAG =
        create("GENERAL SINGLE QUARTER TAG",
               d( 0, 2,"|",END),
               d(-1, 0,"|",CENTER),
               d(+1, 0,"|",CENTER),
               d( 0,-2,"|",END));
    // the same, but breathed over like GENERAL ASYM DIAMOND
    public static final NamedTaggedFormation GENERAL_ASYM_SINGLE_QUARTER_TAG =
        create("GENERAL ASYM SINGLE QUARTER TAG",
               d( 1, 2,"|",END),
               d(-1, 0,"|",CENTER),
               d(+1, 0,"|",CENTER),
               d( 1,-2,"|",END));
    public static final NamedTaggedFormation RH_SINGLE_QUARTER_TAG =
        create("RH SINGLE 1/4 TAG",
               d( 0, 2,"s",END),
               d(-1, 0,"n",BEAU,CENTER),
               d(+1, 0,"s",BEAU,CENTER),
               d( 0,-2,"n",END));
    public static final NamedTaggedFormation LH_SINGLE_QUARTER_TAG =
        create("LH SINGLE 1/4 TAG",
               d( 0, 2,"s",END),
               d(-1, 0,"s",BELLE,CENTER),
               d(+1, 0,"n",BELLE,CENTER),
               d( 0,-2,"n",END));
    public static final NamedTaggedFormation RH_SINGLE_THREE_QUARTER_TAG =
        create("RH SINGLE 3/4 TAG",
               d( 0, 2,"n",END),
               d(-1, 0,"n",BEAU,CENTER),
               d(+1, 0,"s",BEAU,CENTER),
               d( 0,-2,"s",END));
    public static final NamedTaggedFormation LH_SINGLE_THREE_QUARTER_TAG =
        create("LH SINGLE 3/4 TAG",
               d( 0, 2,"n",END),
               d(-1, 0,"s",BELLE,CENTER),
               d(+1, 0,"n",BELLE,CENTER),
               d( 0,-2,"s",END));
    public static final NamedTaggedFormation SINGLE_DOUBLE_PASS_THRU =
        xofy("SINGLE DOUBLE PASS THRU", FACING_DANCERS, TANDEM,
                t(0,END),t(1,CENTER),t(2,CENTER),t(3,END));
    public static final NamedTaggedFormation COMPLETED_SINGLE_DOUBLE_PASS_THRU =
        xofy("COMPLETED SINGLE DOUBLE PASS THRU", BACK_TO_BACK_DANCERS, TANDEM,
                t(0,END),t(1,CENTER),t(2,CENTER),t(3,END));
    // 8-person formations. ///////////////////////////////
    public static final NamedTaggedFormation _1x8 =
        create("1x8", f("++++++++"), WhetherTagger.AUTO_TAGS,
                t(0, OUTSIDE_4), t(1, OUTSIDE_4),
                t(6, OUTSIDE_4), t(7, OUTSIDE_4));
    public static final NamedTaggedFormation _2x4 =
        create("2x4", f("++++","++++"), WhetherTagger.NO_AUTO_TAGS,
                t(0, END), t(1, CENTER), t(2, CENTER), t(3, END),
                t(4, END), t(5, CENTER), t(6, CENTER), t(7, END));
    public static final NamedTaggedFormation PARALLEL_GENERAL_LINES =
        create("PARALLEL GENERAL LINES", f("||||","||||"), WhetherTagger.NO_AUTO_TAGS,
                t(0, END), t(1, CENTER), t(2, CENTER), t(3, END),
                t(4, END), t(5, CENTER), t(6, CENTER), t(7, END));
    public static final NamedTaggedFormation GENERAL_COLUMNS =
        create("GENERAL COLUMNS", f("||","||","||","||"), WhetherTagger.NO_AUTO_TAGS,
                t(0, END), t(1, END), t(2, CENTER), t(3, CENTER),
                t(4, CENTER), t(5, CENTER), t(6, END), t(7, END));
    public static final NamedTaggedFormation STATIC_SQUARE = // callerlab #14
        create("STATIC SQUARE", f(" ss ","e  w","e  w"," nn "),
               WhetherTagger.AUTO_TAGS,
               sd(StandardDancer.COUPLE_3_GIRL,
                  StandardDancer.COUPLE_3_BOY,
                  StandardDancer.COUPLE_4_BOY,
                  StandardDancer.COUPLE_2_GIRL));
    public static final NamedTaggedFormation STATIC_SQUARE_FACING_OUT =
        create("STATIC SQUARE FACING OUT", f(" nn ","w  e","w  e"," ss "),
               WhetherTagger.AUTO_TAGS,
               sd(StandardDancer.COUPLE_3_BOY,
                  StandardDancer.COUPLE_3_GIRL,
                  StandardDancer.COUPLE_4_GIRL,
                  StandardDancer.COUPLE_2_BOY));
    // XXX circle, callerlab #15 (we use STATIC SQUARE for this)
    public static final NamedTaggedFormation SINGLE_FILE_PROMENADE = // callerlab #16
        create("SINGLE FILE PROMENADE", f(" ww ","s  n","s  n"," ee "),
               WhetherTagger.AUTO_TAGS,
               sd(StandardDancer.COUPLE_3_GIRL,
                  StandardDancer.COUPLE_3_BOY,
                  StandardDancer.COUPLE_4_BOY,
                  StandardDancer.COUPLE_2_GIRL));
    public static final NamedTaggedFormation REVERSE_SINGLE_FILE_PROMENADE =
        create("REVERSE SINGLE FILE PROMENADE", f(" ee ","n  s","n  s"," ww "),
               WhetherTagger.AUTO_TAGS,
               sd(StandardDancer.COUPLE_3_GIRL,
                  StandardDancer.COUPLE_3_BOY,
                  StandardDancer.COUPLE_4_BOY,
                  StandardDancer.COUPLE_2_GIRL));
    // callerlab #17, alamo ring -- this is a little sketchy, because we
    // introduce an asymmetry by using squared-set spots.  In reality RH and
    // LH alamo rings are indistinguishable.
    public static final NamedTaggedFormation RH_ALAMO_RING =
        create("RH ALAMO RING", f(" ns ","e  e","w  w"," ns "),
               WhetherTagger.AUTO_TAGS,
               sd(StandardDancer.COUPLE_3_GIRL,
                  StandardDancer.COUPLE_3_BOY,
                  StandardDancer.COUPLE_4_BOY,
                  StandardDancer.COUPLE_2_GIRL));
    public static final NamedTaggedFormation LH_ALAMO_RING =
        create("LH ALAMO RING", f(" sn ","w  w","e  e"," sn "),
               WhetherTagger.AUTO_TAGS,
               sd(StandardDancer.COUPLE_3_GIRL,
                  StandardDancer.COUPLE_3_BOY,
                  StandardDancer.COUPLE_4_BOY,
                  StandardDancer.COUPLE_2_GIRL));
    public static final NamedTaggedFormation O_SPOTS =
        create("O SPOTS", f(" oo ","o  o","o  o"," oo "),
               WhetherTagger.NO_AUTO_TAGS,
               sd(StandardDancer.COUPLE_3_GIRL,
                  StandardDancer.COUPLE_3_BOY,
                  StandardDancer.COUPLE_4_BOY,
                  StandardDancer.COUPLE_2_GIRL));
    public static final NamedTaggedFormation PROMENADE = // callerlab #18
        xofy("PROMENADE", LH_SINGLE_PROMENADE, COUPLE,
             WhetherTagger.AUTO_TAGS,
             sd(StandardDancer.COUPLE_3_GIRL,
                StandardDancer.COUPLE_3_BOY,
                StandardDancer.COUPLE_4_GIRL,
                StandardDancer.COUPLE_4_BOY),
             t(0, END), t(1, CENTER), t(2, END), t(3, CENTER),
                t(4, CENTER), t(5, END), t(6, CENTER), t(7, END));
    public static final NamedTaggedFormation WRONG_WAY_PROMENADE =
        xofy("WRONG WAY PROMENADE", RH_SINGLE_PROMENADE, COUPLE,
                t(0, END), t(1, CENTER), t(2, END), t(3, CENTER),
                t(4, CENTER), t(5, END), t(6, CENTER), t(7, END));
    public static final NamedTaggedFormation STAR_PROMENADE =
        xofy("STAR PROMENADE", LH_STAR, COUPLE,
             WhetherTagger.AUTO_TAGS,
             sd(StandardDancer.COUPLE_3_GIRL,
                StandardDancer.COUPLE_3_BOY,
                StandardDancer.COUPLE_4_GIRL,
                StandardDancer.COUPLE_4_BOY),
             t(0, END), t(1, CENTER), t(2, END), t(3, CENTER),
             t(4, CENTER), t(5, END), t(6, CENTER), t(7, END));
    public static final NamedTaggedFormation WRONG_WAY_STAR_PROMENADE =
        xofy("WRONG WAY STAR PROMENADE", RH_STAR, COUPLE,
                t(0, END), t(1, CENTER), t(2, END), t(3, CENTER),
                t(4, CENTER), t(5, END), t(6, CENTER), t(7, END));
    public static final NamedTaggedFormation THAR =
        xofy("THAR", LH_STAR, LH_MINIWAVE,
                t(0, END), t(1, CENTER), t(2, END), t(3, CENTER),
                t(4, CENTER), t(5, END), t(6, CENTER), t(7, END));
    public static final NamedTaggedFormation WRONG_WAY_THAR =
        xofy("WRONG WAY THAR", LH_STAR, RH_MINIWAVE,
                t(0, END), t(1, CENTER), t(2, END), t(3, CENTER),
                t(4, CENTER), t(5, END), t(6, CENTER), t(7, END));
    public static final NamedTaggedFormation RIGHT_AND_LEFT_GRAND =
        xofy("RIGHT AND LEFT GRAND", LH_SINGLE_PROMENADE, RH_MINIWAVE,
                t(0, END), t(1, CENTER), t(2, END), t(3, CENTER),
                t(4, CENTER), t(5, END), t(6, CENTER), t(7, END));
    public static final NamedTaggedFormation RIGHT_AND_LEFT_GRAND_DIAMOND =
        xofy("RIGHT AND LEFT GRAND DIAMOND", LH_DIAMOND, RH_MINIWAVE,
                t(0, END, POINT), t(1, CENTER, POINT), t(2, END), t(3, CENTER),
                t(4, CENTER), t(5, END), t(6, CENTER, POINT), t(7, END, POINT));
    public static final NamedTaggedFormation LEFT_AND_RIGHT_GRAND =
        xofy("LEFT AND RIGHT GRAND", LH_SINGLE_PROMENADE, LH_MINIWAVE,
                t(0, END), t(1, CENTER), t(2, END), t(3, CENTER),
                t(4, CENTER), t(5, END), t(6, CENTER), t(7, END));
    public static final NamedTaggedFormation FACING_LINES = // callerlab #22
        xofy("FACING LINES", FACING_COUPLES, COUPLE,
             WhetherTagger.AUTO_TAGS,
             sd(StandardDancer.COUPLE_4_GIRL,
                StandardDancer.COUPLE_4_BOY,
                StandardDancer.COUPLE_3_GIRL,
                StandardDancer.COUPLE_3_BOY),
             t(0, END), t(1, CENTER), t(2, CENTER), t(3, END),
             t(4, END), t(5, CENTER), t(6, CENTER), t(7, END));
    public static final NamedTaggedFormation EIGHT_CHAIN_THRU = // callerlab #23
        xofy("EIGHT CHAIN THRU", FACING_COUPLES, FACING_DANCERS,
                WhetherTagger.AUTO_TAGS,
                sd(StandardDancer.COUPLE_1_GIRL,
                   StandardDancer.COUPLE_1_BOY,
                   StandardDancer.COUPLE_2_BOY,
                   StandardDancer.COUPLE_2_GIRL),
                t(0,END   ),t(1,END),
                t(2,CENTER),t(3,CENTER),
                t(4,CENTER),t(5,CENTER),
                t(6,END   ),t(7,END));

    public static final NamedTaggedFormation TRADE_BY = // callerlab #24
        xofy("TRADE BY", FACING_COUPLES, BACK_TO_BACK_DANCERS,
                WhetherTagger.AUTO_TAGS,
                sd(StandardDancer.COUPLE_1_BOY,
                   StandardDancer.COUPLE_1_GIRL,
                   StandardDancer.COUPLE_2_GIRL,
                   StandardDancer.COUPLE_2_BOY),
                t(0,END   ),t(1,END),
                t(2,CENTER),t(3,CENTER),
                t(4,CENTER),t(5,CENTER),
                t(6,END   ),t(7,END));
    public static final NamedTaggedFormation DOUBLE_PASS_THRU = // callerlab #25
        xofy("DOUBLE PASS THRU", FACING_COUPLES, TANDEM,
                WhetherTagger.AUTO_TAGS,
                sd(StandardDancer.COUPLE_1_GIRL,
                   StandardDancer.COUPLE_1_BOY,
                   StandardDancer.COUPLE_2_GIRL,
                   StandardDancer.COUPLE_2_BOY),
                t(0,END   ),t(1,END),
                t(2,CENTER),t(3,CENTER),
                t(4,CENTER),t(5,CENTER),
                t(6,END   ),t(7,END));
    public static final NamedTaggedFormation COMPLETED_DOUBLE_PASS_THRU = // callerlab #26
        xofy("COMPLETED DOUBLE PASS THRU", BACK_TO_BACK_COUPLES, TANDEM,
                t(0,END   ),t(1,END),
                t(2,CENTER),t(3,CENTER),
                t(4,CENTER),t(5,CENTER),
                t(6,END   ),t(7,END));
    public static final NamedTaggedFormation LINES_FACING_OUT = // callerlab #27
        xofy("LINES FACING OUT", BACK_TO_BACK_COUPLES, COUPLE,
                t(0, END), t(1, CENTER), t(2, CENTER), t(3, END),
                t(4, END), t(5, CENTER), t(6, CENTER), t(7, END));
    public static final NamedTaggedFormation PARALLEL_RH_WAVES = // callerlab #28(a)
        xofy("PARALLEL RH WAVES", RH_OCEAN_WAVE, TANDEM,
                t(0, END), t(1, CENTER), t(2, CENTER), t(3, END),
                t(4, END), t(5, CENTER), t(6, CENTER), t(7, END));
    public static final NamedTaggedFormation PARALLEL_LH_WAVES = // callerlab #28(b)
        xofy("PARALLEL LH WAVES", LH_OCEAN_WAVE, TANDEM,
                t(0, END), t(1, CENTER), t(2, CENTER), t(3, END),
                t(4, END), t(5, CENTER), t(6, CENTER), t(7, END));
    public static final NamedTaggedFormation PARALLEL_RH_TWO_FACED_LINES = // callerlab #29(a)
        xofy("PARALLEL RH TWO-FACED LINES", RH_BOX, COUPLE,
                WhetherTagger.AUTO_TAGS,
                sd(StandardDancer.COUPLE_1_BOY,
                   StandardDancer.COUPLE_1_GIRL,
                   StandardDancer.COUPLE_2_GIRL,
                   StandardDancer.COUPLE_2_BOY),
                t(0, END), t(1, CENTER), t(2, CENTER), t(3, END),
                t(4, END), t(5, CENTER), t(6, CENTER), t(7, END));
    public static final NamedTaggedFormation PARALLEL_LH_TWO_FACED_LINES = // callerlab #29(b)
        xofy("PARALLEL LH TWO-FACED LINES", LH_BOX, COUPLE,
                WhetherTagger.AUTO_TAGS,
                sd(StandardDancer.COUPLE_1_GIRL,
                   StandardDancer.COUPLE_1_BOY,
                   StandardDancer.COUPLE_2_BOY,
                   StandardDancer.COUPLE_2_GIRL),
                t(0, END), t(1, CENTER), t(2, CENTER), t(3, END),
                t(4, END), t(5, CENTER), t(6, CENTER), t(7, END));
    public static final NamedTaggedFormation RH_COLUMN = // callerlab #30
        xofy("RH COLUMN", RH_BOX, TANDEM,
                t(0,NUMBER_1,END   ),t(1,NUMBER_4,END),
                t(2,NUMBER_2,CENTER),t(3,NUMBER_3,CENTER),
                t(4,NUMBER_3,CENTER),t(5,NUMBER_2,CENTER),
                t(6,NUMBER_4,END   ),t(7,NUMBER_1,END));
    public static final NamedTaggedFormation LH_COLUMN = // callerlab #31
        xofy("LH COLUMN", LH_BOX, TANDEM,
                t(0,NUMBER_4,END   ),t(1,NUMBER_1,END),
                t(2,NUMBER_3,CENTER),t(3,NUMBER_2,CENTER),
                t(4,NUMBER_2,CENTER),t(5,NUMBER_3,CENTER),
                t(6,NUMBER_1,END   ),t(7,NUMBER_4,END));
    // XXX 3-and-1 lines, 8 possible, callerlab #32
    public static final NamedTaggedFormation ENDS_IN_INVERTED_LINES = // callerlab #33
        xofy("ENDS IN INVERTED LINES", FACING_DANCERS, SINGLE_INVERTED_LINE);
    public static final NamedTaggedFormation ENDS_OUT_INVERTED_LINES = // callerlab #34
        xofy("ENDS OUT INVERTED LINES", BACK_TO_BACK_DANCERS, SINGLE_INVERTED_LINE);
    // XXX in t-bone lines, callerlab #35
    // XXX out t-bone lines, callerlab #36
    // xxx both diamond-spot quarter tag & compressed quarter tag?
    // xxx do we want all variants here, or just the "canonical" ones?
    public static final NamedTaggedFormation GENERAL_QUARTER_TAG =
        create("GENERAL 1/4 TAG", f(" || ","||||"," || "),
               WhetherTagger.AUTO_TAGS);
    public static final NamedTaggedFormation RH_QUARTER_TAG = // callerlab #37(a)
        _ends_in(xofy("RH 1/4 TAG", RH_MINIWAVE, RH_SINGLE_QUARTER_TAG,
                      WhetherTagger.AUTO_TAGS,
                      t(0,BELLE), t(1,BEAU),
                      //t(2,BEAU),t(3,BEAU),t(4,BEAU),t(5,BEAU),
                      t(6,BEAU), t(7,BELLE)));
    public static final NamedTaggedFormation LH_QUARTER_TAG = // callerlab #37(a)
        _ends_in(xofy("LH 1/4 TAG", LH_MINIWAVE, LH_SINGLE_QUARTER_TAG,
                      WhetherTagger.AUTO_TAGS,
                      t(0,BELLE), t(1,BEAU),
                      //t(2,BELLE),t(3,BELLE),t(4,BELLE),t(5,BELLE),
                      t(6,BEAU), t(7,BELLE)));
    public static final NamedTaggedFormation RH_THREE_QUARTER_TAG = // callerlab #38(a)
        _ends_in(xofy("RH 3/4 TAG", RH_MINIWAVE, RH_SINGLE_THREE_QUARTER_TAG,
                      WhetherTagger.AUTO_TAGS,
                      t(0,BEAU), t(1,BELLE),
                      //t(2,BEAU),t(3,BEAU),t(4,BEAU),t(5,BEAU),
                      t(6,BELLE), t(7,BEAU)));
    public static final NamedTaggedFormation LH_THREE_QUARTER_TAG = // callerlab #38(b)
        _ends_in(xofy("LH 3/4 TAG", LH_MINIWAVE, LH_SINGLE_THREE_QUARTER_TAG,
                      WhetherTagger.AUTO_TAGS,
                      t(0,BEAU), t(1,BELLE),
                      //t(2,BELLE),t(3,BELLE),t(4,BELLE),t(5,BELLE),
                      t(6,BELLE), t(7,BEAU)));
    public static final NamedTaggedFormation RH_QUARTER_LINE = // callerlab #39(a)
        /*_ends_in*/(xofy("RH 1/4 LINE", RH_SINGLE_QUARTER_TAG, COUPLE,
                      WhetherTagger.AUTO_TAGS,
                      t(0,BELLE), t(1,BEAU), t(6,BEAU), t(7,BELLE)));
    public static final NamedTaggedFormation LH_QUARTER_LINE = // callerlab #39(b)
        /*_ends_in*/(xofy("LH 1/4 LINE", LH_SINGLE_QUARTER_TAG, COUPLE,
                      WhetherTagger.AUTO_TAGS,
                      t(0,BELLE), t(1,BEAU), t(6,BEAU), t(7,BELLE)));
    public static final NamedTaggedFormation RH_THREE_QUARTER_LINE = // callerlab #39(a)
        /*_ends_in*/(xofy("RH 3/4 LINE", RH_SINGLE_THREE_QUARTER_TAG, COUPLE,
                      WhetherTagger.AUTO_TAGS,
                      t(0,BEAU), t(1,BELLE), t(6,BELLE), t(7,BEAU)));
    public static final NamedTaggedFormation LH_THREE_QUARTER_LINE = // callerlab #39(b)
        /*_ends_in*/(xofy("LH 3/4 LINE", LH_SINGLE_THREE_QUARTER_TAG, COUPLE,
                      WhetherTagger.AUTO_TAGS,
                      t(0,BEAU), t(1,BELLE), t(6,BELLE), t(7,BEAU)));
    public static final NamedTaggedFormation RH_TWIN_DIAMONDS = // callerlab #40
        xofy("RH TWIN DIAMONDS", COUPLE, RH_DIAMOND, WhetherTagger.AUTO_TAGS,
             t(0, TRAILER), t(1, LEADER), t(6, LEADER), t(7, TRAILER));
    public static final NamedTaggedFormation LH_TWIN_DIAMONDS = // callerlab #41
        xofy("LH TWIN DIAMONDS", COUPLE, LH_DIAMOND, WhetherTagger.AUTO_TAGS,
             t(0, LEADER), t(1, TRAILER), t(6, TRAILER), t(7, LEADER));
    public static final NamedTaggedFormation RH_POINT_TO_POINT_DIAMONDS = // callerlab #42(a)
        xofy("RH POINT-TO-POINT DIAMONDS", TANDEM, RH_DIAMOND, WhetherTagger.AUTO_TAGS,
             t(1,LEADER), t(2,TRAILER), t(5,TRAILER), t(6,LEADER));
    public static final NamedTaggedFormation RH_POINT_TO_POINT_FACING_DIAMONDS = // callerlab #42(b)
        xofy("RH POINT-TO-POINT FACING DIAMONDS", TANDEM, RH_FACING_DIAMOND, WhetherTagger.AUTO_TAGS,
             t(1,LEADER), t(2,TRAILER), t(5,TRAILER), t(6,LEADER));
    public static final NamedTaggedFormation LH_POINT_TO_POINT_DIAMONDS = // callerlab #42(c)
        xofy("LH POINT-TO-POINT DIAMONDS", TANDEM, LH_DIAMOND, WhetherTagger.AUTO_TAGS,
             t(1,TRAILER), t(2,LEADER), t(5,LEADER), t(6,TRAILER));
    public static final NamedTaggedFormation LH_POINT_TO_POINT_FACING_DIAMONDS = // callerlab #42(d)
        xofy("LH POINT-TO-POINT FACING DIAMONDS", TANDEM, LH_FACING_DIAMOND, WhetherTagger.AUTO_TAGS,
             t(1,TRAILER), t(2,LEADER), t(5,LEADER), t(6,TRAILER));
    public static final NamedTaggedFormation RH_TWIN_FACING_DIAMONDS = // callerlab #43
        xofy("RH TWIN FACING DIAMONDS", COUPLE, RH_FACING_DIAMOND, WhetherTagger.AUTO_TAGS,
             t(0, LEADER), t(1, TRAILER), t(6, TRAILER), t(7, LEADER));
    public static final NamedTaggedFormation LH_TWIN_FACING_DIAMONDS = // callerlab #44
        xofy("LH TWIN FACING DIAMONDS", COUPLE, LH_FACING_DIAMOND, WhetherTagger.AUTO_TAGS,
             t(0, TRAILER), t(1, LEADER), t(6, LEADER), t(7, TRAILER));
    public static final NamedTaggedFormation TWIN_GENERAL_DIAMONDS =
        xofy("TWIN GENERAL DIAMONDS", COUPLE, GENERAL_DIAMOND);
    public static final NamedTaggedFormation POINT_TO_POINT_GENERAL_DIAMONDS =
        xofy("POINT-TO-POINT GENERAL DIAMONDS", TANDEM, GENERAL_DIAMOND);
    public static final NamedTaggedFormation CONCENTRIC_GENERAL_DIAMONDS =
        xofy("CONCENTRIC GENERAL DIAMONDS", RH_DIAMOND, GENERAL_PARTNERS,
             WhetherTagger.NO_AUTO_TAGS,
             t(0, END,    OUTSIDE_2, OUTSIDE_6),
             t(1, END,    CENTER_6,  OUTSIDE_6),
             t(2, CENTER, CENTER_6,  OUTSIDE_6),
             t(3, CENTER, CENTER_6,  VERY_CENTER),
             t(4, CENTER, CENTER_6,  VERY_CENTER),
             t(5, CENTER, CENTER_6,  OUTSIDE_6),
             t(6, END,    CENTER_6,  OUTSIDE_6),
             t(7, END,    OUTSIDE_2, OUTSIDE_6));
    public static final NamedTaggedFormation RH_HOURGLASS =
        create("RH HOURGLASS",
               d( 0, 4, "e", CENTER, POINT),
               d(-3, 2, "n", END),
               d(+3, 2, "s", END),
               d(-1, 0, "n", BEAU, CENTER, VERY_CENTER),
               d(+1, 0, "s", BEAU, CENTER, VERY_CENTER),
               d(-3,-2, "n", END),
               d(+3,-2, "s", END),
               d( 0,-4, "w", CENTER, POINT)); // callerlab #45(a)
    public static final NamedTaggedFormation LH_HOURGLASS =
        create("LH HOURGLASS",
               d( 0, 4, "w", CENTER, POINT),
               d(-3, 2, "s", END),
               d(+3, 2, "n", END),
               d(-1, 0, "s", BELLE, CENTER, VERY_CENTER),
               d(+1, 0, "n", BELLE, CENTER, VERY_CENTER),
               d(-3,-2, "s", END),
               d(+3,-2, "n", END),
               d( 0,-4, "e", CENTER, POINT)); // callerlab #45(b)
    public static final NamedTaggedFormation GENERAL_HOURGLASS =
        create("GENERAL HOURGLASS",
               d( 0, 4, "-", CENTER, POINT),
               d(-3, 2, "|", END),
               d(+3, 2, "|", END),
               d(-1, 0, "|", CENTER, VERY_CENTER),
               d(+1, 0, "|", CENTER, VERY_CENTER),
               d(-3,-2, "|", END),
               d(+3,-2, "|", END),
               d( 0,-4, "-", CENTER, POINT));
    // XXX left hand Z, callerlab #46
    // XXX right hand Z, callerlab #47
    // XXX right hand stars, callerlab #48
    // XXX left hand stars, callerlab #49
    public static final NamedTaggedFormation RH_TIDAL_WAVE = // callerlab #50(a)
        xofy("RH TIDAL WAVE", COUPLE, RH_OCEAN_WAVE);
    public static final NamedTaggedFormation LH_TIDAL_WAVE = // callerlab #50(b)
        xofy("LH TIDAL WAVE", COUPLE, LH_OCEAN_WAVE);
    public static final NamedTaggedFormation RH_TIDAL_TWO_FACED_LINE = // callerlab #51(a)
        xofy("RH TIDAL TWO-FACED LINE", COUPLE, RH_TWO_FACED_LINE,
             WhetherTagger.AUTO_TAGS,
             sd(StandardDancer.COUPLE_1_BOY,
                StandardDancer.COUPLE_1_GIRL,
                StandardDancer.COUPLE_2_GIRL,
                StandardDancer.COUPLE_2_BOY));
    public static final NamedTaggedFormation LH_TIDAL_TWO_FACED_LINE = // callerlab #51(b)
        xofy("LH TIDAL TWO-FACED LINE", COUPLE, LH_TWO_FACED_LINE,
             WhetherTagger.AUTO_TAGS,
             sd(StandardDancer.COUPLE_1_GIRL,
                StandardDancer.COUPLE_1_BOY,
                StandardDancer.COUPLE_2_BOY,
                StandardDancer.COUPLE_2_GIRL));
    public static final NamedTaggedFormation RH_TIDAL_LINE = // callerlab #52(a)
        xofy("RH TIDAL LINE", RH_TWO_FACED_LINE, COUPLE);
    public static final NamedTaggedFormation LH_TIDAL_LINE = // callerlab #52(b)
        xofy("LH TIDAL LINE", LH_TWO_FACED_LINE, COUPLE,
             WhetherTagger.AUTO_TAGS,
             sd(StandardDancer.COUPLE_1_GIRL,
                StandardDancer.COUPLE_1_BOY,
                StandardDancer.COUPLE_2_GIRL,
                StandardDancer.COUPLE_2_BOY));
    public static final NamedTaggedFormation GENERAL_TIDAL_LINE =
        create("GENERAL TIDAL LINE", f("||||||||"), WhetherTagger.AUTO_TAGS,
               t(0, OUTSIDE_4), t(1, OUTSIDE_4),
               t(6, OUTSIDE_4), t(7, OUTSIDE_4));
    public static final NamedTaggedFormation RH_GALAXY =
        create("RH GALAXY",
               d( 0, 4, "e", END, POINT),
               d(-1, 2, "n", CENTER, BEAU),
               d(+1, 2, "s", CENTER, BEAU),
               d(-3, 0, "n", END, POINT),
               d(+3, 0, "s", END, POINT),
               d(-1,-2, "n", CENTER, BEAU),
               d(+1,-2, "s", CENTER, BEAU),
               d( 0,-4, "w", END, POINT)); // callerlab #53(a)
    public static final NamedTaggedFormation LH_GALAXY =
        create("LH GALAXY",
               d( 0, 4, "w", END, POINT),
               d(-1, 2, "s", CENTER, BELLE),
               d(+1, 2, "n", CENTER, BELLE),
               d(-3, 0, "s", END, POINT),
               d(+3, 0, "n", END, POINT),
               d(-1,-2, "s", CENTER, BELLE),
               d(+1,-2, "n", CENTER, BELLE),
               d( 0,-4, "e", END, POINT)); // callerlab #53(b)
    public static final NamedTaggedFormation GENERAL_GALAXY =
        create("GENERAL GALAXY",
               d( 0, 4, "-", END, POINT),
               d(-1, 2, "|", CENTER),
               d(+1, 2, "|", CENTER),
               d(-3, 0, "|", END, POINT),
               d(+3, 0, "|", END, POINT),
               d(-1,-2, "|", CENTER),
               d(+1,-2, "|", CENTER),
               d( 0,-4, "-", END, POINT));
    public static final NamedTaggedFormation GENERAL_SPINDLE =
        create("GENERAL SPINDLE",
               d( 0, 4, "-", END, POINT),
               d(-1, 2, "|", CENTER_6),
               d(+1, 2, "|", CENTER_6),
               d(-1, 0, "|", VERY_CENTER, CENTER_6),
               d(+1, 0, "|", VERY_CENTER, CENTER_6),
               d(-1,-2, "|", CENTER_6),
               d(+1,-2, "|", CENTER_6),
               d( 0,-4, "-", END, POINT));
    public static final NamedTaggedFormation O_DOUBLE_PASS_THRU = // used for grand square
        create("O DOUBLE PASS THRU", f(" ss ","s  s","n  n"," nn "),
                WhetherTagger.NO_AUTO_TAGS, // gets confused by the distortion
                sd(StandardDancer.COUPLE_1_GIRL,
                   StandardDancer.COUPLE_1_BOY,
                   StandardDancer.COUPLE_2_GIRL,
                   StandardDancer.COUPLE_2_BOY),
               // should have same tags as DOUBLE PASS THRU
               t(0, BELLE, END,    TRAILER, NUMBER_4),
               t(1, BEAU,  END,    TRAILER, NUMBER_4),
               t(2, BELLE, CENTER, LEADER,  NUMBER_3),
               t(3, BEAU,  CENTER, LEADER,  NUMBER_3),
               t(4, BEAU,  CENTER, LEADER,  NUMBER_3),
               t(5, BELLE, CENTER, LEADER,  NUMBER_3),
               t(6, BEAU,  END,    TRAILER, NUMBER_4),
               t(7, BELLE, END,    TRAILER, NUMBER_4));
    public static final NamedTaggedFormation GENERAL_O =
        create("GENERAL O", f(" || ","|  |","|  |"," || "),
                WhetherTagger.NO_AUTO_TAGS,
                t(0, END),    t(1, END),
                t(2, CENTER), t(3, CENTER),
                t(4, CENTER), t(5, CENTER),
                t(6, END),    t(7, END));
    public static final NamedTaggedFormation BUTTERFLY_DOUBLE_PASS_THRU = // used for grand square
        create("BUTTERFLY DOUBLE PASS THRU", f("s  s"," ss "," nn ","n  n"),
               WhetherTagger.NO_AUTO_TAGS, // gets confused by the distortion
               sd(StandardDancer.COUPLE_1_GIRL,
                  StandardDancer.COUPLE_1_BOY,
                  StandardDancer.COUPLE_2_GIRL,
                  StandardDancer.COUPLE_2_BOY),
               // should have same tags as DOUBLE PASS THRU
               t(0, BELLE, END,    TRAILER, NUMBER_4),
               t(1, BEAU,  END,    TRAILER, NUMBER_4),
               t(2, BELLE, CENTER, LEADER,  NUMBER_3),
               t(3, BEAU,  CENTER, LEADER,  NUMBER_3),
               t(4, BEAU,  CENTER, LEADER,  NUMBER_3),
               t(5, BELLE, CENTER, LEADER,  NUMBER_3),
               t(6, BEAU,  END,    TRAILER, NUMBER_4),
               t(7, BELLE, END,    TRAILER, NUMBER_4));
    public static final NamedTaggedFormation GENERAL_BUTTERFLY =
        create("GENERAL BUTTERFLY", f("|  |"," || "," || ","|  |"),
                WhetherTagger.NO_AUTO_TAGS,
                t(0, END),    t(1, END),
                t(2, CENTER), t(3, CENTER),
                t(4, CENTER), t(5, CENTER),
                t(6, END),    t(7, END));
    // 12-person formations
    public static final NamedTaggedFormation _1x12 =
        create("1x12", f("++++++++++++"), WhetherTagger.AUTO_TAGS);
    public static final NamedTaggedFormation _2x6 =
        create("2x6", f("++++++","++++++"), WhetherTagger.AUTO_TAGS);
    public static final NamedTaggedFormation _3x4 =
        create("3x4", f("++++","++++","++++"), WhetherTagger.AUTO_TAGS);
    public static final NamedTaggedFormation TRIPLE_GENERAL_H =
        xofy("TRIPLE GENERAL H",
             create("H", f("n","e","s"), WhetherTagger.NO_AUTO_TAGS),
             noTags(_1x4), WhetherTagger.AUTO_TAGS);
    public static final NamedTaggedFormation TRIPLE_GENERAL_PLUS =
        xofy("TRIPLE GENERAL PLUS",
             create("PLUS", f("nes"), WhetherTagger.NO_AUTO_TAGS),
             noTags(_1x4), WhetherTagger.AUTO_TAGS);
    public static final NamedTaggedFormation TRIPLE_GENERAL_LINES =
        create("TRIPLE GENERAL LINES", f("||||","||||","||||"),
               WhetherTagger.AUTO_TAGS);
    public static final NamedTaggedFormation TRIPLE_GENERAL_DIAMONDS =
        xofy("TRIPLE GENERAL DIAMONDS",
             create("3", f("nnn"), WhetherTagger.NO_AUTO_TAGS),
             noTags(GENERAL_DIAMOND), WhetherTagger.NO_AUTO_TAGS,
             t(0, OUTSIDE_8), t(1, CENTER), t(2, OUTSIDE_8),
             t(3, OUTSIDE_8), t(4, OUTSIDE_8), t(5, CENTER), t(6, CENTER),
             t(7, OUTSIDE_8), t(8, OUTSIDE_8),
             t(9, OUTSIDE_8), t(10, CENTER), t(11, OUTSIDE_8));
    public static final NamedTaggedFormation TRIPLE_GENERAL_TALL_DIAMONDS =
        xofy("TRIPLE GENERAL TALL DIAMONDS",
             create("3", f("nnn"), WhetherTagger.NO_AUTO_TAGS),
             noTags(GENERAL_TALL_DIAMOND), WhetherTagger.NO_AUTO_TAGS,
             t(0, OUTSIDE_8), t(1, CENTER), t(2, OUTSIDE_8),
             t(3, OUTSIDE_8), t(4, OUTSIDE_8), t(5, CENTER), t(6, CENTER),
             t(7, OUTSIDE_8), t(8, OUTSIDE_8),
             t(9, OUTSIDE_8), t(10, CENTER), t(11, OUTSIDE_8));
    public static final NamedTaggedFormation TRIPLE_GENERAL_ASYM_DIAMONDS =
        create("TRIPLE GENERAL ASYM DIAMONDS",
               d(-3, 2,"-",OUTSIDE_8),d(0, 2,"-",CENTER),d(3, 2,"-",OUTSIDE_8),
               d(-5, 0,"|",OUTSIDE_8),d(-3,0,"|",OUTSIDE_8),
               d(-1, 0,"|",CENTER),   d(+1,0,"|",CENTER),
               d(+3, 0,"|",OUTSIDE_8),d(+5,0,"|",OUTSIDE_8),
               d(-3,-2,"-",OUTSIDE_8),d(0,-2,"-",CENTER),d(3,-2,"-",OUTSIDE_8));
    public static final NamedTaggedFormation TRIPLE_POINT_TO_POINT_GENERAL_DIAMONDS =
        xofy("TRIPLE POINT-TO-POINT GENERAL DIAMONDS",
             create("3", f("n","n","n"), WhetherTagger.NO_AUTO_TAGS),
             noTags(GENERAL_DIAMOND), WhetherTagger.NO_AUTO_TAGS,
             t(0, OUTSIDE_8), t(1, OUTSIDE_8), t(2, OUTSIDE_8), t(3, OUTSIDE_8),
             t(4, CENTER), t(5, CENTER), t(6, CENTER), t(7, CENTER),
             t(8, OUTSIDE_8), t(9, OUTSIDE_8), t(10, OUTSIDE_8), t(11, OUTSIDE_8));
    public static final NamedTaggedFormation TRIPLE_POINT_TO_POINT_GENERAL_TALL_DIAMONDS =
        xofy("TRIPLE POINT-TO-POINT GENERAL TALL DIAMONDS",
             create("3", f("n","n","n"), WhetherTagger.NO_AUTO_TAGS),
             noTags(GENERAL_TALL_DIAMOND), WhetherTagger.NO_AUTO_TAGS,
             t(0, OUTSIDE_8), t(1, OUTSIDE_8), t(2, OUTSIDE_8), t(3, OUTSIDE_8),
             t(4, CENTER), t(5, CENTER), t(6, CENTER), t(7, CENTER),
             t(8, OUTSIDE_8), t(9, OUTSIDE_8), t(10, OUTSIDE_8), t(11, OUTSIDE_8));
    // XXX also: TRIPLE RH ZEE, TRIPLE LH ZEE

    // 16-person formations
    public static final NamedTaggedFormation _4x4 =
        create("4x4", f("++++","++++","++++","++++"),
               WhetherTagger.NO_AUTO_TAGS);
    public static final NamedTaggedFormation QUADRUPLE_GENERAL_LINES =
        create("QUADRUPLE GENERAL LINES", f("||||","||||","||||","||||"), WhetherTagger.NO_AUTO_TAGS,
                t(0, OUTSIDE_8), t(1, OUTSIDE_8), t(2, OUTSIDE_8), t(3, OUTSIDE_8),
                t(4, CENTER), t(5, CENTER), t(6, CENTER), t(7, CENTER),
                t(8, CENTER), t(9, CENTER), t(10, CENTER),t(11, CENTER),
                t(12, OUTSIDE_8),t(13, OUTSIDE_8),t(14, OUTSIDE_8),t(15, OUTSIDE_8));
    public static final NamedTaggedFormation QUADRUPLE_GENERAL_COLUMNS =
        create("QUADRUPLE GENERAL COLUMNS", f("||||","||||","||||","||||"), WhetherTagger.NO_AUTO_TAGS,
                t(0, OUTSIDE_8), t(1, CENTER), t(2, CENTER), t(3, OUTSIDE_8),
                t(4, OUTSIDE_8), t(5, CENTER), t(6, CENTER), t(7, OUTSIDE_8),
                t(8, OUTSIDE_8), t(9, CENTER), t(10,CENTER),t(11, OUTSIDE_8),
                t(12, OUTSIDE_8),t(13,CENTER), t(14,CENTER),t(15, OUTSIDE_8));

    /** List of all formations defined in this class. */
    public static final List<NamedTaggedFormation> all = _enumerateFormations();
    private static List<NamedTaggedFormation> _enumerateFormations() {
        List<NamedTaggedFormation> result = new ArrayList<NamedTaggedFormation>();
        for (Field f: FormationListSlow.class.getDeclaredFields()) {
            if (Modifier.isStatic(f.getModifiers()) &&
                Modifier.isPublic(f.getModifiers()) &&
                !f.getName().equals("all")) {
                try {
                    result.add((NamedTaggedFormation)f.get(null));
                } catch (Throwable t) { /* ignore */ }
            }
        }
        return Collections.unmodifiableList(result);
    }
    private static class PositionAndTag {
        public final Position position;
        public final Set<Tag> tags;
        PositionAndTag(Position position, Set<Tag> tags) {
            this.position = position; this.tags = tags;
        }
    }
    private static PositionAndTag d(int x, int y, String facing,
                                    Tag... tags) {
        return new PositionAndTag(Position.getGrid
                                  (x, y, Rotation.fromAbsoluteString(facing)),
                                  TaggedFormation.mkTags(tags));
    }
    private static NamedTaggedFormation create(final String name, PositionAndTag... ptl) {
	List<TaggedDancerInfo> dil = new ArrayList<TaggedDancerInfo>(ptl.length);
	for (PositionAndTag pt: ptl)
	    dil.add(new TaggedDancerInfo(new PhantomDancer(), pt.position, pt.tags, true));
	return new NamedTaggedFormation(name, null, dil.toArray(new TaggedDancerInfo[dil.size()]));
    }
    // first string is 'top' of diagram (closest to caller)
    // dancers are numbered left to right, top to bottom. (reading order)
    private static NamedTaggedFormation create(String name, String[] sa, WhetherTagger wt, StandardDancer[] normalCouples, NumAndTags... tags) {
        Map<Dancer,Position> m = new LinkedHashMap<Dancer,Position>();
	// check validity
	assert sa.length>0;
	for (int i=0; i<sa.length-1; i++)
	    assert sa[i].length()==sa[i+1].length();
	// okay, create formation w/ phantoms.
	for (int y=0; y<sa.length; y++)
	    for (int x=0; x<sa[y].length(); x++) {
		if (sa[y].charAt(x)==' ') continue;
		Rotation r =
		    Rotation.fromAbsoluteString(sa[y].substring(x,x+1));
		m.put(new PhantomDancer(),
		        new Position(Fraction.valueOf(x*2),
		                Fraction.valueOf(y*2).negate(),
		                r));
	    }
	Formation f = new Formation(m).recenter();
        return addTags(name, f, wt, normalCouples, tags);
    }
    // "normalCouples" argument is optional
    private static NamedTaggedFormation create(String name, String[] sa, WhetherTagger wt, NumAndTags... tags) {
        return create(name, sa, wt, null, tags);
    }
    // helper
    private static String[] f(String... sa) { return sa; }
    private static StandardDancer[] sd(StandardDancer... sa) { return sa; }

    /** Formation composition: create a formation with an X of Ys. */
    private static TaggedFormation _xofy(Formation x, TaggedFormation y) {
        /* create an instance of 'y' for each dancer in 'x'. */
        Map<Dancer,Formation> sub = new LinkedHashMap<Dancer,Formation>();
        MultiMap<Dancer,Tag> tags = new GenericMultiMap<Dancer,Tag>
                                        (Factories.enumSetFactory(Tag.class));
        for (Dancer d: x.dancers()) {
            Map<Dancer,Position> m = new LinkedHashMap<Dancer,Position>();
            for (Dancer dd : y.dancers()) {
                Dancer phantom = new PhantomDancer();
                m.put(phantom, y.location(dd));
                tags.addAll(phantom, y.tags(dd));
            }
            sub.put(d, new Formation(m));
        }
        Formation result = Breather.insert(x,sub);
        // Transfer tags from formation y.
        return new TaggedFormation(result, tags);
    }
    /** Helper function for the above to add dancer tags. The dancers
     * are numbered left to right, top to bottom.  A null indicates
     * "no additional tags".
     */
    private static NamedTaggedFormation xofy(final String name, Formation x, TaggedFormation y, NumAndTags... tags) {
        return xofy(name, x, y, WhetherTagger.AUTO_TAGS, null, tags);
    }
    private static NamedTaggedFormation xofy(final String name, Formation x, TaggedFormation y, WhetherTagger wt, NumAndTags... tags) {
        return xofy(name, x, y, wt, null, tags);
    }
    private static NamedTaggedFormation xofy(final String name, Formation x, TaggedFormation y, WhetherTagger wt, StandardDancer[] normalCouples, NumAndTags... tags) {
        return addTags(name, _xofy(x,y), wt, normalCouples, tags);
    }
    private static TaggedFormation noTags(Formation f) {
        return new TaggedFormation(f, new GenericMultiMap<Dancer,Tag>
                                   (Factories.enumSetFactory(Tag.class)));
    }
    private static enum WhetherTagger { AUTO_TAGS, NO_AUTO_TAGS; }
    private static NamedTaggedFormation addTags(final String name, final Formation f,
        WhetherTagger wt, StandardDancer[] normalCouples, NumAndTags... tags) {
        List<Dancer> dancers = f.sortedDancers();
        MultiMap<Dancer,Tag> tm = new GenericMultiMap<Dancer,Tag>
            (Factories.enumSetFactory(Tag.class));
        // add existing tags
        if (f instanceof TaggedFormation)
            for (Dancer d : f.dancers())
                tm.addAll(d, ((TaggedFormation)f).tags(d));
        // add explicitly specified tags.
        for (NumAndTags nt : tags)
            tm.addAll(dancers.get(nt.dancerNum), nt.tags);
        // add implicit/automatic tags
        if (wt != WhetherTagger.NO_AUTO_TAGS) // general formations don't get tags
            Tagger.addAutomatic(f, tm);
        return new NamedTaggedFormation(name, f, tm, normalCouples);
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
    // tweak ends of a quarter tag or diamond on "diamond spots" in.
    private static NamedTaggedFormation _ends_in(NamedTaggedFormation tf) {
        String name = tf.getName();
	Rotation ew = Rotation.fromAbsoluteString("|");
	TaggedFormation result=tf;
	for (Dancer d: tf.tagged(END)) {
	    Position p = tf.location(d);
	    // step "in" in east/west direction towards center line.
	    if (ew.includes(p.facing))
		p = p.sideStep(Fraction.ONE, true);
	    else
		p = p.forwardStep(Fraction.ONE, true);
	    result = result.move(d, p);
	}
	// transfer name
	return new NamedTaggedFormation(name, result);
    }
    /** Do any of the dancers have inexact rotations? */
    private static boolean isGeneral(Formation f) {
        for (Dancer d: f.dancers())
            if (!f.location(d).facing.isExact())
                return true;
        return false;
    }

    /** Compile formations in this class into "fast" formations. */
    public static void main(String[] args) throws Exception {
        if (args.length==0) {
            emit(new PrintWriter(System.out));
        } else {
            StringWriter sw = new StringWriter();
            emit(new PrintWriter(sw));
            BuildGrammars.writeFile(args[0], sw.toString());
        }
    }
    private static void emit(PrintWriter pw) {
        List<String> allFormations = new ArrayList<String>();
        emitHeader(pw);
        for (Field f : FormationListSlow.class.getDeclaredFields()) {
            if (Modifier.isPublic(f.getModifiers()) &&
                Modifier.isStatic(f.getModifiers()) &&
                !f.getName().equals("all")) {
                try {
                    String fieldName = f.getName();
                    NamedTaggedFormation ntf;
                    ntf = (NamedTaggedFormation) f.get(null);
                    emitOne(pw, fieldName, ntf);
                    allFormations.add(fieldName);
                } catch (IllegalArgumentException e) {
                    assert false : e;
                } catch (IllegalAccessException e) {
                    assert false : e;
                }
            }
        }
        emitAll(pw, allFormations);
        emitFooter(pw);
        pw.flush();
    }
    private static void emitHeader(PrintWriter pw) {
        pw.println("package net.cscott.sdr.calls;");
        pw.println();
        pw.println("import java.util.Arrays;");
        pw.println("import java.util.Collections;");
        pw.println("import java.util.List;");
        pw.println();
        pw.println("import org.junit.runner.RunWith;");
        pw.println();
        pw.println("import net.cscott.jdoctest.JDoctestRunner;");
        pw.println();
        pw.println("import net.cscott.sdr.calls.TaggedFormation.Tag;");
        pw.println("import static net.cscott.sdr.calls.TaggedFormation.TaggedDancerInfo;");
        pw.println("import net.cscott.sdr.util.Fraction;");
        pw.println();
        pw.println("/** Compiled version of {@link FormationListSlow}. */");
        pw.println("@RunWith(value=JDoctestRunner.class)");
        pw.println("abstract class FormationListFast {");
    }
    private static void emitOne(PrintWriter pw, String fieldName,
                                NamedTaggedFormation ntf) {
        String mapStd = ""; Formation mntf = ntf;
        if (ntf.dancers().size() <= 8 && !isGeneral(ntf)) {
            mapStd = ".mapStd([])";
            mntf = ntf.mapStd();
        }
        String escapedName = escapeJava(ntf.getName());
        pw.println("    /** "+ntf.getName()+" formation.");
        pw.println("      * @doc.test");
        pw.println("      *  js> tf = FormationList."+fieldName+"; tf"+mapStd+".toStringDiagram('|');");
        pw.println(mntf.toStringDiagram("      *  |"));
        pw.println("      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\\n');");
        for (Dancer d : ntf.sortedDancers())
            pw.println("      *  "+new ArrayList<Tag>(ntf.tags(d)));
        pw.println("      */");
        pw.println("    public static final NamedTaggedFormation "+fieldName+" =");
        pw.println("        new NamedTaggedFormation(\""+escapedName+"\",");
        pw.print  ("            ");
        StandardDancer[] normalCouples = ntf.normalCouples();
        if (normalCouples == null) {
            pw.print  ("null");
        } else {
            pw.println("new StandardDancer[] {");
            for (StandardDancer sd : normalCouples) {
                pw.print  ("                ");
                pw.print  ("StandardDancer.");
                pw.print  (sd.name());
                pw.println(",");
            }
            pw.print  ("            }");
        }
        for (Dancer d : ntf.sortedDancers()) {
            pw.println(",");
            pw.print  ("            ");
            pw.print  ("new TaggedDancerInfo(new PhantomDancer(), ");
            pw.print  (ntf.location(d).repr());
            for (Tag t : ntf.tags(d)) {
                pw.print(", Tag.");
                pw.print(t.name());
            }
            pw.print(")");
        }
        pw.println(");");
        pw.println();
    }
    private static void emitAll(PrintWriter pw, List<String> allFormations) {
        pw.println("    /** List of all formations defined here. */");
        pw.println("    public static final List<NamedTaggedFormation> all =");
        pw.println("        Collections.unmodifiableList(Arrays.asList(");
        boolean first = true;
        for (String fieldName : allFormations) {
            if (!first) pw.println(",");
            pw.print("            FormationListFast."+fieldName);
            first = false;
        }
        pw.println("));");
    }
    private static void emitFooter(PrintWriter pw) {
        pw.println("}");
    }
}
