package net.cscott.sdr.calls;

import static net.cscott.sdr.util.Tools.m;
import static net.cscott.sdr.util.Tools.p;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.cscott.jdoctest.JDoctestRunner;
import net.cscott.jutil.Factories;
import net.cscott.jutil.GenericMultiMap;
import net.cscott.jutil.MultiMap;
import net.cscott.sdr.calls.TaggedFormation.Tag;
import net.cscott.sdr.calls.ast.Expr;
import net.cscott.sdr.util.Fraction;
import net.cscott.sdr.util.ListUtils;
import net.cscott.sdr.util.Tools;

import org.junit.runner.RunWith;

/**
 * The {@link MatcherList} contains matchers for various formations.
 * It mostly parallels {@link FormationList}, although there are
 * matchers for general formations which are a combination of
 * several formations in the formation list.
 *
 * @doc.test Apply STATIC_SQUARE matcher to a SQUARED_SET:
 *  js> MatcherList.STATIC_SQUARE.match(Formation.SQUARED_SET)
 *  AA^
 *  AA:
 *          3Gv  3Bv
 *     
 *     4B>            2G<
 *     
 *     4G>            2B<
 *     
 *          1B^  1G^
 *   [3G: BELLE; 3B: BEAU; 4B: BEAU; 2G: BELLE; 4G: BELLE; 2B: BEAU; 1B: BEAU; 1G: BELLE]
 * @doc.test Apply COUPLE matcher to a SQUARED_SET:
 *  js> fm = MatcherList.COUPLE.match(Formation.SQUARED_SET)
 *       AAv
 *  
 *  BB>       CC<
 *  
 *       DD^
 *  AA:
 *     3B^  3G^
 *   [3B: BEAU; 3G: BELLE]
 *  BB:
 *     4B^  4G^
 *   [4B: BEAU; 4G: BELLE]
 *  CC:
 *     2B^  2G^
 *   [2B: BEAU; 2G: BELLE]
 *  DD:
 *     1B^  1G^
 *   [1B: BEAU; 1G: BELLE]
 *  js> fm.matches.size()
 *  4
 *  js> fm.meta.dancers().size()
 *  4
 * @doc.test Apply COUPLE matcher to FOUR_SQUARE:
 *  js> fm = MatcherList.COUPLE.match(Formation.FOUR_SQUARE)
 *  AAv
 *  
 *  BB^
 *  AA:
 *     3B^  3G^
 *   [3B: BEAU; 3G: BELLE]
 *  BB:
 *     1B^  1G^
 *   [1B: BEAU; 1G: BELLE]
 * @doc.test Apply FACING_DANCERS matcher to FOUR_SQUARE:
 *  js> fm = MatcherList.FACING_DANCERS.match(Formation.FOUR_SQUARE)
 *  AAv  BB^
 *  AA:
 *     1Bv
 *     
 *     3G^
 *   [1B: TRAILER; 3G: TRAILER]
 *  BB:
 *     3Bv
 *     
 *     1G^
 *   [3B: TRAILER; 1G: TRAILER]
 * @doc.test Apply FACING_COUPLES matcher to FOUR_SQUARE:
 *  js> fm = MatcherList.FACING_COUPLES.match(Formation.FOUR_SQUARE)
 *  AA^
 *  AA:
 *     3Gv  3Bv
 *     
 *     1B^  1G^
 *   [3G: BELLE,TRAILER; 3B: BEAU,TRAILER; 1B: BEAU,TRAILER; 1G: BELLE,TRAILER]
 * @doc.test Apply RH_MINIWAVE matcher to PARALLEL_RH_WAVES:
 *  js> FormationList = FormationList.js(this); undefined;
 *  js> fm = MatcherList.RH_MINIWAVE.match(FormationList.PARALLEL_RH_WAVES)
 *  AA^  BBv
 *  
 *  CC^  DDv
 *  AA:
 *     ^    v
 *   [ph: BEAU; ph: BEAU]
 *  BB:
 *     ^    v
 *   [ph: BEAU; ph: BEAU]
 *  CC:
 *     ^    v
 *   [ph: BEAU; ph: BEAU]
 *  DD:
 *     ^    v
 *   [ph: BEAU; ph: BEAU]
 */
@RunWith(value=JDoctestRunner.class)
public class MatcherList {
    private MatcherList() {}

