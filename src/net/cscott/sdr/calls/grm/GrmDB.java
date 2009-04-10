package net.cscott.sdr.calls.grm;

import java.util.Map;

import net.cscott.sdr.calls.Program;

/** Abstract class storing a post-processed natural grammar, as a map from
 * nonterminal name (a String) to a {@link Grm}.
 */
public abstract class GrmDB {
    public abstract Map<String,Grm> grammar();

    public static final Map<String,Grm> grammar(Program program) {
        String pkgName = "net.cscott.sdr.calls.lists.";
        String baseName = program.toTitleCase()+"Grm";
        try {
            GrmDB gdb = (GrmDB) Class.forName(pkgName+baseName)
                .getConstructor().newInstance();
            return gdb.grammar();
        } catch (Exception e) {
            assert false : "should never get here!";
            return null;
        }
    }
}
