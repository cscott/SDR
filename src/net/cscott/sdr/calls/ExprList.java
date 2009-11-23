package net.cscott.sdr.calls;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.cscott.sdr.calls.ExprFunc.EvaluationException;
import net.cscott.sdr.calls.ast.Expr;
import net.cscott.sdr.calls.transform.Evaluator;
import net.cscott.sdr.util.Fraction;

public abstract class ExprList {
    /** Map of all the {@link ExprFunc}s defined here. */
    @SuppressWarnings("unchecked")
    private final static Map<String, ExprFunc> exprGenericFuncs =
        new LinkedHashMap<String,ExprFunc>();
    private final static Map<String, ExprFunc<Fraction>> exprMathFuncs =
        new LinkedHashMap<String,ExprFunc<Fraction>>();
    private final static Map<String, ExprFunc<String>> exprStringFuncs =
        new LinkedHashMap<String,ExprFunc<String>>();

    /** This method evaluates {@link Expr} nodes. */
    public static <T> T evaluate(String atom, Class<T> type,
                                 DanceState ds, List<Expr> args)
        throws EvaluationException {
        return lookup(atom, type).evaluate(type, ds, args);
    }
    /** This method tells whether an {@link Expr} will result in a constant. */
    public static <T> boolean isConstant(String atom, Class<T> type, List<Expr> args) {
        try {
            return lookup(atom, type).isConstant(type, args);
        } catch (EvaluationException e) {
            assert false : "should never happen";
            throw new RuntimeException(e);
        }
    }
    // namespace mechanism.
    @SuppressWarnings("unchecked") // dispatch mechanism needs crazy casts
    private static final <T> ExprFunc<? extends T> lookup(String atom,
                                                          Class<T> type)
        throws EvaluationException {
        final String atomP = atom.toLowerCase();
        if (exprGenericFuncs.containsKey(atomP))
            return (ExprFunc<? extends T>) exprGenericFuncs.get(atomP);
        if (type.isAssignableFrom(Fraction.class) &&
                exprMathFuncs.containsKey(atomP))
                return (ExprFunc<? extends T>) exprMathFuncs.get(atomP);
        if (type.isAssignableFrom(String.class) &&
                exprStringFuncs.containsKey(atomP))
                return (ExprFunc<? extends T>) exprStringFuncs.get(atomP);
        if (type.isAssignableFrom(Boolean.class))
            try {
                return (ExprFunc<? extends T>) (ExprFunc<Boolean>)
                    PredicateList.valueOf(atom);
            } catch (IllegalArgumentException iae) { /* fall through */ }
        if (type.isAssignableFrom(Evaluator.class))
            try {
                return (ExprFunc<? extends T>) (ExprFunc<Evaluator>)
                    CallDB.INSTANCE.lookup(atom);
            } catch (IllegalArgumentException iae) { /* fall through */ }
        if (type.isAssignableFrom(Selector.class))
            try {
                return (ExprFunc<? extends T>) (ExprFunc<Selector>)
                    SelectorList.valueOf(atom);
            } catch (IllegalArgumentException iae) { /* fall through */ }
        if (type.isAssignableFrom(Matcher.class))
            return (ExprFunc<? extends T>) MatcherList.valueOf(atom);
        throw new EvaluationException("Couldn't find function "+atom);
    }
    @SuppressWarnings("unchecked")
    public static final ExprFunc LITERAL = new ExprFunc() {
        @Override
        public String getName() { return "literal"; }
        @Override
        public Object evaluate(Class type, DanceState ds, List _args)
            throws EvaluationException {
            List<Expr> args = (List<Expr>) _args;
            if (type.isAssignableFrom(Expr.class))
                return args.get(0);
            if (args.size() != 1)
                throw new EvaluationException("Missing argument to LITERAL");
            if (type.isAssignableFrom(String.class))
                return args.get(0).atom;
            if (type.isAssignableFrom(Fraction.class))
                return Fraction.valueOf
                    ((String)evaluate(String.class, ds, args));
            if (type.isAssignableFrom(Boolean.class)) {
                String name = (String) evaluate(String.class, ds, args);
                if (name.equalsIgnoreCase("true"))
                    return Boolean.TRUE;
                if (name.equalsIgnoreCase("false"))
                    return Boolean.FALSE;
            }
            if (type.isAssignableFrom(Matcher.class))
                try {
                    // try to get matcher, otherwise make from formation.
                    return Matcher.valueOf
                        ((String)evaluate(String.class, ds, args));
                } catch (IllegalArgumentException iae) {
                    return GeneralFormationMatcher.makeMatcher
                        ((TaggedFormation)evaluate(TaggedFormation.class, ds, args));
                }
            if (type.isAssignableFrom(NamedTaggedFormation.class))
                return FormationList.valueOf
                    ((String)evaluate(String.class, ds, args));
            if (type.isAssignableFrom(Evaluator.class))
                // allow 'trade' to be an abbreviation of 'trade()'
                return args.get(0).evaluate(type, ds);
            if (type.isAssignableFrom(Selector.class))
                // allow 'beau' to be an abbreviation of 'beau()'
                return args.get(0).evaluate(type, ds);
            throw new EvaluationException("Can't evaluate LITERAL as "+type);
        }
        /** Literals are constants. */
        @Override
        public boolean isConstant(Class type, List args) {
            return true;
        }
    };
    static { exprGenericFuncs.put(LITERAL.getName(), LITERAL); }

