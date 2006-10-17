package net.cscott.sdr.calls.ast;

import net.cscott.sdr.calls.transform.TransformVisitor;
import net.cscott.sdr.calls.transform.ValueVisitor;

/**
 * A <code>Comp</code> is a composition operator: either a sequence,
 * an option list, a parallel split, or a restriction operator.
 * A top-level <code>Comp</code> is a call definition.  Lower-level
 * <code>Comp</code>s you can think of as call "comp"onents.
 * @author C. Scott Ananian
 * @version $Id: AstNode.java,v 1.1 2006-10-17 16:29:05 cananian Exp $
 */
public abstract class AstNode {
    private final String name;
    public final int type;
    protected AstNode(int type) {
        this(type, null);
    }
    protected AstNode(int type, String name) {
        if (name==null)
            name = getClass().getName().replaceAll("[^.]+[.]","");
        this.type = type;
        this.name = name.intern();
    }
    public abstract <T> AstNode accept(TransformVisitor<T> v, T t);
    public abstract <RESULT,CLOSURE>
    RESULT accept (ValueVisitor<RESULT,CLOSURE> v, CLOSURE cl);

    @Override
    public String toString() { return ("("+name+" "+argsToString()+")").replaceAll(" \\)",")"); }
    protected String argsToString() { return ""; }
}
