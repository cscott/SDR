package net.cscott.sdr.webapp.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import net.cscott.sdr.calls.Program;

/**
 * A sequence being displayed/edited in the UI.  This can be loaded/saved and
 * is what is passed to the server for rendering into dancer paths &mdash;
 * see {@link EngineResults}.
 *
 * @author C. Scott Ananian
 */
@SuppressWarnings("serial")
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Sequence implements Serializable {
    /** Primary key, non-null for sequences already saved */
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    @Extension(vendorName="datanucleus", key="gae.encoded-pk", value="true")
    public transient String key;

    /** The dance program our calls are supposed to belong to. */
    @Persistent(serialized="true")
    public Program program = Program.PLUS;
    /** Starting formation for the dance. */
    @Persistent(serialized="true")
    public StartingFormationType startingFormation =
        StartingFormationType.SQUARED_SET;
    /** A list of calls, as the user entered them. */
    @Persistent
    public List<String> calls = new ArrayList<String>();

    /** Types of starting formations supported. */
    public enum StartingFormationType {
        TWO_COUPLE("Two couple"), SQUARED_SET("Four couple"),
        // future fun:
        BIGON("Bigon"), HEXAGON("Hexagon"), OCTAGON("Octagon");
        public final String humanName;
        StartingFormationType(String humanName) { this.humanName = humanName; }
    }

    public Sequence() {
        // create sequence with default settings
    }
    /** Keep the given key, but copy every thing else from the given sequence.*/
    public void copyFrom(Sequence s) {
        this.program = s.program;
        this.startingFormation = s.startingFormation;
        this.calls = s.calls;
    }
}