    // universal matcher
    public static final Matcher ANY = new Matcher() {
        public FormationMatch match(Formation f) throws NoMatchException {
            TaggedFormation tf = TaggedFormation.coerce(f);
            Formation meta = FormationList.SINGLE_DANCER;
	    Dancer metaDancer = meta.dancers().iterator().next();
	    return new FormationMatch(meta, m(p(metaDancer, tf)),
				      Collections.<Dancer>emptySet());
        }
        public String getName() { return "ANY"; }
    };
    // 0-person matchers
    public static final Matcher NONE = new Matcher() {
        public FormationMatch match(Formation f) throws NoMatchException {
            throw new NoMatchException("NONE", "NONE matcher used");
        }
        public String getName() { return "NONE"; }
    };
    // 1-person matchers
    public static final Matcher SINGLE_DANCER =
        GeneralFormationMatcher.makeMatcher(FormationList.SINGLE_DANCER);
    // 2-person matchers
    public static final Matcher GENERAL_PARTNERS =
        GeneralFormationMatcher.makeMatcher(FormationList.GENERAL_PARTNERS);
    // this matcher has an extra underscore because the parser treats it
    // as two items: the number one, and the identifier x2.
    public static final Matcher _1_X2 =
        Tagger.autotag2
            (GeneralFormationMatcher.makeMatcher(FormationList._1x2));
    public static final Matcher COUPLE = 
        GeneralFormationMatcher.makeMatcher(FormationList.COUPLE);
    public static final Matcher FACING_DANCERS =
        GeneralFormationMatcher.makeMatcher(FormationList.FACING_DANCERS);
    public static final Matcher BACK_TO_BACK_DANCERS =
        GeneralFormationMatcher.makeMatcher(FormationList.BACK_TO_BACK_DANCERS);
    public static final Matcher TANDEM =
        GeneralFormationMatcher.makeMatcher(FormationList.TANDEM);
    public static final Matcher RH_MINIWAVE =
        GeneralFormationMatcher.makeMatcher(FormationList.RH_MINIWAVE);
    public static final Matcher LH_MINIWAVE =
        GeneralFormationMatcher.makeMatcher(FormationList.LH_MINIWAVE);
    public static final Matcher MINIWAVE =
        OR("MINIWAVE", RH_MINIWAVE, LH_MINIWAVE);
    // 4-person matchers
    public static final Matcher GENERAL_LINE =
        Tagger.autotag2
            (GeneralFormationMatcher.makeMatcher(FormationList.GENERAL_LINE));
    // this matcher has an extra underscore because the parser treats it
    // as two items: the number one, and the identifier x4.
    public static final Matcher _1_X4 =
        Tagger.autotag2
            (GeneralFormationMatcher.makeMatcher(FormationList._1x4));
    // this matcher has an extra underscore because the parser treats it
    // as two items: the number two, and the identifier x2.
    public static final Matcher _2_X2 =
        Tagger.autotag2
            (GeneralFormationMatcher.makeMatcher(FormationList._2x2));
    public static final Matcher SINGLE_STATIC_SQUARE =
        GeneralFormationMatcher.makeMatcher(FormationList.SINGLE_STATIC_SQUARE);
    public static final Matcher FACING_COUPLES =
        GeneralFormationMatcher.makeMatcher(FormationList.FACING_COUPLES);
    public static final Matcher BACK_TO_BACK_COUPLES =
        GeneralFormationMatcher.makeMatcher(FormationList.BACK_TO_BACK_COUPLES);
    public static final Matcher TANDEM_COUPLES =
        GeneralFormationMatcher.makeMatcher(FormationList.TANDEM_COUPLES);
    public static final Matcher RH_OCEAN_WAVE =
        GeneralFormationMatcher.makeMatcher(FormationList.RH_OCEAN_WAVE);
    public static final Matcher LH_OCEAN_WAVE =
        GeneralFormationMatcher.makeMatcher(FormationList.LH_OCEAN_WAVE);
    public static final Matcher OCEAN_WAVE =
        OR("OCEAN WAVE", RH_OCEAN_WAVE, LH_OCEAN_WAVE);
    public static final Matcher RH_BOX =
        GeneralFormationMatcher.makeMatcher(FormationList.RH_BOX);
    public static final Matcher LH_BOX =
        GeneralFormationMatcher.makeMatcher(FormationList.LH_BOX);
    public static final Matcher BOX =
        OR("BOX", RH_BOX, LH_BOX);
    public static final Matcher RH_IN_PINWHEEL =
        GeneralFormationMatcher.makeMatcher(FormationList.RH_IN_PINWHEEL);
    public static final Matcher LH_IN_PINWHEEL =
        GeneralFormationMatcher.makeMatcher(FormationList.LH_IN_PINWHEEL);
    public static final Matcher RH_OUT_PINWHEEL =
        GeneralFormationMatcher.makeMatcher(FormationList.RH_OUT_PINWHEEL);
    public static final Matcher LH_OUT_PINWHEEL =
        GeneralFormationMatcher.makeMatcher(FormationList.LH_OUT_PINWHEEL);
    public static final Matcher RH_SINGLE_QUARTER_ZEE =
        GeneralFormationMatcher.makeMatcher(FormationList.RH_SINGLE_QUARTER_ZEE);
    public static final Matcher LH_SINGLE_QUARTER_ZEE =
        GeneralFormationMatcher.makeMatcher(FormationList.LH_SINGLE_QUARTER_ZEE);
    public static final Matcher SINGLE_QUARTER_ZEE =
        OR("SINGLE 1/4 ZEE", RH_SINGLE_QUARTER_ZEE, LH_SINGLE_QUARTER_ZEE);
    public static final Matcher RH_SINGLE_THREE_QUARTER_ZEE =
        GeneralFormationMatcher.makeMatcher(FormationList.RH_SINGLE_THREE_QUARTER_ZEE);
    public static final Matcher LH_SINGLE_THREE_QUARTER_ZEE =
        GeneralFormationMatcher.makeMatcher(FormationList.LH_SINGLE_THREE_QUARTER_ZEE);
    public static final Matcher SINGLE_THREE_QUARTER_ZEE =
        OR("SINGLE 3/4 ZEE", RH_SINGLE_THREE_QUARTER_ZEE, LH_SINGLE_THREE_QUARTER_ZEE);
    public static final Matcher RH_TWO_FACED_LINE =
        GeneralFormationMatcher.makeMatcher(FormationList.RH_TWO_FACED_LINE);
    public static final Matcher LH_TWO_FACED_LINE =
        GeneralFormationMatcher.makeMatcher(FormationList.LH_TWO_FACED_LINE);
    public static final Matcher TWO_FACED_LINE =
        OR("TWO-FACED LINE", RH_TWO_FACED_LINE, LH_TWO_FACED_LINE);
    public static final Matcher SINGLE_INVERTED_LINE =
        GeneralFormationMatcher.makeMatcher(FormationList.SINGLE_INVERTED_LINE);
    public static final Matcher RH_DIAMOND =
        GeneralFormationMatcher.makeMatcher(FormationList.RH_DIAMOND);
    public static final Matcher RH_FACING_DIAMOND =
        GeneralFormationMatcher.makeMatcher(FormationList.RH_FACING_DIAMOND);
    public static final Matcher LH_DIAMOND =
        GeneralFormationMatcher.makeMatcher(FormationList.LH_DIAMOND);
    public static final Matcher LH_FACING_DIAMOND =
        GeneralFormationMatcher.makeMatcher(FormationList.LH_FACING_DIAMOND);
    public static final Matcher FACING_DIAMOND =
        OR("FACING DIAMOND", RH_FACING_DIAMOND, LH_FACING_DIAMOND);
    public static final Matcher MIXED_FACING_DIAMOND =
        mixed("MIXED FACING DIAMOND", FormationList.RH_FACING_DIAMOND, FormationList.LH_FACING_DIAMOND);
    public static final Matcher DIAMOND =
        OR("DIAMOND", RH_DIAMOND, LH_DIAMOND, RH_FACING_DIAMOND, LH_FACING_DIAMOND);
    public static final Matcher MIXED_DIAMOND =
        mixed("MIXED DIAMOND", FormationList.RH_DIAMOND, FormationList.LH_DIAMOND, FormationList.RH_FACING_DIAMOND, FormationList.LH_FACING_DIAMOND);
    public static final Matcher RH_STAR =
        GeneralFormationMatcher.makeMatcher(FormationList.RH_STAR);
    public static final Matcher LH_STAR =
        GeneralFormationMatcher.makeMatcher(FormationList.LH_STAR);
    public static final Matcher RH_SINGLE_PROMENADE =
        GeneralFormationMatcher.makeMatcher(FormationList.RH_SINGLE_PROMENADE);
    public static final Matcher LH_SINGLE_PROMENADE =
        GeneralFormationMatcher.makeMatcher(FormationList.LH_SINGLE_PROMENADE);
    public static final Matcher RH_SINGLE_QUARTER_TAG =
        GeneralFormationMatcher.makeMatcher(FormationList.RH_SINGLE_QUARTER_TAG);
    public static final Matcher LH_SINGLE_QUARTER_TAG =
        GeneralFormationMatcher.makeMatcher(FormationList.LH_SINGLE_QUARTER_TAG);
    public static final Matcher SINGLE_QUARTER_TAG =
        OR("SINGLE 1/4 TAG", RH_SINGLE_QUARTER_TAG, LH_SINGLE_QUARTER_TAG);
    public static final Matcher RH_SINGLE_THREE_QUARTER_TAG =
        GeneralFormationMatcher.makeMatcher(FormationList.RH_SINGLE_THREE_QUARTER_TAG);
    public static final Matcher LH_SINGLE_THREE_QUARTER_TAG =
        GeneralFormationMatcher.makeMatcher(FormationList.LH_SINGLE_THREE_QUARTER_TAG);
    public static final Matcher SINGLE_THREE_QUARTER_TAG =
        OR("SINGLE 3/4 TAG", RH_SINGLE_THREE_QUARTER_TAG, LH_SINGLE_THREE_QUARTER_TAG);

