package net.cscott.sdr.calls;

import java.util.*;

import net.cscott.sdr.calls.FormationMapper.FormationPiece;
import net.cscott.sdr.util.Fraction;
import net.cscott.jutil.*;

public abstract class GeneralFormationMatcher {
    // currying, oh, my
    public static Selector makeSelector(final TaggedFormation goal) {
        return new Selector() {
            public FormationMatch match(Formation f) throws NoMatchException {
                return doMatch(f, goal, false, false);
            }
            public String toString() { return goal.toString(); }
        };
    }

    // eventually: pass booleans for 'allow unmatched dancers' and
    // 'use phantoms' which allow dancers in the input and result formations,
    // respectively, not to match up.
    public static FormationMatch doMatch(
            final Formation input, 
            final TaggedFormation goal,
            boolean allowUnmatchedDancers,
            boolean usePhantoms)
    throws NoMatchException {
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
            throw new NoMatchException("goal is too large");

        // Make a canonical ordering for the goal dancers.
        List<Dancer> goalDancers=new ArrayList<Dancer>(goal.dancers());
        // sort so that first dancers' target rotations are most constrained,
        // for efficiency. (ie largest rotation moduli are first)
        Collections.sort(goalDancers, new Comparator<Dancer>() {
            public int compare(Dancer d1, Dancer d2) {
                return -goal.location(d1).facing.modulus.compareTo
                (goal.location(d2).facing.modulus);
            }
        });
        SetFactory<Dancer> gsf = new BitSetFactory<Dancer>(goalDancers);
        // sort the input dancers the same way: real dancers before phantoms.
        // there must be at least one non-phantom dancer in the formation.
        final List<Dancer> inputDancers=new ArrayList<Dancer>(input.dancers());
        Collections.sort(inputDancers, new Comparator<Dancer>() {
            public int compare(Dancer d1, Dancer d2) {
                return -input.location(d1).facing.modulus.compareTo
                (input.location(d2).facing.modulus);
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
        Position p0 = goal.location(gd0).normalize();
        for (Dancer gd : goalDancers)
            if (rotated(goal.location(gd)).contains(p0))
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
        tryOne(mi, 0, initialAssignment, inputEmpty);
        if (mi.matches.isEmpty())
            throw new NoMatchException("no matches");
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
                    throw new NoMatchException("ambiguous");
                else {
                    bestMatch = match;
                    found = true;
                }
        assert found;
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
            Warp warp = Warp.rotateAndMove(goP, inP);
	    ExactRotation rr = (ExactRotation) inP.facing.subtract(goP.facing.amount);
            Map<Dancer,Dancer> map = new HashMap<Dancer,Dancer>();
            for (int g=0; g<mi.goalPositions.size(); g++) {
                goP = mi.goalPositions.get(g);
                inP = warp.warp(goP, Fraction.ZERO);
                Dancer id = mi.inputPositionMap.get(zeroRotation(inP));
                map.put(goalDancers.get(g), id);
            }
	    Formation piece = input.select(new HashSet<Dancer>(map.values())).onlySelected();
	    Dancer dd = new PhantomDancer();
            TaggedFormation tf = new TaggedFormation(goal, map);
	    canonical.put(dd, tf);
	    pieces.add(new FormationPiece(piece, dd, rr));
        }
        // the components formations are the warped & rotated version.
        // the rotation in 'components' tells how much they were rotated.
        // the canonical formations have the input dancers, and the formations
        // are unwarped and unrotated.  The key dancers in the canonical map
        // are the phantoms from the meta formation.
        return new FormationMatch(FormationMapper.compress(pieces), canonical);
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
            PersistentSet<OneMatch> currentAssignment, PersistentSet<Dancer> inFormation) {
        if (dancerNum >= mi.numInput) {
            // we've got a complete assignment; save it.
            if (!currentAssignment.isEmpty())
                mi.matches.add(currentAssignment);
            return;
        }
        // try NOT assigning this dancer
        tryOne(mi, dancerNum+1, currentAssignment, inFormation);
        // okay, try to assign the next dancer, possibly w/ some extra rotation
        for (int i=0; i < mi.numExtra; i++) {
            Fraction extraRot = Fraction.valueOf(i,mi.numExtra);
            PersistentSet<OneMatch> newAssignment = currentAssignment.add
                (new OneMatch(mi.inputIndex.getByID(dancerNum), extraRot));
            mi.inFormation = inFormation;
            if (validate(mi, dancerNum, extraRot))
                tryOne(mi, dancerNum+1, newAssignment, mi.inFormation);
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
        Set<Position> s = new HashSet<Position>(4);
        for (int i=0; i<4; i++) {
            p = ONE_QUARTER_AROUND_ORIGIN.warp(p, Fraction.ZERO);
            s.add(p);
        }
        return s;
    }
    private static final Warp ONE_QUARTER_AROUND_ORIGIN = Warp.rotateAndMove
           (Position.getGrid(0,0,ExactRotation.ZERO),
            Position.getGrid(0,0,ExactRotation.ONE_QUARTER));
}
