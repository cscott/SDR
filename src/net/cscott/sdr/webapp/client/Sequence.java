package net.cscott.sdr.webapp.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.cscott.sdr.calls.Program;

/**
 * A sequence being displayed/edited in the UI.  This can be loaded/saved and
 * is what is passed to the server for rendering into dancer paths &mdash;
 * see {@link EngineResults}.
 *
 * @author C. Scott Ananian
 */
public class Sequence implements Serializable {
    /** The dance program our calls are supposed to belong to. */
    //public Program program = Program.PLUS;
    /** Starting formation for the dance. */
    public StartingFormationType startingFormation =
        StartingFormationType.SQUARED_SET;
    /** A list of calls, as the user entered them. */
    public List<String> calls = new ArrayList<String>();

    /** Types of starting formations supported. */
    public enum StartingFormationType {
        TWO_COUPLE, BIGON, SQUARED_SET, HEXAGON, OCTAGON
    }

    public Sequence() {
        // create sequence with default settings
    }
}
