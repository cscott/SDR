package net.cscott.sdr.calls.grm;

import net.cscott.sdr.calls.grm.Grm.Alt;
import net.cscott.sdr.calls.grm.Grm.Concat;
import net.cscott.sdr.calls.grm.Grm.Mult;
import net.cscott.sdr.calls.grm.Grm.Nonterminal;
import net.cscott.sdr.calls.grm.Grm.Terminal;
import net.cscott.sdr.util.Tools;

import java.util.*;

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

}
