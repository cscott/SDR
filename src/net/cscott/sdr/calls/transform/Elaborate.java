package net.cscott.sdr.calls.transform;

import java.util.*;

import net.cscott.sdr.calls.*;
import net.cscott.sdr.calls.FormationMatch.TaggedFormationAndWarp;
import net.cscott.sdr.calls.TaggedFormation.Tag;
import net.cscott.sdr.calls.ast.*;
/**
 * The {@link Elaborate} class does formation evaluation to eliminate
 * {@link Opt} elements in the call tree and identify the dancers in
 * {@link Par}s.  It inserts {@link Warp} elements as needed.
 * The result is a 'simplified tree'.
 * @author C. Scott Ananian
 * @version $Id: Elaborate.java,v 1.1 2006-10-17 20:04:48 cananian Exp $
 */
public class Elaborate extends TransformVisitor<Formation> {
    /** Static dance state (like program, etc). */
    private final DanceState ds;
    /** Should we evaluate all the way down to a simplified tree, or just
     * do 'one step' of elaboration? */
    private final boolean doFully;
    private Elaborate(DanceState ds, boolean doFully) {
        this.ds = ds;
        this.doFully = doFully;
    }
    public static Comp elaborate(DanceState ds, Formation f, Comp c, boolean doFully) {
        return c.accept(new Elaborate(ds, doFully), f);
    }
    
    public SeqCall visit(Apply a, Formation f) {
        // expand this call.
        Comp c = a.expand();
        if (doFully) // go off an elaborate the expansion.
            c = c.accept(this, f);
        return new Part(true, c);
    }
    public Comp visit(If iff, Formation f) {
        // evaluate the predicate
        Predicate p = iff.condition.getPredicate();
        if (!p.evaluate(ds, f, iff.condition)) 
            throw new BadCallException("condition failed");
        Comp c = iff.child;
        if (doFully) c = c.accept(this, f);
        return c;
    }
    public Comp visit(Opt opt, Formation f) {
        // first, reduce to one formation per OptCall
        List<OptCall> l = new ArrayList<OptCall>(opt.children.size());
        for (OptCall oc : opt.children)
            for (Selector s : oc.selectors)
                l.add(oc.build(Collections.singletonList(s), oc.child));
        // now recurse into options.
        return super.visit(opt.build(l), f);
    }
    // (FROM [MINIWAVE] x) ->
    //   (FROM [MINIWAVE] (SELECT (d1,d2,d3) x (d4,d5,d6) y))
    public OptCall visit(OptCall oc, Formation f) {
        // this is the tricky one: perform the formation match
        assert oc.selectors.size()==1;
        Selector s = oc.selectors.get(0);
        FormationMatch fm = s.match(f);
        assert !fm.matches.isEmpty();
        List<ParCall> l = new ArrayList<ParCall>(fm.matches.size());
        for (TaggedFormationAndWarp tfw : fm.matches) {
            Comp c = new Warped(tfw.w, oc.child);
            if (doFully)
                c = c.accept(this, tfw.tf);
            l.add(new ParCall(selectedTags(tfw.tf), c));
        }
        return oc.build(oc.selectors, new Par(l));
    }
    /** Ensure that every selected dancers matches one of the clauses. */
    public Comp visit(Par p, Formation _f) {
        TaggedFormation tf = (TaggedFormation) _f;
        Set<Dancer> sel = new HashSet<Dancer>(tf.selectedDancers());
        List<ParCall> l = new ArrayList<ParCall>(p.children.size());
        for (ParCall pc : p.children) {
            try {
                l.add(pc.accept(this, tf));
            } catch (BadCallException ex) {
                // ignore the exception; just don't add it to the list
            }
            // remove matched dancers from the formation.
            sel.removeAll(selected(pc,tf));
            tf = tf.select(sel);
        }
        if (l.isEmpty()) // all options have been exhausted
            throw new BadCallException("No dancers can be selected");
        if (!sel.isEmpty()) // some dancers not matched
            throw new BadCallException("Unmatched dancers: "+sel);
        return p.build(l);
    }
    /** Select the given dancers in the formation, and evaluate the child. */
    public ParCall visit(ParCall pc, Formation f) {
        TaggedFormation tf = (TaggedFormation) f;
        // identify the dancers selected by this.
        Set<Dancer> nSel = selected(pc, tf);
        if (nSel.isEmpty()) throw new BadCallException("no dancers selected");
        // replace the tags with DANCER_x tags, corresponding to the
        // dancers in the given formation with that tag.
        Set<Tag> nTags;
        if (doFully) { // only if doFully
            nTags = EnumSet.noneOf(Tag.class);
            for (Dancer d : nSel)
                nTags.add(d.primitiveTag());
        } else nTags = pc.tags; // otherwise use old tags
        // create a new TaggedFormation with just these dancers selected.
        TaggedFormation nf=tf.select(nSel);
        // and evaluate the child in this formation.
        Comp c = pc.child.accept(this, nf);
        // build a primitive sel if doFully; otherwise just rebuild the select
        return pc.build(nTags, c);
    }
    /** Return the set of dancers named by this ParCall. */
    private Set<Dancer> selected(ParCall pc, TaggedFormation tf) {
        Set<Dancer> result = new HashSet<Dancer>(8);
        for (Tag tag : pc.tags)
            for (Dancer d : tf.tagged(tag))
                if (tf.isSelected(d))
                    result.add(d);
        return result;
    }
    private Set<Tag> selectedTags(Formation f) {
        Set<Tag> tags = EnumSet.noneOf(Tag.class);
        for (Dancer d : f.selectedDancers())
            tags.add(d.primitiveTag());
        return tags;
    }
}
