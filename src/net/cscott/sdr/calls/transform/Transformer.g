header {
package net.cscott.sdr.calls.transform;

import net.cscott.sdr.calls.BadCallException;
import net.cscott.sdr.calls.ast.*;
import net.cscott.sdr.util.*;
import java.util.*;
}
// @@startwalker
/** {@link Transformer} implements the basic structure of a
 * CALL AST tree transformer, including basic error handling.
 * It is intended to be used in subclasses (as it is, it makes
 * no changes to the tree).
 */
class Transformer extends TreeParser;
options { importVocab = Ast; defaultErrorHandler=false; }
    
// @@startrules
pieces returns [Comp c] // call definitions start with pieces
	: c=opt
	| c=seq
	| c=par
	| c=res
	;
opt returns [Opt o] {OptCall oc;List<OptCall> l=new ArrayList<OptCall>(); }
	: #(OPT ( one_opt_handler[l] )+)
	{ if (l.isEmpty()) throw new BadCallException("none of the options are valid");
	  o = ((Opt)#opt).build(l); }
	;
one_opt_handler[List<OptCall> l] { OptCall oc; }
	: oc=one_opt { l.add(oc); }
	; exception
	catch [BadCallException bce] {
		/* ignore (ie, don't add to l) */
	}
one_opt returns [OptCall oc] { Comp c; }
	: #(FROM c=pieces)
	{ oc=(OptCall)#one_opt; oc = oc.build(oc.getSelectors(), c); }
	;
seq returns [Seq s] {SeqCall sc;List<SeqCall> l=new ArrayList<SeqCall>(); }
	: #(SEQ ( one_seq_handler[l] )+)
	{ assert !l.isEmpty(); s = ((Seq)#seq).build(l); }
	;
one_seq_handler[List<SeqCall> l] { SeqCall sc; }
	: sc=one_seq { l.add(sc); }
	; // if one seq is bad in a list, they all are:
	  // don't trap/handle exception.
one_seq returns [SeqCall sc]
	: sc=prim
	| sc=call_body
	| sc=part
	;
prim returns [SeqCall sc]
	: PRIM
	{ Prim p=(Prim)#prim; sc = p.build(p.x,p.y,p.rot,p.time); }
	;
part returns [SeqCall sc] { Comp c; }
	: #(PART c=pieces)
	{ Part p=(Part)#part; sc = p.build(p.isDivisible, c); }
	;
	
par returns [Par p] {List<ParCall> l=new ArrayList<ParCall>(); }
    : #(PAR (one_par_handler[l])+)
	{ if (l.isEmpty()) throw new BadCallException("no selects are valid");
	  p = ((Par)#par).build(l);
	}
    ;
one_par_handler[List<ParCall> l] { ParCall pc; }
	: pc=one_par { l.add(pc); }
	; exception
	catch [BadCallException bce] {
		/* ignore (ie, don't add to l) */
	}
one_par returns [ParCall p] { Comp c; }
    : #(SELECT c=pieces)
    { p = ((ParCall)#one_par);
      p = p.build(p.tags, c); }
	;
// restrictions/timing
res returns [Comp c]
    : c=in
    | c=cond
    ;
in returns [In in] { Comp c; }
	: #(IN c=pieces)
	{ in = (In) #in; in = in.build(in.count, c); }
	;
cond returns [If cond] { Condition c; Comp p; }
	: #(IF c=cond_body p=pieces)
	{ cond = ((If)#cond).build(c, p); }
	;

call_body returns [Apply a=null] { List<Apply> args; }
	: #(APPLY args=call_args )
	{ a=(Apply)#call_body; a = a.build(a.callName, args); }
	;
call_args returns [List<Apply> l] { l = new ArrayList<Apply>(); Apply c; }
	: (c=call_body {l.add(c);} )*
	;
cond_body returns [Condition c=null] { List<Condition> args; }
	: #(CONDITION args=cond_args )
	{ c=(Condition)#cond_body; c=c.build(c.predicate, args); }
	;
cond_args returns [List<Condition> l] { l = new ArrayList<Condition>(); Condition c; }
	: (c=cond_body {l.add(c);} )*
	;

		
// @@endrules

// @@endwalker
