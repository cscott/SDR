package net.cscott.sdr.calls;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;

import net.cscott.jdoctest.Doctests;
import net.cscott.jutil.Factories;
import net.cscott.jutil.GenericMultiMap;
import net.cscott.sdr.calls.TaggedFormation.Tag;
import net.cscott.sdr.calls.ast.Prim;
import net.cscott.sdr.util.Fraction;
import net.cscott.sdr.util.ToStringBuilder;
import net.cscott.sdr.util.ToStringStyle;
import net.cscott.sdr.util.Tools.ListMultiMap;
import static net.cscott.sdr.util.Tools.mml;


/**
 * {@link DanceState} captures all the <i>dynamic</i> information about a
 * dance: the current formation and the queues of dancer actions and calls. It
 * includes a link to a {@link DanceProgram} which is the static information
 * about the dance.
 *
 * @author C. Scott Ananian
 */
@Doctests
public class DanceState {
    public final DanceProgram dance;
    private final NavigableMap<Fraction, Formation> formations;
    private final ListMultiMap<Dancer, TimedAction> actions; // XXX?
    private final Map<Dancer, NavigableMap<Fraction, DancerPath>> movements;
    // this is used to keep track of a 'designated dancer' stack
    private final Stack<Set<Dancer>> designatedStack;
    /** This is an interface into the environment of the dance engine.
     *  If a {@link java.util.concurrent.ConcurrentMap} is used, then the
     *  environment can be updated asynchronously. */
    private final Map<String,String> properties;
    /** Where this dance state sits in its parent's formation, or
     *  {@code null} at the top level; see {@link #atTopLevel()}. */
    private final Frame frame;
    /** One level of nesting: either the position of our meta dancer in
     *  the parent's meta formation, or the transform a concept applied to
     *  the parent's formation to get ours.  The other one is {@code null}. */
    private record Frame(Position placement,
                         Transform transform, Frame parent) { }
    /** A change a concept makes to the formation; see
     *  {@link #cloneAndClearWithTransform(Transform)}. */
    @FunctionalInterface
    public interface Transform {
        /** Apply this transform to {@code f}, or if {@code undo} is true,
         *  undo it. */
        Formation apply(Formation f, boolean undo);
    }

    public DanceState(DanceProgram dance, Formation f,
                      Map<String,String> properties) {
        this(dance, f, properties, null);
    }
    private DanceState(DanceProgram dance, Formation f,
                       Map<String,String> properties, Frame frame) {
        this.dance = dance;
        this.formations = new TreeMap<>();
        this.formations.put(Fraction.ZERO, f);
        this.actions = mml();
        // initialize movements
        this.movements = new LinkedHashMap<>
            (f.dancers().size());
        for (Dancer d: f.dancers())
            this.movements.put(d, new TreeMap<>());
        // xxx: initialize actions?
        this.designatedStack = new Stack<>();
        this.properties = properties;
        this.frame = frame;
    }
    // convenience constructor.
    public DanceState(DanceProgram dance, Formation f) {
        this(dance, f, Collections.<String,String>emptyMap());
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
        .append("dance", dance)
        .append("formations", formations)
        .append("actions", actions)
        .append("movements", movements)
        .append("designated", designated())
        .toString();
    }

    /** Return the last in the list of timed formations. */
    public Formation currentFormation() {
        return formations.lastEntry().getValue();
    }
    /** Return the time of the last formation. */
    public Fraction currentTime() {
        return formations.lastKey();
    }

    /**
     * Return a new independent dance state with the same static state and
     * current formation, but empty movements and actions. The timing of the
     * current formation is reset to zero. This is used for independently
     * evaluation subcalls, when we then want to apply a transformation to the
     * timing, positions, movements, formations, etc, before adding them to the
     * parent state.
     *
     * @return a new clean DanceState
     */
    public DanceState cloneAndClear() {
        return cloneAndClear(currentFormation());
    }

