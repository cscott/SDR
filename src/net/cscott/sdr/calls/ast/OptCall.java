package net.cscott.sdr.calls.ast;

import static net.cscott.sdr.calls.transform.AstTokenTypes.FROM;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.cscott.sdr.calls.Selector;
import net.cscott.sdr.calls.transform.TransformVisitor;
import net.cscott.sdr.calls.transform.ValueVisitor;

/** <code>OptCall</code> bundles a formation condition with a
 * <code>Comp</code>.
 * @author C. Scott Ananian
 * @version $Id: OptCall.java,v 1.7 2006-10-19 18:44:50 cananian Exp $
 */
public class OptCall extends AstNode {
    public final List<Selector> selectors;
    public final Comp child;
    // use 'parseFormations' if you want to create an OptCall from a list of
    // strings.
    public OptCall(List<Selector> selectors, Comp child) {
        this(selectors.toArray(new Selector[selectors.size()]), child);
    }
    // does not make a copy of selectors.
    private OptCall(Selector[] selectors, Comp child) {
        super(FROM, "From");
        this.selectors = Collections.unmodifiableList
        (Arrays.asList(selectors));
        this.child = child;
    }
    @Override
    public <T> OptCall accept(TransformVisitor<T> v, T t) {
        return v.visit(this, t);
    }
    @Override
    public <RESULT,CLOSURE>
    RESULT accept(ValueVisitor<RESULT,CLOSURE> v, CLOSURE cl) {
        return v.visit(this, cl);
    }
    
    public static List<Selector> parseFormations(List<String> formations) {
        List<Selector> sels = new ArrayList<Selector>(formations.size());
        for (String s: formations)
            sels.add(Selector.valueOf(s));
        return sels;
    }
    /** Factory: creates new OptCall only if it would differ from this. */
    public OptCall build(List<Selector> selectors, Comp child) {
        if (this.selectors.equals(selectors) && this.child==child)
            return this;
        return new OptCall(selectors.toArray(new Selector[selectors.size()]),
                child);
    }
    @Override
    public String argsToString() {
        StringBuilder sb = new StringBuilder();
        sb.append(selectors);
        sb.append(' ');
        sb.append(child);
        return sb.toString();
    }
}