    // mathematics
    private static abstract class MathFunc extends ExprFunc<Fraction> {
        private final String name;
        MathFunc(String name) { this.name = name; }
        @Override
        public String getName() { return name; }
        abstract int minArgs();
        abstract int maxArgs();
        abstract Fraction doOp(Fraction f1, Fraction f2);
        // the result of a math op is a constant if the args are.
        @Override
        public boolean isConstant(Class<? super Fraction> type,List<Expr> args){
            for (Expr e : args)
                if (!ExprList.isConstant(e.atom, Fraction.class, e.args))
                    return false;
            return true;
        }
        @Override
        public Fraction evaluate(Class<? super Fraction> type,
                                 DanceState ds, List<Expr> args)
                throws EvaluationException {
            if (type.isAssignableFrom(Fraction.class)) {
                if (args.size() < minArgs())
                    throw new EvaluationException("Not enough arguments");
                if (args.size() > maxArgs())
                    throw new EvaluationException("Too many arguments");
                Fraction result = null;
                for (Expr e : args) {
                    Fraction f = e.evaluate(Fraction.class, ds);
                    result = (result==null) ? f : doOp(result, f);
                }
                return result;
            }
            // should we allow conversions to string, boolean, etc?
            throw new EvaluationException("Type mismatch for math call");
        }
    }
    /**
     * Simple math: addition.
     * @doc.test
     *  js> ds = new DanceState(new DanceProgram(Program.C4), Formation.SQUARED_SET); undefined;
     *  js> fc = java.lang.Class.forName('net.cscott.sdr.util.Fraction'); undefined
     *  js> c=net.cscott.sdr.calls.ast.AstNode.valueOf("(Expr _add num '2 '1)")
     *  (Expr _add num '2 '1)
     *  js> c.evaluate(fc, ds).toProperString()
     *  3
     */
    public static final ExprFunc<Fraction> _ADD_NUM = new MathFunc("_add num") {
        @Override
        Fraction doOp(Fraction f1, Fraction f2) { return f1.add(f2); }
        @Override
        public int maxArgs() { return Integer.MAX_VALUE; }
        @Override
        public int minArgs() { return 1; }
    };
    static { exprMathFuncs.put(_ADD_NUM.getName(), _ADD_NUM); }
    /**
     * Simple math: multiplication.
     * @doc.test
     *  js> ds = new DanceState(new DanceProgram(Program.C4), Formation.SQUARED_SET); undefined;
     *  js> fc = java.lang.Class.forName('net.cscott.sdr.util.Fraction'); undefined
     *  js> c=net.cscott.sdr.calls.ast.AstNode.valueOf("(Expr _multiply num '3 '2)")
     *  (Expr _multiply num '3 '2)
     *  js> c.evaluate(fc, ds).toProperString()
     *  6
     */
    public static final ExprFunc<Fraction> _MULTIPLY_NUM = new MathFunc("_multiply num") {
        @Override
        Fraction doOp(Fraction f1, Fraction f2) { return f1.multiply(f2); }
        @Override
        public int maxArgs() { return Integer.MAX_VALUE; }
        @Override
        public int minArgs() { return 1; }
    };
    static { exprMathFuncs.put(_MULTIPLY_NUM.getName(), _MULTIPLY_NUM); }
    /**
     * Simple math: subtraction.
     * @doc.test
     *  js> ds = new DanceState(new DanceProgram(Program.C4), Formation.SQUARED_SET); undefined;
     *  js> fc = java.lang.Class.forName('net.cscott.sdr.util.Fraction'); undefined
     *  js> c=net.cscott.sdr.calls.ast.AstNode.valueOf("(Expr _subtract num '3 '2)")
     *  (Expr _subtract num '3 '2)
     *  js> c.evaluate(fc, ds).toProperString()
     *  1
     */
    public static final ExprFunc<Fraction> _SUBTRACT_NUM = new MathFunc("_subtract num") {
        @Override
        Fraction doOp(Fraction f1, Fraction f2) { return f1.subtract(f2); }
        @Override
        public int maxArgs() { return 2; }
        @Override
        public int minArgs() { return 2; }
    };
    static { exprMathFuncs.put(_SUBTRACT_NUM.getName(), _SUBTRACT_NUM); }
    /**
     * Simple math: division.
     * @doc.test
     *  js> ds = new DanceState(new DanceProgram(Program.C4), Formation.SQUARED_SET); undefined;
     *  js> fc = java.lang.Class.forName('net.cscott.sdr.util.Fraction'); undefined
     *  js> c=net.cscott.sdr.calls.ast.AstNode.valueOf("(Expr _divide num '3 '2)")
     *  (Expr _divide num '3 '2)
     *  js> c.evaluate(fc, ds).toProperString()
     *  1 1/2
     */
    public static final ExprFunc<Fraction> _DIVIDE_NUM = new MathFunc("_divide num") {
        @Override
        Fraction doOp(Fraction f1, Fraction f2) { return f1.divide(f2); }
        @Override
        public int maxArgs() { return 2; }
        @Override
        public int minArgs() { return 2; }
    };
    static { exprMathFuncs.put(_DIVIDE_NUM.getName(), _DIVIDE_NUM); }

