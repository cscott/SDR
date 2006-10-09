package net.cscott.sdr.calls.ast;

import static net.cscott.sdr.calls.CallFileParserTokenTypes.SELECT;

import java.util.List;

import antlr.CommonAST;

/** <code>ParCall</code> bundles a selector with a
 * <code>Comp</code>.
 * @author C. Scott Ananian
 * @version $Id: ParCall.java,v 1.1 2006-10-09 19:57:12 cananian Exp $
 */
public class ParCall extends CommonAST {
    // XXX selector?
    public ParCall(List<String> selectors, Comp child) {
        super();
        initialize(SELECT, "Select");
        addChild(child);
    }
}
