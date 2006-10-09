package net.cscott.sdr.calls.ast;

import antlr.CommonAST;

/** This is the common superclass of all AST elements which can appear
 * in a <code>Seq</code>.
 * @author C. Scott Ananian
 * @version $Id: SeqCall.java,v 1.1 2006-10-09 19:57:12 cananian Exp $
 */
public abstract class SeqCall extends CommonAST {
    public SeqCall(int type) {
        super();
        initialize(type, this.getClass().getName().replaceAll("[^.]+[.]",""));
    }
}