    // 8-person matchers

    // this matcher has an extra underscore because the parser treats it
    // as two items: the number one, and the identifier x8.
    public static final Matcher _1_X8 =
        Tagger.autotag4
            (GeneralFormationMatcher.makeMatcher(FormationList._1x8));
    // this matcher has an extra underscore because the parser treats it
    // as two items: the number two, and the identifier x4.
    public static final Matcher _2_X4 =
        Tagger.autotag4
            (GeneralFormationMatcher.makeMatcher(FormationList._2x4));
    public static final Matcher PARALLEL_GENERAL_LINES =
        Tagger.autotag4
            (GeneralFormationMatcher.makeMatcher(FormationList.PARALLEL_GENERAL_LINES));
    public static final Matcher GENERAL_COLUMNS =
        Tagger.autotag4
            (GeneralFormationMatcher.makeMatcher(FormationList.GENERAL_COLUMNS));
    public static final Matcher STATIC_SQUARE =
        GeneralFormationMatcher.makeMatcher(FormationList.STATIC_SQUARE);
    public static final Matcher STATIC_SQUARE_FACING_OUT =
        GeneralFormationMatcher.makeMatcher(FormationList.STATIC_SQUARE_FACING_OUT);
    public static final Matcher SINGLE_FILE_PROMENADE =
        GeneralFormationMatcher.makeMatcher(FormationList.SINGLE_FILE_PROMENADE);
    public static final Matcher REVERSE_SINGLE_FILE_PROMENADE =
        GeneralFormationMatcher.makeMatcher(FormationList.REVERSE_SINGLE_FILE_PROMENADE);
    public static final Matcher RH_ALAMO_RING =
        GeneralFormationMatcher.makeMatcher(FormationList.RH_ALAMO_RING);
    public static final Matcher LH_ALAMO_RING =
        GeneralFormationMatcher.makeMatcher(FormationList.LH_ALAMO_RING);
    public static final Matcher O_SPOTS =
        GeneralFormationMatcher.makeMatcher(FormationList.O_SPOTS);
    public static final Matcher PROMENADE =
        GeneralFormationMatcher.makeMatcher(FormationList.PROMENADE);
    public static final Matcher WRONG_WAY_PROMENADE =
        GeneralFormationMatcher.makeMatcher(FormationList.WRONG_WAY_PROMENADE);
    public static final Matcher STAR_PROMENADE =
        GeneralFormationMatcher.makeMatcher(FormationList.STAR_PROMENADE);
    public static final Matcher WRONG_WAY_STAR_PROMENADE =
        GeneralFormationMatcher.makeMatcher(FormationList.WRONG_WAY_STAR_PROMENADE);
    public static final Matcher THAR =
        GeneralFormationMatcher.makeMatcher(FormationList.THAR);
    public static final Matcher WRONG_WAY_THAR =
        GeneralFormationMatcher.makeMatcher(FormationList.WRONG_WAY_THAR);
    public static final Matcher RIGHT_AND_LEFT_GRAND =
        GeneralFormationMatcher.makeMatcher(FormationList.RIGHT_AND_LEFT_GRAND);
    public static final Matcher RIGHT_AND_LEFT_GRAND_DIAMOND =
        GeneralFormationMatcher.makeMatcher(FormationList.RIGHT_AND_LEFT_GRAND_DIAMOND);
    public static final Matcher LEFT_AND_RIGHT_GRAND =
        GeneralFormationMatcher.makeMatcher(FormationList.LEFT_AND_RIGHT_GRAND);
    public static final Matcher FACING_LINES =
        GeneralFormationMatcher.makeMatcher(FormationList.FACING_LINES);
    public static final Matcher EIGHT_CHAIN_THRU =
        GeneralFormationMatcher.makeMatcher(FormationList.EIGHT_CHAIN_THRU);
    public static final Matcher TRADE_BY =
        GeneralFormationMatcher.makeMatcher(FormationList.TRADE_BY);
    public static final Matcher DOUBLE_PASS_THRU =
        GeneralFormationMatcher.makeMatcher(FormationList.DOUBLE_PASS_THRU);
    public static final Matcher SINGLE_DOUBLE_PASS_THRU =
        GeneralFormationMatcher.makeMatcher(FormationList.SINGLE_DOUBLE_PASS_THRU);
    public static final Matcher COMPLETED_DOUBLE_PASS_THRU =
        GeneralFormationMatcher.makeMatcher(FormationList.COMPLETED_DOUBLE_PASS_THRU);
    public static final Matcher COMPLETED_SINGLE_DOUBLE_PASS_THRU =
        GeneralFormationMatcher.makeMatcher(FormationList.COMPLETED_SINGLE_DOUBLE_PASS_THRU);
    public static final Matcher LINES_FACING_OUT =
        GeneralFormationMatcher.makeMatcher(FormationList.LINES_FACING_OUT);
    public static final Matcher PARALLEL_RH_WAVES =
        GeneralFormationMatcher.makeMatcher(FormationList.PARALLEL_RH_WAVES);
    public static final Matcher PARALLEL_LH_WAVES =
        GeneralFormationMatcher.makeMatcher(FormationList.PARALLEL_LH_WAVES);
    public static final Matcher PARALLEL_WAVES =
        OR("PARALLEL WAVES", PARALLEL_RH_WAVES, PARALLEL_LH_WAVES);
    public static final Matcher PARALLEL_RH_TWO_FACED_LINES =
        GeneralFormationMatcher.makeMatcher(FormationList.PARALLEL_RH_TWO_FACED_LINES);
    public static final Matcher PARALLEL_LH_TWO_FACED_LINES =
        GeneralFormationMatcher.makeMatcher(FormationList.PARALLEL_LH_TWO_FACED_LINES);
    public static final Matcher PARALLEL_TWO_FACED_LINES =
        OR("PARALLEL TWO-FACED LINES", PARALLEL_RH_TWO_FACED_LINES, PARALLEL_LH_TWO_FACED_LINES);
    public static final Matcher RH_COLUMN =
        GeneralFormationMatcher.makeMatcher(FormationList.RH_COLUMN);
    public static final Matcher LH_COLUMN =
        GeneralFormationMatcher.makeMatcher(FormationList.LH_COLUMN);
    public static final Matcher COLUMN =
        OR("COLUMN", RH_COLUMN, LH_COLUMN);
    public static final Matcher ENDS_IN_INVERTED_LINES =
        GeneralFormationMatcher.makeMatcher(FormationList.ENDS_IN_INVERTED_LINES);
    public static final Matcher ENDS_OUT_INVERTED_LINES =
        GeneralFormationMatcher.makeMatcher(FormationList.ENDS_OUT_INVERTED_LINES);
    public static final Matcher INVERTED_LINES =
	OR("INVERTED LINES", ENDS_IN_INVERTED_LINES, ENDS_OUT_INVERTED_LINES);
    public static final Matcher RH_QUARTER_TAG =
        GeneralFormationMatcher.makeMatcher(FormationList.RH_QUARTER_TAG);
    public static final Matcher LH_QUARTER_TAG =
        GeneralFormationMatcher.makeMatcher(FormationList.LH_QUARTER_TAG);
    public static final Matcher QUARTER_TAG =
        OR("1/4 TAG", RH_QUARTER_TAG, LH_QUARTER_TAG);
    public static final Matcher RH_THREE_QUARTER_TAG =
        GeneralFormationMatcher.makeMatcher(FormationList.RH_THREE_QUARTER_TAG);
    public static final Matcher LH_THREE_QUARTER_TAG =
        GeneralFormationMatcher.makeMatcher(FormationList.LH_THREE_QUARTER_TAG);
    public static final Matcher THREE_QUARTER_TAG =
        OR("3/4 TAG", RH_THREE_QUARTER_TAG, LH_THREE_QUARTER_TAG);
    public static final Matcher RH_QUARTER_LINE =
        GeneralFormationMatcher.makeMatcher(FormationList.RH_QUARTER_LINE);
    public static final Matcher LH_QUARTER_LINE =
        GeneralFormationMatcher.makeMatcher(FormationList.LH_QUARTER_LINE);
    public static final Matcher QUARTER_LINE =
        OR("1/4 LINE", RH_QUARTER_LINE, LH_QUARTER_LINE);
    public static final Matcher RH_THREE_QUARTER_LINE =
        GeneralFormationMatcher.makeMatcher(FormationList.RH_THREE_QUARTER_LINE);
    public static final Matcher LH_THREE_QUARTER_LINE =
        GeneralFormationMatcher.makeMatcher(FormationList.LH_THREE_QUARTER_LINE);
    public static final Matcher THREE_QUARTER_LINE =
        OR("3/4 LINE", RH_THREE_QUARTER_LINE, LH_THREE_QUARTER_LINE);
    public static final Matcher RH_TWIN_DIAMONDS =
        GeneralFormationMatcher.makeMatcher(FormationList.RH_TWIN_DIAMONDS);
    public static final Matcher LH_TWIN_DIAMONDS =
        GeneralFormationMatcher.makeMatcher(FormationList.LH_TWIN_DIAMONDS);
    public static final Matcher TWIN_DIAMONDS =
        OR("TWIN DIAMONDS", RH_TWIN_DIAMONDS, LH_TWIN_DIAMONDS);
    public static final Matcher RH_POINT_TO_POINT_DIAMONDS =
        GeneralFormationMatcher.makeMatcher(FormationList.RH_POINT_TO_POINT_DIAMONDS);
    public static final Matcher LH_POINT_TO_POINT_DIAMONDS =
        GeneralFormationMatcher.makeMatcher(FormationList.LH_POINT_TO_POINT_DIAMONDS);
    public static final Matcher POINT_TO_POINT_DIAMONDS =
        OR("POINT-TO-POINT DIAMONDS", RH_POINT_TO_POINT_DIAMONDS, LH_POINT_TO_POINT_DIAMONDS);
    public static final Matcher RH_POINT_TO_POINT_FACING_DIAMONDS =
        GeneralFormationMatcher.makeMatcher(FormationList.RH_POINT_TO_POINT_FACING_DIAMONDS);
    public static final Matcher LH_POINT_TO_POINT_FACING_DIAMONDS =
        GeneralFormationMatcher.makeMatcher(FormationList.LH_POINT_TO_POINT_FACING_DIAMONDS);
    public static final Matcher POINT_TO_POINT_FACING_DIAMONDS =
        OR("POINT-TO-POINT FACING DIAMONDS", RH_POINT_TO_POINT_FACING_DIAMONDS, LH_POINT_TO_POINT_FACING_DIAMONDS);
    public static final Matcher RH_TWIN_FACING_DIAMONDS =
        GeneralFormationMatcher.makeMatcher(FormationList.RH_TWIN_FACING_DIAMONDS);
    public static final Matcher LH_TWIN_FACING_DIAMONDS =
        GeneralFormationMatcher.makeMatcher(FormationList.LH_TWIN_FACING_DIAMONDS);
    public static final Matcher TWIN_FACING_DIAMONDS =
        OR("TWIN FACING DIAMONDS", RH_TWIN_FACING_DIAMONDS, LH_TWIN_FACING_DIAMONDS);
    public static final Matcher RH_TIDAL_WAVE =
        GeneralFormationMatcher.makeMatcher(FormationList.RH_TIDAL_WAVE);
    public static final Matcher LH_TIDAL_WAVE =
        GeneralFormationMatcher.makeMatcher(FormationList.LH_TIDAL_WAVE);
    public static final Matcher TIDAL_WAVE =
        OR("TIDAL WAVE", RH_TIDAL_WAVE, LH_TIDAL_WAVE);
    public static final Matcher RH_TIDAL_TWO_FACED_LINE =
        GeneralFormationMatcher.makeMatcher(FormationList.RH_TIDAL_TWO_FACED_LINE);
    public static final Matcher LH_TIDAL_TWO_FACED_LINE =
        GeneralFormationMatcher.makeMatcher(FormationList.LH_TIDAL_TWO_FACED_LINE);
    public static final Matcher TIDAL_TWO_FACED_LINE =
        OR("TIDAL TWO-FACED LINE", RH_TIDAL_TWO_FACED_LINE, LH_TIDAL_TWO_FACED_LINE);
    public static final Matcher RH_TIDAL_LINE =
        GeneralFormationMatcher.makeMatcher(FormationList.RH_TIDAL_LINE);
    public static final Matcher LH_TIDAL_LINE =
        GeneralFormationMatcher.makeMatcher(FormationList.LH_TIDAL_LINE);
    public static final Matcher TIDAL_LINE =
        OR("TIDAL LINE", RH_TIDAL_LINE, LH_TIDAL_LINE);
    public static final Matcher O_DOUBLE_PASS_THRU =
        GeneralFormationMatcher.makeMatcher(FormationList.O_DOUBLE_PASS_THRU);
    public static final Matcher BUTTERFLY_DOUBLE_PASS_THRU =
        GeneralFormationMatcher.makeMatcher(FormationList.BUTTERFLY_DOUBLE_PASS_THRU);

