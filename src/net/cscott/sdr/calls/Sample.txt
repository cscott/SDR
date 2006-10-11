package net.cscott.sdr.calls;

import java.util.Map;
import java.util.Set;

import net.cscott.sdr.util.Fraction;

public class Sample {

    /* 'calls' are functions from formation->formation.
     * modifiers like 'as couples', 'and roll', and 'and sweep 1/4'
     * are 'call constructors' from call->call. */

    /*
      formation 'breathe' moved people together but does *NOT* create stars.
      a single-file promenade should NOT be made into a diamond or a star.
      
      a formation maps dancers to positions.
      we can do 'create phantom' and 'select' (etc) by maintaining
      dancer->dancer mappings.
      perhaps the dancer->dancer mapping is best specified as a
      transform (rotation,x,y)->(rotation,x,y)
      and we can fetch dancer by x,y
      
      we also need to 'step back' by mapping dancers in this formation to
      dancers in previous formations.
      
      calls are static fields, not enumerations.
      Call is a class mapping formation to formation.
      Concept is a class mapping call to call.
    */
    // issues: positions should include 'n/s' or 'e/w' as a restriction
    //         in addition to completely 'rotation unspecified'
    //  this allows general lines, general tag, etc.
    //  canonicalize positions so that "most" dancers are facing n/s????
    //  calls are either:
    //     primitive calls, or
    //     parallel combinations (concentric, cross-concentric), or
    //     sequential combinations (which can be fractionalized)
    // [some parallel combinations can be fractionalized, as well]
    // we need to expose the creation, so that concepts like 'finally' work.
    //  finally takes a concept and a call, and does all but the last step
    //  of the call, then applies the concept to the last part.
    // write routine to create FASD?
    // ideally, all non-primitive calls can be specified externally.
    //   primitive calls perhaps, too.

    // maintain dance as a parse tree:
    //  (concept left (call swing-through))
    //  (concept finally (concept and-clover (call right-and-left-thru)))
    //   ^^ note here that finally is a concept that takes a concept
    //      the child concept must have an accessible call.
    // concepts and calls have a method 'elaborate' which simplifies one
    //  layer of the parse tree.  this usually requires a (generalized)
    //  formation, in order to select one of several possibilities for the
    //  call. e.g.:
    //  (concept fan (call spin-the-top))
    //   calling elaborate on (call spin-the-top) yields
    //             (seq (call swing) (concentric (<rotate 1/4>)
    //                                           (call cast-three-quarters)))
    //  the fan concept can define a pattern matching function elaborate:
    //    fan(x,f) =
    //      x:concept -> fan(x.elaborate(f))
    //      (seq (call swing) yl) -> (seq yl)
    //      (seq x:primitive-call yl) -> error
    //      (seq x:concept|call yl) -> fan((seq x.elaborate(f) yl)
    //      (par xl) -> (par map fan xl)
    //  There's a 'flatten' operator which uses elaborate to map
    //  all calls to primitives.
    //
    // A SLIGHTLY DIFFERENT TAKE:
    //  instead of requiring elaborate to take a formation, we allow
    //  all calls to return lists of options.  only one option ought to
    //  apply to a given formation.  a concept like fan can discard
    //  any options it doesn't like. ROUGHLY:
    //         
    //     fan x = x in
    //        x:list -> map fan x
    //        (seq x yl) and (remove-concepts x) contains (call swing) -> (seq yl)
    //        (par xl) -> (par map fan xl)
    //        _ -> Invalid // including fan Invalid -> Invalid.
    //
    //  we can add a 'require' concept to map all calls which can't be
    //  performed from the required formation to invalid.
    //  an option list is invalid only if all the options are invalid.
    //  (they can be removed; then this is equiv to 'if the list if empty')
    //  a call(list) c is valid from a given formation f only if
    //    require f c != ()
    //  complexity should be managable if we avoid making cross products
    //  of options; i.e transforming a sequence of options to an
    //  option list of sequences.  [this train of thought suggests MLj]
    //  parsed calls here correspond to CONCEPTS which return options of
    //  concrete calls.  concrete calls must have a method specifying the
    //  [one? multiple?] formations they are valid from.
    //  [maybe only primitive calls have this; all others elaborate and
    //  summarize]

