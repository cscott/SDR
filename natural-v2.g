header {
	package net.cscott.sdr.calls.transform;
	import java.util.*;
	import net.cscott.sdr.calls.ast.*;
}
/** "Natural language" grammar for calls. */

class NaturalParser extends Parser;
options {
	k=2;
}

statement
	: prefix statement
	| call (options{greedy=true;}:"and" call)* (options{greedy=true;}: suffix )*
	;
call
	: ms_call
	;
prefix
	: ms_prefix
	;
suffix
	: ms_suffix
	;
	
ms_prefix
	: "left" | "reverse" | people | wave_select
	;
ms_suffix
	: "and" "roll"
	;


ms_call
	: "forward" "and" "back"
	| "dosado" ("to" "a" "wave")?
	| "pass" "thru" 
	| "roll" "away"
	| "u" "turn" "back"
	| "double" "pass" "thru"
	| "half" "sashay"
	| "two" people "chain"
	| "chain" "down" "the" "line"
	| "lead" ("left" | "right")
	| "right" "and" "left" "thru"
	| "star" "thru"
	| "bend" "the" "line"
	| "square" "thru" one_four ("hands" ("around"|"round")?)?
	| "california" "twirl"
	| "wheel" "around"
	| "box" "the" "gnat"
	| "step" "to" "a" "wave"
	| "balance"
	| "pass" "the" "ocean"
	| "step" "thru"
	| "extend"
	| "swing" "thru"
	| ("cross")? ("run"|"fold")
	| "partner" "trade"
	| "couples" "trade"
	| "zoom"
	| "flutter" "wheel"
	| "veer" ("left" | "right")
	| "touch" ("one"|"a") "quarter"
	| ("split"|"box") "circulate"
	| "turn" "thru"
	| "spin" "the" "top"
	| "cast" "off" "three" "quarters"
	| "walk" "and" "dodge"
	//| people "walk" people "dodge"
	| "walk" "others" "dodge"
	| "slide" "thru"
	| "dixie" "style" "to" ("a" "wave"|"an" "ocean" "wave")
	| "tag" "the" "line" ("face" ("left"|"right"))?
	| "half" "tag"
	| "scoot" "back"
	| ("single"|"couples") "hinge"
	| "recycle"
	;
	
people
	: boys | girls | all
	;
boys
	: "boys" | "men" ;
girls
	: "girls" | "ladies";
all
	: "all" | "everyone" | "every" "one" | "every" "body";
wave_select
	: "centers" | "ends"
	;
one_four
	: "one" | "two" | "three" | "four" | "five" ;

class NaturalLexer extends Lexer;
options {
  charVocabulary = '\0'..'\177'; // ascii only
  testLiterals=true;
  caseSensitiveLiterals=false;
}

WS
	: ( ' ' | '\t' )
    { $setType(Token.SKIP); }
    ;
IDENT
  : ('_'|'a'..'z'|'A'..'Z') ('_'|'a'..'z'|'A'..'Z'|'0'..'9'|'-')*
  ;
