package net.cscott.sdr.calls;

import java.util.EnumSet;
import java.util.Set;

import net.cscott.sdr.calls.TaggedFormation.Tag;

/** An object representing a phantom dancer. */
public class PhantomDancer implements Dancer {
    private static int counter = 0;
    private final int id;
    private final EnumSet<Tag> primitiveTags;
    /** You can create as many phantom dancers as you need. */
    public PhantomDancer() { this(NO_TAGS); }
    /** Sometimes you want to create phantoms which match specific primitive
     *  dancer tags. */
    public PhantomDancer(Set<Tag> tags) {
        this.id = counter++;
        this.primitiveTags = tags.isEmpty() ? NO_TAGS : EnumSet.copyOf(tags);
    }
    /** Phantoms are (usually) not heads. */
    public boolean isHead() { return primitiveTags.contains(Tag.HEAD); }
    /** Phantoms are (usually) not sides. */
    public boolean isSide() { return primitiveTags.contains(Tag.SIDE); }
    /** Phantoms are (usually) not boys. */
    public boolean isBoy() { return primitiveTags.contains(Tag.BOY); }
    /** Phantoms are (usually) not girls. */
    public boolean isGirl() { return primitiveTags.contains(Tag.GIRL); }
    /** Phantoms aren't couple 1, dancer 2, boy/girl etc. */
    public boolean matchesTag(Tag tag) {
        return tag==Tag.ALL || primitiveTags.contains(tag);
    }
    /** Phantoms can't be primitively-selected. */
    public Tag primitiveTag() { return null; }

    /** Human-readable representation. */
    public String toString() { return "<phantom@"+Integer.toHexString(hashCode())+">"; }
    /** Repeatable hashcode: return the id field of this phantom, which
     * is incremented by one for each dancer. */
    @Override
    public int hashCode() { return 123+id; }
    @Override
    public boolean equals(Object o) { return this==o; }
    /** Shared empty set of tags. */
    private final static EnumSet<Tag> NO_TAGS = EnumSet.noneOf(Tag.class);
}
