package net.cscott.sdr.calls;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import net.cscott.jutil.MultiMap;
import net.cscott.sdr.util.SdrToString;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Helper class to associate a standard name with a canonical
 * formation.  This makes string representations more compact!
 */
public class NamedTaggedFormation extends TaggedFormation {
    private final String name;
    public NamedTaggedFormation(String name, TaggedDancerInfo... tdi) {
	super(tdi);
	this.name = name;
    }
    public NamedTaggedFormation(String name, Formation f,
				MultiMap<Dancer,Tag> tags) {
	super(f, tags);
	this.name = name;
    }
    public NamedTaggedFormation(String name, TaggedFormation tf) {
        super(tf, new AbstractMap<Dancer,Dancer>() {
            // HACK! simple identity map.
            @Override
            public Dancer get(Object d) { return (Dancer) d; }
            @Override
            public Set<Map.Entry<Dancer, Dancer>> entrySet() {
                return Collections.emptySet();
            }});
        this.name = name;
    }
    public String getName() { return this.name; }
    public String toString() {
	return new ToStringBuilder(this, SdrToString.STYLE)
	    .append("name", getName())
	    .appendSuper(super.toString())
	    .toString();
    }
}
