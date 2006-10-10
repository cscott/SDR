package net.cscott.sdr.calls.ast;

import static net.cscott.sdr.calls.ast.TokenTypes.CALL;

import java.util.concurrent.Future;

/** <code>Sub</code> executes another Comp.
 * This allows us to define complicated calls using simpler calls.
 * <code>Sub</code> is a leaf node.
 * @author C. Scott Ananian
 * @version $Id: Sub.java,v 1.2 2006-10-10 18:57:30 cananian Exp $
 */
public class Sub extends SeqCall {
    // XXX: how to represent sub call?
    private Future<Comp> subCall;
    
    public Sub(Future<Comp> subCall) {
        super(CALL);
        this.subCall = subCall;
    }

    public Comp subCall() {
        try {
            return subCall.get(); 
        } catch (Throwable t) {
            // XXX!
            throw new RuntimeException("whoops");
        }
    }
}
