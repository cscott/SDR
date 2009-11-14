/**
 * This package contains code to parse call lists and transform call abstract
 * syntax in various ways.
 *
 * @doc.test Square thru is "2 beats per hand", so square thru 1 1/2 takes 3
 *  beats.  Tests call evaluation, in removal, and fractionalization.
 *  js> importPackage(net.cscott.sdr.util) // for Fraction
 *  js> importPackage(net.cscott.sdr.calls) // for CallDB, DanceState
 *  js> importPackage(net.cscott.sdr.calls.ast) // for Apply
 *  js> ds = new DanceState(new DanceProgram(Program.BASIC), Formation.FOUR_SQUARE); undefined;
 *  js> sqthr = CallDB.INSTANCE.lookup("square thru")
 *  square thru[basic]
 *  js> def = sqthr.getEvaluator(ds, java.util.Arrays.asList(Expr.literal("1 1/2"))).simpleExpansion(); undefined
 *  js> new Evaluator.Standard(def).evaluateAll(ds); undefined;
 *  js> ds.currentTime()
 *  3/1
 */
package net.cscott.sdr.calls.transform;
