package net.cscott.sdr.calls.grm;

/** Instance of the visitor pattern for {@link Grm}. */
public abstract class GrmVisitor<T> {
    public abstract T visit(Grm.Alt alt);
    public abstract T visit(Grm.Concat concat);
    public abstract T visit(Grm.Mult mult);
    public abstract T visit(Grm.Nonterminal nonterm);
    public abstract T visit(Grm.Terminal term);
}
