package net.cscott.sdr.calls;

import java.util.*;
/** 
 * A successful attempt to match one or more instances of a given formation
 * against the current setup yields a <code>FormationMatch</code>
 * objects.  The <code>FormationMatch</code> contains a list of
 * <code>TaggedFormation</code>; for example, matching a diamond against a
 * twin diamond setup will result in two <code>TaggedFormation</code>s,
 * one for each diamond.  A <code>FormationMatch</code> may also contain
 * a <code>Warp</code>, which specifies a transformation on the paths 
 * resulting from executing a call in the given <code>TaggedFormation</code>s.
 * For example, matching stretched boxes will result in
 * <code>TaggedFormations</code> of undistorted boxes, and a <code>Warp</code>
 * which will initially stretch the boxes.
 */
public class FormationMatch {
    /** The meta formation contains one dancer for each matched
     * sub-formation in the setup.  Unmatched dancers (if allowed by the match)
     * result in phantom dancers in the meta formation mapped to the
     * {@link FormationList#SINGLE_DANCER} sub-formation.
     */
    public final Formation meta; // meta formation
    /** The {@link FormationMatch#matches} field is a map from phantom dancers
     * in the meta formation to real dancers in a tagged subformation.  The
     * meta formation says how these subformations could be rotated, shifted,
     * and recombined into the original setup.
     */
    public final Map<Dancer,TaggedFormation> matches;

    public FormationMatch(Formation meta, Map<Dancer,TaggedFormation> matches) {
        this.meta = meta;
        this.matches = Collections.unmodifiableMap(matches);
    }
}
