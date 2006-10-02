package net.cscott.sdr.calls;

/** Dancer objects represent a dancer (real or phantom).  Equality is
 *  object identity.  There are eight canonical 'real' dancers, and
 *  an unlimited number of phantoms. */
public interface Dancer {
    public boolean isHead();
    public boolean isSide();
    public boolean isBoy();
    public boolean isGirl();
}
