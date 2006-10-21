package net.cscott.sdr.calls.grm;

import net.cscott.sdr.calls.grm.Grm.Alt;
import net.cscott.sdr.calls.grm.Grm.Concat;
import net.cscott.sdr.calls.grm.Grm.Mult;
import net.cscott.sdr.calls.grm.Grm.Nonterminal;
import net.cscott.sdr.calls.grm.Grm.Terminal;
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
        for (Grm g : alt.alternates)
            l.add(g.accept(this));
        return new Alt(l);
    }

    @Override
    public Grm visit(Concat concat) {
        assert !concat.sequence.isEmpty();
        if (concat.sequence.size()==1)
            return concat.sequence.get(0).accept(this);
        List<Grm> l = new ArrayList<Grm>(concat.sequence.size());
        for (Grm g : concat.sequence)
            l.add(g.accept(this));
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
