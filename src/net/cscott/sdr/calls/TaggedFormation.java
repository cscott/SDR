package net.cscott.sdr.calls;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import net.cscott.jdoctest.JDoctestRunner;
import net.cscott.jutil.Factories;
import net.cscott.jutil.GenericMultiMap;
import net.cscott.jutil.MultiMap;
import net.cscott.jutil.UnmodifiableMultiMap;
import net.cscott.sdr.util.SdrToString;
import net.cscott.sdr.util.Tools;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.junit.runner.RunWith;

/** A {@link TaggedFormation} is a set of dancers and positions for them.
 * Certain dancers in the formation can be selected. 
 * In addition, all dancers can be tagged with modifiers
 * such as 'BEAU', 'BELLE', 'LEADER', 'TRAILER', etc.
 * @doc.test Fetch tagged dancers from a TaggedFormation:
 *  js> FormationList = FormationList.js(this); undefined;
 *  js> f = FormationList.STATIC_SQUARE ; f.getClass()
 *  class net.cscott.sdr.calls.NamedTaggedFormation
 *  js> [ f.location(d) for each (d in Iterator(f.tagged(TaggedFormation.Tag.BEAU)))]
 *  1,3,s,-3,1,e,3,-1,w,-1,-3,n
 */
@RunWith(value=JDoctestRunner.class)
public class TaggedFormation extends Formation {
    /** Dancer descriptions based on position in the {@link Formation}. */
    public enum Tag {
        // "primitive" dancer tags
        // the Dancer object (not the Formation) is
        // responsible for identifying these.
        DANCER_1, DANCER_2, DANCER_3, DANCER_4,
        DANCER_5, DANCER_6, DANCER_7, DANCER_8,
        COUPLE_1, COUPLE_2, COUPLE_3, COUPLE_4,
        BOY, GIRL, HEAD, SIDE, NONE, ALL,
        // this is a tag kept by the DanceState; the
        // MetaEvaluator ensures that it is applied
        // appropriately after matching
        DESIGNATED,
        // this is a tag added by _use phantoms Matchers
        // it helps use distinguish phantoms from real dancers
        NONCORPOREAL,
        // more interesting tags.
        BEAU, BELLE, LEADER, TRAILER,
	POINT, CENTER, VERY_CENTER, END,
        OUTSIDE_2, OUTSIDE_4, CENTER_6, OUTSIDE_6,
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

    /** Returns true if the dancer is tagged by the given tag.  The tag can be
     *  either a primitive dancer tag, or a formation-dependent tag.
     */
    public boolean isTagged(Dancer d, Tag tag) {
        if (tag.isPrimitive()) return d.matchesTag(tag);
        return tags.contains(d,tag);
    }
    /** Return true if the dancer is tagged by any tag in the given
     *  set of tags */
    public boolean isTagged(Dancer d, Set<Tag> tags) {
        for (Tag t: tags)
            if (isTagged(d, t)) return true;
        return false;
    }
    /** Return the set of dancers tagged with the given tag.  The tag can be
     *  either a primitive dancer tag or a formation-dependent tag.
     */
    public Set<Dancer> tagged(Tag tag) {
        return tagged(Collections.singleton(tag));
    }
    /** Return the set of dancers tagged by any of the given set of tags. */
    public Set<Dancer> tagged(Set<Tag> tags) {
        Set<Dancer> dancers = dancers();
        Set<Dancer> s = new LinkedHashSet<Dancer>(dancers.size());
        for(Dancer d : dancers)
            if (isTagged(d, tags))
                s.add(d);
        return s;
    }
    /**
     * Return the <b>non-primitive</b> tags attached to this dancer.
     *
     * WARNING: because this does not include primitive tags, you should use
     * this method only to transfer tag information between
     * {@link TaggedFormation}s, <i>not</i> to test whether a given dancer has
     * a particular tag. Use
     * {@link #isTagged(Dancer, net.cscott.sdr.calls.TaggedFormation.Tag)} if
     * you want to check whether a dancer has a particular tag.
     */
    public Set<Tag> tags(Dancer d) {
        EnumSet<Tag> copy = EnumSet.noneOf(Tag.class);
        copy.addAll(tags.getValues(d));
        return copy;
    }
    @Override
    public TaggedFormation select(Collection<Dancer> d) {
        Set<Dancer> nSel = new LinkedHashSet<Dancer>(dancers());
        nSel.retainAll(d);
        return new TaggedFormation
        (location, Collections.unmodifiableSet(nSel), tags);
    }
    @Override
    public TaggedFormation move(Dancer d, Position p) {
	assert this.location.containsKey(d);
	Map<Dancer,Position> nmap = new LinkedHashMap<Dancer,Position>
	    (this.location);
	nmap.put(d, p);
	return new TaggedFormation(nmap, this.selected, this.tags);
    }
    /** Add additional tags to the given formation. */
    public TaggedFormation addTags(MultiMap<Dancer,Tag> newTags) {
        MultiMap<Dancer,Tag> nmap = new GenericMultiMap<Dancer,Tag>
            (Factories.enumSetFactory(Tag.class));
        nmap.addAll(this.tags);
        nmap.addAll(newTags);
        return new TaggedFormation(this.location, this.selected, nmap);
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
	// convert tags map to string in order by dancer
	StringBuilder sb = new StringBuilder("{");
	for (Dancer d: this.sortedDancers()) {
	    Collection<Tag> t = tags.getValues(d);
	    if (t.size()==0) continue;
	    if (sb.length() > 1) sb.append(", ");
	    sb.append(d);
	    sb.append('=');
	    if (t.size()==1)
		sb.append(t.iterator().next());
	    else
		sb.append(tags.getValues(d));
	}
	sb.append("}");
	return new ToStringBuilder(this, SdrToString.STYLE)
	    .appendSuper(super.toString())
	    .append("tags", sb.toString())
            .toString();
    }

