package net.cscott.sdr.calls;

import net.cscott.sdr.calls.ast.Comp;

public class Test {
    public static void main(String[] args) {
        Call trade = CallDB.INSTANCE.lookup("double pass thru");
        System.out.println(trade);
        Comp def = trade.apply(null);
        System.out.println(def.toStringList());
    }

}
