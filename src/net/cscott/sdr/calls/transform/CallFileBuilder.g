// Build a 'proper' Call AST from the parse tree
header {
package net.cscott.sdr.calls.transform;

import net.cscott.sdr.calls.Rotation;
import net.cscott.sdr.calls.ast.*;
import net.cscott.sdr.util.*;
import java.util.*;
}
// @@startparser
/** CallFileBuilder builds a proper AST from the parse tree. */
class CallFileBuilder extends TreeParser;
options { importVocab = CallFileParser; defaultErrorHandler=false; }
{
	private final Map<String,Comp> callMap = new HashMap<String,Comp>();
	private final Map<String,String> programMap = new HashMap<String,String>();
	public Map<String,Comp> getMap() { return callMap; } // XXX for debugging.
	String currentProgram = null;
}
    
// @@startrules
calllist
	: #(CALLLIST (program)* )
	;

program
	: #(PROGRAM id:IDENT { currentProgram=id.getText(); } (def)* )
	;

def
{ String n; Comp c; }
	: #(DEF n=simple_words {System.err.println(n);} c=pieces)
	{ assert !callMap.containsKey(n) : "duplicate call: "+n;
	  callMap.put(n, c);
	  programMap.put(n, currentProgram);
	}
	;
	
pieces returns [Comp r]
	: r=opt
	| r=seq
	| r=par
	| r=res ;
	
opt returns [Opt o]
{ OptCall oc; List<OptCall> l = new ArrayList<OptCall>(); }
	: #(OPT (oc=one_opt {l.add(oc);})+)
	{ o = new Opt(l.toArray(new OptCall[l.size()])); }
	;
one_opt returns [OptCall oc] {List<String> f; Comp co; }
	: #(FROM f=simple_body co=pieces)
	{ oc = new OptCall(f, co); }
	;
seq returns [Seq s]
{ SeqCall sc; List<SeqCall> l = new ArrayList<SeqCall>(); }
	: #(SEQ (sc=one_seq {l.add(sc);})+)
	{ s = new Seq(l.toArray(new SeqCall[l.size()])); }
	;
one_seq returns [SeqCall r]
{ Fraction x, y; Comp d; }
	: #(PRIM x=number y=number dir:IDENT)
	{ r = new Prim(x, y, Rotation.fromRelativeString(dir.getText()), Fraction.ONE); }
	| #(CALL body)
	{ r = new Sub(null/*XXX*/); }
	| #(PART d=pieces)
	{ r = new Part(d); }
	;

par returns [Par p] {ParCall pc;List<ParCall> l=new ArrayList<ParCall>();}
    : #(PAR (pc=one_par {l.add(pc);})+)
	{ p = new Par(l.toArray(new ParCall[l.size()])); }
    ;

one_par returns [ParCall pc]
{ List<String> sl; Comp d; }
    : #(SELECT sl=simple_body d=pieces)
	{ pc = new ParCall(sl, d); }
	;
// restrictions/timing
res returns [Comp c] { Fraction f; }
    : #(IN f=number c=pieces)
	{ c = new In(f, c); }
    | #(CONDITION body c=pieces)
	{ c = new If("XXX", c); }
    ;
	
simple_words returns [String r]
{
  StringBuilder sb = new StringBuilder();
  String s;
}
	: #(ITEM s=simple_word {sb.append(s);}
	        (s=simple_word {sb.append(' ');sb.append(s);})* )
      { r = sb.toString(); }
	;
simple_word returns [String r] { Fraction n; }
	: i:IDENT { r = i.getText(); }
	| n=number { r = n.toString(); }
	;

simple_body returns [List<String> l] { String s; l = new ArrayList<String>(); }
	: #(BODY (s=simple_words {l.add(s);} )+)
	;

words
	: #(ITEM (word)+ )
	;
word
	: IDENT
	| number
	| body
	;
body returns [AST ast]
	: #(BODY (words)+)
	{ ast=#body; }
	;

number returns [Fraction r]
	: n:NUMBER
	{ r = Fraction.valueOf(n.getText()); }
	;
	
// @@endrules

// @@endparser
