package net.cscott.sdr.calls.ast;

import static net.cscott.sdr.calls.ast.TokenTypes.CONDITION;

/** <code>If</code> rejects its child as suitable for execution from
 * the current formation unless its condition evaluates true.
 * @author C. Scott Ananian
 * @version $Id: If.java,v 1.3 2006-10-11 15:39:22 cananian Exp $
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
