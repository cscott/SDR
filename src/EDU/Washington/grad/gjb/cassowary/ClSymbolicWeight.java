// $Id: ClSymbolicWeight.java,v 1.1 2008/08/12 22:32:43 larrymelia Exp $
//
// Cassowary Incremental Constraint Solver
// Original Smalltalk Implementation by Alan Borning
// This Java Implementation by Greg J. Badros, <gjb@cs.washington.edu>
// http://www.cs.washington.edu/homes/gjb
// (C) 1998, 1999 Greg J. Badros and Alan Borning
// See ../LICENSE for legal details regarding this software
//
// ClSymbolicWeight

package EDU.Washington.grad.gjb.cassowary;

import net.cscott.sdr.util.Fraction;

public class ClSymbolicWeight {

    public ClSymbolicWeight(int cLevels) {
        _values = new Fraction[cLevels];
        for (int i = 0; i < cLevels; i++) {
            _values[i] = Fraction.ZERO;
        }
    }

    public ClSymbolicWeight(Fraction w1, Fraction w2, Fraction w3) {
        _values = new Fraction[3];
        _values[0] = w1;
        _values[1] = w2;
        _values[2] = w3;
    }

    public ClSymbolicWeight(Fraction[] weights) {
        final int cLevels = weights.length;
        _values = new Fraction[cLevels];
        for (int i = 0; i < cLevels; i++) {
            _values[i] = weights[i];
        }
    }

    public static final ClSymbolicWeight clsZero = new ClSymbolicWeight(Fraction.ZERO,
            Fraction.ZERO, Fraction.ZERO);

    public Object clone() {
        return new ClSymbolicWeight(_values);
    }

    public ClSymbolicWeight times(Fraction n) {
        ClSymbolicWeight clsw = (ClSymbolicWeight) clone();
        for (int i = 0; i < _values.length; i++) {
            clsw._values[i] = clsw._values[i].multiply(n);
        }
        return clsw;
    }

    public ClSymbolicWeight divideBy(Fraction n) {
        // assert(n != 0);
        ClSymbolicWeight clsw = (ClSymbolicWeight) clone();
        for (int i = 0; i < _values.length; i++) {
            clsw._values[i] = clsw._values[i].divide(n);
        }
        return clsw;
    }

    public ClSymbolicWeight add(ClSymbolicWeight cl) {
        // assert(cl.cLevels() == cLevels());

        ClSymbolicWeight clsw = (ClSymbolicWeight) clone();
        for (int i = 0; i < _values.length; i++) {
            clsw._values[i] = clsw._values[i].add(cl._values[i]);
        }
        return clsw;
    }

    public ClSymbolicWeight subtract(ClSymbolicWeight cl) {
        // assert(cl.cLevels() == cLevels());

        ClSymbolicWeight clsw = (ClSymbolicWeight) clone();
        for (int i = 0; i < _values.length; i++) {
            clsw._values[i] = clsw._values[i].subtract(cl._values[i]);
        }
        return clsw;
    }

    public boolean lessThan(ClSymbolicWeight cl) {
        // assert cl.cLevels() == cLevels()
        for (int i = 0; i < _values.length; i++) {
            if (_values[i].compareTo(cl._values[i]) < 0)
                return true;
            else if (_values[i].compareTo(cl._values[i]) > 0)
                return false;
        }
        return false; // they are equal
    }

    public boolean lessThanOrEqual(ClSymbolicWeight cl) {
        // assert cl.cLevels() == cLevels()
        for (int i = 0; i < _values.length; i++) {
            if (_values[i].compareTo(cl._values[i]) < 0)
                return true;
            else if (_values[i].compareTo(cl._values[i]) > 0)
                return false;
        }
        return true; // they are equal
    }

    public boolean equal(ClSymbolicWeight cl) {
        for (int i = 0; i < _values.length; i++) {
            if (!_values[i].equals(cl._values[i]))
                return false;
        }
        return true; // they are equal
    }

    public boolean greaterThan(ClSymbolicWeight cl) {
        return !this.lessThanOrEqual(cl);
    }

    public boolean greaterThanOrEqual(ClSymbolicWeight cl) {
        return !this.lessThan(cl);
    }

    public boolean isNegative() {
        return this.lessThan(clsZero);
    }

    public Fraction asFraction() {
        // ClSymbolicWeight clsw = (ClSymbolicWeight) clone(); // LM--Not used
        Fraction sum = Fraction.ZERO;
        Fraction factor = Fraction.ONE;
        Fraction multiplier = Fraction.valueOf(1000);
        for (int i = _values.length - 1; i >= 0; i--) {
            sum = sum.add(_values[i].multiply(factor));
            factor = factor.multiply(multiplier);
        }
        return sum;
    }

    public String toString() {
        StringBuffer bstr = new StringBuffer("[");
        for (int i = 0; i < _values.length - 1; i++) {
            bstr.append(_values[i]);
            bstr.append(",");
        }
        bstr.append(_values[_values.length - 1]);
        bstr.append("]");
        return bstr.toString();
    }

    public int cLevels() {
        return _values.length;
    }

    private Fraction[] _values;

}
