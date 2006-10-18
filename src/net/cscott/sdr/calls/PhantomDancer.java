package net.cscott.sdr.calls;

import net.cscott.sdr.calls.TaggedFormation.Tag;

/** An object representing a phantom dancer. */
public class PhantomDancer implements Dancer {
    /** You can create as many phantom dancers as you need. */
    public PhantomDancer() { }
    /** Phantoms are not heads. */
    public boolean isHead() { return false; }
    /** Phantoms are not sides. */
    public boolean isSide() { return false; }
    /** Phantoms are not boys. */
    public boolean isBoy() { return false; }
    /** Phantoms are not girls. */
    public boolean isGirl() { return false; }
    /** Phantoms aren't couple 1, dancer 2, boy/girl etc. */
    public boolean matchesTag(Tag tag) { return tag==Tag.ALL; }
    /** Phantoms can't be primitively-selected. */
    public Tag primitiveTag() { return null; }

    /** Human-readable representation. */
    public String toString() { return "<phantom@"+Integer.toHexString(hashCode())+">"; }
}
