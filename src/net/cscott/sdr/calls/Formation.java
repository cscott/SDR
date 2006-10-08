package net.cscott.sdr.calls;

import static net.cscott.sdr.calls.StandardDancer.*;

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
import net.cscott.jutil.*;

/** A Formation is a set of dancers and positions for them.
 * Certain dancers in the formation can be selected. 
 * In addition, all dancers can be tagged with modifiers
 * such as 'BEAU', 'BELLE', 'LEADER', 'TRAILER', etc.
 * */
public class Formation {
    public enum Identifier {
        BEAU, BELLE, LEADER, TRAILER;
    };
    private final Map<Dancer,Position> location;
    private final Map<Dancer,Set<Identifier>> tags;
    private final Set<Dancer> selected;

    public boolean isSelected(Dancer d) {
	return selected.contains(d);
    }
    public Set<Dancer> selectedDancers() {
	return selected;
    }
    public Set<Dancer> dancers() {
	return location.keySet();
    }
    public Position location(Dancer d) {
	return location.get(d);
    }
    // utility functions.
    public boolean equals(Object o) {
	if (!(o instanceof Formation)) return false;
	Formation f = (Formation) o;
	return new EqualsBuilder()
	    .append(location, f.location)
	    .append(selected, f.selected)
	    .isEquals();
    }
    public int hashCode() {
	return new HashCodeBuilder()
	    .append(location).append(selected)
	    .toHashCode();
    }
    public String toString() {
	return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
	    .append("location", location)
	    .append("selected", selected)
	    .toString();
    }
    // starting formation for 8-couple dancing
    public static final Formation SQUARED_SET = new Formation
	(new DancerInfo(COUPLE_1_BOY,
			Position.getGrid(-1,-3,Rotation.ZERO), Identifier.BEAU),
	 new DancerInfo(COUPLE_1_GIRL,
			Position.getGrid(+1,-3,Rotation.ZERO), Identifier.BELLE),
	 new DancerInfo(COUPLE_2_BOY,
			Position.getGrid(+3,-1,Rotation.THREE_QUARTERS), Identifier.BEAU),
	 new DancerInfo(COUPLE_2_GIRL,
			Position.getGrid(+3,+1,Rotation.THREE_QUARTERS), Identifier.BELLE),
	 new DancerInfo(COUPLE_3_BOY,
			Position.getGrid(+1,+3,Rotation.ONE_HALF), Identifier.BEAU),
	 new DancerInfo(COUPLE_3_GIRL,
			Position.getGrid(-1,+3,Rotation.ONE_HALF), Identifier.BELLE),
	 new DancerInfo(COUPLE_4_BOY,
			Position.getGrid(-3,+1,Rotation.ONE_QUARTER), Identifier.BEAU),
	 new DancerInfo(COUPLE_4_GIRL,
			Position.getGrid(-3,-1,Rotation.ONE_QUARTER), Identifier.BELLE)
	 );
    // starting formation for 2-couple dancing.
    public static final Formation FOUR_SQUARE = new Formation
        (new DancerInfo(COUPLE_1_BOY,
                Position.getGrid(-1,-1,Rotation.ZERO), Identifier.BEAU),
         new DancerInfo(COUPLE_1_GIRL,
                Position.getGrid(+1,-1,Rotation.ZERO), Identifier.BELLE),
         new DancerInfo(COUPLE_3_BOY,
                Position.getGrid(+1,+1,Rotation.ONE_HALF), Identifier.BEAU),
         new DancerInfo(COUPLE_3_GIRL,
                Position.getGrid(-1,+1,Rotation.ONE_HALF), Identifier.BELLE)
        );

    Formation(DancerInfo... dis) {
	Map<Dancer,Position> m = new HashMap<Dancer,Position>();
	Set<Dancer> s = new HashSet<Dancer>();
        Map<Dancer,Set<Identifier>> t = new HashMap<Dancer,Set<Identifier>>();
	for (DancerInfo di : dis) {
	    m.put(di.dancer, di.position);
	    t.put(di.dancer, Collections.unmodifiableSet(di.tags));
            if (di.isSelected)
		s.add(di.dancer);
	}
	this.location = Collections.unmodifiableMap(m);
	this.selected = Collections.unmodifiableSet(s);
        this.tags = Collections.unmodifiableMap(t);
    }
    static class DancerInfo {
	final Dancer dancer;
	final Position position;
	final Set<Identifier> tags;
        final boolean isSelected;
	DancerInfo(Dancer d, Position p, Set<Identifier> tags, boolean s) {
	    this.dancer = d; this.position = p;
	    this.tags = tags; this.isSelected = s;
	}
	DancerInfo(Dancer d, Position p, Identifier... tags) {
            this(d,p,setOf(tags),true);
        }
    }
    private static Set<Identifier> setOf(Identifier... tags) {
        if (tags.length==0) return EnumSet.noneOf(Identifier.class);
        else return EnumSet.of(tags[0], tags);
    }
}
