package net.cscott.sdr.calls.grm;

import java.util.Map;

import net.cscott.sdr.calls.Program;

/** Abstract class storing a post-processed natural grammar, as a map from
 * nonterminal name (a String) to a {@link Grm}.
 */
public abstract class GrmDB {
    public abstract Map<String,Grm> grammar();

    public static final GrmDB dbFor(Program program) {
        if (program!=Program.BASIC) program=Program.C4; // for debugging
        String pkgName = "net.cscott.sdr.calls.lists.";
        String baseName = program.toTitleCase()+"Grm";
        try {
            GrmDB gdb = (GrmDB) Class.forName(pkgName+baseName)
                .getConstructor().newInstance();
            return gdb;
        } catch (Exception e) {
            assert false : "should never get here!";
            return null;
        }
    }
}
