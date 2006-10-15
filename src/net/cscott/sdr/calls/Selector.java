package net.cscott.sdr.calls;


/** A selector takes a formation and pulls out all instances of a selected
 *  sub-formation, numbering the dancers in each in a canonical order.
 *  (We've given names to common numberings.)
 *  For example, given facing lines, the FACING_COUPLES selector would
 *  extract the two instances of facing couples, and number the dancers
 *  in each facing couple 0-3.  Each instance of a facing couple could
 *  then be decomposed into two FACING_DANCERS (numbered 0-1).
 */
public abstract class Selector {
    /** Select sub-formations from a formation using this selector.  (The
     *  subformation may be as large as the original formation -- or even
     *  larger, if phantoms are generated; it may also be as small as a
     *  single dancer.)  If the given formation can't be selected from
     *  the current dancer configuration, throws NoMatchException. */
    public abstract FormationMatch match(Formation f)
        throws NoMatchException;

    public static Selector valueOf(String s) {
        // Look for this selector in the SelectorList
        try {
            return (Selector) SelectorList.class.getField
                (s.toUpperCase().replace(' ','_').replace('-','_')).get(null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Bad selector name: "+s);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Can't access selector "+s+": "+e);
        }
    }
}
