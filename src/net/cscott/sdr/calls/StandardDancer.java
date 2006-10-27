package net.cscott.sdr.calls;

import net.cscott.sdr.calls.TaggedFormation.Tag;

/** An object representing one of the eight real dancers.
 */
public enum StandardDancer implements Dancer {
    COUPLE_1_BOY, COUPLE_1_GIRL, COUPLE_2_BOY, COUPLE_2_GIRL,
	COUPLE_3_BOY, COUPLE_3_GIRL, COUPLE_4_BOY, COUPLE_4_GIRL;
    
    /** Returns a couple number, from 1-4. */
    public int coupleNumber() { return 1+(ordinal()/2); }
    public boolean isHead() { return 1==(coupleNumber()%2); }
    public boolean isSide() { return 0==(coupleNumber()%2); }
    public boolean isBoy() { return 0==(ordinal()%2); }
    public boolean isGirl() { return 1==(ordinal()%2); }
    public boolean matchesTag(Tag tag) {
        switch (tag) {
        case BOY: return isBoy();
        case GIRL: return isGirl();
        case HEAD: return isHead();
        case SIDE: return isSide();
        case COUPLE_1: return coupleNumber()==1;
        case COUPLE_2: return coupleNumber()==2;
        case COUPLE_3: return coupleNumber()==3;
        case COUPLE_4: return coupleNumber()==4;
        case DANCER_1: return ordinal()==1;
        case DANCER_2: return ordinal()==2;
        case DANCER_3: return ordinal()==3;
        case DANCER_4: return ordinal()==4;
        case DANCER_5: return ordinal()==5;
        case DANCER_6: return ordinal()==6;
        case DANCER_7: return ordinal()==7;
        case DANCER_8: return ordinal()==8;
        case ALL: return true; // all matches everyone.
        default:
            assert !tag.isPrimitive();
            return false;
        }
    }
    public Tag primitiveTag() {
        return Tag.values()[ordinal()+Tag.DANCER_1.ordinal()];
    }
    @Override
    public String toString() { return this.name().replace('_',' '); }
}
