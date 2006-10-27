package net.cscott.sdr.calls;

import java.util.*;

import net.cscott.sdr.calls.FormationMatch.TaggedFormationAndWarp;
import net.cscott.sdr.util.Fraction;

public abstract class GeneralFormationMatcher {
    public static enum MatchType { EXACT, GENERAL, TBONED }
    // currying, oh, my
    public static Selector makeSelector(final TaggedFormation goal) {
        return new Selector() {
            public FormationMatch match(Formation f) throws NoMatchException {
                return doMatch(f, goal);
            }
            public String toString() { return goal.toString(); }
        };
    }

    private static FormationMatch doMatch(Formation input, TaggedFormation goal)
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
            throw new NoMatchException("goal is too large"); // goal is too large

        // First, identify the symmetry.
        // Assumes center of goal formation is 0,0
        List<Dancer> goalDancers=new ArrayList<Dancer>(goal.dancers());
        Dancer d0 = goalDancers.get(0);
        long eq0 = 1; // include d0 (1L<<0)
        Position p0 = goal.location(d0).normalize();
        for (int i=1; i<goalDancers.size(); i++)
            if (rotated(goal.location(goalDancers.get(i))).contains(p0))
                eq0 |= (1L<<i);
        
        // now try setting each dancer in 'f' to d0 in the goal formation.
        assert input.dancers().size() <= 64 : "formation is too large for a long";

