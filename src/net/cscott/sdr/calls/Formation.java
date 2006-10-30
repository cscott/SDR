package net.cscott.sdr.calls;

import static net.cscott.sdr.calls.StandardDancer.COUPLE_1_BOY;
import static net.cscott.sdr.calls.StandardDancer.COUPLE_1_GIRL;
import static net.cscott.sdr.calls.StandardDancer.COUPLE_2_BOY;
import static net.cscott.sdr.calls.StandardDancer.COUPLE_2_GIRL;
import static net.cscott.sdr.calls.StandardDancer.COUPLE_3_BOY;
import static net.cscott.sdr.calls.StandardDancer.COUPLE_3_GIRL;
import static net.cscott.sdr.calls.StandardDancer.COUPLE_4_BOY;
import static net.cscott.sdr.calls.StandardDancer.COUPLE_4_GIRL;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import net.cscott.sdr.util.Box;
import net.cscott.sdr.util.Fraction;
import net.cscott.sdr.util.Point;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/** A Formation is a set of dancers and positions for them.
 * Certain dancers in the formation can be selected. 
 * In addition, all dancers can be tagged with modifiers
 * such as 'BEAU', 'BELLE', 'LEADER', 'TRAILER', etc.
 * */
public class Formation {
    protected final Map<Dancer,Position> location;
    protected final Set<Dancer> selected;
    protected Formation(Map<Dancer,Position> location, Set<Dancer> selected) {
        this.location = location;
        this.selected = selected;
    }
    public Formation(Map<Dancer,Position> location) {
        this.location = Collections.unmodifiableMap(new LinkedHashMap<Dancer,Position>(location));
        this.selected = this.location.keySet();
    }