    /**
     * Similar to {@link #cloneAndClear()}, but allows you to specify the new
     * 'current formation' of the result. This is useful for concepts which
     * alter the shape of the current formation (say, creating a mirror image)
     * before applying the subcall.
     *
     * @param formation
     *            what {@code result.currentFormation()} should return.
     * @return a new clean DanceState.
     */
    public DanceState cloneAndClear(Formation formation) {
        return cloneAndClear(formation, this.frame);
    }

    /**
     * Similar to {@link #cloneAndClear(Formation)}, but the result is nested
     * one level down: {@code formation} is a part of our current formation,
     * placed (and rotated) where {@code placement} says.  This is used when
     * we match a meta formation, so that {@link #atTopLevel()} can find out
     * which way the dancers in the part face in the whole formation.
     *
     * @param formation
     *            what {@code result.currentFormation()} should return.
     * @param placement
     *            the position of the meta dancer standing for
     *            {@code formation} in the meta formation.
     * @return a new clean DanceState.
     */
    public DanceState cloneAndClear(Formation formation, Position placement) {
        return cloneAndClear(formation, new Frame(placement, null, this.frame));
    }

    /**
     * Similar to {@link #cloneAndClear()}, for concepts (like "mirror")
     * which transform the formation before applying the subcall: the
     * current formation of the result is {@code transform} applied to ours.
     * {@link #atTopLevel()} then applies {@code transform} to the top level
     * formation, too, so that facing directions inside the concept work as
     * they would if the concept had been applied to the whole set.
     * On the way up, {@link #atTopLevel()} undoes the transform, to find
     * where our dancers are in the parent's formation.  A transform which
     * is its own inverse (as mirroring is) can ignore {@code undo}.
     *
     * @param transform
     *            gives {@code result.currentFormation()} from our current
     *            formation.
     * @return a new clean DanceState.
     */
    public DanceState cloneAndClearWithTransform(Transform transform) {
        return cloneAndClear(transform.apply(currentFormation(), false),
                             new Frame(null, transform, this.frame));
    }

    private DanceState cloneAndClear(Formation formation, Frame frame) {
        // XXX: should revisit this?
        DanceState nds = new DanceState(dance, formation, properties, frame);
        for (Set<Dancer> designated : this.designatedStack)
            nds.pushDesignated(designated);
        return nds;
    }

