package net.cscott.sdr.calls;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import net.cscott.sdr.calls.TaggedFormation.Tag;
import net.cscott.sdr.calls.ast.Expr;

/** List of useful {@link Selector}s. */
public class SelectorList {
    private SelectorList() {}
    private static Map<String,ExprFunc<Selector>> selectorList =
        new LinkedHashMap<String,ExprFunc<Selector>>();

    /** Return the {@link Selector} function with the given (case-insensitive)
     *  name.
     * @throws IllegalArgumentException if no selector with the given name is
     *         found.
     */
    public static ExprFunc<Selector> valueOf(String s) {
        String ss = normalize(s);
        if (!selectorList.containsKey(ss))
            throw new IllegalArgumentException("No such selector: "+s);
        return selectorList.get(ss);
    }

    /** Create an {@link ExprFunc} for the given {@link Tag}. */
    private static ExprFunc<Selector> tagSelectorFunc(final Tag t) {
        final String name = normalize(t.name());
        final Selector selector = new Selector() {
            @Override
            public Set<Dancer> select(TaggedFormation tf) {
                return tf.tagged(t);
            }
        };
        return new ExprFunc<Selector>() {
            @Override
            public String getName() { return name; }
            @Override
            public Selector evaluate(Class<? super Selector> type,
                                     DanceState ds, List<Expr> args)
                throws EvaluationException {
                return selector;
            }
        };
    }
    /** Add Selectors for all tags. */
    static {
        for (Tag t : Tag.values())
            addToList(tagSelectorFunc(t));
    }

    /** Selector which matches no dancers. */
    public static ExprFunc<Selector> NONE = new ExprFunc<Selector>() {
        private final Selector selector = new Selector() {
            @Override
            public Set<Dancer> select(TaggedFormation tf) {
                return Collections.emptySet();
            }
        };
        @Override
        public String getName() { return "none"; }
        @Override
        public Selector evaluate(Class<? super Selector> type,
                                 DanceState ds, List<Expr> args)
                throws EvaluationException {
            return selector;
        }
    };
    static { addToList(NONE); }

    /** Human-friendly synonym for ALL. */
    public static ExprFunc<Selector> OTHERS = new ExprFunc<Selector>() {
        private ExprFunc<Selector> ALL = valueOf("all");
        @Override
        public String getName() { return "others"; }
        @Override
        public Selector evaluate(Class<? super Selector> type,
                                 DanceState ds, List<Expr> args)
                throws EvaluationException {
            return ALL.evaluate(type, ds, args);
        }
    };
    static { addToList(OTHERS); }

    /** Selector combiner: select dancers who <i>don't</i> match the selector
     *  argument. */
    public static ExprFunc<Selector> NOT = new ExprFunc<Selector>() {
        @Override
        public String getName() { return "not"; }
        @Override
        public Selector evaluate(Class<? super Selector> type,
                                 final DanceState ds, List<Expr> args)
            throws EvaluationException {
            if (args.size()!=1)
                throw new EvaluationException("only one argument to not");
            final Selector arg = args.get(0).evaluate(Selector.class, ds);
            return new Selector() {
                @Override
                public Set<Dancer> select(TaggedFormation tf) {
                    Set<Dancer> d = new LinkedHashSet<Dancer>(tf.dancers());
                    d.removeAll(arg.select(tf));
                    return d;
                }
            };
        }
    };
    static { addToList(NOT); }

    /** Selector combiner: select dancers who match all of the selector
     *  arguments. */
    public static ExprFunc<Selector> AND = new ExprFunc<Selector>() {
        @Override
        public String getName() { return "and"; }
        @Override
        public Selector evaluate(Class<? super Selector> type,
                                 final DanceState ds, List<Expr> args)
            throws EvaluationException {
            final List<Selector> ls = new ArrayList<Selector>(args.size());
            for (Expr arg : args)
                ls.add(arg.evaluate(Selector.class, ds));
            return new Selector() {
                @Override
                public Set<Dancer> select(TaggedFormation tf) {
                    Set<Dancer> d = new LinkedHashSet<Dancer>(tf.dancers());
                    for (Selector s : ls)
                        d.retainAll(s.select(tf));
                    return d;
                }
            };
        }
    };
    static { addToList(AND); }

