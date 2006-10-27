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
    /** Evaluate the derivative of a cubic bezier with the given four control
     * points. */
    public static float cubicDeriv(float p0, float p1, float p2, float p3, float t) {
        return quadInterp(3*(p1-p0),3*(p2-p1),3*(p3-p2),t);
    }
    /** Evaluate a quadratic bezier with the given three control points.  The
     * time argument should be between 0 and 1, inclusive. */
    public static float quadInterp(float p0, float p1, float p2, float t) {
        if (t<0) t = 0;
        if (t>1) t = 1;
        float onem = 1 - t;
        return p0*onem*onem + 2*p1*t*onem + p2*t*t;
    }
}