    /**
     * Return a new clean dance state, at the top level, whose current
     * formation holds our dancers as they are in the top level formation:
     * facing the same way, and on the same side of each axis.  We don't
     * breathe the parts back together, so each dancer is placed at -1, 0,
     * or 1 on each axis.  That is enough to say which way they face, and
     * whether that is in or out, but not where they stand: use this for
     * facing direction selectors and patterns (in/out, north/south/east/west)
     * and not for formation or position ones.  The dancers keep their tags,
     * and the designated dancers stay designated.
     * @doc.test
     *  Split a squared set into couples; the side couples are turned a
     *  quarter, and couple #4's part straddles the x axis:
     *  js> f = Formation.SQUARED_SET; undefined
     *  js> ds = new DanceState(new DanceProgram(Program.PLUS), f); undefined
     *  js> fm = Matcher.valueOf("COUPLE").match(f); undefined
     *  js> md = Array.from(fm.meta.dancers()).find(md =>
     *    >   fm.matches.get(md).dancers().contains(StandardDancer.COUPLE_4_BOY)); undefined
     *  js> fm.meta.location(md)
     *  -2,0,e
     *  js> sub = fm.matches.get(md); sub.toStringDiagram()
     *  4B^  4G^
     *  js> nds = ds.cloneAndClear(sub, fm.meta.location(md)); undefined
     *  js> tf = nds.atTopLevel().currentFormation(); undefined
     *  js> [StandardDancer.COUPLE_4_BOY, StandardDancer.COUPLE_4_GIRL].map(
     *    >   d => d+": "+tf.location(d)+" (really "+f.location(d)+")").join("\n")
     *  COUPLE 4 BOY: -1,1,e (really -3,1,e)
     *  COUPLE 4 GIRL: -1,-1,e (really -3,-1,e)
     * @doc.test
     *  Under "mirror", we see the mirror image of the top level:
     *  js> f = Formation.SQUARED_SET; undefined
     *  js> ds = new DanceState(new DanceProgram(Program.PLUS), f); undefined
     *  js> mds = ds.cloneAndClearWithTransform((f, undo) => f.mirror(true)); undefined
     *  js> mf = mds.currentFormation(); mf.toStringDiagram()
     *       3Bv  3Gv
     *  
     *  2G>            4B<
     *  
     *  2B>            4G<
     *  
     *       1G^  1B^
     *  js> fm = Matcher.valueOf("COUPLE").match(mf); undefined
     *  js> md = Array.from(fm.meta.dancers()).find(md =>
     *    >   fm.matches.get(md).dancers().contains(StandardDancer.COUPLE_4_BOY)); undefined
     *  js> nds = mds.cloneAndClear(fm.matches.get(md), fm.meta.location(md)); undefined
     *  js> tf = nds.atTopLevel().currentFormation(); undefined
     *  js> [StandardDancer.COUPLE_4_BOY, StandardDancer.COUPLE_4_GIRL].map(
     *    >   d => d+": "+tf.location(d)+" (mirrored "+mf.location(d)+")").join("\n")
     *  COUPLE 4 BOY: 1,1,w (mirrored 3,1,w,[PASS_LEFT])
     *  COUPLE 4 GIRL: 1,-1,w (mirrored 3,-1,w,[PASS_LEFT])
     *  js> // "mirror mirror" undoes itself
     *  js> mmds = mds.cloneAndClearWithTransform((f, undo) => f.mirror(true)); undefined
     *  js> tf = mmds.atTopLevel().currentFormation(); undefined
     *  js> [StandardDancer.COUPLE_4_BOY, StandardDancer.COUPLE_4_GIRL].map(
     *    >   d => d+": "+tf.location(d)).join("\n")
     *  COUPLE 4 BOY: -1,1,e
     *  COUPLE 4 GIRL: -1,-1,e
     */
    public DanceState atTopLevel() {
        Formation f = currentFormation();
        Map<Dancer,Position> m = new LinkedHashMap<>();
        for (Dancer d : f.dancers()) {
            Position p = f.location(d);
            m.put(d, new Position(p.x.compareTo(Fraction.ZERO),
                                  p.y.compareTo(Fraction.ZERO), p.facing));
        }
        f = new Formation(m);
        // walk up to the top level, and then apply the transforms we found
        // on the way back down.
        Deque<Transform> transforms = new ArrayDeque<>();
        boolean lost = false;
        // XXX "rewind" may want a transform, or to stop partway up.
        for (Frame fr = this.frame; fr != null; fr = fr.parent()) {
            if (fr.transform() != null) {
                f = fr.transform().apply(f, true);
                transforms.push(fr.transform());
                continue;
            }
            Position place = fr.placement();
            ExactRotation rot = (ExactRotation) place.facing;
            // A part off an axis is all on one side of it.  A part on an
            // axis straddles it, so there we keep the side we had in the
            // part.  If the part is turned by an eighth we can't tell which
            // side we're on any more, so we put the dancers at the origin.
            lost |= rot.amount.multiply(Fraction.FOUR).getDenominator() != 1;
            int px = place.x.compareTo(Fraction.ZERO);
            int py = place.y.compareTo(Fraction.ZERO);
            m = new LinkedHashMap<>();
            for (Dancer d : f.dancers()) {
                Position r = f.location(d).rotateAroundOrigin(rot);
                int x = lost ? 0 : px != 0 ? px : r.x.compareTo(Fraction.ZERO);
                int y = lost ? 0 : py != 0 ? py : r.y.compareTo(Fraction.ZERO);
                m.put(d, new Position(x, y, r.facing));
            }
            f = new Formation(m);
        }
        for (Transform t : transforms)
            f = t.apply(f, false);
        // keep the tags
        TaggedFormation tf = TaggedFormation.coerce(currentFormation());
        for (Dancer d : tf.dancers())
            tf = tf.move(d, f.location(d));
        return cloneAndClear(tf, (Frame) null);
    }

