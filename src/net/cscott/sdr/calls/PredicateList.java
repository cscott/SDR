package net.cscott.sdr.calls;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import static java.util.Arrays.asList;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.runner.RunWith;

import net.cscott.jdoctest.JDoctestRunner;
import net.cscott.sdr.calls.TaggedFormation.Tag;
import net.cscott.sdr.calls.ast.Expr;
import net.cscott.sdr.calls.ast.ParCall;
import net.cscott.sdr.util.Fraction;

/** This class contains all the predicates known to the system. */
@RunWith(value=JDoctestRunner.class)
public abstract class PredicateList {
    // zero-arg operators
    /**
     * Always true.
     * @doc.test
     *  js> ds = new DanceState(new DanceProgram(Program.C4), Formation.SQUARED_SET); undefined;
     *  js> c = net.cscott.sdr.calls.ast.AstNode.valueOf('(Expr true)');
     *  (Expr true)
     *  js> PredicateList.predicates.get(c.atom).evaluate(ds, c.args)
     *  true
     */
    public final static Predicate TRUE = new _Predicate("true") {
        @Override
        public boolean evaluate(DanceState ds, List<Expr> args) {
            assert args.isEmpty();
            return true;
        }        
    };
    /**
     * Always false.
     * @doc.test
     *  js> ds = new DanceState(new DanceProgram(Program.C4), Formation.SQUARED_SET); undefined;
     *  js> c = net.cscott.sdr.calls.ast.AstNode.valueOf('(Expr false)');
     *  (Expr false)
     *  js> PredicateList.predicates.get(c.atom).evaluate(ds, c.args)
     *  false
     */
    public final static Predicate FALSE = new _Predicate("false") {
        @Override
        public boolean evaluate(DanceState ds, List<Expr> args) {
            assert args.isEmpty();
            return false;
        }
    };
    // one-arg operators
    /**
     * Boolean negation.
     * @doc.test
     *  js> ds = new DanceState(new DanceProgram(Program.C4), Formation.SQUARED_SET); undefined;
     *  js> c = net.cscott.sdr.calls.ast.AstNode.valueOf('(Expr not (Expr false))');
     *  (Expr not (Expr false))
     *  js> PredicateList.predicates.get(c.atom).evaluate(ds, c.args)
     *  true
     *  js> c = net.cscott.sdr.calls.ast.AstNode.valueOf('(Expr not (Expr true))');
     *  (Expr not (Expr true))
     *  js> PredicateList.predicates.get(c.atom).evaluate(ds, c.args)
     *  false
     */
    public final static Predicate NOT = new _Predicate("not") {
        @Override
        public boolean evaluate(DanceState ds, List<Expr> args) throws EvaluationException {
            assert args.size()==1;
            Expr arg = args.get(0);
            return !arg.evaluate(Boolean.class, ds);
        }
    };
    // binary numerical operators
    /**
     * Numerical equality.
     * @doc.test
     *  js> ds = new DanceState(new DanceProgram(Program.C4), Formation.SQUARED_SET); undefined;
     *  js> c = net.cscott.sdr.calls.ast.AstNode.valueOf('(Expr equal \'"1 1/2" \'1 1/2)');
     *  (Expr equal '1 1/2 '1 1/2)
     *  js> PredicateList.predicates.get(c.atom).evaluate(ds, c.args)
     *  true
     *  js> c = net.cscott.sdr.calls.ast.AstNode.valueOf('(Expr equal \'"1 1/2" \'2)');
     *  (Expr equal '1 1/2 '2)
     *  js> PredicateList.predicates.get(c.atom).evaluate(ds, c.args)
     *  false
     */
    public final static Predicate EQUAL = new _Predicate("equal") {
        @Override
        public boolean evaluate(DanceState ds, List<Expr> args) throws EvaluationException {
            assert args.size()==2;
            Fraction f0 = args.get(0).evaluate(Fraction.class, ds);
            Fraction f1 = args.get(1).evaluate(Fraction.class, ds);
            return f0.equals(f1);
        }
    };
    /**
     * Numerical comparison.
     * @doc.test
     *  js> ds = new DanceState(new DanceProgram(Program.C4), Formation.SQUARED_SET); undefined;
     *  js> c = net.cscott.sdr.calls.ast.AstNode.valueOf('(Expr greater \'"1 1/2" \'"1 1/2")');
     *  (Expr greater '1 1/2 '1 1/2)
     *  js> PredicateList.predicates.get(c.atom).evaluate(ds, c.args)
     *  false
     *  js> c = net.cscott.sdr.calls.ast.AstNode.valueOf("(Expr greater '2 '1 1/2)");
     *  (Expr greater '2 '1 1/2)
     *  js> PredicateList.predicates.get(c.atom).evaluate(ds, c.args)
     *  true
     */
    public final static Predicate GREATER = new _Predicate("greater") {
        @Override
        public boolean evaluate(DanceState ds, List<Expr> args) throws EvaluationException {
            assert args.size()==2;
            Fraction f0 = args.get(0).evaluate(Fraction.class, ds);
            Fraction f1 = args.get(1).evaluate(Fraction.class, ds);
            return f0.compareTo(f1) > 0;
        }
    };
    // n-ary operators.
    /**
     * Short-circuit boolean conjunction.
     * @doc.test
     *  js> ds = new DanceState(new DanceProgram(Program.C4), Formation.SQUARED_SET); undefined;
     *  js> c = net.cscott.sdr.calls.ast.AstNode.valueOf('(Expr and (Expr true) (Expr true) (Expr false))');
     *  (Expr and (Expr true) (Expr true) (Expr false))
     *  js> PredicateList.predicates.get(c.atom).evaluate(ds, c.args)
     *  false
     *  js> c = net.cscott.sdr.calls.ast.AstNode.valueOf('(Expr and (Expr true) (Expr true) (Expr true))');
     *  (Expr and (Expr true) (Expr true) (Expr true))
     *  js> PredicateList.predicates.get(c.atom).evaluate(ds, c.args)
     *  true
     *  js> // short-circuits
     *  js> c = net.cscott.sdr.calls.ast.AstNode.valueOf('(Expr and (Expr false) (Expr bogus))');
     *  (Expr and (Expr false) (Expr bogus))
     *  js> PredicateList.predicates.get(c.atom).evaluate(ds, c.args)
     *  false
     */
    public final static Predicate AND = new _Predicate("and") {
        @Override
        public boolean evaluate(DanceState ds, List<Expr> args) throws EvaluationException {
            assert args.size()>0;
            boolean result = true;
            for (Expr cc : args) {
                result = cc.evaluate(Boolean.class, ds);
                if (!result) break; // short-circuit operator.
            }
            return result;
        }
    };
    /**
     * Short-circuit boolean disjunction.
     * @doc.test
     *  js> ds = new DanceState(new DanceProgram(Program.C4), Formation.SQUARED_SET); undefined;
     *  js> c = net.cscott.sdr.calls.ast.AstNode.valueOf('(Expr or (Expr false) (Expr false) (Expr false))');
     *  (Expr or (Expr false) (Expr false) (Expr false))
     *  js> PredicateList.predicates.get(c.atom).evaluate(ds, c.args)
     *  false
     *  js> c = net.cscott.sdr.calls.ast.AstNode.valueOf('(Expr or (Expr false) (Expr false) (Expr true))');
     *  (Expr or (Expr false) (Expr false) (Expr true))
     *  js> PredicateList.predicates.get(c.atom).evaluate(ds, c.args)
     *  true
     *  js> // short-circuits
     *  js> c = net.cscott.sdr.calls.ast.AstNode.valueOf('(Expr or (Expr true) (Expr bogus))');
     *  (Expr or (Expr true) (Expr bogus))
     *  js> PredicateList.predicates.get(c.atom).evaluate(ds, c.args)
     *  true
     */
    public final static Predicate OR = new _Predicate("or") {
        @Override
        public boolean evaluate(DanceState ds, List<Expr> args) throws EvaluationException {
            assert args.size()>0;
            boolean result = false;
            for (Expr cc : args) {
                result = cc.evaluate(Boolean.class, ds);
                if (result) break; // short-circuit operator.
            }
            return result;
        }
    };
    // okay, square-dance-specific operators.
    /**
     * Check the current dance program level.
     * @doc.test
     *  js> ds = new DanceState(new DanceProgram(Program.PLUS), Formation.SQUARED_SET); undefined;
     *  js> c = net.cscott.sdr.calls.ast.AstNode.valueOf("(Expr PROGRAM AT LEAST 'BASIC)");
     *  (Expr PROGRAM AT LEAST 'BASIC)
     *  js> PredicateList.PROGRAM_AT_LEAST.evaluate(ds, c.args)
     *  true
     *  js> c = net.cscott.sdr.calls.ast.AstNode.valueOf("(Expr PROGRAM AT LEAST 'A2)");
     *  (Expr PROGRAM AT LEAST 'A2)
     *  js> PredicateList.PROGRAM_AT_LEAST.evaluate(ds, c.args)
     *  false
     */
    public final static Predicate PROGRAM_AT_LEAST = new _Predicate("program at least") {
        @Override
        public boolean evaluate(DanceState ds, List<Expr> args) throws EvaluationException {
            assert args.size()==1;
            Program p = Program.valueOf
                (args.get(0).evaluate(String.class, ds).toUpperCase());
            return ds.dance.getProgram().includes(p);
        }
    };
    /**
     * Check the order of the selected dancers within the given formation.
     * @doc.test
     *  js> FormationList = FormationListJS.initJS(this); undefined;
     *  js> SD = StandardDancer; undefined
     *  js> // rotate the formation 1/2 just to get rid of the original tags
     *  js> f = FormationList.RH_OCEAN_WAVE; f.toStringDiagram()
     *  ^    v    ^    v
     *  js> // label those dancers
     *  js> f= f.mapStd([SD.COUPLE_1_BOY, SD.COUPLE_1_GIRL,
     *    >              SD.COUPLE_3_BOY, SD.COUPLE_3_GIRL]); f.toStringDiagram()
     *  1B^  1Gv  3B^  3Gv
     *  js> ds = new DanceState(new DanceProgram(Program.PLUS), f); undefined;
     *  js> function test(sel, pat) {
     *    >   let c = net.cscott.sdr.calls.ast.AstNode.valueOf(
     *    >           "(Expr SELECTION PATTERN '"+sel+" '"+pat+")");
     *    >    return PredicateList.SELECTION_PATTERN.evaluate(ds, c.args);
     *    > }
     *  js> test('BOY', '____')
     *  false
     *  js> test('BOY', 'x_x_')
     *  true
     *  js> test('BOY', '_x_x')
     *  false
     *  js> test('CENTER', '_xx_')
     *  true
     *  js> test('HEAD', 'xxxx')
     *  true
     *  js> test('SIDE', '____')
     *  true
     *  js> test('COUPLE 1', 'xx__')
     *  true
     *  js> test('SIDE', '_xx_')
     *  false
     */
    public final static Predicate SELECTION_PATTERN = new _Predicate("selection pattern") {
        @Override
        public boolean evaluate(DanceState ds, List<Expr> args) throws EvaluationException {
            List<String> sArgs = new ArrayList<String>(args.size());
            for (int i=0; i<args.size()-1; i++)
                // XXX: should parse as Tag.class
                sArgs.add(args.get(i).evaluate(String.class, ds));
            Set<Tag> tags = ParCall.parseTags(sArgs);

            TaggedFormation tf = TaggedFormation.coerce(ds.currentFormation());
            String pattern = args.get(args.size()-1).evaluate(String.class, ds);
            if (pattern.length() != tf.dancers().size())
                return false;
            // check each dancer against the corresponding character in the
            // pattern.
            int i=0;
            for (Dancer d : tf.sortedDancers()) {
                boolean t1 = tf.isTagged(d, tags);
                boolean t2 = pattern.charAt(i++) != '_';
                if (t1!=t2) return false;
            }
            return true;
        }
    };
    /** Check whether the tagged dancers are t-boned.
     * @doc.test
     *  js> importPackage(net.cscott.sdr.util); // for Fraction
     *  js> FormationList = FormationListJS.initJS(this); undefined;
     *  js> SD = StandardDancer; undefined
     *  js> // rotate the formation 1/2 just to get rid of the original tags
     *  js> f = FormationList.RH_OCEAN_WAVE; f.toStringDiagram()
     *  ^    v    ^    v
     *  js> d = [d for (d in Iterator(f.sortedDancers()))]; undefined
     *  js> f = f.move(d[1], f.location(d[1]).turn
     *    >                 (Fraction.ONE_QUARTER, false)) ; f.toStringDiagram()
     *  ^    <    ^    v
     *  js> f = f.move(d[3], f.location(d[3]).turn
     *    >                 (Fraction.ONE_QUARTER, false)) ; f.toStringDiagram()
     *  ^    <    ^    <
     *  js> // label those dancers
     *  js> f= f.mapStd([SD.COUPLE_1_BOY, SD.COUPLE_1_GIRL,
     *    >              SD.COUPLE_3_BOY, SD.COUPLE_3_GIRL]); f.toStringDiagram()
     *  1B^  1G<  3B^  3G<
     *  js> ds = new DanceState(new DanceProgram(Program.PLUS), f); undefined;
     *  js> function test(sel) {
     *    >   let c = net.cscott.sdr.calls.ast.AstNode.valueOf(
     *    >           "(Expr TBONED '"+sel+")");
     *    >    return PredicateList.TBONED.evaluate(ds, c.args);
     *    > }
     *  js> test('BOY')
     *  false
     *  js> test('GIRL')
     *  false
     *  js> test('CENTER')
     *  true
     *  js> test('END')
     *  true
     *  js> test('HEAD')
     *  true
     */
    public final static Predicate TBONED = new _Predicate("tboned") {
        @Override
        public boolean evaluate(DanceState ds, List<Expr> args) throws EvaluationException {
            List<String> sArgs = new ArrayList<String>(args.size());
            for (Expr e : args)
                // XXX should eval as Tag.class
                sArgs.add(e.evaluate(String.class, ds));
            Set<Tag> tags = ParCall.parseTags(sArgs);
            // ok, look at rotation directions for the selected dancers.
            Rotation r = null;
            // each selected dancer must have all of these tags
            TaggedFormation tf = TaggedFormation.coerce(ds.currentFormation());
            for (Dancer d: tf.selectedDancers()) {
                if (!tf.isTagged(d, tags))
                    continue;
                if (r==null) {
                    r = tf.location(d).facing;
                    r = r.union(r.add(Fraction.ONE_HALF)); // fuzz
                } else {
                    if (!r.includes(tf.location(d).facing))
                        return true; // yes, it's t-boned
                }
            }
            return false; // nope, not t-boned
        }
    };
    /** Check that the tagged dancers also have some other tag. */
    public final static Predicate ARE = new _Predicate("are") {
        @Override
        public boolean evaluate(DanceState ds, List<Expr> args) throws EvaluationException {
            assert args.size()==2;
            // XXX should parse as Set<Tag> -- PersonSelector?
            String left = args.get(0).evaluate(String.class, ds);
            String right = args.get(1).evaluate(String.class, ds);

            Set<Tag> leftTags = ParCall.parseTags
                (Arrays.asList(left.split(",\\s*")));
            Set<Tag> rightTags = ParCall.parseTags
                (Arrays.asList(right.split(",\\s*")));

            TaggedFormation tf = TaggedFormation.coerce(ds.currentFormation());
            Set<Dancer> leftDancers = tf.tagged(leftTags);
            Set<Dancer> rightDancers = tf.tagged(rightTags);

            return rightDancers.containsAll(leftDancers);
        }
    };
    /** Check that all dancers have the specified tag. */
    public final static Predicate ALL = new _Predicate("all") {
        @Override
        public boolean evaluate(DanceState ds, List<Expr> args) throws EvaluationException {
            assert args.size()==1;
            return ARE.evaluate(ds,
                                asList(Expr.literal("ALL"),
                                       args.get(0)));
        }
    };

