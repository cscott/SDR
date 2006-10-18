package net.cscott.sdr.calls;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
    private final Map<Dancer,Set<Tag>> tags;
    protected TaggedFormation(Map<Dancer,Position> location,
            Set<Dancer> selected, Map<Dancer,Set<Tag>> tags) {
        super(location,selected);
        this.tags = tags;
    }

    public boolean isTagged(Dancer d, Tag tag) {
        if (tag.isPrimitive()) return d.matchesTag(tag);
        return tags.get(d).contains(tag);
    }
    public Set<Dancer> tagged(Tag tag) {
        Set<Dancer> dancers = dancers();
        Set<Dancer> s = new HashSet<Dancer>(dancers.size());
        for(Dancer d : dancers)
            if (isTagged(d, tag))
                s.add(d);
        return s;
    }
    @Override
    public TaggedFormation select(Set<Dancer> s) {
        Set<Dancer> nSel = new HashSet<Dancer>(s);
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
    public int hashCode() {
	return new HashCodeBuilder()
            .appendSuper(super.hashCode()).append(tags)
	    .toHashCode();
    }
    public String toString() {
	return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
	    .appendSuper(super.toString())
	    .append("tags", tags)
            .toString();
    }

    public TaggedFormation(TaggedFormation tf, Map<Dancer,Dancer> map) {
        super(tf, map);
        Map<Dancer,Set<Tag>> t = new HashMap<Dancer,Set<Tag>>();
        for (Map.Entry<Dancer,Set<Tag>> me : tf.tags.entrySet())
            t.put(map.get(me.getKey()), me.getValue());
        this.tags = Collections.unmodifiableMap(t);
    }
    
    TaggedFormation(TaggedDancerInfo... dis) {
        super((Formation.DancerInfo[])dis);
        Map<Dancer,Set<Tag>> t = new HashMap<Dancer,Set<Tag>>();
	for (TaggedDancerInfo di : dis) {
	    t.put(di.dancer, Collections.unmodifiableSet(di.tags));
	}
        this.tags = Collections.unmodifiableMap(t);
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