    protected TaggedFormation(TaggedFormation tf, Map<Dancer,Dancer> map) {
        super(tf, map);
        MultiMap<Dancer,Tag> t = new GenericMultiMap<Dancer,Tag>
        (Factories.enumSetFactory(Tag.class));
        for (Dancer d : tf.dancers())
            t.addAll(map.get(d), tf.tags.getValues(d));
        this.tags = UnmodifiableMultiMap.proxy(t);
    }
    @Override
    public TaggedFormation map(Map<Dancer,Dancer> map) {
        return new TaggedFormation(this, map);
    }
    /**
     * {@inheritDoc}
     * @doc.test Demonstrate usage of call in scripts:
     *  js> FormationList = FormationList.js(this); undefined;
     *  js> const SD = StandardDancer; const l = net.cscott.sdr.util.Tools.l;
     *  js> f = FormationList.TRADE_BY; f.toStringDiagram();
     *  ^    ^
     *  
     *  v    v
     *  
     *  ^    ^
     *  
     *  v    v
     *  js> // infinitest doesn't like overloaded methods in Rhino...
     *  js> f.map(l(SD.COUPLE_1_BOY, SD.COUPLE_1_GIRL,
     *    >         SD.COUPLE_2_GIRL, SD.COUPLE_2_BOY,
     *    >         SD.COUPLE_4_BOY, SD.COUPLE_4_GIRL,
     *    >         SD.COUPLE_3_GIRL, SD.COUPLE_3_BOY)
     *    >         .toArray(java.lang.reflect.Array.newInstance(Dancer,0))
     *    >      ).toStringDiagram();
     *  1B^  1G^
     *  
     *  2Gv  2Bv
     *  
     *  4B^  4G^
     *  
     *  3Gv  3Bv
     */
    @Override
    public TaggedFormation map(Dancer... dancers) {
        // we know the superclass definition is in terms of this.map,
        // so the result will be a TaggedFormation.
        return (TaggedFormation) super.map(dancers);
    }
    /**
     * {@inheritDoc}
     * @doc.test Demonstrate usage of call in scripts:
     *  js> FormationList = FormationList.js(this); undefined;
     *  js> const SD = StandardDancer;
     *  js> f = FormationList.TRADE_BY; undefined;
     *  js> f.mapStd([SD.COUPLE_1_BOY, SD.COUPLE_1_GIRL,
     *    >           SD.COUPLE_2_GIRL, SD.COUPLE_2_BOY]).toStringDiagram();
     *  1B^  1G^
     *  
     *  2Gv  2Bv
     *  
     *  4B^  4G^
     *  
     *  3Gv  3Bv
     */
    @Override
    public TaggedFormation mapStd(StandardDancer... dancers) {
        // we know the superclass definition is in terms of this.map,
        // so the result will be a TaggedFormation.
        return (TaggedFormation) super.mapStd(dancers);
    }

    public TaggedFormation(Formation f, MultiMap<Dancer,Tag> tags) {
        super(f.location,f.selected);
        this.tags = UnmodifiableMultiMap.proxy(tags);//tags can be changed
    }
    public static TaggedFormation coerce(Formation f) {
        if (f instanceof TaggedFormation)
            return (TaggedFormation) f;
        return new TaggedFormation(f, Tools.<Dancer,Tag>mml());
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
