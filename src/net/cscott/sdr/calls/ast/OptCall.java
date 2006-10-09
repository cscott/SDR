package net.cscott.sdr.calls.ast;

import static net.cscott.sdr.calls.CallFileParserTokenTypes.FROM;

import java.util.List;

import antlr.CommonAST;

/** <code>OptCall</code> bundles a formation condition with a
 * <code>Comp</code>.
 * @author C. Scott Ananian
 * @version $Id: OptCall.java,v 1.1 2006-10-09 19:57:12 cananian Exp $
 */
public class OptCall extends CommonAST {
    // XXX formation specifier?
    public OptCall(List<String> formations, Comp child) {
        super();
        initialize(FROM, "From");
        addChild(child);
    }
}
