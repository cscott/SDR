package net.cscott.sdr.calls;

import static java.util.Arrays.asList;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.cscott.jdoctest.JDoctestRunner;
import net.cscott.sdr.calls.ast.Expr;
import net.cscott.sdr.util.Fraction;

import org.junit.runner.RunWith;

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
     *  js> PredicateList.valueOf(c.atom).evaluate(ds, c.args)
     *  true
     */
    public final static Predicate TRUE = new _Predicate("true") {
        @Override
        public boolean evaluate(DanceState ds, List<Expr> args) {
            assert args.isEmpty();
            return true;
        }
        @Override
        public boolean isConstant(List<Expr> args) { return true; }
    };
    /**
     * Always false.
     * @doc.test
     *  js> ds = new DanceState(new DanceProgram(Program.C4), Formation.SQUARED_SET); undefined;
     *  js> c = net.cscott.sdr.calls.ast.AstNode.valueOf('(Expr false)');
     *  (Expr false)
     *  js> PredicateList.valueOf(c.atom).evaluate(ds, c.args)
     *  false
     */
    public final static Predicate FALSE = new _Predicate("false") {
        @Override
        public boolean evaluate(DanceState ds, List<Expr> args) {
            assert args.isEmpty();
            return false;
        }
        @Override
        public boolean isConstant(List<Expr> args) { return true; }
    };
    // one-arg operators
    /**
     * Boolean negation.
     * @doc.test
     *  js> ds = new DanceState(new DanceProgram(Program.C4), Formation.SQUARED_SET); undefined;
     *  js> c = net.cscott.sdr.calls.ast.AstNode.valueOf('(Expr not (Expr false))');
     *  (Expr not (Expr false))
     *  js> PredicateList.valueOf(c.atom).evaluate(ds, c.args)
     *  true
     *  js> c = net.cscott.sdr.calls.ast.AstNode.valueOf('(Expr not (Expr true))');
     *  (Expr not (Expr true))
     *  js> PredicateList.valueOf(c.atom).evaluate(ds, c.args)
     *  false
     */
    public final static Predicate NOT = new _Predicate("not") {
        @Override
        public boolean evaluate(DanceState ds, List<Expr> args) throws EvaluationException {
            assert args.size()==1;
            Expr arg = args.get(0);
            return !arg.evaluate(Boolean.class, ds);
        }
        @Override
        public boolean isConstant(List<Expr> args) {
            return args.get(0).isConstant(Boolean.class);
        }
    };
    // binary numerical operators
    /**
     * Numerical equality.
     * @doc.test
     *  js> ds = new DanceState(new DanceProgram(Program.C4), Formation.SQUARED_SET); undefined;
     *  js> c = net.cscott.sdr.calls.ast.AstNode.valueOf('(Expr equal \'"1 1/2" \'1 1/2)');
     *  (Expr equal '1 1/2 '1 1/2)
     *  js> PredicateList.valueOf(c.atom).evaluate(ds, c.args)
     *  true
     *  js> c = net.cscott.sdr.calls.ast.AstNode.valueOf('(Expr equal \'"1 1/2" \'2)');
     *  (Expr equal '1 1/2 '2)
     *  js> PredicateList.valueOf(c.atom).evaluate(ds, c.args)
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
        @Override
        public boolean isConstant(List<Expr> args) {
            return argsAreConstant(Fraction.class, args);
        }
    };
    /**
     * Numerical comparison.
     * @doc.test
     *  js> ds = new DanceState(new DanceProgram(Program.C4), Formation.SQUARED_SET); undefined;
     *  js> c = net.cscott.sdr.calls.ast.AstNode.valueOf('(Expr greater \'"1 1/2" \'"1 1/2")');
     *  (Expr greater '1 1/2 '1 1/2)
     *  js> PredicateList.valueOf(c.atom).evaluate(ds, c.args)
     *  false
     *  js> c = net.cscott.sdr.calls.ast.AstNode.valueOf("(Expr greater '2 '1 1/2)");
     *  (Expr greater '2 '1 1/2)
     *  js> PredicateList.valueOf(c.atom).evaluate(ds, c.args)
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
        @Override
        public boolean isConstant(List<Expr> args) {
            return argsAreConstant(Fraction.class, args);
        }
    };
    // n-ary operators.
    /**
     * Short-circuit boolean conjunction.
     * @doc.test
     *  js> ds = new DanceState(new DanceProgram(Program.C4), Formation.SQUARED_SET); undefined;
     *  js> c = net.cscott.sdr.calls.ast.AstNode.valueOf('(Expr and (Expr true) (Expr true) (Expr false))');
     *  (Expr and (Expr true) (Expr true) (Expr false))
     *  js> PredicateList.valueOf(c.atom).evaluate(ds, c.args)
     *  false
     *  js> c = net.cscott.sdr.calls.ast.AstNode.valueOf('(Expr and (Expr true) (Expr true) (Expr true))');
     *  (Expr and (Expr true) (Expr true) (Expr true))
     *  js> PredicateList.valueOf(c.atom).evaluate(ds, c.args)
     *  true
     *  js> // short-circuits
     *  js> c = net.cscott.sdr.calls.ast.AstNode.valueOf('(Expr and (Expr false) (Expr bogus))');
     *  (Expr and (Expr false) (Expr bogus))
     *  js> PredicateList.valueOf(c.atom).evaluate(ds, c.args)
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
        @Override
        public boolean isConstant(List<Expr> args) {
            return argsAreConstant(Boolean.class, args);
        }
    };
    /**
     * Short-circuit boolean disjunction.
     * @doc.test
     *  js> ds = new DanceState(new DanceProgram(Program.C4), Formation.SQUARED_SET); undefined;
     *  js> c = net.cscott.sdr.calls.ast.AstNode.valueOf('(Expr or (Expr false) (Expr false) (Expr false))');
     *  (Expr or (Expr false) (Expr false) (Expr false))
     *  js> PredicateList.valueOf(c.atom).evaluate(ds, c.args)
     *  false
     *  js> c = net.cscott.sdr.calls.ast.AstNode.valueOf('(Expr or (Expr false) (Expr false) (Expr true))');
     *  (Expr or (Expr false) (Expr false) (Expr true))
     *  js> PredicateList.valueOf(c.atom).evaluate(ds, c.args)
     *  true
     *  js> // short-circuits
     *  js> c = net.cscott.sdr.calls.ast.AstNode.valueOf('(Expr or (Expr true) (Expr bogus))');
     *  (Expr or (Expr true) (Expr bogus))
     *  js> PredicateList.valueOf(c.atom).evaluate(ds, c.args)
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
        @Override
        public boolean isConstant(List<Expr> args) {
            return argsAreConstant(Boolean.class, args);
        }
    };
    // okay, square-dance-specific operators.
    /**
     * Check the current dance program level.
     * @doc.test
     *  js> ds = new DanceState(new DanceProgram(Program.PLUS), Formation.SQUARED_SET); undefined;
     *  js> c = net.cscott.sdr.calls.ast.AstNode.valueOf("(Expr PROGRAM AT LEAST 'BASIC)");
     *  (Expr PROGRAM AT LEAST 'BASIC)
     *  js> PredicateList.valueOf(c.atom).evaluate(ds, c.args)
     *  true
     *  js> c = net.cscott.sdr.calls.ast.AstNode.valueOf("(Expr PROGRAM AT LEAST 'A2)");
     *  (Expr PROGRAM AT LEAST 'A2)
     *  js> PredicateList.valueOf(c.atom).evaluate(ds, c.args)
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
     *    >    return PredicateList.valueOf(c.atom).evaluate(ds, c.args)
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
            assert args.size()==2;
            Selector selector = args.get(0).evaluate(Selector.class, ds);

            TaggedFormation tf = TaggedFormation.coerce(ds.currentFormation());
            Set<Dancer> selected = selector.select(tf);
            String pattern = args.get(1).evaluate(String.class, ds);
            if (pattern.length() != tf.dancers().size())
                return false;
            // check each dancer against the corresponding character in the
            // pattern.
            int i=0;
            for (Dancer d : tf.sortedDancers()) {
                boolean t1 = selected.contains(d);
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
     *    >    return PredicateList.valueOf(c.atom).evaluate(ds, c.args)
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
            Selector selector =
                SelectorList.OR.evaluate(Selector.class, ds, args);
            // ok, look at rotation directions for the selected dancers.
            Rotation r = null;
            // each selected dancer must have all of these tags
            TaggedFormation tf = TaggedFormation.coerce(ds.currentFormation());
            Set<Dancer> selected = selector.select(tf);
            for (Dancer d: tf.selectedDancers()) {
                if (!selected.contains(d))
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
            Selector left = args.get(0).evaluate(Selector.class, ds);
            Selector right = args.get(1).evaluate(Selector.class, ds);

            TaggedFormation tf = TaggedFormation.coerce(ds.currentFormation());
            Set<Dancer> leftDancers = left.select(tf);
            Set<Dancer> rightDancers = right.select(tf);

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
        /** This is a case-insensitive tree comparison. */
        @Override
        public boolean evaluate(DanceState ds, List<Expr> args) {
            assert args.size() == 2;
            return exprEquals(args.get(0), args.get(1));
        }
        // XXX fix this (and isConstant) if/when REF substitution is moved into
        //     expression evaluation.  If so, we'd have to substitute the refs
        //     as we do the comparison.
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
        /** No evaluation, just based on the shape of the tree. */
        @Override
        public boolean isConstant(List<Expr> args) { return true; }
    };

    /** Return the {@link Predicate} function with the given (case-insensitive)
     *  name.
     * @throws IllegalArgumentException if no predicate with the given name is
     *         found.
     */
    public static Predicate valueOf(String s) {
        String ss = normalize(s);
        if (!predicateList.containsKey(ss))
            throw new IllegalArgumentException("No such predicate: "+s);
        return predicateList.get(ss);
    }

    // helper class ////////////////////////////////////
    private static abstract class _Predicate extends Predicate {
        private final String name;
        _Predicate(String name) { this.name = name; }
        public String getName() { return name; }
        public boolean isConstant(List<Expr> args) { return false; }
        @Override
        public final boolean isConstant(Class<? super Boolean> type,
                                        List<Expr> args) {
            return isConstant(args);
        }
        protected static boolean argsAreConstant(Class<?> type,
                                                 List<Expr> args) {
            for (Expr e : args)
                if (!e.isConstant(type))
                    return false;
            return true;
        }
    }

    /** List of all the {@link Predicate}s defined here. */
    private final static Map<String, Predicate> predicateList =
        new LinkedHashMap<String, Predicate>();
    static {
        for (Field f : PredicateList.class.getDeclaredFields()) {
            if (f.getName().equals(f.getName().toUpperCase()) &&
                Predicate.class.isAssignableFrom(f.getType()) &&
                Modifier.isStatic(f.getModifiers())) {
                try {
                    addToList((Predicate) f.get(null));
                } catch (Throwable t) {
                    assert false : "unreachable";
                }
            }
        }
    }
    private static String normalize(String s) {
        return s.toLowerCase().replace('_', ' ').intern();
    }
    private static void addToList(Predicate s) {
        assert normalize(s.getName()).equals(s.getName());
        predicateList.put(s.getName(), s);
    }
}
