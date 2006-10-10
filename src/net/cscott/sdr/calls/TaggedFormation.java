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
        BEAU, BELLE, LEADER, TRAILER, POINT, CENTER, VERY_CENTER, END, OUTSIDE4;
    };
    private final Map<Dancer,Set<Tag>> tags;

    public boolean isTagged(Dancer d, Tag tag) {
        return tags.get(d).contains(tag);
    }
    public Set<Dancer> tagged(Tag tag) {
        Set<Dancer> s = new HashSet<Dancer>();
        for(Dancer d : tags.keySet())
            if (tags.get(d).contains(tag))
                s.add(d);
        return s;
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
