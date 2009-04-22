package net.cscott.sdr.util;

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
}
