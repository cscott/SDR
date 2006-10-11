package net.cscott.sdr.calls.ast;

import static net.cscott.sdr.calls.ast.TokenTypes.IF;

/** <code>If</code> rejects its child as suitable for execution from
 * the current formation unless its condition evaluates true.
 * @author C. Scott Ananian
 * @version $Id: If.java,v 1.4 2006-10-11 18:27:33 cananian Exp $
 */
public class If extends Comp {
    
    public If(Condition cond, Comp child) {
        super(IF);
        addChild(cond);
        addChild(child);
    }
}
