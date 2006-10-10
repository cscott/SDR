package net.cscott.sdr.calls.ast;

import static net.cscott.sdr.calls.transform.CallFileParserTokenTypes.CONDITION;

/** <code>If</code> rejects its child as suitable for execution from
 * the current formation unless its condition evaluates true.
 * @author C. Scott Ananian
 * @version $Id: If.java,v 1.2 2006-10-10 18:57:30 cananian Exp $
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
