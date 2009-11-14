package net.cscott.sdr.calls;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.cscott.sdr.calls.ast.Expr;
import net.cscott.sdr.calls.ExprFunc.EvaluationException;
import net.cscott.sdr.util.Fraction;

public abstract class ExprList {
    // XXX move all evaluation to net.cscott.sdr.calls.ExprFunc
    //     static method evaluate() there.  The ast just represents the
    //     computation, it doesn't perform it.
    // XXX provide implementation that looks up 'atom' in ExprList,
    // SelectorList, depending on the type requested.  This will be the
    // 'name spacing' mechanism: different namespace per result type.
    public static <T> T evaluate(String atom, Class<T> type,
                                 DanceState ds, List<Expr> args)
        throws EvaluationException {
        return lookup(atom, type).evaluate(type, ds, args);
    }
    // namespacing mechanism.
    private static final ExprFunc lookup(final String atom, Class<?> type)
        throws EvaluationException {
        if (exprFuncs.containsKey(atom))
            return exprFuncs.get(atom);
        throw new EvaluationException("Couldn't find function "+atom);
    }
    
    public static final ExprFunc CALL = new ExprFunc() {
        @Override
        public String getName() { return "call"; }
        @Override
        public <T> T evaluate(Class<T> type, DanceState ds, List<Expr> args)
                throws EvaluationException {
            // TODO Auto-generated method stub
            return null;
        }
    };
    public static final ExprFunc SELECTOR  = new ExprFunc() {
        @Override
        public String getName() { return "selector"; }
        @Override
        public <T> T evaluate(Class<T> type, DanceState ds, List<Expr> args)
                throws EvaluationException {
            // TODO Auto-generated method stub
            return null;
        }
    };
    public static final ExprFunc PREDICATE = new ExprFunc() {
        @Override
        public String getName() { return "predicate"; }
        @Override
        public <T> T evaluate(Class<T> type, DanceState ds, List<Expr> args)
                throws EvaluationException {
            // TODO Auto-generated method stub
            return null;
        }
    };
    public static final ExprFunc LITERAL = new ExprFunc() {
        @Override
        public String getName() { return "literal"; }
        @SuppressWarnings("unchecked")
        public <T> T evaluate(Class<T> type, DanceState ds, List<Expr> args)
            throws EvaluationException {
            if (type.isAssignableFrom(Expr.class))
                return (T) args.get(0);
            if (args.size()!=1)
                throw new EvaluationException("Missing argument to LITERAL");
            if (type.isAssignableFrom(String.class))
                return (T) evaluate(Expr.class, ds, args).atom;
            if (type.isAssignableFrom(Fraction.class))
                return (T) Fraction.valueOf(evaluate(String.class, ds, args));
            if (type.isAssignableFrom(Boolean.class)) {
                String name = evaluate(String.class, ds, args);
                if (name.equalsIgnoreCase("true"))
                    return (T) Boolean.TRUE;
                if (name.equalsIgnoreCase("false"))
                    return (T) Boolean.FALSE;
            }
            if (type.isAssignableFrom(Selector.class))
                // XXX try to get selector, otherwise make from formation.
                return (T) Selector.valueOf(evaluate(String.class, ds, args));
            if (type.isAssignableFrom(NamedTaggedFormation.class))
                return (T) FormationList.valueOf(evaluate(String.class, ds, args));
            throw new EvaluationException("Can't evaluate LITERAL as "+type);
        }
    };
    /** List of all the {@link Predicate}s defined here. */
    public final static Map<String, ExprFunc> exprFuncs;
    static {
        Map<String,ExprFunc> _exprFuncs = new LinkedHashMap<String,ExprFunc>();
        for (Field f : ExprList.class.getDeclaredFields()) {
            if (f.getName().equals(f.getName().toUpperCase()) &&
                ExprFunc.class.isAssignableFrom(f.getType()) &&
                Modifier.isStatic(f.getModifiers())) {
                try {
                    ExprFunc ef = (ExprFunc) f.get(null);
                    _exprFuncs.put(ef.getName(), ef);
                } catch (Throwable t) {
                    assert false : "unreachable";
                }
            }
        }
        exprFuncs = Collections.unmodifiableMap(_exprFuncs);
    }
}