    // special purpose matcher
    public static final Matcher CENTER_2 = new CenterMatcher(2);
    public static final Matcher CENTER_4 = new CenterMatcher(4);
    public static final Matcher CENTER_6 = new CenterMatcher(6);
    public static final Matcher CENTER_HALF = new CenterMatcher();
    /**
     * Algorithmically select the center N dancers from a formation.
     * @doc.test Finding the centers of a 4-person formation:
     *  js> FormationList = FormationList.js(this); undefined;
     *  js> SD = StandardDancer; undefined
     *  js> // rotate the formation 1/2 just to get rid of the original tags
     *  js> f = FormationList.RH_OCEAN_WAVE.rotate(ExactRotation.ONE_HALF); f.
     *    >     toStringDiagram()
     *  ^    v    ^    v
     *  js> // label those dancers
     *  js> f= f.map(SD.COUPLE_1_BOY, SD.COUPLE_1_GIRL,
     *    >          SD.COUPLE_3_GIRL, SD.COUPLE_3_BOY); f.toStringDiagram()
     *  1B^  1Gv  3G^  3Bv
     *  js> MatcherList.CENTER_HALF.match(f)
     *  AA^
     *  AA:
     *     1B^  1Gv  3G^  3Bv
     *   [1G: CENTER; 3G: CENTER]
     *  js> MatcherList.CENTER_2.match(f)
     *  AA^
     *  AA:
     *     1B^  1Gv  3G^  3Bv
     *   [1G: CENTER; 3G: CENTER]
     *  js> MatcherList.CENTER_4.match(f)
     *  AA^
     *  AA:
     *     1B^  1Gv  3G^  3Bv
     *   [1B: CENTER; 1G: CENTER; 3G: CENTER; 3B: CENTER]
     *  js> try { MatcherList.CENTER_6.match(f); } catch (e) { print (e.javaException); }
     *  net.cscott.sdr.calls.NoMatchException: No match for CENTER(6): Can't find 6 center dancers
     * @doc.test Finding the centers of an 8-person formation:
     *  js> FormationList = FormationList.js(this); undefined;
     *  js> SD = StandardDancer; undefined
     *  js> // rotate the formation 1/2 just to get rid of the original tags
     *  js> f = FormationList.RH_QUARTER_TAG.rotate(ExactRotation.ONE_HALF); f.
     *    >     toStringDiagram()
     *       v    v
     *  
     *  ^    v    ^    v
     *  
     *       ^    ^
     *  js> // label those dancers
     *  js> f= f.map(SD.COUPLE_1_BOY, SD.COUPLE_1_GIRL,
     *    >          SD.COUPLE_2_BOY, SD.COUPLE_2_GIRL, SD.COUPLE_4_GIRL, SD.COUPLE_4_BOY,
     *    >          SD.COUPLE_3_GIRL, SD.COUPLE_3_BOY); f.toStringDiagram()
     *       1Bv  1Gv
     *  
     *  2B^  2Gv  4G^  4Bv
     *  
     *       3G^  3B^
     *  js> MatcherList.CENTER_HALF.match(f)
     *  AA^
     *  AA:
     *          1Bv  1Gv
     *     
     *     2B^  2Gv  4G^  4Bv
     *     
     *          3G^  3B^
     *   [2B: CENTER; 2G: CENTER; 4G: CENTER; 4B: CENTER]
     *  js> MatcherList.CENTER_2.match(f)
     *  AA^
     *  AA:
     *          1Bv  1Gv
     *     
     *     2B^  2Gv  4G^  4Bv
     *     
     *          3G^  3B^
     *   [2G: CENTER; 4G: CENTER]
     *  js> MatcherList.CENTER_4.match(f)
     *  AA^
     *  AA:
     *          1Bv  1Gv
     *     
     *     2B^  2Gv  4G^  4Bv
     *     
     *          3G^  3B^
     *   [2B: CENTER; 2G: CENTER; 4G: CENTER; 4B: CENTER]
     *  js> MatcherList.CENTER_6.match(f)
     *  AA^
     *  AA:
     *          1Bv  1Gv
     *     
     *     2B^  2Gv  4G^  4Bv
     *     
     *          3G^  3B^
     *   [1B: CENTER; 1G: CENTER; 2G: CENTER; 4G: CENTER; 3G: CENTER; 3B: CENTER]
     */
    private static class CenterMatcher extends Matcher {
        private final boolean half;
        private final int howMany;
        /** Select the center "half" of the formation. */
        CenterMatcher() {
            this.half = true;
            this.howMany = 0;
        }
        /** Select the center N dancers of the formation. */
        CenterMatcher(int i) {
            this.half = false;
            this.howMany = i;
        }
        @Override
        public FormationMatch match(Formation f) throws NoMatchException {
            int n = this.howMany;
            if (half) {
                assert (f.dancers().size() % 2) == 0;
                n = f.dancers().size() / 2;
            }
            // ok, order dancers by distance from the center
            // now take dancers from center out, skipping an entire group
            // if we can't take them all w/o going over 'n'
            // (ie, in an hourglass, the box points might be nearer than the
            //  diamond points, but we can't take them all w/o going over
            //  half the dancers)
            MultiMap<Fraction,Dancer> mm = new GenericMultiMap<Fraction,Dancer>
                (Factories.<Fraction,Collection<Dancer>>treeMapFactory(),
                 Factories.<Dancer>linkedHashSetFactory());
            for (Dancer d: f.dancers()) {
                Position p = f.location(d);
                Fraction dist2 = (p.x.multiply(p.x)).add(p.y.multiply(p.y));
                mm.add(dist2, d);
            }
            Set<Dancer> centerDancers = new LinkedHashSet<Dancer>();
            for (Fraction dist2 : mm.keySet()) {
                Collection<Dancer> group = mm.getValues(dist2);
                if (centerDancers.size()+group.size() > n)
                    continue; // skip this group
                centerDancers.addAll(group);
            }
            if (centerDancers.size() != n)
                throw new NoMatchException("CENTER("+n+")",
                                           "Can't find "+n+" center dancers");
            // ok, apply the tags
            MultiMap<Dancer,Tag> newTags = new GenericMultiMap<Dancer,Tag>();
            for (Dancer d : centerDancers)
                newTags.add(d, Tag.CENTER);
            TaggedFormation tf = new TaggedFormation(f, newTags);
            Formation meta = FormationList.SINGLE_DANCER;
            return new FormationMatch
                (meta,
                 Tools.m(Tools.p(meta.dancers().iterator().next(), tf)),
                 Collections.<Dancer>emptySet());
        }
        public String getName() {
            if (half) return "CENTER HALF";
            return "CENTER("+howMany+")";
        }
    }

