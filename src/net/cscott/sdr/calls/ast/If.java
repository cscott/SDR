package net.cscott.sdr.calls.ast;

import static net.cscott.sdr.calls.CallFileParserTokenTypes.CONDITION;
import net.cscott.sdr.util.*;

/** <code>If</code> rejects its child as suitable for execution from
 * the current formation unless its condition evaluates true.
 * @author C. Scott Ananian
 * @version $Id: If.java,v 1.1 2006-10-09 19:57:12 cananian Exp $
 */
public class If extends Comp {
    // XXX: how to represent the condition?
    public final Comp child;
    
    public If(String condition/*XXX*/, Comp child) {
        super(CONDITION);
        this.child = child;
        addChild(child);
    }
}
