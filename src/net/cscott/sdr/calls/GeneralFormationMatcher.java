package net.cscott.sdr.calls;

import java.util.*;

import net.cscott.sdr.calls.Breather.FormationPiece;
import net.cscott.sdr.calls.Position.Flag;
import net.cscott.sdr.calls.TaggedFormation.Tag;
import net.cscott.sdr.calls.TaggedFormation.TaggedDancerInfo;
import net.cscott.sdr.util.Fraction;
import static net.cscott.sdr.util.Tools.m;
import static net.cscott.sdr.util.Tools.p;
import net.cscott.jutil.*;

/**
 * {@link GeneralFormationMatcher} produces a {@link FormationMatch}
 * given an input {@link Formation} and a goal {@link TaggedFormation}.
 * This can be used to make {@link Selector}s out of {@link TaggedFormation}s,
 * via the {@link #makeSelector} method.
 *
 * @author C. Scott Ananian
 */
public abstract class GeneralFormationMatcher {
    // currying, oh, my
    public static Selector makeSelector(final TaggedFormation goal) {
        return new Selector() {
            public FormationMatch match(Formation f) throws NoMatchException {
                return doMatch(f, goal, false, false);
            }
            public String toString() {
		// special case for "standard" formations.
                if (goal instanceof NamedTaggedFormation)
                    return ((NamedTaggedFormation)goal).getName();
                return goal.toString();
	    }
        };
    }

