package net.cscott.sdr.calls;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.cscott.sdr.calls.ExprFunc.EvaluationException;
import net.cscott.sdr.calls.ast.Expr;
import net.cscott.sdr.calls.transform.Evaluator;
import net.cscott.sdr.util.Fraction;

public abstract class ExprList {
    /** Map of all the {@link ExprFunc}s defined here. */
    private final static Map<String, ExprFunc<Object>> exprGenericFuncs =
        new LinkedHashMap<String,ExprFunc<Object>>();
    private final static Map<String, ExprFunc<Fraction>> exprMathFuncs =
        new LinkedHashMap<String,ExprFunc<Fraction>>();

    // XXX move all evaluation to net.cscott.sdr.calls.ExprFunc
    //     static method evaluate() there.  The ast just represents the
    //     computation, it doesn't perform it.
    // XXX provide implementation that looks up 'atom' in ExprList,
    // MatcherList, depending on the type requested.  This will be the
    // 'name spacing' mechanism: different namespace per result type.
    public static <T> T evaluate(String atom, Class<T> type,
                                 DanceState ds, List<Expr> args)
        throws EvaluationException {
        return lookup(atom, type).evaluate(type, ds, args);
    }
    // namespace mechanism.
    @SuppressWarnings("unchecked") // dispatch mechanism needs crazy casts
    private static final <T> ExprFunc<? extends T> lookup(String atom,
                                                          Class<T> type)
        throws EvaluationException {
        final String atomP = atom.toLowerCase().replace(' ', '_');
        if (exprGenericFuncs.containsKey(atomP))
            return (ExprFunc<? extends T>) exprGenericFuncs.get(atomP);
        if (type.isAssignableFrom(Fraction.class) &&
            exprMathFuncs.containsKey(atomP))
            return (ExprFunc<? extends T>) exprMathFuncs.get(atomP);
        if (type.isAssignableFrom(Boolean.class) &&
            PredicateList.predicates.containsKey(atomP))
            return (ExprFunc<? extends T>) (ExprFunc<Boolean>)
                PredicateList.predicates.get(atomP);
        if (type.isAssignableFrom(Evaluator.class)) {
            try {
                return (ExprFunc<? extends T>) (ExprFunc<Evaluator>)
                    CallDB.INSTANCE.lookup(atom);
            } catch (IllegalArgumentException iae) {
                // fall through
            }
        }
        throw new EvaluationException("Couldn't find function "+atom);
    }
    public static final ExprFunc<Object> LITERAL = new ExprFunc<Object>() {
        @Override
        public String getName() { return "literal"; }
        @SuppressWarnings("unchecked") // polymorphic function
        public <T> T _evaluate(Class<? super T> type,
                               DanceState ds, List<Expr> args)
            throws EvaluationException {
            if (type.isAssignableFrom(Expr.class))
                return (T) args.get(0);
            if (args.size() != 1)
                throw new EvaluationException("Missing argument to LITERAL");
            if (type.isAssignableFrom(String.class))
                return (T) this.<Expr>_evaluate(Expr.class, ds, args).atom;
            if (type.isAssignableFrom(Fraction.class))
                return (T) Fraction.valueOf
                    (this.<String>_evaluate(String.class, ds, args));
            if (type.isAssignableFrom(Boolean.class)) {
                String name = _evaluate(String.class, ds, args);
                if (name.equalsIgnoreCase("true"))
                    return (T) Boolean.TRUE;
                if (name.equalsIgnoreCase("false"))
                    return (T) Boolean.FALSE;
            }
            if (type.isAssignableFrom(Matcher.class))
                // XXX try to get matcher, otherwise make from formation.
                return (T) Matcher.valueOf
                    (this.<String>_evaluate(String.class, ds, args));
            if (type.isAssignableFrom(NamedTaggedFormation.class))
                return (T) FormationList.valueOf
                    (this.<String>_evaluate(String.class, ds, args));
            if (type.isAssignableFrom(Evaluator.class))
                // allow 'trade' to be an abbreviation of 'trade()'
                return (T) args.get(0).evaluate(type, ds);
            throw new EvaluationException("Can't evaluate LITERAL as "+type);
        }
        @Override
        public Object evaluate(Class<? super Object> type, DanceState ds,
                List<Expr> args) throws EvaluationException {
            return _evaluate(type, ds, args);
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
     *  js> c=net.cscott.sdr.calls.ast.AstNode.valueOf("(Expr _add_num '2 '1)")
     *  (Expr _add_num '2 '1)
     *  js> c.evaluate(fc, ds).toProperString()
     *  3
     */
    public static final ExprFunc<Fraction> _ADD_NUM = new MathFunc("_add_num") {
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
     *  js> c=net.cscott.sdr.calls.ast.AstNode.valueOf("(Expr _multiply_num '3 '2)")
     *  (Expr _multiply_num '3 '2)
     *  js> c.evaluate(fc, ds).toProperString()
     *  6
     */
    public static final ExprFunc<Fraction> _MULTIPLY_NUM = new MathFunc("_multiply_num") {
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
     *  js> c=net.cscott.sdr.calls.ast.AstNode.valueOf("(Expr _subtract_num '3 '2)")
     *  (Expr _subtract_num '3 '2)
     *  js> c.evaluate(fc, ds).toProperString()
     *  1
     */
    public static final ExprFunc<Fraction> _SUBTRACT_NUM = new MathFunc("_subtract_num") {
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
     *  js> c=net.cscott.sdr.calls.ast.AstNode.valueOf("(Expr _divide_num '3 '2)")
     *  (Expr _divide_num '3 '2)
     *  js> c.evaluate(fc, ds).toProperString()
     *  1 1/2
     */
    public static final ExprFunc<Fraction> _DIVIDE_NUM = new MathFunc("_divide_num") {
        @Override
        Fraction doOp(Fraction f1, Fraction f2) { return f1.divide(f2); }
        @Override
        public int maxArgs() { return 2; }
        @Override
        public int minArgs() { return 2; }
    };
    static { exprMathFuncs.put(_DIVIDE_NUM.getName(), _DIVIDE_NUM); }
}
