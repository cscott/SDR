package net.cscott.sdr.calls.ast;

import static net.cscott.sdr.calls.ast.TokenTypes.PAR;
/**
 * <code>Par</code> is a list of call pieces.  Each piece has
 * an associated selector.  Every member of the formation must match
 * at least one selector.  Each person executes the piece corresponding to
 * the first selector which matches them, in parallel.
 * @author C. Scott Ananian
 * @version $Id: Par.java,v 1.2 2006-10-10 18:57:30 cananian Exp $
 */
public class Par extends Comp {
    private final ParCall[] children;
    public Par(ParCall... children) {
        super(PAR);
        this.children = children;
        for (ParCall pc : children)
            addChild(pc);
    }
}