    private static abstract class PatternFunc<T> extends ExprFunc<String> {
        private final String name;
        PatternFunc(String name) { this.name = name; }
        @Override
        public String getName() { return name; }
        @Override
        public String evaluate(Class<? super String> type, DanceState ds,
                               List<Expr> args) throws EvaluationException {
            T closure = parseArgs(ds, args);
            TaggedFormation tf = TaggedFormation.coerce(ds.currentFormation());
            StringBuilder sb = new StringBuilder();
            for (Dancer d : tf.sortedDancers())
                sb.append(dancerToString(tf, d, closure));
            return sb.toString();
        }
        protected abstract String dancerToString(TaggedFormation tf, Dancer d,
                                                 T closure);
        protected T parseArgs(DanceState ds, List<Expr> args)
            throws EvaluationException { return null; }
    }
    public static final ExprFunc<String> _ROLL_PATTERN =
        new PatternFunc<Void>("_roll pattern") {
        @Override
        protected String dancerToString(TaggedFormation tf, Dancer d, Void v) {
            Position p = tf.location(d);
            if (p.flags.contains(Position.Flag.ROLL_LEFT))
                return "L";
            if (p.flags.contains(Position.Flag.ROLL_RIGHT))
                return "R";
            return "_";
        }
    };
    static { exprStringFuncs.put(_ROLL_PATTERN.getName(), _ROLL_PATTERN); }

    public static final ExprFunc<String> _SWEEP_PATTERN =
        new PatternFunc<Void>("_sweep pattern") {
        @Override
        protected String dancerToString(TaggedFormation tf, Dancer d, Void v) {
            Position p = tf.location(d);
            if (p.flags.contains(Position.Flag.SWEEP_LEFT))
                return "L";
            if (p.flags.contains(Position.Flag.SWEEP_RIGHT))
                return "R";
            return "_";
        }
    };
    static { exprStringFuncs.put(_SWEEP_PATTERN.getName(), _SWEEP_PATTERN); }

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
     *    >           "(Expr MATCH (Expr _SELECTION PATTERN '"+sel+") '\""+pat+"\")");
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
     *  js> // wildcards
     *  js> test('BOY', 'x...')
     *  true
     */
    public static final ExprFunc<String> _SELECTION_PATTERN =
        new PatternFunc<Set<Dancer>>("_selection pattern") {
        protected Set<Dancer> parseArgs(DanceState ds, List<Expr> args)
            throws EvaluationException {
            Selector selector =
                SelectorList.OR.evaluate(Selector.class, ds, args);
            TaggedFormation tf = TaggedFormation.coerce(ds.currentFormation());
            return selector.select(tf);
        }
        @Override
        protected String dancerToString(TaggedFormation tf, Dancer d,
                                        Set<Dancer> selected) {
            return selected.contains(d) ? "X" : "_";
        }
    };
    static { exprStringFuncs.put(_SELECTION_PATTERN.getName(), _SELECTION_PATTERN); }
}
