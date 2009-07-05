package net.cscott.sdr.util;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/** A 2D point, where x and y coordinate are {@link Fraction}s. */
public class Point {
    public static final Point ZERO = new Point(Fraction.ZERO, Fraction.ZERO);
    public final Fraction x, y;
    public Point(Fraction x, Fraction y) {
        this.x = x; this.y = y;
    }
    public Point add(Point p) {
        return new Point(this.x.add(p.x), this.y.add(p.y));
    }
    public Point subtract(Point p) {
        return new Point(this.x.subtract(p.x), this.y.subtract(p.y));
    }
    public Point multiply(Fraction f) {
        return new Point(this.x.multiply(f), this.y.multiply(f));
    }
    public boolean equals(Object o) {
        if (!(o instanceof Point)) return false;
        Point p = (Point) o;
        return new EqualsBuilder()
            .append(this.x, p.x)
            .append(this.y, p.y)
            .isEquals();
    }
    public int hashCode() {
        return new HashCodeBuilder()
            .append(x).append(y)
            .toHashCode();
    }
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE)
            .append("x", x.toProperString())
            .append("y", y.toProperString())
            .toString();
    }
}
