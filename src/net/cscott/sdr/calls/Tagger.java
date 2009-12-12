package net.cscott.sdr.calls;

import static net.cscott.sdr.util.Tools.m;
import static net.cscott.sdr.util.Tools.p;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import net.cscott.jutil.Default;
import net.cscott.jutil.Factories;
import net.cscott.jutil.GenericMultiMap;
import net.cscott.jutil.MultiMap;
import net.cscott.jutil.PersistentMultiMapFactory;
import net.cscott.jutil.PersistentSet;
import net.cscott.sdr.calls.TaggedFormation.Tag;
import net.cscott.sdr.util.Fraction;
import net.cscott.sdr.util.LL;
import net.cscott.sdr.util.Point;

/** Helper class to add common {@link TaggedFormation.Tag}s automatically to
 *  {@link TaggedFormation}s. */
public abstract class Tagger {
    private Tagger() { /* no instances */ }
    
    // XXX if we find 4-dancer formations, we should look inside those for
    //     2-person formations (ie, not allow 2-person matches which span
    //     multiple 4-person formations, like the very centers in
    //     point-to-point diamonds.
    public static void addAutomatic(Formation f, MultiMap<Dancer, Tag> tags) {
        switch(f.dancers().size()) {
        case 8:
            // XXX: add center 2, outer 6
        case 4:
            tag4(f, tags);
        case 2:
            tag2(f, tags);
        default: // skip
            break;
        }
    }

    /** Find all instance of the tagged {@code template} formation in the given
     * given formation {@code f}, and add the tags from the template to the
     * set of tags on {@code f} given in {@code tags}.
     */
    public static void addFrom(TaggedFormation template, Formation f, MultiMap<Dancer, Tag> tags) {
        assert false : "unimplemented";
    }

    /** Return a new matcher which adds 1x2 tags
     *  (BEAU/BELLE/LEADER/TRAILER) to the generic formation matched by the
     *  given {@link Matcher}.
     */
    public static Matcher autotag2(Matcher s) {
        return new AutoMatcher(s) {
            @Override
            public void tag(TaggedFormation tf, MultiMap<Dancer, Tag> nTags) {
                tag2(tf, nTags);
            }
        };
    }

    /** Return a new matcher which adds 1x2 and 1x4 tags
     *  (BEAU/BELLE/LEADER/TRAILER/NUMBER 1/2/3/4/CENTER/END) to the generic
     *  formation matched by the given {@link Matcher}.
     */
    public static Matcher autotag4(Matcher s) {
        return new AutoMatcher(s) {
            @Override
            public void tag(TaggedFormation tf, MultiMap<Dancer, Tag> nTags) {
                tag2(tf, nTags);
                tag4(tf, nTags);
            }
        };
    }

    private static abstract class AutoMatcher extends Matcher {
        private final Matcher s;
        AutoMatcher(Matcher s) { this.s = s; }
        @Override
        public String getName() { return s.toString(); }
        @Override
        public FormationMatch match(Formation f) throws NoMatchException {
            // first match with the child matcher.
            FormationMatch fm = s.match(f);
            // now go through the match and add autotags to the pieces.
            Map<Dancer,TaggedFormation> nmatches =
                new LinkedHashMap<Dancer,TaggedFormation>();
            for (Dancer metaDancer : fm.meta.dancers()) {
                TaggedFormation tf = fm.matches.get(metaDancer);
                MultiMap<Dancer,Tag> nTags =
                    new GenericMultiMap<Dancer,Tag>
                (Factories.<Dancer,Collection<Tag>>linkedHashMapFactory(),
                        Factories.enumSetFactory(Tag.class));
                this.tag(tf, nTags);
                nmatches.put(metaDancer, tf.addTags(nTags));
            }
            return new FormationMatch(fm.meta, nmatches, fm.unmatched);
        }
        public abstract void tag(TaggedFormation tf, MultiMap<Dancer,Tag> nTags);
    }

    /** Discover 1x2 tags (BEAU/BELLE/LEADER/TRAILER) in the given
     *  formation. */
    public static void tag2(Formation f, MultiMap<Dancer,Tag> tags) {
        tagN(2, f, tags);
    }
    /** Discover 1x4 tags (NUMBER_1/2/3/4, CENTER/END) in the given
     *  formation. */
    public static void tag4(Formation f, MultiMap<Dancer,Tag> tags) {
        tagN(4, f, tags);
    }

