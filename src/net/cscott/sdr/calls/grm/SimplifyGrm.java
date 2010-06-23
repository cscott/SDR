package net.cscott.sdr.calls.grm;

import net.cscott.jutil.Default;
import net.cscott.jutil.PersistentSetFactory;
import net.cscott.jutil.SetFactory;
import net.cscott.sdr.calls.grm.Grm.Alt;
import net.cscott.sdr.calls.grm.Grm.Concat;
import net.cscott.sdr.calls.grm.Grm.Mult;
import net.cscott.sdr.calls.grm.Grm.Nonterminal;
import net.cscott.sdr.calls.grm.Grm.Terminal;
import net.cscott.sdr.util.Tools;

import java.util.*;

/** Basic optimizations for a grammar rule. */
public class SimplifyGrm extends GrmVisitor<Grm> {
    public static Grm simplify(Grm g) {
        return g.accept(new SimplifyGrm());
    }
    
    @Override
    public Grm visit(Alt alt) {
        assert !alt.alternates.isEmpty();
        if (alt.alternates.size()==1)
            return alt.alternates.get(0).accept(this);
        List<Grm> l = new ArrayList<Grm>(alt.alternates.size());
        Tools.ListMultiMap<Grm,Grm> commonPrefix = Tools.mml();
        for (Grm g : alt.alternates) {
            g = g.accept(this); // recursively simplify
            if (g instanceof Concat) {
                Concat c = ((Concat) g);
                Grm head = c.sequence.get(0);
                Grm tail = new Concat(c.sequence.subList(1, c.sequence.size()));
                if (head instanceof Terminal || head instanceof Nonterminal) {
                    commonPrefix.add(head, tail);
                    continue;
                }
            }
            l.add(g);
        }
        // now combine common prefixes
        for (Grm head: commonPrefix.keySet()) {
            Grm tail = new Alt(commonPrefix.getValues(head));
            l.add(new Concat(Tools.l(head, tail)).accept(this));
        }
        // sort alternates alphabetically
        Collections.sort(l, altComparator);
        return new Alt(l);
    }

    @Override
    public Grm visit(Concat concat) {
        assert !concat.sequence.isEmpty();
        if (concat.sequence.size()==1)
            return concat.sequence.get(0).accept(this);
        List<Grm> l = new ArrayList<Grm>(concat.sequence.size());
        // hoist nested concats into a single sequence.
        for (Grm g : concat.sequence) {
            g = g.accept(this);
            if (g instanceof Concat)
                l.addAll(((Concat)g).sequence);
            else
                l.add(g);
        }
        return new Concat(l);
    }

    @Override
    public Grm visit(Mult mult) {
        return new Mult(mult.operand.accept(this), mult.type);
    }

    @Override
    public Grm visit(Nonterminal nonterm) {
        return nonterm;
    }

    @Override
    public Grm visit(Terminal term) {
        return term;
    }

    /** Return the first terminal (in alphabetic order) which could be
     *  yielded by the given grammar. */
    private final static String firstTerm(Grm g) {
        // visitor to return set of terminal/nonterminals which could be first
        GrmVisitor<Set<String>> v = new GrmVisitor<Set<String>>() {
            final SetFactory<String> sf = new PersistentSetFactory<String>
                (Default.<String>comparator());
            @Override
            public Set<String> visit(Alt alt) {
                // Any of the alternates could yield a first element.
                Set<String> s = sf.makeSet();
                for (Grm g: alt.alternates)
                    s.addAll(g.accept(this));
                return s;
            }

            @Override
            public Set<String> visit(Concat concat) {
                // Only the first element -- unless it could be empty, in
                // which case it could be the second element, etc.
                Set<String> s = sf.makeSet();
                for (Grm g : concat.sequence) {
                    boolean sawEmpty = false;
                    for (String f : g.accept(this)) {
                        if (f == null) // g yield EMPTY
                            sawEmpty = true;
                        else
                            s.add(f);
                    }
                    if (!sawEmpty) return s;
                }
                // got to the end w/o getting rid of the EMPTY possibility
                s.add(null/*"empty" production*/);
                return s;
            }

            @Override
            public Set<String> visit(Mult mult) {
                // allow for the EMPTY possibility
                Set<String> s = sf.makeSet(mult.operand.accept(this));
                if (mult.type != Grm.Mult.Type.PLUS)
                    s.add(null); // possibly 0 repetitions
                return s;
            }

            @Override
            public Set<String> visit(Nonterminal nonterm) {
                /** This is where approximation occurs.  We should really
                 *  recurse into the nonterminal definition and extract a
                 *  first-symbol set, iteratively resolving circular
                 *  definitions.  Also, we'd need to determine if the
                 *  nonterm could produce the EMPTY possibility, and add
                 *  'null' if appropriate.
                 *
                 *  Instead we just pretend that the nonterminal returns
                 *  the string "<nonterminal>" with the name of the nonterminal
                 *  rule. */
                Set<String> s = sf.makeSet();
                String name = nonterm.prettyName;
                if (name==null) name = nonterm.ruleName;
                s.add("<"+name+">");
                return s;
            }

            @Override
            public Set<String> visit(Terminal term) {
                // this is the easy case
                Set<String> s = sf.makeSet();
                s.add(term.literal);
                return s;
            }
        };

        // return the alphabetically-first of the possibilities
        List<String> firsts = new ArrayList<String>(g.accept(v));
        for (Iterator<String> it=firsts.iterator(); it.hasNext(); )
            if (it.next() == null) it.remove();
        Collections.sort(firsts);
        return firsts.isEmpty() ? "" : firsts.get(0);
    }

    /** Sort grm alt options pseudo-alphabetically, using the approximate
     *  {@link #firstTerm} method. */
    private final static Comparator<Grm> altComparator = new Comparator<Grm>(){
        public int compare(Grm o1, Grm o2) {
            String f1 = firstTerm(o1), f2 = firstTerm(o2);
            return f1.compareTo(f2);
        }
    };
}
