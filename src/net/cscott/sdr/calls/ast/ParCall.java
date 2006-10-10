package net.cscott.sdr.calls.ast;

import static net.cscott.sdr.calls.CallFileParserTokenTypes.SELECT;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import net.cscott.sdr.calls.TaggedFormation.Tag;
import antlr.CommonAST;

/** <code>ParCall</code> bundles a selector with a
 * <code>Comp</code>.  A <code>ParCall</code> applies
 * the child to dancers which match the given
 * <code>TaggedFormation.Tag</code>s.
 * @author C. Scott Ananian
 * @version $Id: ParCall.java,v 1.2 2006-10-10 15:38:39 cananian Exp $
 */
public class ParCall extends CommonAST {
    public final Set<Tag> tags;

    public ParCall(List<String> tags, Comp child) {
        this(parseTags(tags), child);
    }
    public ParCall(EnumSet<Tag> tags, Comp child) {
        super();
        initialize(SELECT, "Select");
        addChild(child);
        this.tags = Collections.unmodifiableSet(EnumSet.copyOf(tags));
    }
    private static EnumSet<Tag> parseTags(List<String> tagNames) {
        EnumSet<Tag> sels = EnumSet.noneOf(Tag.class);
        for (String s: tagNames) {
            if (s.equals("NONE"))
                continue;
            else if (s.equals("ALL"))
                sels = EnumSet.allOf(Tag.class);
            else
                sels.add(Tag.valueOf(s));
        }
        return sels;
    }
}