    /** Siamese matcher.  Used in Siamese concept (C1). */
    public static final Matcher SIAMESE =
        GeneralFormationMatcher.makeMatcher(FormationList.COUPLE,
                                             FormationList.TANDEM);

    // matcher combinators
    /**
     * The {@link #OR} function creates a Matcher which matches any one of
     * the given alternatives.
     * @doc.test Diamonds or quarter tag:
     *  js> FormationList = FormationList.js(this); undefined;
     *  js> sel = MatcherList.OR("OR(RH BOX,RH DIAMOND)", MatcherList.RH_BOX, MatcherList.RH_DIAMOND)
     *  OR(RH BOX,RH DIAMOND)
     *  js> sel.match(FormationList.RH_BOX)                                    
     *  AA^
     *  AA:
     *     ^    v
     *     
     *     ^    v
     *   [ph: BEAU,LEADER; ph: BEAU,TRAILER; ph: BEAU,TRAILER; ph: BEAU,LEADER]
     *  js> sel.match(FormationList.RH_DIAMOND)
     *  AA^
     *  AA:
     *       >
     *     
     *     ^    v
     *     
     *       <
     *   [ph: POINT; ph: BEAU,CENTER; ph: BEAU,CENTER; ph: POINT]
     */
    public static Matcher OR(String name, Matcher... alternatives) {
        return OR(name, Arrays.asList(alternatives));
    }
    public static Matcher OR(final String name,
                             final List<Matcher> alternatives) {
        return new Matcher() {
            @Override
            public FormationMatch match(Formation f) throws NoMatchException {
                List<String> reasons = new ArrayList<String>(3);
                for (Matcher s : alternatives) {
                    try {
                        return s.match(f);
                    } catch (NoMatchException e) {
                        /* try next matcher */
                        reasons.add(e.target+"("+e.reason+")");
                    }
                }
                // no matches in any matcher
                throw new NoMatchException(name, ListUtils.join(reasons, ", "));
            }
            @Override
            public String getName() { return name; }
        };
    }
    public static Matcher mixed(String name, TaggedFormation... goals) {
        return GeneralFormationMatcher.makeMatcher(name, Arrays.asList(goals));
    }
    public static ExprFunc<Matcher> _MIXED = new ExprFunc<Matcher>() {
        @Override
        public String getName() { return "mixed"; }
        @Override
        public Matcher evaluate(Class<? super Matcher> type,
                                DanceState ds, List<Expr> args)
            throws EvaluationException {
            // parse the argument list as formations
            assert args.size() > 0;
            List<TaggedFormation> goals = new ArrayList<TaggedFormation>();
            for (Expr e : args)
                goals.add(e.evaluate(TaggedFormation.class, ds));
            // XXX if args are constant, cache Matcher
            return GeneralFormationMatcher.makeMatcher
                (goals.toArray(new TaggedFormation[goals.size()]));
        }
        @Override
        public boolean isConstant(Class<? super Matcher> type, List<Expr> args){
            for (Expr e : args)
                if (!e.isConstant(TaggedFormation.class))
                    return false;
            return true;
        }
    };
    public static ExprFunc<Matcher> _OR = new ExprFunc<Matcher>(){
        @Override
        public String getName() { return "or"; }
        @Override
        public Matcher evaluate(Class<? super Matcher> type,
                                DanceState ds, List<Expr> args)
            throws EvaluationException {
            List<Matcher> ml = new ArrayList<Matcher>(args.size());
            for (Expr alternative : args)
                ml.add(alternative.evaluate(Matcher.class, ds));
            String name = new Expr(getName(), args).toShortString();
            return OR(name, ml);
        }
        @Override
        public boolean isConstant(Class<? super Matcher> type, List<Expr> args){
            for (Expr e : args)
                if (!e.isConstant(Matcher.class))
                    return false;
            return true;
        }
    };
    public static ExprFunc<Matcher> _CENTER =
        new ExprFunc<Matcher>() {
        @Override
        public String getName() { return "center"; }
        @Override
        public Matcher evaluate(Class<? super Matcher> type,
                                DanceState ds, List<Expr> args)
            throws EvaluationException {
            assert args.size()==1;
            Fraction n = args.get(0).evaluate(Fraction.class, ds);
            if (n.getDenominator()!=1 || n.compareTo(Fraction.ZERO) <= 0)
                throw new EvaluationException
                     ("Can't match "+n.toProperString()+" dancers");
            return new CenterMatcher(n.intValue());
        }
        @Override
        public boolean isConstant(Class<? super Matcher> type, List<Expr> args){
            assert args.size()==1;
            return args.get(0).isConstant(type);
        }
    };