    /**
     * Attempt to match the input formation against the goal formation; you can
     * have multiple rotated copies of the goal formation in the input.
     * Allow dancers who are not part of copies of the goal formation if
     * allowUnmatchedDancers is true; allow copies of the goal formation
     * with phantoms in them if usePhantoms is true.  Returns the best
     * such match (ie, most copies of the goal formation).
     * @param input An untagged formation to match against.
     * @param goal A tagged goal formation
     * @param allowUnmatchedDancers allow dancers in the input formation not to
     *        match dancers in (copies of) the goal
     * @param usePhantoms allow dancers in the goal formation not to match
     *        dancers in the input
     * @return the match result
     * @throws NoMatchException if there is no way to match the goal formation
     *   with the given input
     * @doc.test A successful match with no phantoms or unmatched dancers:
     *  js> GeneralFormationMatcher.doMatch(Formation.SQUARED_SET,
     *    >                                 FormationList.COUPLE,
     *    >                                 false, false)
     *       AAv
     *  
     *  BB>       CC<
     *  
     *       DD^
     *  AA:
     *     3B^  3G^
     *   [3B: BEAU; 3G: BELLE]
     *  BB:
     *     4B^  4G^
     *   [4B: BEAU; 4G: BELLE]
     *  CC:
     *     2B^  2G^
     *   [2B: BEAU; 2G: BELLE]
     *  DD:
     *     1B^  1G^
     *   [1B: BEAU; 1G: BELLE]
     * @doc.test A successful match with some unmatched dancers:
     *  js> GeneralFormationMatcher.doMatch(FormationList.RH_TWIN_DIAMONDS,
     *    >                                 FormationList.RH_MINIWAVE,
     *    >                                 true, false)
     *  AA>  BB>
     *  
     *  CC^  DDv
     *  
     *  EE<  FF<
     *  AA:
     *     ^
     *  BB:
     *     ^
     *  CC:
     *     ^    v
     *   [ph: BEAU; ph: BEAU]
     *  DD:
     *     ^    v
     *   [ph: BEAU; ph: BEAU]
     *  EE:
     *     ^
     *  FF:
     *     ^
     * @doc.test When possible, symmetry is preserved in the result:
     *  js> GeneralFormationMatcher.doMatch(FormationList.PARALLEL_RH_WAVES,
     *    >                                 FormationList.RH_MINIWAVE,
     *    >                                 false, false)
     *  AA^  BBv
     *  
     *  CC^  DDv
     *  AA:
     *     ^    v
     *   [ph: BEAU; ph: BEAU]
     *  BB:
     *     ^    v
     *   [ph: BEAU; ph: BEAU]
     *  CC:
     *     ^    v
     *   [ph: BEAU; ph: BEAU]
     *  DD:
     *     ^    v
     *   [ph: BEAU; ph: BEAU]
     */
    // booleans for 'allow unmatched dancers' and
    // 'use phantoms' allow dancers in the input and result formations,
    // respectively, not to match up.
    // XXX: implement usePhantoms
    // NOTE THAT result will include 1-dancer formations if
    // allowUnmatchedDancers is true; see the contract of FormationMatch.
    public static FormationMatch doMatch(
            final Formation input, 
            final TaggedFormation goal,
            boolean allowUnmatchedDancers,
            boolean usePhantoms)
    throws NoMatchException {
        assert !usePhantoms : "matching with phantoms is not implemented";
        // get an appropriate formation name
        String target;
        if (goal instanceof NamedTaggedFormation)
            target = ((NamedTaggedFormation)goal).getName();
        else
            target = goal.toString();

        // okay, try to perform match by trying to use each dancer in turn
        // as dancer #1 in the goal formation.  We then validate the match:
        // make sure that there is a dancer in each position, that no dancer
        // in the match is already in another match, and that the state isn't
        // redundant due to symmetry.  (To determine this last, we identify
        // those 'other dancers' in the goal formation which are rotationally
        // symmetric to dancer #1, and make sure that the proposed match
        // doesn't assign any of these positions to dancers we've already
        // tried as #1.)  Finally, we'll have a list of matches.  We
        // identify the ones with a maximum number of the goal formation,
        // and assert that this maximal match is unique; otherwise the
        // match is ambiguous and we throw NoMatchException.
        // (note that we ignore unselected dancers in formation f)
        // (note that 'dancer #1' is really 'dancer #0' below)
        if (goal.dancers().size() > input.dancers().size())
            throw new NoMatchException(target, "goal is too large");

        // Make a canonical ordering for the goal dancers.
        List<Dancer> goalDancers=new ArrayList<Dancer>(goal.dancers());
        // sort so that first dancers' target rotations are most constrained,
        // for efficiency. (ie largest rotation moduli are first)
        final Comparator<Position> pcomp = new Comparator<Position>() {
            public int compare(Position p1, Position p2) {
                int c = -p1.facing.modulus.compareTo(p2.facing.modulus);
                if (c!=0) return c;
                return p1.compareTo(p2);
            }
        };
        Collections.sort(goalDancers, new Comparator<Dancer>() {
            public int compare(Dancer d1, Dancer d2) {
                return pcomp.compare(goal.location(d1), goal.location(d2));
            }
        });
        SetFactory<Dancer> gsf = new BitSetFactory<Dancer>(goalDancers);
        // sort the input dancers the same way: real dancers before phantoms.
        // there must be at least one non-phantom dancer in the formation.
        // in addition, group symmetric dancers together in the order, so
        // that the resulting matches tend to symmetry.
        final List<Dancer> inputDancers=new ArrayList<Dancer>(input.dancers());
        Collections.sort(inputDancers, new Comparator<Dancer>() {
            /** minimum of position rotated through 4 quarter rotations */
            private Position qtrMin(Position p) {
                return Collections.min(rotated(p), pcomp);
            }
            /** minimum of position rotated by 180 degrees */
            private Position halfMin(Position p) {
                Position pprime = p.rotateAroundOrigin(ExactRotation.ONE_HALF);
                return Collections.min(Arrays.asList(p,pprime), pcomp);
            }
            public int compare(Dancer d1, Dancer d2) {
                Position p1 = input.location(d1), p2 = input.location(d2);
                // first comparison is against min of quarter-rotated versions
                int c = pcomp.compare(qtrMin(p1), qtrMin(p2));
                if (c!=0) return c;
                // now, compare against min of half-rotated versions
                c = pcomp.compare(halfMin(p1), halfMin(p2));
                if (c!=0) return c;
                // finally, break ties by comparing against "real" position
                return pcomp.compare(p1, p2);
            }
        });
        final Indexer<Dancer> inputIndex = new Indexer<Dancer>() {
            Map<Dancer,Integer> index = new HashMap<Dancer,Integer>();
            { int i=0; for (Dancer d: inputDancers) index.put(d, i++); }
            @Override
            public int getID(Dancer d) { return index.get(d); }
            @Override
            public Dancer getByID(int id) { return inputDancers.get(id); }
            @Override
            public boolean implementsReverseMapping() { return true; }
        };
        final PersistentSet<Dancer> inputEmpty = new PersistentSet<Dancer>
        (new Comparator<Dancer>() {
           public int compare(Dancer d1, Dancer d2) {
               return inputIndex.getID(d1) - inputIndex.getID(d2);
            } 
        });
        
        // Identify dancers who are symmetric to dancer #1
        assert goal.isCentered(); // Assumes center of goal formation is 0,0
        Dancer gd0 = goalDancers.get(0);
        Set<Dancer> eq0 = gsf.makeSet();
        Position p0 = goal.location(gd0).normalize(); // remember p0.facing is most exact
        for (Dancer gd : goalDancers)
            for (Position rp: rotated(goal.location(gd)))
                    if (rp.x.equals(p0.x) && rp.y.equals(p0.y)&& 
                            rp.facing.includes(p0.facing))
                        eq0.add(gd);
        assert eq0.contains(gd0);//at the very least, gd0 is symmetric to itself
        
        // now try setting each dancer in 'f' to d0 in the goal formation.

        // Construct MatchInfo & initial (empty) assignment
        MatchInfo mi = new MatchInfo(input, goal, inputDancers, inputIndex, inputEmpty, goalDancers, eq0);
        PersistentSet<OneMatch> initialAssignment = new PersistentSet<OneMatch>
        (new Comparator<OneMatch>(){
            public int compare(OneMatch o1, OneMatch o2) {
                int c=inputIndex.getID(o1.dancer)-inputIndex.getID(o2.dancer);
                if (c!=0) return c;
                return o1.extraRot.compareTo(o2.extraRot);
            }
        }); 
        // Do the match
        tryOne(mi, 0, initialAssignment, inputEmpty, allowUnmatchedDancers);
        if (mi.matches.isEmpty())
            throw new NoMatchException(target, "no matches");
        
        // Filter out the max
        int max = 0;
        for (PersistentSet<OneMatch> match: mi.matches)
            max = Math.max(max,match.size());
        assert max > 0;
        // Is it unique?
        PersistentSet<OneMatch> bestMatch=null; boolean found = false;
        for (PersistentSet<OneMatch> match: mi.matches)
            if (match.size()==max)
                if (found) // ambiguous match.
                    throw new NoMatchException(target, "ambiguous");
                else {
                    bestMatch = match;
                    found = true;
                }
        assert found;
        // track the input dancers who aren't involved in matches
        Set<Dancer> unmappedInputDancers = new LinkedHashSet<Dancer>(inputDancers);
        // Create a FormationMatch object from FormationPieces.
	List<FormationPiece> pieces = new ArrayList<FormationPiece>(max);
        Map<Dancer,TaggedFormation> canonical=new LinkedHashMap<Dancer,TaggedFormation>();
        for (OneMatch om : bestMatch) {
            Dancer id0 = om.dancer;//input dancer who's #1 in the goal formation
            int dn0 = inputIndex.getID(id0);
            Position inP = mi.inputPositions.get(dn0);
            assert inP.facing instanceof ExactRotation :
                "at least one real dancer must be in formation";
            // make an ExactRotation for pGoal, given the extraRot
            Position goP = makeExact(mi.goalPositions.get(0), om.extraRot);
            Warp warpF = Warp.rotateAndMove(goP, inP);
            Warp warpB = Warp.rotateAndMove(inP, goP);
            ExactRotation rr = (ExactRotation) inP.facing.subtract(goP.facing.amount);
            Map<Dancer,Position> subPos = new LinkedHashMap<Dancer,Position>();
            MultiMap<Dancer,Tag> subTag = new GenericMultiMap<Dancer,Tag>();
            for (Dancer goD : mi.goalDancers) {
                goP = goal.location(goD);
                // warp to find which input dancer corresponds to this one
                inP = warpF.warp(goP, Fraction.ZERO);
                Dancer inD = mi.inputPositionMap.get(zeroRotation(inP));
                // warp back to get an exact rotation for this version of goal
                goP = warpB.warp(input.location(inD), Fraction.ZERO);
                // add to this subformation.
                subPos.put(inD, goP);
                subTag.addAll(inD, goal.tags(goD));
                unmappedInputDancers.remove(inD);
            }
            TaggedFormation tf =
                new TaggedFormation(new Formation(subPos), subTag);
	    Dancer dd = new PhantomDancer();
	    canonical.put(dd, tf);

            Formation pieceI = input.select(tf.dancers()).onlySelected();
            Formation pieceO = new Formation(m(p(dd, new Position(0,0,rr))));
            pieces.add(new FormationPiece(pieceI, pieceO));
        }
        // add pieces for unmapped dancers (see spec for FormationMatch.meta)
        for (Dancer d : unmappedInputDancers) {
	    // these clauses are parallel to the ones above for matched dancers
            Position inP = input.location(d);
            Position goP = Position.getGrid(0,0,"n");
            ExactRotation rr = (ExactRotation) // i know this is a no-op.
		inP.facing.subtract(goP.facing.amount);

            Dancer dd = new PhantomDancer();
            TaggedFormation tf = new TaggedFormation
		(new TaggedDancerInfo(d, goP));
            canonical.put(dd, tf);

            Formation pieceI = input.select(tf.dancers()).onlySelected();
            Formation pieceO = new Formation(m(p(dd, new Position(0,0,rr))));
            pieces.add(new FormationPiece(pieceI, pieceO));
        }
        // the components formations are the warped & rotated version.
        // the rotation in 'components' tells how much they were rotated.
        // the canonical formations have the input dancers, and the formations
        // are unwarped and unrotated.  The key dancers in the canonical map
        // are the phantoms from the meta formation.
        return new FormationMatch(Breather.breathe(pieces), canonical,
                                  unmappedInputDancers);
    }
    private static class OneMatch {
        /** This input dancer is #1 in the goal formation. */
        public final Dancer dancer;
        /** This is the 'extra rotation' needed to align the goal formation,
         * if dancer #1 in the goal formation allows multiple orientations. */
        public final Fraction extraRot;
        OneMatch(Dancer dancer, Fraction extraRot) {
            this.dancer = dancer; this.extraRot = extraRot;
        }
    }
    private static class MatchInfo {
        final List<PersistentSet<OneMatch>> matches = new ArrayList<PersistentSet<OneMatch>>();
        final Indexer<Dancer> inputIndex;
        final Map<Position,Dancer> inputPositionMap = new HashMap<Position,Dancer>();
        final List<Position> inputPositions = new ArrayList<Position>();
        final List<Dancer> goalDancers;
        final List<Position> goalPositions = new ArrayList<Position>();
        final Set<Dancer> eq0; // goal dancers who are symmetric to goal dancer #0
        final int numInput;
        final int numExtra; // number of 'extra' rotations we'll try to match
        final Set<Dancer> sel; // input dancers who are selected
        // these next one is used to pass info into validate & back:
        /** Input dancers who are already assigned to a formation. */
        PersistentSet<Dancer> inFormation;
        MatchInfo(Formation f, TaggedFormation goal,
                  List<Dancer> inputDancers, Indexer<Dancer> inputIndex,
                  PersistentSet<Dancer> inputEmpty,
                  List<Dancer> goalDancers, Set<Dancer> eq0) {
            for (Dancer d : inputDancers) {
                Position p = f.location(d);
                this.inputPositions.add(p);
                this.inputPositionMap.put(zeroRotation(p), d);
            }
            this.numInput = inputDancers.size();
            this.inputIndex = inputIndex;
            this.goalDancers = goalDancers;
            for (Dancer d : goalDancers) {
                Position p = goal.location(d);
                this.goalPositions.add(p);
            }
            this.eq0 = eq0;
            this.sel = f.selected;
            this.inFormation = inputEmpty;
            // first goal dancer has a rotation modulus which is 1/N for some
            // N.  This means we need to try N other rotations for matches.
            this.numExtra = goal.location
                (goalDancers.get(0)).facing.modulus.getDenominator();
        }
    }
    private static boolean validate(MatchInfo mi, int dancerNum, Fraction extraRot) {
        PersistentSet<Dancer> inFormation = mi.inFormation;
        Set<Dancer> eq0 = mi.eq0;
        // find some Dancer in the input formation to correspond to each
        // Dancer in the goal formation.  Each such dancer must not already
        // be assigned.
        Position pIn = mi.inputPositions.get(dancerNum);
        assert pIn.facing instanceof ExactRotation :
            "at least one dancer in the input formation must be non-phantom";
        Position pGoal = makeExact(mi.goalPositions.get(0), extraRot);
        Warp warp = Warp.rotateAndMove(pGoal, pIn);
        int gNum = 0;
        for (Position gp : mi.goalPositions) {
            // compute warped position.
            gp = warp.warp(gp, Fraction.ZERO);
            Position key = zeroRotation(gp);
            if (!mi.inputPositionMap.containsKey(key))
                return false; // no input dancer at this goal position.
            // okay, there is an input dancer:
            Dancer iDan = mi.inputPositionMap.get(key);
            int iNum = mi.inputIndex.getID(iDan);
            // if this dancer selected?
            if (!mi.sel.contains(iDan))
                return false; // this dancer isn't selected.
            // is he free to be assigned to this formation?
            if (inFormation.contains(iDan))
                return false; // this dancer is already in some match
            // check for symmetry: if this goal position is 'eq0' (ie,
            // symmetric with the 0 dancer's position), then this dancer #
            // must be >= the 0 dancer's input # (ie, dancerNum)
            if (eq0.contains(mi.goalDancers.get(gNum)))
                    if (iNum < dancerNum)
                        return false; // symmetric to some other canonical formation
            // is his facing direction consistent?
            Position ip = mi.inputPositions.get(iNum);
            assert ip.x.equals(gp.x) && ip.y.equals(gp.y);
            if (!gp.facing.includes(ip.facing))
                return false; // rotations aren't correct.
            // update 'in formation' and 'gNum'
            inFormation = inFormation.add(iDan);
            gNum++;
        }
        // return updates to inFormation
        mi.inFormation = inFormation;
        return true; // this is a valid match.
    }
        
