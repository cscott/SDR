package net.cscott.sdr.calls;

/** An object representing one of the eight real dancers.
 */
public enum StandardDancer implements Dancer {
    COUPLE_1_BEAU, COUPLE_1_BELLE, COUPLE_2_BEAU, COUPLE_2_BELLE,
	COUPLE_3_BEAU, COUPLE_3_BELLE, COUPLE_4_BEAU, COUPLE_4_BELLE;
    
    /** Returns a couple number, from 1-4. */
    public int coupleNumber() { return 1+(ordinal()/2); }
    public boolean isHead() { return 1==(coupleNumber()%2); }
    public boolean isSide() { return 0==(coupleNumber()%2); }
    public boolean isBoy() { return 0==(ordinal()%2); }
    public boolean isGirl() { return 1==(ordinal()%2); }
}