    /** Add the set of designated dancers to the stack stored in the
     *  {@link DanceState}.
     * @param designated
     */
    public void pushDesignated(Set<Dancer> designated) {
        this.designatedStack.push(Collections.unmodifiableSet(designated));
    }
    /** Pop the top off the designated dancer stack. */
    public void popDesignated() {
        this.designatedStack.pop();
    }
    /** Look at the top of the designated dancer stack. */
    public Set<Dancer> designated() {
        if (this.designatedStack.isEmpty())
            return Collections.<Dancer>emptySet();
        return this.designatedStack.peek();
    }
    /** Add the 'DESIGNATED' tag to the given Formation. */
    public TaggedFormation tagDesignated(Formation f) {
        TaggedFormation tf = TaggedFormation.coerce(f);
        GenericMultiMap<Dancer,Tag> newTags =
            new GenericMultiMap<>
                (Factories.enumSetFactory(Tag.class));
        for (Dancer d : this.designated())
            if (f.dancers().contains(d))
                newTags.add(d, Tag.DESIGNATED);
        return tf.addTags(newTags);
    }
    /** Add the 'DESIGNATED' tag to the given FormationMatch. */
    public FormationMatch tagDesignated(FormationMatch fm) {
        if (this.designatedStack.isEmpty() ||
            this.designatedStack.peek().isEmpty())
            return fm; // fast out for common case
        Map<Dancer,TaggedFormation> nMatches =
            new LinkedHashMap<>();
        for (Dancer d : fm.matches.keySet())
            nMatches.put(d, this.tagDesignated(fm.matches.get(d)));
        return new FormationMatch(fm.meta, nMatches, fm.unmatched, fm.inserted);
    }
    /** Access the environment's property map.
     *  @see ExprList#_PROPERTY
     */
    public String property(String name, String defaultValue) {
        String v = properties.get(name);
        return (v==null) ? defaultValue : v;
    }

    /**
     * Move the given dancer along the specified dancer path. This will alter
     * the {@link #currentFormation()}, and may create additional intermediate
     * formations, if the {@link DancerPath#time} falls in between existing
     * formations.
     */
    public void add(Dancer d, DancerPath dp) {
        // add to list of dancer paths
	NavigableMap<Fraction,DancerPath> dmove = this.movements.get(d);
	Fraction last = dmove.isEmpty() ? Fraction.ZERO : dmove.lastKey();
        Fraction next = last.add(dp.time);
        dmove.put(next, dp);
        // get formation with time == next, or "just before"
        if (!this.formations.containsKey(next)) {
            // clone the formation "just before"
            this.formations.put(next,
                                this.formations.floorEntry(next).getValue());
        }
        // okay, now iterate from 'next' forward, adjusting this dancer's
        // location
        for (Map.Entry<Fraction, Formation> me:
             this.formations.tailMap(next).entrySet()) {
            me.setValue(me.getValue().move(d, dp.to));
        }
        // done!
    }

