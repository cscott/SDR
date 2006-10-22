// Build a 'proper' Call AST from the parse tree
header {
package net.cscott.sdr.calls.transform;

import static net.cscott.sdr.calls.transform.BuilderHelper.*;
import net.cscott.sdr.calls.*;
import net.cscott.sdr.calls.ast.*;
import net.cscott.sdr.calls.grm.Grm;
import net.cscott.sdr.calls.grm.Rule;
import net.cscott.sdr.calls.grm.SimplifyGrm;
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
	private Map<String,Integer> scope = new HashMap<String,Integer>();
	private void semex(AST a, String s) throws SemanticException {
		throw new SemanticException(s, "<unknown>", a.getLine(), a.getColumn());
	}
}
    
// @@startrules
// start production for parsing call file.
calllist
	: #(CALLLIST (program)* )
	;
// start production for parsing grammar rules
grammar_start returns [Grm g]
	: g=grm_rule
	;


program
	: #(PROGRAM id:IDENT { currentProgram=Program.valueOf(id.getText().toUpperCase()); } (def)* )
	;

def
{ String n=null; B<? extends Comp> c; B<Apply> cb; Apply a=null; 
  Set<String> optional = new HashSet<String>(); Fraction prec=null;
  Grm g=null;
}
	: #(d:DEF cb=call_body
	{ if (!cb.isConstant()) semex(d, "Bad call definition");
	  a = cb.build(null);
	  n = a.callName;
	  // if there are arguments, add them to our scope.
	  int i=0;
	  for (Apply arg : a.args) {
	    if (arg.args.size()!=0) semex(d, "Arguments can't have arguments");
	    scope.put(arg.callName, i++);
	  }
	}
       ( #(OPTIONAL (id:IDENT {optional.add(id.getText().toUpperCase());})+ ) )?
       ( #(SPOKEN (prec=number)? g=grm_rule ) )?
	   c=pieces)
	{ if (names.contains(n)) semex(d, "duplicate call: "+n);
      n = n.intern();
      names.add(n);

	  String ruleName = optional.contains("LEFT") ? "leftable_anything" :
	  	optional.contains("REVERSE") ? "reversable_anything" :
	  	"anything";
	  Rule rule = null;
	  if (g==null && !n.startsWith("_"))
	    g = Grm.mkGrm(n.split("\\s+"));
	  if (g!=null)
	    rule = new Rule(ruleName, SimplifyGrm.simplify(g),
						prec==null ? Fraction.ZERO : prec);

      Call call = makeCall(n, currentProgram, c, a.args.size(), rule);
	  db.add(call);

	  scope.clear();
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
{ List<B<String>> sl; B<? extends Comp> d; }
    : #(SELECT sl=simple_ref_body d=pieces)
	{ pc = mkParCall(sl, d); }
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

words_or_ref returns [B<String> b=null] { String s; int r; }
	: s=simple_words
	{ b = mkConstant(s); }
	| r=ref
	{ final int param = r;
	  b = new B<String>() {
	  	public String build(List<Apply> args) {
	  	  assert args.get(param).args.isEmpty();
          return args.get(param).callName;
	  	}
	  };
	}
	;

simple_ref_body returns [List<B<String>> l] { B<String> s; l = new ArrayList<B<String>>(); }
    : #(BODY (s=words_or_ref {l.add(s);} )+)
    ;

call_body returns [B<Apply> ast=null] {String s; List<B<Apply>> args; Fraction n; int r; }
	// shorthand: 3/4 (foo) = fractional(3/4, foo)
	: ( #(APPLY #(ITEM number) (.)* ) ) =>
	  #(APPLY #(ITEM n=number) args=call_args )
	{   args.add(0, mkConstant(Apply.makeApply(n.toString().intern())));
		ast = mkApply("_fractional", args); }
	// parameter reference
	| ( #(APPLY REF (.)* ) ) => 
	  #(APPLY r=ref args=call_args )
	{ final int param = r;
	  final List<B<Apply>> call_args = args;
	  if (call_args.isEmpty()) {
	  	// if no args, then substitute given Apply node wholesale.
	    ast = new B<Apply>() {
	    	public Apply build(List<Apply> args) {
	    		return args.get(param);
	    	}
	    };
	  } else {
	  	// otherwise, just use the given parameter as a string.
	    ast = new B<Apply>() {
	    	public Apply build(List<Apply> args) {
				assert args.get(param).args.isEmpty();
	    		String callName = args.get(param).callName;
	    		return new Apply(callName, reduce(call_args, args));
	    	}
	    };
	  }
	}
	// standard rule
	| #(APPLY s=simple_words args=call_args )
	{ ast = mkApply(s.intern(), args); }
	;
ref returns [int v=0]
	: r:REF
	{ if (!scope.containsKey(r.getText())) semex(r, "No argument named "+r.getText());
	  v=scope.get(r.getText()); }
	;
call_args returns [List<B<Apply>> l] { l = new ArrayList<B<Apply>>(); B<Apply> c; }
	: (c=call_body {l.add(c);} )*
	;
cond_body returns [B<Condition> c=null] { String s; List<B<Condition>> args; int r; }
	// parameter reference
	: ( #(CONDITION REF (.)* ) ) => 
	  #(CONDITION r=ref args=cond_args )
	{ final int param = r;
	  final List<B<Condition>> cond_args = args;
	  // use the given parameter as a string.
	  c = new B<Condition>() {
	    	public Condition build(List<Apply> args) {
				assert args.get(param).args.isEmpty();
	    		String predicate = args.get(param).callName;
	    		return new Condition(predicate, reduce(cond_args, args));
	    	}
	  };
	}
	| #(CONDITION s=simple_words args=cond_args )
	{ c = mkCondition(s.intern(), args); }
	;
cond_args returns [List<B<Condition>> l] { l = new ArrayList<B<Condition>>(); B<Condition> c; }
	: (c=cond_body {l.add(c);} )*
	;

number returns [Fraction r=null]
	: n:NUMBER
	{ r = Fraction.valueOf(n.getText()); }
	;
	
grm_rule returns [Grm g=null] { List<Grm> l = new ArrayList<Grm>(); Integer p=null;}
	: #(VBAR (g=grm_rule {l.add(g);})+ )
	{ g = new Grm.Alt(l); }
	| #(ADJ (g=grm_rule {l.add(g);})+ )
	{ g = new Grm.Concat(l); }
	| #(PLUS g=grm_rule )
	{ g = new Grm.Mult(g, Grm.Mult.Type.PLUS); }
	| #(STAR g=grm_rule )
	{ g = new Grm.Mult(g, Grm.Mult.Type.STAR); }
	| #(QUESTION g=grm_rule )
	{ g = new Grm.Mult(g, Grm.Mult.Type.QUESTION); }
	| i:IDENT
	{ g = new Grm.Terminal(i.getText()); }
	| #(REF r:IDENT (p=grm_ref_or_int)? )
	{ g = new Grm.Nonterminal(r.getText(), p==null ? -1 : p); }
	;
grm_ref_or_int returns [Integer i=null]
	: p:IDENT
	{ if (!scope.containsKey(p.getText()))
        semex(p, "No argument named "+p.getText());
      i=scope.get(p.getText()); }
	| n:INTEGER { i=Integer.valueOf(n.getText()); }
	;

// @@endrules

// @@endparser
