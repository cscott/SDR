// Build a 'proper' Call AST from the parse tree
header {
package net.cscott.sdr.calls.transform;

import net.cscott.sdr.calls.*;
import net.cscott.sdr.calls.ast.*;
import net.cscott.sdr.util.*;
import java.util.*;
}
// @@startparser
/** CallFileBuilder builds a proper AST from the parse tree. */
class CallFileBuilder extends TreeParser;
options { importVocab = CallFileParser; defaultErrorHandler=false; }
{
	private final Set<String> names = new HashSet<String>();
	private final List<Call> db = new ArrayList<Call>();
	public List<Call> getList() { return Collections.unmodifiableList(db); }
	Program currentProgram = null;
	// quick helper
	public <T> T ifNull(T t, T otherwise) { return (t==null)?otherwise:t; }
	public Prim.Direction d(Prim.Direction d) { return ifNull(d, Prim.Direction.ASIS); }
}
    
// @@startrules
calllist
	: #(CALLLIST (program)* )
	;

program
	: #(PROGRAM id:IDENT { currentProgram=Program.valueOf(id.getText().toUpperCase()); } (def)* )
	;

def
{ String n; Comp c; }
	: #(DEF n=simple_words c=pieces)
	{ assert !names.contains(n) : "duplicate call: "+n;
      names.add(n);
      db.add(Call.makeSimpleCall(n.intern(), currentProgram, c));
	}
	;
	
pieces returns [Comp r]
	: r=opt
	| r=seq
	| r=par
	| r=res ;
	
opt returns [Opt o=null]
{ OptCall oc; List<OptCall> l = new ArrayList<OptCall>(); }
	: #(OPT (oc=one_opt {l.add(oc);})+)
	{ o = new Opt(l.toArray(new OptCall[l.size()])); }
	;
one_opt returns [OptCall oc=null] {List<String> f; Comp co; }
	: #(FROM f=simple_body co=pieces)
	{ oc = new OptCall(f, co); }
	;
seq returns [Seq s=null]
{ SeqCall sc; List<SeqCall> l = new ArrayList<SeqCall>(); }
	: #(SEQ (sc=one_seq {l.add(sc);})+)
	{ s = new Seq(l.toArray(new SeqCall[l.size()])); }
	;
one_seq returns [SeqCall sc=null]
{ Fraction x, y; Comp d;
  Prim.Direction dx=null, dy=null, dr=null; Rotation r=null;
}
	: #(PRIM (dx=direction)? x=number (dy=direction)? y=number (dr=direction | r=rotation) )
	{ sc=new Prim(d(dx), x, d(dy), y, d(dr), ifNull(r,Rotation.ONE_QUARTER)); }
	| #(CALL sc=call_body)
	| #(PART d=pieces)
	{ sc = new Part(true, d); /* divisible part */}
	| #(IPART d=pieces)
	{ sc = new Part(false, d); /* indivisible part */}
	;

direction returns [Prim.Direction d=null]
	: IN { d=Prim.Direction.IN; }
	| OUT { d=Prim.Direction.OUT; }
	;
rotation returns [Rotation r=null]
	: RIGHT { r = Rotation.ONE_QUARTER; }
	| LEFT { r = Rotation.mONE_QUARTER; }
	| NONE { r = Rotation.ZERO; }
	;

par returns [Par p=null] {ParCall pc;List<ParCall> l=new ArrayList<ParCall>();}
    : #(PAR (pc=one_par {l.add(pc);})+)
	{ p = new Par(l.toArray(new ParCall[l.size()])); }
    ;

one_par returns [ParCall pc=null]
{ List<String> sl; Comp d; }
    : #(SELECT sl=simple_body d=pieces)
	{ pc = new ParCall(sl, d); }
	;
// restrictions/timing
res returns [Comp c] { Fraction f; Condition cd; }
    : #(IN f=number c=pieces)
	{ c = new In(f, c); }
    | #(IF cd=cond_body c=pieces)
	{ c = new If(cd, c); }
    ;
	
simple_words returns [String r=null]
{
  StringBuilder sb = new StringBuilder();
  String s;
}
	: #(ITEM s=simple_word {sb.append(s);}
	        (s=simple_word {sb.append(' ');sb.append(s);})* )
      { r = sb.toString(); }
	;
simple_word returns [String r=null] { Fraction n; }
	: i:IDENT { r = i.getText(); }
	| n=number { r = n.toProperString(); }
	;

simple_body returns [List<String> l] { String s; l = new ArrayList<String>(); }
	: #(BODY (s=simple_words {l.add(s);} )+)
	;

call_body returns [Apply ast=null] {String s; List<Apply> args; Fraction n;}
	// shorthand: 3/4 (foo) = fractional(3/4, foo)
	: ( #(APPLY #(ITEM number) (.)* ) ) =>
	  #(APPLY #(ITEM n=number) args=call_args )
	{   args.add(0, Apply.makeApply(n.toString().intern()));
		ast = new Apply("_fractional", args); }
	// standard rule
	| #(APPLY s=simple_words args=call_args )
	{ ast = new Apply(s.intern(), args); }
	;
call_args returns [List<Apply> l] { l = new ArrayList<Apply>(); Apply c; }
	: (c=call_body {l.add(c);} )*
	;
cond_body returns [Condition c=null] { String s; List<Condition> args; }
	: #(CONDITION s=simple_words args=cond_args )
	{ c = new Condition(s.intern(), args); }
	;
cond_args returns [List<Condition> l] { l = new ArrayList<Condition>(); Condition c; }
	: (c=cond_body {l.add(c);} )*
	;

number returns [Fraction r=null]
	: n:NUMBER
	{ r = Fraction.valueOf(n.getText()); }
	;
	
// @@endrules

// @@endparser
