package net.cscott.sdr.calls.ast;

import static net.cscott.sdr.calls.ast.TokenTypes.IN;

import java.util.Arrays;
import java.util.List;

import net.cscott.sdr.calls.Selector;
import net.cscott.sdr.util.*;

/** <code>In</code> alters the timing of its child such that it
 * executes in exactly <code>count</code> beats.
 * @author C. Scott Ananian
 * @version $Id: In.java,v 1.3 2006-10-15 03:15:04 cananian Exp $
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
    @Override
    public String toString() {
        return super.toString()+" "+count.toProperString();
    }
    /** Factory: creates new In only if it would differ from this. */
    public In build(Fraction count, Comp child) {
        if (count.equals(this.count) && child==this.getFirstChild())
            return this;
        return new In(count, child);
    }
}
