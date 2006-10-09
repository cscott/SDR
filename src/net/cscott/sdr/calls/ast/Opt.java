package net.cscott.sdr.calls.ast;

import static net.cscott.sdr.calls.CallFileParserTokenTypes.OPT;
/**
 * <code>Opt</code> is a list of call options.  Each option has
 * an associated formation.  This first option whose formation is matchable
 * against the current formation is used to perform the call; the rest are
 * ignored.
 * @author C. Scott Ananian
 * @version $Id: Opt.java,v 1.1 2006-10-09 19:57:12 cananian Exp $
 */
public class Opt extends Comp {
    private final OptCall[] children;
    public Opt(OptCall... children) {
        super(OPT);
        this.children = children;
        for (OptCall oc : children)
            addChild(oc);
    }
}
