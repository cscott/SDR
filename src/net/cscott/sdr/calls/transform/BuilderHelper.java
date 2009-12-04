package net.cscott.sdr.calls.transform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import net.cscott.sdr.calls.Call;
import net.cscott.sdr.calls.DanceState;
import net.cscott.sdr.calls.ExactRotation;
import net.cscott.sdr.calls.Program;
import net.cscott.sdr.calls.ast.Apply;
import net.cscott.sdr.calls.ast.Comp;
import net.cscott.sdr.calls.ast.Expr;
import net.cscott.sdr.calls.ast.If;
import net.cscott.sdr.calls.ast.In;
import net.cscott.sdr.calls.ast.Opt;
import net.cscott.sdr.calls.ast.OptCall;
import net.cscott.sdr.calls.ast.Par;
import net.cscott.sdr.calls.ast.ParCall;
import net.cscott.sdr.calls.ast.Part;
import static net.cscott.sdr.calls.ast.Part.Divisibility.*;
import net.cscott.sdr.calls.ast.Prim;
import net.cscott.sdr.calls.ast.Seq;
import net.cscott.sdr.calls.ast.SeqCall;
import net.cscott.sdr.calls.ast.Part.Divisibility;
import net.cscott.sdr.calls.ast.Prim.Direction;
import net.cscott.sdr.calls.grm.Rule;
import net.cscott.sdr.util.Fraction;
/** 
 * The {@link BuilderHelper} class helps with the generation of parameterized
 * calls.  It supports an abstraction which lets us treat AST trees as
 * "AST tree generation functions", while optimizing the case where the
 * function generates a constant.
 * @author C. Scott Ananian
 */
