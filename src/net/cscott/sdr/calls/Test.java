package net.cscott.sdr.calls;

import java.util.HashMap;
import java.util.Map;
import net.cscott.sdr.calls.ast.Apply;
import net.cscott.sdr.calls.ast.Comp;
import net.cscott.sdr.calls.transform.Elaborate;
import net.cscott.sdr.calls.transform.RemoveIn;
import net.cscott.sdr.util.Fraction;

public class Test {
    public static void main(String[] args) {
        // test basic call database functionality
        Comp def = CallDB.INSTANCE.parse(Program.BASIC, "double pass thru").expand();
        System.out.println("DEFINITION OF double pass thru (expanded):");
        System.out.println(def);
        // again, with an application
        Call sqthr = CallDB.INSTANCE.lookup("square thru");
        System.out.println("DEFINITION OF square thru (unexpanded):");
        System.out.println(sqthr);
        def = sqthr.apply(Apply.makeApply("square thru", Fraction.valueOf("3 1/2")));
        System.out.println("DEFINITION OF square thru 3 1/2");
        System.out.println(def);
        System.out.println();

        // expanding an application
        Apply a = Apply.makeApply("_touch", Fraction.valueOf("3/4"));
        def = a.expand();
        System.out.println("EXPANSION OF _touch 3/4");
        System.out.println(def);
        // again
        a = Apply.makeApply("run", Apply.makeApply("boy"));
        def = a.expand();
        System.out.println("EXPANSION OF boys run");
        System.out.println(def);
        System.out.println("-----");

        // testing elaboration
        a = Apply.makeApply("_fractional", Apply.makeApply("1/2"), a);
        System.out.println("EXPANSION OF "+a);
        def = a.expand();
        System.out.println(def);
        // create a dance state (ds, plus tagged formation)
        DanceState ds = new DanceState(Program.MAINSTREAM);
        // associate real dancers with the TaggedFormation.
        TaggedFormation tf = m(FormationList.FACING_COUPLES, Formation.FOUR_SQUARE);
        System.out.println("ONE STEP ELABORATION FROM FACING COUPLES:");
        def = Elaborate.elaborate(ds, tf, def, false);
        System.out.println(def);
        System.out.println("FULL ELABORATION FROM FACING COUPLES:");
        def = Elaborate.elaborate(ds, tf, def, true);
        System.out.println(def);
        
        // test the RemoveIn module
        def = RemoveIn.removeIn(def);
        System.out.println("REMOVING THE In FROM 1/2 boys run FROM SQUARED SET");
        System.out.println(def);
    }
    /** Build a map from dancers in the TaggedFormation tf to real physical
     * dancers in Formation f, and use it to map the dancers in the tagged
     * formation to a new TaggedFormation with the real dancers.
     * @return the new TaggedFormation
     */
    private static TaggedFormation m(TaggedFormation tf, Formation f) {
        // build reverse map
        Map<Position,Dancer> floc = new HashMap<Position,Dancer>(f.dancers().size());
        for (Dancer d : f.dancers())
            floc.put(f.location(d), d);
        // use this to create a map from tf dancers to f dancers
        Map<Dancer,Dancer> dmap = new HashMap<Dancer,Dancer>(floc.size());
        for (Dancer d : tf.dancers())
            dmap.put(d, floc.get(tf.location(d)));
        // finally, create the new TaggedFormation
        return new TaggedFormation(tf, dmap);
    }
}
