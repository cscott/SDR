package net.cscott.sdr.calls.lists;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import net.cscott.sdr.calls.BadCallException;
import net.cscott.sdr.calls.Breather;
import net.cscott.sdr.calls.Call;
import net.cscott.sdr.calls.DanceState;
import net.cscott.sdr.calls.Dancer;
import net.cscott.sdr.calls.DancerPath;
import net.cscott.sdr.calls.Formation;
import net.cscott.sdr.calls.FormationList;
import net.cscott.sdr.calls.FormationMatch;
import net.cscott.sdr.calls.GeneralFormationMatcher;
import net.cscott.sdr.calls.NamedTaggedFormation;
import net.cscott.sdr.calls.NoMatchException;
import net.cscott.sdr.calls.Position;
import net.cscott.sdr.calls.Program;
import net.cscott.sdr.calls.Selector;
import net.cscott.sdr.calls.TaggedFormation;
import net.cscott.sdr.calls.TimedFormation;
import net.cscott.sdr.calls.ast.Apply;
import net.cscott.sdr.calls.ast.Expr;
import net.cscott.sdr.calls.grm.Grm;
import net.cscott.sdr.calls.grm.Rule;
import net.cscott.sdr.calls.transform.Evaluator;
import net.cscott.sdr.util.Fraction;

/**
 * The <code>A1List</code> class contains complex call
 * and concept definitions which are on the 'A1' program.
 * Note that "simple" calls and concepts are defined in
 * the resource file at
 * <a href="doc-files/a1.calls"><code>net/cscott/sdr/calls/lists/a1.calls</code></a>;
 * this class contains only those definitions for which an
 * executable component is required.
 * @author C. Scott Ananian
 */
public abstract class A1List {
    // hide constructor.
    private A1List() { }

    private static abstract class A1Call extends Call {
        private final String name;
        A1Call(String name) { this.name = name; }
        @Override
        public final String getName() { return name; }
        @Override
        public final Program getProgram() { return Program.A1; }
        @Override
        public List<Expr> getDefaultArguments() {
            return Collections.emptyList();
        }
    }

    public static final Call AS_COUPLES = new A1Call("as couples") {
        @Override
        public int getMinNumberOfArguments() { return 1; }
        @Override
        public Rule getRule() {
            Grm g = Grm.parse("as couples <0=anything>");
            return new Rule("anything", g, Fraction.valueOf(-10));
        }
        @Override
        public Evaluator getEvaluator(DanceState ds, List<Expr> args) {
            assert args.size() == 1;
            return new SolidEvaluator(args.get(0), FormationList.COUPLE,
                                      SolidMatch.ALL, SolidType.SOLID);
        }
    };

    public static enum SolidType { SOLID, TWOSOME };
    public static enum SolidMatch { SOME, ALL };
    /** Evaluator for couples/tandem/solid formations. */
    public static class SolidEvaluator extends Evaluator {
        private final Expr subCall;
        private final String formationName;
        private final Selector solidSelector;
        private final SolidType type;
        public SolidEvaluator(Expr subCall,
                              final NamedTaggedFormation solidFormation,
                              final SolidMatch match, SolidType type) {
            this(subCall, solidFormation.getName(), new Selector() {
                @Override
                public FormationMatch match(Formation f) throws NoMatchException {
                    return GeneralFormationMatcher.doMatch
                        (f, solidFormation, (match == SolidMatch.SOME),
                         false /* no phantoms */);
                }}, type);
        }
        public SolidEvaluator(Expr subCall, String formationName,
                              Selector solidSelector, SolidType type) {
            this.subCall = subCall;
            this.formationName = formationName;
            this.solidSelector = solidSelector;
            this.type = type;
        }

        @Override
        public Evaluator evaluate(DanceState ds) {
            // this is very similar to what we do in MetaEvaluator
            FormationMatch fm;
            try {
                fm = this.solidSelector.match(ds.currentFormation());
            } catch (NoMatchException nme) {
                throw new BadCallException("No "+this.formationName+" dancers");
            }
            Formation metaF = fm.meta;
            // ok, now tag the meta dancers the way the real dancers are
            // tagged
            // XXX: DO ME
            // and do the call in the meta formation
            DanceState metaS = ds.cloneAndClear(metaF);
            new Apply(this.subCall).evaluator(metaS).evaluateAll(metaS);
            metaS.syncDancers();
            // now go through moment-by-moment and insert the subformations
            // XXX lots of cut-and-paste from MetaEvaluator; we should
            //     refactor out the common code.

            // go through all the moments in time, constructing an appropriately
            // breathed formation.
            TreeMap<Fraction,Formation> merged =
                new TreeMap<Fraction,Formation>();
            for (TimedFormation tf : metaS.formations()) {
                Map<Dancer,Formation> pieces =
                    new LinkedHashMap<Dancer,Formation>();
                for (Entry<Dancer,TaggedFormation> e : fm.matches.entrySet()) {
                    Dancer d = e.getKey();
                    TaggedFormation solidPiece = e.getValue();
                    if (this.type == SolidType.TWOSOME) {
                        // XXX for twosome, rotate formations
                        throw new BadCallException("twosome not implemented");
                    }
                    // XXX transfer roll, etc from tf.formation to solidPiece
                    pieces.put(d, solidPiece);
                }
                Formation mergeF = Breather.insert(tf.formation, pieces);
                merged.put(tf.time, mergeF);
            }
            // okay, now go through the individual dancer paths, adjusting the
            // 'to' and 'from' positions to match breathed.
            for (Dancer metaD : metaS.dancers()) {
                for (Dancer d : fm.matches.get(metaD).dancers()) {
                    Fraction t = Fraction.ZERO;
                    for (DancerPath dp : metaS.movements(metaD)) {
                        Position nfrom = merged.get(t).location(d);
                        t = t.add(dp.time);
                        Position nto = merged.get(t).location(d);
                        DancerPath ndp = dp.translate(nfrom, nto);
                        ds.add(d, ndp);
                    }
                }
            }
            // dancers should all be in sync at this point.
            // no more to evaluate
            return null;
        }
    };
}
