package net.cscott.sdr.calls;

import net.cscott.sdr.calls.FormationMatch.TaggedFormationAndWarp;

/** The selector list creates selectors for various formations. */
public abstract class SelectorList {
    // 0-person selectors
    public static final Selector NONE = new Selector() {
        public FormationMatch match(Formation f) throws NoMatchException {
            throw new NoMatchException();
        }
    };
    // 2-person selectors
    public static final Selector COUPLE = 
        GeneralFormationMatcher.makeSelector(FormationList.COUPLE);
    public static final Selector RH_MINIWAVE =
        GeneralFormationMatcher.makeSelector(FormationList.RH_MINIWAVE);
    public static final Selector LH_MINIWAVE =
        GeneralFormationMatcher.makeSelector(FormationList.LH_MINIWAVE);
    public static final Selector FACING_DANCERS =
        GeneralFormationMatcher.makeSelector(FormationList.FACING_DANCERS);
    public static final Selector TANDEM =
        GeneralFormationMatcher.makeSelector(FormationList.TANDEM);
    // 4-person selectors
    public static final Selector FACING_COUPLES =
        GeneralFormationMatcher.makeSelector(FormationList.FACING_COUPLES);

    // unimplemented selectors
    private static final Selector _STUB_ = new Selector() {
        @Override
        public FormationMatch match(Formation f) throws NoMatchException {
            assert false : "unimplemented";
            throw new RuntimeException("unimplemented");
        }
    };
    public static final Selector RH_TWO_FACED_LINE = _STUB_;
    public static final Selector LH_TWO_FACED_LINE = _STUB_;
    public static final Selector FACING_COUPLE = _STUB_;
    public static final Selector SINGLE_DPT = _STUB_;
    public static final Selector LH_BOX = _STUB_;
    public static final Selector RH_BOX = _STUB_;
    public static final Selector RH_WAVE = _STUB_;
    public static final Selector LH_WAVE = _STUB_;
    public static final Selector GENERAL_BOX = _STUB_;
    public static final Selector LH_3_AND_1 = _STUB_;
    public static final Selector LH_SPLIT_3_AND_1 = _STUB_;
    public static final Selector RH_3_AND_1 = _STUB_;
    public static final Selector RH_SPLIT_3_AND_1 = _STUB_;
    public static final Selector PARALLEL_GENERAL_LINES = _STUB_;
    public static final Selector GENERAL_COLUMNS = _STUB_;
    
    public static void main(String[] args) throws Exception {
        // test 
        Formation f = Formation.SQUARED_SET;
        FormationMatch fm = COUPLE.match(f);
        System.out.println(fm.matches.size()+" couples");
        for (TaggedFormationAndWarp fw : fm.matches) {
            System.out.println(fw.tf);
            System.out.println(fw.w);
        }
    }
}
