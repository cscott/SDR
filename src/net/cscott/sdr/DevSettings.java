package net.cscott.sdr;

/**
 * This class defines some simple constants to allow us to turn off
 * or enable certain features during development.
 *
 * @author C. Scott Ananian
 */
public abstract class DevSettings {
    private DevSettings() { /* don't allow constructor */ }

    /** Speed up compilation by only building the C4 grammar, not all
     *  the separate grammars for each level. */
    public static final boolean ONLY_C4_GRAMMAR = false;
}