    // ADJUST TIMING is a CONCEPT.  Each primitive call has an associated
    // timing; ADJUST TIMING can create new primitive calls with altered
    // timing (via a delegate?)

    // ought we be able to (within reason) reconstruct complex calls from
    // simple calls?  i.e. if SWING isn't a primitive call [instead it is,
    // say, a (concept centers-and-ends (call partner-trade)) ], can
    // we still define FAN in terms of SWING?
    // [a reasonable answer: no.  we just need to elaborate SWING before
    //  doing the matching.]

    static class Dancer {
	DancerName name; // optionally, a name.
    }
    static class Position {
	/** location */
	Fraction x, y;
	/** Facing direction. */
	Rotation facing; // facing direction.
    }
    static class Formation {
	Map<Dancer,Position> location;
	Set<Dancer> selected;
    }
    static class DancingFormation {
	/** The actual positions of the dancers. */
	Formation formation;
	/** Every formation can be canonicalized. */
	Transform canonicalization;
	/** When true, the dancers should actually end up in canonical
	 *  positions when they are in this formation. */
	boolean doBreath;
    }
    static class Step {
	Fraction count;
	// not every dancer is in this formation.
	Formation formation;
	// all dancers in this formation should be keys in this map.
	Map<Dancer,Step> prevPath;

	static Step makeStep(Step prev, Formation... next, Fraction count);
    }
    static class Call {
	Step dance(Step f);
    }
    static class Concept {
	Call apply(Call c);
    }
    static class Transform {
	Position apply(Position p);
    }


    PathAndTiming castAShadow() {
	// "in 'cast a shadow', centers facing out do a cloverleaf as if
	// they were trailers in a completed double pass-through formation.
	// centers facing in do extend, hinge, extend (?)
	// (unless all are facing in; then do pass in, pass through)
	// ends (forming a tandem) do 1/2 zoom, arm turn 3/4, spread.

	Path centersDo;
	if (formation.select(CENTERS, FACING_OUT).numDancers()>0)
	    centersDo =	inParallel
		(formation.select(CENTERS, FACING_OUT)
		 .createPhantom(COMPLETED_DOUBLE_PASS_THROUGH.select(TRAILERS))
		 .doFully(CLOVERLEAF),
		 formation.select(CENTERS, FACING_IN)
		 .createPhantom(BOX_CIRCULATE).extend().hinge()/*in sequence*/)
		;
	else
	    centersDo = formation.select(CENTERS).require(FACING_COUPLES)
		.passIn().passThrough();
	Path endsDo =
	    formation.select(ENDS).require(TANDEM/*???*/)
	    .doHalf(ZOOM).doThreeQuarters(ARM_TURN).do(SPREAD);

	return inParallel(endsDo, centersDo).in(8)
	    .do(EXTEND).in(2);
    }
    void partnerTrade() { // trade.
	// a partner trade is not two hinges, even though
	// a hinge is half of a trade.
	return startPart().call(HINGE).startPart().call(HINGE).in(2);
    }

    static class Path {
	PathAndTiming in(int counts) { /* XXX */ }
	/** adjust path to add/remove breathing room. */
	// a good place to do this is after each top-level call.
	Path breath() { /* XXX */ }
    }

    enum DancerName {
	CENTERS, ENDS, OUTSIDES, VERY_CENTERS, LEADS, TRAILERS;
	Formation select(Formation f) {
	    assert false; // implement me.
	}
    }
    enum CallPart {
	TRADE, HINGE, CLOVER_LEAF, _AND_ROLL, CLOVER_AND_, TOUCH, LEFT_,
	_AND_SWEEP, FAN_, SPIN_THE_TOP, LOCK_IT, CAST_A_SHADOW;
	// q: how do we write '_AND_SWING 1/4'?  we'd like to do 1/4 just
	//    the last half of the call.  maybe 'doHalf()' on compound
	//    calls just means do the subcall FULLY and do half of the
	//    'extra' stuff.