    /**
     * Add "do nothing" actions as necessary so that every dancer's next
     * action will occur at the same time.
     */
    public void syncDancers() {
        this.syncDancers(this.currentTime());
    }
    /**
     * Add "do nothing" actions as necessary so that every dancer's next
     * action will occur at the given time.
     * @doc.test
     *  js> ds = new DanceState(new DanceProgram(Program.PLUS),
     *    >                     Formation.FOUR_SQUARE); undefined;
     *  js> function addPrim(d, str, f) {
     *    >   if (!f) f = ds.formationAt(net.cscott.sdr.util.Fraction.ZERO);
     *    >   const prim = net.cscott.sdr.calls.ast.AstNode.valueOf(str);
     *    >   ds.add(d, EvalPrim.apply(d, f, prim));
     *    > }
     *  js> addPrim(StandardDancer.COUPLE_1_GIRL,'(Prim 0, 0, left, 1)');
     *  js> addPrim(StandardDancer.COUPLE_3_BOY, '(Prim 0, 0, right,1)');
     *  js> f = ds.currentFormation(); undefined
     *  js> addPrim(StandardDancer.COUPLE_1_BOY, '(Prim 0, 1, none, 2)');
     *  js> addPrim(StandardDancer.COUPLE_1_GIRL,'(Prim 0, 0, none, 2)', f);
     *  js> addPrim(StandardDancer.COUPLE_3_BOY, '(Prim 0, 0, none, 3, preserve-roll)', f);
     *  js> ds.syncDancers(net.cscott.sdr.util.Fraction.valueOf(5));
     *  js> // preserve sweep direction of first dancer
     *  js> ds.movements(StandardDancer.COUPLE_1_BOY)
     *  [DancerPath[from=-1,-1,n,to=-1,0,n,[SWEEP_LEFT],time=2,pointOfRotation=<null>], DancerPath[from=-1,0,n,[SWEEP_LEFT],to=-1,0,n,[SWEEP_LEFT],time=3,pointOfRotation=<null>]]
     *  js> // extend prim for 2nd dancer (without preserving roll)
     *  js> ds.movements(StandardDancer.COUPLE_1_GIRL)
     *  [DancerPath[from=1,-1,n,to=1,-1,w,[ROLL_LEFT],time=1,pointOfRotation=SINGLE_DANCER], DancerPath[from=1,-1,w,[ROLL_LEFT],to=1,-1,w,time=4,pointOfRotation=<null>]]
     *  js> // preserve roll for 3rd dancer
     *  js> ds.movements(StandardDancer.COUPLE_3_BOY)
     *  [DancerPath[from=1,1,s,to=1,1,w,[ROLL_RIGHT],time=1,pointOfRotation=SINGLE_DANCER], DancerPath[from=1,1,w,[ROLL_RIGHT],to=1,1,w,[ROLL_RIGHT],time=4,pointOfRotation=<null>]]
     */
    public void syncDancers(Fraction time) {
        for (Map.Entry<Dancer,NavigableMap<Fraction,DancerPath>> me :
                this.movements.entrySet()) {
            Dancer d = me.getKey();
            NavigableMap<Fraction,DancerPath> dmove = me.getValue();
            Fraction lastTime = dmove.isEmpty()? Fraction.ZERO: dmove.lastKey();
            assert lastTime.compareTo(time) <= 0;
            if (lastTime.equals(time))
                continue;
            // try to scale last path, if it was a stand still
            DancerPath dp = dmove.get(lastTime), nothingPath;
            if (dp == null || !dp.isStandStill()) {
                // create new path
                Prim nothingPrim =
                    Prim.STAND_STILL.scaleTime(time.subtract(lastTime));
                nothingPath =
                    EvalPrim.apply(d, formations.get(lastTime), nothingPrim);
            } else {
                // scale old path
                Fraction startTime = lastTime.subtract(dp.time);
                nothingPath =
                    dp.scaleTime(time.subtract(startTime).divide(dp.time));
                dmove.remove(lastTime);
            }
            dmove.put(time, nothingPath);
        }
    }
    /**
     * Return the time corresponding to the last movement.
     * (Trailing "stand still" actions are ignored.)
     * @doc.test
     *  js> ds = new DanceState(new DanceProgram(Program.PLUS),
     *    >                     Formation.SQUARED_SET); undefined;
     *  js> function addPrim(d, str, f) {
     *    >   if (!f) f = ds.formationAt(net.cscott.sdr.util.Fraction.ZERO);
     *    >   const prim = net.cscott.sdr.calls.ast.AstNode.valueOf(str);
     *    >   ds.add(d, EvalPrim.apply(d, f, prim));
     *    > }
     *  js> addPrim(StandardDancer.COUPLE_1_BOY, '(Prim 0, 1, none, 2)');
     *  js> addPrim(StandardDancer.COUPLE_2_BOY, '(Prim 0, 0, none, 3)');
     *  js> addPrim(StandardDancer.COUPLE_3_BOY, '(Prim 0, 0, none, 4, preserve-roll)');
     *  js> ds.currentTime()
     *  4/1
     *  js> ds.lastMovement()
     *  2/1
     */
    public Fraction lastMovement() {
        for (Fraction time : formations.descendingKeySet()) {
            for (NavigableMap<Fraction,DancerPath> dmove: movements.values()) {
                Map.Entry<Fraction,DancerPath> e = dmove.ceilingEntry(time);
                if (e!=null && !e.getValue().isStandStill())
                    // found a dancer moving at this time!
                    return time;
            }
        }
        return Fraction.ZERO;
    }

