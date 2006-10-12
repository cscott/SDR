// Remove 'In's from a call tree by pushing them down and adjusting Prim timing
// Step 2: Using the 'inherent' timings given by a BeatCounter, proportionally
//         allocate beats top-down.
header {
package net.cscott.sdr.calls.transform;

import net.cscott.sdr.calls.ast.*;
import net.cscott.sdr.util.*;
import java.util.*;
}
// @@walker
/** Propage 'inherent' time bottom-up: where prim and part = 1, and IN resets
 * to its spec, whatever that is. */
class RemoveIn extends TreeParser;
options { importVocab = Ast; defaultErrorHandler=false; }
{
	private BeatCounter bc;
	RemoveIn(BeatCounter bc) { this(); setBeatCounter(bc); }
	void setBeatCounter(BeatCounter bc) { this.bc = bc; }

	/** Main method: pass in a comp, and get out a Comp without In nodes. */
	public static Comp removeIn(Comp c) {
		try {
			BeatCounter bc = new BeatCounter();
			bc.pieces(c);
			RemoveIn ri = new RemoveIn(bc);
			return ri.pieces(c, null);
		} catch (RecognitionException re) {
			throw new RuntimeException(re); // shouldn't happen!
		}
	}

	// helper methods, to reuse old tree if unmodified.
	private Seq build(Seq old, List<SeqCall> l) {
		if (compare(old, l)) return old;
		return new Seq(l.toArray(new SeqCall[l.size()]));
	}
	private Par build(Par old, List<ParCall> l) {
		if (compare(old, l)) return old;
		return new Par(l.toArray(new ParCall[l.size()]));
	}
	private ParCall build(ParCall old, Comp c) {
		if (compare(old, c)) return old;
		return new ParCall(old.tags, c);
	}
	private <T extends AST> boolean compare(AST ast, T child) {
		return compare(ast, Collections.singletonList(child));
	}
	private <T extends AST> boolean compare(AST ast, List<T> l) {
		if (ast.getNumberOfChildren() != l.size()) return false;
		AST child = ast.getFirstChild();
		for (T t: l) {
			if (t != child) return false; // reference equality
			child = child.getNextSibling();
		}
		return true;
	}			
}
    
// @@startrules
pieces[Fraction f] returns [Comp c] // call definitions start with pieces
	: c=seq[f]
	| c=par[f]
	| c=res[f]
	;
seq[Fraction f] returns [Seq s]
{ SeqCall sc; List<SeqCall> l=new ArrayList<SeqCall>(); }
	: #(SEQ 
		{ assert f != null : "Seq reached without passing outer In";
		  Fraction old = bc.getBeats(#seq);
		  Fraction scale = f.divide(old);   }
		(sc=one_seq[scale] {l.add(sc);})+)
		{ s = build((Seq)#seq, l); }
	;
one_seq[Fraction scale] returns [SeqCall sc] { Comp c; }
	: PRIM
	{ /* apply scaling factor to prim */
	  sc = ((Prim)#one_seq).scale(scale); // will return same object if scale=1
	}
	// Calls shouldn't really be present at this stage.
	| #(PART 
		{ /* get old inherent length of pieces */
		  Part part = (Part) #one_seq;
	      Fraction old = bc.getBeats(part.getFirstChild());
		  /* scale this to get new length */
		  Fraction f = old.multiply(scale);
		}
		c=pieces[f])
		{ sc = part.build(part.isDivisible, c); }
	;
// pass timing straight down Par: this will cause all sections of the
// par to finish at the same time; revisit this (make it more like Seq
// if that turns out not to be the right thing to do).
par[Fraction f] returns [Par p]
{ ParCall pc; List<ParCall> l=new ArrayList<ParCall>(); }
    : #(PAR (pc=one_par[f] {l.add(pc);})+)
	{ p = build((Par)#par, l); }
    ;
one_par[Fraction f] returns [ParCall pc] { Comp c; }
    : #(SELECT c=pieces[f])
	{ pc = build((ParCall)#one_par, c); }
	;
// restrictions/timing
res[Fraction f] returns [Comp c]
    : #(IN 
    	{ if (f==null) f=((In)#res).count; } // only the outer In matters
	    c=pieces[f]) // note that this removes the 'In' node.
    ;
		
// @@endrules

// @@endwalker
