// Basic Call Definition AST walker; intended for use in derived walkers.
header {
package net.cscott.sdr.calls.transform;

import net.cscott.sdr.util.*;
import java.util.*;
}
// @@startwalker
/** <code>Walker</code> walks a Call AST, doing nothing; it is intended
 * for use as a base class for derived ast walkers. */
class Walker extends TreeParser;
options { importVocab = Ast; defaultErrorHandler=false; }
    
// @@startrules
pieces // call definitions start with pieces
	: opt
	| seq
	| par
	| res
	;
opt
	: #(OPT (one_opt)+)
	;
one_opt
	: #(FROM pieces)
	;
seq
	: #(SEQ (one_seq)+)
	;
one_seq
	: PRIM
	| CALL
	| #(PART pieces)
	;
par
    : #(PAR (one_par)+)
    ;
one_par
    : #(SELECT pieces)
	;
// restrictions/timing
res
    : #(IN pieces)
    | #(IF pieces)
    ;
		
// @@endrules

// @@endwalker
