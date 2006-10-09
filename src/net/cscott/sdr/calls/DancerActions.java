package net.cscott.sdr.calls;

import java.util.*;
import net.cscott.sdr.util.*;

/** <code>DancerActions</code> is a bundle of 
 * <code>PathAndTiming</code> objects, one for each dancer.
 */
public class DancerActions {
    private final Map<Dancer,PathAndTiming> actionMap =
        new HashMap<Dancer,PathAndTiming>();
    
    public DancerActions() { }
    
    public Fraction actionDuration() { return null; }//XXX
    public void addAction(Dancer d, Fraction start, Fraction end, Path p) { }
    private class PathAndTiming { } //XXX
    private class Path { } //XXX
}
