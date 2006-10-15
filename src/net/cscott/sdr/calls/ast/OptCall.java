package net.cscott.sdr.calls.ast;

import static net.cscott.sdr.calls.ast.TokenTypes.FROM;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.cscott.sdr.calls.Selector;
import net.cscott.sdr.calls.TaggedFormation.Tag;

import antlr.CommonAST;

/** <code>OptCall</code> bundles a formation condition with a
 * <code>Comp</code>.
 * @author C. Scott Ananian
 * @version $Id: OptCall.java,v 1.4 2006-10-15 03:15:05 cananian Exp $
 */
public class OptCall extends CommonAST {
    private Selector[] selectors;
    public OptCall(List<String> formations, Comp child) {
        this(parseFormations(formations), child);
    }
    public OptCall(Selector[] selectors, Comp child) {
        super();
        initialize(FROM, "From");
        addChild(child);
        this.selectors = selectors.clone();
    }
    public List<Selector> getSelectors() {
        return Collections.unmodifiableList(Arrays.asList(selectors));
    }
    
    private static Selector[] parseFormations(List<String> formations) {
        List<Selector> sels = new ArrayList<Selector>(formations.size());
        for (String s: formations)
            sels.add(Selector.valueOf(s));
        return sels.toArray(new Selector[sels.size()]);
    }
    /** Factory: creates new OptCall only if it would differ from this. */
    public OptCall build(List<Selector> selectors, Comp child) {
        if (selectors.equals(Arrays.asList(this.selectors)) &&
                child==this.getFirstChild())
            return this;
        return new OptCall(selectors.toArray(new Selector[selectors.size()]),
                child);
    }
}
