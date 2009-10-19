// $Id: ClStrength.java,v 1.1 2008/08/12 22:32:42 larrymelia Exp $
//
// Cassowary Incremental Constraint Solver
// Original Smalltalk Implementation by Alan Borning
// This Java Implementation by Greg J. Badros, <gjb@cs.washington.edu>
// http://www.cs.washington.edu/homes/gjb
// (C) 1998, 1999 Greg J. Badros and Alan Borning
// See ../LICENSE for legal details regarding this software
//
// ClStrength

package EDU.Washington.grad.gjb.cassowary;

import net.cscott.sdr.util.Fraction;

public class ClStrength {
    public ClStrength(String name, ClSymbolicWeight symbolicWeight) {
        _name = name;
        _symbolicWeight = symbolicWeight;
    }

    public ClStrength(String name, Fraction w1, Fraction w2, Fraction w3) {
        _name = name;
        _symbolicWeight = new ClSymbolicWeight(w1, w2, w3);
    }

    public boolean isRequired() {
        return (this == required);
    }

    public String toString() {
        return name() + (!isRequired() ? (":" + symbolicWeight()) : "");
    }

    public ClSymbolicWeight symbolicWeight() {
        return _symbolicWeight;
    }

    public String name() {
        return _name;
    }

    public void set_name(String name) {
        _name = name;
    }

    public void set_symbolicWeight(ClSymbolicWeight symbolicWeight) {
        _symbolicWeight = symbolicWeight;
    }

    public static final ClStrength required = new ClStrength("<Required>",
            Fraction.valueOf(1000), Fraction.valueOf(1000), Fraction.valueOf(1000));

    public static final ClStrength strong = new ClStrength("strong", Fraction.ONE, Fraction.ZERO,
            Fraction.ZERO);

    public static final ClStrength medium = new ClStrength("medium", Fraction.ZERO, Fraction.ONE,
            Fraction.ZERO);

    public static final ClStrength weak = new ClStrength("weak", Fraction.ZERO, Fraction.ZERO, Fraction.ONE);

    private String _name;

    private ClSymbolicWeight _symbolicWeight;

}
