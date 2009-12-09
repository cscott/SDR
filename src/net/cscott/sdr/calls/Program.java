package net.cscott.sdr.calls;

import net.cscott.jdoctest.JDoctestRunner;

import org.junit.runner.RunWith;

/** Enumeration of square dance programs.  Every call belongs to one or more
 *  square dance programs, which are usually arranged in a hierarchy such
 *  that each successive program contains all the calls from all previous
 *  programs. */
@RunWith(value=JDoctestRunner.class)
public enum Program {
    // There are probably other programs that could be used here:
    // Vic Cedar's "variant" lists, the different C4/C4X lists, etc.
    BASIC, MAINSTREAM, PLUS, A1, A2, C1, C2, C3A, C3B, C4;
    /**
     * Implement an ordering on programs.  Right now this is the same as
     * the ordinal numbering for the enumeration, but when we implement
     * 'variants', then the variant lists should split off (so that C1V
     * includes C1 and A2V, but C1 doesn't include A2V).   Best way to
     * implement this (again, eventually) is to have each class indicate
     * what the class directly below it is, and recursively define inclusion.
     * @doc.test
     *  js> Program.C3A.includes(Program.C1)
     *  true
     *  js> Program.MAINSTREAM.includes(Program.A1)
     *  false
     *  js> [Program.BASIC.includes(p) for each (p in Program.values())]
     *  true,false,false,false,false,false,false,false,false,false
     *  js> [Program.C3B.includes(p) for each (p in Program.values())]
     *  true,true,true,true,true,true,true,true,true,false
     */
    public boolean includes(Program p) {
        return this.ordinal() >= p.ordinal();
    }
    public String toTitleCase() {
        return toString().toUpperCase().charAt(0)+
            toString().toLowerCase().substring(1);
    }
}
