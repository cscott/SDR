package net.cscott.sdr.util;

import java.util.Arrays;

/** Utility methods for efficiently evaluating bezier curves. */
public class Bezier {
    private Bezier() { }

    /** Evaluate a cubic bezier with the given four control points.  The time
     * argument should be between 0 and 1, inclusive. */
    public static float cubicInterp(float p0, float p1, float p2, float p3, float t){
        if (t<0) t = 0;
        if (t>1) t = 1;
        float onem = 1 - t;
        return p0*onem*onem*onem + 3*p1*t*onem*onem + 3*p2*t*t*onem + p3*t*t*t;
    }
    /** Evaluate a cubic bezier with the given four control points.  The time
     * argument should be between 0 and 1, inclusive. */
    public static Fraction cubicInterp(Fraction p0, Fraction p1,
                                       Fraction p2, Fraction p3, Fraction t) {
        if (t.compareTo(Fraction.ZERO) < 0) t = Fraction.ZERO;
        if (t.compareTo(Fraction.ONE) > 0) t = Fraction.ONE;
        Fraction THREE = Fraction.valueOf(3);
        Fraction onem = Fraction.ONE.subtract(t);
        Fraction onem2 = onem.multiply(onem);
        Fraction onem3 = onem.multiply(onem2);
        Fraction t2 = t.multiply(t);
        Fraction t3 = t.multiply(t2);
        return Fraction.ZERO.add
            (p0.multiply(onem3)).add
            (THREE.multiply(p1).multiply(t).multiply(onem2)).add
            (THREE.multiply(p2).multiply(t2).multiply(onem)).add
            (p3.multiply(t3));
    }
    /** Evaluate the derivative of a cubic bezier with the given four control
     * points. */
    public static float cubicDeriv(float p0, float p1, float p2, float p3, float t) {
        return quadInterp(3*(p1-p0),3*(p2-p1),3*(p3-p2),t);
    }
    /** Evaluate the derivative of a cubic bezier with the given four control
     * points. */
    public static Fraction cubicDeriv(Fraction p0, Fraction p1,
                                      Fraction p2, Fraction p3, Fraction t) {
        Fraction THREE = Fraction.valueOf(3);
        return quadInterp(THREE.multiply(p1.subtract(p0)),
                          THREE.multiply(p2.subtract(p1)),
                          THREE.multiply(p3.subtract(p2)), t);
    }
    /** Evaluate a quadratic bezier with the given three control points.  The
     * time argument should be between 0 and 1, inclusive. */
    public static float quadInterp(float p0, float p1, float p2, float t) {
        if (t<0) t = 0;
        if (t>1) t = 1;
        float onem = 1 - t;
        return p0*onem*onem + 2*p1*t*onem + p2*t*t;
    }
    /** Evaluate a quadratic bezier with the given three control points.  The
     * time argument should be between 0 and 1, inclusive. */
    public static Fraction quadInterp(Fraction p0, Fraction p1,
                                      Fraction p2, Fraction t) {
        if (t.compareTo(Fraction.ZERO) < 0) t = Fraction.ZERO;
        if (t.compareTo(Fraction.ONE) > 0) t = Fraction.ONE;
        Fraction onem = Fraction.ONE.subtract(t);
        Fraction onem2 = onem.multiply(onem);
        Fraction t2 = t.multiply(t);
        return p0.multiply(onem2).add
            (Fraction.TWO.multiply(p1).multiply(t).multiply(onem)).add
            (p2.multiply(t2));
    }
    /** Bundle bezier parameters together into an object */
    public static class Bezier2D {
        final Point p[];
        public Bezier2D(Point... p) {
            this.p = p;
        }
        public String toString() {
            return Arrays.asList(p).toString();
        }
        public int degree() {
            return p.length-1;
        }
        /** Evaluate the given Bezier at the given time parameter, which should
         *  be in the range [0, 1].
         */
        public Point evaluate(Fraction t) {
            switch (degree()) {
            case 0:
            case 1:
                return raise().evaluate(t);
            case 2:
                return new Point
                    (quadInterp(p[0].x, p[1].x, p[2].x, t),
                     quadInterp(p[0].y, p[1].y, p[2].y, t));
            case 3:
                return new Point
                    (cubicInterp(p[0].x, p[1].x, p[2].x, p[3].x, t),
                     cubicInterp(p[0].y, p[1].y, p[2].y, p[3].y, t));
            default:
                throw new RuntimeException
                    ("Higher-order beziers not supported.");
            }
        }
        /** Compute the derivative of the given Bezier curve.
         * @doc.test Derivative of a straight line is a constant:
         *  js> p1 = new Point(Fraction.ZERO, Fraction.ONE);
         *  0,1
         *  js> p2 = new Point(Fraction.ONE, Fraction.ZERO);
         *  1,0
         *  js> b = new Bezier.Bezier2D(p1, p2);
         *  [0,1, 1,0]
         *  js> b.tangent()
         *  [1,-1]
         *  js> b.raise()
         *  [0,1, 1/2,1/2, 1,0]
         *  js> b.raise().tangent()
         *  [1,-1, 1,-1]
         * @doc.test More complicated tangent:
         *  js> b = new Bezier.Bezier2D(new Point(Fraction.ZERO, Fraction.ZERO),
         *    >                         new Point(Fraction.ZERO, Fraction.ONE),
         *    >                         new Point(Fraction.ONE, Fraction.ONE),
         *    >                         new Point(Fraction.ONE, Fraction.ZERO));
         *  [0,0, 0,1, 1,1, 1,0]
         *  js> b.tangent()
         *  [0,3, 3,0, 0,-3]
         */
        public Bezier2D tangent() {
            // compute bezier derivative
            Fraction degree = Fraction.valueOf(p.length - 1);
            Point[] np = new Point[p.length-1];
            for (int i=0; i<np.length; i++)
                np[i] = p[i+1].subtract(p[i]).multiply(degree);
            return new Bezier2D(np);
        }
        /** Raise the degree of a bezier curve (add an additional control
         *  point).
         * @doc.test Basic interpolation of a straight line.
         *  js> p1 = new Point(Fraction.ZERO, Fraction.ONE);
         *  0,1
         *  js> p2 = new Point(Fraction.ONE, Fraction.ZERO);
         *  1,0
         *  js> b = new Bezier.Bezier2D(p1, p2);
         *  [0,1, 1,0]
         *  js> b = b.raise()
         *  [0,1, 1/2,1/2, 1,0]
         *  js> b = b.raise()
         *  [0,1, 1/3,2/3, 2/3,1/3, 1,0]
         *  js> b = b.raise()
         *  [0,1, 1/4,3/4, 1/2,1/2, 3/4,1/4, 1,0]
         * @doc.test With enough iterations, the control polygon begins to
         *  approximate the curve.
         *  js> b = new Bezier.Bezier2D(new Point(Fraction.ZERO, Fraction.ZERO),
         *    >                         new Point(Fraction.ZERO, Fraction.ONE),
         *    >                         new Point(Fraction.ONE, Fraction.ONE),
         *    >                         new Point(Fraction.ONE, Fraction.ZERO));
         *  [0,0, 0,1, 1,1, 1,0]
         *  js> b = b.raise();
         *  [0,0, 0,3/4, 1/2,1, 1,3/4, 1,0]
         *  js> b = b.raise();
         *  [0,0, 0,3/5, 3/10,9/10, 7/10,9/10, 1,3/5, 1,0]
         *  js> b = b.raise();
         *  [0,0, 0,1/2, 1/5,4/5, 1/2,9/10, 4/5,4/5, 1,1/2, 1,0]
         *  js> b = b.raise();
         *  [0,0, 0,3/7, 1/7,5/7, 13/35,6/7, 22/35,6/7, 6/7,5/7, 1,3/7, 1,0]
         */
        public Bezier2D raise() {
            // raise bezier curve to a higher degree.
            Point[] np = new Point[p.length+1];
            Fraction newDegree = Fraction.valueOf(np.length-1);
            Point ZERO = new Point(Fraction.ZERO, Fraction.ZERO);
            for (int i=0; i<np.length; i++) {
                Fraction alpha = Fraction.valueOf(i).divide(newDegree);
                np[i] = ZERO;
                if (i>0)
                    np[i] = np[i].add(p[i-1].multiply(alpha));
                if (i<p.length)
                    np[i] = np[i].add(p[i].multiply(Fraction.ONE.subtract(alpha)));
            }
            return new Bezier2D(np);
        }
    }
}
