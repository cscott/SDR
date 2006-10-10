package net.cscott.sdr.calls;

import net.cscott.sdr.calls.FormationMatch.TaggedFormationAndWarp;

/** The selector list creates selectors for various formations. */
public abstract class SelectorList {
    public static final Selector NONE = new Selector() {
        public FormationMatch match(Formation f) throws NoMatchException {
            throw new NoMatchException();
        }
    };
    public static final Selector COUPLE = 
        GeneralFormationMatcher.makeSelector(FormationList.COUPLE);


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
