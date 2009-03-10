package net.cscott.sdr.calls;

/** {@link DanceProgram} captures all the static information about a dance
 * which a {@link Predicate} might need to know.  An example might be
 * the current dance program.
 * @author C. Scott Ananian
 * @version $Id: DanceProgram.java,v 1.3 2006-10-25 20:37:20 cananian Exp $
 */
public class DanceProgram {
    private final Program program;
    public DanceProgram(Program program) { this.program = program; }
    /**
     * @return Returns the dance program.
     */
    public Program getProgram() {
        return program;
    }
}
