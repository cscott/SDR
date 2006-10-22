package net.cscott.sdr.calls;

import net.cscott.sdr.calls.ast.Apply;
import net.cscott.sdr.calls.ast.Comp;
import net.cscott.sdr.calls.transform.Elaborate;
import net.cscott.sdr.calls.transform.RemoveIn;
import net.cscott.sdr.util.Fraction;

public class Test {
    public static void main(String[] args) {
        Comp def = CallDB.INSTANCE.parse(Program.BASIC, "double pass thru").expand();
        System.out.println(def);
        //
        Call sqthr = CallDB.INSTANCE.lookup("square thru");
        System.out.println(sqthr);
        def = sqthr.apply(Apply.makeApply("square thru", Fraction.valueOf("3 1/2")));
        System.out.println(def);
        Apply a = Apply.makeApply("_touch", Fraction.valueOf("3/4"));
        def = a.expand();
        System.out.println(def);
        a = Apply.makeApply("run", Apply.makeApply("boy"));
        def = a.expand();
        System.out.println(def);
        System.out.println("-----");
        a = Apply.makeApply("_fractional", Apply.makeApply("1/2"), a);
        System.out.println(a);
        def = a.expand();
        System.out.println(def);
        DanceState ds = new DanceState(Program.MAINSTREAM);
        //def = Elaborate.elaborate(ds, Formation.FOUR_SQUARE, def, false);
        System.out.println(def);
        def = Elaborate.elaborate(ds, Formation.FOUR_SQUARE, def, true);
        System.out.println(def);
        def = RemoveIn.removeIn(def);
        System.out.println(def);
    }
}
