package net.cscott.sdr.calls;

import net.cscott.sdr.calls.ast.Apply;
import net.cscott.sdr.calls.ast.Comp;
import net.cscott.sdr.util.Fraction;

public class Test {
    public static void main(String[] args) {
        Call trade = CallDB.INSTANCE.lookup("double pass thru");
        System.out.println(trade);
        Comp def = trade.apply(null);
        System.out.println(def.toStringList());
        //
        Call sqthr = CallDB.INSTANCE.lookup("square thru");
        System.out.println(sqthr);
        def = sqthr.apply(Apply.makeApply("square thru", Fraction.valueOf("3 1/2")));
        System.out.println(def.toStringList());
    }
}
