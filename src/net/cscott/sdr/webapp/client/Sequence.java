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
@SuppressWarnings("serial")
public class Sequence implements Serializable {
    /** The dance program our calls are supposed to belong to. */
    public Program program = Program.PLUS;
    /** Starting formation for the dance. */
    public StartingFormationType startingFormation =
        StartingFormationType.SQUARED_SET;
    /** A list of calls, as the user entered them. */
    public List<String> calls = new ArrayList<String>();

    /** Types of starting formations supported. */
    public enum StartingFormationType {
        TWO_COUPLE("Two couple"),
        SQUARED_SET("Four couple"),
        //VIC_HEX("Six couple"),
        // square transformations:
        BIGON("Bigon"),
        HEXAGON("Hexagon"),
        OCTAGON("Octagon");
        /** Human-friendly name for this starting formation/dance type. */
        public final String humanName;
        StartingFormationType(String humanName) {
            this.humanName = humanName;
        }
    }
    /** Games. */
    public enum GameType {
        NORMAL("Normal"),
        BARSTOOL("Barstool"),
        STABLE_BARSTOOL("Stable Barstool");
        /** Human-friendly name for this starting formation/dance type. */
        public final String humanName;
        GameType(String humanName) {
            this.humanName = humanName;
        }
    }

    public Sequence() {
        // create sequence with default settings
    }
    /** Copy everything from the given sequence. */
    public void copyFrom(Sequence s) {
        this.program = s.program;
        this.startingFormation = s.startingFormation;
        this.calls = s.calls;
    }
}
