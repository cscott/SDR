package net.cscott.sdr.calls.ast;

import static net.cscott.sdr.calls.transform.AstTokenTypes.SELECT;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import net.cscott.sdr.calls.TaggedFormation.Tag;
import net.cscott.sdr.calls.transform.TransformVisitor;
import net.cscott.sdr.calls.transform.ValueVisitor;

/** <code>ParCall</code> bundles a selector with a
 * <code>Comp</code>.  A <code>ParCall</code> applies
 * the child to dancers which match the given
 * <code>TaggedFormation.Tag</code>s.
 * @author C. Scott Ananian
 * @version $Id: ParCall.java,v 1.9 2006-10-18 01:57:20 cananian Exp $
 */
public class ParCall extends AstNode {
    public final Set<Tag> tags;
    public final Comp child;

    public ParCall(List<String> tags, Comp child) {
        this(parseTags(tags), child);
    }
    public ParCall(Set<Tag> tags, Comp child) {
        super(SELECT, "Select");
        this.tags = Collections.unmodifiableSet(EnumSet.copyOf(tags));
        this.child = child;
    }
    private static EnumSet<Tag> parseTags(List<String> tagNames) {
        EnumSet<Tag> sels = EnumSet.noneOf(Tag.class);
        for (String s: tagNames) {
            s=s.toUpperCase().replace(' ','_').replace('-','_');
            if (s.equals("NONE"))
                continue;
            else
                sels.add(Tag.valueOf(s));
        }
        return sels;
    }
    @Override
    public <T> ParCall accept(TransformVisitor<T> v, T t) {
        return v.visit(this, t);
    }
    @Override
    public <RESULT,CLOSURE>
    RESULT accept(ValueVisitor<RESULT,CLOSURE> v, CLOSURE cl) {
        return v.visit(this, cl);
    }
    /** Factory: creates new ParCall only if it would differ from this. */
    public ParCall build(Set<Tag> tags, Comp child) {
        if (tags.equals(this.tags) && this.child==child)
            return this;
        return new ParCall(tags, child);
    }
    @Override
    public String argsToString() {
        StringBuilder sb = new StringBuilder();
        sb.append(tags);
        sb.append(' ');
        sb.append(child);
        return sb.toString();
    }
}
