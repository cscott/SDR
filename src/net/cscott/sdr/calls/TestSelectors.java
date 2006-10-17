package net.cscott.sdr.calls;

import java.util.ArrayList;
import java.util.List;

import net.cscott.sdr.calls.FormationMatch.TaggedFormationAndWarp;
import net.cscott.sdr.calls.TaggedFormation.Tag;
import junit.framework.TestCase;
import static net.cscott.sdr.calls.SelectorList.*;

public class TestSelectors extends TestCase {

    public static void main(String[] args) {
        junit.swingui.TestRunner.run(TestSelectors.class);
    }

    public TestSelectors(String arg0) {
        super(arg0);
    }

    public void testCouplesInSquaredSet() {
        // test 
        Formation f = Formation.SQUARED_SET;
        FormationMatch fm = COUPLE.match(f);
        assertEquals(fm.matches.size(), 4);
        for (TaggedFormationAndWarp fw : fm.matches) {
            for (Dancer d : fw.tf.dancers()) {
                assertEquals(fw.tf.isTagged(d, Tag.BEAU),
                        d.isBoy());
                assertEquals(fw.tf.isTagged(d, Tag.BELLE),
                        d.isGirl());
            }
        }
    }
    public void testCouplesInFourSquare() {
        // test 
        Formation f = Formation.FOUR_SQUARE;
        FormationMatch fm = COUPLE.match(f);
        assertEquals(fm.matches.size(), 2);
        for (TaggedFormationAndWarp fw : fm.matches) {
            for (Dancer d : fw.tf.dancers()) {
                assertEquals(fw.tf.isTagged(d, Tag.BEAU),
                        d.isBoy());
                assertEquals(fw.tf.isTagged(d, Tag.BELLE),
                        d.isGirl());
            }
        }
    }
    public void testFacingDancersInFourSquare() {
        // test 
        Formation f = Formation.FOUR_SQUARE;
        FormationMatch fm = FACING_DANCERS.match(f);
        assertEquals(fm.matches.size(), 2);
        for (TaggedFormationAndWarp fw : fm.matches) {
            List<Dancer> l = new ArrayList<Dancer>(fw.tf.dancers());
            assertEquals(l.size(), 2);
            assertEquals(l.get(0).isBoy(), l.get(1).isGirl());
        }
    }
    public void testFacingCouplesInFourSquare() {
        // test 
        Formation f = Formation.FOUR_SQUARE;
        FormationMatch fm = FACING_COUPLES.match(f);
        assertEquals(fm.matches.size(), 1);
    }
}