	/** Identify simple-vs-compound call names. */
	public boolean isCompound() {
	    return name().startsWith("_") || name().endsWith("_");
	}
	/** Convenience method: turns a simple call name into a
	 *  <code>Call</code> object. */
	SimpleCall call() { assert !isCompound(); return new SimpleCall(this);}
	/** Convenience method; creates compound call from a simple call
	 *  and a modifier. Allows syntax like:
	 *  <code>CLOVER_AND_.with(TRADE)</code> and
	 *  <code>TRADE.with(_AND_ROLL)</code>. */
	CompoundCall with(CallPart name) {
	    if (this.isCompound())
		return new CompoundCall(this, name.call());
	    else
		return new CompoundCall(name, this.call());
	}
	/** Convenience method; short for
	 *  <code>new CompoundCall(this,call)</code>.
	 *  Allows syntax like:
	 *  <code>CLOVER_AND_.with(TOUCH.oneQuarter())</code>
	 */
	CompoundCall with(Call call) { return new CompoundCall(this, call); }
	/** Convenience method; short for
	 *  <code>this.call().oneQuarter()</code>. */
	public Call oneQuarter() { return this.call().oneQuarter(); }
	/** Convenience method; short for
	 *  <code>this.call().oneHalf()</code>. */
	public Call oneHalf() { return this.call().oneHalf(); }
	/** Convenience method; short for
	 *  <code>this.call().threeQuarters()</code>. */
	public Call threeQuarters() { return this.call().threeQuarters(); }
    }
    
    static class Call {
	private Call makePartial(int numerator, int denominator) {
	    return new FractionalCall(this, numerator, denominator);
	}
	/** Make a call out of smaller parts. */
	// is TRADE=HINGE.then(HINGE)
	// or is HINGE=TRADE.oneHalf() ?
	public final Call then(Call nextCall) {
	    // something like startPart().this.startPart(next) ??
	    return new SequenceCall(this, nextCall);
	}
	public final Call oneQuarter() { return makePartial(1, 4); }
	public final Call oneHalf() { return makePartial(1, 2); }
	public final Call threeQuarters() { return makePartial(3, 4); }
    }
    static class SimpleCall extends Call {
	CallPart name;
    }
    static class CompoundCall extends Call {
	CallPart name;
	Call subpart;
	/** chain a compound call to allow syntax like:
	 *  CLOVER_AND_.with(TOUCH_1_4).with(_AND_ROLL) */
	
	CompoundCall with(CallPart name) {
	    return new CompoundCall(name, this);
	}
    }
    static class Formation {
	/** Unmark certain dancers in the formation. */
	Formation select(DancerName which) { return which.select(this); }
	/** Unmark certain dancers in the formation. */
	Formation select(DancerName... restrictions) {
	    Formation f = this;
	    for (DancerName which : restrictions)
		f = f.select(which);
	    return f;
	}
	/** Return the number of selected, non-phantom dancers. */
	public int numDancers() { /* XXX */ }
	/** Create a phantom formation by mapping the selected dancers in
	 *  this formation to the selected dancers in the selected formation
	 *  (rotating it if necessary). */
	Formation createPhantom(Formation f) throws IllegalCallException {
	    for (int i=0; i<4; i++) {
		try {
		    // map dancers
		    assert false : "unimplemented";
		} catch (IllegalCallException e) { /* ignore */ }
		f = f.rotateCW();
	    }
	    throw new IllegalCallException("Can't map "+this+" to "+f);
	}
	/** Check that this formation exactly matches some rotation and
	 *  some multiple of the given formation. */
	Formation require(Formation f) throws IllegalCallException {
	    return f;
	}
	/** Do a call from this formation. */
	DancePath dance(Call... call) {
	}
    }
}
