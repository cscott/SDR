package net.cscott.sdr.calls.ast;

import net.cscott.sdr.calls.transform.TransformVisitor;
import antlr.CommonAST;

/** This is the common superclass of all AST elements which can appear
 * in a <code>Seq</code>.
 * @author C. Scott Ananian
 * @version $Id: SeqCall.java,v 1.2 2006-10-17 01:53:57 cananian Exp $
 */
public abstract class SeqCall extends CommonAST {
    public SeqCall(int type) {
        super();
        initialize(type, this.getClass().getName().replaceAll("[^.]+[.]",""));
    }
    
    public abstract <T> SeqCall accept(TransformVisitor<T> v, T t);
}
