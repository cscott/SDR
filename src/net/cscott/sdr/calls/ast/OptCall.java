package net.cscott.sdr.calls.ast;

import static net.cscott.sdr.calls.CallFileParserTokenTypes.FROM;

import java.util.ArrayList;
import java.util.List;

import net.cscott.sdr.calls.Selector;

import antlr.CommonAST;

/** <code>OptCall</code> bundles a formation condition with a
 * <code>Comp</code>.
 * @author C. Scott Ananian
 * @version $Id: OptCall.java,v 1.2 2006-10-09 21:41:49 cananian Exp $
 */
public class OptCall extends CommonAST {
    Selector[] selector;
    public OptCall(List<String> formations, Comp child) {
        this(parseFormations(formations), child);
    }
    public OptCall(Selector[] selectors, Comp child) {
        super();
        initialize(FROM, "From");
        addChild(child);
        this.selector = selectors.clone();
    }
    
    private static Selector[] parseFormations(List<String> formations) {
        List<Selector> sels = new ArrayList<Selector>(formations.size());
        for (String s: formations)
            sels.add(Selector.valueOf(s));
        return sels.toArray(new Selector[sels.size()]);
    }
}
