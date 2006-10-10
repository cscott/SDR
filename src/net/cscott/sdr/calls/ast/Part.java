package net.cscott.sdr.calls.ast;

import static net.cscott.sdr.calls.ast.TokenTypes.PART;
/**
 * <code>Part</code> denotes an indivisible part of a call.  Calls can not be
 * fractionalized below a <code>Part</code>.  It also denotes timing:
 * each part executes in the same amount of time (unless modified by
 * <code>In</code>. <code>Part</code> has exactly one child, which is a
 * <code>CallPiece</code>.
 * @author C. Scott Ananian
 * @version $Id: Part.java,v 1.2 2006-10-10 18:57:30 cananian Exp $
 */
public class Part extends SeqCall {
    public final Comp child;
    public Part(Comp child) {
        super(PART);
        this.child = child;
        addChild(child);
    }
}
