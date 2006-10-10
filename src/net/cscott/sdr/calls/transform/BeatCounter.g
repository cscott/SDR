// Remove 'In's from a call tree by pushing them down and adjusting Prim timing
// Step 1: Compute the 'inherent' timing of the subtrees; this will be used
//         to proportionally allocate the number of beats we will be given.
header {
package net.cscott.sdr.calls.transform;

import net.cscott.sdr.calls.ast.*;
import net.cscott.sdr.util.*;
import java.util.*;
}
// @@walker
/** Propage 'inherent' time bottom-up: where prim and part = 1, and IN resets
 * to its spec, whatever that is. */
class BeatCounter extends TreeParser;
options { importVocab = CallFileParser; }
{
	public final Map<AST,Fraction> inherent = new HashMap<AST,Fraction>();
	// shorthand for registering the timing of an AST node
	private void r(AST ast, Fraction f) { inherent.put(ast, f); }
	// Math.max for Fractions.
	private Fraction max(Fraction a, Fraction b) {
		return (a.compareTo(b) < 0) ? b : a;
	}
	public Fraction getBeats(AST ast) { return inherent.get(ast); }
}
    
// @@startrules
pieces returns [Fraction f=null;] // call definitions start with pieces
	: f=seq { r(#pieces, f); }
	| f=par { r(#pieces, f); }
	| f=res { r(#pieces, f); }
	;
seq returns [Fraction f=Fraction.ZERO;] { Fraction s; }
	: #(SEQ (s=one_seq {f=f.add(s);})+) { r(#seq, f); }
	;
one_seq returns [Fraction f=Fraction.ONE; /* default timing */]
	: PRIM { r(#one_seq,f); }
	// Calls shouldn't really be present at this stage.
	| #(PART pieces) { r(#one_seq,f); } // note we ignore length of pieces
	;
par returns [Fraction f=Fraction.ZERO;] { Fraction p; }
    : #(PAR (p=one_par {f=max(f,p);})+) { r(#par, f); }
    ;
one_par returns [Fraction f=null;]
    : #(SELECT f=pieces) { r(#one_par, f); }
	;
// restrictions/timing
res returns [Fraction f=null;]
    : #(IN pieces) { f = ((In)#res).count; r(#res, f); }// ignore pieces length
    ;
		
// @@endrules

// @@endwalker
