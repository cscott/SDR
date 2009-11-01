package net.cscott.sdr.calls;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.cscott.sdr.calls.TaggedFormation.Tag;
import static net.cscott.sdr.calls.TaggedFormation.TaggedDancerInfo;
import net.cscott.sdr.util.Fraction;

/** Compiled version of {@link FormationListSlow}. */
abstract class FormationListFast {
    public static final NamedTaggedFormation SINGLE_DANCER =
        new NamedTaggedFormation("SINGLE DANCER",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ZERO, Fraction.ZERO, ExactRotation.NORTH)));

    public static final NamedTaggedFormation GENERAL_PARTNERS =
        new NamedTaggedFormation("GENERAL PARTNERS",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.ZERO, Rotation.fromAbsoluteString("|"))),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.ZERO, Rotation.fromAbsoluteString("|"))));

    public static final NamedTaggedFormation COUPLE =
        new NamedTaggedFormation("COUPLE",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.ZERO, ExactRotation.NORTH), Tag.BEAU),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.ZERO, ExactRotation.NORTH), Tag.BELLE));

    public static final NamedTaggedFormation FACING_DANCERS =
        new NamedTaggedFormation("FACING DANCERS",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ZERO, Fraction.ONE, ExactRotation.SOUTH), Tag.TRAILER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ZERO, Fraction.mONE, ExactRotation.NORTH), Tag.TRAILER));

    public static final NamedTaggedFormation BACK_TO_BACK_DANCERS =
        new NamedTaggedFormation("BACK TO BACK DANCERS",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ZERO, Fraction.ONE, ExactRotation.NORTH), Tag.LEADER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ZERO, Fraction.mONE, ExactRotation.SOUTH), Tag.LEADER));

    public static final NamedTaggedFormation TANDEM =
        new NamedTaggedFormation("TANDEM",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ZERO, Fraction.ONE, ExactRotation.NORTH), Tag.LEADER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ZERO, Fraction.mONE, ExactRotation.NORTH), Tag.TRAILER));

    public static final NamedTaggedFormation RH_MINIWAVE =
        new NamedTaggedFormation("RH MINIWAVE",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.ZERO, ExactRotation.NORTH), Tag.BEAU),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.ZERO, ExactRotation.SOUTH), Tag.BEAU));

    public static final NamedTaggedFormation LH_MINIWAVE =
        new NamedTaggedFormation("LH MINIWAVE",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.ZERO, ExactRotation.SOUTH), Tag.BELLE),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.ZERO, ExactRotation.NORTH), Tag.BELLE));

    public static final NamedTaggedFormation GENERAL_LINE =
        new NamedTaggedFormation("GENERAL LINE",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.ZERO, Rotation.fromAbsoluteString("|")), Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.ZERO, Rotation.fromAbsoluteString("|")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.ZERO, Rotation.fromAbsoluteString("|")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.ZERO, Rotation.fromAbsoluteString("|")), Tag.END));

    public static final NamedTaggedFormation _1x4 =
        new NamedTaggedFormation("1x4",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.ZERO, Rotation.fromAbsoluteString("+"))),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.ZERO, Rotation.fromAbsoluteString("+"))),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.ZERO, Rotation.fromAbsoluteString("+"))),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.ZERO, Rotation.fromAbsoluteString("+"))));

    public static final NamedTaggedFormation _2x2 =
        new NamedTaggedFormation("2x2",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.ONE, Rotation.fromAbsoluteString("+"))),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.ONE, Rotation.fromAbsoluteString("+"))),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.mONE, Rotation.fromAbsoluteString("+"))),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.mONE, Rotation.fromAbsoluteString("+"))));

    public static final NamedTaggedFormation FACING_COUPLES =
        new NamedTaggedFormation("FACING COUPLES",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.ONE, ExactRotation.SOUTH), Tag.BELLE, Tag.TRAILER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.ONE, ExactRotation.SOUTH), Tag.BEAU, Tag.TRAILER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.mONE, ExactRotation.NORTH), Tag.BEAU, Tag.TRAILER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.mONE, ExactRotation.NORTH), Tag.BELLE, Tag.TRAILER));

    public static final NamedTaggedFormation BACK_TO_BACK_COUPLES =
        new NamedTaggedFormation("BACK TO BACK COUPLES",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.ONE, ExactRotation.NORTH), Tag.BEAU, Tag.LEADER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.ONE, ExactRotation.NORTH), Tag.BELLE, Tag.LEADER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.mONE, ExactRotation.SOUTH), Tag.BELLE, Tag.LEADER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.mONE, ExactRotation.SOUTH), Tag.BEAU, Tag.LEADER));

    public static final NamedTaggedFormation TANDEM_COUPLES =
        new NamedTaggedFormation("TANDEM COUPLES",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.ONE, ExactRotation.NORTH), Tag.BEAU, Tag.LEADER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.ONE, ExactRotation.NORTH), Tag.BELLE, Tag.LEADER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.mONE, ExactRotation.NORTH), Tag.BEAU, Tag.TRAILER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.mONE, ExactRotation.NORTH), Tag.BELLE, Tag.TRAILER));

    public static final NamedTaggedFormation RH_OCEAN_WAVE =
        new NamedTaggedFormation("RH OCEAN WAVE",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.ZERO, ExactRotation.NORTH), Tag.BEAU, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.ZERO, ExactRotation.SOUTH), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.ZERO, ExactRotation.NORTH), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.ZERO, ExactRotation.SOUTH), Tag.BEAU, Tag.END));

    public static final NamedTaggedFormation LH_OCEAN_WAVE =
        new NamedTaggedFormation("LH OCEAN WAVE",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.ZERO, ExactRotation.SOUTH), Tag.BELLE, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.ZERO, ExactRotation.NORTH), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.ZERO, ExactRotation.SOUTH), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.ZERO, ExactRotation.NORTH), Tag.BELLE, Tag.END));

    public static final NamedTaggedFormation RH_BOX =
        new NamedTaggedFormation("RH BOX",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.ONE, ExactRotation.NORTH), Tag.BEAU, Tag.LEADER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.ONE, ExactRotation.SOUTH), Tag.BEAU, Tag.TRAILER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.mONE, ExactRotation.NORTH), Tag.BEAU, Tag.TRAILER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.mONE, ExactRotation.SOUTH), Tag.BEAU, Tag.LEADER));

    public static final NamedTaggedFormation LH_BOX =
        new NamedTaggedFormation("LH BOX",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.ONE, ExactRotation.SOUTH), Tag.BELLE, Tag.TRAILER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.ONE, ExactRotation.NORTH), Tag.BELLE, Tag.LEADER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.mONE, ExactRotation.SOUTH), Tag.BELLE, Tag.LEADER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.mONE, ExactRotation.NORTH), Tag.BELLE, Tag.TRAILER));

    public static final NamedTaggedFormation RH_TWO_FACED_LINE =
        new NamedTaggedFormation("RH TWO-FACED LINE",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.ZERO, ExactRotation.NORTH), Tag.BEAU, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.ZERO, ExactRotation.NORTH), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.ZERO, ExactRotation.SOUTH), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.ZERO, ExactRotation.SOUTH), Tag.BEAU, Tag.END));

    public static final NamedTaggedFormation LH_TWO_FACED_LINE =
        new NamedTaggedFormation("LH TWO-FACED LINE",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.ZERO, ExactRotation.SOUTH), Tag.BELLE, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.ZERO, ExactRotation.SOUTH), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.ZERO, ExactRotation.NORTH), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.ZERO, ExactRotation.NORTH), Tag.BELLE, Tag.END));

    public static final NamedTaggedFormation SINGLE_INVERTED_LINE =
        new NamedTaggedFormation("SINGLE INVERTED LINE",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.ZERO, ExactRotation.SOUTH), Tag.BELLE, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.ZERO, ExactRotation.NORTH), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.ZERO, ExactRotation.NORTH), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.ZERO, ExactRotation.SOUTH), Tag.BEAU, Tag.END));

    public static final NamedTaggedFormation RH_DIAMOND =
        new NamedTaggedFormation("RH DIAMOND",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ZERO, Fraction.valueOf(3), ExactRotation.EAST), Tag.POINT),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.ZERO, ExactRotation.NORTH), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.ZERO, ExactRotation.SOUTH), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ZERO, Fraction.valueOf(-3), ExactRotation.WEST), Tag.POINT));

    public static final NamedTaggedFormation RH_FACING_DIAMOND =
        new NamedTaggedFormation("RH FACING DIAMOND",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ZERO, Fraction.valueOf(3), ExactRotation.EAST), Tag.POINT),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.ZERO, ExactRotation.SOUTH), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.ZERO, ExactRotation.NORTH), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ZERO, Fraction.valueOf(-3), ExactRotation.WEST), Tag.POINT));

    public static final NamedTaggedFormation LH_DIAMOND =
        new NamedTaggedFormation("LH DIAMOND",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ZERO, Fraction.valueOf(3), ExactRotation.WEST), Tag.POINT),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.ZERO, ExactRotation.SOUTH), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.ZERO, ExactRotation.NORTH), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ZERO, Fraction.valueOf(-3), ExactRotation.EAST), Tag.POINT));

    public static final NamedTaggedFormation LH_FACING_DIAMOND =
        new NamedTaggedFormation("LH FACING DIAMOND",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ZERO, Fraction.valueOf(3), ExactRotation.WEST), Tag.POINT),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.ZERO, ExactRotation.NORTH), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.ZERO, ExactRotation.SOUTH), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ZERO, Fraction.valueOf(-3), ExactRotation.EAST), Tag.POINT));

    public static final NamedTaggedFormation RH_SINGLE_PROMENADE =
        new NamedTaggedFormation("RH SINGLE PROMENADE",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ZERO, Fraction.ONE, ExactRotation.EAST)),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.ZERO, ExactRotation.NORTH)),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.ZERO, ExactRotation.SOUTH)),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ZERO, Fraction.mONE, ExactRotation.WEST)));

    public static final NamedTaggedFormation LH_SINGLE_PROMENADE =
        new NamedTaggedFormation("LH SINGLE PROMENADE",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ZERO, Fraction.ONE, ExactRotation.WEST)),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.ZERO, ExactRotation.SOUTH)),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.ZERO, ExactRotation.NORTH)),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ZERO, Fraction.mONE, ExactRotation.EAST)));

    public static final NamedTaggedFormation RH_SINGLE_QUARTER_TAG =
        new NamedTaggedFormation("RH SINGLE 1/4 TAG",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ZERO, Fraction.TWO, ExactRotation.SOUTH), Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.ZERO, ExactRotation.NORTH), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.ZERO, ExactRotation.SOUTH), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ZERO, Fraction.valueOf(-2), ExactRotation.NORTH), Tag.END));

    public static final NamedTaggedFormation LH_SINGLE_QUARTER_TAG =
        new NamedTaggedFormation("LH SINGLE 1/4 TAG",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ZERO, Fraction.TWO, ExactRotation.SOUTH), Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.ZERO, ExactRotation.SOUTH), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.ZERO, ExactRotation.NORTH), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ZERO, Fraction.valueOf(-2), ExactRotation.NORTH), Tag.END));

    public static final NamedTaggedFormation RH_SINGLE_THREE_QUARTER_TAG =
        new NamedTaggedFormation("RH SINGLE 3/4 TAG",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ZERO, Fraction.TWO, ExactRotation.NORTH), Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.ZERO, ExactRotation.NORTH), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.ZERO, ExactRotation.SOUTH), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ZERO, Fraction.valueOf(-2), ExactRotation.SOUTH), Tag.END));

    public static final NamedTaggedFormation LH_SINGLE_THREE_QUARTER_TAG =
        new NamedTaggedFormation("LH SINGLE 3/4 TAG",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ZERO, Fraction.TWO, ExactRotation.NORTH), Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.ZERO, ExactRotation.SOUTH), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.ZERO, ExactRotation.NORTH), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ZERO, Fraction.valueOf(-2), ExactRotation.SOUTH), Tag.END));

    public static final NamedTaggedFormation SINGLE_DOUBLE_PASS_THRU =
        new NamedTaggedFormation("SINGLE DOUBLE PASS THRU",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ZERO, Fraction.valueOf(3), ExactRotation.SOUTH), Tag.TRAILER, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ZERO, Fraction.ONE, ExactRotation.SOUTH), Tag.LEADER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ZERO, Fraction.mONE, ExactRotation.NORTH), Tag.LEADER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ZERO, Fraction.valueOf(-3), ExactRotation.NORTH), Tag.TRAILER, Tag.END));

    public static final NamedTaggedFormation COMPLETED_SINGLE_DOUBLE_PASS_THRU =
        new NamedTaggedFormation("COMPLETED SINGLE DOUBLE PASS THRU",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ZERO, Fraction.valueOf(3), ExactRotation.NORTH), Tag.LEADER, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ZERO, Fraction.ONE, ExactRotation.NORTH), Tag.TRAILER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ZERO, Fraction.mONE, ExactRotation.SOUTH), Tag.TRAILER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ZERO, Fraction.valueOf(-3), ExactRotation.SOUTH), Tag.LEADER, Tag.END));

    public static final NamedTaggedFormation STATIC_SQUARE =
        new NamedTaggedFormation("STATIC SQUARE",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.valueOf(3), ExactRotation.SOUTH), Tag.BELLE),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.valueOf(3), ExactRotation.SOUTH), Tag.BEAU),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.ONE, ExactRotation.EAST), Tag.BEAU),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.ONE, ExactRotation.WEST), Tag.BELLE),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.mONE, ExactRotation.EAST), Tag.BELLE),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.mONE, ExactRotation.WEST), Tag.BEAU),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.valueOf(-3), ExactRotation.NORTH), Tag.BEAU),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.valueOf(-3), ExactRotation.NORTH), Tag.BELLE));

    public static final NamedTaggedFormation PROMENADE =
        new NamedTaggedFormation("PROMENADE",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ZERO, Fraction.valueOf(3), ExactRotation.WEST), Tag.BELLE),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ZERO, Fraction.ONE, ExactRotation.WEST), Tag.BEAU),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.ZERO, ExactRotation.SOUTH), Tag.BELLE),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.ZERO, ExactRotation.SOUTH), Tag.BEAU),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.ZERO, ExactRotation.NORTH), Tag.BEAU),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.ZERO, ExactRotation.NORTH), Tag.BELLE),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ZERO, Fraction.mONE, ExactRotation.EAST), Tag.BEAU),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ZERO, Fraction.valueOf(-3), ExactRotation.EAST), Tag.BELLE));

    public static final NamedTaggedFormation WRONG_WAY_PROMENADE =
        new NamedTaggedFormation("WRONG WAY PROMENADE",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ZERO, Fraction.valueOf(3), ExactRotation.EAST), Tag.BEAU),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ZERO, Fraction.ONE, ExactRotation.EAST), Tag.BELLE),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.ZERO, ExactRotation.NORTH), Tag.BEAU),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.ZERO, ExactRotation.NORTH), Tag.BELLE),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.ZERO, ExactRotation.SOUTH), Tag.BELLE),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.ZERO, ExactRotation.SOUTH), Tag.BEAU),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ZERO, Fraction.mONE, ExactRotation.WEST), Tag.BELLE),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ZERO, Fraction.valueOf(-3), ExactRotation.WEST), Tag.BEAU));

    public static final NamedTaggedFormation THAR =
        new NamedTaggedFormation("THAR",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ZERO, Fraction.valueOf(3), ExactRotation.WEST), Tag.BELLE),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ZERO, Fraction.ONE, ExactRotation.EAST), Tag.BELLE),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.ZERO, ExactRotation.SOUTH), Tag.BELLE),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.ZERO, ExactRotation.NORTH), Tag.BELLE),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.ZERO, ExactRotation.SOUTH), Tag.BELLE),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.ZERO, ExactRotation.NORTH), Tag.BELLE),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ZERO, Fraction.mONE, ExactRotation.WEST), Tag.BELLE),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ZERO, Fraction.valueOf(-3), ExactRotation.EAST), Tag.BELLE));

    public static final NamedTaggedFormation WRONG_WAY_THAR =
        new NamedTaggedFormation("WRONG WAY THAR",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ZERO, Fraction.valueOf(3), ExactRotation.EAST), Tag.BEAU),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ZERO, Fraction.ONE, ExactRotation.WEST), Tag.BEAU),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.ZERO, ExactRotation.NORTH), Tag.BEAU),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.ZERO, ExactRotation.SOUTH), Tag.BEAU),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.ZERO, ExactRotation.NORTH), Tag.BEAU),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.ZERO, ExactRotation.SOUTH), Tag.BEAU),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ZERO, Fraction.mONE, ExactRotation.EAST), Tag.BEAU),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ZERO, Fraction.valueOf(-3), ExactRotation.WEST), Tag.BEAU));

    public static final NamedTaggedFormation FACING_LINES =
        new NamedTaggedFormation("FACING LINES",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.ONE, ExactRotation.SOUTH), Tag.BELLE, Tag.TRAILER, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.ONE, ExactRotation.SOUTH), Tag.BEAU, Tag.TRAILER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.ONE, ExactRotation.SOUTH), Tag.BELLE, Tag.TRAILER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.ONE, ExactRotation.SOUTH), Tag.BEAU, Tag.TRAILER, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.mONE, ExactRotation.NORTH), Tag.BEAU, Tag.TRAILER, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.mONE, ExactRotation.NORTH), Tag.BELLE, Tag.TRAILER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.mONE, ExactRotation.NORTH), Tag.BEAU, Tag.TRAILER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.mONE, ExactRotation.NORTH), Tag.BELLE, Tag.TRAILER, Tag.END));

    public static final NamedTaggedFormation EIGHT_CHAIN_THRU =
        new NamedTaggedFormation("EIGHT CHAIN THRU",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.valueOf(3), ExactRotation.SOUTH), Tag.BELLE, Tag.TRAILER, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.valueOf(3), ExactRotation.SOUTH), Tag.BEAU, Tag.TRAILER, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.ONE, ExactRotation.NORTH), Tag.BEAU, Tag.TRAILER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.ONE, ExactRotation.NORTH), Tag.BELLE, Tag.TRAILER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.mONE, ExactRotation.SOUTH), Tag.BELLE, Tag.TRAILER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.mONE, ExactRotation.SOUTH), Tag.BEAU, Tag.TRAILER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.valueOf(-3), ExactRotation.NORTH), Tag.BEAU, Tag.TRAILER, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.valueOf(-3), ExactRotation.NORTH), Tag.BELLE, Tag.TRAILER, Tag.END));

    public static final NamedTaggedFormation TRADE_BY =
        new NamedTaggedFormation("TRADE BY",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.valueOf(3), ExactRotation.NORTH), Tag.BEAU, Tag.LEADER, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.valueOf(3), ExactRotation.NORTH), Tag.BELLE, Tag.LEADER, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.ONE, ExactRotation.SOUTH), Tag.BELLE, Tag.LEADER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.ONE, ExactRotation.SOUTH), Tag.BEAU, Tag.LEADER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.mONE, ExactRotation.NORTH), Tag.BEAU, Tag.LEADER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.mONE, ExactRotation.NORTH), Tag.BELLE, Tag.LEADER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.valueOf(-3), ExactRotation.SOUTH), Tag.BELLE, Tag.LEADER, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.valueOf(-3), ExactRotation.SOUTH), Tag.BEAU, Tag.LEADER, Tag.END));

    public static final NamedTaggedFormation DOUBLE_PASS_THRU =
        new NamedTaggedFormation("DOUBLE PASS THRU",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.valueOf(3), ExactRotation.SOUTH), Tag.BELLE, Tag.TRAILER, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.valueOf(3), ExactRotation.SOUTH), Tag.BEAU, Tag.TRAILER, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.ONE, ExactRotation.SOUTH), Tag.BELLE, Tag.LEADER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.ONE, ExactRotation.SOUTH), Tag.BEAU, Tag.LEADER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.mONE, ExactRotation.NORTH), Tag.BEAU, Tag.LEADER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.mONE, ExactRotation.NORTH), Tag.BELLE, Tag.LEADER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.valueOf(-3), ExactRotation.NORTH), Tag.BEAU, Tag.TRAILER, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.valueOf(-3), ExactRotation.NORTH), Tag.BELLE, Tag.TRAILER, Tag.END));

    public static final NamedTaggedFormation COMPLETED_DOUBLE_PASS_THRU =
        new NamedTaggedFormation("COMPLETED DOUBLE PASS THRU",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.valueOf(3), ExactRotation.NORTH), Tag.BEAU, Tag.LEADER, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.valueOf(3), ExactRotation.NORTH), Tag.BELLE, Tag.LEADER, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.ONE, ExactRotation.NORTH), Tag.BEAU, Tag.TRAILER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.ONE, ExactRotation.NORTH), Tag.BELLE, Tag.TRAILER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.mONE, ExactRotation.SOUTH), Tag.BELLE, Tag.TRAILER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.mONE, ExactRotation.SOUTH), Tag.BEAU, Tag.TRAILER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.valueOf(-3), ExactRotation.SOUTH), Tag.BELLE, Tag.LEADER, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.valueOf(-3), ExactRotation.SOUTH), Tag.BEAU, Tag.LEADER, Tag.END));

    public static final NamedTaggedFormation LINES_FACING_OUT =
        new NamedTaggedFormation("LINES FACING OUT",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.ONE, ExactRotation.NORTH), Tag.BEAU, Tag.LEADER, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.ONE, ExactRotation.NORTH), Tag.BELLE, Tag.LEADER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.ONE, ExactRotation.NORTH), Tag.BEAU, Tag.LEADER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.ONE, ExactRotation.NORTH), Tag.BELLE, Tag.LEADER, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.mONE, ExactRotation.SOUTH), Tag.BELLE, Tag.LEADER, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.mONE, ExactRotation.SOUTH), Tag.BEAU, Tag.LEADER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.mONE, ExactRotation.SOUTH), Tag.BELLE, Tag.LEADER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.mONE, ExactRotation.SOUTH), Tag.BEAU, Tag.LEADER, Tag.END));

    public static final NamedTaggedFormation PARALLEL_RH_WAVES =
        new NamedTaggedFormation("PARALLEL RH WAVES",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.ONE, ExactRotation.NORTH), Tag.BEAU, Tag.LEADER, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.ONE, ExactRotation.SOUTH), Tag.BEAU, Tag.TRAILER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.ONE, ExactRotation.NORTH), Tag.BEAU, Tag.LEADER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.ONE, ExactRotation.SOUTH), Tag.BEAU, Tag.TRAILER, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.mONE, ExactRotation.NORTH), Tag.BEAU, Tag.TRAILER, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.mONE, ExactRotation.SOUTH), Tag.BEAU, Tag.LEADER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.mONE, ExactRotation.NORTH), Tag.BEAU, Tag.TRAILER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.mONE, ExactRotation.SOUTH), Tag.BEAU, Tag.LEADER, Tag.END));

    public static final NamedTaggedFormation PARALLEL_LH_WAVES =
        new NamedTaggedFormation("PARALLEL LH WAVES",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.ONE, ExactRotation.SOUTH), Tag.BELLE, Tag.TRAILER, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.ONE, ExactRotation.NORTH), Tag.BELLE, Tag.LEADER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.ONE, ExactRotation.SOUTH), Tag.BELLE, Tag.TRAILER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.ONE, ExactRotation.NORTH), Tag.BELLE, Tag.LEADER, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.mONE, ExactRotation.SOUTH), Tag.BELLE, Tag.LEADER, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.mONE, ExactRotation.NORTH), Tag.BELLE, Tag.TRAILER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.mONE, ExactRotation.SOUTH), Tag.BELLE, Tag.LEADER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.mONE, ExactRotation.NORTH), Tag.BELLE, Tag.TRAILER, Tag.END));

    public static final NamedTaggedFormation PARALLEL_RH_TWO_FACED_LINES =
        new NamedTaggedFormation("PARALLEL RH TWO-FACED LINES",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.ONE, ExactRotation.NORTH), Tag.BEAU, Tag.LEADER, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.ONE, ExactRotation.NORTH), Tag.BELLE, Tag.LEADER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.ONE, ExactRotation.SOUTH), Tag.BELLE, Tag.TRAILER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.ONE, ExactRotation.SOUTH), Tag.BEAU, Tag.TRAILER, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.mONE, ExactRotation.NORTH), Tag.BEAU, Tag.TRAILER, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.mONE, ExactRotation.NORTH), Tag.BELLE, Tag.TRAILER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.mONE, ExactRotation.SOUTH), Tag.BELLE, Tag.LEADER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.mONE, ExactRotation.SOUTH), Tag.BEAU, Tag.LEADER, Tag.END));

    public static final NamedTaggedFormation PARALLEL_LH_TWO_FACED_LINES =
        new NamedTaggedFormation("PARALLEL LH TWO-FACED LINES",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.ONE, ExactRotation.SOUTH), Tag.BELLE, Tag.TRAILER, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.ONE, ExactRotation.SOUTH), Tag.BEAU, Tag.TRAILER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.ONE, ExactRotation.NORTH), Tag.BEAU, Tag.LEADER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.ONE, ExactRotation.NORTH), Tag.BELLE, Tag.LEADER, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.mONE, ExactRotation.SOUTH), Tag.BELLE, Tag.LEADER, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.mONE, ExactRotation.SOUTH), Tag.BEAU, Tag.LEADER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.mONE, ExactRotation.NORTH), Tag.BEAU, Tag.TRAILER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.mONE, ExactRotation.NORTH), Tag.BELLE, Tag.TRAILER, Tag.END));

    public static final NamedTaggedFormation RH_COLUMN =
        new NamedTaggedFormation("RH COLUMN",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.valueOf(3), ExactRotation.NORTH), Tag.BEAU, Tag.LEADER, Tag.END, Tag.NUMBER_1),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.valueOf(3), ExactRotation.SOUTH), Tag.BEAU, Tag.TRAILER, Tag.END, Tag.NUMBER_4),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.ONE, ExactRotation.NORTH), Tag.BEAU, Tag.TRAILER, Tag.CENTER, Tag.NUMBER_2),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.ONE, ExactRotation.SOUTH), Tag.BEAU, Tag.LEADER, Tag.CENTER, Tag.NUMBER_3),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.mONE, ExactRotation.NORTH), Tag.BEAU, Tag.LEADER, Tag.CENTER, Tag.NUMBER_3),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.mONE, ExactRotation.SOUTH), Tag.BEAU, Tag.TRAILER, Tag.CENTER, Tag.NUMBER_2),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.valueOf(-3), ExactRotation.NORTH), Tag.BEAU, Tag.TRAILER, Tag.END, Tag.NUMBER_4),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.valueOf(-3), ExactRotation.SOUTH), Tag.BEAU, Tag.LEADER, Tag.END, Tag.NUMBER_1));

    public static final NamedTaggedFormation LH_COLUMN =
        new NamedTaggedFormation("LH COLUMN",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.valueOf(3), ExactRotation.SOUTH), Tag.BELLE, Tag.TRAILER, Tag.END, Tag.NUMBER_4),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.valueOf(3), ExactRotation.NORTH), Tag.BELLE, Tag.LEADER, Tag.END, Tag.NUMBER_1),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.ONE, ExactRotation.SOUTH), Tag.BELLE, Tag.LEADER, Tag.CENTER, Tag.NUMBER_3),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.ONE, ExactRotation.NORTH), Tag.BELLE, Tag.TRAILER, Tag.CENTER, Tag.NUMBER_2),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.mONE, ExactRotation.SOUTH), Tag.BELLE, Tag.TRAILER, Tag.CENTER, Tag.NUMBER_2),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.mONE, ExactRotation.NORTH), Tag.BELLE, Tag.LEADER, Tag.CENTER, Tag.NUMBER_3),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.valueOf(-3), ExactRotation.SOUTH), Tag.BELLE, Tag.LEADER, Tag.END, Tag.NUMBER_1),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.valueOf(-3), ExactRotation.NORTH), Tag.BELLE, Tag.TRAILER, Tag.END, Tag.NUMBER_4));

    public static final NamedTaggedFormation ENDS_IN_INVERTED_LINES =
        new NamedTaggedFormation("ENDS IN INVERTED LINES",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.ONE, ExactRotation.SOUTH), Tag.BELLE, Tag.TRAILER, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.ONE, ExactRotation.NORTH), Tag.BELLE, Tag.LEADER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.ONE, ExactRotation.NORTH), Tag.BEAU, Tag.LEADER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.ONE, ExactRotation.SOUTH), Tag.BEAU, Tag.TRAILER, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.mONE, ExactRotation.NORTH), Tag.BEAU, Tag.TRAILER, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.mONE, ExactRotation.SOUTH), Tag.BEAU, Tag.LEADER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.mONE, ExactRotation.SOUTH), Tag.BELLE, Tag.LEADER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.mONE, ExactRotation.NORTH), Tag.BELLE, Tag.TRAILER, Tag.END));

    public static final NamedTaggedFormation ENDS_OUT_INVERTED_LINES =
        new NamedTaggedFormation("ENDS OUT INVERTED LINES",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.ONE, ExactRotation.NORTH), Tag.BEAU, Tag.LEADER, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.ONE, ExactRotation.SOUTH), Tag.BEAU, Tag.TRAILER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.ONE, ExactRotation.SOUTH), Tag.BELLE, Tag.TRAILER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.ONE, ExactRotation.NORTH), Tag.BELLE, Tag.LEADER, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.mONE, ExactRotation.SOUTH), Tag.BELLE, Tag.LEADER, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.mONE, ExactRotation.NORTH), Tag.BELLE, Tag.TRAILER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.mONE, ExactRotation.NORTH), Tag.BEAU, Tag.TRAILER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.mONE, ExactRotation.SOUTH), Tag.BEAU, Tag.LEADER, Tag.END));

    public static final NamedTaggedFormation RH_QUARTER_TAG =
        new NamedTaggedFormation("RH 1/4 TAG",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.TWO, ExactRotation.SOUTH), Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.TWO, ExactRotation.SOUTH), Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.ZERO, ExactRotation.NORTH), Tag.BEAU, Tag.CENTER, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.ZERO, ExactRotation.SOUTH), Tag.BEAU, Tag.CENTER, Tag.VERY_CENTER, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.ZERO, ExactRotation.NORTH), Tag.BEAU, Tag.CENTER, Tag.VERY_CENTER, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.ZERO, ExactRotation.SOUTH), Tag.BEAU, Tag.CENTER, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.valueOf(-2), ExactRotation.NORTH), Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.valueOf(-2), ExactRotation.NORTH), Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6));

    public static final NamedTaggedFormation LH_QUARTER_TAG =
        new NamedTaggedFormation("LH 1/4 TAG",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.TWO, ExactRotation.SOUTH), Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.TWO, ExactRotation.SOUTH), Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.ZERO, ExactRotation.SOUTH), Tag.BELLE, Tag.CENTER, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.ZERO, ExactRotation.NORTH), Tag.BELLE, Tag.CENTER, Tag.VERY_CENTER, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.ZERO, ExactRotation.SOUTH), Tag.BELLE, Tag.CENTER, Tag.VERY_CENTER, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.ZERO, ExactRotation.NORTH), Tag.BELLE, Tag.CENTER, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.valueOf(-2), ExactRotation.NORTH), Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.valueOf(-2), ExactRotation.NORTH), Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6));

    public static final NamedTaggedFormation RH_THREE_QUARTER_TAG =
        new NamedTaggedFormation("RH 3/4 TAG",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.TWO, ExactRotation.NORTH), Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.TWO, ExactRotation.NORTH), Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.ZERO, ExactRotation.NORTH), Tag.BEAU, Tag.CENTER, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.ZERO, ExactRotation.SOUTH), Tag.BEAU, Tag.CENTER, Tag.VERY_CENTER, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.ZERO, ExactRotation.NORTH), Tag.BEAU, Tag.CENTER, Tag.VERY_CENTER, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.ZERO, ExactRotation.SOUTH), Tag.BEAU, Tag.CENTER, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.valueOf(-2), ExactRotation.SOUTH), Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.valueOf(-2), ExactRotation.SOUTH), Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6));

    public static final NamedTaggedFormation LH_THREE_QUARTER_TAG =
        new NamedTaggedFormation("LH 3/4 TAG",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.TWO, ExactRotation.NORTH), Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.TWO, ExactRotation.NORTH), Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.ZERO, ExactRotation.SOUTH), Tag.BELLE, Tag.CENTER, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.ZERO, ExactRotation.NORTH), Tag.BELLE, Tag.CENTER, Tag.VERY_CENTER, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.ZERO, ExactRotation.SOUTH), Tag.BELLE, Tag.CENTER, Tag.VERY_CENTER, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.ZERO, ExactRotation.NORTH), Tag.BELLE, Tag.CENTER, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.valueOf(-2), ExactRotation.SOUTH), Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.valueOf(-2), ExactRotation.SOUTH), Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6));

    public static final NamedTaggedFormation RH_QUARTER_LINE =
        new NamedTaggedFormation("RH 1/4 LINE",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.TWO, ExactRotation.SOUTH), Tag.BELLE, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.TWO, ExactRotation.SOUTH), Tag.BEAU, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.ZERO, ExactRotation.NORTH), Tag.BEAU, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.ZERO, ExactRotation.NORTH), Tag.BELLE, Tag.VERY_CENTER, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.ZERO, ExactRotation.SOUTH), Tag.BELLE, Tag.VERY_CENTER, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.ZERO, ExactRotation.SOUTH), Tag.BEAU, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.valueOf(-2), ExactRotation.NORTH), Tag.BEAU, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.valueOf(-2), ExactRotation.NORTH), Tag.BELLE, Tag.CENTER_6, Tag.OUTSIDE_6));

    public static final NamedTaggedFormation LH_QUARTER_LINE =
        new NamedTaggedFormation("LH 1/4 LINE",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.TWO, ExactRotation.SOUTH), Tag.BELLE, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.TWO, ExactRotation.SOUTH), Tag.BEAU, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.ZERO, ExactRotation.SOUTH), Tag.BELLE, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.ZERO, ExactRotation.SOUTH), Tag.BEAU, Tag.VERY_CENTER, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.ZERO, ExactRotation.NORTH), Tag.BEAU, Tag.VERY_CENTER, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.ZERO, ExactRotation.NORTH), Tag.BELLE, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.valueOf(-2), ExactRotation.NORTH), Tag.BEAU, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.valueOf(-2), ExactRotation.NORTH), Tag.BELLE, Tag.CENTER_6, Tag.OUTSIDE_6));

    public static final NamedTaggedFormation RH_THREE_QUARTER_LINE =
        new NamedTaggedFormation("RH 3/4 LINE",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.TWO, ExactRotation.NORTH), Tag.BEAU, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.TWO, ExactRotation.NORTH), Tag.BELLE, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.ZERO, ExactRotation.NORTH), Tag.BEAU, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.ZERO, ExactRotation.NORTH), Tag.BELLE, Tag.VERY_CENTER, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.ZERO, ExactRotation.SOUTH), Tag.BELLE, Tag.VERY_CENTER, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.ZERO, ExactRotation.SOUTH), Tag.BEAU, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.valueOf(-2), ExactRotation.SOUTH), Tag.BELLE, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.valueOf(-2), ExactRotation.SOUTH), Tag.BEAU, Tag.CENTER_6, Tag.OUTSIDE_6));

    public static final NamedTaggedFormation LH_THREE_QUARTER_LINE =
        new NamedTaggedFormation("LH 3/4 LINE",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.TWO, ExactRotation.NORTH), Tag.BEAU, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.TWO, ExactRotation.NORTH), Tag.BELLE, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.ZERO, ExactRotation.SOUTH), Tag.BELLE, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.ZERO, ExactRotation.SOUTH), Tag.BEAU, Tag.VERY_CENTER, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.ZERO, ExactRotation.NORTH), Tag.BEAU, Tag.VERY_CENTER, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.ZERO, ExactRotation.NORTH), Tag.BELLE, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.valueOf(-2), ExactRotation.SOUTH), Tag.BELLE, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.valueOf(-2), ExactRotation.SOUTH), Tag.BEAU, Tag.CENTER_6, Tag.OUTSIDE_6));

    public static final NamedTaggedFormation RH_TWIN_DIAMONDS =
        new NamedTaggedFormation("RH TWIN DIAMONDS",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-2), Fraction.valueOf(3), ExactRotation.EAST), Tag.POINT),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.TWO, Fraction.valueOf(3), ExactRotation.EAST), Tag.POINT),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.ZERO, ExactRotation.NORTH), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.ZERO, ExactRotation.SOUTH), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.ZERO, ExactRotation.NORTH), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.ZERO, ExactRotation.SOUTH), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-2), Fraction.valueOf(-3), ExactRotation.WEST), Tag.POINT),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.TWO, Fraction.valueOf(-3), ExactRotation.WEST), Tag.POINT));

    public static final NamedTaggedFormation LH_TWIN_DIAMONDS =
        new NamedTaggedFormation("LH TWIN DIAMONDS",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-2), Fraction.valueOf(3), ExactRotation.WEST), Tag.POINT),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.TWO, Fraction.valueOf(3), ExactRotation.WEST), Tag.POINT),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.ZERO, ExactRotation.SOUTH), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.ZERO, ExactRotation.NORTH), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.ZERO, ExactRotation.SOUTH), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.ZERO, ExactRotation.NORTH), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-2), Fraction.valueOf(-3), ExactRotation.EAST), Tag.POINT),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.TWO, Fraction.valueOf(-3), ExactRotation.EAST), Tag.POINT));

    public static final NamedTaggedFormation RH_POINT_TO_POINT_DIAMONDS =
        new NamedTaggedFormation("RH POINT-TO-POINT DIAMONDS",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ZERO, Fraction.valueOf(7), ExactRotation.EAST), Tag.POINT),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.valueOf(4), ExactRotation.NORTH), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.valueOf(4), ExactRotation.SOUTH), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ZERO, Fraction.ONE, ExactRotation.WEST), Tag.BELLE, Tag.POINT),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ZERO, Fraction.mONE, ExactRotation.EAST), Tag.BELLE, Tag.POINT),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.valueOf(-4), ExactRotation.NORTH), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.valueOf(-4), ExactRotation.SOUTH), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ZERO, Fraction.valueOf(-7), ExactRotation.WEST), Tag.POINT));

    public static final NamedTaggedFormation RH_POINT_TO_POINT_FACING_DIAMONDS =
        new NamedTaggedFormation("RH POINT-TO-POINT FACING DIAMONDS",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ZERO, Fraction.valueOf(7), ExactRotation.EAST), Tag.POINT),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.valueOf(4), ExactRotation.SOUTH), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.valueOf(4), ExactRotation.NORTH), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ZERO, Fraction.ONE, ExactRotation.WEST), Tag.BELLE, Tag.POINT),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ZERO, Fraction.mONE, ExactRotation.EAST), Tag.BELLE, Tag.POINT),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.valueOf(-4), ExactRotation.SOUTH), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.valueOf(-4), ExactRotation.NORTH), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ZERO, Fraction.valueOf(-7), ExactRotation.WEST), Tag.POINT));

    public static final NamedTaggedFormation LH_POINT_TO_POINT_DIAMONDS =
        new NamedTaggedFormation("LH POINT-TO-POINT DIAMONDS",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ZERO, Fraction.valueOf(7), ExactRotation.WEST), Tag.POINT),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.valueOf(4), ExactRotation.SOUTH), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.valueOf(4), ExactRotation.NORTH), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ZERO, Fraction.ONE, ExactRotation.EAST), Tag.BEAU, Tag.POINT),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ZERO, Fraction.mONE, ExactRotation.WEST), Tag.BEAU, Tag.POINT),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.valueOf(-4), ExactRotation.SOUTH), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.valueOf(-4), ExactRotation.NORTH), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ZERO, Fraction.valueOf(-7), ExactRotation.EAST), Tag.POINT));

    public static final NamedTaggedFormation LH_POINT_TO_POINT_FACING_DIAMONDS =
        new NamedTaggedFormation("LH POINT-TO-POINT FACING DIAMONDS",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ZERO, Fraction.valueOf(7), ExactRotation.WEST), Tag.POINT),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.valueOf(4), ExactRotation.NORTH), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.valueOf(4), ExactRotation.SOUTH), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ZERO, Fraction.ONE, ExactRotation.EAST), Tag.BEAU, Tag.POINT),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ZERO, Fraction.mONE, ExactRotation.WEST), Tag.BEAU, Tag.POINT),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.valueOf(-4), ExactRotation.NORTH), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.valueOf(-4), ExactRotation.SOUTH), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ZERO, Fraction.valueOf(-7), ExactRotation.EAST), Tag.POINT));

    public static final NamedTaggedFormation RH_TWIN_FACING_DIAMONDS =
        new NamedTaggedFormation("RH TWIN FACING DIAMONDS",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-2), Fraction.valueOf(3), ExactRotation.EAST), Tag.POINT),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.TWO, Fraction.valueOf(3), ExactRotation.EAST), Tag.POINT),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.ZERO, ExactRotation.SOUTH), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.ZERO, ExactRotation.NORTH), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.ZERO, ExactRotation.SOUTH), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.ZERO, ExactRotation.NORTH), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-2), Fraction.valueOf(-3), ExactRotation.WEST), Tag.POINT),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.TWO, Fraction.valueOf(-3), ExactRotation.WEST), Tag.POINT));

    public static final NamedTaggedFormation LH_TWIN_FACING_DIAMONDS =
        new NamedTaggedFormation("LH TWIN FACING DIAMONDS",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-2), Fraction.valueOf(3), ExactRotation.WEST), Tag.POINT),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.TWO, Fraction.valueOf(3), ExactRotation.WEST), Tag.POINT),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.ZERO, ExactRotation.NORTH), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.ZERO, ExactRotation.SOUTH), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.ZERO, ExactRotation.NORTH), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.ZERO, ExactRotation.SOUTH), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-2), Fraction.valueOf(-3), ExactRotation.EAST), Tag.POINT),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.TWO, Fraction.valueOf(-3), ExactRotation.EAST), Tag.POINT));

    public static final NamedTaggedFormation RH_TIDAL_WAVE =
        new NamedTaggedFormation("RH TIDAL WAVE",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-7), Fraction.ZERO, ExactRotation.NORTH), Tag.BEAU, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-5), Fraction.ZERO, ExactRotation.SOUTH), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.ZERO, ExactRotation.NORTH), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.ZERO, ExactRotation.SOUTH), Tag.BEAU, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.ZERO, ExactRotation.NORTH), Tag.BEAU, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.ZERO, ExactRotation.SOUTH), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(5), Fraction.ZERO, ExactRotation.NORTH), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(7), Fraction.ZERO, ExactRotation.SOUTH), Tag.BEAU, Tag.END));

    public static final NamedTaggedFormation LH_TIDAL_WAVE =
        new NamedTaggedFormation("LH TIDAL WAVE",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-7), Fraction.ZERO, ExactRotation.SOUTH), Tag.BELLE, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-5), Fraction.ZERO, ExactRotation.NORTH), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.ZERO, ExactRotation.SOUTH), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.ZERO, ExactRotation.NORTH), Tag.BELLE, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.ZERO, ExactRotation.SOUTH), Tag.BELLE, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.ZERO, ExactRotation.NORTH), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(5), Fraction.ZERO, ExactRotation.SOUTH), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(7), Fraction.ZERO, ExactRotation.NORTH), Tag.BELLE, Tag.END));

    public static final NamedTaggedFormation RH_TIDAL_TWO_FACED_LINE =
        new NamedTaggedFormation("RH TIDAL TWO-FACED LINE",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-7), Fraction.ZERO, ExactRotation.NORTH), Tag.BEAU, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-5), Fraction.ZERO, ExactRotation.NORTH), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.ZERO, ExactRotation.SOUTH), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.ZERO, ExactRotation.SOUTH), Tag.BEAU, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.ZERO, ExactRotation.NORTH), Tag.BEAU, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.ZERO, ExactRotation.NORTH), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(5), Fraction.ZERO, ExactRotation.SOUTH), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(7), Fraction.ZERO, ExactRotation.SOUTH), Tag.BEAU, Tag.END));

    public static final NamedTaggedFormation LH_TIDAL_TWO_FACED_LINE =
        new NamedTaggedFormation("LH TIDAL TWO-FACED LINE",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-7), Fraction.ZERO, ExactRotation.SOUTH), Tag.BELLE, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-5), Fraction.ZERO, ExactRotation.SOUTH), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.ZERO, ExactRotation.NORTH), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.ZERO, ExactRotation.NORTH), Tag.BELLE, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.ZERO, ExactRotation.SOUTH), Tag.BELLE, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.ZERO, ExactRotation.SOUTH), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(5), Fraction.ZERO, ExactRotation.NORTH), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(7), Fraction.ZERO, ExactRotation.NORTH), Tag.BELLE, Tag.END));

    public static final NamedTaggedFormation RH_TIDAL_LINE =
        new NamedTaggedFormation("RH TIDAL LINE",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-7), Fraction.ZERO, ExactRotation.NORTH), Tag.BEAU),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-5), Fraction.ZERO, ExactRotation.NORTH), Tag.BELLE),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.ZERO, ExactRotation.NORTH), Tag.BEAU),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.ZERO, ExactRotation.NORTH), Tag.BELLE),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.ZERO, ExactRotation.SOUTH), Tag.BELLE),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.ZERO, ExactRotation.SOUTH), Tag.BEAU),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(5), Fraction.ZERO, ExactRotation.SOUTH), Tag.BELLE),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(7), Fraction.ZERO, ExactRotation.SOUTH), Tag.BEAU));

    public static final NamedTaggedFormation LH_TIDAL_LINE =
        new NamedTaggedFormation("LH TIDAL LINE",
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-7), Fraction.ZERO, ExactRotation.SOUTH), Tag.BELLE),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-5), Fraction.ZERO, ExactRotation.SOUTH), Tag.BEAU),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.ZERO, ExactRotation.SOUTH), Tag.BELLE),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.mONE, Fraction.ZERO, ExactRotation.SOUTH), Tag.BEAU),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.ONE, Fraction.ZERO, ExactRotation.NORTH), Tag.BEAU),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.ZERO, ExactRotation.NORTH), Tag.BELLE),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(5), Fraction.ZERO, ExactRotation.NORTH), Tag.BEAU),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(7), Fraction.ZERO, ExactRotation.NORTH), Tag.BELLE));

    public static final List<NamedTaggedFormation> all =
        Collections.unmodifiableList(Arrays.asList(
            FormationListFast.SINGLE_DANCER,
            FormationListFast.GENERAL_PARTNERS,
            FormationListFast.COUPLE,
            FormationListFast.FACING_DANCERS,
            FormationListFast.BACK_TO_BACK_DANCERS,
            FormationListFast.TANDEM,
            FormationListFast.RH_MINIWAVE,
            FormationListFast.LH_MINIWAVE,
            FormationListFast.GENERAL_LINE,
            FormationListFast._1x4,
            FormationListFast._2x2,
            FormationListFast.FACING_COUPLES,
            FormationListFast.BACK_TO_BACK_COUPLES,
            FormationListFast.TANDEM_COUPLES,
            FormationListFast.RH_OCEAN_WAVE,
            FormationListFast.LH_OCEAN_WAVE,
            FormationListFast.RH_BOX,
            FormationListFast.LH_BOX,
            FormationListFast.RH_TWO_FACED_LINE,
            FormationListFast.LH_TWO_FACED_LINE,
            FormationListFast.SINGLE_INVERTED_LINE,
            FormationListFast.RH_DIAMOND,
            FormationListFast.RH_FACING_DIAMOND,
            FormationListFast.LH_DIAMOND,
            FormationListFast.LH_FACING_DIAMOND,
            FormationListFast.RH_SINGLE_PROMENADE,
            FormationListFast.LH_SINGLE_PROMENADE,
            FormationListFast.RH_SINGLE_QUARTER_TAG,
            FormationListFast.LH_SINGLE_QUARTER_TAG,
            FormationListFast.RH_SINGLE_THREE_QUARTER_TAG,
            FormationListFast.LH_SINGLE_THREE_QUARTER_TAG,
            FormationListFast.SINGLE_DOUBLE_PASS_THRU,
            FormationListFast.COMPLETED_SINGLE_DOUBLE_PASS_THRU,
            FormationListFast.STATIC_SQUARE,
            FormationListFast.PROMENADE,
            FormationListFast.WRONG_WAY_PROMENADE,
            FormationListFast.THAR,
            FormationListFast.WRONG_WAY_THAR,
            FormationListFast.FACING_LINES,
            FormationListFast.EIGHT_CHAIN_THRU,
            FormationListFast.TRADE_BY,
            FormationListFast.DOUBLE_PASS_THRU,
            FormationListFast.COMPLETED_DOUBLE_PASS_THRU,
            FormationListFast.LINES_FACING_OUT,
            FormationListFast.PARALLEL_RH_WAVES,
            FormationListFast.PARALLEL_LH_WAVES,
            FormationListFast.PARALLEL_RH_TWO_FACED_LINES,
            FormationListFast.PARALLEL_LH_TWO_FACED_LINES,
            FormationListFast.RH_COLUMN,
            FormationListFast.LH_COLUMN,
            FormationListFast.ENDS_IN_INVERTED_LINES,
            FormationListFast.ENDS_OUT_INVERTED_LINES,
            FormationListFast.RH_QUARTER_TAG,
            FormationListFast.LH_QUARTER_TAG,
            FormationListFast.RH_THREE_QUARTER_TAG,
            FormationListFast.LH_THREE_QUARTER_TAG,
            FormationListFast.RH_QUARTER_LINE,
            FormationListFast.LH_QUARTER_LINE,
            FormationListFast.RH_THREE_QUARTER_LINE,
            FormationListFast.LH_THREE_QUARTER_LINE,
            FormationListFast.RH_TWIN_DIAMONDS,
            FormationListFast.LH_TWIN_DIAMONDS,
            FormationListFast.RH_POINT_TO_POINT_DIAMONDS,
            FormationListFast.RH_POINT_TO_POINT_FACING_DIAMONDS,
            FormationListFast.LH_POINT_TO_POINT_DIAMONDS,
            FormationListFast.LH_POINT_TO_POINT_FACING_DIAMONDS,
            FormationListFast.RH_TWIN_FACING_DIAMONDS,
            FormationListFast.LH_TWIN_FACING_DIAMONDS,
            FormationListFast.RH_TIDAL_WAVE,
            FormationListFast.LH_TIDAL_WAVE,
            FormationListFast.RH_TIDAL_TWO_FACED_LINE,
            FormationListFast.LH_TIDAL_TWO_FACED_LINE,
            FormationListFast.RH_TIDAL_LINE,
            FormationListFast.LH_TIDAL_LINE));
}
