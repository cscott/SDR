package net.cscott.sdr.calls;

/** This exception indicates that a call was invalid. */
public class BadCallException extends RuntimeException {
    //public BadCallException() { super(); }
    public BadCallException(String s) { super(s); }
}
