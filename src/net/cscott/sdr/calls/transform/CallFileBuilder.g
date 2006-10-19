// Build a 'proper' Call AST from the parse tree
header {
package net.cscott.sdr.calls.transform;

import static net.cscott.sdr.calls.transform.BuilderHelper.*;
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
{ String n; B<? extends Comp> c; }
	: #(DEF n=simple_words c=pieces)
	{ assert !names.contains(n) : "duplicate call: "+n;
      names.add(n);
      Call call = makeCall(n.intern(), currentProgram, c);
	  db.add(call);
	}
	;
	
pieces returns [B<? extends Comp> r]
	: r=opt
	| r=seq
	| r=par
	| r=res ;
	
opt returns [B<Opt> o=null]
{ B<OptCall> oc; List<B<OptCall>> l = new ArrayList<B<OptCall>>(); }
	: #(OPT (oc=one_opt {l.add(oc);})+)
	{ o = mkOpt(l); }
	;
one_opt returns [B<OptCall> oc=null] {List<String> f; B<? extends Comp> co; }
	: #(FROM f=simple_body co=pieces)
	{ oc = mkOptCall(OptCall.parseFormations(f), co); }
	;
seq returns [B<Seq> s=null]
{ B<? extends SeqCall> sc; List<B<? extends SeqCall>> l = new ArrayList<B<? extends SeqCall>>(); }
	: #(SEQ (sc=one_seq {l.add(sc);})+)
	{ s = mkSeq(l); }
	;
one_seq returns [B<? extends SeqCall> sc=null]
{ Fraction x, y; B<? extends Comp> d;
  Prim.Direction dx=null, dy=null, dr=null; Rotation r=null;
}
	: #(PRIM (dx=direction)? x=number (dy=direction)? y=number (dr=direction | r=rotation) )
	{ sc=mkPrim(d(dx), x, d(dy), y, d(dr), ifNull(r,Rotation.ONE_QUARTER)); }
	| #(CALL sc=call_body)
	| #(PART d=pieces)
	{ sc = mkPart(true, d); /* divisible part */}
	| #(IPART d=pieces)
	{ sc = mkPart(false, d); /* indivisible part */}
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

par returns [B<Par> p=null] {B<ParCall> pc;List<B<ParCall>> l=new ArrayList<B<ParCall>>();}
    : #(PAR (pc=one_par {l.add(pc);})+)
	{ p = mkPar(l); }
    ;

one_par returns [B<ParCall> pc=null]
{ List<String> sl; B<? extends Comp> d; }
    : #(SELECT sl=simple_body d=pieces)
	{ pc = mkParCall(ParCall.parseTags(sl), d); }
	;
// restrictions/timing
res returns [B<? extends Comp> c] { Fraction f; B<Condition> cd; }
    : #(IN f=number c=pieces)
	{ c = mkIn(f, c); }
    | #(IF cd=cond_body c=pieces)
	{ c = mkIf(cd, c); }
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

call_body returns [B<Apply> ast=null] {String s; List<B<Apply>> args; Fraction n;}
	// shorthand: 3/4 (foo) = fractional(3/4, foo)
	: ( #(APPLY #(ITEM number) (.)* ) ) =>
	  #(APPLY #(ITEM n=number) args=call_args )
	{   args.add(0, mkConstant(Apply.makeApply(n.toString().intern())));
		ast = mkApply("_fractional", args); }
	// standard rule
	| #(APPLY s=simple_words args=call_args )
	{ ast = mkApply(s.intern(), args); }
	;
call_args returns [List<B<Apply>> l] { l = new ArrayList<B<Apply>>(); B<Apply> c; }
	: (c=call_body {l.add(c);} )*
	;
cond_body returns [B<Condition> c=null] { String s; List<B<Condition>> args; }
	: #(CONDITION s=simple_words args=cond_args )
	{ c = mkCondition(s.intern(), args); }
	;
cond_args returns [List<B<Condition>> l] { l = new ArrayList<B<Condition>>(); B<Condition> c; }
	: (c=cond_body {l.add(c);} )*
	;

number returns [Fraction r=null]
	: n:NUMBER
	{ r = Fraction.valueOf(n.getText()); }
	;
	
// @@endrules

// @@endparser
