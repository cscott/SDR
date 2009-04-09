/**
 * 
 */
package net.cscott.sdr.calls.grm;

import java.util.HashMap;
import java.util.Map;

import net.cscott.sdr.calls.grm.Grm.Alt;
import net.cscott.sdr.calls.grm.Grm.Concat;
import net.cscott.sdr.calls.grm.Grm.Mult;
import net.cscott.sdr.calls.grm.Grm.Nonterminal;
import net.cscott.sdr.calls.grm.Grm.Terminal;

class NumberParams extends GrmVisitor<Void> {
    public final Map<Integer,Integer> paramToOrder =
        new HashMap<Integer,Integer>();
    private int order;
    private boolean inMult=false;
    NumberParams(Grm g) { this(g, 0); }
    NumberParams(Grm g, int start) {
        this.order = start;
        g.accept(this);
    }

    @Override
    public Void visit(Alt alt) {
        int o = order;
        for (Grm g : alt.alternates) {
            order = o;
            g.accept(this);
        }
        return null;
    }
    @Override
    public Void visit(Concat concat) {
        for (Grm g : concat.sequence)
            g.accept(this);
        return null;
    }
    @Override
    public Void visit(Mult mult) {
        boolean m = inMult;
        if (mult.type!=Mult.Type.QUESTION)
            inMult=true;
        mult.operand.accept(this);
        inMult=m;
        return null;
    }
    @Override
    public Void visit(Nonterminal nonterm) {
        int o = order++;
        if (nonterm.param>=0) {
            assert !inMult : "named non-terminal can repeat";
            // check that nonterm is not already present.
            if (paramToOrder.containsKey(nonterm.param))
                assert o == paramToOrder.get(nonterm.param) :
                    "nonterminal used with different ordering "+
                    "in alternatives";
            paramToOrder.put(nonterm.param, o);
        }
        return null;
    }
    @Override
    public Void visit(Terminal term) {
        // nothing to see here.
        return null;
    }
}
