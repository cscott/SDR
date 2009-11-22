package net.cscott.sdr.calls;

import java.util.List;

import net.cscott.sdr.calls.ast.Expr;


/** A {@link Matcher} takes a formation and pulls out all instances of
 *  a sub-formation.  For example, given facing lines, the
 *  FACING_COUPLES matcher would extract the two instances of facing
 *  couples.  Each instance of a facing couple could then be
 *  decomposed into two FACING_DANCERS.  Subformations are in standard
 *  orientations (see {@link FormationList}) and the dancers can be
 *  numbered in a standard left-to-right top-to-bottom order using
 *  {@link Formation#sortedDancers()}.
 */
public abstract class Matcher extends ExprFunc<FormationMatch> {
    /** Match sub-formations from a formation.  The
     *  subformation may be as large as the original formation -- or even
     *  larger, if phantoms are generated; it may also be as small as a
     *  single dancer.  If the given formation can't be matched from
     *  the current dancer configuration, throws {@link NoMatchException}. */
    public abstract FormationMatch match(Formation f)
        throws NoMatchException;

    /** Implement the {@link ExprFunc} contract. */
    @Override
    public final FormationMatch evaluate(Class<? super FormationMatch> type,
                                         DanceState ds, List<Expr> args)
            throws net.cscott.sdr.calls.ExprFunc.EvaluationException {
        assert args.size()==0;
        return match(ds.currentFormation());
    }
    @Override
    public String toString() { return getName(); }

    /** @throws IllegalArgumentException if the matcher is unknown. */
    public static Matcher valueOf(String s) {
        // Look for this matcher in the MatcherList
        try {
            // convert to uppercase, prepend underscore if numeric,
            // replace spaces and dashes with underscores.
            s = s.toUpperCase().replaceFirst("^(?=\\d)", "_")
                .replace(' ','_').replace('-','_');
            return (Matcher) MatcherList.class.getField(s).get(null);
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException("Bad matcher name: "+s);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Can't access matcher "+s+": "+e);
        }
    }
}