    /** Support circle choreography by matching a squared set
     * "the other way".
     * @doc.test Apply CIRCLE_ADJUST matcher to a SQUARED_SET:
     *  js> MatcherList.CIRCLE_ADJUST.match(Formation.SQUARED_SET);
     *  AA7
     *  AA:
     *          3Bv  2Gv
     *     
     *     3G>            2B<
     *     
     *     4B>            1G<
     *     
     *          4G^  1B^
     * @doc.test "Do nothing" after the circle adjust, to show what formation
     *  child calls will see.
     *  js> comp = net.cscott.sdr.calls.ast.AstNode.valueOf(
     *    >        "(Opt (From 'CIRCLE ADJUST (Seq (Apply 'nothing))))");
     *  (Opt (From 'CIRCLE ADJUST (Seq (Apply 'nothing))))
     *  js> ds = new DanceState(new DanceProgram(Program.BASIC), Formation.SQUARED_SET); undefined;
     *  js> e = new Evaluator.Standard(comp); e.evaluateAll(ds);
     *  js> ds.currentFormation().toStringDiagram("|");
     *  |     3GQ       3BL
     *  |
     *  |4BQ                 2GL
     *  |
     *  |
     *  |
     *  |4G7                 2B`
     *  |
     *  |     1B7       1G`
     */
    public static final Matcher CIRCLE_ADJUST = new Matcher() {
        @Override
        public String getName() { return "_circle adjust"; }
        @Override
        public FormationMatch match(Formation f) throws NoMatchException {
            /*
             *    0 1
             *  2     3
             *  4     5
             *    6 7
             */
            int[] r = new int[] { 1, 3, 0, 5, 2, 7, 4, 6 };
            FormationMatch fm = MatcherList.O_SPOTS.match(f);
            assert fm.meta.dancers().size() == 1;
            Dancer metaD = fm.meta.dancers().iterator().next();
            TaggedFormation from = fm.matches.get(metaD);
            List<Dancer> sortedDancers = from.sortedDancers();
            assert sortedDancers.size() == r.length;
            Map<Dancer,Dancer> rotated = new HashMap<Dancer,Dancer>();
            for (int i=0 ; i < sortedDancers.size() ; i++) {
                rotated.put(sortedDancers.get(i), sortedDancers.get(r[i]));
            }
            TaggedFormation to = from.map(rotated);
            Position p = fm.meta.location(metaD)
                .turn(Fraction.ONE_EIGHTH, false);
            return new FormationMatch
                (new Formation(m(p(metaD, p))), m(p(metaD, to)),
                 Collections.<Dancer>emptySet());
        }
    };

    /** Parse names of {@link Matcher} combination functions. */
    public static ExprFunc<Matcher> valueOf(String s) {
        s = s.toLowerCase().intern();
        if (s == "mixed")
            return _MIXED;
        if (s == "or")
            return _OR;
        if (s == "center")
            return _CENTER;
        throw new IllegalArgumentException("No such Matcher function");
    }
    // unimplemented matchers
    /** Stub for not-yet-implemented {@link Matcher}s. */
    private static final Matcher _STUB_ = new Matcher() {
        @Override
        public FormationMatch match(Formation f) throws NoMatchException {
            throw new NoMatchException(f.toString(), "Unimplemented");
        }
        @Override
        public String getName() { return "*STUB*"; }
    };
    public static final Matcher LH_3_AND_1 = _STUB_;
    public static final Matcher LH_SPLIT_3_AND_1 = _STUB_;
    public static final Matcher RH_3_AND_1 = _STUB_;
    public static final Matcher RH_SPLIT_3_AND_1 = _STUB_;
}
