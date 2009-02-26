package net.cscott.sdr.calls;

import net.cscott.jutil.MultiMap;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

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
    public String getName() { return this.name; }
    public String toString() {
	return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
	    .append("name", getName())
	    .appendSuper(super.toString())
	    .toString();
    }
}
