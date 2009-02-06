package net.cscott.sdr.calls;

import java.util.*;
/** 
 * A successful attempt to match one or more instances of a given formation
 * against the current setup yields a <code>FormationMatch</code>
 * objects.  The <code>FormationMatch</code> contains a list of
 * <code>TaggedFormation</code>; for example, matching a diamond against a
 * twin diamond setup will result in two <code>TaggedFormation</code>s,
 * one for each diamond.
 */
// XXX: it appears that the <code>Warp</code> idea was replaced with the
// meta formation, as described in the field's javadoc.  Leaving the below
// javadoc chunk here for now, in case Warp needs to be reintroduced.
/*   A <code>FormationMatch</code> may also contain
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

    /** Pretty-print a {@link FormationMatch}. */
    public String toString() {
	List<Dancer> phantoms = new ArrayList<Dancer>(meta.dancers());
	// sort the phantoms to ensure a consistent string representation
        Collections.sort(phantoms, new Comparator<Dancer>() {
            // in reading order: top-to-bottom, left-to-right
            public int compare(Dancer d1, Dancer d2) {
                Position p1 = meta.location(d1), p2 = meta.location(d2);
                int c = -p1.y.compareTo(p2.y); // largest y first
                if (c!=0) return c;
                c = p1.x.compareTo(p2.x); // smallest x first
                return c;
            }
        });
        // now map phantoms to 'AA', 'BB', etc.
        Map<Dancer,String> metaDancerNames =
            new HashMap<Dancer,String>(meta.dancers().size());
        char tag='A';
        for (Dancer d: phantoms) {
            metaDancerNames.put(d, new String(new char[] { tag, tag }));
            tag += 1; // advance to next letter
        }
        StringBuilder sb = new StringBuilder();
        // first part of result is the meta formation
        sb.append(meta.toStringDiagram("", metaDancerNames));
        sb.append("\n");
        // now one tagged formation per phantom
        for (Dancer d: phantoms) {
            sb.append(metaDancerNames.get(d));
            sb.append(":\n");
            sb.append(matches.get(d).toStringDiagram
                      ("   ", Formation.dancerNames)); // xx tags?
            sb.append("\n");
        }
        // trim trailing \n
        sb.setLength(sb.length()-1);
        // convert to string, we're done!
        return sb.toString();
    }
}
