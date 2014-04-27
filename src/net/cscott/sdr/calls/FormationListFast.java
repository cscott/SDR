package net.cscott.sdr.calls;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.runner.RunWith;

import net.cscott.jdoctest.JDoctestRunner;

import net.cscott.sdr.calls.TaggedFormation.Tag;
import static net.cscott.sdr.calls.TaggedFormation.TaggedDancerInfo;
import net.cscott.sdr.util.Fraction;

/** Compiled version of {@link FormationListSlow}. */
@RunWith(value=JDoctestRunner.class)
abstract class FormationListFast {
    /** SINGLE DANCER formation.
      * @doc.test
      *  js> tf = FormationList.SINGLE_DANCER; tf.mapStd([]).toStringDiagram('|');
      *  |1B^
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  []
      */
    public static final NamedTaggedFormation SINGLE_DANCER =
        new NamedTaggedFormation("SINGLE DANCER",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(0), ExactRotation.NORTH)));

    /** GENERAL PARTNERS formation.
      * @doc.test
      *  js> tf = FormationList.GENERAL_PARTNERS; tf.toStringDiagram('|');
      *  ||    |
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  []
      *  []
      */
    public static final NamedTaggedFormation GENERAL_PARTNERS =
        new NamedTaggedFormation("GENERAL PARTNERS",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), Rotation.fromAbsoluteString("|"))),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), Rotation.fromAbsoluteString("|"))));

    /** GENERAL TANDEM formation.
      * @doc.test
      *  js> tf = FormationList.GENERAL_TANDEM; tf.toStringDiagram('|');
      *  ||
      *  |
      *  ||
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  []
      *  []
      */
    public static final NamedTaggedFormation GENERAL_TANDEM =
        new NamedTaggedFormation("GENERAL TANDEM",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(1), Rotation.fromAbsoluteString("|"))),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-1), Rotation.fromAbsoluteString("|"))));

    /** 1x2 formation.
      * @doc.test
      *  js> tf = FormationList._1x2; tf.toStringDiagram('|');
      *  |+    +
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  []
      *  []
      */
    public static final NamedTaggedFormation _1x2 =
        new NamedTaggedFormation("1x2",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), Rotation.fromAbsoluteString("+"))),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), Rotation.fromAbsoluteString("+"))));

    /** COUPLE formation.
      * @doc.test
      *  js> tf = FormationList.COUPLE; tf.mapStd([]).toStringDiagram('|');
      *  |1B^  1G^
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [BEAU]
      *  [BELLE]
      */
    public static final NamedTaggedFormation COUPLE =
        new NamedTaggedFormation("COUPLE",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BEAU),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BELLE));

    /** COUPLE NO TAGS formation.
      * @doc.test
      *  js> tf = FormationList.COUPLE_NO_TAGS; tf.mapStd([]).toStringDiagram('|');
      *  |1B^  1G^
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  []
      *  []
      */
    public static final NamedTaggedFormation COUPLE_NO_TAGS =
        new NamedTaggedFormation("COUPLE NO TAGS",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), ExactRotation.NORTH)),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), ExactRotation.NORTH)));

    /** FACING DANCERS formation.
      * @doc.test
      *  js> tf = FormationList.FACING_DANCERS; tf.mapStd([]).toStringDiagram('|');
      *  |1Bv
      *  |
      *  |1G^
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [TRAILER]
      *  [TRAILER]
      */
    public static final NamedTaggedFormation FACING_DANCERS =
        new NamedTaggedFormation("FACING DANCERS",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(1), ExactRotation.SOUTH), Tag.TRAILER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-1), ExactRotation.NORTH), Tag.TRAILER));

    /** BACK-TO-BACK DANCERS formation.
      * @doc.test
      *  js> tf = FormationList.BACK_TO_BACK_DANCERS; tf.mapStd([]).toStringDiagram('|');
      *  |1B^
      *  |
      *  |1Gv
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [LEADER]
      *  [LEADER]
      */
    public static final NamedTaggedFormation BACK_TO_BACK_DANCERS =
        new NamedTaggedFormation("BACK-TO-BACK DANCERS",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(1), ExactRotation.NORTH), Tag.LEADER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-1), ExactRotation.SOUTH), Tag.LEADER));

    /** TANDEM formation.
      * @doc.test
      *  js> tf = FormationList.TANDEM; tf.mapStd([]).toStringDiagram('|');
      *  |1B^
      *  |
      *  |1G^
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [LEADER]
      *  [TRAILER]
      */
    public static final NamedTaggedFormation TANDEM =
        new NamedTaggedFormation("TANDEM",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(1), ExactRotation.NORTH), Tag.LEADER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-1), ExactRotation.NORTH), Tag.TRAILER));

    /** RH MINIWAVE formation.
      * @doc.test
      *  js> tf = FormationList.RH_MINIWAVE; tf.mapStd([]).toStringDiagram('|');
      *  |1B^  1Gv
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [BEAU]
      *  [BEAU]
      */
    public static final NamedTaggedFormation RH_MINIWAVE =
        new NamedTaggedFormation("RH MINIWAVE",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BEAU),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BEAU));

    /** LH MINIWAVE formation.
      * @doc.test
      *  js> tf = FormationList.LH_MINIWAVE; tf.mapStd([]).toStringDiagram('|');
      *  |1Bv  1G^
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [BELLE]
      *  [BELLE]
      */
    public static final NamedTaggedFormation LH_MINIWAVE =
        new NamedTaggedFormation("LH MINIWAVE",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BELLE),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BELLE));

    /** 1x3 formation.
      * @doc.test
      *  js> tf = FormationList._1x3; tf.toStringDiagram('|');
      *  |+    +    +
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [END]
      *  [CENTER]
      *  [END]
      */
    public static final NamedTaggedFormation _1x3 =
        new NamedTaggedFormation("1x3",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-2), Fraction.valueOf(0), Rotation.fromAbsoluteString("+")), Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(0), Rotation.fromAbsoluteString("+")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(2), Fraction.valueOf(0), Rotation.fromAbsoluteString("+")), Tag.END));

    /** GENERAL LINE OF 3 formation.
      * @doc.test
      *  js> tf = FormationList.GENERAL_LINE_OF_3; tf.toStringDiagram('|');
      *  ||    |    |
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [END]
      *  [CENTER]
      *  [END]
      */
    public static final NamedTaggedFormation GENERAL_LINE_OF_3 =
        new NamedTaggedFormation("GENERAL LINE OF 3",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-2), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(2), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.END));

    /** GENERAL LINE formation.
      * @doc.test
      *  js> tf = FormationList.GENERAL_LINE; tf.toStringDiagram('|');
      *  ||    |    |    |
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [END]
      *  [CENTER]
      *  [CENTER]
      *  [END]
      */
    public static final NamedTaggedFormation GENERAL_LINE =
        new NamedTaggedFormation("GENERAL LINE",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.END));

    /** GENERAL COLUMN formation.
      * @doc.test
      *  js> tf = FormationList.GENERAL_COLUMN; tf.toStringDiagram('|');
      *  ||
      *  |
      *  ||
      *  |
      *  ||
      *  |
      *  ||
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [END]
      *  [CENTER]
      *  [CENTER]
      *  [END]
      */
    public static final NamedTaggedFormation GENERAL_COLUMN =
        new NamedTaggedFormation("GENERAL COLUMN",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(3), Rotation.fromAbsoluteString("|")), Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(1), Rotation.fromAbsoluteString("|")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-1), Rotation.fromAbsoluteString("|")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-3), Rotation.fromAbsoluteString("|")), Tag.END));

    /** 1x4 formation.
      * @doc.test
      *  js> tf = FormationList._1x4; tf.toStringDiagram('|');
      *  |+    +    +    +
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [END]
      *  [CENTER]
      *  [CENTER]
      *  [END]
      */
    public static final NamedTaggedFormation _1x4 =
        new NamedTaggedFormation("1x4",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(0), Rotation.fromAbsoluteString("+")), Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), Rotation.fromAbsoluteString("+")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), Rotation.fromAbsoluteString("+")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(0), Rotation.fromAbsoluteString("+")), Tag.END));

    /** 2x2 formation.
      * @doc.test
      *  js> tf = FormationList._2x2; tf.toStringDiagram('|');
      *  |+    +
      *  |
      *  |+    +
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  []
      *  []
      *  []
      *  []
      */
    public static final NamedTaggedFormation _2x2 =
        new NamedTaggedFormation("2x2",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(1), Rotation.fromAbsoluteString("+"))),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(1), Rotation.fromAbsoluteString("+"))),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-1), Rotation.fromAbsoluteString("+"))),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-1), Rotation.fromAbsoluteString("+"))));

    /** SINGLE STATIC SQUARE formation.
      * @doc.test
      *  js> tf = FormationList.SINGLE_STATIC_SQUARE; tf.mapStd([]).toStringDiagram('|');
      *  |     1Bv
      *  |
      *  |1G>       3G<
      *  |
      *  |     3B^
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  []
      *  []
      *  []
      *  []
      */
    public static final NamedTaggedFormation SINGLE_STATIC_SQUARE =
        new NamedTaggedFormation("SINGLE STATIC SQUARE",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(2), ExactRotation.SOUTH)),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-2), Fraction.valueOf(0), ExactRotation.EAST)),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(2), Fraction.valueOf(0), ExactRotation.WEST)),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-2), ExactRotation.NORTH)));

    /** FACING COUPLES formation.
      * @doc.test
      *  js> tf = FormationList.FACING_COUPLES; tf.mapStd([]).toStringDiagram('|');
      *  |3Gv  3Bv
      *  |
      *  |1B^  1G^
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [BELLE, TRAILER]
      *  [BEAU, TRAILER]
      *  [BEAU, TRAILER]
      *  [BELLE, TRAILER]
      */
    public static final NamedTaggedFormation FACING_COUPLES =
        new NamedTaggedFormation("FACING COUPLES",
            new StandardDancer[] {
                StandardDancer.COUPLE_3_GIRL,
                StandardDancer.COUPLE_3_BOY,
            },
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(1), ExactRotation.SOUTH), Tag.BELLE, Tag.TRAILER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(1), ExactRotation.SOUTH), Tag.BEAU, Tag.TRAILER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-1), ExactRotation.NORTH), Tag.BEAU, Tag.TRAILER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-1), ExactRotation.NORTH), Tag.BELLE, Tag.TRAILER));

    /** BACK-TO-BACK COUPLES formation.
      * @doc.test
      *  js> tf = FormationList.BACK_TO_BACK_COUPLES; tf.mapStd([]).toStringDiagram('|');
      *  |1B^  1G^
      *  |
      *  |3Gv  3Bv
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [BEAU, LEADER]
      *  [BELLE, LEADER]
      *  [BELLE, LEADER]
      *  [BEAU, LEADER]
      */
    public static final NamedTaggedFormation BACK_TO_BACK_COUPLES =
        new NamedTaggedFormation("BACK-TO-BACK COUPLES",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(1), ExactRotation.NORTH), Tag.BEAU, Tag.LEADER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(1), ExactRotation.NORTH), Tag.BELLE, Tag.LEADER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-1), ExactRotation.SOUTH), Tag.BELLE, Tag.LEADER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-1), ExactRotation.SOUTH), Tag.BEAU, Tag.LEADER));

    /** TANDEM COUPLES formation.
      * @doc.test
      *  js> tf = FormationList.TANDEM_COUPLES; tf.mapStd([]).toStringDiagram('|');
      *  |3B^  3G^
      *  |
      *  |1B^  1G^
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [BEAU, LEADER]
      *  [BELLE, LEADER]
      *  [BEAU, TRAILER]
      *  [BELLE, TRAILER]
      */
    public static final NamedTaggedFormation TANDEM_COUPLES =
        new NamedTaggedFormation("TANDEM COUPLES",
            new StandardDancer[] {
                StandardDancer.COUPLE_3_BOY,
                StandardDancer.COUPLE_3_GIRL,
                StandardDancer.COUPLE_1_BOY,
                StandardDancer.COUPLE_1_GIRL,
            },
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(1), ExactRotation.NORTH), Tag.BEAU, Tag.LEADER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(1), ExactRotation.NORTH), Tag.BELLE, Tag.LEADER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-1), ExactRotation.NORTH), Tag.BEAU, Tag.TRAILER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-1), ExactRotation.NORTH), Tag.BELLE, Tag.TRAILER));

    /** RH OCEAN WAVE formation.
      * @doc.test
      *  js> tf = FormationList.RH_OCEAN_WAVE; tf.mapStd([]).toStringDiagram('|');
      *  |1B^  3Gv  1G^  3Bv
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [BEAU, END]
      *  [BEAU, CENTER]
      *  [BEAU, CENTER]
      *  [BEAU, END]
      */
    public static final NamedTaggedFormation RH_OCEAN_WAVE =
        new NamedTaggedFormation("RH OCEAN WAVE",
            new StandardDancer[] {
                StandardDancer.COUPLE_1_BOY,
                StandardDancer.COUPLE_3_GIRL,
            },
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BEAU, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BEAU, Tag.END));

    /** LH OCEAN WAVE formation.
      * @doc.test
      *  js> tf = FormationList.LH_OCEAN_WAVE; tf.mapStd([]).toStringDiagram('|');
      *  |3Gv  1B^  3Bv  1G^
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [BELLE, END]
      *  [BELLE, CENTER]
      *  [BELLE, CENTER]
      *  [BELLE, END]
      */
    public static final NamedTaggedFormation LH_OCEAN_WAVE =
        new NamedTaggedFormation("LH OCEAN WAVE",
            new StandardDancer[] {
                StandardDancer.COUPLE_3_GIRL,
                StandardDancer.COUPLE_1_BOY,
            },
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BELLE, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BELLE, Tag.END));

    /** RH BOX formation.
      * @doc.test
      *  js> tf = FormationList.RH_BOX; tf.mapStd([]).toStringDiagram('|');
      *  |1B^  1Gv
      *  |
      *  |3G^  3Bv
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [BEAU, LEADER]
      *  [BEAU, TRAILER]
      *  [BEAU, TRAILER]
      *  [BEAU, LEADER]
      */
    public static final NamedTaggedFormation RH_BOX =
        new NamedTaggedFormation("RH BOX",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(1), ExactRotation.NORTH), Tag.BEAU, Tag.LEADER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(1), ExactRotation.SOUTH), Tag.BEAU, Tag.TRAILER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-1), ExactRotation.NORTH), Tag.BEAU, Tag.TRAILER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-1), ExactRotation.SOUTH), Tag.BEAU, Tag.LEADER));

    /** LH BOX formation.
      * @doc.test
      *  js> tf = FormationList.LH_BOX; tf.mapStd([]).toStringDiagram('|');
      *  |1Bv  1G^
      *  |
      *  |3Gv  3B^
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [BELLE, TRAILER]
      *  [BELLE, LEADER]
      *  [BELLE, LEADER]
      *  [BELLE, TRAILER]
      */
    public static final NamedTaggedFormation LH_BOX =
        new NamedTaggedFormation("LH BOX",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(1), ExactRotation.SOUTH), Tag.BELLE, Tag.TRAILER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(1), ExactRotation.NORTH), Tag.BELLE, Tag.LEADER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-1), ExactRotation.SOUTH), Tag.BELLE, Tag.LEADER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-1), ExactRotation.NORTH), Tag.BELLE, Tag.TRAILER));

    /** INVERTED BOX formation.
      * @doc.test
      *  js> tf = FormationList.INVERTED_BOX; tf.mapStd([]).toStringDiagram('|');
      *  |1Bv  1G^
      *  |
      *  |3G^  3Bv
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [BELLE, TRAILER]
      *  [BELLE, LEADER]
      *  [BEAU, TRAILER]
      *  [BEAU, LEADER]
      */
    public static final NamedTaggedFormation INVERTED_BOX =
        new NamedTaggedFormation("INVERTED BOX",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(1), ExactRotation.SOUTH), Tag.BELLE, Tag.TRAILER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(1), ExactRotation.NORTH), Tag.BELLE, Tag.LEADER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-1), ExactRotation.NORTH), Tag.BEAU, Tag.TRAILER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-1), ExactRotation.SOUTH), Tag.BEAU, Tag.LEADER));

    /** RH IN PINWHEEL formation.
      * @doc.test
      *  js> tf = FormationList.RH_IN_PINWHEEL; tf.mapStd([]).toStringDiagram('|');
      *  |1B>  1Gv
      *  |
      *  |3G^  3B<
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [BEAU, TRAILER]
      *  [BEAU, TRAILER]
      *  [BEAU, TRAILER]
      *  [BEAU, TRAILER]
      */
    public static final NamedTaggedFormation RH_IN_PINWHEEL =
        new NamedTaggedFormation("RH IN PINWHEEL",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(1), ExactRotation.EAST), Tag.BEAU, Tag.TRAILER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(1), ExactRotation.SOUTH), Tag.BEAU, Tag.TRAILER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-1), ExactRotation.NORTH), Tag.BEAU, Tag.TRAILER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-1), ExactRotation.WEST), Tag.BEAU, Tag.TRAILER));

    /** LH IN PINWHEEL formation.
      * @doc.test
      *  js> tf = FormationList.LH_IN_PINWHEEL; tf.mapStd([]).toStringDiagram('|');
      *  |1Bv  1G<
      *  |
      *  |3G>  3B^
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [BELLE, TRAILER]
      *  [BELLE, TRAILER]
      *  [BELLE, TRAILER]
      *  [BELLE, TRAILER]
      */
    public static final NamedTaggedFormation LH_IN_PINWHEEL =
        new NamedTaggedFormation("LH IN PINWHEEL",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(1), ExactRotation.SOUTH), Tag.BELLE, Tag.TRAILER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(1), ExactRotation.WEST), Tag.BELLE, Tag.TRAILER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-1), ExactRotation.EAST), Tag.BELLE, Tag.TRAILER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-1), ExactRotation.NORTH), Tag.BELLE, Tag.TRAILER));

    /** RH OUT PINWHEEL formation.
      * @doc.test
      *  js> tf = FormationList.RH_OUT_PINWHEEL; tf.mapStd([]).toStringDiagram('|');
      *  |1B^  1G>
      *  |
      *  |3G<  3Bv
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [BEAU, LEADER]
      *  [BEAU, LEADER]
      *  [BEAU, LEADER]
      *  [BEAU, LEADER]
      */
    public static final NamedTaggedFormation RH_OUT_PINWHEEL =
        new NamedTaggedFormation("RH OUT PINWHEEL",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(1), ExactRotation.NORTH), Tag.BEAU, Tag.LEADER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(1), ExactRotation.EAST), Tag.BEAU, Tag.LEADER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-1), ExactRotation.WEST), Tag.BEAU, Tag.LEADER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-1), ExactRotation.SOUTH), Tag.BEAU, Tag.LEADER));

    /** LH OUT PINWHEEL formation.
      * @doc.test
      *  js> tf = FormationList.LH_OUT_PINWHEEL; tf.mapStd([]).toStringDiagram('|');
      *  |1B<  1G^
      *  |
      *  |3Gv  3B>
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [BELLE, LEADER]
      *  [BELLE, LEADER]
      *  [BELLE, LEADER]
      *  [BELLE, LEADER]
      */
    public static final NamedTaggedFormation LH_OUT_PINWHEEL =
        new NamedTaggedFormation("LH OUT PINWHEEL",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(1), ExactRotation.WEST), Tag.BELLE, Tag.LEADER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(1), ExactRotation.NORTH), Tag.BELLE, Tag.LEADER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-1), ExactRotation.SOUTH), Tag.BELLE, Tag.LEADER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-1), ExactRotation.EAST), Tag.BELLE, Tag.LEADER));

    /** RH SINGLE 1/4 ZEE formation.
      * @doc.test
      *  js> tf = FormationList.RH_SINGLE_QUARTER_ZEE; tf.mapStd([]).toStringDiagram('|');
      *  |     1Bv
      *  |
      *  |1G^  3Gv
      *  |
      *  |3B^
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [BEAU, TRAILER]
      *  [BEAU, LEADER, CENTER]
      *  [BEAU, LEADER, CENTER]
      *  [BEAU, TRAILER]
      */
    public static final NamedTaggedFormation RH_SINGLE_QUARTER_ZEE =
        new NamedTaggedFormation("RH SINGLE 1/4 ZEE",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(2), ExactRotation.SOUTH), Tag.BEAU, Tag.TRAILER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BEAU, Tag.LEADER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BEAU, Tag.LEADER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-2), ExactRotation.NORTH), Tag.BEAU, Tag.TRAILER));

    /** LH SINGLE 1/4 ZEE formation.
      * @doc.test
      *  js> tf = FormationList.LH_SINGLE_QUARTER_ZEE; tf.mapStd([]).toStringDiagram('|');
      *  |1Bv
      *  |
      *  |1Gv  3G^
      *  |
      *  |     3B^
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [BELLE, TRAILER]
      *  [BELLE, LEADER, CENTER]
      *  [BELLE, LEADER, CENTER]
      *  [BELLE, TRAILER]
      */
    public static final NamedTaggedFormation LH_SINGLE_QUARTER_ZEE =
        new NamedTaggedFormation("LH SINGLE 1/4 ZEE",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(2), ExactRotation.SOUTH), Tag.BELLE, Tag.TRAILER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BELLE, Tag.LEADER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BELLE, Tag.LEADER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-2), ExactRotation.NORTH), Tag.BELLE, Tag.TRAILER));

    /** RH SINGLE 3/4 ZEE formation.
      * @doc.test
      *  js> tf = FormationList.RH_SINGLE_THREE_QUARTER_ZEE; tf.mapStd([]).toStringDiagram('|');
      *  |1B^
      *  |
      *  |1G^  3Gv
      *  |
      *  |     3Bv
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [BEAU, LEADER]
      *  [BEAU, TRAILER, CENTER]
      *  [BEAU, TRAILER, CENTER]
      *  [BEAU, LEADER]
      */
    public static final NamedTaggedFormation RH_SINGLE_THREE_QUARTER_ZEE =
        new NamedTaggedFormation("RH SINGLE 3/4 ZEE",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(2), ExactRotation.NORTH), Tag.BEAU, Tag.LEADER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BEAU, Tag.TRAILER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BEAU, Tag.TRAILER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-2), ExactRotation.SOUTH), Tag.BEAU, Tag.LEADER));

    /** LH SINGLE 3/4 ZEE formation.
      * @doc.test
      *  js> tf = FormationList.LH_SINGLE_THREE_QUARTER_ZEE; tf.mapStd([]).toStringDiagram('|');
      *  |     1B^
      *  |
      *  |1Gv  3G^
      *  |
      *  |3Bv
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [BELLE, LEADER]
      *  [BELLE, TRAILER, CENTER]
      *  [BELLE, TRAILER, CENTER]
      *  [BELLE, LEADER]
      */
    public static final NamedTaggedFormation LH_SINGLE_THREE_QUARTER_ZEE =
        new NamedTaggedFormation("LH SINGLE 3/4 ZEE",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(2), ExactRotation.NORTH), Tag.BELLE, Tag.LEADER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BELLE, Tag.TRAILER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BELLE, Tag.TRAILER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-2), ExactRotation.SOUTH), Tag.BELLE, Tag.LEADER));

    /** ONE-FACED LINE formation.
      * @doc.test
      *  js> tf = FormationList.ONE_FACED_LINE; tf.mapStd([]).toStringDiagram('|');
      *  |1B^  1G^  3G^  3B^
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [BEAU, END]
      *  [BELLE, CENTER]
      *  [BEAU, CENTER]
      *  [BELLE, END]
      */
    public static final NamedTaggedFormation ONE_FACED_LINE =
        new NamedTaggedFormation("ONE-FACED LINE",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BEAU, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BELLE, Tag.END));

    /** RH TWO-FACED LINE formation.
      * @doc.test
      *  js> tf = FormationList.RH_TWO_FACED_LINE; tf.mapStd([]).toStringDiagram('|');
      *  |1B^  1G^  3Gv  3Bv
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [BEAU, END]
      *  [BELLE, CENTER]
      *  [BELLE, CENTER]
      *  [BEAU, END]
      */
    public static final NamedTaggedFormation RH_TWO_FACED_LINE =
        new NamedTaggedFormation("RH TWO-FACED LINE",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BEAU, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BEAU, Tag.END));

    /** LH TWO-FACED LINE formation.
      * @doc.test
      *  js> tf = FormationList.LH_TWO_FACED_LINE; tf.mapStd([]).toStringDiagram('|');
      *  |1Bv  1Gv  3G^  3B^
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [BELLE, END]
      *  [BEAU, CENTER]
      *  [BEAU, CENTER]
      *  [BELLE, END]
      */
    public static final NamedTaggedFormation LH_TWO_FACED_LINE =
        new NamedTaggedFormation("LH TWO-FACED LINE",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BELLE, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BELLE, Tag.END));

    /** SINGLE INVERTED LINE formation.
      * @doc.test
      *  js> tf = FormationList.SINGLE_INVERTED_LINE; tf.mapStd([]).toStringDiagram('|');
      *  |1B^  1Gv  3Bv  3G^
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [BEAU, END]
      *  [BEAU, CENTER]
      *  [BELLE, CENTER]
      *  [BELLE, END]
      */
    public static final NamedTaggedFormation SINGLE_INVERTED_LINE =
        new NamedTaggedFormation("SINGLE INVERTED LINE",
            new StandardDancer[] {
                StandardDancer.COUPLE_1_BOY,
                StandardDancer.COUPLE_1_GIRL,
                StandardDancer.COUPLE_3_BOY,
                StandardDancer.COUPLE_3_GIRL,
            },
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BEAU, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BELLE, Tag.END));

    /** RH THREE-AND-ONE LINE formation.
      * @doc.test
      *  js> tf = FormationList.RH_THREE_AND_ONE_LINE; tf.mapStd([]).toStringDiagram('|');
      *  |1B^  1G^  3G^  3Bv
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [BEAU, END]
      *  [BELLE, CENTER]
      *  [BEAU, CENTER]
      *  [BEAU, END]
      */
    public static final NamedTaggedFormation RH_THREE_AND_ONE_LINE =
        new NamedTaggedFormation("RH THREE-AND-ONE LINE",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BEAU, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BEAU, Tag.END));

    /** LH THREE-AND-ONE LINE formation.
      * @doc.test
      *  js> tf = FormationList.LH_THREE_AND_ONE_LINE; tf.mapStd([]).toStringDiagram('|');
      *  |1Bv  1G^  3G^  3B^
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [BELLE, END]
      *  [BELLE, CENTER]
      *  [BEAU, CENTER]
      *  [BELLE, END]
      */
    public static final NamedTaggedFormation LH_THREE_AND_ONE_LINE =
        new NamedTaggedFormation("LH THREE-AND-ONE LINE",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BELLE, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BELLE, Tag.END));

    /** GENERAL DIAMOND formation.
      * @doc.test
      *  js> tf = FormationList.GENERAL_DIAMOND; tf.toStringDiagram('|');
      *  |  -
      *  |
      *  ||    |
      *  |
      *  |  -
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [POINT]
      *  [CENTER]
      *  [CENTER]
      *  [POINT]
      */
    public static final NamedTaggedFormation GENERAL_DIAMOND =
        new NamedTaggedFormation("GENERAL DIAMOND",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(2), Rotation.fromAbsoluteString("-")), Tag.POINT),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-2), Rotation.fromAbsoluteString("-")), Tag.POINT));

    /** GENERAL TALL DIAMOND formation.
      * @doc.test
      *  js> tf = FormationList.GENERAL_TALL_DIAMOND; tf.toStringDiagram('|');
      *  |  -
      *  |
      *  |
      *  ||    |
      *  |
      *  |
      *  |  -
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [POINT]
      *  [CENTER]
      *  [CENTER]
      *  [POINT]
      */
    public static final NamedTaggedFormation GENERAL_TALL_DIAMOND =
        new NamedTaggedFormation("GENERAL TALL DIAMOND",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(3), Rotation.fromAbsoluteString("-")), Tag.POINT),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-3), Rotation.fromAbsoluteString("-")), Tag.POINT));

    /** GENERAL ASYM DIAMOND formation.
      * @doc.test
      *  js> tf = FormationList.GENERAL_ASYM_DIAMOND; tf.toStringDiagram('|');
      *  |     -
      *  |
      *  ||    |
      *  |
      *  |     -
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [POINT]
      *  [CENTER]
      *  [CENTER]
      *  [POINT]
      */
    public static final NamedTaggedFormation GENERAL_ASYM_DIAMOND =
        new NamedTaggedFormation("GENERAL ASYM DIAMOND",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(2), Rotation.fromAbsoluteString("-")), Tag.POINT),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-2), Rotation.fromAbsoluteString("-")), Tag.POINT));

    /** RH DIAMOND formation.
      * @doc.test
      *  js> tf = FormationList.RH_DIAMOND; tf.mapStd([]).toStringDiagram('|');
      *  |  1B>
      *  |
      *  |1G^  3Gv
      *  |
      *  |  3B<
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [POINT]
      *  [BEAU, CENTER]
      *  [BEAU, CENTER]
      *  [POINT]
      */
    public static final NamedTaggedFormation RH_DIAMOND =
        new NamedTaggedFormation("RH DIAMOND",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(2), ExactRotation.EAST), Tag.POINT),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-2), ExactRotation.WEST), Tag.POINT));

    /** RH FACING DIAMOND formation.
      * @doc.test
      *  js> tf = FormationList.RH_FACING_DIAMOND; tf.mapStd([]).toStringDiagram('|');
      *  |  1B<
      *  |
      *  |1G^  3Gv
      *  |
      *  |  3B>
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [POINT]
      *  [BEAU, CENTER]
      *  [BEAU, CENTER]
      *  [POINT]
      */
    public static final NamedTaggedFormation RH_FACING_DIAMOND =
        new NamedTaggedFormation("RH FACING DIAMOND",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(2), ExactRotation.WEST), Tag.POINT),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-2), ExactRotation.EAST), Tag.POINT));

    /** LH DIAMOND formation.
      * @doc.test
      *  js> tf = FormationList.LH_DIAMOND; tf.mapStd([]).toStringDiagram('|');
      *  |  1B<
      *  |
      *  |1Gv  3G^
      *  |
      *  |  3B>
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [POINT]
      *  [BELLE, CENTER]
      *  [BELLE, CENTER]
      *  [POINT]
      */
    public static final NamedTaggedFormation LH_DIAMOND =
        new NamedTaggedFormation("LH DIAMOND",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(2), ExactRotation.WEST), Tag.POINT),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-2), ExactRotation.EAST), Tag.POINT));

    /** LH FACING DIAMOND formation.
      * @doc.test
      *  js> tf = FormationList.LH_FACING_DIAMOND; tf.mapStd([]).toStringDiagram('|');
      *  |  1B>
      *  |
      *  |1Gv  3G^
      *  |
      *  |  3B<
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [POINT]
      *  [BELLE, CENTER]
      *  [BELLE, CENTER]
      *  [POINT]
      */
    public static final NamedTaggedFormation LH_FACING_DIAMOND =
        new NamedTaggedFormation("LH FACING DIAMOND",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(2), ExactRotation.EAST), Tag.POINT),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-2), ExactRotation.WEST), Tag.POINT));

    /** RH STAR formation.
      * @doc.test
      *  js> tf = FormationList.RH_STAR; tf.mapStd([]).toStringDiagram('|');
      *  |  1B>
      *  |1G^  3Gv
      *  |  3B<
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  []
      *  []
      *  []
      *  []
      */
    public static final NamedTaggedFormation RH_STAR =
        new NamedTaggedFormation("RH STAR",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(1), ExactRotation.EAST)),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), ExactRotation.NORTH)),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), ExactRotation.SOUTH)),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-1), ExactRotation.WEST)));

    /** LH STAR formation.
      * @doc.test
      *  js> tf = FormationList.LH_STAR; tf.mapStd([]).toStringDiagram('|');
      *  |  1B<
      *  |1Gv  3G^
      *  |  3B>
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  []
      *  []
      *  []
      *  []
      */
    public static final NamedTaggedFormation LH_STAR =
        new NamedTaggedFormation("LH STAR",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(1), ExactRotation.WEST)),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), ExactRotation.SOUTH)),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), ExactRotation.NORTH)),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-1), ExactRotation.EAST)));

    /** RH SINGLE PROMENADE formation.
      * @doc.test
      *  js> tf = FormationList.RH_SINGLE_PROMENADE; tf.mapStd([]).toStringDiagram('|');
      *  |     1B>
      *  |
      *  |1G^       3Gv
      *  |
      *  |     3B<
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  []
      *  []
      *  []
      *  []
      */
    public static final NamedTaggedFormation RH_SINGLE_PROMENADE =
        new NamedTaggedFormation("RH SINGLE PROMENADE",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(2), ExactRotation.EAST)),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-2), Fraction.valueOf(0), ExactRotation.NORTH)),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(2), Fraction.valueOf(0), ExactRotation.SOUTH)),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-2), ExactRotation.WEST)));

    /** LH SINGLE PROMENADE formation.
      * @doc.test
      *  js> tf = FormationList.LH_SINGLE_PROMENADE; tf.mapStd([]).toStringDiagram('|');
      *  |     1B<
      *  |
      *  |1Gv       3G^
      *  |
      *  |     3B>
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  []
      *  []
      *  []
      *  []
      */
    public static final NamedTaggedFormation LH_SINGLE_PROMENADE =
        new NamedTaggedFormation("LH SINGLE PROMENADE",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(2), ExactRotation.WEST)),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-2), Fraction.valueOf(0), ExactRotation.SOUTH)),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(2), Fraction.valueOf(0), ExactRotation.NORTH)),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-2), ExactRotation.EAST)));

    /** GENERAL SINGLE QUARTER TAG formation.
      * @doc.test
      *  js> tf = FormationList.GENERAL_SINGLE_QUARTER_TAG; tf.toStringDiagram('|');
      *  |  |
      *  |
      *  ||    |
      *  |
      *  |  |
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [END]
      *  [CENTER]
      *  [CENTER]
      *  [END]
      */
    public static final NamedTaggedFormation GENERAL_SINGLE_QUARTER_TAG =
        new NamedTaggedFormation("GENERAL SINGLE QUARTER TAG",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(2), Rotation.fromAbsoluteString("|")), Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-2), Rotation.fromAbsoluteString("|")), Tag.END));

    /** GENERAL ASYM SINGLE QUARTER TAG formation.
      * @doc.test
      *  js> tf = FormationList.GENERAL_ASYM_SINGLE_QUARTER_TAG; tf.toStringDiagram('|');
      *  |     |
      *  |
      *  ||    |
      *  |
      *  |     |
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [END]
      *  [CENTER]
      *  [CENTER]
      *  [END]
      */
    public static final NamedTaggedFormation GENERAL_ASYM_SINGLE_QUARTER_TAG =
        new NamedTaggedFormation("GENERAL ASYM SINGLE QUARTER TAG",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(2), Rotation.fromAbsoluteString("|")), Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-2), Rotation.fromAbsoluteString("|")), Tag.END));

    /** RH SINGLE 1/4 TAG formation.
      * @doc.test
      *  js> tf = FormationList.RH_SINGLE_QUARTER_TAG; tf.mapStd([]).toStringDiagram('|');
      *  |  1Bv
      *  |
      *  |1G^  3Gv
      *  |
      *  |  3B^
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [END]
      *  [BEAU, CENTER]
      *  [BEAU, CENTER]
      *  [END]
      */
    public static final NamedTaggedFormation RH_SINGLE_QUARTER_TAG =
        new NamedTaggedFormation("RH SINGLE 1/4 TAG",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(2), ExactRotation.SOUTH), Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-2), ExactRotation.NORTH), Tag.END));

    /** LH SINGLE 1/4 TAG formation.
      * @doc.test
      *  js> tf = FormationList.LH_SINGLE_QUARTER_TAG; tf.mapStd([]).toStringDiagram('|');
      *  |  1Bv
      *  |
      *  |1Gv  3G^
      *  |
      *  |  3B^
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [END]
      *  [BELLE, CENTER]
      *  [BELLE, CENTER]
      *  [END]
      */
    public static final NamedTaggedFormation LH_SINGLE_QUARTER_TAG =
        new NamedTaggedFormation("LH SINGLE 1/4 TAG",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(2), ExactRotation.SOUTH), Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-2), ExactRotation.NORTH), Tag.END));

    /** RH SINGLE 3/4 TAG formation.
      * @doc.test
      *  js> tf = FormationList.RH_SINGLE_THREE_QUARTER_TAG; tf.mapStd([]).toStringDiagram('|');
      *  |  1B^
      *  |
      *  |1G^  3Gv
      *  |
      *  |  3Bv
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [END]
      *  [BEAU, CENTER]
      *  [BEAU, CENTER]
      *  [END]
      */
    public static final NamedTaggedFormation RH_SINGLE_THREE_QUARTER_TAG =
        new NamedTaggedFormation("RH SINGLE 3/4 TAG",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(2), ExactRotation.NORTH), Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-2), ExactRotation.SOUTH), Tag.END));

    /** LH SINGLE 3/4 TAG formation.
      * @doc.test
      *  js> tf = FormationList.LH_SINGLE_THREE_QUARTER_TAG; tf.mapStd([]).toStringDiagram('|');
      *  |  1B^
      *  |
      *  |1Gv  3G^
      *  |
      *  |  3Bv
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [END]
      *  [BELLE, CENTER]
      *  [BELLE, CENTER]
      *  [END]
      */
    public static final NamedTaggedFormation LH_SINGLE_THREE_QUARTER_TAG =
        new NamedTaggedFormation("LH SINGLE 3/4 TAG",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(2), ExactRotation.NORTH), Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-2), ExactRotation.SOUTH), Tag.END));

    /** SINGLE DOUBLE PASS THRU formation.
      * @doc.test
      *  js> tf = FormationList.SINGLE_DOUBLE_PASS_THRU; tf.mapStd([]).toStringDiagram('|');
      *  |1Bv
      *  |
      *  |1Gv
      *  |
      *  |3G^
      *  |
      *  |3B^
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [TRAILER, END, NUMBER_4]
      *  [LEADER, CENTER, NUMBER_3]
      *  [LEADER, CENTER, NUMBER_3]
      *  [TRAILER, END, NUMBER_4]
      */
    public static final NamedTaggedFormation SINGLE_DOUBLE_PASS_THRU =
        new NamedTaggedFormation("SINGLE DOUBLE PASS THRU",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(3), ExactRotation.SOUTH), Tag.TRAILER, Tag.END, Tag.NUMBER_4),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(1), ExactRotation.SOUTH), Tag.LEADER, Tag.CENTER, Tag.NUMBER_3),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-1), ExactRotation.NORTH), Tag.LEADER, Tag.CENTER, Tag.NUMBER_3),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-3), ExactRotation.NORTH), Tag.TRAILER, Tag.END, Tag.NUMBER_4));

    /** COMPLETED SINGLE DOUBLE PASS THRU formation.
      * @doc.test
      *  js> tf = FormationList.COMPLETED_SINGLE_DOUBLE_PASS_THRU; tf.mapStd([]).toStringDiagram('|');
      *  |1B^
      *  |
      *  |1G^
      *  |
      *  |3Gv
      *  |
      *  |3Bv
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [LEADER, END, NUMBER_1]
      *  [TRAILER, CENTER, NUMBER_2]
      *  [TRAILER, CENTER, NUMBER_2]
      *  [LEADER, END, NUMBER_1]
      */
    public static final NamedTaggedFormation COMPLETED_SINGLE_DOUBLE_PASS_THRU =
        new NamedTaggedFormation("COMPLETED SINGLE DOUBLE PASS THRU",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(3), ExactRotation.NORTH), Tag.LEADER, Tag.END, Tag.NUMBER_1),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(1), ExactRotation.NORTH), Tag.TRAILER, Tag.CENTER, Tag.NUMBER_2),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-1), ExactRotation.SOUTH), Tag.TRAILER, Tag.CENTER, Tag.NUMBER_2),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-3), ExactRotation.SOUTH), Tag.LEADER, Tag.END, Tag.NUMBER_1));

    /** 1x8 formation.
      * @doc.test
      *  js> tf = FormationList._1x8; tf.toStringDiagram('|');
      *  |+    +    +    +    +    +    +    +
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [END, OUTSIDE_2, OUTSIDE_4, OUTSIDE_6]
      *  [CENTER, OUTSIDE_4, CENTER_6, OUTSIDE_6]
      *  [CENTER, CENTER_6, OUTSIDE_6]
      *  [VERY_CENTER, END, CENTER_6]
      *  [VERY_CENTER, END, CENTER_6]
      *  [CENTER, CENTER_6, OUTSIDE_6]
      *  [CENTER, OUTSIDE_4, CENTER_6, OUTSIDE_6]
      *  [END, OUTSIDE_2, OUTSIDE_4, OUTSIDE_6]
      */
    public static final NamedTaggedFormation _1x8 =
        new NamedTaggedFormation("1x8",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-7), Fraction.valueOf(0), Rotation.fromAbsoluteString("+")), Tag.END, Tag.OUTSIDE_2, Tag.OUTSIDE_4, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-5), Fraction.valueOf(0), Rotation.fromAbsoluteString("+")), Tag.CENTER, Tag.OUTSIDE_4, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(0), Rotation.fromAbsoluteString("+")), Tag.CENTER, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), Rotation.fromAbsoluteString("+")), Tag.VERY_CENTER, Tag.END, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), Rotation.fromAbsoluteString("+")), Tag.VERY_CENTER, Tag.END, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(0), Rotation.fromAbsoluteString("+")), Tag.CENTER, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(5), Fraction.valueOf(0), Rotation.fromAbsoluteString("+")), Tag.CENTER, Tag.OUTSIDE_4, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(7), Fraction.valueOf(0), Rotation.fromAbsoluteString("+")), Tag.END, Tag.OUTSIDE_2, Tag.OUTSIDE_4, Tag.OUTSIDE_6));

    /** 2x4 formation.
      * @doc.test
      *  js> tf = FormationList._2x4; tf.toStringDiagram('|');
      *  |+    +    +    +
      *  |
      *  |+    +    +    +
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [END]
      *  [CENTER]
      *  [CENTER]
      *  [END]
      *  [END]
      *  [CENTER]
      *  [CENTER]
      *  [END]
      */
    public static final NamedTaggedFormation _2x4 =
        new NamedTaggedFormation("2x4",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(1), Rotation.fromAbsoluteString("+")), Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(1), Rotation.fromAbsoluteString("+")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(1), Rotation.fromAbsoluteString("+")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(1), Rotation.fromAbsoluteString("+")), Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(-1), Rotation.fromAbsoluteString("+")), Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-1), Rotation.fromAbsoluteString("+")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-1), Rotation.fromAbsoluteString("+")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(-1), Rotation.fromAbsoluteString("+")), Tag.END));

    /** PARALLEL GENERAL LINES formation.
      * @doc.test
      *  js> tf = FormationList.PARALLEL_GENERAL_LINES; tf.toStringDiagram('|');
      *  ||    |    |    |
      *  |
      *  ||    |    |    |
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [END]
      *  [CENTER]
      *  [CENTER]
      *  [END]
      *  [END]
      *  [CENTER]
      *  [CENTER]
      *  [END]
      */
    public static final NamedTaggedFormation PARALLEL_GENERAL_LINES =
        new NamedTaggedFormation("PARALLEL GENERAL LINES",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(1), Rotation.fromAbsoluteString("|")), Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(1), Rotation.fromAbsoluteString("|")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(1), Rotation.fromAbsoluteString("|")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(1), Rotation.fromAbsoluteString("|")), Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(-1), Rotation.fromAbsoluteString("|")), Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-1), Rotation.fromAbsoluteString("|")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-1), Rotation.fromAbsoluteString("|")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(-1), Rotation.fromAbsoluteString("|")), Tag.END));

    /** GENERAL COLUMNS formation.
      * @doc.test
      *  js> tf = FormationList.GENERAL_COLUMNS; tf.toStringDiagram('|');
      *  ||    |
      *  |
      *  ||    |
      *  |
      *  ||    |
      *  |
      *  ||    |
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [END]
      *  [END]
      *  [CENTER]
      *  [CENTER]
      *  [CENTER]
      *  [CENTER]
      *  [END]
      *  [END]
      */
    public static final NamedTaggedFormation GENERAL_COLUMNS =
        new NamedTaggedFormation("GENERAL COLUMNS",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(3), Rotation.fromAbsoluteString("|")), Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(3), Rotation.fromAbsoluteString("|")), Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(1), Rotation.fromAbsoluteString("|")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(1), Rotation.fromAbsoluteString("|")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-1), Rotation.fromAbsoluteString("|")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-1), Rotation.fromAbsoluteString("|")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-3), Rotation.fromAbsoluteString("|")), Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-3), Rotation.fromAbsoluteString("|")), Tag.END));

    /** STATIC SQUARE formation.
      * @doc.test
      *  js> tf = FormationList.STATIC_SQUARE; tf.mapStd([]).toStringDiagram('|');
      *  |     3Gv  3Bv
      *  |
      *  |4B>            2G<
      *  |
      *  |4G>            2B<
      *  |
      *  |     1B^  1G^
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [BELLE]
      *  [BEAU]
      *  [BEAU]
      *  [BELLE]
      *  [BELLE]
      *  [BEAU]
      *  [BEAU]
      *  [BELLE]
      */
    public static final NamedTaggedFormation STATIC_SQUARE =
        new NamedTaggedFormation("STATIC SQUARE",
            new StandardDancer[] {
                StandardDancer.COUPLE_3_GIRL,
                StandardDancer.COUPLE_3_BOY,
                StandardDancer.COUPLE_4_BOY,
                StandardDancer.COUPLE_2_GIRL,
            },
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(3), ExactRotation.SOUTH), Tag.BELLE),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(3), ExactRotation.SOUTH), Tag.BEAU),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(1), ExactRotation.EAST), Tag.BEAU),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(1), ExactRotation.WEST), Tag.BELLE),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(-1), ExactRotation.EAST), Tag.BELLE),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(-1), ExactRotation.WEST), Tag.BEAU),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-3), ExactRotation.NORTH), Tag.BEAU),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-3), ExactRotation.NORTH), Tag.BELLE));

    /** STATIC SQUARE FACING OUT formation.
      * @doc.test
      *  js> tf = FormationList.STATIC_SQUARE_FACING_OUT; tf.mapStd([]).toStringDiagram('|');
      *  |     3B^  3G^
      *  |
      *  |4G<            2B>
      *  |
      *  |4B<            2G>
      *  |
      *  |     1Gv  1Bv
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [BEAU]
      *  [BELLE]
      *  [BELLE]
      *  [BEAU]
      *  [BEAU]
      *  [BELLE]
      *  [BELLE]
      *  [BEAU]
      */
    public static final NamedTaggedFormation STATIC_SQUARE_FACING_OUT =
        new NamedTaggedFormation("STATIC SQUARE FACING OUT",
            new StandardDancer[] {
                StandardDancer.COUPLE_3_BOY,
                StandardDancer.COUPLE_3_GIRL,
                StandardDancer.COUPLE_4_GIRL,
                StandardDancer.COUPLE_2_BOY,
            },
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(3), ExactRotation.NORTH), Tag.BEAU),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(3), ExactRotation.NORTH), Tag.BELLE),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(1), ExactRotation.WEST), Tag.BELLE),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(1), ExactRotation.EAST), Tag.BEAU),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(-1), ExactRotation.WEST), Tag.BEAU),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(-1), ExactRotation.EAST), Tag.BELLE),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-3), ExactRotation.SOUTH), Tag.BELLE),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-3), ExactRotation.SOUTH), Tag.BEAU));

    /** SINGLE FILE PROMENADE formation.
      * @doc.test
      *  js> tf = FormationList.SINGLE_FILE_PROMENADE; tf.mapStd([]).toStringDiagram('|');
      *  |     3G<  3B<
      *  |
      *  |4Bv            2G^
      *  |
      *  |4Gv            2B^
      *  |
      *  |     1B>  1G>
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [LEADER]
      *  [TRAILER]
      *  [TRAILER]
      *  [LEADER]
      *  [LEADER]
      *  [TRAILER]
      *  [TRAILER]
      *  [LEADER]
      */
    public static final NamedTaggedFormation SINGLE_FILE_PROMENADE =
        new NamedTaggedFormation("SINGLE FILE PROMENADE",
            new StandardDancer[] {
                StandardDancer.COUPLE_3_GIRL,
                StandardDancer.COUPLE_3_BOY,
                StandardDancer.COUPLE_4_BOY,
                StandardDancer.COUPLE_2_GIRL,
            },
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(3), ExactRotation.WEST), Tag.LEADER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(3), ExactRotation.WEST), Tag.TRAILER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(1), ExactRotation.SOUTH), Tag.TRAILER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(1), ExactRotation.NORTH), Tag.LEADER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(-1), ExactRotation.SOUTH), Tag.LEADER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(-1), ExactRotation.NORTH), Tag.TRAILER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-3), ExactRotation.EAST), Tag.TRAILER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-3), ExactRotation.EAST), Tag.LEADER));

    /** REVERSE SINGLE FILE PROMENADE formation.
      * @doc.test
      *  js> tf = FormationList.REVERSE_SINGLE_FILE_PROMENADE; tf.mapStd([]).toStringDiagram('|');
      *  |     3G>  3B>
      *  |
      *  |4B^            2Gv
      *  |
      *  |4G^            2Bv
      *  |
      *  |     1B<  1G<
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [TRAILER]
      *  [LEADER]
      *  [LEADER]
      *  [TRAILER]
      *  [TRAILER]
      *  [LEADER]
      *  [LEADER]
      *  [TRAILER]
      */
    public static final NamedTaggedFormation REVERSE_SINGLE_FILE_PROMENADE =
        new NamedTaggedFormation("REVERSE SINGLE FILE PROMENADE",
            new StandardDancer[] {
                StandardDancer.COUPLE_3_GIRL,
                StandardDancer.COUPLE_3_BOY,
                StandardDancer.COUPLE_4_BOY,
                StandardDancer.COUPLE_2_GIRL,
            },
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(3), ExactRotation.EAST), Tag.TRAILER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(3), ExactRotation.EAST), Tag.LEADER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(1), ExactRotation.NORTH), Tag.LEADER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(1), ExactRotation.SOUTH), Tag.TRAILER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(-1), ExactRotation.NORTH), Tag.TRAILER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(-1), ExactRotation.SOUTH), Tag.LEADER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-3), ExactRotation.WEST), Tag.LEADER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-3), ExactRotation.WEST), Tag.TRAILER));

    /** RH ALAMO RING formation.
      * @doc.test
      *  js> tf = FormationList.RH_ALAMO_RING; tf.mapStd([]).toStringDiagram('|');
      *  |     3G^  3Bv
      *  |
      *  |4B>            2G>
      *  |
      *  |4G<            2B<
      *  |
      *  |     1B^  1Gv
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [BEAU]
      *  [BEAU]
      *  [BEAU]
      *  [BEAU]
      *  [BEAU]
      *  [BEAU]
      *  [BEAU]
      *  [BEAU]
      */
    public static final NamedTaggedFormation RH_ALAMO_RING =
        new NamedTaggedFormation("RH ALAMO RING",
            new StandardDancer[] {
                StandardDancer.COUPLE_3_GIRL,
                StandardDancer.COUPLE_3_BOY,
                StandardDancer.COUPLE_4_BOY,
                StandardDancer.COUPLE_2_GIRL,
            },
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(3), ExactRotation.NORTH), Tag.BEAU),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(3), ExactRotation.SOUTH), Tag.BEAU),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(1), ExactRotation.EAST), Tag.BEAU),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(1), ExactRotation.EAST), Tag.BEAU),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(-1), ExactRotation.WEST), Tag.BEAU),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(-1), ExactRotation.WEST), Tag.BEAU),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-3), ExactRotation.NORTH), Tag.BEAU),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-3), ExactRotation.SOUTH), Tag.BEAU));

    /** LH ALAMO RING formation.
      * @doc.test
      *  js> tf = FormationList.LH_ALAMO_RING; tf.mapStd([]).toStringDiagram('|');
      *  |     3Gv  3B^
      *  |
      *  |4B<            2G<
      *  |
      *  |4G>            2B>
      *  |
      *  |     1Bv  1G^
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [BELLE]
      *  [BELLE]
      *  [BELLE]
      *  [BELLE]
      *  [BELLE]
      *  [BELLE]
      *  [BELLE]
      *  [BELLE]
      */
    public static final NamedTaggedFormation LH_ALAMO_RING =
        new NamedTaggedFormation("LH ALAMO RING",
            new StandardDancer[] {
                StandardDancer.COUPLE_3_GIRL,
                StandardDancer.COUPLE_3_BOY,
                StandardDancer.COUPLE_4_BOY,
                StandardDancer.COUPLE_2_GIRL,
            },
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(3), ExactRotation.SOUTH), Tag.BELLE),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(3), ExactRotation.NORTH), Tag.BELLE),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(1), ExactRotation.WEST), Tag.BELLE),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(1), ExactRotation.WEST), Tag.BELLE),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(-1), ExactRotation.EAST), Tag.BELLE),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(-1), ExactRotation.EAST), Tag.BELLE),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-3), ExactRotation.SOUTH), Tag.BELLE),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-3), ExactRotation.NORTH), Tag.BELLE));

    /** O SPOTS formation.
      * @doc.test
      *  js> tf = FormationList.O_SPOTS; tf.toStringDiagram('|');
      *  |     o    o
      *  |
      *  |o              o
      *  |
      *  |o              o
      *  |
      *  |     o    o
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  []
      *  []
      *  []
      *  []
      *  []
      *  []
      *  []
      *  []
      */
    public static final NamedTaggedFormation O_SPOTS =
        new NamedTaggedFormation("O SPOTS",
            new StandardDancer[] {
                StandardDancer.COUPLE_3_GIRL,
                StandardDancer.COUPLE_3_BOY,
                StandardDancer.COUPLE_4_BOY,
                StandardDancer.COUPLE_2_GIRL,
            },
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(3), Rotation.fromAbsoluteString("o"))),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(3), Rotation.fromAbsoluteString("o"))),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(1), Rotation.fromAbsoluteString("o"))),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(1), Rotation.fromAbsoluteString("o"))),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(-1), Rotation.fromAbsoluteString("o"))),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(-1), Rotation.fromAbsoluteString("o"))),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-3), Rotation.fromAbsoluteString("o"))),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-3), Rotation.fromAbsoluteString("o"))));

    /** PROMENADE formation.
      * @doc.test
      *  js> tf = FormationList.PROMENADE; tf.mapStd([]).toStringDiagram('|');
      *  |          3G<
      *  |
      *  |          3B<
      *  |
      *  |4Gv  4Bv       2B^  2G^
      *  |
      *  |          1B>
      *  |
      *  |          1G>
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [BELLE, END]
      *  [BEAU, CENTER]
      *  [BELLE, END]
      *  [BEAU, CENTER]
      *  [BEAU, CENTER]
      *  [BELLE, END]
      *  [BEAU, CENTER]
      *  [BELLE, END]
      */
    public static final NamedTaggedFormation PROMENADE =
        new NamedTaggedFormation("PROMENADE",
            new StandardDancer[] {
                StandardDancer.COUPLE_3_GIRL,
                StandardDancer.COUPLE_3_BOY,
                StandardDancer.COUPLE_4_GIRL,
                StandardDancer.COUPLE_4_BOY,
            },
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(4), ExactRotation.WEST), Tag.BELLE, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(2), ExactRotation.WEST), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-4), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BELLE, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-2), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(2), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(4), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BELLE, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-2), ExactRotation.EAST), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-4), ExactRotation.EAST), Tag.BELLE, Tag.END));

    /** WRONG WAY PROMENADE formation.
      * @doc.test
      *  js> tf = FormationList.WRONG_WAY_PROMENADE; tf.mapStd([]).toStringDiagram('|');
      *  |          1B>
      *  |
      *  |          1G>
      *  |
      *  |2B^  2G^       4Gv  4Bv
      *  |
      *  |          3G<
      *  |
      *  |          3B<
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [BEAU, END]
      *  [BELLE, CENTER]
      *  [BEAU, END]
      *  [BELLE, CENTER]
      *  [BELLE, CENTER]
      *  [BEAU, END]
      *  [BELLE, CENTER]
      *  [BEAU, END]
      */
    public static final NamedTaggedFormation WRONG_WAY_PROMENADE =
        new NamedTaggedFormation("WRONG WAY PROMENADE",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(4), ExactRotation.EAST), Tag.BEAU, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(2), ExactRotation.EAST), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-4), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BEAU, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-2), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(2), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(4), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BEAU, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-2), ExactRotation.WEST), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-4), ExactRotation.WEST), Tag.BEAU, Tag.END));

    /** STAR PROMENADE formation.
      * @doc.test
      *  js> tf = FormationList.STAR_PROMENADE; tf.mapStd([]).toStringDiagram('|');
      *  |       3G<
      *  |
      *  |       3B<
      *  |4Gv  4Bv  2B^  2G^
      *  |       1B>
      *  |
      *  |       1G>
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [BELLE, END]
      *  [BEAU, CENTER]
      *  [BELLE, END]
      *  [BEAU, CENTER]
      *  [BEAU, CENTER]
      *  [BELLE, END]
      *  [BEAU, CENTER]
      *  [BELLE, END]
      */
    public static final NamedTaggedFormation STAR_PROMENADE =
        new NamedTaggedFormation("STAR PROMENADE",
            new StandardDancer[] {
                StandardDancer.COUPLE_3_GIRL,
                StandardDancer.COUPLE_3_BOY,
                StandardDancer.COUPLE_4_GIRL,
                StandardDancer.COUPLE_4_BOY,
            },
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(3), ExactRotation.WEST), Tag.BELLE, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(1), ExactRotation.WEST), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BELLE, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BELLE, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-1), ExactRotation.EAST), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-3), ExactRotation.EAST), Tag.BELLE, Tag.END));

    /** WRONG WAY STAR PROMENADE formation.
      * @doc.test
      *  js> tf = FormationList.WRONG_WAY_STAR_PROMENADE; tf.mapStd([]).toStringDiagram('|');
      *  |       1B>
      *  |
      *  |       1G>
      *  |2B^  2G^  4Gv  4Bv
      *  |       3G<
      *  |
      *  |       3B<
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [BEAU, END]
      *  [BELLE, CENTER]
      *  [BEAU, END]
      *  [BELLE, CENTER]
      *  [BELLE, CENTER]
      *  [BEAU, END]
      *  [BELLE, CENTER]
      *  [BEAU, END]
      */
    public static final NamedTaggedFormation WRONG_WAY_STAR_PROMENADE =
        new NamedTaggedFormation("WRONG WAY STAR PROMENADE",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(3), ExactRotation.EAST), Tag.BEAU, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(1), ExactRotation.EAST), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BEAU, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BEAU, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-1), ExactRotation.WEST), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-3), ExactRotation.WEST), Tag.BEAU, Tag.END));

    /** THAR formation.
      * @doc.test
      *  js> tf = FormationList.THAR; tf.mapStd([]).toStringDiagram('|');
      *  |       1B<
      *  |
      *  |       1G>
      *  |2Bv  2G^  4Gv  4B^
      *  |       3G<
      *  |
      *  |       3B>
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [BELLE, END]
      *  [BELLE, CENTER]
      *  [BELLE, END]
      *  [BELLE, CENTER]
      *  [BELLE, CENTER]
      *  [BELLE, END]
      *  [BELLE, CENTER]
      *  [BELLE, END]
      */
    public static final NamedTaggedFormation THAR =
        new NamedTaggedFormation("THAR",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(3), ExactRotation.WEST), Tag.BELLE, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(1), ExactRotation.EAST), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BELLE, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BELLE, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-1), ExactRotation.WEST), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-3), ExactRotation.EAST), Tag.BELLE, Tag.END));

    /** WRONG WAY THAR formation.
      * @doc.test
      *  js> tf = FormationList.WRONG_WAY_THAR; tf.mapStd([]).toStringDiagram('|');
      *  |       1B>
      *  |
      *  |       1G<
      *  |2B^  2Gv  4G^  4Bv
      *  |       3G>
      *  |
      *  |       3B<
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [BEAU, END]
      *  [BEAU, CENTER]
      *  [BEAU, END]
      *  [BEAU, CENTER]
      *  [BEAU, CENTER]
      *  [BEAU, END]
      *  [BEAU, CENTER]
      *  [BEAU, END]
      */
    public static final NamedTaggedFormation WRONG_WAY_THAR =
        new NamedTaggedFormation("WRONG WAY THAR",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(3), ExactRotation.EAST), Tag.BEAU, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(1), ExactRotation.WEST), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BEAU, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BEAU, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-1), ExactRotation.EAST), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-3), ExactRotation.WEST), Tag.BEAU, Tag.END));

    /** RIGHT AND LEFT GRAND formation.
      * @doc.test
      *  js> tf = FormationList.RIGHT_AND_LEFT_GRAND; tf.mapStd([]).toStringDiagram('|');
      *  |          1B>
      *  |
      *  |          1G<
      *  |
      *  |2B^  2Gv       4G^  4Bv
      *  |
      *  |          3G>
      *  |
      *  |          3B<
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [BEAU, END]
      *  [BEAU, CENTER]
      *  [BEAU, END]
      *  [BEAU, CENTER]
      *  [BEAU, CENTER]
      *  [BEAU, END]
      *  [BEAU, CENTER]
      *  [BEAU, END]
      */
    public static final NamedTaggedFormation RIGHT_AND_LEFT_GRAND =
        new NamedTaggedFormation("RIGHT AND LEFT GRAND",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(4), ExactRotation.EAST), Tag.BEAU, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(2), ExactRotation.WEST), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-4), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BEAU, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-2), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(2), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(4), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BEAU, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-2), ExactRotation.EAST), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-4), ExactRotation.WEST), Tag.BEAU, Tag.END));

    /** RIGHT AND LEFT GRAND DIAMOND formation.
      * @doc.test
      *  js> tf = FormationList.RIGHT_AND_LEFT_GRAND_DIAMOND; tf.mapStd([]).toStringDiagram('|');
      *  |       1B>
      *  |
      *  |       1G<
      *  |
      *  |2B^  2Gv  4G^  4Bv
      *  |
      *  |       3G>
      *  |
      *  |       3B<
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [BEAU, POINT, END, OUTSIDE_2, OUTSIDE_6]
      *  [BEAU, POINT, CENTER, CENTER_6, OUTSIDE_6]
      *  [BEAU, END, CENTER_6, OUTSIDE_6]
      *  [BEAU, CENTER, VERY_CENTER, CENTER_6]
      *  [BEAU, CENTER, VERY_CENTER, CENTER_6]
      *  [BEAU, END, CENTER_6, OUTSIDE_6]
      *  [BEAU, POINT, CENTER, CENTER_6, OUTSIDE_6]
      *  [BEAU, POINT, END, OUTSIDE_2, OUTSIDE_6]
      */
    public static final NamedTaggedFormation RIGHT_AND_LEFT_GRAND_DIAMOND =
        new NamedTaggedFormation("RIGHT AND LEFT GRAND DIAMOND",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(4), ExactRotation.EAST), Tag.BEAU, Tag.POINT, Tag.END, Tag.OUTSIDE_2, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(2), ExactRotation.WEST), Tag.BEAU, Tag.POINT, Tag.CENTER, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BEAU, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BEAU, Tag.CENTER, Tag.VERY_CENTER, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BEAU, Tag.CENTER, Tag.VERY_CENTER, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BEAU, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-2), ExactRotation.EAST), Tag.BEAU, Tag.POINT, Tag.CENTER, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-4), ExactRotation.WEST), Tag.BEAU, Tag.POINT, Tag.END, Tag.OUTSIDE_2, Tag.OUTSIDE_6));

    /** LEFT AND RIGHT GRAND formation.
      * @doc.test
      *  js> tf = FormationList.LEFT_AND_RIGHT_GRAND; tf.mapStd([]).toStringDiagram('|');
      *  |          1B<
      *  |
      *  |          1G>
      *  |
      *  |2Bv  2G^       4Gv  4B^
      *  |
      *  |          3G<
      *  |
      *  |          3B>
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [BELLE, END]
      *  [BELLE, CENTER]
      *  [BELLE, END]
      *  [BELLE, CENTER]
      *  [BELLE, CENTER]
      *  [BELLE, END]
      *  [BELLE, CENTER]
      *  [BELLE, END]
      */
    public static final NamedTaggedFormation LEFT_AND_RIGHT_GRAND =
        new NamedTaggedFormation("LEFT AND RIGHT GRAND",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(4), ExactRotation.WEST), Tag.BELLE, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(2), ExactRotation.EAST), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-4), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BELLE, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-2), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(2), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(4), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BELLE, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-2), ExactRotation.WEST), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-4), ExactRotation.EAST), Tag.BELLE, Tag.END));

    /** FACING LINES formation.
      * @doc.test
      *  js> tf = FormationList.FACING_LINES; tf.mapStd([]).toStringDiagram('|');
      *  |4Gv  4Bv  3Gv  3Bv
      *  |
      *  |1B^  1G^  2B^  2G^
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [BELLE, TRAILER, END]
      *  [BEAU, TRAILER, CENTER]
      *  [BELLE, TRAILER, CENTER]
      *  [BEAU, TRAILER, END]
      *  [BEAU, TRAILER, END]
      *  [BELLE, TRAILER, CENTER]
      *  [BEAU, TRAILER, CENTER]
      *  [BELLE, TRAILER, END]
      */
    public static final NamedTaggedFormation FACING_LINES =
        new NamedTaggedFormation("FACING LINES",
            new StandardDancer[] {
                StandardDancer.COUPLE_4_GIRL,
                StandardDancer.COUPLE_4_BOY,
                StandardDancer.COUPLE_3_GIRL,
                StandardDancer.COUPLE_3_BOY,
            },
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(1), ExactRotation.SOUTH), Tag.BELLE, Tag.TRAILER, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(1), ExactRotation.SOUTH), Tag.BEAU, Tag.TRAILER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(1), ExactRotation.SOUTH), Tag.BELLE, Tag.TRAILER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(1), ExactRotation.SOUTH), Tag.BEAU, Tag.TRAILER, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(-1), ExactRotation.NORTH), Tag.BEAU, Tag.TRAILER, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-1), ExactRotation.NORTH), Tag.BELLE, Tag.TRAILER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-1), ExactRotation.NORTH), Tag.BEAU, Tag.TRAILER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(-1), ExactRotation.NORTH), Tag.BELLE, Tag.TRAILER, Tag.END));

    /** EIGHT CHAIN THRU formation.
      * @doc.test
      *  js> tf = FormationList.EIGHT_CHAIN_THRU; tf.mapStd([]).toStringDiagram('|');
      *  |1Gv  1Bv
      *  |
      *  |2B^  2G^
      *  |
      *  |4Gv  4Bv
      *  |
      *  |3B^  3G^
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [BELLE, TRAILER, END, NUMBER_4]
      *  [BEAU, TRAILER, END, NUMBER_4]
      *  [BEAU, TRAILER, CENTER, NUMBER_2]
      *  [BELLE, TRAILER, CENTER, NUMBER_2]
      *  [BELLE, TRAILER, CENTER, NUMBER_2]
      *  [BEAU, TRAILER, CENTER, NUMBER_2]
      *  [BEAU, TRAILER, END, NUMBER_4]
      *  [BELLE, TRAILER, END, NUMBER_4]
      */
    public static final NamedTaggedFormation EIGHT_CHAIN_THRU =
        new NamedTaggedFormation("EIGHT CHAIN THRU",
            new StandardDancer[] {
                StandardDancer.COUPLE_1_GIRL,
                StandardDancer.COUPLE_1_BOY,
                StandardDancer.COUPLE_2_BOY,
                StandardDancer.COUPLE_2_GIRL,
            },
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(3), ExactRotation.SOUTH), Tag.BELLE, Tag.TRAILER, Tag.END, Tag.NUMBER_4),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(3), ExactRotation.SOUTH), Tag.BEAU, Tag.TRAILER, Tag.END, Tag.NUMBER_4),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(1), ExactRotation.NORTH), Tag.BEAU, Tag.TRAILER, Tag.CENTER, Tag.NUMBER_2),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(1), ExactRotation.NORTH), Tag.BELLE, Tag.TRAILER, Tag.CENTER, Tag.NUMBER_2),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-1), ExactRotation.SOUTH), Tag.BELLE, Tag.TRAILER, Tag.CENTER, Tag.NUMBER_2),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-1), ExactRotation.SOUTH), Tag.BEAU, Tag.TRAILER, Tag.CENTER, Tag.NUMBER_2),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-3), ExactRotation.NORTH), Tag.BEAU, Tag.TRAILER, Tag.END, Tag.NUMBER_4),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-3), ExactRotation.NORTH), Tag.BELLE, Tag.TRAILER, Tag.END, Tag.NUMBER_4));

    /** TRADE BY formation.
      * @doc.test
      *  js> tf = FormationList.TRADE_BY; tf.mapStd([]).toStringDiagram('|');
      *  |1B^  1G^
      *  |
      *  |2Gv  2Bv
      *  |
      *  |4B^  4G^
      *  |
      *  |3Gv  3Bv
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [BEAU, LEADER, END, NUMBER_1]
      *  [BELLE, LEADER, END, NUMBER_1]
      *  [BELLE, LEADER, CENTER, NUMBER_3]
      *  [BEAU, LEADER, CENTER, NUMBER_3]
      *  [BEAU, LEADER, CENTER, NUMBER_3]
      *  [BELLE, LEADER, CENTER, NUMBER_3]
      *  [BELLE, LEADER, END, NUMBER_1]
      *  [BEAU, LEADER, END, NUMBER_1]
      */
    public static final NamedTaggedFormation TRADE_BY =
        new NamedTaggedFormation("TRADE BY",
            new StandardDancer[] {
                StandardDancer.COUPLE_1_BOY,
                StandardDancer.COUPLE_1_GIRL,
                StandardDancer.COUPLE_2_GIRL,
                StandardDancer.COUPLE_2_BOY,
            },
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(3), ExactRotation.NORTH), Tag.BEAU, Tag.LEADER, Tag.END, Tag.NUMBER_1),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(3), ExactRotation.NORTH), Tag.BELLE, Tag.LEADER, Tag.END, Tag.NUMBER_1),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(1), ExactRotation.SOUTH), Tag.BELLE, Tag.LEADER, Tag.CENTER, Tag.NUMBER_3),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(1), ExactRotation.SOUTH), Tag.BEAU, Tag.LEADER, Tag.CENTER, Tag.NUMBER_3),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-1), ExactRotation.NORTH), Tag.BEAU, Tag.LEADER, Tag.CENTER, Tag.NUMBER_3),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-1), ExactRotation.NORTH), Tag.BELLE, Tag.LEADER, Tag.CENTER, Tag.NUMBER_3),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-3), ExactRotation.SOUTH), Tag.BELLE, Tag.LEADER, Tag.END, Tag.NUMBER_1),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-3), ExactRotation.SOUTH), Tag.BEAU, Tag.LEADER, Tag.END, Tag.NUMBER_1));

    /** DOUBLE PASS THRU formation.
      * @doc.test
      *  js> tf = FormationList.DOUBLE_PASS_THRU; tf.mapStd([]).toStringDiagram('|');
      *  |1Gv  1Bv
      *  |
      *  |2Gv  2Bv
      *  |
      *  |4B^  4G^
      *  |
      *  |3B^  3G^
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [BELLE, TRAILER, END, NUMBER_4]
      *  [BEAU, TRAILER, END, NUMBER_4]
      *  [BELLE, LEADER, CENTER, NUMBER_3]
      *  [BEAU, LEADER, CENTER, NUMBER_3]
      *  [BEAU, LEADER, CENTER, NUMBER_3]
      *  [BELLE, LEADER, CENTER, NUMBER_3]
      *  [BEAU, TRAILER, END, NUMBER_4]
      *  [BELLE, TRAILER, END, NUMBER_4]
      */
    public static final NamedTaggedFormation DOUBLE_PASS_THRU =
        new NamedTaggedFormation("DOUBLE PASS THRU",
            new StandardDancer[] {
                StandardDancer.COUPLE_1_GIRL,
                StandardDancer.COUPLE_1_BOY,
                StandardDancer.COUPLE_2_GIRL,
                StandardDancer.COUPLE_2_BOY,
            },
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(3), ExactRotation.SOUTH), Tag.BELLE, Tag.TRAILER, Tag.END, Tag.NUMBER_4),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(3), ExactRotation.SOUTH), Tag.BEAU, Tag.TRAILER, Tag.END, Tag.NUMBER_4),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(1), ExactRotation.SOUTH), Tag.BELLE, Tag.LEADER, Tag.CENTER, Tag.NUMBER_3),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(1), ExactRotation.SOUTH), Tag.BEAU, Tag.LEADER, Tag.CENTER, Tag.NUMBER_3),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-1), ExactRotation.NORTH), Tag.BEAU, Tag.LEADER, Tag.CENTER, Tag.NUMBER_3),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-1), ExactRotation.NORTH), Tag.BELLE, Tag.LEADER, Tag.CENTER, Tag.NUMBER_3),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-3), ExactRotation.NORTH), Tag.BEAU, Tag.TRAILER, Tag.END, Tag.NUMBER_4),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-3), ExactRotation.NORTH), Tag.BELLE, Tag.TRAILER, Tag.END, Tag.NUMBER_4));

    /** COMPLETED DOUBLE PASS THRU formation.
      * @doc.test
      *  js> tf = FormationList.COMPLETED_DOUBLE_PASS_THRU; tf.mapStd([]).toStringDiagram('|');
      *  |1B^  1G^
      *  |
      *  |2B^  2G^
      *  |
      *  |4Gv  4Bv
      *  |
      *  |3Gv  3Bv
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [BEAU, LEADER, END, NUMBER_1]
      *  [BELLE, LEADER, END, NUMBER_1]
      *  [BEAU, TRAILER, CENTER, NUMBER_2]
      *  [BELLE, TRAILER, CENTER, NUMBER_2]
      *  [BELLE, TRAILER, CENTER, NUMBER_2]
      *  [BEAU, TRAILER, CENTER, NUMBER_2]
      *  [BELLE, LEADER, END, NUMBER_1]
      *  [BEAU, LEADER, END, NUMBER_1]
      */
    public static final NamedTaggedFormation COMPLETED_DOUBLE_PASS_THRU =
        new NamedTaggedFormation("COMPLETED DOUBLE PASS THRU",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(3), ExactRotation.NORTH), Tag.BEAU, Tag.LEADER, Tag.END, Tag.NUMBER_1),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(3), ExactRotation.NORTH), Tag.BELLE, Tag.LEADER, Tag.END, Tag.NUMBER_1),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(1), ExactRotation.NORTH), Tag.BEAU, Tag.TRAILER, Tag.CENTER, Tag.NUMBER_2),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(1), ExactRotation.NORTH), Tag.BELLE, Tag.TRAILER, Tag.CENTER, Tag.NUMBER_2),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-1), ExactRotation.SOUTH), Tag.BELLE, Tag.TRAILER, Tag.CENTER, Tag.NUMBER_2),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-1), ExactRotation.SOUTH), Tag.BEAU, Tag.TRAILER, Tag.CENTER, Tag.NUMBER_2),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-3), ExactRotation.SOUTH), Tag.BELLE, Tag.LEADER, Tag.END, Tag.NUMBER_1),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-3), ExactRotation.SOUTH), Tag.BEAU, Tag.LEADER, Tag.END, Tag.NUMBER_1));

    /** LINES FACING OUT formation.
      * @doc.test
      *  js> tf = FormationList.LINES_FACING_OUT; tf.mapStd([]).toStringDiagram('|');
      *  |1B^  1G^  2B^  2G^
      *  |
      *  |4Gv  4Bv  3Gv  3Bv
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [BEAU, LEADER, END]
      *  [BELLE, LEADER, CENTER]
      *  [BEAU, LEADER, CENTER]
      *  [BELLE, LEADER, END]
      *  [BELLE, LEADER, END]
      *  [BEAU, LEADER, CENTER]
      *  [BELLE, LEADER, CENTER]
      *  [BEAU, LEADER, END]
      */
    public static final NamedTaggedFormation LINES_FACING_OUT =
        new NamedTaggedFormation("LINES FACING OUT",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(1), ExactRotation.NORTH), Tag.BEAU, Tag.LEADER, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(1), ExactRotation.NORTH), Tag.BELLE, Tag.LEADER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(1), ExactRotation.NORTH), Tag.BEAU, Tag.LEADER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(1), ExactRotation.NORTH), Tag.BELLE, Tag.LEADER, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(-1), ExactRotation.SOUTH), Tag.BELLE, Tag.LEADER, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-1), ExactRotation.SOUTH), Tag.BEAU, Tag.LEADER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-1), ExactRotation.SOUTH), Tag.BELLE, Tag.LEADER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(-1), ExactRotation.SOUTH), Tag.BEAU, Tag.LEADER, Tag.END));

    /** PARALLEL RH WAVES formation.
      * @doc.test
      *  js> tf = FormationList.PARALLEL_RH_WAVES; tf.mapStd([]).toStringDiagram('|');
      *  |1B^  1Gv  2B^  2Gv
      *  |
      *  |4G^  4Bv  3G^  3Bv
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [BEAU, LEADER, END]
      *  [BEAU, TRAILER, CENTER]
      *  [BEAU, LEADER, CENTER]
      *  [BEAU, TRAILER, END]
      *  [BEAU, TRAILER, END]
      *  [BEAU, LEADER, CENTER]
      *  [BEAU, TRAILER, CENTER]
      *  [BEAU, LEADER, END]
      */
    public static final NamedTaggedFormation PARALLEL_RH_WAVES =
        new NamedTaggedFormation("PARALLEL RH WAVES",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(1), ExactRotation.NORTH), Tag.BEAU, Tag.LEADER, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(1), ExactRotation.SOUTH), Tag.BEAU, Tag.TRAILER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(1), ExactRotation.NORTH), Tag.BEAU, Tag.LEADER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(1), ExactRotation.SOUTH), Tag.BEAU, Tag.TRAILER, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(-1), ExactRotation.NORTH), Tag.BEAU, Tag.TRAILER, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-1), ExactRotation.SOUTH), Tag.BEAU, Tag.LEADER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-1), ExactRotation.NORTH), Tag.BEAU, Tag.TRAILER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(-1), ExactRotation.SOUTH), Tag.BEAU, Tag.LEADER, Tag.END));

    /** PARALLEL LH WAVES formation.
      * @doc.test
      *  js> tf = FormationList.PARALLEL_LH_WAVES; tf.mapStd([]).toStringDiagram('|');
      *  |1Bv  1G^  2Bv  2G^
      *  |
      *  |4Gv  4B^  3Gv  3B^
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [BELLE, TRAILER, END]
      *  [BELLE, LEADER, CENTER]
      *  [BELLE, TRAILER, CENTER]
      *  [BELLE, LEADER, END]
      *  [BELLE, LEADER, END]
      *  [BELLE, TRAILER, CENTER]
      *  [BELLE, LEADER, CENTER]
      *  [BELLE, TRAILER, END]
      */
    public static final NamedTaggedFormation PARALLEL_LH_WAVES =
        new NamedTaggedFormation("PARALLEL LH WAVES",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(1), ExactRotation.SOUTH), Tag.BELLE, Tag.TRAILER, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(1), ExactRotation.NORTH), Tag.BELLE, Tag.LEADER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(1), ExactRotation.SOUTH), Tag.BELLE, Tag.TRAILER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(1), ExactRotation.NORTH), Tag.BELLE, Tag.LEADER, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(-1), ExactRotation.SOUTH), Tag.BELLE, Tag.LEADER, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-1), ExactRotation.NORTH), Tag.BELLE, Tag.TRAILER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-1), ExactRotation.SOUTH), Tag.BELLE, Tag.LEADER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(-1), ExactRotation.NORTH), Tag.BELLE, Tag.TRAILER, Tag.END));

    /** PARALLEL RH TWO-FACED LINES formation.
      * @doc.test
      *  js> tf = FormationList.PARALLEL_RH_TWO_FACED_LINES; tf.mapStd([]).toStringDiagram('|');
      *  |1B^  1G^  2Gv  2Bv
      *  |
      *  |4B^  4G^  3Gv  3Bv
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [BEAU, LEADER, END]
      *  [BELLE, LEADER, CENTER]
      *  [BELLE, TRAILER, CENTER]
      *  [BEAU, TRAILER, END]
      *  [BEAU, TRAILER, END]
      *  [BELLE, TRAILER, CENTER]
      *  [BELLE, LEADER, CENTER]
      *  [BEAU, LEADER, END]
      */
    public static final NamedTaggedFormation PARALLEL_RH_TWO_FACED_LINES =
        new NamedTaggedFormation("PARALLEL RH TWO-FACED LINES",
            new StandardDancer[] {
                StandardDancer.COUPLE_1_BOY,
                StandardDancer.COUPLE_1_GIRL,
                StandardDancer.COUPLE_2_GIRL,
                StandardDancer.COUPLE_2_BOY,
            },
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(1), ExactRotation.NORTH), Tag.BEAU, Tag.LEADER, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(1), ExactRotation.NORTH), Tag.BELLE, Tag.LEADER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(1), ExactRotation.SOUTH), Tag.BELLE, Tag.TRAILER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(1), ExactRotation.SOUTH), Tag.BEAU, Tag.TRAILER, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(-1), ExactRotation.NORTH), Tag.BEAU, Tag.TRAILER, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-1), ExactRotation.NORTH), Tag.BELLE, Tag.TRAILER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-1), ExactRotation.SOUTH), Tag.BELLE, Tag.LEADER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(-1), ExactRotation.SOUTH), Tag.BEAU, Tag.LEADER, Tag.END));

    /** PARALLEL LH TWO-FACED LINES formation.
      * @doc.test
      *  js> tf = FormationList.PARALLEL_LH_TWO_FACED_LINES; tf.mapStd([]).toStringDiagram('|');
      *  |1Gv  1Bv  2B^  2G^
      *  |
      *  |4Gv  4Bv  3B^  3G^
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [BELLE, TRAILER, END]
      *  [BEAU, TRAILER, CENTER]
      *  [BEAU, LEADER, CENTER]
      *  [BELLE, LEADER, END]
      *  [BELLE, LEADER, END]
      *  [BEAU, LEADER, CENTER]
      *  [BEAU, TRAILER, CENTER]
      *  [BELLE, TRAILER, END]
      */
    public static final NamedTaggedFormation PARALLEL_LH_TWO_FACED_LINES =
        new NamedTaggedFormation("PARALLEL LH TWO-FACED LINES",
            new StandardDancer[] {
                StandardDancer.COUPLE_1_GIRL,
                StandardDancer.COUPLE_1_BOY,
                StandardDancer.COUPLE_2_BOY,
                StandardDancer.COUPLE_2_GIRL,
            },
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(1), ExactRotation.SOUTH), Tag.BELLE, Tag.TRAILER, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(1), ExactRotation.SOUTH), Tag.BEAU, Tag.TRAILER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(1), ExactRotation.NORTH), Tag.BEAU, Tag.LEADER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(1), ExactRotation.NORTH), Tag.BELLE, Tag.LEADER, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(-1), ExactRotation.SOUTH), Tag.BELLE, Tag.LEADER, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-1), ExactRotation.SOUTH), Tag.BEAU, Tag.LEADER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-1), ExactRotation.NORTH), Tag.BEAU, Tag.TRAILER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(-1), ExactRotation.NORTH), Tag.BELLE, Tag.TRAILER, Tag.END));

    /** RH COLUMN formation.
      * @doc.test
      *  js> tf = FormationList.RH_COLUMN; tf.mapStd([]).toStringDiagram('|');
      *  |1B^  1Gv
      *  |
      *  |2B^  2Gv
      *  |
      *  |4G^  4Bv
      *  |
      *  |3G^  3Bv
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [BEAU, LEADER, END, NUMBER_1]
      *  [BEAU, TRAILER, END, NUMBER_4]
      *  [BEAU, TRAILER, CENTER, NUMBER_2]
      *  [BEAU, LEADER, CENTER, NUMBER_3]
      *  [BEAU, LEADER, CENTER, NUMBER_3]
      *  [BEAU, TRAILER, CENTER, NUMBER_2]
      *  [BEAU, TRAILER, END, NUMBER_4]
      *  [BEAU, LEADER, END, NUMBER_1]
      */
    public static final NamedTaggedFormation RH_COLUMN =
        new NamedTaggedFormation("RH COLUMN",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(3), ExactRotation.NORTH), Tag.BEAU, Tag.LEADER, Tag.END, Tag.NUMBER_1),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(3), ExactRotation.SOUTH), Tag.BEAU, Tag.TRAILER, Tag.END, Tag.NUMBER_4),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(1), ExactRotation.NORTH), Tag.BEAU, Tag.TRAILER, Tag.CENTER, Tag.NUMBER_2),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(1), ExactRotation.SOUTH), Tag.BEAU, Tag.LEADER, Tag.CENTER, Tag.NUMBER_3),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-1), ExactRotation.NORTH), Tag.BEAU, Tag.LEADER, Tag.CENTER, Tag.NUMBER_3),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-1), ExactRotation.SOUTH), Tag.BEAU, Tag.TRAILER, Tag.CENTER, Tag.NUMBER_2),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-3), ExactRotation.NORTH), Tag.BEAU, Tag.TRAILER, Tag.END, Tag.NUMBER_4),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-3), ExactRotation.SOUTH), Tag.BEAU, Tag.LEADER, Tag.END, Tag.NUMBER_1));

    /** LH COLUMN formation.
      * @doc.test
      *  js> tf = FormationList.LH_COLUMN; tf.mapStd([]).toStringDiagram('|');
      *  |1Bv  1G^
      *  |
      *  |2Bv  2G^
      *  |
      *  |4Gv  4B^
      *  |
      *  |3Gv  3B^
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [BELLE, TRAILER, END, NUMBER_4]
      *  [BELLE, LEADER, END, NUMBER_1]
      *  [BELLE, LEADER, CENTER, NUMBER_3]
      *  [BELLE, TRAILER, CENTER, NUMBER_2]
      *  [BELLE, TRAILER, CENTER, NUMBER_2]
      *  [BELLE, LEADER, CENTER, NUMBER_3]
      *  [BELLE, LEADER, END, NUMBER_1]
      *  [BELLE, TRAILER, END, NUMBER_4]
      */
    public static final NamedTaggedFormation LH_COLUMN =
        new NamedTaggedFormation("LH COLUMN",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(3), ExactRotation.SOUTH), Tag.BELLE, Tag.TRAILER, Tag.END, Tag.NUMBER_4),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(3), ExactRotation.NORTH), Tag.BELLE, Tag.LEADER, Tag.END, Tag.NUMBER_1),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(1), ExactRotation.SOUTH), Tag.BELLE, Tag.LEADER, Tag.CENTER, Tag.NUMBER_3),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(1), ExactRotation.NORTH), Tag.BELLE, Tag.TRAILER, Tag.CENTER, Tag.NUMBER_2),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-1), ExactRotation.SOUTH), Tag.BELLE, Tag.TRAILER, Tag.CENTER, Tag.NUMBER_2),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-1), ExactRotation.NORTH), Tag.BELLE, Tag.LEADER, Tag.CENTER, Tag.NUMBER_3),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-3), ExactRotation.SOUTH), Tag.BELLE, Tag.LEADER, Tag.END, Tag.NUMBER_1),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-3), ExactRotation.NORTH), Tag.BELLE, Tag.TRAILER, Tag.END, Tag.NUMBER_4));

    /** TRANS RH COLUMN formation.
      * @doc.test
      *  js> tf = FormationList.TRANS_RH_COLUMN; tf.mapStd([]).toStringDiagram('|');
      *  |1B^
      *  |
      *  |1G^
      *  |
      *  |     3Gv
      *  |
      *  |     3Bv
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [NUMBER_1]
      *  [NUMBER_2]
      *  [NUMBER_2]
      *  [NUMBER_1]
      */
    public static final NamedTaggedFormation TRANS_RH_COLUMN =
        new NamedTaggedFormation("TRANS RH COLUMN",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(3), ExactRotation.NORTH), Tag.NUMBER_1),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(1), ExactRotation.NORTH), Tag.NUMBER_2),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-1), ExactRotation.SOUTH), Tag.NUMBER_2),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-3), ExactRotation.SOUTH), Tag.NUMBER_1));

    /** TRANS LH COLUMN formation.
      * @doc.test
      *  js> tf = FormationList.TRANS_LH_COLUMN; tf.mapStd([]).toStringDiagram('|');
      *  |     1B^
      *  |
      *  |     1G^
      *  |
      *  |3Gv
      *  |
      *  |3Bv
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [NUMBER_1]
      *  [NUMBER_2]
      *  [NUMBER_2]
      *  [NUMBER_1]
      */
    public static final NamedTaggedFormation TRANS_LH_COLUMN =
        new NamedTaggedFormation("TRANS LH COLUMN",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(3), ExactRotation.NORTH), Tag.NUMBER_1),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(1), ExactRotation.NORTH), Tag.NUMBER_2),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-1), ExactRotation.SOUTH), Tag.NUMBER_2),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-3), ExactRotation.SOUTH), Tag.NUMBER_1));

    /** ENDS IN INVERTED LINES formation.
      * @doc.test
      *  js> tf = FormationList.ENDS_IN_INVERTED_LINES; tf.mapStd([]).toStringDiagram('|');
      *  |1Bv  1G^  2B^  2Gv
      *  |
      *  |4G^  4Bv  3Gv  3B^
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [BELLE, TRAILER, END]
      *  [BELLE, LEADER, CENTER]
      *  [BEAU, LEADER, CENTER]
      *  [BEAU, TRAILER, END]
      *  [BEAU, TRAILER, END]
      *  [BEAU, LEADER, CENTER]
      *  [BELLE, LEADER, CENTER]
      *  [BELLE, TRAILER, END]
      */
    public static final NamedTaggedFormation ENDS_IN_INVERTED_LINES =
        new NamedTaggedFormation("ENDS IN INVERTED LINES",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(1), ExactRotation.SOUTH), Tag.BELLE, Tag.TRAILER, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(1), ExactRotation.NORTH), Tag.BELLE, Tag.LEADER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(1), ExactRotation.NORTH), Tag.BEAU, Tag.LEADER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(1), ExactRotation.SOUTH), Tag.BEAU, Tag.TRAILER, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(-1), ExactRotation.NORTH), Tag.BEAU, Tag.TRAILER, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-1), ExactRotation.SOUTH), Tag.BEAU, Tag.LEADER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-1), ExactRotation.SOUTH), Tag.BELLE, Tag.LEADER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(-1), ExactRotation.NORTH), Tag.BELLE, Tag.TRAILER, Tag.END));

    /** ENDS OUT INVERTED LINES formation.
      * @doc.test
      *  js> tf = FormationList.ENDS_OUT_INVERTED_LINES; tf.mapStd([]).toStringDiagram('|');
      *  |1B^  1Gv  2Bv  2G^
      *  |
      *  |4Gv  4B^  3G^  3Bv
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [BEAU, LEADER, END]
      *  [BEAU, TRAILER, CENTER]
      *  [BELLE, TRAILER, CENTER]
      *  [BELLE, LEADER, END]
      *  [BELLE, LEADER, END]
      *  [BELLE, TRAILER, CENTER]
      *  [BEAU, TRAILER, CENTER]
      *  [BEAU, LEADER, END]
      */
    public static final NamedTaggedFormation ENDS_OUT_INVERTED_LINES =
        new NamedTaggedFormation("ENDS OUT INVERTED LINES",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(1), ExactRotation.NORTH), Tag.BEAU, Tag.LEADER, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(1), ExactRotation.SOUTH), Tag.BEAU, Tag.TRAILER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(1), ExactRotation.SOUTH), Tag.BELLE, Tag.TRAILER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(1), ExactRotation.NORTH), Tag.BELLE, Tag.LEADER, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(-1), ExactRotation.SOUTH), Tag.BELLE, Tag.LEADER, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-1), ExactRotation.NORTH), Tag.BELLE, Tag.TRAILER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-1), ExactRotation.NORTH), Tag.BEAU, Tag.TRAILER, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(-1), ExactRotation.SOUTH), Tag.BEAU, Tag.LEADER, Tag.END));

    /** GENERAL 1/4 TAG formation.
      * @doc.test
      *  js> tf = FormationList.GENERAL_QUARTER_TAG; tf.toStringDiagram('|');
      *  |     |    |
      *  |
      *  ||    |    |    |
      *  |
      *  |     |    |
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [END, CENTER_6, OUTSIDE_6]
      *  [END, CENTER_6, OUTSIDE_6]
      *  [CENTER, OUTSIDE_2, OUTSIDE_6]
      *  [CENTER, VERY_CENTER, CENTER_6]
      *  [CENTER, VERY_CENTER, CENTER_6]
      *  [CENTER, OUTSIDE_2, OUTSIDE_6]
      *  [END, CENTER_6, OUTSIDE_6]
      *  [END, CENTER_6, OUTSIDE_6]
      */
    public static final NamedTaggedFormation GENERAL_QUARTER_TAG =
        new NamedTaggedFormation("GENERAL 1/4 TAG",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(2), Rotation.fromAbsoluteString("|")), Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(2), Rotation.fromAbsoluteString("|")), Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.CENTER, Tag.OUTSIDE_2, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.CENTER, Tag.VERY_CENTER, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.CENTER, Tag.VERY_CENTER, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.CENTER, Tag.OUTSIDE_2, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-2), Rotation.fromAbsoluteString("|")), Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-2), Rotation.fromAbsoluteString("|")), Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6));

    /** RH 1/4 TAG formation.
      * @doc.test
      *  js> tf = FormationList.RH_QUARTER_TAG; tf.mapStd([]).toStringDiagram('|');
      *  |     1Bv  1Gv
      *  |
      *  |2B^  2Gv  4G^  4Bv
      *  |
      *  |     3G^  3B^
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [BELLE, END, CENTER_6, OUTSIDE_6]
      *  [BEAU, END, CENTER_6, OUTSIDE_6]
      *  [BEAU, CENTER, OUTSIDE_2, OUTSIDE_6]
      *  [BEAU, CENTER, VERY_CENTER, CENTER_6]
      *  [BEAU, CENTER, VERY_CENTER, CENTER_6]
      *  [BEAU, CENTER, OUTSIDE_2, OUTSIDE_6]
      *  [BEAU, END, CENTER_6, OUTSIDE_6]
      *  [BELLE, END, CENTER_6, OUTSIDE_6]
      */
    public static final NamedTaggedFormation RH_QUARTER_TAG =
        new NamedTaggedFormation("RH 1/4 TAG",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(2), ExactRotation.SOUTH), Tag.BELLE, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(2), ExactRotation.SOUTH), Tag.BEAU, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BEAU, Tag.CENTER, Tag.OUTSIDE_2, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BEAU, Tag.CENTER, Tag.VERY_CENTER, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BEAU, Tag.CENTER, Tag.VERY_CENTER, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BEAU, Tag.CENTER, Tag.OUTSIDE_2, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-2), ExactRotation.NORTH), Tag.BEAU, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-2), ExactRotation.NORTH), Tag.BELLE, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6));

    /** LH 1/4 TAG formation.
      * @doc.test
      *  js> tf = FormationList.LH_QUARTER_TAG; tf.mapStd([]).toStringDiagram('|');
      *  |     1Bv  1Gv
      *  |
      *  |2Bv  2G^  4Gv  4B^
      *  |
      *  |     3G^  3B^
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [BELLE, END, CENTER_6, OUTSIDE_6]
      *  [BEAU, END, CENTER_6, OUTSIDE_6]
      *  [BELLE, CENTER, OUTSIDE_2, OUTSIDE_6]
      *  [BELLE, CENTER, VERY_CENTER, CENTER_6]
      *  [BELLE, CENTER, VERY_CENTER, CENTER_6]
      *  [BELLE, CENTER, OUTSIDE_2, OUTSIDE_6]
      *  [BEAU, END, CENTER_6, OUTSIDE_6]
      *  [BELLE, END, CENTER_6, OUTSIDE_6]
      */
    public static final NamedTaggedFormation LH_QUARTER_TAG =
        new NamedTaggedFormation("LH 1/4 TAG",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(2), ExactRotation.SOUTH), Tag.BELLE, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(2), ExactRotation.SOUTH), Tag.BEAU, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BELLE, Tag.CENTER, Tag.OUTSIDE_2, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BELLE, Tag.CENTER, Tag.VERY_CENTER, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BELLE, Tag.CENTER, Tag.VERY_CENTER, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BELLE, Tag.CENTER, Tag.OUTSIDE_2, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-2), ExactRotation.NORTH), Tag.BEAU, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-2), ExactRotation.NORTH), Tag.BELLE, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6));

    /** RH 3/4 TAG formation.
      * @doc.test
      *  js> tf = FormationList.RH_THREE_QUARTER_TAG; tf.mapStd([]).toStringDiagram('|');
      *  |     1B^  1G^
      *  |
      *  |2B^  2Gv  4G^  4Bv
      *  |
      *  |     3Gv  3Bv
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [BEAU, END, CENTER_6, OUTSIDE_6]
      *  [BELLE, END, CENTER_6, OUTSIDE_6]
      *  [BEAU, CENTER, OUTSIDE_2, OUTSIDE_6]
      *  [BEAU, CENTER, VERY_CENTER, CENTER_6]
      *  [BEAU, CENTER, VERY_CENTER, CENTER_6]
      *  [BEAU, CENTER, OUTSIDE_2, OUTSIDE_6]
      *  [BELLE, END, CENTER_6, OUTSIDE_6]
      *  [BEAU, END, CENTER_6, OUTSIDE_6]
      */
    public static final NamedTaggedFormation RH_THREE_QUARTER_TAG =
        new NamedTaggedFormation("RH 3/4 TAG",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(2), ExactRotation.NORTH), Tag.BEAU, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(2), ExactRotation.NORTH), Tag.BELLE, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BEAU, Tag.CENTER, Tag.OUTSIDE_2, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BEAU, Tag.CENTER, Tag.VERY_CENTER, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BEAU, Tag.CENTER, Tag.VERY_CENTER, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BEAU, Tag.CENTER, Tag.OUTSIDE_2, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-2), ExactRotation.SOUTH), Tag.BELLE, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-2), ExactRotation.SOUTH), Tag.BEAU, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6));

    /** LH 3/4 TAG formation.
      * @doc.test
      *  js> tf = FormationList.LH_THREE_QUARTER_TAG; tf.mapStd([]).toStringDiagram('|');
      *  |     1B^  1G^
      *  |
      *  |2Bv  2G^  4Gv  4B^
      *  |
      *  |     3Gv  3Bv
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [BEAU, END, CENTER_6, OUTSIDE_6]
      *  [BELLE, END, CENTER_6, OUTSIDE_6]
      *  [BELLE, CENTER, OUTSIDE_2, OUTSIDE_6]
      *  [BELLE, CENTER, VERY_CENTER, CENTER_6]
      *  [BELLE, CENTER, VERY_CENTER, CENTER_6]
      *  [BELLE, CENTER, OUTSIDE_2, OUTSIDE_6]
      *  [BELLE, END, CENTER_6, OUTSIDE_6]
      *  [BEAU, END, CENTER_6, OUTSIDE_6]
      */
    public static final NamedTaggedFormation LH_THREE_QUARTER_TAG =
        new NamedTaggedFormation("LH 3/4 TAG",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(2), ExactRotation.NORTH), Tag.BEAU, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(2), ExactRotation.NORTH), Tag.BELLE, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BELLE, Tag.CENTER, Tag.OUTSIDE_2, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BELLE, Tag.CENTER, Tag.VERY_CENTER, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BELLE, Tag.CENTER, Tag.VERY_CENTER, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BELLE, Tag.CENTER, Tag.OUTSIDE_2, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-2), ExactRotation.SOUTH), Tag.BELLE, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-2), ExactRotation.SOUTH), Tag.BEAU, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6));

    /** RH 1/4 LINE formation.
      * @doc.test
      *  js> tf = FormationList.RH_QUARTER_LINE; tf.mapStd([]).toStringDiagram('|');
      *  |     1Bv  1Gv
      *  |
      *  |2B^  2G^  4Gv  4Bv
      *  |
      *  |     3G^  3B^
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [BELLE, END, CENTER_6, OUTSIDE_6]
      *  [BEAU, END, CENTER_6, OUTSIDE_6]
      *  [BEAU, CENTER, OUTSIDE_2, OUTSIDE_6]
      *  [BELLE, CENTER, VERY_CENTER, CENTER_6]
      *  [BELLE, CENTER, VERY_CENTER, CENTER_6]
      *  [BEAU, CENTER, OUTSIDE_2, OUTSIDE_6]
      *  [BEAU, END, CENTER_6, OUTSIDE_6]
      *  [BELLE, END, CENTER_6, OUTSIDE_6]
      */
    public static final NamedTaggedFormation RH_QUARTER_LINE =
        new NamedTaggedFormation("RH 1/4 LINE",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(2), ExactRotation.SOUTH), Tag.BELLE, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(2), ExactRotation.SOUTH), Tag.BEAU, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BEAU, Tag.CENTER, Tag.OUTSIDE_2, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BELLE, Tag.CENTER, Tag.VERY_CENTER, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BELLE, Tag.CENTER, Tag.VERY_CENTER, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BEAU, Tag.CENTER, Tag.OUTSIDE_2, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-2), ExactRotation.NORTH), Tag.BEAU, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-2), ExactRotation.NORTH), Tag.BELLE, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6));

    /** LH 1/4 LINE formation.
      * @doc.test
      *  js> tf = FormationList.LH_QUARTER_LINE; tf.mapStd([]).toStringDiagram('|');
      *  |     1Bv  1Gv
      *  |
      *  |2Bv  2Gv  4G^  4B^
      *  |
      *  |     3G^  3B^
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [BELLE, END, CENTER_6, OUTSIDE_6]
      *  [BEAU, END, CENTER_6, OUTSIDE_6]
      *  [BELLE, CENTER, OUTSIDE_2, OUTSIDE_6]
      *  [BEAU, CENTER, VERY_CENTER, CENTER_6]
      *  [BEAU, CENTER, VERY_CENTER, CENTER_6]
      *  [BELLE, CENTER, OUTSIDE_2, OUTSIDE_6]
      *  [BEAU, END, CENTER_6, OUTSIDE_6]
      *  [BELLE, END, CENTER_6, OUTSIDE_6]
      */
    public static final NamedTaggedFormation LH_QUARTER_LINE =
        new NamedTaggedFormation("LH 1/4 LINE",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(2), ExactRotation.SOUTH), Tag.BELLE, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(2), ExactRotation.SOUTH), Tag.BEAU, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BELLE, Tag.CENTER, Tag.OUTSIDE_2, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BEAU, Tag.CENTER, Tag.VERY_CENTER, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BEAU, Tag.CENTER, Tag.VERY_CENTER, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BELLE, Tag.CENTER, Tag.OUTSIDE_2, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-2), ExactRotation.NORTH), Tag.BEAU, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-2), ExactRotation.NORTH), Tag.BELLE, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6));

    /** RH 3/4 LINE formation.
      * @doc.test
      *  js> tf = FormationList.RH_THREE_QUARTER_LINE; tf.mapStd([]).toStringDiagram('|');
      *  |     1B^  1G^
      *  |
      *  |2B^  2G^  4Gv  4Bv
      *  |
      *  |     3Gv  3Bv
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [BEAU, END, CENTER_6, OUTSIDE_6]
      *  [BELLE, END, CENTER_6, OUTSIDE_6]
      *  [BEAU, CENTER, OUTSIDE_2, OUTSIDE_6]
      *  [BELLE, CENTER, VERY_CENTER, CENTER_6]
      *  [BELLE, CENTER, VERY_CENTER, CENTER_6]
      *  [BEAU, CENTER, OUTSIDE_2, OUTSIDE_6]
      *  [BELLE, END, CENTER_6, OUTSIDE_6]
      *  [BEAU, END, CENTER_6, OUTSIDE_6]
      */
    public static final NamedTaggedFormation RH_THREE_QUARTER_LINE =
        new NamedTaggedFormation("RH 3/4 LINE",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(2), ExactRotation.NORTH), Tag.BEAU, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(2), ExactRotation.NORTH), Tag.BELLE, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BEAU, Tag.CENTER, Tag.OUTSIDE_2, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BELLE, Tag.CENTER, Tag.VERY_CENTER, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BELLE, Tag.CENTER, Tag.VERY_CENTER, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BEAU, Tag.CENTER, Tag.OUTSIDE_2, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-2), ExactRotation.SOUTH), Tag.BELLE, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-2), ExactRotation.SOUTH), Tag.BEAU, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6));

    /** LH 3/4 LINE formation.
      * @doc.test
      *  js> tf = FormationList.LH_THREE_QUARTER_LINE; tf.mapStd([]).toStringDiagram('|');
      *  |     1B^  1G^
      *  |
      *  |2Bv  2Gv  4G^  4B^
      *  |
      *  |     3Gv  3Bv
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [BEAU, END, CENTER_6, OUTSIDE_6]
      *  [BELLE, END, CENTER_6, OUTSIDE_6]
      *  [BELLE, CENTER, OUTSIDE_2, OUTSIDE_6]
      *  [BEAU, CENTER, VERY_CENTER, CENTER_6]
      *  [BEAU, CENTER, VERY_CENTER, CENTER_6]
      *  [BELLE, CENTER, OUTSIDE_2, OUTSIDE_6]
      *  [BELLE, END, CENTER_6, OUTSIDE_6]
      *  [BEAU, END, CENTER_6, OUTSIDE_6]
      */
    public static final NamedTaggedFormation LH_THREE_QUARTER_LINE =
        new NamedTaggedFormation("LH 3/4 LINE",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(2), ExactRotation.NORTH), Tag.BEAU, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(2), ExactRotation.NORTH), Tag.BELLE, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BELLE, Tag.CENTER, Tag.OUTSIDE_2, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BEAU, Tag.CENTER, Tag.VERY_CENTER, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BEAU, Tag.CENTER, Tag.VERY_CENTER, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BELLE, Tag.CENTER, Tag.OUTSIDE_2, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-2), ExactRotation.SOUTH), Tag.BELLE, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-2), ExactRotation.SOUTH), Tag.BEAU, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6));

    /** RH TWIN DIAMONDS formation.
      * @doc.test
      *  js> tf = FormationList.RH_TWIN_DIAMONDS; tf.mapStd([]).toStringDiagram('|');
      *  |  1B>       1G>
      *  |
      *  |2B^  2Gv  4G^  4Bv
      *  |
      *  |  3G<       3B<
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [TRAILER, POINT, END, CENTER_6, OUTSIDE_6]
      *  [LEADER, POINT, END, CENTER_6, OUTSIDE_6]
      *  [BEAU, CENTER, OUTSIDE_2, OUTSIDE_6]
      *  [BEAU, CENTER, VERY_CENTER, CENTER_6]
      *  [BEAU, CENTER, VERY_CENTER, CENTER_6]
      *  [BEAU, CENTER, OUTSIDE_2, OUTSIDE_6]
      *  [LEADER, POINT, END, CENTER_6, OUTSIDE_6]
      *  [TRAILER, POINT, END, CENTER_6, OUTSIDE_6]
      */
    public static final NamedTaggedFormation RH_TWIN_DIAMONDS =
        new NamedTaggedFormation("RH TWIN DIAMONDS",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-2), Fraction.valueOf(2), ExactRotation.EAST), Tag.TRAILER, Tag.POINT, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(2), Fraction.valueOf(2), ExactRotation.EAST), Tag.LEADER, Tag.POINT, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BEAU, Tag.CENTER, Tag.OUTSIDE_2, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BEAU, Tag.CENTER, Tag.VERY_CENTER, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BEAU, Tag.CENTER, Tag.VERY_CENTER, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BEAU, Tag.CENTER, Tag.OUTSIDE_2, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-2), Fraction.valueOf(-2), ExactRotation.WEST), Tag.LEADER, Tag.POINT, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(2), Fraction.valueOf(-2), ExactRotation.WEST), Tag.TRAILER, Tag.POINT, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6));

    /** LH TWIN DIAMONDS formation.
      * @doc.test
      *  js> tf = FormationList.LH_TWIN_DIAMONDS; tf.mapStd([]).toStringDiagram('|');
      *  |  1B<       1G<
      *  |
      *  |2Bv  2G^  4Gv  4B^
      *  |
      *  |  3G>       3B>
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [LEADER, POINT, END, CENTER_6, OUTSIDE_6]
      *  [TRAILER, POINT, END, CENTER_6, OUTSIDE_6]
      *  [BELLE, CENTER, OUTSIDE_2, OUTSIDE_6]
      *  [BELLE, CENTER, VERY_CENTER, CENTER_6]
      *  [BELLE, CENTER, VERY_CENTER, CENTER_6]
      *  [BELLE, CENTER, OUTSIDE_2, OUTSIDE_6]
      *  [TRAILER, POINT, END, CENTER_6, OUTSIDE_6]
      *  [LEADER, POINT, END, CENTER_6, OUTSIDE_6]
      */
    public static final NamedTaggedFormation LH_TWIN_DIAMONDS =
        new NamedTaggedFormation("LH TWIN DIAMONDS",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-2), Fraction.valueOf(2), ExactRotation.WEST), Tag.LEADER, Tag.POINT, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(2), Fraction.valueOf(2), ExactRotation.WEST), Tag.TRAILER, Tag.POINT, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BELLE, Tag.CENTER, Tag.OUTSIDE_2, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BELLE, Tag.CENTER, Tag.VERY_CENTER, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BELLE, Tag.CENTER, Tag.VERY_CENTER, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BELLE, Tag.CENTER, Tag.OUTSIDE_2, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-2), Fraction.valueOf(-2), ExactRotation.EAST), Tag.TRAILER, Tag.POINT, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(2), Fraction.valueOf(-2), ExactRotation.EAST), Tag.LEADER, Tag.POINT, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6));

    /** RH POINT-TO-POINT DIAMONDS formation.
      * @doc.test
      *  js> tf = FormationList.RH_POINT_TO_POINT_DIAMONDS; tf.mapStd([]).toStringDiagram('|');
      *  |  1B>
      *  |
      *  |1G^  2Bv
      *  |
      *  |  2G<
      *  |
      *  |  4G>
      *  |
      *  |4B^  3Gv
      *  |
      *  |  3B<
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [POINT, CENTER, OUTSIDE_2, OUTSIDE_6]
      *  [BEAU, LEADER, END, CENTER_6, OUTSIDE_6]
      *  [BEAU, TRAILER, END, CENTER_6, OUTSIDE_6]
      *  [BELLE, POINT, CENTER, VERY_CENTER, CENTER_6]
      *  [BELLE, POINT, CENTER, VERY_CENTER, CENTER_6]
      *  [BEAU, TRAILER, END, CENTER_6, OUTSIDE_6]
      *  [BEAU, LEADER, END, CENTER_6, OUTSIDE_6]
      *  [POINT, CENTER, OUTSIDE_2, OUTSIDE_6]
      */
    public static final NamedTaggedFormation RH_POINT_TO_POINT_DIAMONDS =
        new NamedTaggedFormation("RH POINT-TO-POINT DIAMONDS",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(5), ExactRotation.EAST), Tag.POINT, Tag.CENTER, Tag.OUTSIDE_2, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(3), ExactRotation.NORTH), Tag.BEAU, Tag.LEADER, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(3), ExactRotation.SOUTH), Tag.BEAU, Tag.TRAILER, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(1), ExactRotation.WEST), Tag.BELLE, Tag.POINT, Tag.CENTER, Tag.VERY_CENTER, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-1), ExactRotation.EAST), Tag.BELLE, Tag.POINT, Tag.CENTER, Tag.VERY_CENTER, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-3), ExactRotation.NORTH), Tag.BEAU, Tag.TRAILER, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-3), ExactRotation.SOUTH), Tag.BEAU, Tag.LEADER, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-5), ExactRotation.WEST), Tag.POINT, Tag.CENTER, Tag.OUTSIDE_2, Tag.OUTSIDE_6));

    /** RH POINT-TO-POINT FACING DIAMONDS formation.
      * @doc.test
      *  js> tf = FormationList.RH_POINT_TO_POINT_FACING_DIAMONDS; tf.mapStd([]).toStringDiagram('|');
      *  |  1B<
      *  |
      *  |1G^  2Bv
      *  |
      *  |  2G>
      *  |
      *  |  4G<
      *  |
      *  |4B^  3Gv
      *  |
      *  |  3B>
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [POINT, CENTER, OUTSIDE_2, OUTSIDE_6]
      *  [BEAU, LEADER, END, CENTER_6, OUTSIDE_6]
      *  [BEAU, TRAILER, END, CENTER_6, OUTSIDE_6]
      *  [BEAU, POINT, CENTER, VERY_CENTER, CENTER_6]
      *  [BEAU, POINT, CENTER, VERY_CENTER, CENTER_6]
      *  [BEAU, TRAILER, END, CENTER_6, OUTSIDE_6]
      *  [BEAU, LEADER, END, CENTER_6, OUTSIDE_6]
      *  [POINT, CENTER, OUTSIDE_2, OUTSIDE_6]
      */
    public static final NamedTaggedFormation RH_POINT_TO_POINT_FACING_DIAMONDS =
        new NamedTaggedFormation("RH POINT-TO-POINT FACING DIAMONDS",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(5), ExactRotation.WEST), Tag.POINT, Tag.CENTER, Tag.OUTSIDE_2, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(3), ExactRotation.NORTH), Tag.BEAU, Tag.LEADER, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(3), ExactRotation.SOUTH), Tag.BEAU, Tag.TRAILER, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(1), ExactRotation.EAST), Tag.BEAU, Tag.POINT, Tag.CENTER, Tag.VERY_CENTER, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-1), ExactRotation.WEST), Tag.BEAU, Tag.POINT, Tag.CENTER, Tag.VERY_CENTER, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-3), ExactRotation.NORTH), Tag.BEAU, Tag.TRAILER, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-3), ExactRotation.SOUTH), Tag.BEAU, Tag.LEADER, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-5), ExactRotation.EAST), Tag.POINT, Tag.CENTER, Tag.OUTSIDE_2, Tag.OUTSIDE_6));

    /** LH POINT-TO-POINT DIAMONDS formation.
      * @doc.test
      *  js> tf = FormationList.LH_POINT_TO_POINT_DIAMONDS; tf.mapStd([]).toStringDiagram('|');
      *  |  1B<
      *  |
      *  |1Gv  2B^
      *  |
      *  |  2G>
      *  |
      *  |  4G<
      *  |
      *  |4Bv  3G^
      *  |
      *  |  3B>
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [POINT, CENTER, OUTSIDE_2, OUTSIDE_6]
      *  [BELLE, TRAILER, END, CENTER_6, OUTSIDE_6]
      *  [BELLE, LEADER, END, CENTER_6, OUTSIDE_6]
      *  [BEAU, POINT, CENTER, VERY_CENTER, CENTER_6]
      *  [BEAU, POINT, CENTER, VERY_CENTER, CENTER_6]
      *  [BELLE, LEADER, END, CENTER_6, OUTSIDE_6]
      *  [BELLE, TRAILER, END, CENTER_6, OUTSIDE_6]
      *  [POINT, CENTER, OUTSIDE_2, OUTSIDE_6]
      */
    public static final NamedTaggedFormation LH_POINT_TO_POINT_DIAMONDS =
        new NamedTaggedFormation("LH POINT-TO-POINT DIAMONDS",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(5), ExactRotation.WEST), Tag.POINT, Tag.CENTER, Tag.OUTSIDE_2, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(3), ExactRotation.SOUTH), Tag.BELLE, Tag.TRAILER, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(3), ExactRotation.NORTH), Tag.BELLE, Tag.LEADER, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(1), ExactRotation.EAST), Tag.BEAU, Tag.POINT, Tag.CENTER, Tag.VERY_CENTER, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-1), ExactRotation.WEST), Tag.BEAU, Tag.POINT, Tag.CENTER, Tag.VERY_CENTER, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-3), ExactRotation.SOUTH), Tag.BELLE, Tag.LEADER, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-3), ExactRotation.NORTH), Tag.BELLE, Tag.TRAILER, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-5), ExactRotation.EAST), Tag.POINT, Tag.CENTER, Tag.OUTSIDE_2, Tag.OUTSIDE_6));

    /** LH POINT-TO-POINT FACING DIAMONDS formation.
      * @doc.test
      *  js> tf = FormationList.LH_POINT_TO_POINT_FACING_DIAMONDS; tf.mapStd([]).toStringDiagram('|');
      *  |  1B>
      *  |
      *  |1Gv  2B^
      *  |
      *  |  2G<
      *  |
      *  |  4G>
      *  |
      *  |4Bv  3G^
      *  |
      *  |  3B<
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [POINT, CENTER, OUTSIDE_2, OUTSIDE_6]
      *  [BELLE, TRAILER, END, CENTER_6, OUTSIDE_6]
      *  [BELLE, LEADER, END, CENTER_6, OUTSIDE_6]
      *  [BELLE, POINT, CENTER, VERY_CENTER, CENTER_6]
      *  [BELLE, POINT, CENTER, VERY_CENTER, CENTER_6]
      *  [BELLE, LEADER, END, CENTER_6, OUTSIDE_6]
      *  [BELLE, TRAILER, END, CENTER_6, OUTSIDE_6]
      *  [POINT, CENTER, OUTSIDE_2, OUTSIDE_6]
      */
    public static final NamedTaggedFormation LH_POINT_TO_POINT_FACING_DIAMONDS =
        new NamedTaggedFormation("LH POINT-TO-POINT FACING DIAMONDS",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(5), ExactRotation.EAST), Tag.POINT, Tag.CENTER, Tag.OUTSIDE_2, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(3), ExactRotation.SOUTH), Tag.BELLE, Tag.TRAILER, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(3), ExactRotation.NORTH), Tag.BELLE, Tag.LEADER, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(1), ExactRotation.WEST), Tag.BELLE, Tag.POINT, Tag.CENTER, Tag.VERY_CENTER, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-1), ExactRotation.EAST), Tag.BELLE, Tag.POINT, Tag.CENTER, Tag.VERY_CENTER, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-3), ExactRotation.SOUTH), Tag.BELLE, Tag.LEADER, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-3), ExactRotation.NORTH), Tag.BELLE, Tag.TRAILER, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-5), ExactRotation.WEST), Tag.POINT, Tag.CENTER, Tag.OUTSIDE_2, Tag.OUTSIDE_6));

    /** RH TWIN FACING DIAMONDS formation.
      * @doc.test
      *  js> tf = FormationList.RH_TWIN_FACING_DIAMONDS; tf.mapStd([]).toStringDiagram('|');
      *  |  1B<       1G<
      *  |
      *  |2B^  2Gv  4G^  4Bv
      *  |
      *  |  3G>       3B>
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [LEADER, POINT, END, CENTER_6, OUTSIDE_6]
      *  [TRAILER, POINT, END, CENTER_6, OUTSIDE_6]
      *  [BEAU, CENTER, OUTSIDE_2, OUTSIDE_6]
      *  [BEAU, CENTER, VERY_CENTER, CENTER_6]
      *  [BEAU, CENTER, VERY_CENTER, CENTER_6]
      *  [BEAU, CENTER, OUTSIDE_2, OUTSIDE_6]
      *  [TRAILER, POINT, END, CENTER_6, OUTSIDE_6]
      *  [LEADER, POINT, END, CENTER_6, OUTSIDE_6]
      */
    public static final NamedTaggedFormation RH_TWIN_FACING_DIAMONDS =
        new NamedTaggedFormation("RH TWIN FACING DIAMONDS",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-2), Fraction.valueOf(2), ExactRotation.WEST), Tag.LEADER, Tag.POINT, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(2), Fraction.valueOf(2), ExactRotation.WEST), Tag.TRAILER, Tag.POINT, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BEAU, Tag.CENTER, Tag.OUTSIDE_2, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BEAU, Tag.CENTER, Tag.VERY_CENTER, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BEAU, Tag.CENTER, Tag.VERY_CENTER, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BEAU, Tag.CENTER, Tag.OUTSIDE_2, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-2), Fraction.valueOf(-2), ExactRotation.EAST), Tag.TRAILER, Tag.POINT, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(2), Fraction.valueOf(-2), ExactRotation.EAST), Tag.LEADER, Tag.POINT, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6));

    /** LH TWIN FACING DIAMONDS formation.
      * @doc.test
      *  js> tf = FormationList.LH_TWIN_FACING_DIAMONDS; tf.mapStd([]).toStringDiagram('|');
      *  |  1B>       1G>
      *  |
      *  |2Bv  2G^  4Gv  4B^
      *  |
      *  |  3G<       3B<
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [TRAILER, POINT, END, CENTER_6, OUTSIDE_6]
      *  [LEADER, POINT, END, CENTER_6, OUTSIDE_6]
      *  [BELLE, CENTER, OUTSIDE_2, OUTSIDE_6]
      *  [BELLE, CENTER, VERY_CENTER, CENTER_6]
      *  [BELLE, CENTER, VERY_CENTER, CENTER_6]
      *  [BELLE, CENTER, OUTSIDE_2, OUTSIDE_6]
      *  [LEADER, POINT, END, CENTER_6, OUTSIDE_6]
      *  [TRAILER, POINT, END, CENTER_6, OUTSIDE_6]
      */
    public static final NamedTaggedFormation LH_TWIN_FACING_DIAMONDS =
        new NamedTaggedFormation("LH TWIN FACING DIAMONDS",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-2), Fraction.valueOf(2), ExactRotation.EAST), Tag.TRAILER, Tag.POINT, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(2), Fraction.valueOf(2), ExactRotation.EAST), Tag.LEADER, Tag.POINT, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BELLE, Tag.CENTER, Tag.OUTSIDE_2, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BELLE, Tag.CENTER, Tag.VERY_CENTER, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BELLE, Tag.CENTER, Tag.VERY_CENTER, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BELLE, Tag.CENTER, Tag.OUTSIDE_2, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-2), Fraction.valueOf(-2), ExactRotation.WEST), Tag.LEADER, Tag.POINT, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(2), Fraction.valueOf(-2), ExactRotation.WEST), Tag.TRAILER, Tag.POINT, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6));

    /** TWIN GENERAL DIAMONDS formation.
      * @doc.test
      *  js> tf = FormationList.TWIN_GENERAL_DIAMONDS; tf.toStringDiagram('|');
      *  |  -         -
      *  |
      *  ||    |    |    |
      *  |
      *  |  -         -
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [POINT, END, CENTER_6, OUTSIDE_6]
      *  [POINT, END, CENTER_6, OUTSIDE_6]
      *  [CENTER, OUTSIDE_2, OUTSIDE_6]
      *  [CENTER, VERY_CENTER, CENTER_6]
      *  [CENTER, VERY_CENTER, CENTER_6]
      *  [CENTER, OUTSIDE_2, OUTSIDE_6]
      *  [POINT, END, CENTER_6, OUTSIDE_6]
      *  [POINT, END, CENTER_6, OUTSIDE_6]
      */
    public static final NamedTaggedFormation TWIN_GENERAL_DIAMONDS =
        new NamedTaggedFormation("TWIN GENERAL DIAMONDS",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-2), Fraction.valueOf(2), Rotation.fromAbsoluteString("-")), Tag.POINT, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(2), Fraction.valueOf(2), Rotation.fromAbsoluteString("-")), Tag.POINT, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.CENTER, Tag.OUTSIDE_2, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.CENTER, Tag.VERY_CENTER, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.CENTER, Tag.VERY_CENTER, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.CENTER, Tag.OUTSIDE_2, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-2), Fraction.valueOf(-2), Rotation.fromAbsoluteString("-")), Tag.POINT, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(2), Fraction.valueOf(-2), Rotation.fromAbsoluteString("-")), Tag.POINT, Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6));

    /** POINT-TO-POINT GENERAL DIAMONDS formation.
      * @doc.test
      *  js> tf = FormationList.POINT_TO_POINT_GENERAL_DIAMONDS; tf.toStringDiagram('|');
      *  |  -
      *  |
      *  ||    |
      *  |
      *  |  -
      *  |
      *  |  -
      *  |
      *  ||    |
      *  |
      *  |  -
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [POINT, CENTER, OUTSIDE_2, OUTSIDE_6]
      *  [END, CENTER_6, OUTSIDE_6]
      *  [END, CENTER_6, OUTSIDE_6]
      *  [POINT, CENTER, VERY_CENTER, CENTER_6]
      *  [POINT, CENTER, VERY_CENTER, CENTER_6]
      *  [END, CENTER_6, OUTSIDE_6]
      *  [END, CENTER_6, OUTSIDE_6]
      *  [POINT, CENTER, OUTSIDE_2, OUTSIDE_6]
      */
    public static final NamedTaggedFormation POINT_TO_POINT_GENERAL_DIAMONDS =
        new NamedTaggedFormation("POINT-TO-POINT GENERAL DIAMONDS",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(5), Rotation.fromAbsoluteString("-")), Tag.POINT, Tag.CENTER, Tag.OUTSIDE_2, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(3), Rotation.fromAbsoluteString("|")), Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(3), Rotation.fromAbsoluteString("|")), Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(1), Rotation.fromAbsoluteString("-")), Tag.POINT, Tag.CENTER, Tag.VERY_CENTER, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-1), Rotation.fromAbsoluteString("-")), Tag.POINT, Tag.CENTER, Tag.VERY_CENTER, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-3), Rotation.fromAbsoluteString("|")), Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-3), Rotation.fromAbsoluteString("|")), Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-5), Rotation.fromAbsoluteString("-")), Tag.POINT, Tag.CENTER, Tag.OUTSIDE_2, Tag.OUTSIDE_6));

    /** CONCENTRIC GENERAL DIAMONDS formation.
      * @doc.test
      *  js> tf = FormationList.CONCENTRIC_GENERAL_DIAMONDS; tf.toStringDiagram('|');
      *  |       -
      *  |
      *  |       -
      *  |
      *  ||    |    |    |
      *  |
      *  |       -
      *  |
      *  |       -
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [END, OUTSIDE_2, OUTSIDE_6]
      *  [END, CENTER_6, OUTSIDE_6]
      *  [CENTER, CENTER_6, OUTSIDE_6]
      *  [CENTER, VERY_CENTER, CENTER_6]
      *  [CENTER, VERY_CENTER, CENTER_6]
      *  [CENTER, CENTER_6, OUTSIDE_6]
      *  [END, CENTER_6, OUTSIDE_6]
      *  [END, OUTSIDE_2, OUTSIDE_6]
      */
    public static final NamedTaggedFormation CONCENTRIC_GENERAL_DIAMONDS =
        new NamedTaggedFormation("CONCENTRIC GENERAL DIAMONDS",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(4), Rotation.fromAbsoluteString("-")), Tag.END, Tag.OUTSIDE_2, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(2), Rotation.fromAbsoluteString("-")), Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.CENTER, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.CENTER, Tag.VERY_CENTER, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.CENTER, Tag.VERY_CENTER, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.CENTER, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-2), Rotation.fromAbsoluteString("-")), Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-4), Rotation.fromAbsoluteString("-")), Tag.END, Tag.OUTSIDE_2, Tag.OUTSIDE_6));

    /** RH HOURGLASS formation.
      * @doc.test
      *  js> tf = FormationList.RH_HOURGLASS; tf.mapStd([]).toStringDiagram('|');
      *  |       1B>
      *  |
      *  |1G^            2Bv
      *  |
      *  |     2G^  4Gv
      *  |
      *  |4B^            3Gv
      *  |
      *  |       3B<
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [POINT, CENTER]
      *  [END]
      *  [END]
      *  [BEAU, CENTER, VERY_CENTER]
      *  [BEAU, CENTER, VERY_CENTER]
      *  [END]
      *  [END]
      *  [POINT, CENTER]
      */
    public static final NamedTaggedFormation RH_HOURGLASS =
        new NamedTaggedFormation("RH HOURGLASS",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(4), ExactRotation.EAST), Tag.POINT, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(2), ExactRotation.NORTH), Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(2), ExactRotation.SOUTH), Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BEAU, Tag.CENTER, Tag.VERY_CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BEAU, Tag.CENTER, Tag.VERY_CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(-2), ExactRotation.NORTH), Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(-2), ExactRotation.SOUTH), Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-4), ExactRotation.WEST), Tag.POINT, Tag.CENTER));

    /** LH HOURGLASS formation.
      * @doc.test
      *  js> tf = FormationList.LH_HOURGLASS; tf.mapStd([]).toStringDiagram('|');
      *  |       1B<
      *  |
      *  |1Gv            2B^
      *  |
      *  |     2Gv  4G^
      *  |
      *  |4Bv            3G^
      *  |
      *  |       3B>
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [POINT, CENTER]
      *  [END]
      *  [END]
      *  [BELLE, CENTER, VERY_CENTER]
      *  [BELLE, CENTER, VERY_CENTER]
      *  [END]
      *  [END]
      *  [POINT, CENTER]
      */
    public static final NamedTaggedFormation LH_HOURGLASS =
        new NamedTaggedFormation("LH HOURGLASS",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(4), ExactRotation.WEST), Tag.POINT, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(2), ExactRotation.SOUTH), Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(2), ExactRotation.NORTH), Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BELLE, Tag.CENTER, Tag.VERY_CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BELLE, Tag.CENTER, Tag.VERY_CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(-2), ExactRotation.SOUTH), Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(-2), ExactRotation.NORTH), Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-4), ExactRotation.EAST), Tag.POINT, Tag.CENTER));

    /** GENERAL HOURGLASS formation.
      * @doc.test
      *  js> tf = FormationList.GENERAL_HOURGLASS; tf.toStringDiagram('|');
      *  |       -
      *  |
      *  ||              |
      *  |
      *  |     |    |
      *  |
      *  ||              |
      *  |
      *  |       -
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [POINT, CENTER]
      *  [END]
      *  [END]
      *  [CENTER, VERY_CENTER]
      *  [CENTER, VERY_CENTER]
      *  [END]
      *  [END]
      *  [POINT, CENTER]
      */
    public static final NamedTaggedFormation GENERAL_HOURGLASS =
        new NamedTaggedFormation("GENERAL HOURGLASS",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(4), Rotation.fromAbsoluteString("-")), Tag.POINT, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(2), Rotation.fromAbsoluteString("|")), Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(2), Rotation.fromAbsoluteString("|")), Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.CENTER, Tag.VERY_CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.CENTER, Tag.VERY_CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(-2), Rotation.fromAbsoluteString("|")), Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(-2), Rotation.fromAbsoluteString("|")), Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-4), Rotation.fromAbsoluteString("-")), Tag.POINT, Tag.CENTER));

    /** RH TIDAL WAVE formation.
      * @doc.test
      *  js> tf = FormationList.RH_TIDAL_WAVE; tf.mapStd([]).toStringDiagram('|');
      *  |1B^  1Gv  2B^  2Gv  4G^  4Bv  3G^  3Bv
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [BEAU, END, OUTSIDE_2, OUTSIDE_6]
      *  [BEAU, CENTER, CENTER_6, OUTSIDE_6]
      *  [BEAU, CENTER, CENTER_6, OUTSIDE_6]
      *  [BEAU, VERY_CENTER, END, CENTER_6]
      *  [BEAU, VERY_CENTER, END, CENTER_6]
      *  [BEAU, CENTER, CENTER_6, OUTSIDE_6]
      *  [BEAU, CENTER, CENTER_6, OUTSIDE_6]
      *  [BEAU, END, OUTSIDE_2, OUTSIDE_6]
      */
    public static final NamedTaggedFormation RH_TIDAL_WAVE =
        new NamedTaggedFormation("RH TIDAL WAVE",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-7), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BEAU, Tag.END, Tag.OUTSIDE_2, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-5), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BEAU, Tag.CENTER, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BEAU, Tag.CENTER, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BEAU, Tag.VERY_CENTER, Tag.END, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BEAU, Tag.VERY_CENTER, Tag.END, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BEAU, Tag.CENTER, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(5), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BEAU, Tag.CENTER, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(7), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BEAU, Tag.END, Tag.OUTSIDE_2, Tag.OUTSIDE_6));

    /** LH TIDAL WAVE formation.
      * @doc.test
      *  js> tf = FormationList.LH_TIDAL_WAVE; tf.mapStd([]).toStringDiagram('|');
      *  |1Bv  1G^  2Bv  2G^  4Gv  4B^  3Gv  3B^
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [BELLE, END, OUTSIDE_2, OUTSIDE_6]
      *  [BELLE, CENTER, CENTER_6, OUTSIDE_6]
      *  [BELLE, CENTER, CENTER_6, OUTSIDE_6]
      *  [BELLE, VERY_CENTER, END, CENTER_6]
      *  [BELLE, VERY_CENTER, END, CENTER_6]
      *  [BELLE, CENTER, CENTER_6, OUTSIDE_6]
      *  [BELLE, CENTER, CENTER_6, OUTSIDE_6]
      *  [BELLE, END, OUTSIDE_2, OUTSIDE_6]
      */
    public static final NamedTaggedFormation LH_TIDAL_WAVE =
        new NamedTaggedFormation("LH TIDAL WAVE",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-7), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BELLE, Tag.END, Tag.OUTSIDE_2, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-5), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BELLE, Tag.CENTER, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BELLE, Tag.CENTER, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BELLE, Tag.VERY_CENTER, Tag.END, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BELLE, Tag.VERY_CENTER, Tag.END, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BELLE, Tag.CENTER, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(5), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BELLE, Tag.CENTER, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(7), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BELLE, Tag.END, Tag.OUTSIDE_2, Tag.OUTSIDE_6));

    /** RH TIDAL TWO-FACED LINE formation.
      * @doc.test
      *  js> tf = FormationList.RH_TIDAL_TWO_FACED_LINE; tf.mapStd([]).toStringDiagram('|');
      *  |1B^  1G^  2Gv  2Bv  4B^  4G^  3Gv  3Bv
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [BEAU, END, OUTSIDE_2, OUTSIDE_6]
      *  [BELLE, CENTER, CENTER_6, OUTSIDE_6]
      *  [BELLE, CENTER, CENTER_6, OUTSIDE_6]
      *  [BEAU, VERY_CENTER, END, CENTER_6]
      *  [BEAU, VERY_CENTER, END, CENTER_6]
      *  [BELLE, CENTER, CENTER_6, OUTSIDE_6]
      *  [BELLE, CENTER, CENTER_6, OUTSIDE_6]
      *  [BEAU, END, OUTSIDE_2, OUTSIDE_6]
      */
    public static final NamedTaggedFormation RH_TIDAL_TWO_FACED_LINE =
        new NamedTaggedFormation("RH TIDAL TWO-FACED LINE",
            new StandardDancer[] {
                StandardDancer.COUPLE_1_BOY,
                StandardDancer.COUPLE_1_GIRL,
                StandardDancer.COUPLE_2_GIRL,
                StandardDancer.COUPLE_2_BOY,
            },
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-7), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BEAU, Tag.END, Tag.OUTSIDE_2, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-5), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BELLE, Tag.CENTER, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BELLE, Tag.CENTER, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BEAU, Tag.VERY_CENTER, Tag.END, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BEAU, Tag.VERY_CENTER, Tag.END, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BELLE, Tag.CENTER, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(5), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BELLE, Tag.CENTER, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(7), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BEAU, Tag.END, Tag.OUTSIDE_2, Tag.OUTSIDE_6));

    /** LH TIDAL TWO-FACED LINE formation.
      * @doc.test
      *  js> tf = FormationList.LH_TIDAL_TWO_FACED_LINE; tf.mapStd([]).toStringDiagram('|');
      *  |1Gv  1Bv  2B^  2G^  4Gv  4Bv  3B^  3G^
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [BELLE, END, OUTSIDE_2, OUTSIDE_6]
      *  [BEAU, CENTER, CENTER_6, OUTSIDE_6]
      *  [BEAU, CENTER, CENTER_6, OUTSIDE_6]
      *  [BELLE, VERY_CENTER, END, CENTER_6]
      *  [BELLE, VERY_CENTER, END, CENTER_6]
      *  [BEAU, CENTER, CENTER_6, OUTSIDE_6]
      *  [BEAU, CENTER, CENTER_6, OUTSIDE_6]
      *  [BELLE, END, OUTSIDE_2, OUTSIDE_6]
      */
    public static final NamedTaggedFormation LH_TIDAL_TWO_FACED_LINE =
        new NamedTaggedFormation("LH TIDAL TWO-FACED LINE",
            new StandardDancer[] {
                StandardDancer.COUPLE_1_GIRL,
                StandardDancer.COUPLE_1_BOY,
                StandardDancer.COUPLE_2_BOY,
                StandardDancer.COUPLE_2_GIRL,
            },
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-7), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BELLE, Tag.END, Tag.OUTSIDE_2, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-5), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BEAU, Tag.CENTER, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BEAU, Tag.CENTER, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BELLE, Tag.VERY_CENTER, Tag.END, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BELLE, Tag.VERY_CENTER, Tag.END, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BEAU, Tag.CENTER, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(5), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BEAU, Tag.CENTER, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(7), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BELLE, Tag.END, Tag.OUTSIDE_2, Tag.OUTSIDE_6));

    /** RH TIDAL LINE formation.
      * @doc.test
      *  js> tf = FormationList.RH_TIDAL_LINE; tf.mapStd([]).toStringDiagram('|');
      *  |1B^  1G^  2B^  2G^  4Gv  4Bv  3Gv  3Bv
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [BEAU, END, OUTSIDE_2, OUTSIDE_6]
      *  [BELLE, CENTER, CENTER_6, OUTSIDE_6]
      *  [BEAU, CENTER, CENTER_6, OUTSIDE_6]
      *  [BELLE, VERY_CENTER, END, CENTER_6]
      *  [BELLE, VERY_CENTER, END, CENTER_6]
      *  [BEAU, CENTER, CENTER_6, OUTSIDE_6]
      *  [BELLE, CENTER, CENTER_6, OUTSIDE_6]
      *  [BEAU, END, OUTSIDE_2, OUTSIDE_6]
      */
    public static final NamedTaggedFormation RH_TIDAL_LINE =
        new NamedTaggedFormation("RH TIDAL LINE",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-7), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BEAU, Tag.END, Tag.OUTSIDE_2, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-5), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BELLE, Tag.CENTER, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BEAU, Tag.CENTER, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BELLE, Tag.VERY_CENTER, Tag.END, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BELLE, Tag.VERY_CENTER, Tag.END, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BEAU, Tag.CENTER, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(5), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BELLE, Tag.CENTER, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(7), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BEAU, Tag.END, Tag.OUTSIDE_2, Tag.OUTSIDE_6));

    /** LH TIDAL LINE formation.
      * @doc.test
      *  js> tf = FormationList.LH_TIDAL_LINE; tf.mapStd([]).toStringDiagram('|');
      *  |1Gv  1Bv  2Gv  2Bv  4B^  4G^  3B^  3G^
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [BELLE, END, OUTSIDE_2, OUTSIDE_6]
      *  [BEAU, CENTER, CENTER_6, OUTSIDE_6]
      *  [BELLE, CENTER, CENTER_6, OUTSIDE_6]
      *  [BEAU, VERY_CENTER, END, CENTER_6]
      *  [BEAU, VERY_CENTER, END, CENTER_6]
      *  [BELLE, CENTER, CENTER_6, OUTSIDE_6]
      *  [BEAU, CENTER, CENTER_6, OUTSIDE_6]
      *  [BELLE, END, OUTSIDE_2, OUTSIDE_6]
      */
    public static final NamedTaggedFormation LH_TIDAL_LINE =
        new NamedTaggedFormation("LH TIDAL LINE",
            new StandardDancer[] {
                StandardDancer.COUPLE_1_GIRL,
                StandardDancer.COUPLE_1_BOY,
                StandardDancer.COUPLE_2_GIRL,
                StandardDancer.COUPLE_2_BOY,
            },
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-7), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BELLE, Tag.END, Tag.OUTSIDE_2, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-5), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BEAU, Tag.CENTER, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BELLE, Tag.CENTER, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.BEAU, Tag.VERY_CENTER, Tag.END, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BEAU, Tag.VERY_CENTER, Tag.END, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BELLE, Tag.CENTER, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(5), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BEAU, Tag.CENTER, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(7), Fraction.valueOf(0), ExactRotation.NORTH), Tag.BELLE, Tag.END, Tag.OUTSIDE_2, Tag.OUTSIDE_6));

    /** GENERAL TIDAL LINE formation.
      * @doc.test
      *  js> tf = FormationList.GENERAL_TIDAL_LINE; tf.toStringDiagram('|');
      *  ||    |    |    |    |    |    |    |
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [END, OUTSIDE_2, OUTSIDE_4, OUTSIDE_6]
      *  [CENTER, OUTSIDE_4, CENTER_6, OUTSIDE_6]
      *  [CENTER, CENTER_6, OUTSIDE_6]
      *  [VERY_CENTER, END, CENTER_6]
      *  [VERY_CENTER, END, CENTER_6]
      *  [CENTER, CENTER_6, OUTSIDE_6]
      *  [CENTER, OUTSIDE_4, CENTER_6, OUTSIDE_6]
      *  [END, OUTSIDE_2, OUTSIDE_4, OUTSIDE_6]
      */
    public static final NamedTaggedFormation GENERAL_TIDAL_LINE =
        new NamedTaggedFormation("GENERAL TIDAL LINE",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-7), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.END, Tag.OUTSIDE_2, Tag.OUTSIDE_4, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-5), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.CENTER, Tag.OUTSIDE_4, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.CENTER, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.VERY_CENTER, Tag.END, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.VERY_CENTER, Tag.END, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.CENTER, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(5), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.CENTER, Tag.OUTSIDE_4, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(7), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.END, Tag.OUTSIDE_2, Tag.OUTSIDE_4, Tag.OUTSIDE_6));

    /** RH GALAXY formation.
      * @doc.test
      *  js> tf = FormationList.RH_GALAXY; tf.mapStd([]).toStringDiagram('|');
      *  |       1B>
      *  |
      *  |     1G^  2Bv
      *  |
      *  |2G^            4Gv
      *  |
      *  |     4B^  3Gv
      *  |
      *  |       3B<
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [POINT, END]
      *  [BEAU, CENTER]
      *  [BEAU, CENTER]
      *  [POINT, END]
      *  [POINT, END]
      *  [BEAU, CENTER]
      *  [BEAU, CENTER]
      *  [POINT, END]
      */
    public static final NamedTaggedFormation RH_GALAXY =
        new NamedTaggedFormation("RH GALAXY",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(4), ExactRotation.EAST), Tag.POINT, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(2), ExactRotation.NORTH), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(2), ExactRotation.SOUTH), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(0), ExactRotation.NORTH), Tag.POINT, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.POINT, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-2), ExactRotation.NORTH), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-2), ExactRotation.SOUTH), Tag.BEAU, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-4), ExactRotation.WEST), Tag.POINT, Tag.END));

    /** LH GALAXY formation.
      * @doc.test
      *  js> tf = FormationList.LH_GALAXY; tf.mapStd([]).toStringDiagram('|');
      *  |       1B<
      *  |
      *  |     1Gv  2B^
      *  |
      *  |2Gv            4G^
      *  |
      *  |     4Bv  3G^
      *  |
      *  |       3B>
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [POINT, END]
      *  [BELLE, CENTER]
      *  [BELLE, CENTER]
      *  [POINT, END]
      *  [POINT, END]
      *  [BELLE, CENTER]
      *  [BELLE, CENTER]
      *  [POINT, END]
      */
    public static final NamedTaggedFormation LH_GALAXY =
        new NamedTaggedFormation("LH GALAXY",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(4), ExactRotation.WEST), Tag.POINT, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(2), ExactRotation.SOUTH), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(2), ExactRotation.NORTH), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(0), ExactRotation.SOUTH), Tag.POINT, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(0), ExactRotation.NORTH), Tag.POINT, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-2), ExactRotation.SOUTH), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-2), ExactRotation.NORTH), Tag.BELLE, Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-4), ExactRotation.EAST), Tag.POINT, Tag.END));

    /** GENERAL GALAXY formation.
      * @doc.test
      *  js> tf = FormationList.GENERAL_GALAXY; tf.toStringDiagram('|');
      *  |       -
      *  |
      *  |     |    |
      *  |
      *  ||              |
      *  |
      *  |     |    |
      *  |
      *  |       -
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [POINT, END]
      *  [CENTER]
      *  [CENTER]
      *  [POINT, END]
      *  [POINT, END]
      *  [CENTER]
      *  [CENTER]
      *  [POINT, END]
      */
    public static final NamedTaggedFormation GENERAL_GALAXY =
        new NamedTaggedFormation("GENERAL GALAXY",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(4), Rotation.fromAbsoluteString("-")), Tag.POINT, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(2), Rotation.fromAbsoluteString("|")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(2), Rotation.fromAbsoluteString("|")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.POINT, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.POINT, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-2), Rotation.fromAbsoluteString("|")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-2), Rotation.fromAbsoluteString("|")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-4), Rotation.fromAbsoluteString("-")), Tag.POINT, Tag.END));

    /** GENERAL SPINDLE formation.
      * @doc.test
      *  js> tf = FormationList.GENERAL_SPINDLE; tf.toStringDiagram('|');
      *  |  -
      *  |
      *  ||    |
      *  |
      *  ||    |
      *  |
      *  ||    |
      *  |
      *  |  -
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [POINT, END]
      *  [CENTER_6]
      *  [CENTER_6]
      *  [VERY_CENTER, CENTER_6]
      *  [VERY_CENTER, CENTER_6]
      *  [CENTER_6]
      *  [CENTER_6]
      *  [POINT, END]
      */
    public static final NamedTaggedFormation GENERAL_SPINDLE =
        new NamedTaggedFormation("GENERAL SPINDLE",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(4), Rotation.fromAbsoluteString("-")), Tag.POINT, Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(2), Rotation.fromAbsoluteString("|")), Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(2), Rotation.fromAbsoluteString("|")), Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.VERY_CENTER, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.VERY_CENTER, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-2), Rotation.fromAbsoluteString("|")), Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-2), Rotation.fromAbsoluteString("|")), Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-4), Rotation.fromAbsoluteString("-")), Tag.POINT, Tag.END));

    /** GENERAL 1x3 DIAMOND formation.
      * @doc.test
      *  js> tf = FormationList.GENERAL_1x3_DIAMOND; tf.toStringDiagram('|');
      *  |               -
      *  ||    |    |         |    |    |
      *  |               -
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [CENTER, VERY_CENTER, CENTER_6]
      *  [END, OUTSIDE_2, OUTSIDE_6]
      *  [END, CENTER_6, OUTSIDE_6]
      *  [CENTER, CENTER_6, OUTSIDE_6]
      *  [CENTER, CENTER_6, OUTSIDE_6]
      *  [END, CENTER_6, OUTSIDE_6]
      *  [END, OUTSIDE_2, OUTSIDE_6]
      *  [CENTER, VERY_CENTER, CENTER_6]
      */
    public static final NamedTaggedFormation GENERAL_1x3_DIAMOND =
        new NamedTaggedFormation("GENERAL 1x3 DIAMOND",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(1), Rotation.fromAbsoluteString("-")), Tag.CENTER, Tag.VERY_CENTER, Tag.CENTER_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-6), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.END, Tag.OUTSIDE_2, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-4), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-2), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.CENTER, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(2), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.CENTER, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(4), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.END, Tag.CENTER_6, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(6), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.END, Tag.OUTSIDE_2, Tag.OUTSIDE_6),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-1), Rotation.fromAbsoluteString("-")), Tag.CENTER, Tag.VERY_CENTER, Tag.CENTER_6));

    /** O DOUBLE PASS THRU formation.
      * @doc.test
      *  js> tf = FormationList.O_DOUBLE_PASS_THRU; tf.mapStd([]).toStringDiagram('|');
      *  |     1Gv  1Bv
      *  |
      *  |2Gv            2Bv
      *  |
      *  |4B^            4G^
      *  |
      *  |     3B^  3G^
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [BELLE, TRAILER, END, NUMBER_4]
      *  [BEAU, TRAILER, END, NUMBER_4]
      *  [BELLE, LEADER, CENTER, NUMBER_3]
      *  [BEAU, LEADER, CENTER, NUMBER_3]
      *  [BEAU, LEADER, CENTER, NUMBER_3]
      *  [BELLE, LEADER, CENTER, NUMBER_3]
      *  [BEAU, TRAILER, END, NUMBER_4]
      *  [BELLE, TRAILER, END, NUMBER_4]
      */
    public static final NamedTaggedFormation O_DOUBLE_PASS_THRU =
        new NamedTaggedFormation("O DOUBLE PASS THRU",
            new StandardDancer[] {
                StandardDancer.COUPLE_1_GIRL,
                StandardDancer.COUPLE_1_BOY,
                StandardDancer.COUPLE_2_GIRL,
                StandardDancer.COUPLE_2_BOY,
            },
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(3), ExactRotation.SOUTH), Tag.BELLE, Tag.TRAILER, Tag.END, Tag.NUMBER_4),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(3), ExactRotation.SOUTH), Tag.BEAU, Tag.TRAILER, Tag.END, Tag.NUMBER_4),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(1), ExactRotation.SOUTH), Tag.BELLE, Tag.LEADER, Tag.CENTER, Tag.NUMBER_3),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(1), ExactRotation.SOUTH), Tag.BEAU, Tag.LEADER, Tag.CENTER, Tag.NUMBER_3),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(-1), ExactRotation.NORTH), Tag.BEAU, Tag.LEADER, Tag.CENTER, Tag.NUMBER_3),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(-1), ExactRotation.NORTH), Tag.BELLE, Tag.LEADER, Tag.CENTER, Tag.NUMBER_3),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-3), ExactRotation.NORTH), Tag.BEAU, Tag.TRAILER, Tag.END, Tag.NUMBER_4),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-3), ExactRotation.NORTH), Tag.BELLE, Tag.TRAILER, Tag.END, Tag.NUMBER_4));

    /** GENERAL O formation.
      * @doc.test
      *  js> tf = FormationList.GENERAL_O; tf.toStringDiagram('|');
      *  |     |    |
      *  |
      *  ||              |
      *  |
      *  ||              |
      *  |
      *  |     |    |
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [END]
      *  [END]
      *  [CENTER]
      *  [CENTER]
      *  [CENTER]
      *  [CENTER]
      *  [END]
      *  [END]
      */
    public static final NamedTaggedFormation GENERAL_O =
        new NamedTaggedFormation("GENERAL O",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(3), Rotation.fromAbsoluteString("|")), Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(3), Rotation.fromAbsoluteString("|")), Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(1), Rotation.fromAbsoluteString("|")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(1), Rotation.fromAbsoluteString("|")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(-1), Rotation.fromAbsoluteString("|")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(-1), Rotation.fromAbsoluteString("|")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-3), Rotation.fromAbsoluteString("|")), Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-3), Rotation.fromAbsoluteString("|")), Tag.END));

    /** BUTTERFLY DOUBLE PASS THRU formation.
      * @doc.test
      *  js> tf = FormationList.BUTTERFLY_DOUBLE_PASS_THRU; tf.mapStd([]).toStringDiagram('|');
      *  |1Gv            1Bv
      *  |
      *  |     2Gv  2Bv
      *  |
      *  |     4B^  4G^
      *  |
      *  |3B^            3G^
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [BELLE, TRAILER, END, NUMBER_4]
      *  [BEAU, TRAILER, END, NUMBER_4]
      *  [BELLE, LEADER, CENTER, NUMBER_3]
      *  [BEAU, LEADER, CENTER, NUMBER_3]
      *  [BEAU, LEADER, CENTER, NUMBER_3]
      *  [BELLE, LEADER, CENTER, NUMBER_3]
      *  [BEAU, TRAILER, END, NUMBER_4]
      *  [BELLE, TRAILER, END, NUMBER_4]
      */
    public static final NamedTaggedFormation BUTTERFLY_DOUBLE_PASS_THRU =
        new NamedTaggedFormation("BUTTERFLY DOUBLE PASS THRU",
            new StandardDancer[] {
                StandardDancer.COUPLE_1_GIRL,
                StandardDancer.COUPLE_1_BOY,
                StandardDancer.COUPLE_2_GIRL,
                StandardDancer.COUPLE_2_BOY,
            },
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(3), ExactRotation.SOUTH), Tag.BELLE, Tag.TRAILER, Tag.END, Tag.NUMBER_4),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(3), ExactRotation.SOUTH), Tag.BEAU, Tag.TRAILER, Tag.END, Tag.NUMBER_4),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(1), ExactRotation.SOUTH), Tag.BELLE, Tag.LEADER, Tag.CENTER, Tag.NUMBER_3),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(1), ExactRotation.SOUTH), Tag.BEAU, Tag.LEADER, Tag.CENTER, Tag.NUMBER_3),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-1), ExactRotation.NORTH), Tag.BEAU, Tag.LEADER, Tag.CENTER, Tag.NUMBER_3),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-1), ExactRotation.NORTH), Tag.BELLE, Tag.LEADER, Tag.CENTER, Tag.NUMBER_3),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(-3), ExactRotation.NORTH), Tag.BEAU, Tag.TRAILER, Tag.END, Tag.NUMBER_4),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(-3), ExactRotation.NORTH), Tag.BELLE, Tag.TRAILER, Tag.END, Tag.NUMBER_4));

    /** GENERAL BUTTERFLY formation.
      * @doc.test
      *  js> tf = FormationList.GENERAL_BUTTERFLY; tf.toStringDiagram('|');
      *  ||              |
      *  |
      *  |     |    |
      *  |
      *  |     |    |
      *  |
      *  ||              |
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [END]
      *  [END]
      *  [CENTER]
      *  [CENTER]
      *  [CENTER]
      *  [CENTER]
      *  [END]
      *  [END]
      */
    public static final NamedTaggedFormation GENERAL_BUTTERFLY =
        new NamedTaggedFormation("GENERAL BUTTERFLY",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(3), Rotation.fromAbsoluteString("|")), Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(3), Rotation.fromAbsoluteString("|")), Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(1), Rotation.fromAbsoluteString("|")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(1), Rotation.fromAbsoluteString("|")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-1), Rotation.fromAbsoluteString("|")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-1), Rotation.fromAbsoluteString("|")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(-3), Rotation.fromAbsoluteString("|")), Tag.END),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(-3), Rotation.fromAbsoluteString("|")), Tag.END));

    /** 1x12 formation.
      * @doc.test
      *  js> tf = FormationList._1x12; tf.toStringDiagram('|');
      *  |+    +    +    +    +    +    +    +    +    +    +    +
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [OUTSIDE_8]
      *  [OUTSIDE_8]
      *  [OUTSIDE_8]
      *  [OUTSIDE_8]
      *  [CENTER]
      *  [CENTER]
      *  [CENTER]
      *  [CENTER]
      *  [OUTSIDE_8]
      *  [OUTSIDE_8]
      *  [OUTSIDE_8]
      *  [OUTSIDE_8]
      */
    public static final NamedTaggedFormation _1x12 =
        new NamedTaggedFormation("1x12",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-11), Fraction.valueOf(0), Rotation.fromAbsoluteString("+")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-9), Fraction.valueOf(0), Rotation.fromAbsoluteString("+")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-7), Fraction.valueOf(0), Rotation.fromAbsoluteString("+")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-5), Fraction.valueOf(0), Rotation.fromAbsoluteString("+")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(0), Rotation.fromAbsoluteString("+")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), Rotation.fromAbsoluteString("+")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), Rotation.fromAbsoluteString("+")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(0), Rotation.fromAbsoluteString("+")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(5), Fraction.valueOf(0), Rotation.fromAbsoluteString("+")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(7), Fraction.valueOf(0), Rotation.fromAbsoluteString("+")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(9), Fraction.valueOf(0), Rotation.fromAbsoluteString("+")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(11), Fraction.valueOf(0), Rotation.fromAbsoluteString("+")), Tag.OUTSIDE_8));

    /** 2x6 formation.
      * @doc.test
      *  js> tf = FormationList._2x6; tf.toStringDiagram('|');
      *  |+    +    +    +    +    +
      *  |
      *  |+    +    +    +    +    +
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [OUTSIDE_8]
      *  [OUTSIDE_8]
      *  [CENTER]
      *  [CENTER]
      *  [OUTSIDE_8]
      *  [OUTSIDE_8]
      *  [OUTSIDE_8]
      *  [OUTSIDE_8]
      *  [CENTER]
      *  [CENTER]
      *  [OUTSIDE_8]
      *  [OUTSIDE_8]
      */
    public static final NamedTaggedFormation _2x6 =
        new NamedTaggedFormation("2x6",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-5), Fraction.valueOf(1), Rotation.fromAbsoluteString("+")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(1), Rotation.fromAbsoluteString("+")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(1), Rotation.fromAbsoluteString("+")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(1), Rotation.fromAbsoluteString("+")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(1), Rotation.fromAbsoluteString("+")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(5), Fraction.valueOf(1), Rotation.fromAbsoluteString("+")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-5), Fraction.valueOf(-1), Rotation.fromAbsoluteString("+")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(-1), Rotation.fromAbsoluteString("+")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-1), Rotation.fromAbsoluteString("+")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-1), Rotation.fromAbsoluteString("+")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(-1), Rotation.fromAbsoluteString("+")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(5), Fraction.valueOf(-1), Rotation.fromAbsoluteString("+")), Tag.OUTSIDE_8));

    /** 3x4 formation.
      * @doc.test
      *  js> tf = FormationList._3x4; tf.toStringDiagram('|');
      *  |+    +    +    +
      *  |
      *  |+    +    +    +
      *  |
      *  |+    +    +    +
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [OUTSIDE_8]
      *  [OUTSIDE_8]
      *  [OUTSIDE_8]
      *  [OUTSIDE_8]
      *  [CENTER]
      *  [CENTER]
      *  [CENTER]
      *  [CENTER]
      *  [OUTSIDE_8]
      *  [OUTSIDE_8]
      *  [OUTSIDE_8]
      *  [OUTSIDE_8]
      */
    public static final NamedTaggedFormation _3x4 =
        new NamedTaggedFormation("3x4",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(2), Rotation.fromAbsoluteString("+")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(2), Rotation.fromAbsoluteString("+")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(2), Rotation.fromAbsoluteString("+")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(2), Rotation.fromAbsoluteString("+")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(0), Rotation.fromAbsoluteString("+")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), Rotation.fromAbsoluteString("+")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), Rotation.fromAbsoluteString("+")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(0), Rotation.fromAbsoluteString("+")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(-2), Rotation.fromAbsoluteString("+")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-2), Rotation.fromAbsoluteString("+")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-2), Rotation.fromAbsoluteString("+")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(-2), Rotation.fromAbsoluteString("+")), Tag.OUTSIDE_8));

    /** TRIPLE GENERAL H formation.
      * @doc.test
      *  js> tf = FormationList.TRIPLE_GENERAL_H; tf.toStringDiagram('|');
      *  |+    +    +    +
      *  |
      *  |       +
      *  |
      *  |       +
      *  |
      *  |       +
      *  |
      *  |       +
      *  |
      *  |+    +    +    +
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [OUTSIDE_8]
      *  [OUTSIDE_8]
      *  [OUTSIDE_8]
      *  [OUTSIDE_8]
      *  [CENTER]
      *  [CENTER]
      *  [CENTER]
      *  [CENTER]
      *  [OUTSIDE_8]
      *  [OUTSIDE_8]
      *  [OUTSIDE_8]
      *  [OUTSIDE_8]
      */
    public static final NamedTaggedFormation TRIPLE_GENERAL_H =
        new NamedTaggedFormation("TRIPLE GENERAL H",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(5), Rotation.fromAbsoluteString("+")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(5), Rotation.fromAbsoluteString("+")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(5), Rotation.fromAbsoluteString("+")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(5), Rotation.fromAbsoluteString("+")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(3), Rotation.fromAbsoluteString("+")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(1), Rotation.fromAbsoluteString("+")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-1), Rotation.fromAbsoluteString("+")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-3), Rotation.fromAbsoluteString("+")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(-5), Rotation.fromAbsoluteString("+")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-5), Rotation.fromAbsoluteString("+")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-5), Rotation.fromAbsoluteString("+")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(-5), Rotation.fromAbsoluteString("+")), Tag.OUTSIDE_8));

    /** TRIPLE GENERAL PLUS formation.
      * @doc.test
      *  js> tf = FormationList.TRIPLE_GENERAL_PLUS; tf.toStringDiagram('|');
      *  |                    +
      *  |
      *  |                    +
      *  |+    +    +    +         +    +    +    +
      *  |                    +
      *  |
      *  |                    +
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [OUTSIDE_8]
      *  [CENTER]
      *  [OUTSIDE_8]
      *  [OUTSIDE_8]
      *  [OUTSIDE_8]
      *  [CENTER]
      *  [CENTER]
      *  [OUTSIDE_8]
      *  [OUTSIDE_8]
      *  [OUTSIDE_8]
      *  [CENTER]
      *  [OUTSIDE_8]
      */
    public static final NamedTaggedFormation TRIPLE_GENERAL_PLUS =
        new NamedTaggedFormation("TRIPLE GENERAL PLUS",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(3), Rotation.fromAbsoluteString("+")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(1), Rotation.fromAbsoluteString("+")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-8), Fraction.valueOf(0), Rotation.fromAbsoluteString("+")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-6), Fraction.valueOf(0), Rotation.fromAbsoluteString("+")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-4), Fraction.valueOf(0), Rotation.fromAbsoluteString("+")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-2), Fraction.valueOf(0), Rotation.fromAbsoluteString("+")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(2), Fraction.valueOf(0), Rotation.fromAbsoluteString("+")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(4), Fraction.valueOf(0), Rotation.fromAbsoluteString("+")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(6), Fraction.valueOf(0), Rotation.fromAbsoluteString("+")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(8), Fraction.valueOf(0), Rotation.fromAbsoluteString("+")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-1), Rotation.fromAbsoluteString("+")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-3), Rotation.fromAbsoluteString("+")), Tag.OUTSIDE_8));

    /** TRIPLE GENERAL LINES formation.
      * @doc.test
      *  js> tf = FormationList.TRIPLE_GENERAL_LINES; tf.toStringDiagram('|');
      *  ||    |    |    |
      *  |
      *  ||    |    |    |
      *  |
      *  ||    |    |    |
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [OUTSIDE_8]
      *  [OUTSIDE_8]
      *  [OUTSIDE_8]
      *  [OUTSIDE_8]
      *  [CENTER]
      *  [CENTER]
      *  [CENTER]
      *  [CENTER]
      *  [OUTSIDE_8]
      *  [OUTSIDE_8]
      *  [OUTSIDE_8]
      *  [OUTSIDE_8]
      */
    public static final NamedTaggedFormation TRIPLE_GENERAL_LINES =
        new NamedTaggedFormation("TRIPLE GENERAL LINES",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(2), Rotation.fromAbsoluteString("|")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(2), Rotation.fromAbsoluteString("|")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(2), Rotation.fromAbsoluteString("|")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(2), Rotation.fromAbsoluteString("|")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(-2), Rotation.fromAbsoluteString("|")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-2), Rotation.fromAbsoluteString("|")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-2), Rotation.fromAbsoluteString("|")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(-2), Rotation.fromAbsoluteString("|")), Tag.OUTSIDE_8));

    /** TRIPLE GENERAL DIAMONDS formation.
      * @doc.test
      *  js> tf = FormationList.TRIPLE_GENERAL_DIAMONDS; tf.toStringDiagram('|');
      *  |  -         -         -
      *  |
      *  ||    |    |    |    |    |
      *  |
      *  |  -         -         -
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [OUTSIDE_8]
      *  [CENTER]
      *  [OUTSIDE_8]
      *  [OUTSIDE_8]
      *  [OUTSIDE_8]
      *  [CENTER]
      *  [CENTER]
      *  [OUTSIDE_8]
      *  [OUTSIDE_8]
      *  [OUTSIDE_8]
      *  [CENTER]
      *  [OUTSIDE_8]
      */
    public static final NamedTaggedFormation TRIPLE_GENERAL_DIAMONDS =
        new NamedTaggedFormation("TRIPLE GENERAL DIAMONDS",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-4), Fraction.valueOf(2), Rotation.fromAbsoluteString("-")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(2), Rotation.fromAbsoluteString("-")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(4), Fraction.valueOf(2), Rotation.fromAbsoluteString("-")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-5), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(5), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-4), Fraction.valueOf(-2), Rotation.fromAbsoluteString("-")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-2), Rotation.fromAbsoluteString("-")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(4), Fraction.valueOf(-2), Rotation.fromAbsoluteString("-")), Tag.OUTSIDE_8));

    /** TRIPLE GENERAL TALL DIAMONDS formation.
      * @doc.test
      *  js> tf = FormationList.TRIPLE_GENERAL_TALL_DIAMONDS; tf.toStringDiagram('|');
      *  |  -         -         -
      *  |
      *  |
      *  ||    |    |    |    |    |
      *  |
      *  |
      *  |  -         -         -
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [OUTSIDE_8]
      *  [CENTER]
      *  [OUTSIDE_8]
      *  [OUTSIDE_8]
      *  [OUTSIDE_8]
      *  [CENTER]
      *  [CENTER]
      *  [OUTSIDE_8]
      *  [OUTSIDE_8]
      *  [OUTSIDE_8]
      *  [CENTER]
      *  [OUTSIDE_8]
      */
    public static final NamedTaggedFormation TRIPLE_GENERAL_TALL_DIAMONDS =
        new NamedTaggedFormation("TRIPLE GENERAL TALL DIAMONDS",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-4), Fraction.valueOf(3), Rotation.fromAbsoluteString("-")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(3), Rotation.fromAbsoluteString("-")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(4), Fraction.valueOf(3), Rotation.fromAbsoluteString("-")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-5), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(5), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-4), Fraction.valueOf(-3), Rotation.fromAbsoluteString("-")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-3), Rotation.fromAbsoluteString("-")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(4), Fraction.valueOf(-3), Rotation.fromAbsoluteString("-")), Tag.OUTSIDE_8));

    /** TRIPLE GENERAL ASYM DIAMONDS formation.
      * @doc.test
      *  js> tf = FormationList.TRIPLE_GENERAL_ASYM_DIAMONDS; tf.toStringDiagram('|');
      *  |     -      -       -
      *  |
      *  ||    |    |    |    |    |
      *  |
      *  |     -      -       -
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [OUTSIDE_8]
      *  [CENTER]
      *  [OUTSIDE_8]
      *  [OUTSIDE_8]
      *  [OUTSIDE_8]
      *  [CENTER]
      *  [CENTER]
      *  [OUTSIDE_8]
      *  [OUTSIDE_8]
      *  [OUTSIDE_8]
      *  [CENTER]
      *  [OUTSIDE_8]
      */
    public static final NamedTaggedFormation TRIPLE_GENERAL_ASYM_DIAMONDS =
        new NamedTaggedFormation("TRIPLE GENERAL ASYM DIAMONDS",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(2), Rotation.fromAbsoluteString("-")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(2), Rotation.fromAbsoluteString("-")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(2), Rotation.fromAbsoluteString("-")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-5), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(5), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(-2), Rotation.fromAbsoluteString("-")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-2), Rotation.fromAbsoluteString("-")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(-2), Rotation.fromAbsoluteString("-")), Tag.OUTSIDE_8));

    /** TRIPLE POINT-TO-POINT GENERAL DIAMONDS formation.
      * @doc.test
      *  js> tf = FormationList.TRIPLE_POINT_TO_POINT_GENERAL_DIAMONDS; tf.toStringDiagram('|');
      *  |  -
      *  |
      *  ||    |
      *  |
      *  |  -
      *  |
      *  |  -
      *  |
      *  ||    |
      *  |
      *  |  -
      *  |
      *  |  -
      *  |
      *  ||    |
      *  |
      *  |  -
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [OUTSIDE_8]
      *  [OUTSIDE_8]
      *  [OUTSIDE_8]
      *  [OUTSIDE_8]
      *  [CENTER]
      *  [CENTER]
      *  [CENTER]
      *  [CENTER]
      *  [OUTSIDE_8]
      *  [OUTSIDE_8]
      *  [OUTSIDE_8]
      *  [OUTSIDE_8]
      */
    public static final NamedTaggedFormation TRIPLE_POINT_TO_POINT_GENERAL_DIAMONDS =
        new NamedTaggedFormation("TRIPLE POINT-TO-POINT GENERAL DIAMONDS",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(8), Rotation.fromAbsoluteString("-")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(6), Rotation.fromAbsoluteString("|")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(6), Rotation.fromAbsoluteString("|")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(4), Rotation.fromAbsoluteString("-")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(2), Rotation.fromAbsoluteString("-")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-2), Rotation.fromAbsoluteString("-")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-4), Rotation.fromAbsoluteString("-")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-6), Rotation.fromAbsoluteString("|")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-6), Rotation.fromAbsoluteString("|")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-8), Rotation.fromAbsoluteString("-")), Tag.OUTSIDE_8));

    /** TRIPLE POINT-TO-POINT GENERAL TALL DIAMONDS formation.
      * @doc.test
      *  js> tf = FormationList.TRIPLE_POINT_TO_POINT_GENERAL_TALL_DIAMONDS; tf.toStringDiagram('|');
      *  |  -
      *  |
      *  |
      *  ||    |
      *  |
      *  |
      *  |  -
      *  |
      *  |  -
      *  |
      *  |
      *  ||    |
      *  |
      *  |
      *  |  -
      *  |
      *  |  -
      *  |
      *  |
      *  ||    |
      *  |
      *  |
      *  |  -
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [OUTSIDE_8]
      *  [OUTSIDE_8]
      *  [OUTSIDE_8]
      *  [OUTSIDE_8]
      *  [CENTER]
      *  [CENTER]
      *  [CENTER]
      *  [CENTER]
      *  [OUTSIDE_8]
      *  [OUTSIDE_8]
      *  [OUTSIDE_8]
      *  [OUTSIDE_8]
      */
    public static final NamedTaggedFormation TRIPLE_POINT_TO_POINT_GENERAL_TALL_DIAMONDS =
        new NamedTaggedFormation("TRIPLE POINT-TO-POINT GENERAL TALL DIAMONDS",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(11), Rotation.fromAbsoluteString("-")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(8), Rotation.fromAbsoluteString("|")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(8), Rotation.fromAbsoluteString("|")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(5), Rotation.fromAbsoluteString("-")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(3), Rotation.fromAbsoluteString("-")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(0), Rotation.fromAbsoluteString("|")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-3), Rotation.fromAbsoluteString("-")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-5), Rotation.fromAbsoluteString("-")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-8), Rotation.fromAbsoluteString("|")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-8), Rotation.fromAbsoluteString("|")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(0), Fraction.valueOf(-11), Rotation.fromAbsoluteString("-")), Tag.OUTSIDE_8));

    /** 4x4 formation.
      * @doc.test
      *  js> tf = FormationList._4x4; tf.toStringDiagram('|');
      *  |+    +    +    +
      *  |
      *  |+    +    +    +
      *  |
      *  |+    +    +    +
      *  |
      *  |+    +    +    +
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  []
      *  []
      *  []
      *  []
      *  []
      *  []
      *  []
      *  []
      *  []
      *  []
      *  []
      *  []
      *  []
      *  []
      *  []
      *  []
      */
    public static final NamedTaggedFormation _4x4 =
        new NamedTaggedFormation("4x4",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(3), Rotation.fromAbsoluteString("+"))),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(3), Rotation.fromAbsoluteString("+"))),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(3), Rotation.fromAbsoluteString("+"))),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(3), Rotation.fromAbsoluteString("+"))),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(1), Rotation.fromAbsoluteString("+"))),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(1), Rotation.fromAbsoluteString("+"))),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(1), Rotation.fromAbsoluteString("+"))),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(1), Rotation.fromAbsoluteString("+"))),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(-1), Rotation.fromAbsoluteString("+"))),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-1), Rotation.fromAbsoluteString("+"))),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-1), Rotation.fromAbsoluteString("+"))),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(-1), Rotation.fromAbsoluteString("+"))),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(-3), Rotation.fromAbsoluteString("+"))),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-3), Rotation.fromAbsoluteString("+"))),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-3), Rotation.fromAbsoluteString("+"))),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(-3), Rotation.fromAbsoluteString("+"))));

    /** QUADRUPLE GENERAL LINES formation.
      * @doc.test
      *  js> tf = FormationList.QUADRUPLE_GENERAL_LINES; tf.toStringDiagram('|');
      *  ||    |    |    |
      *  |
      *  ||    |    |    |
      *  |
      *  ||    |    |    |
      *  |
      *  ||    |    |    |
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [OUTSIDE_8]
      *  [OUTSIDE_8]
      *  [OUTSIDE_8]
      *  [OUTSIDE_8]
      *  [CENTER]
      *  [CENTER]
      *  [CENTER]
      *  [CENTER]
      *  [CENTER]
      *  [CENTER]
      *  [CENTER]
      *  [CENTER]
      *  [OUTSIDE_8]
      *  [OUTSIDE_8]
      *  [OUTSIDE_8]
      *  [OUTSIDE_8]
      */
    public static final NamedTaggedFormation QUADRUPLE_GENERAL_LINES =
        new NamedTaggedFormation("QUADRUPLE GENERAL LINES",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(3), Rotation.fromAbsoluteString("|")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(3), Rotation.fromAbsoluteString("|")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(3), Rotation.fromAbsoluteString("|")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(3), Rotation.fromAbsoluteString("|")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(1), Rotation.fromAbsoluteString("|")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(1), Rotation.fromAbsoluteString("|")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(1), Rotation.fromAbsoluteString("|")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(1), Rotation.fromAbsoluteString("|")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(-1), Rotation.fromAbsoluteString("|")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-1), Rotation.fromAbsoluteString("|")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-1), Rotation.fromAbsoluteString("|")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(-1), Rotation.fromAbsoluteString("|")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(-3), Rotation.fromAbsoluteString("|")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-3), Rotation.fromAbsoluteString("|")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-3), Rotation.fromAbsoluteString("|")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(-3), Rotation.fromAbsoluteString("|")), Tag.OUTSIDE_8));

    /** QUADRUPLE GENERAL COLUMNS formation.
      * @doc.test
      *  js> tf = FormationList.QUADRUPLE_GENERAL_COLUMNS; tf.toStringDiagram('|');
      *  ||    |    |    |
      *  |
      *  ||    |    |    |
      *  |
      *  ||    |    |    |
      *  |
      *  ||    |    |    |
      *  js> [tf.tags(dd) for each (dd in Iterator(tf.sortedDancers())) ].join('\n');
      *  [OUTSIDE_8]
      *  [CENTER]
      *  [CENTER]
      *  [OUTSIDE_8]
      *  [OUTSIDE_8]
      *  [CENTER]
      *  [CENTER]
      *  [OUTSIDE_8]
      *  [OUTSIDE_8]
      *  [CENTER]
      *  [CENTER]
      *  [OUTSIDE_8]
      *  [OUTSIDE_8]
      *  [CENTER]
      *  [CENTER]
      *  [OUTSIDE_8]
      */
    public static final NamedTaggedFormation QUADRUPLE_GENERAL_COLUMNS =
        new NamedTaggedFormation("QUADRUPLE GENERAL COLUMNS",
            null,
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(3), Rotation.fromAbsoluteString("|")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(3), Rotation.fromAbsoluteString("|")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(3), Rotation.fromAbsoluteString("|")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(3), Rotation.fromAbsoluteString("|")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(1), Rotation.fromAbsoluteString("|")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(1), Rotation.fromAbsoluteString("|")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(1), Rotation.fromAbsoluteString("|")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(1), Rotation.fromAbsoluteString("|")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(-1), Rotation.fromAbsoluteString("|")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-1), Rotation.fromAbsoluteString("|")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-1), Rotation.fromAbsoluteString("|")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(-1), Rotation.fromAbsoluteString("|")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-3), Fraction.valueOf(-3), Rotation.fromAbsoluteString("|")), Tag.OUTSIDE_8),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(-1), Fraction.valueOf(-3), Rotation.fromAbsoluteString("|")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(1), Fraction.valueOf(-3), Rotation.fromAbsoluteString("|")), Tag.CENTER),
            new TaggedDancerInfo(new PhantomDancer(), new Position(Fraction.valueOf(3), Fraction.valueOf(-3), Rotation.fromAbsoluteString("|")), Tag.OUTSIDE_8));

    /** List of all formations defined here. */
    public static final List<NamedTaggedFormation> all =
        Collections.unmodifiableList(Arrays.asList(
            FormationListFast.SINGLE_DANCER,
            FormationListFast.GENERAL_PARTNERS,
            FormationListFast.GENERAL_TANDEM,
            FormationListFast._1x2,
            FormationListFast.COUPLE,
            FormationListFast.COUPLE_NO_TAGS,
            FormationListFast.FACING_DANCERS,
            FormationListFast.BACK_TO_BACK_DANCERS,
            FormationListFast.TANDEM,
            FormationListFast.RH_MINIWAVE,
            FormationListFast.LH_MINIWAVE,
            FormationListFast._1x3,
            FormationListFast.GENERAL_LINE_OF_3,
            FormationListFast.GENERAL_LINE,
            FormationListFast.GENERAL_COLUMN,
            FormationListFast._1x4,
            FormationListFast._2x2,
            FormationListFast.SINGLE_STATIC_SQUARE,
            FormationListFast.FACING_COUPLES,
            FormationListFast.BACK_TO_BACK_COUPLES,
            FormationListFast.TANDEM_COUPLES,
            FormationListFast.RH_OCEAN_WAVE,
            FormationListFast.LH_OCEAN_WAVE,
            FormationListFast.RH_BOX,
            FormationListFast.LH_BOX,
            FormationListFast.INVERTED_BOX,
            FormationListFast.RH_IN_PINWHEEL,
            FormationListFast.LH_IN_PINWHEEL,
            FormationListFast.RH_OUT_PINWHEEL,
            FormationListFast.LH_OUT_PINWHEEL,
            FormationListFast.RH_SINGLE_QUARTER_ZEE,
            FormationListFast.LH_SINGLE_QUARTER_ZEE,
            FormationListFast.RH_SINGLE_THREE_QUARTER_ZEE,
            FormationListFast.LH_SINGLE_THREE_QUARTER_ZEE,
            FormationListFast.ONE_FACED_LINE,
            FormationListFast.RH_TWO_FACED_LINE,
            FormationListFast.LH_TWO_FACED_LINE,
            FormationListFast.SINGLE_INVERTED_LINE,
            FormationListFast.RH_THREE_AND_ONE_LINE,
            FormationListFast.LH_THREE_AND_ONE_LINE,
            FormationListFast.GENERAL_DIAMOND,
            FormationListFast.GENERAL_TALL_DIAMOND,
            FormationListFast.GENERAL_ASYM_DIAMOND,
            FormationListFast.RH_DIAMOND,
            FormationListFast.RH_FACING_DIAMOND,
            FormationListFast.LH_DIAMOND,
            FormationListFast.LH_FACING_DIAMOND,
            FormationListFast.RH_STAR,
            FormationListFast.LH_STAR,
            FormationListFast.RH_SINGLE_PROMENADE,
            FormationListFast.LH_SINGLE_PROMENADE,
            FormationListFast.GENERAL_SINGLE_QUARTER_TAG,
            FormationListFast.GENERAL_ASYM_SINGLE_QUARTER_TAG,
            FormationListFast.RH_SINGLE_QUARTER_TAG,
            FormationListFast.LH_SINGLE_QUARTER_TAG,
            FormationListFast.RH_SINGLE_THREE_QUARTER_TAG,
            FormationListFast.LH_SINGLE_THREE_QUARTER_TAG,
            FormationListFast.SINGLE_DOUBLE_PASS_THRU,
            FormationListFast.COMPLETED_SINGLE_DOUBLE_PASS_THRU,
            FormationListFast._1x8,
            FormationListFast._2x4,
            FormationListFast.PARALLEL_GENERAL_LINES,
            FormationListFast.GENERAL_COLUMNS,
            FormationListFast.STATIC_SQUARE,
            FormationListFast.STATIC_SQUARE_FACING_OUT,
            FormationListFast.SINGLE_FILE_PROMENADE,
            FormationListFast.REVERSE_SINGLE_FILE_PROMENADE,
            FormationListFast.RH_ALAMO_RING,
            FormationListFast.LH_ALAMO_RING,
            FormationListFast.O_SPOTS,
            FormationListFast.PROMENADE,
            FormationListFast.WRONG_WAY_PROMENADE,
            FormationListFast.STAR_PROMENADE,
            FormationListFast.WRONG_WAY_STAR_PROMENADE,
            FormationListFast.THAR,
            FormationListFast.WRONG_WAY_THAR,
            FormationListFast.RIGHT_AND_LEFT_GRAND,
            FormationListFast.RIGHT_AND_LEFT_GRAND_DIAMOND,
            FormationListFast.LEFT_AND_RIGHT_GRAND,
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
            FormationListFast.TRANS_RH_COLUMN,
            FormationListFast.TRANS_LH_COLUMN,
            FormationListFast.ENDS_IN_INVERTED_LINES,
            FormationListFast.ENDS_OUT_INVERTED_LINES,
            FormationListFast.GENERAL_QUARTER_TAG,
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
            FormationListFast.TWIN_GENERAL_DIAMONDS,
            FormationListFast.POINT_TO_POINT_GENERAL_DIAMONDS,
            FormationListFast.CONCENTRIC_GENERAL_DIAMONDS,
            FormationListFast.RH_HOURGLASS,
            FormationListFast.LH_HOURGLASS,
            FormationListFast.GENERAL_HOURGLASS,
            FormationListFast.RH_TIDAL_WAVE,
            FormationListFast.LH_TIDAL_WAVE,
            FormationListFast.RH_TIDAL_TWO_FACED_LINE,
            FormationListFast.LH_TIDAL_TWO_FACED_LINE,
            FormationListFast.RH_TIDAL_LINE,
            FormationListFast.LH_TIDAL_LINE,
            FormationListFast.GENERAL_TIDAL_LINE,
            FormationListFast.RH_GALAXY,
            FormationListFast.LH_GALAXY,
            FormationListFast.GENERAL_GALAXY,
            FormationListFast.GENERAL_SPINDLE,
            FormationListFast.GENERAL_1x3_DIAMOND,
            FormationListFast.O_DOUBLE_PASS_THRU,
            FormationListFast.GENERAL_O,
            FormationListFast.BUTTERFLY_DOUBLE_PASS_THRU,
            FormationListFast.GENERAL_BUTTERFLY,
            FormationListFast._1x12,
            FormationListFast._2x6,
            FormationListFast._3x4,
            FormationListFast.TRIPLE_GENERAL_H,
            FormationListFast.TRIPLE_GENERAL_PLUS,
            FormationListFast.TRIPLE_GENERAL_LINES,
            FormationListFast.TRIPLE_GENERAL_DIAMONDS,
            FormationListFast.TRIPLE_GENERAL_TALL_DIAMONDS,
            FormationListFast.TRIPLE_GENERAL_ASYM_DIAMONDS,
            FormationListFast.TRIPLE_POINT_TO_POINT_GENERAL_DIAMONDS,
            FormationListFast.TRIPLE_POINT_TO_POINT_GENERAL_TALL_DIAMONDS,
            FormationListFast._4x4,
            FormationListFast.QUADRUPLE_GENERAL_LINES,
            FormationListFast.QUADRUPLE_GENERAL_COLUMNS));
}
