package net.cscott.sdr.calls;


/** The selector list creates selectors for various formations. */
public abstract class SelectorList {
    // 0-person selectors
    public static final Selector NONE = new Selector() {
        public FormationMatch match(Formation f) throws NoMatchException {
            throw new NoMatchException("NONE selector used");
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
    public static final Selector RH_OCEAN_WAVE =
        GeneralFormationMatcher.makeSelector(FormationList.RH_OCEAN_WAVE);
    public static final Selector LH_OCEA_WAVE =
        GeneralFormationMatcher.makeSelector(FormationList.LH_OCEAN_WAVE);
    public static final Selector RH_DIAMOND =
        GeneralFormationMatcher.makeSelector(FormationList.RH_DIAMOND);
    public static final Selector LH_DIAMOND =
        GeneralFormationMatcher.makeSelector(FormationList.LH_DIAMOND);

    // unimplemented selectors
    private static final Selector _STUB_ = new Selector() {
        @Override
        public FormationMatch match(Formation f) throws NoMatchException {
            assert false : "unimplemented";
            throw new RuntimeException("unimplemented");
        }
        @Override
        public String toString() { return "*STUB*"; }
    };
    public static final Selector RH_TWO_FACED_LINE = _STUB_;
    public static final Selector LH_TWO_FACED_LINE = _STUB_;
    public static final Selector SINGLE_DPT = _STUB_;
    public static final Selector LH_BOX = _STUB_;
    public static final Selector RH_BOX = _STUB_;
    public static final Selector GENERAL_LINE = _STUB_;
    public static final Selector GENERAL_BOX = _STUB_;
    public static final Selector LH_3_AND_1 = _STUB_;
    public static final Selector LH_SPLIT_3_AND_1 = _STUB_;
    public static final Selector RH_3_AND_1 = _STUB_;
    public static final Selector RH_SPLIT_3_AND_1 = _STUB_;
    public static final Selector PARALLEL_GENERAL_LINES = _STUB_;
    public static final Selector GENERAL_COLUMNS = _STUB_;
    public static final Selector FACING_TANDEMS = _STUB_;
}
