package net.cscott.sdr.calls;


/** A {@link Matcher} takes a formation and pulls out all instances of
 *  a sub-formation.  For example, given facing lines, the
 *  FACING_COUPLES matcher would extract the two instances of facing
 *  couples.  Each instance of a facing couple could then be
 *  decomposed into two FACING_DANCERS.  Subformations are in standard
 *  orientations (see {@link FormationList}) and the dancers can be
 *  numbered in a standard left-to-right top-to-bottom order using
 *  {@link Formation#sortedDancers()}.
 */
public abstract class Matcher {
    /** Match sub-formations from a formation using this.  (The
     *  subformation may be as large as the original formation -- or even
     *  larger, if phantoms are generated; it may also be as small as a
     *  single dancer.)  If the given formation can't be matched from
     *  the current dancer configuration, throws NoMatchException. */
    public abstract FormationMatch match(Formation f)
        throws NoMatchException;

    public static Matcher valueOf(String s) {
        // Look for this matcher in the MatcherList
        try {
            // convert to uppercase, prepend underscore if numeric,
            // replace spaces and dashes with underscores.
            s = s.toUpperCase().replaceFirst("^(?=\\d)", "_")
                .replace(' ','_').replace('-','_');
            return (Matcher) MatcherList.class.getField(s).get(null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Bad matcher name: "+s);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Can't access matcher "+s+": "+e);
        }
    }
}
