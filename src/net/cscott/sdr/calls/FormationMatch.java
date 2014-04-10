package net.cscott.sdr.calls;

import java.util.*;

import net.cscott.sdr.calls.TaggedFormation.Tag;
/** 
 * A successful attempt to match one or more instances of a given formation
 * against the current setup yields a <code>FormationMatch</code>
 * object.  The <code>FormationMatch</code> contains a list of
 * {@link TaggedFormation}; for example, matching a diamond against a
 * twin diamond setup will result in two <code>TaggedFormation</code>s,
 * one for each diamond.
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
    /** Be explicit about unmatched dancers, just in case real matched dancers
     * also map to {@link FormationList#SINGLE_DANCER}.  This is a set of
     * *phantom* dancers in the meta formation, which correspond (via
     * SINGLE DANCER subformations) to unmatched real dancers.
     */
    public final Set<Dancer> unmatched;
    /** The set of new NONCORPORAL dancers added in this match, who should
     *  be removed after calls are done in the matched formation.  Unlike
     *  {@link #unmatched}, these are "real" dancers present in the
     *  {@link TaggedFormation}s in {@link #matches}.
     */
    public final Set<Dancer> inserted;

    public FormationMatch(Formation meta, Map<Dancer,TaggedFormation> matches,
                          Set<Dancer> unmatched, Set<Dancer> inserted) {
        this.meta = meta;
        this.matches = Collections.unmodifiableMap(matches);
        this.unmatched = Collections.unmodifiableSet(unmatched);
        this.inserted = Collections.unmodifiableSet(inserted);
    }
    /** Remap the meta dancers in the given {@link FormationMatch}, returning
     *  a new {@link FormationMatch}. */
    public FormationMatch map(Map<Dancer,Dancer> m) {
        Formation nmeta = this.meta.map(m);
        Map<Dancer,TaggedFormation> nmatches =
            new LinkedHashMap<Dancer,TaggedFormation>();
        Set<Dancer> nunmatched = new LinkedHashSet<Dancer>();
        for (Dancer d : this.meta.dancers()) {
            nmatches.put(m.get(d), this.matches.get(d));
            if (this.unmatched.contains(d))
                nunmatched.add(m.get(d));
        }
        return new FormationMatch(nmeta, nmatches, nunmatched, this.inserted);
    }

    /** Pretty-print a {@link FormationMatch}. */
    public String toString() {
        // sort the phantoms to ensure a consistent string representation
        List<Dancer> phantoms = meta.sortedDancers();
        // now map phantoms to 'AA', 'BB', etc.
        Map<Dancer,String> metaDancerNames =
            new HashMap<Dancer,String>(phantoms.size());
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
            sb.append(':');
            if (unmatched.contains(d))
                sb.append(" (unmatched)");
            sb.append('\n');
            TaggedFormation tf = matches.get(d);
            sb.append(tf.toStringDiagram
                      ("   ", Formation.dancerNames));
            sb.append("\n");
            // include tags for tagged dancers
            // NOTE that this does not include "primitive" dancer tags
            // such as HEAD/SIDE/BOY/GIRL, etc.
            boolean atLeastOne=false;
            for (Dancer dd: tf.sortedDancers()) {
                List<Tag> tags = new ArrayList<Tag>(tf.tags(dd));
                Collections.sort(tags);
                if (tags.isEmpty() && !inserted.contains(dd)) continue;
                sb.append(atLeastOne ? "; ":" [");
                atLeastOne=true;
                String name = Formation.dancerNames.get(dd);
                sb.append(name!=null ? name : "ph");
                if (inserted.contains(dd)) {
                    sb.append(" inserted");
                }
                if (tags.isEmpty()) continue;
                sb.append(": ");
                boolean firstTag = true;
                for (Tag t: tags) {
                    if (firstTag) firstTag=false;
                    else sb.append(",");
                    sb.append(t);
                }
            }
            if (atLeastOne) sb.append("]\n");
        }
        // trim trailing \n
        sb.setLength(sb.length()-1);
        // convert to string, we're done!
        return sb.toString();
    }
    public String toString(String prefix) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String line : toString().split("\n")) {
            if (!first) sb.append("\n");
            sb.append(prefix);
            sb.append(line);
            first = false;
        }
        return sb.toString();
    }
}