abstract class BuilderHelper {
    /** An enumeration of directions, as specified in the call file. */
    static enum BDirection {
        ASIS, IN, OUT;
        /** Translate {@link BDirection}s to {@link Direction}s. */
        Prim.Direction primDir() {
            switch (this) {
            case OUT:
            case IN: return Prim.Direction.IN;
            default: assert false;
            case ASIS: return Prim.Direction.ASIS;
            }
        }
        /** Flip the sign of the given fraction if {@link BDirection} is OUT. */
        Fraction setSign(Fraction f) {
            return (this==OUT) ? f.negate() : f;
        }
        ExactRotation setSign(ExactRotation r) {
            return (this==OUT) ? r.negate() : r;
        }
    }
    /**
     * 'B' is pronounced as 'Builder'.  So a B&lt;Prim&gt; builds Prim objects.
     */
    static abstract class B<T> {
        public abstract T build(List<Expr> args);
        /** 
         * Returns true if the build operation will succeed given a zero-length
         * argument list.
         */
        public boolean isConstant() { return false; } // always safe
    }
    static <T> B<T> mkConstant(final T t) {
        return new B<T>() {
            @Override
            public T build(List<Expr> args) { return t; }
            @Override
            public boolean isConstant() { return true; }
        };
    }
    static <T> boolean isConstant(List<? extends B<? extends T>> l) {
        for (B<? extends T> b : l)
            if (!b.isConstant()) return false;
        return true;
    }
    static <T> List<T> reduce(List<? extends B<? extends T>> l, List<Expr> args) {
        List<T> ll = new ArrayList<T>(l.size());
        for (B<? extends T> b : l)
            ll.add(b.build(args));
        return ll;
    }
    static <T> B<T> optimize(B<T> b, boolean isConstant) {
        return (isConstant) ? mkConstant(b.build(null)) : b;
    }
    static B<Apply> mkApply(final B<Expr> call) {
        return optimize(new B<Apply>() {
            public Apply build(List<Expr> fargs) {
                return new Apply(call.build(fargs));
            }
        }, call.isConstant());
    }
    static B<Expr> mkExpr(final String predicate, final List<B<Expr>> args) {
        return optimize(new B<Expr>() {
            public Expr build(List<Expr> fargs) {
                return new Expr(predicate, reduce(args, fargs));
            }
        }, isConstant(args));
    }
    static B<If> mkIf(final If.When when,
		      final B<Expr> cond, final Fraction priority,
                      final String msg, final B<? extends Comp> child) {
        return optimize(new B<If>() {
            public If build(List<Expr> fargs) {
                return new If(when, cond.build(fargs), child.build(fargs),
                              msg, priority);
            }
        }, cond.isConstant() && child.isConstant());
    }
    static B<In> mkIn(final B<Expr> count, final B<? extends Comp> child) {
        return optimize(new B<In>() {
            public In build(List<Expr> fargs) {
                return new In(count.build(fargs), child.build(fargs));
            }
        }, count.isConstant() && child.isConstant());
    }
    static B<Opt> mkOpt(final List<B<OptCall>> children) {
        return optimize(new B<Opt>() {
            public Opt build(List<Expr> fargs) {
                return new Opt(reduce(children, fargs));
            }
        }, isConstant(children));
    }
    static B<OptCall> mkOptCall(final B<Expr> matcher, final B<? extends Comp> child) {
        return optimize(new B<OptCall>() {
            public OptCall build(List<Expr> fargs) {
                return new OptCall(matcher.build(fargs), child.build(fargs));
            }
        }, matcher.isConstant() && child.isConstant());
    }
    static B<Par> mkPar(final List<B<ParCall>> children) {
        return optimize(new B<Par>() {
            public Par build(List<Expr> fargs) {
                return new Par(reduce(children, fargs));
            }
        }, isConstant(children));
    }
    static B<ParCall> mkParCall(final B<Expr> selector, final B<? extends Comp> child) {
        return optimize(new B<ParCall>() {
            public ParCall build(List<Expr> fargs) {
                return new ParCall(selector.build(fargs), child.build(fargs));
            }
        }, child.isConstant() && selector.isConstant());
    }
    static B<Part> mkPart(final Divisibility divisible, final B<Expr> howMany, final B<? extends Comp> child) {
        return optimize(new B<Part>() {
            public Part build(List<Expr> fargs) {
                return new Part(divisible, howMany.build(fargs), child.build(fargs));
            }
        }, child.isConstant());
    }
    static B<Prim> mkPrim(final BDirection dirX, final Fraction x,
            final BDirection dirY, final Fraction y,
            final BDirection dirRot, final ExactRotation rot,
            final Set<Prim.Flag> flags) {
        return mkConstant(new Prim(dirX.primDir(), dirX.setSign(x),
                                   dirY.primDir(), dirY.setSign(y),
                                   dirRot.primDir(), dirRot.setSign(rot),
                                   Fraction.ONE,
				   flags.toArray(new Prim.Flag[flags.size()])
				   ));
    }
    static B<Seq> mkSeq(final List<B<? extends SeqCall>> children) {
        return optimize(new B<Seq>() {
            public Seq build(List<Expr> fargs) {
                return new Seq(reduce(children, fargs));
            }
        }, isConstant(children));
    }
    //////////////
    /** Calls can have defaults for arguments. */
    static class ArgAndDefault {
        public final String name, defaultValue;
        public ArgAndDefault(String name, String defaultValue) {
            this.name = name; this.defaultValue = defaultValue;
        }
        public String toString() {
            if (defaultValue==null) return name;
            return name+"="+defaultValue;
        }
    }
    //////////////
    static Call makeCall(final String name, final Program program,
            final B<? extends Comp> b, List<ArgAndDefault> args,
            final Rule rule) {
        if (b.isConstant() && args.size()==0)
            return Call.makeSimpleCall(name,program,b.build(null), rule);
        // min # of arguments is the last arg w/o a default.
        int i;
        for (i=args.size(); i>0; i--)
            if (args.get(i-1).defaultValue == null)
                break;
        final int minNumberOfArguments = i;
        // make default arguments list.
        final List<Expr> defaultArguments;
        for (i=args.size(); i>0; i--)
            if (args.get(i-1).defaultValue != null)
                break;
        if (i==0) /* no default arguments */
            defaultArguments = Collections.emptyList(); /* save memory */
        else {
             defaultArguments = new ArrayList<Expr>();
             for (ArgAndDefault a: args)
                 defaultArguments.add(a.defaultValue==null ? null :
                                      Expr.literal(a.defaultValue));
        }

        return new Call() {
            @Override
            public String getName() { return name; }
            @Override
            public Program getProgram() { return program; }
            @Override
            public int getMinNumberOfArguments() {
                return minNumberOfArguments;
            }
            @Override
            public List<Expr> getDefaultArguments() {
                return defaultArguments;
            }
            @Override
            public Rule getRule() { return rule; }
            @Override
            public Evaluator getEvaluator(DanceState ds, List<Expr> args) {
                assert args.size() >= minNumberOfArguments;
                List<Expr> nargs = new ArrayList<Expr>(args);
                /* add default arguments if missing */
                for (int i=nargs.size(); i<defaultArguments.size(); i++)
                    nargs.add(defaultArguments.get(i));
                return new Evaluator.Standard(b.build(nargs));
            }
        };
    }
}
