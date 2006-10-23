package net.cscott.sdr.util;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/** An orthogonal box, with sides parallel to the x and y axes. */
public class Box {
    public final Point ll, ur;
    public Box(Point ll, Point ur) {
        this.ll=ll; this.ur=ur;
        assert ll.x.compareTo(ur.x) <= 0;
        assert ll.y.compareTo(ur.y) <= 0;
    }
    public Fraction height() {
        return ur.y.subtract(ll.y);
    }
    public Fraction width() {
        return ur.x.subtract(ll.x);
    }
    public boolean equals(Object o) {
        if (!(o instanceof Box)) return false;
        Box b = (Box) o;
        return new EqualsBuilder()
            .append(this.ll, b.ll)
            .append(this.ur, b.ur)
            .isEquals();
    }
    public int hashCode() {
        return new HashCodeBuilder()
            .append(ll).append(ur)
            .toHashCode();
    }
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE)
            .append("ll", ll)
            .append("ur", ur)
            .toString();
    }
}
