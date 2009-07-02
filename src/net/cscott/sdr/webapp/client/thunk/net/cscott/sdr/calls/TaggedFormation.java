package net.cscott.sdr.calls;

import java.util.EnumSet;
import java.util.Set;

/** A Formation is a set of dancers and positions for them.
 * Certain dancers in the formation can be selected. 
 * In addition, all dancers can be tagged with modifiers
 * such as 'BEAU', 'BELLE', 'LEADER', 'TRAILER', etc.
 * */
public class TaggedFormation {
    public enum Tag {
        // "primitive" dancer tags
        // the Dancer object (not the Formation) is
        // responsible for identifying these.
        DANCER_1, DANCER_2, DANCER_3, DANCER_4,
        DANCER_5, DANCER_6, DANCER_7, DANCER_8,
        COUPLE_1, COUPLE_2, COUPLE_3, COUPLE_4,
        BOY, GIRL, HEAD, SIDE, ALL,
        // more interesting tags.
        BEAU, BELLE, LEADER, TRAILER,
	POINT, CENTER, VERY_CENTER, END,
	OUTSIDE_4, CENTER_6, OUTSIDE_6,
        NUMBER_1, NUMBER_2, NUMBER_3, NUMBER_4;
        public boolean isPrimitive() {
            return ordinal() <= ALL.ordinal();
        }
    };
    static Set<Tag> mkTags(Tag... tags) {
        if (tags.length==0) return EnumSet.noneOf(Tag.class);
        else return EnumSet.of(tags[0], tags);
    }
}
