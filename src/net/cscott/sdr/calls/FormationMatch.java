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
    public final List<TaggedFormation> taggedFormations;
    public final Warp warp;

    public FormationMatch() {
        this.warp = Warp.NONE;
        this.taggedFormations = null;
        // XXX TODO
    }
}
