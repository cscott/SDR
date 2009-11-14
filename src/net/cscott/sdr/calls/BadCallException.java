package net.cscott.sdr.calls;

import net.cscott.sdr.util.Fraction;

/** This exception indicates that a call was invalid. */
public class BadCallException extends RuntimeException {
    /** Priority field helps us distinguish
     *  ordinary "you can't do that from here" exceptions from special
     *  messages from If nodes, like "facing recycle is not
     *  valid at Plus".  The default priority for "ordinary" exceptions
     *  is 0. {@link net.cscott.sdr.calls.ast.If} nodes with messages default
     *  to 1.
     */
    public final Fraction priority;
    //public BadCallException() { super(); }
    public BadCallException(String s) { this(s, Fraction.ZERO); }
    public BadCallException(String s, Fraction priority) {
	super(s);
	this.priority = priority;
    }
}