    /**
     * Split any "do nothing" actions which cross the given moment in
     * time.
     * @doc.test
     *  js> ds = new DanceState(new DanceProgram(Program.PLUS),
     *    >                     Formation.SQUARED_SET); undefined;
     *  js> function addPrim(d, str, f) {
     *    >   if (!f) f = ds.formationAt(net.cscott.sdr.util.Fraction.ZERO);
     *    >   const prim = net.cscott.sdr.calls.ast.AstNode.valueOf(str);
     *    >   ds.add(d, EvalPrim.apply(d, f, prim));
     *    > }
     *  js> addPrim(StandardDancer.COUPLE_1_GIRL,'(Prim 0, 0, left, 1)');
     *  js> addPrim(StandardDancer.COUPLE_3_BOY, '(Prim 0, 0, right,1)');
     *  js> f = ds.currentFormation(); undefined
     *  js> addPrim(StandardDancer.COUPLE_1_BOY, '(Prim 0, 1, none, 2)');
     *  js> addPrim(StandardDancer.COUPLE_1_GIRL,'(Prim 0, 0, none, 2)', f);
     *  js> addPrim(StandardDancer.COUPLE_3_BOY, '(Prim 0, 0, none, 3, preserve-roll)', f);
     *  js> // ok, split time at time 2
     *  js> ds.splitTime(net.cscott.sdr.util.Fraction.TWO)
     *  js> // dancer 1 should be unaffected
     *  js> ds.movements(StandardDancer.COUPLE_1_BOY)
     *  [DancerPath[from=-1,-3,n,to=-1,-2,n,[SWEEP_LEFT],time=2,pointOfRotation=<null>]]
     *  js> // dancer 2 should still not have roll afterwards
     *  js> ds.movements(StandardDancer.COUPLE_1_GIRL)
     *  [DancerPath[from=1,-3,n,to=1,-3,w,[ROLL_LEFT],time=1,pointOfRotation=SINGLE_DANCER], DancerPath[from=1,-3,w,[ROLL_LEFT],to=1,-3,w,[ROLL_LEFT],time=1,pointOfRotation=<null>], DancerPath[from=1,-3,w,[ROLL_LEFT],to=1,-3,w,time=1,pointOfRotation=<null>]]
     *  js> // dancer 3 should preserve roll
     *  js> ds.movements(StandardDancer.COUPLE_3_BOY)
     *  [DancerPath[from=1,3,s,to=1,3,w,[ROLL_RIGHT],time=1,pointOfRotation=SINGLE_DANCER], DancerPath[from=1,3,w,[ROLL_RIGHT],to=1,3,w,[ROLL_RIGHT],time=1,pointOfRotation=<null>], DancerPath[from=1,3,w,[ROLL_RIGHT],to=1,3,w,[ROLL_RIGHT],time=2,pointOfRotation=<null>]]
     */
    public void splitTime(Fraction time) {
        if (formations.get(time) == null) {
            formations.put(time, formations.floorEntry(time).getValue());
        }
        eachDancer:
        for (Dancer d : this.movements.keySet()) {
            NavigableMap<Fraction,DancerPath> dmove = this.movements.get(d);
            Map.Entry<Fraction,DancerPath> e = dmove.ceilingEntry(time);
            if (e==null) continue;
            DancerPath dp = e.getValue();
            if (!dp.isStandStill()) continue;
            Fraction endTime = e.getKey();
            Fraction startTime = endTime.subtract(dp.time);
            if (time.equals(endTime) || time.equals(startTime)) continue;
            assert startTime.compareTo(time) < 0;
            assert time.compareTo(endTime) < 0;
            // split this in two
            Prim nothingPrim =
                Prim.STAND_STILL.scaleTime(time.subtract(startTime));
            DancerPath first = EvalPrim.apply(d, formations.get(startTime),
                                              nothingPrim);
            DancerPath second = dp.scaleTime
                (endTime.subtract(time).divide(dp.time));
            assert first.time.add(second.time).equals(dp.time);
            dmove.put(time, first);
            dmove.put(endTime, second);
        }
    }
    /**
     * Remove any trailing "do nothing" actions so that the next action
     * flows directly from the previous one.  This is used in "roll" --
     * I don't know if it's useful for any other calls.
     * @doc.test
     *  js> importPackage(net.cscott.sdr.util);
     *  js> ds = new DanceState(new DanceProgram(Program.PLUS),
     *    >                     Formation.SQUARED_SET); undefined;
     *  js> function addPrim(d, str, f) {
     *    >   if (!f) f = ds.formationAt(net.cscott.sdr.util.Fraction.ZERO);
     *    >   const prim = net.cscott.sdr.calls.ast.AstNode.valueOf(str);
     *    >   ds.add(d, EvalPrim.apply(d, f, prim));
     *    > }
     *  js> addPrim(StandardDancer.COUPLE_1_BOY, '(Prim 0, 1, none, 2)');
     *  js> addPrim(StandardDancer.COUPLE_2_BOY, '(Prim 0, 0, none, 3)');
     *  js> addPrim(StandardDancer.COUPLE_3_BOY, '(Prim 0, 0, none, 4, preserve-roll)');
     *  js> Array.from(ds.formations(), tf => tf.time.toProperString())
     *  0,2,3,4
     *  js> // unsyncDancers from here should remove the formations @ 3 and 4
     *  js> // (note that good practice here is to split @ 2 first)
     *  js> ds.splitTime(Fraction.TWO) ; ds.unsyncDancers()
     *  js> Array.from(ds.formations(), tf => tf.time.toProperString())
     *  0,2
     */
    public void unsyncDancers() {
        eachDancer:
        for (NavigableMap<Fraction,DancerPath> dmove : this.movements.values()){
            Iterator<Fraction> it= dmove.navigableKeySet().descendingIterator();
            while (it.hasNext()) {
                if (dmove.get(it.next()).isStandStill())
                    it.remove();
                else
                    continue eachDancer;
            }
        }
        // Remove entire TimedFormation if all dancers were standing still
        formations.tailMap(lastMovement(), false).clear();
    }

