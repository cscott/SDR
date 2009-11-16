package net.cscott.sdr.calls.ast;

import static net.cscott.sdr.calls.transform.AstTokenTypes.FROM;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.cscott.sdr.calls.Matcher;
import net.cscott.sdr.calls.transform.TransformVisitor;
import net.cscott.sdr.calls.transform.ValueVisitor;

/** <code>OptCall</code> bundles a formation condition with a
 * <code>Comp</code>.
 * @author C. Scott Ananian
 * @version $Id: OptCall.java,v 1.7 2006-10-19 18:44:50 cananian Exp $
 */
public class OptCall extends AstNode {
    public final List<Matcher> matchers;
    public final Comp child;
    // use 'parseFormations' if you want to create an OptCall from a list of
    // strings.
    public OptCall(List<Matcher> matchers, Comp child) {
        this(matchers.toArray(new Matcher[matchers.size()]), child);
    }
    // does not make a copy of matchers.
    private OptCall(Matcher[] matchers, Comp child) {
        super(FROM, "From");
        this.matchers = Collections.unmodifiableList(Arrays.asList(matchers));
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
    
    public static List<Matcher> parseFormations(List<String> formations) {
        List<Matcher> lm = new ArrayList<Matcher>(formations.size());
        for (String s: formations)
            lm.add(Matcher.valueOf(s));
        return lm;
    }
    /** Factory: creates new OptCall only if it would differ from this. */
    public OptCall build(List<Matcher> matchers, Comp child) {
        if (this.matchers.equals(matchers) && this.child==child)
            return this;
        return new OptCall(matchers.toArray(new Matcher[matchers.size()]),
                child);
    }
    @Override
    public String argsToString() {
        StringBuilder sb = new StringBuilder();
        sb.append(matchers);
        sb.append(' ');
        sb.append(child);
        return sb.toString();
    }
}