    /** Check the identify of a call provided as an argument.
     *  Used in a hack to implement "boys trade".
     */
    public final static Predicate CALL_IS = new _Predicate("call is") {
        /** This is just a case-insensitive string comparison, really. */
        @Override
        public boolean evaluate(DanceState ds, List<Expr> args) {
            assert args.size() == 2;
            return exprEquals(args.get(0), args.get(1));
        }
        private boolean exprEquals(Expr c1, Expr c2) {
            if (!c1.atom.equalsIgnoreCase(c2.atom))
                return false;
            if (c1.args.size() != c2.args.size())
                return false;
            for (int i=0; i<c1.args.size(); i++)
                if (!exprEquals(c1.args.get(i), c2.args.get(i)))
                    return false;
            return true;
        }
    };

    // helper class ////////////////////////////////////
    private static abstract class _Predicate extends Predicate {
        private final String name;
        _Predicate(String name) { this.name = name; }
        public String getName() { return name; }
    }

    /** List of all the {@link Predicate}s defined here. */
    public final static Map<String, Predicate> predicates;
    static {
        Map<String,Predicate> _p = new LinkedHashMap<String,Predicate>();
        for (Field f : PredicateList.class.getDeclaredFields()) {
            if (f.getName().equals(f.getName().toUpperCase()) &&
                Predicate.class.isAssignableFrom(f.getType()) &&
                Modifier.isStatic(f.getModifiers())) {
                try {
                    Predicate p = (Predicate) f.get(null);
                    _p.put(p.getName().replace(' ', '_'), p);
                } catch (Throwable t) {
                    assert false : "unreachable";
                }
            }
        }
        predicates = Collections.unmodifiableMap(_p);
    }
}
