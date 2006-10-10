package net.cscott.sdr.calls.ast;

import static net.cscott.sdr.calls.ast.TokenTypes.IN;
import net.cscott.sdr.util.*;

/** <code>In</code> alters the timing of its child such that it
 * executes in exactly <code>count</code> beats.
 * @author C. Scott Ananian
 * @version $Id: In.java,v 1.2 2006-10-10 18:57:30 cananian Exp $
 */
public class In extends Comp {
    public final Fraction count;
    public final Comp child;
    
    public In(Fraction count, Comp child) {
        super(IN);
        this.count = count;
        this.child = child;
        addChild(child);
    }
    public String toString() {
        return super.toString()+" "+count.toProperString();
    }
}