    private static void tagN(int n, Formation f, MultiMap<Dancer,Tag> tags) {
        assert n==2 || n==4;
        Formation template = (n==2) ? template2 : (n==4) ? template4 : null;
        int toff = (n==2) ? 0 : (n==4) ? 2 : -1;
        // match against our beau/belle pattern.
        LL<Dancer> allDancers = LL.create(f.sortedDancers());
        Map<Point,Dancer> where = new HashMap<Point,Dancer>();
        for (Dancer d: allDancers)
            where.put(pos2pt(f.location(d)), d);
        Match noMatch = new Match(allDancers);
        for (int i=0; i<4; i+=2)  { //change to +=1 to support 45-off formations
            try {
                // rotate the template formation and attempt a match.
                Fraction extraRot = Fraction.valueOf(i, 8);
                Formation t = template.rotate(new ExactRotation(extraRot));
                Match m = match(f, where, n, toff, pos2pt(t.location(dancer[toff+1])),
                                extraRot.negate(), allDancers, noMatch);
                // add tags from this match
                tags.addAll(m.tags);
            } catch (NoMatchException nme) {
                // hm, don't tag: the tag assignment is ambiguous.
                continue;
            }
        }
    }

    /** Compare {@link Dancer}s by their order in the supplied list. */
    private static class DancerComparator implements Comparator<Dancer> {
        Map<Dancer,Integer> order = new HashMap<Dancer,Integer>();
        DancerComparator(LL<Dancer> dancerList) {
            int i=0;
            for (Dancer d: dancerList)
                order.put(d, i++);
        }
        public int compare(Dancer d1, Dancer d2) {
            return order.get(d1).compareTo(order.get(d2));
        }
    }
    /** Match state class: holds the current dancers and tags assigned. */
    private static class Match {
        PersistentMultiMapFactory<Dancer,Tag> mmf;
        MultiMap<Dancer,Tag> tags;
        PersistentSet<Dancer> assigned;
        /** Create a new empty match. */
        Match(LL<Dancer> allDancers) {
            this(new DancerComparator(allDancers));
        }
        private Match(Comparator<Dancer> c) {
            this(new PersistentMultiMapFactory<Dancer,Tag>
                     (c, Default.<Tag>comparator()),
                 new PersistentSet<Dancer>(c));
        }
        private Match(PersistentMultiMapFactory<Dancer,Tag> mmf,
                      PersistentSet<Dancer> assigned) {
            this(mmf, mmf.makeMultiMap(), assigned);
        }
        private Match(PersistentMultiMapFactory<Dancer,Tag> mmf,
                      MultiMap<Dancer,Tag> tags,
                      PersistentSet<Dancer> assigned) {
            this.mmf = mmf;
            this.tags = tags;
            this.assigned = assigned;
        }
        /** Create a new match which maps the given dancers to tags. */
        Match add(Dancer d0, Tag t0) {
            assert !assigned.contains(d0);
            MultiMap<Dancer,Tag> nTags = mmf.makeMultiMap(tags); // fast
            if (t0!=null) nTags.add(d0, t0);
            PersistentSet<Dancer> nAssigned = assigned.add(d0);
            return new Match(mmf, nTags, nAssigned);
        }
    }
    /** Attempt to extend the given partialMatch by examining the next dancer
     *  in the 'remaining' list. */
    private static Match match(Formation f,
                               Map<Point, Dancer> where,
                               int templateSize, int templateOffset,
                               Point offset, Fraction extraRot,
                               LL<Dancer> remaining,
                               Match partialMatch) {
        // anything left to do?
        if (remaining.isEmpty())
            return partialMatch;

        // skip this dancer if it's already assigned
        Dancer d0 = remaining.head;
        Match nBest1 = null;
        if (!partialMatch.assigned.contains(d0)) {
            // this dancer can be assigned.  try to make it into a match.
            boolean possible = true;
            Point p0 = pos2pt(f.location(d0));
            Point p1 = p0;
            for (int i=1; i<templateSize; i++) {
                p1 = p1.add(offset);
                if ((!where.containsKey(p1)) ||
                    partialMatch.assigned.contains(where.get(p1))) {
                    possible = false;
                    break;
                }
            }
            if (possible) {
                // hey, there's a complete match here.  assign d0, d1, ...
                Match m = partialMatch;
                p1 = p0;
                for (int i=0; i<templateSize; i++) {
                    Dancer d1 = where.get(p1);
                    Rotation r1 = f.location(d1).facing.add(extraRot);
                    Tag t1 = dancerTags.get(dancer[i+templateOffset]).get(r1);
                    m = m.add(d1, t1);
                    p1 = p1.add(offset);
                }
                // recurse after adding these dancers/tags to the partialMatch
                nBest1 = match(f, where, templateSize, templateOffset,
                               offset, extraRot, remaining.tail, m);
                // quit early if there's no way to beat the nBest1 match
                // without assigning dancer d0.
                if (remaining.tail.size() < nBest1.assigned.size())
                    return nBest1;
            }
        }
        // okay, try to finish the match, skipping dancer d0
        Match nBest0 = match(f, where, templateSize, templateOffset,
                             offset, extraRot, remaining.tail, partialMatch);

        // return the better match among nBest0 and nBest1
        if (nBest1 == null) return nBest0;
        if (nBest0.assigned.size() == nBest1.assigned.size())
            throw new NoMatchException("general "+templateSize, "ambiguous");
        return (nBest0.assigned.size() > nBest1.assigned.size()) ?
                nBest0 : nBest1;
    }

