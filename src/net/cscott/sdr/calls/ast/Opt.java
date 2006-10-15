package net.cscott.sdr.calls.ast;

import static net.cscott.sdr.calls.ast.TokenTypes.OPT;

import java.util.Arrays;
import java.util.List;

import antlr.collections.AST;

import net.cscott.sdr.calls.Selector;
/**
 * <code>Opt</code> is a list of call options.  Each option has
 * an associated formation.  This first option whose formation is matchable
 * against the current formation is used to perform the call; the rest are
 * ignored.
 * @author C. Scott Ananian
 * @version $Id: Opt.java,v 1.3 2006-10-15 03:15:05 cananian Exp $
 */
public class Opt extends Comp {
    private final OptCall[] children;
    public Opt(OptCall... children) {
        super(OPT);
        this.children = children;
        for (OptCall oc : children)
            addChild(oc);
    }
    /** Factory: creates new Opt only if it would differ from this. */
    public Opt build(List<OptCall> children) {
        if (compare(children)) return this;
        return new Opt(children.toArray(new OptCall[children.size()]));
    }
    private boolean compare(List<OptCall> l) {
        if (getNumberOfChildren() != l.size()) return false;
        AST child = this.getFirstChild();
        for (OptCall t: l) {
                if (t != child) return false; // reference equality
                child = child.getNextSibling();
        }
        return true;
    }
}
