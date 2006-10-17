package net.cscott.sdr.calls.ast;

import net.cscott.sdr.calls.transform.TransformVisitor;

/** This is the common superclass of all AST elements which can appear
 * in a <code>Seq</code>.
 * @author C. Scott Ananian
 * @version $Id: SeqCall.java,v 1.3 2006-10-17 16:29:05 cananian Exp $
 */
public abstract class SeqCall extends AstNode {
    public SeqCall(int type) {
        super(type);
    }
    @Override
    public abstract <T> SeqCall accept(TransformVisitor<T> v, T t);
}