    /**
     * Return all the {@link DancerPath}s, in order, performed by the given
     * {@link Dancer}.
     */
    public List<DancerPath> movements(Dancer d) {
        return Collections.unmodifiableList
            (new ArrayList<>(this.movements.get(d).values()));
    }

    /** Return all the {@link Dancer}s in this {@link DanceState}. */
    public Set<Dancer> dancers() {
        return this.movements.keySet();
    }

    /**
     * Return a sorted list of {@link TimedFormation}s describing the net
     * effect of the {@link DancerPath}s in this {@link DanceState}. The first
     * element in the list will match the starting formation provided when this
     * {@link DanceState} was constructed, will have an absolute time of
     * {@link Fraction#ZERO}, and will have all dancers selected. The final
     * element in the list will represent the final state. Formations should not
     * be considered animation targets: not all dancers will have moved in all
     * formations. For example, if dancer #1 has a movement which takes 2 beats,
     * and dancer #2 has two movements which take 1 each, the result list will
     * include the starting formation, with time 0, an intermediate formation
     * with time 1 where dancer #1 is in their starting position and dancer #2
     * has taken their first movement, and a final position with time 2 where
     * both dancer #1 and dancer #2 have moved.  Consult the list of
     * {@link DancerPath}s for accurate animation information.
     */
    public List<TimedFormation> formations() {
        List<TimedFormation> result =
            new ArrayList<>(this.formations.size());
        for (Map.Entry<Fraction,Formation> me: this.formations.entrySet()) {
            result.add(new TimedFormation(me.getValue(), me.getKey(), true));
        }
        return Collections.unmodifiableList(result);
    }
    /** Return the latest formation at or preceding the given time. */
    public Formation formationAt(Fraction time) {
        return this.formations.floorEntry(time).getValue();
    }
}
