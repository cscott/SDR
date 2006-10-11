package net.cscott.sdr.calls;

/** {@link DanceState} captures all the static information about a dance
 * which a {@link Predicate} might need to know.  An example might be
 * the current dance program.
 * @author C. Scott Ananian
 * @version $Id: DanceState.java,v 1.2 2006-10-11 18:50:35 cananian Exp $
 */
public class DanceState {
    public final Program program;
    public DanceState(Program program) { this.program = program; }
}
