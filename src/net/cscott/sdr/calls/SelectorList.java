package net.cscott.sdr.calls;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import net.cscott.sdr.calls.TaggedFormation.Tag;
import net.cscott.sdr.calls.ast.Expr;

/** List of useful {@link Selector}s. */
public class SelectorList {
    private SelectorList() {}

    public Selector tagSelector(final Tag t) {
        return new Selector() {
            @Override
            public Set<Dancer> select(TaggedFormation tf) {
                return tf.tagged(t);
            }
        };
    }
    public ExprFunc<Selector> AND = new ExprFunc<Selector>() {
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
    public ExprFunc<Selector> OR = new ExprFunc<Selector>() {
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
}