    /** Helper method: strip the facing direction from a {@link Position}. */
    private static Point pos2pt(Position p) {
        return new Point(p.x, p.y);
    }
    /** Helper method: concisely create a {@link Position}. */
    private static Position pos(int x, int y, String dir) {
        return new Position(x,y,Rotation.fromAbsoluteString(dir));
    }
    /** Our private stash of phantom dancers. */
    private static final Dancer[] dancer = new Dancer[] {
        new PhantomDancer(), new PhantomDancer(),
        new PhantomDancer(), new PhantomDancer(),
        new PhantomDancer(), new PhantomDancer(),
    };
    /** Our two-dancer template formation. */
    private static final Formation template2 =
        new Formation(m(p(dancer[0], pos(0, 0, "o")),
                        p(dancer[1], pos(2, 0, "o"))));
    /** Our four-dancer template formation. */
    private static final Formation template4 =
        new Formation(m(p(dancer[2], pos(0, 0, "o")),
                        p(dancer[3], pos(2, 0, "o")),
                        p(dancer[4], pos(4, 0, "o")),
                        p(dancer[5], pos(6, 0, "o"))));
    /** Map rotations of dancers in the template to appropriate tags. */
    @SuppressWarnings("unchecked")
    private static final Map<Dancer,Map<Rotation,Tag>> dancerTags =
        m(// two-dancer tags
          p(dancer[0], m(p((Rotation)ExactRotation.NORTH, Tag.BEAU),
                         p((Rotation)ExactRotation.SOUTH, Tag.BELLE),
                         p((Rotation)ExactRotation.EAST, Tag.TRAILER),
                         p((Rotation)ExactRotation.WEST, Tag.LEADER))),
          p(dancer[1], m(p((Rotation)ExactRotation.NORTH, Tag.BELLE),
                         p((Rotation)ExactRotation.SOUTH, Tag.BEAU),
                         p((Rotation)ExactRotation.EAST, Tag.LEADER),
                         p((Rotation)ExactRotation.WEST, Tag.TRAILER))),
          // four-dancer tags
          p(dancer[2], m(p((Rotation)ExactRotation.NORTH, Tag.END),
                         p((Rotation)ExactRotation.SOUTH, Tag.END),
                         p((Rotation)ExactRotation.EAST, Tag.NUMBER_4),
                         p((Rotation)ExactRotation.WEST, Tag.NUMBER_1))),
          p(dancer[3], m(p((Rotation)ExactRotation.NORTH, Tag.CENTER),
                         p((Rotation)ExactRotation.SOUTH, Tag.CENTER),
                         p((Rotation)ExactRotation.EAST, Tag.NUMBER_3),
                         p((Rotation)ExactRotation.WEST, Tag.NUMBER_2))),
          p(dancer[4], m(p((Rotation)ExactRotation.NORTH, Tag.CENTER),
                         p((Rotation)ExactRotation.SOUTH, Tag.CENTER),
                         p((Rotation)ExactRotation.EAST, Tag.NUMBER_2),
                         p((Rotation)ExactRotation.WEST, Tag.NUMBER_3))),
          p(dancer[5], m(p((Rotation)ExactRotation.NORTH, Tag.END),
                         p((Rotation)ExactRotation.SOUTH, Tag.END),
                         p((Rotation)ExactRotation.EAST, Tag.NUMBER_1),
                         p((Rotation)ExactRotation.WEST, Tag.NUMBER_4))));
}
