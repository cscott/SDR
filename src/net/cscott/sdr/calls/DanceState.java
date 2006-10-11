package net.cscott.sdr.calls;

/** {@link DanceState} captures all the static information about a dance.
 * For example: the current dance program.
 * @author C. Scott Ananian
 * @version $Id: DanceState.java,v 1.1 2006-10-11 18:48:50 cananian Exp $
 */
public class DanceState {
    public final Program program;
    public DanceState(Program program) { this.program = program; }
}
