package net.cscott.sdr.util;

import net.cscott.jdoctest.JDoctestRunner;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.junit.runner.RunWith;

/** An orthogonal box, with sides parallel to the x and y axes. */
@RunWith(value=JDoctestRunner.class)
public class Box {
    public final Point ll, ur;
    /** Basic constructor.
     * @param ll Lower-left corner (minimum x, minimum y)
     * @param ur Upper-right corner (maximum x, maximum y)
     */
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
    public Fraction area() {
        return height().multiply(width());
    }
    /** Returns the center of the box.
     * @doc.test
     *  js> function f(i) { return Fraction.valueOf(i); }
     *  js> new Box(new Point(f(-1),f(-1)), new Point(f(1),f(1))).center();
     *  0,0
     *  js> new Box(new Point(f(0),f(0)), new Point(f(3),f(4))).center();
     *  1 1/2,2
     */
    public Point center() {
        return new Point(
                ll.x.add(ur.x).divide(Fraction.TWO),
                ll.y.add(ur.y).divide(Fraction.TWO));
    }
    /** Returns a box which contains both this box and the given one.
     * @doc.test
     *  js> function f(i) { return Fraction.valueOf(i); }
     *  js> b1 = new Box(new Point(f(-1),f(-1)), new Point(f(1),f(1)));
     *  (-1,-1;1,1)
     *  js> b2 = new Box(new Point(f(0),f(0)), new Point(f(3),f(3)));
     *  (0,0;3,3)
     *  js> b3 = b1.union(b2);
     *  (-1,-1;3,3)
     */
    public Box union(Box b) {
        return new Box(new Point(Fraction.min(this.ll.x, b.ll.x),
                                 Fraction.min(this.ll.y, b.ll.y)),
                       new Point(Fraction.max(this.ur.x, b.ur.x),
                                 Fraction.max(this.ur.y, b.ur.y)));
    }
    /**
     * Returns true iff this box overlaps the given one.  The edges count
     * as part of the inside.
     * @doc.test
     *  js> function f(i) { return Fraction.valueOf(i); }
     *  js> b1 = new Box(new Point(f(-1),f(-1)), new Point(f(1),f(1)));
     *  (-1,-1;1,1)
     *  js> b2 = new Box(new Point(f(0),f(0)), new Point(f(3),f(3)));
     *  (0,0;3,3)
     *  js> b3 = new Box(new Point(f(-2),f(-2)), new Point(f(0),f(0)));
     *  (-2,-2;0,0)
     *  js> b4 = new Box(new Point(f(1),f(4)), new Point(f(2),f(4)));
     *  (1,4;2,4)
     *  js> b1.overlaps(b2)
     *  true
     *  js> b1.overlaps(b2) == b2.overlaps(b1)
     *  true
     *  js> b1.overlaps(b3)
     *  true
     *  js> b1.overlaps(b3) == b3.overlaps(b1)
     *  true
     *  js> b2.overlaps(b4)
     *  false
     *  js> b2.overlaps(b4) == b4.overlaps(b2)
     *  true
     *  js> b2.overlaps(b3) // not the same as overlapsExcl
     *  true
     *  js> b2.overlaps(b3) == b3.overlaps(b2)
     *  true
     * @doc.test
     *  js> function f(i) { return Fraction.valueOf(i); }
     *  js> b1 = new Box(new Point(f(-4),f(0)), new Point(f(0),f(2)));
     *  (-4,0;0,2)
     *  js> b2 = new Box(new Point(f(-4),f(-2)), new Point(f(0),f(0)));
     *  (-4,-2;0,0)
     *  js> b1.overlaps(b2)
     *  true
     *  js> b2.overlaps(b1)
     *  true
     */
    public boolean overlaps(Box b) {
        return (ll.x.compareTo(b.ur.x) <= 0 &&
                b.ll.x.compareTo(ur.x) <= 0 &&
                ll.y.compareTo(b.ur.y) <= 0 &&
                b.ll.y.compareTo(ur.y) <= 0);
    }
    /**
     * Returns true iff this box overlaps the given one.  The edges
     * DO NOT count as part of the inside.
     * @doc.test
     *  js> function f(i) { return Fraction.valueOf(i); }
     *  js> b1 = new Box(new Point(f(-1),f(-1)), new Point(f(1),f(1)));
     *  (-1,-1;1,1)
     *  js> b2 = new Box(new Point(f(0),f(0)), new Point(f(3),f(3)));
     *  (0,0;3,3)
     *  js> b3 = new Box(new Point(f(-2),f(-2)), new Point(f(0),f(0)));
     *  (-2,-2;0,0)
     *  js> b4 = new Box(new Point(f(1),f(4)), new Point(f(2),f(4)));
     *  (1,4;2,4)
     *  js> b1.overlapsExcl(b2)
     *  true
     *  js> b1.overlapsExcl(b2) == b2.overlapsExcl(b1)
     *  true
     *  js> b1.overlapsExcl(b3)
     *  true
     *  js> b1.overlapsExcl(b3) == b3.overlapsExcl(b1)
     *  true
     *  js> b2.overlapsExcl(b4)
     *  false
     *  js> b2.overlapsExcl(b4) == b4.overlapsExcl(b2)
     *  true
     *  js> b2.overlapsExcl(b3) // not the same as overlaps()
     *  false
     *  js> b2.overlapsExcl(b3) == b3.overlapsExcl(b2)
     *  true
     * @doc.test
     *  js> function f(i) { return Fraction.valueOf(i); }
     *  js> b1 = new Box(new Point(f(-4),f(0)), new Point(f(0),f(2)));
     *  (-4,0;0,2)
     *  js> b2 = new Box(new Point(f(-4),f(-2)), new Point(f(0),f(0)));
     *  (-4,-2;0,0)
     *  js> b1.overlapsExcl(b2)
     *  false
     *  js> b2.overlapsExcl(b1)
     *  false
     */
    public boolean overlapsExcl(Box b) {
        return (ll.x.compareTo(b.ur.x) < 0 &&
                b.ll.x.compareTo(ur.x) < 0 &&
                ll.y.compareTo(b.ur.y) < 0 &&
                b.ll.y.compareTo(ur.y) < 0);
    }
    /**
     * Returns true iff the given point in inside this box.  The edges count
     * as "inside".
     * @doc.test
     *  js> function f(i) { return Fraction.valueOf(i); }
     *  js> b = new Box(new Point(f(-1),f(-1)), new Point(f(1),f(1)));
     *  (-1,-1;1,1)
     *  js> b.includes(new Point(f(0),f(0)))
     *  true
     *  js> b.includes(b.ll)
     *  true
     *  js> b.includes(b.ur)
     *  true
     *  js> b.includes(new Point(f(-1),f(1)))
     *  true
     *  js> b.includes(new Point(f(1),f(-1)))
     *  true
     *  js> b.includes(new Point(f(-2),f(0)))
     *  false
     *  js> b.includes(new Point(f(0),f(2)))
     *  false
     */
    public boolean includes(Point p) {
        return (ll.x.compareTo(p.x) <= 0 &&
                ll.y.compareTo(p.y) <= 0 &&
                ur.x.compareTo(p.x) >= 0 &&
                ur.y.compareTo(p.y) >= 0);
    }
    /**
     * Returns true iff the given point in inside this box.  The edges
     * do *not* count as "inside".
     * @doc.test
     *  js> function f(i) { return Fraction.valueOf(i); }
     *  js> b = new Box(new Point(f(-1),f(-1)), new Point(f(1),f(1)));
     *  (-1,-1;1,1)
     *  js> b.includesExcl(new Point(f(0),f(0)))
     *  true
     *  js> b.includesExcl(b.ll)
     *  false
     *  js> b.includesExcl(b.ur)
     *  false
     *  js> b.includesExcl(new Point(f(-1),f(1)))
     *  false
     *  js> b.includesExcl(new Point(f(1),f(-1)))
     *  false
     *  js> b.includesExcl(new Point(f(-2),f(0)))
     *  false
     *  js> b.includesExcl(new Point(f(0),f(2)))
     *  false
     */
    public boolean includesExcl(Point p) {
        return (ll.x.compareTo(p.x) < 0 &&
                ll.y.compareTo(p.y) < 0 &&
                ur.x.compareTo(p.x) > 0 &&
                ur.y.compareTo(p.y) > 0);
    }
    /**
     * Mirror the box along the y-axis (that is, flip the x coordinates).
     * @doc.test
     *  js> function f(i) { return Fraction.valueOf(i); }
     *  js> b = new Box(new Point(f(-1),f(-2)), new Point(f(3),f(4)));
     *  (-1,-2;3,4)
     *  js> b.mirrorX()
     *  (-3,-2;1,4)
     */
    public Box mirrorX() {
        return new Box(new Point(ur.x.negate(), ll.y),
                       new Point(ll.x.negate(), ur.y));
    }
    /**
     * Mirror the box along the x-axis (that is, flip the y coordinates).
     * @doc.test
     *  js> function f(i) { return Fraction.valueOf(i); }
     *  js> b = new Box(new Point(f(-1),f(-2)), new Point(f(3),f(4)));
     *  (-1,-2;3,4)
     *  js> b.mirrorY()
     *  (-1,-4;3,2)
     */
    public Box mirrorY() {
        return new Box(new Point(ll.x, ur.y.negate()),
                       new Point(ur.x, ll.y.negate()));
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
        return "("+ll+";"+ur+")";
    }
}
