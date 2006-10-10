package net.cscott.sdr.calls.ast;

import static net.cscott.sdr.calls.ast.TokenTypes.SEQ;
/**
 * <code>Seq</code> is the serial composition of primitive call pieces.
 * @author C. Scott Ananian
 * @version $Id: Seq.java,v 1.2 2006-10-10 18:57:30 cananian Exp $
 */
public class Seq extends Comp {
    private final SeqCall[] children;
    public Seq(SeqCall... children) {
        super(SEQ);
        this.children = children;
        for (SeqCall sc : children)
            addChild(sc);
    }
}
