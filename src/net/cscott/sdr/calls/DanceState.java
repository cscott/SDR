package net.cscott.sdr.calls;

/** {@link DanceState} captures all the static information about a dance
 * which a {@link Predicate} might need to know.  An example might be
 * the current dance program.
 * @author C. Scott Ananian
 * @version $Id: DanceState.java,v 1.3 2006-10-25 20:37:20 cananian Exp $
 */
public class DanceState {
    private final Program program;
    public DanceState(Program program) { this.program = program; }
    /**
     * @return Returns the dance program.
     */
    public Program getProgram() {
        return program;
    }
}