    /** Selector combiner: select dancers who match any of the selector
     *  arguments. */
    public static ExprFunc<Selector> OR = new ExprFunc<Selector>() {
        @Override
        public String getName() { return "or"; }
        @Override
        public Selector evaluate(Class<? super Selector> type,
                                 final DanceState ds, List<Expr> args)
            throws EvaluationException {
            final List<Selector> ls = new ArrayList<Selector>(args.size());
            for (Expr arg : args)
                ls.add(arg.evaluate(Selector.class, ds));
            return new Selector() {
                @Override
                public Set<Dancer> select(TaggedFormation tf) {
                    Set<Dancer> d = new LinkedHashSet<Dancer>();
                    for (Selector s : ls)
                        d.addAll(s.select(tf));
                    return d;
                }
            };
        }
    };
    static { addToList(OR); }

    /** Select dancers matched on a regex match against a dancer pattern.
     *  @see ExprList#_FACING_PATTERN
     *  @see ExprList#_ROLL_PATTERN
     *  @see ExprList#_INOUT_PATTERN
     */
    public static ExprFunc<Selector> MATCH = new ExprFunc<Selector>() {
        @Override
        public String getName() { return "match"; }
        @Override
        public Selector evaluate(Class<? super Selector> type,
                                 final DanceState ds, List<Expr> args)
            throws EvaluationException {
            if (args.size()!=2)
                throw new EvaluationException("needs two arguments");
            final String dancers = args.get(0).evaluate(String.class, ds);
            final Pattern pattern = Pattern.compile
                (args.get(1).evaluate(String.class, ds),
                 Pattern.CASE_INSENSITIVE);
            return new Selector() {
                @Override
                public Set<Dancer> select(TaggedFormation tf) {
                    Set<Dancer> result = new LinkedHashSet<Dancer>();
                    List<Dancer> sortedDancers = tf.sortedDancers();
                    for (int i=0; i<sortedDancers.size(); i++) {
                        String d = dancers.substring(i, i+1);
                        if (pattern.matcher(d).matches())
                            result.add(sortedDancers.get(i));
                    }
                    return result;
                }
            };
        }
    };
    static { addToList(MATCH); }

    /** Complex selector: do a formation match and select tagged dancers from
     *  the match -- but don't change the dance state.  Most useful for
     *  'ends in' conditions, which don't have a TaggedFormation handy. */
    public static ExprFunc<Selector> FORMATION = new ExprFunc<Selector>() {
        @Override
        public String getName() { return "formation"; }
        @Override
        public Selector evaluate(Class<? super Selector> type,
                                 final DanceState ds, List<Expr> args)
            throws EvaluationException {
            if (args.size()!=2)
                throw new EvaluationException("needs two arguments");
            final Matcher m = args.get(0).evaluate(Matcher.class, ds);
            final Selector s = args.get(1).evaluate(Selector.class, ds);
            return new Selector() {
                @Override
                public Set<Dancer> select(TaggedFormation tf) {
                    try {
                        FormationMatch fm = m.match(Breather.breathe(tf));
                        Set<Dancer> d = new LinkedHashSet<Dancer>();
                        for (TaggedFormation ntf : fm.matches.values())
                            d.addAll(s.select(ntf));
                        return d;
                    } catch (NoMatchException nme) {
                        // hm, didn't match, so say no dancers matched
                        return Collections.emptySet();
                    }
                }
            };
        }
    };
    static { addToList(FORMATION); }

    // Keep a list of Selector functions. //////////////////////
    private static String normalize(String s) {
        return s.toLowerCase().replace('_', ' ').intern();
    }
    private static void addToList(ExprFunc<Selector> s) {
        assert normalize(s.getName()).equals(s.getName());
        selectorList.put(s.getName(), s);
    }
}
