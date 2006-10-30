package net.cscott.sdr.calls;

import java.util.ArrayList;
import java.util.List;

import net.cscott.sdr.calls.TaggedFormation.Tag;
import static net.cscott.sdr.calls.SelectorList.*;

import org.junit.Test;
import static org.junit.Assert.*;
import junit.framework.JUnit4TestAdapter;

public class TestSelectors {

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(TestSelectors.class);
    }
    
    @Test
    public void testCouplesInSquaredSet() {
        // test 
        Formation f = Formation.SQUARED_SET;
        FormationMatch fm = COUPLE.match(f);
        assertEquals(fm.matches.size(), 4);
        assertEquals(fm.meta.dancers().size(), 4);
        for (Dancer md : fm.meta.dancers()) {
            TaggedFormation tf = fm.matches.get(md);
            for (Dancer d : tf.dancers()) {
                assertEquals(tf.isTagged(d, Tag.BEAU),
                        d.isBoy());
                assertEquals(tf.isTagged(d, Tag.BELLE),
                        d.isGirl());
            }
        }
    }
    @Test
    public void testCouplesInFourSquare() {
        // test 
        Formation f = Formation.FOUR_SQUARE;
        FormationMatch fm = COUPLE.match(f);
        assertEquals(fm.matches.size(), 2);
        assertEquals(fm.meta.dancers().size(), 2);
        for (Dancer md : fm.meta.dancers()) {
            TaggedFormation tf = fm.matches.get(md);
            for (Dancer d : tf.dancers()) {
                assertEquals(tf.isTagged(d, Tag.BEAU),
                        d.isBoy());
                assertEquals(tf.isTagged(d, Tag.BELLE),
                        d.isGirl());
            }
        }
    }
    @Test
    public void testFacingDancersInFourSquare() {
        // test 
        Formation f = Formation.FOUR_SQUARE;
        FormationMatch fm = FACING_DANCERS.match(f);
        assertEquals(fm.matches.size(), 2);
        assertEquals(fm.meta.dancers().size(), 2);
        for (Dancer md : fm.meta.dancers()) {
            TaggedFormation tf = fm.matches.get(md);
            List<Dancer> l = new ArrayList<Dancer>(tf.dancers());
            assertEquals(l.size(), 2);
            assertEquals(l.get(0).isBoy(), l.get(1).isGirl());
        }
    }
    @Test
    public void testFacingCouplesInFourSquare() {
        // test 
        Formation f = Formation.FOUR_SQUARE;
        FormationMatch fm = FACING_COUPLES.match(f);
        assertEquals(fm.matches.size(), 1);
        assertEquals(fm.meta.dancers().size(), 1);
    }
}
