// Apply a call to a formation to get a set of dancer paths.
header {
package net.cscott.sdr.calls;

import net.cscott.sdr.util.*;
import java.util.*;
}
// @@startparser
/** CallApply takes a Def tree and a Formation and creates
 * DancerActions. */
class CallApply extends TreeParser;
options { importVocab = CallFileParser; }
    
// @@startrules
def
	: #(DEF callname pieces)
	;
	
pieces
	: opt | seq | par | res ;
	
opt
	: #(OPT (one_opt)+)
	;
one_opt
	: #(FROM body pieces)
	;
seq
	: #(SEQ (one_seq)+)
	;
one_seq
	: #(PRIM number number IDENT)
	| #(CALL body)
	| #(PART pieces)
	;

par
    : #(PAR (one_par)+)
    ;

one_par
    : #(SELECT body pieces)
	;
// restrictions/timing
res
    : #(IN number pieces)
    | #(CONDITION body pieces)
    ;
	
callname returns [String r]
{
  StringBuilder sb = new StringBuilder();
  String s;
  r = "";
}
	: #(ITEM s=simple_word {sb.append(s);}
	        (s=simple_word {sb.append(' ');sb.append(s);})* )
      { r = sb.toString(); }
	;
simple_word returns [String r] { Fraction n; r = ""; }
	: i:IDENT { r = i.getText(); }
	| n=number { r = n.toString(); }
	;

words returns [List<String> l] { String s; l = new ArrayList<String>(); }
	: #(ITEM (s=word { l.add(s); })+ )
	;
word returns [String r] { Fraction n; List<String> b; r = ""; }
	: i:IDENT
	{ r = i.getText(); }
	| n=number
	{ r = n.toString(); }
	| b=body
	{ r = b.toString(); }
	;
body returns [List<String> l] { l = null; }
	: #(BODY (words)+)
	;

number returns [Fraction r] { r = null; }
	: n:NUMBER
	{ r = Fraction.valueOf(n.getText()); }
	;
	
// @@endrules

// @@endparser
