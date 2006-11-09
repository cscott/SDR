package net.cscott.sdr.calls;

import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import net.cscott.jutil.Factories;
import net.cscott.jutil.GenericMultiMap;
import net.cscott.jutil.MultiMap;
import net.cscott.jutil.UnmodifiableMultiMap;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/** A Formation is a set of dancers and positions for them.
 * Certain dancers in the formation can be selected. 
 * In addition, all dancers can be tagged with modifiers
 * such as 'BEAU', 'BELLE', 'LEADER', 'TRAILER', etc.
 * */
public class TaggedFormation extends Formation {
    public enum Tag {
        // "primitive" dancer tags
        // the Dancer object (not the Formation) is
        // responsible for identifying these.
        DANCER_1, DANCER_2, DANCER_3, DANCER_4,
        DANCER_5, DANCER_6, DANCER_7, DANCER_8,
        COUPLE_1, COUPLE_2, COUPLE_3, COUPLE_4,
        BOY, GIRL, HEAD, SIDE, ALL,
        // more interesting tags.
        BEAU, BELLE, LEADER, TRAILER,
        POINT, CENTER, VERY_CENTER, END, OUTSIDE4,
        NUMBER_1, NUMBER_2, NUMBER_3, NUMBER_4;
        public boolean isPrimitive() {
            return ordinal() <= ALL.ordinal();
        }
    };
    private final MultiMap<Dancer,Tag> tags;
    protected TaggedFormation(Map<Dancer,Position> location,
            Set<Dancer> selected, MultiMap<Dancer,Tag> tags) {
        super(location,selected);
        this.tags = tags;
    }

    public boolean isTagged(Dancer d, Tag tag) {
        if (tag.isPrimitive()) return d.matchesTag(tag);
        return tags.contains(d,tag);
    }
    public Set<Dancer> tagged(Tag tag) {
        Set<Dancer> dancers = dancers();
        Set<Dancer> s = new LinkedHashSet<Dancer>(dancers.size());
        for(Dancer d : dancers)
            if (isTagged(d, tag))
                s.add(d);
        return s;
    }
    public Set<Tag> tags(Dancer d) {
        EnumSet<Tag> copy = EnumSet.noneOf(Tag.class);
        copy.addAll(tags.getValues(d));
        return copy;
    }
    @Override
    public TaggedFormation select(Set<Dancer> s) {
        Set<Dancer> nSel = new LinkedHashSet<Dancer>(s);
        nSel.retainAll(dancers());
        return new TaggedFormation
        (location, Collections.unmodifiableSet(nSel), tags);
    }
    // utility functions.
    public boolean equals(Object o) {
	if (!(o instanceof TaggedFormation)) return false;
	TaggedFormation f = (TaggedFormation) o;
	return new EqualsBuilder()
            .appendSuper(super.equals(o))
	    .append(tags, f.tags)
            .isEquals();
    }
    private transient int hashCode = 0;
    public int hashCode() {
        if (this.hashCode==0)
            this.hashCode = new HashCodeBuilder()
            .appendSuper(super.hashCode()).append(tags)
            .toHashCode();
        return this.hashCode;
    }
    public String toString() {
	return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
	    .appendSuper(super.toString())
	    .append("tags", tags)
            .toString();
    }

    public TaggedFormation(TaggedFormation tf, Map<Dancer,Dancer> map) {
        super(tf, map);
        MultiMap<Dancer,Tag> t = new GenericMultiMap<Dancer,Tag>
        (Factories.enumSetFactory(Tag.class));
        for (Dancer d : tf.dancers())
            t.addAll(map.get(d), tf.tags.getValues(d));
        this.tags = UnmodifiableMultiMap.proxy(t);
    }
    public TaggedFormation(Formation f, MultiMap<Dancer,Tag> tags) {
        super(f.location,f.selected);
        this.tags = UnmodifiableMultiMap.proxy(tags);//tags can be changed
    }
    
    TaggedFormation(TaggedDancerInfo... dis) {
        super((Formation.DancerInfo[])dis);
        MultiMap<Dancer,Tag> t = new GenericMultiMap<Dancer,Tag>
        (Factories.enumSetFactory(Tag.class));
	for (TaggedDancerInfo di : dis) {
	    t.addAll(di.dancer, di.tags);
	}
        this.tags = UnmodifiableMultiMap.proxy(t);
    }
    static class TaggedDancerInfo extends DancerInfo {
	final Set<Tag> tags;
	TaggedDancerInfo(Dancer d, Position p, Set<Tag> tags, boolean s)
        {
	    super(d, p, s);
	    this.tags = tags;
	}
	TaggedDancerInfo(Dancer d, Position p, Tag... tags) {
            this(d,p,mkTags(tags),true);
        }
    }
    static Set<Tag> mkTags(Tag... tags) {
        if (tags.length==0) return EnumSet.noneOf(Tag.class);
        else return EnumSet.of(tags[0], tags);
    }
}
