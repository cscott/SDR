package net.cscott.sdr.calls;

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
}
