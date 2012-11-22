package net.cscott.sdr.calls;

import org.junit.runner.RunWith;

import net.cscott.jdoctest.JDoctestRunner;
import net.cscott.sdr.calls.TaggedFormation.Tag;

/**
 * An object representing one of the eight real dancers.
 * @doc.test Properties of DANCER_1 (a head boy):
 *  js> const Tag = TaggedFormation.Tag;
 *  js> d = StandardDancer.COUPLE_1_BOY;
 *  COUPLE 1 BOY
 *  js> d.coupleNumber()
 *  1
 *  js> d.isHead()
 *  true
 *  js> d.isSide()
 *  false
 *  js> d.isBoy()
 *  true
 *  js> d.isGirl()
 *  false
 *  js> d.ordinal()
 *  0
 *  js> d.primitiveTag()
 *  DANCER_1
 *  js> d.matchesTag(d.primitiveTag())
 *  true
 *  js> d.matchesTag(Tag.BOY)
 *  true
 *  js> d.matchesTag(Tag.GIRL)
 *  false
 *  js> d.matchesTag(Tag.HEAD)
 *  true
 *  js> d.matchesTag(Tag.SIDE)
 *  false
 *  js> [d.matchesTag(t) for each (t in
 *    >  [Tag.COUPLE_1, Tag.COUPLE_2, Tag.COUPLE_3, Tag.COUPLE_4])]
 *  true,false,false,false
 *  js> [d.matchesTag(t) for each (t in
 *    >  [Tag.DANCER_1, Tag.DANCER_2, Tag.DANCER_3, Tag.DANCER_4,
 *    >   Tag.DANCER_5, Tag.DANCER_6, Tag.DANCER_7, Tag.DANCER_8])]
 *  true,false,false,false,false,false,false,false
 *  js> d.matchesTag(Tag.NONE)
 *  false
 *  js> d.matchesTag(Tag.ALL)
 *  true
 * @doc.test Properties of DANCER_8 (a side girl):
 *  js> const Tag = TaggedFormation.Tag;
 *  js> d = StandardDancer.COUPLE_4_GIRL;
 *  COUPLE 4 GIRL
 *  js> d.coupleNumber()
 *  4
 *  js> d.isHead()
 *  false
 *  js> d.isSide()
 *  true
 *  js> d.isBoy()
 *  false
 *  js> d.isGirl()
 *  true
 *  js> d.ordinal()
 *  7
 *  js> d.primitiveTag()
 *  DANCER_8
 *  js> d.matchesTag(d.primitiveTag())
 *  true
 *  js> d.matchesTag(Tag.BOY)
 *  false
 *  js> d.matchesTag(Tag.GIRL)
 *  true
 *  js> d.matchesTag(Tag.HEAD)
 *  false
 *  js> d.matchesTag(Tag.SIDE)
 *  true
 *  js> [d.matchesTag(t) for each (t in
 *    >  [Tag.COUPLE_1, Tag.COUPLE_2, Tag.COUPLE_3, Tag.COUPLE_4])]
 *  false,false,false,true
 *  js> [d.matchesTag(t) for each (t in
 *    >  [Tag.DANCER_1, Tag.DANCER_2, Tag.DANCER_3, Tag.DANCER_4,
 *    >   Tag.DANCER_5, Tag.DANCER_6, Tag.DANCER_7, Tag.DANCER_8])]
 *  false,false,false,false,false,false,false,true
 *  js> d.matchesTag(Tag.NONE)
 *  false
 *  js> d.matchesTag(Tag.ALL)
 *  true
 */
@RunWith(value=JDoctestRunner.class)
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
        case DANCER_1: return ordinal()==0;
        case DANCER_2: return ordinal()==1;
        case DANCER_3: return ordinal()==2;
        case DANCER_4: return ordinal()==3;
        case DANCER_5: return ordinal()==4;
        case DANCER_6: return ordinal()==5;
        case DANCER_7: return ordinal()==6;
        case DANCER_8: return ordinal()==7;
        case NONE: return false; // none matches no one
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