        // Construct MatchInfo
        List<Dancer> inputDancers=new ArrayList<Dancer>(input.dancers());
        MatchInfo mi = new MatchInfo(input, goal, inputDancers, goalDancers, eq0);
        // Do the match
        tryOne(mi, 0, 0, 0);
        if (mi.matches.isEmpty())
            throw new NoMatchException("no matches");
        // Filter out the max
        int max = 0;
        for (Long match: mi.matches)
            max = Math.max(max,Long.bitCount(match));
        assert max > 0;
        // Is it unique?
        long bestMatch=0; boolean found = false;
        for (Long match: mi.matches)
            if (Long.bitCount(match)==max)
                if (found) // ambiguous match.
                    throw new NoMatchException("ambiguous");
                else {
                    bestMatch = match;
                    found = true;
                }
        assert found;
        // Create a FormationMatch object.
        List<TaggedFormationAndWarp> ltfw =
            new ArrayList<TaggedFormationAndWarp>(max);
        while (bestMatch!=0) {
            int dn0 = Long.numberOfTrailingZeros(bestMatch);
            bestMatch &= ~(1L<<dn0); // clear that bit.
            // XXX: NEED TO STORE THE ROTATION INFO
            Position inP = mi.inputPositions.get(dn0); 
            Position goP = mi.goalPositions.get(0);
            Warp warp = Warp.rotateAndMove(goP, inP);
            Map<Dancer,Dancer> map = new HashMap<Dancer,Dancer>();
            for (int g=0; g<mi.goalPositions.size(); g++) {
                goP = mi.goalPositions.get(g);
                inP = warp.warp(goP, Fraction.ZERO);
                int i = mi.inputPositionMap.get(zeroRotation(inP));
                map.put(goalDancers.get(g), inputDancers.get(i));
            }
            TaggedFormation tf = new TaggedFormation(goal, map);
            ltfw.add(new TaggedFormationAndWarp(tf, warp));
        }
        return new FormationMatch(ltfw);
    }
    private static class MatchInfo {
        final List<Long> matches = new ArrayList<Long>();
        final Map<Position,Integer> inputPositionMap = new HashMap<Position,Integer>();
        final List<Position> inputPositions = new ArrayList<Position>();
        final List<Position> goalPositions = new ArrayList<Position>();
        final long eq0; // goal dancers who are symmetric to goal dancer #0
        final int numInput;
        final MatchType type = MatchType.EXACT; //XXX
        final long sel; // input dancers who are selected
        // these next one is used to pass info into validate & back
        long inFormation = 0; // input dancers who are already assigned to a formation
        MatchInfo(Formation f, TaggedFormation goal,
                  List<Dancer> inputDancers, List<Dancer> goalDancers,
                  long eq0) {
            int i=0;
            for (Dancer d : inputDancers) {
                Position p = f.location(d);
                this.inputPositions.add(p);
                this.inputPositionMap.put(zeroRotation(p), i++);
            }
            this.numInput = i;
            for (Dancer d : goalDancers) {
                Position p = goal.location(d);
                this.goalPositions.add(p);
            }
            this.eq0 = eq0;
            long sel = 0; i=0;
            for (Dancer d : inputDancers) {
                if (f.isSelected(d))
                    sel |= (1L<<i);
                i++;
            }
            this.sel = sel;
        }
    }
    private static boolean validate(MatchInfo mi, int dancerNum, Fraction extraRot) {
        long inFormation = mi.inFormation;
        long eq0 = mi.eq0;
        // find some Dancer in the input formation to correspond to each
        // Dancer in the goal formation.  Each such dancer must not already
        // be assigned.
        Position pIn = mi.inputPositions.get(dancerNum);
        Position pGoal = mi.goalPositions.get(0).turn(extraRot);
        Warp warp = Warp.rotateAndMove(pGoal, pIn);
        int gNum = 0;
        for (Position gp : mi.goalPositions) {
            // compute warped position.
            gp = warp.warp(gp, Fraction.ZERO);
            Position key = zeroRotation(gp);
            if (!mi.inputPositionMap.containsKey(key))
                return false; // no input dancer at this goal position.
            // okay, there is an input dancer:
            int iNum = mi.inputPositionMap.get(key);
            // if this dancer selected?
            if (0==(mi.sel & (1L<<iNum)))
                return false; // this dancer isn't selected.
            // is he free to be assigned to this formation?
            if (0!=(inFormation & (1L<<iNum)))
                return false; // this dancer is already in some match
            // check for symmetry: if this goal position is 'eq0' (ie,
            // symmetric with the 0 dancer's position), then this dancer #
            // must be >= the 0 dancer's input # (ie, dancerNum)
            if (0!=(eq0 & (1L<<gNum)))
                    if (iNum < dancerNum)
                        return false; // symmetric to some other canonical formation
            // is his facing direction consistent?
            Position ip = mi.inputPositions.get(iNum);
            assert ip.x.equals(gp.x) && ip.y.equals(gp.y);
            Rotation gr = gp.facing.normalize();
            Rotation ir = ip.facing.normalize();
            if (mi.type == MatchType.TBONED) {
                /* okay */
            } else if (gr.equals(ir)) {
                /* okay */
            } else if (mi.type == MatchType.GENERAL &&
                    gr.add(Fraction.ONE_HALF).normalize().equals(ir)) {
                /* okay */
            } else return false; // rotations aren't correct.
            // update 'in formation' and 'gNum'
            inFormation |= (1L<<iNum);
            gNum++;
        }
        // return updates to inFormation
        mi.inFormation = inFormation;
        return true; // this is a valid match.
    }
        
    private static void tryOne(MatchInfo mi, int dancerNum,
            long currentAssignment, long inFormation) {
        if (dancerNum >= mi.numInput) {
            // we've got a complete assignment; save it.
            if (currentAssignment!=0) {
                mi.matches.add(currentAssignment);
            }
            return;
        }
        // try NOT assigning this dancer
        tryOne(mi, dancerNum+1, currentAssignment, inFormation);
        // okay, try to assign the next dancer
        long newAssignment = currentAssignment | (1L<<dancerNum);
        mi.inFormation = inFormation;
        if (validate(mi,dancerNum,Fraction.ZERO)) {
            inFormation = mi.inFormation;
            tryOne(mi, dancerNum+1, newAssignment, inFormation);
        }
        // XXX try with rotations of 1/2 (if GENERAL) and 1/4,3/4 (in TBONED)
    }
    
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