    /** Create a new formation containing only the selected dancers from this
     * formation. */
    public Formation onlySelected() {
	Map<Dancer,Position> nloc = new LinkedHashMap<Dancer,Position>
	    (this.location);
	nloc.keySet().retainAll(this.selected);
	return new Formation(nloc);
    }
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
    /** Return the bounds of this formation, which is the bounding
     * box around all the dancers' bounding boxes.  This means that
     * the 1 unit border from {@link #bounds(Dancer)} applies here
     * as well. If there are no dancers, returns a zero-width,
     * zero-height box centered at the origin. */
    public Box bounds() {
        Fraction minx=null,miny=null,maxx=null,maxy=null;
        for (Position p : location.values()) {
            if (minx==null || minx.compareTo(p.x) > 0) minx = p.x;
            if (maxx==null || maxx.compareTo(p.x) < 0) maxx = p.x;
            if (miny==null || miny.compareTo(p.y) > 0) miny = p.y;
            if (maxy==null || maxy.compareTo(p.y) < 0) maxy = p.y;
        }
        Point ll, ur;
        if (minx==null) {
            assert minx==null && miny==null && maxx==null && maxy==null;
            ll = new Point(Fraction.ZERO, Fraction.ZERO);
            ur = ll;
        } else {
            ll = new Point(minx.subtract(Fraction.ONE),
                           miny.subtract(Fraction.ONE));
            ur = new Point(maxx.add(Fraction.ONE),
                           maxy.add(Fraction.ONE));
        }
        return new Box(ll, ur);
    }
    /** Return the bounds of the given dancer -- always its position
     * plus or minus 1 unit on the x and y axes. */
    public Box bounds(Dancer d) {
        Position p = location(d);
        return new Box
              (new Point(p.x.subtract(Fraction.ONE),p.y.subtract(Fraction.ONE)),
               new Point(p.x.add(Fraction.ONE),p.y.add(Fraction.ONE)));
    }
    /** Build a new formation with only the given dancers
     * selected. */
    public Formation select(Set<Dancer> s) {
        Set<Dancer> nSel = new LinkedHashSet<Dancer>(s);
        nSel.retainAll(dancers());
        return new Formation(location, Collections.unmodifiableSet(nSel));
    }
    /** Build a new formation, centered on 0,0 */
    public Formation recenter() {
        Box bounds = bounds();
        Fraction ox = bounds.ll.x.add(bounds.ur.x).divide(Fraction.TWO);
        Fraction oy = bounds.ll.y.add(bounds.ur.y).divide(Fraction.TWO);
        Map<Dancer,Position> m = new LinkedHashMap<Dancer,Position>(location.size());
        for (Map.Entry<Dancer,Position> me : location.entrySet())
            m.put(me.getKey(), new Position(me.getValue().x.subtract(ox),
                    me.getValue().y.subtract(oy), me.getValue().facing));
        return new Formation(Collections.unmodifiableMap(m), selected);
    }
    /** Build a new formation, like this one except rotated around 0,0.
     * We rotate CW by the amount given in the {@code rotation} parameter;
     * "north" corresponds to no rotation. */
    public Formation rotate(ExactRotation rotation) {
        Map<Dancer,Position> m = new LinkedHashMap<Dancer,Position>(location.size());
        for (Map.Entry<Dancer,Position> me : location.entrySet())
            m.put(me.getKey(), me.getValue().rotateAroundOrigin(rotation));
        return new Formation(Collections.unmodifiableMap(m), selected);
    }
    public boolean isCentered() {
        Box bounds = bounds();
        return
        (bounds.ll.x.add(bounds.ur.x).compareTo(Fraction.ZERO) == 0) &&
        (bounds.ll.y.add(bounds.ur.y).compareTo(Fraction.ZERO) == 0);
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
			Position.getGrid(-1,-3,ExactRotation.ZERO)),
	 new DancerInfo(COUPLE_1_GIRL,
			Position.getGrid(+1,-3,ExactRotation.ZERO)),
	 new DancerInfo(COUPLE_2_BOY,
			Position.getGrid(+3,-1,ExactRotation.THREE_QUARTERS)),
	 new DancerInfo(COUPLE_2_GIRL,
			Position.getGrid(+3,+1,ExactRotation.THREE_QUARTERS)),
	 new DancerInfo(COUPLE_3_BOY,
			Position.getGrid(+1,+3,ExactRotation.ONE_HALF)),
	 new DancerInfo(COUPLE_3_GIRL,
			Position.getGrid(-1,+3,ExactRotation.ONE_HALF)),
	 new DancerInfo(COUPLE_4_BOY,
			Position.getGrid(-3,+1,ExactRotation.ONE_QUARTER)),
	 new DancerInfo(COUPLE_4_GIRL,
			Position.getGrid(-3,-1,ExactRotation.ONE_QUARTER))
	 );
    // starting formation for 2-couple dancing.
    public static final Formation FOUR_SQUARE = new Formation
        (new DancerInfo(COUPLE_1_BOY,
                Position.getGrid(-1,-1,ExactRotation.ZERO)),
         new DancerInfo(COUPLE_1_GIRL,
                Position.getGrid(+1,-1,ExactRotation.ZERO)),
         new DancerInfo(COUPLE_3_BOY,
                Position.getGrid(+1,+1,ExactRotation.ONE_HALF)),
         new DancerInfo(COUPLE_3_GIRL,
                Position.getGrid(-1,+1,ExactRotation.ONE_HALF))
        );

    public Formation(Formation f, Map<Dancer,Dancer> map) {
        Map<Dancer,Position> m = new LinkedHashMap<Dancer,Position>();
        Set<Dancer> s = new LinkedHashSet<Dancer>();
        for (Map.Entry<Dancer,Position> me : f.location.entrySet())
            m.put(map.get(me.getKey()), me.getValue());
        for (Dancer d : f.selected)
            s.add(map.get(d));
        this.location = Collections.unmodifiableMap(m);
        this.selected = Collections.unmodifiableSet(s);
    }
    
    Formation(DancerInfo... dis) {
	Map<Dancer,Position> m = new LinkedHashMap<Dancer,Position>();
	Set<Dancer> s = new LinkedHashSet<Dancer>();
	for (DancerInfo di : dis) {
	    m.put(di.dancer, di.position);
            if (di.isSelected)
		s.add(di.dancer);
	}
	this.location = Collections.unmodifiableMap(m);
	this.selected = Collections.unmodifiableSet(s);
    }
    static class DancerInfo {
	final Dancer dancer;
	final Position position;
        final boolean isSelected;
	DancerInfo(Dancer d, Position p, boolean s) {
	    this.dancer = d; this.position = p;
	    this.isSelected = s;
	}
	DancerInfo(Dancer d, Position p) {
            this(d,p,true);
        }
    }
}
