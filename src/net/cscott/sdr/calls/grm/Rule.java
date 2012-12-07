package net.cscott.sdr.calls.grm;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

import net.cscott.sdr.util.Fraction;

/** Grammar rule: a right-hand side, left-hand side,
 *  a precedence level, and a set of {@link Option}s. */
public class Rule {
    public final String lhs;
    public final Grm rhs;
    public final Fraction prec; // precedence level
    public final Set<Option> options;
    
    public Rule(String lhs, Grm rhs, Fraction prec,
                Collection<Option> options) {
        this.lhs = lhs;
        this.rhs = rhs;
        this.prec = prec;
        this.options = EnumSet.noneOf(Option.class);
        if (options!=null) { this.options.addAll(options); }
    }
    // helper constructor
    public Rule(String lhs, Grm rhs, Fraction prec, Option... options) {
        this(lhs, rhs, prec, Arrays.asList(options));
    }
    
    public String toString() {
        return lhs+" -> "+rhs+(prec==null?"":" // prec "+prec.toProperString());
    }

    /** Rule options: these describe variants of the rule which should
     *  also be constructed and/or how it fits into the larger grammar. */
    public enum Option {
        /** Call can be preceded by "left" */
        LEFT,
        /** Call can be preceded by "reverse" */
        REVERSE,
        /** Call is a concept (rhs suffix should be <anything>) */
        CONCEPT,
        /** Call is a supercall (rhs suffix should be <anything>) */
        SUPERCALL,
        /** Call is a metaconcept (rhs suffix should be <concept> <anything>) */
        METACONCEPT;
	/**
	 * Normalize an {@link Rule.Option} name: convert to uppercase, and
	 * convert dashes to underscores.
	 */
	public static String canon(String s) {
	    return s.toUpperCase().replace('-','_');
	}
    }
}