    private static void tryOne(MatchInfo mi, int dancerNum,
            PersistentSet<OneMatch> currentAssignment,
            PersistentSet<Dancer> inFormation,
            boolean allowUnmatchedDancers) {
        if (dancerNum >= mi.numInput) {
            if (inFormation.size() != mi.numInput)
                if (!allowUnmatchedDancers)
                    return; // not a good assignment
            // we've got a complete assignment; save it.
            if (!currentAssignment.isEmpty())
                mi.matches.add(currentAssignment);
            return;
        }
        // try NOT assigning this dancer
        tryOne(mi, dancerNum+1, currentAssignment, inFormation,
               allowUnmatchedDancers);
        // okay, try to assign the next dancer, possibly w/ some extra rotation
        for (int i=0; i < mi.numExtra; i++) {
            Fraction extraRot = Fraction.valueOf(i,mi.numExtra);
            PersistentSet<OneMatch> newAssignment = currentAssignment.add
                (new OneMatch(mi.inputIndex.getByID(dancerNum), extraRot));
            mi.inFormation = inFormation;
            if (validate(mi, dancerNum, extraRot))
                tryOne(mi, dancerNum+1, newAssignment, mi.inFormation,
                        allowUnmatchedDancers);
        }
    }

    /** Make a position with an ExactRotation from the given position with a
     * general rotation and an 'extra rotation' amount. */
    private static Position makeExact(Position p, Fraction extraRot) {
        return new Position(p.x, p.y, 
                new ExactRotation(p.facing.amount.add(extraRot)));
    }
    /** Make a position with exact zero rotation from the given position. */
    private static Position zeroRotation(Position p) {
        return new Position(p.x, p.y, ExactRotation.ZERO);
    }
    private static Set<Position> rotated(Position p) {
        Set<Position> s = new LinkedHashSet<Position>(4);
        for (int i=0; i<4; i++) {
            s.add(p);
            p = p.rotateAroundOrigin(ExactRotation.ONE_QUARTER);
        }
        return s;
    }
    /** @deprecated XXX: rewrite to remove dependency on old Warp class */
    private static abstract class Warp {
        public abstract Position warp(Position p, Fraction time);
        /** A <code>Warp</code> which returns points unchanged. */
        public static final Warp NONE = new Warp() {
            public Position warp(Position p, Fraction time) { return p; }
        };
	/** Returns a <code>Warp</code> which will rotate and translate
	 * points such that <code>from</code> is warped to <code>to</code>.
	 * Requires that both {@code from.facing} and {@code to.facing} are
	 * {@link ExactRotation}s.
	 */
	// XXX is this the right spec?  Should we allow general Rotations?
	public static Warp rotateAndMove(Position from, Position to) {
	    assert from.facing instanceof ExactRotation;
	    assert to.facing instanceof ExactRotation;
	    if (from.equals(to)) return NONE;
	    ExactRotation rot = (ExactRotation) to.facing.add(from.facing.amount.negate());
	    Position nFrom = rotateCWAroundOrigin(from,rot);
	    final Position warp = new Position
		(to.x.subtract(nFrom.x), to.y.subtract(nFrom.y),
		 rot);
	    Warp w = new Warp() {
	        public Position warp(Position p, Fraction time) {
		    p = rotateCWAroundOrigin(p, (ExactRotation) warp.facing);
		    return p.relocate
                    (p.x.add(warp.x), p.y.add(warp.y), p.facing);
		}
	    };
	    assert to.setFlags(from.flags.toArray(new Flag[0])).equals(w.warp(from,Fraction.ZERO)) : "bad warp "+to+" vs "+w.warp(from, Fraction.ZERO);
	    return w;
	}
	// helper method for rotateAndMove
	private static Position rotateCWAroundOrigin(Position p, ExactRotation amt) {
	    Fraction x = p.x.multiply(amt.toY()).add(p.y.multiply(amt.toX()));
	    Fraction y = p.y.multiply(amt.toY()).subtract(p.x.multiply(amt.toX()));
	    return p.relocate(x, y, p.facing.add(amt.amount));
	}
    }
}
