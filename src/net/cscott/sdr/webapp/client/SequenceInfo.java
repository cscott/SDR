package net.cscott.sdr.webapp.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.cscott.sdr.webapp.server.SequenceInfoJDO;

/**
 * Meta information about a saved sequence.  Title and tags.
 * @author C. Scott Ananian
 */
@SuppressWarnings("serial")
public class SequenceInfo implements Serializable {
    /** Primary key of {@link SequenceInfoJDO} in database, or null if sequence
     *  has never been saved. */
    public Long id;
    /** Title of the sequence. */
    public String title;
    /** Manually-applied tags for the sequence.  Not indexed. */
    public List<String> manualTags;
    /** All tags for the sequence.  This includes manually-applied tags, as
     * well as automatically-applied tags based on sequence program,
     *  resolution status, etc.  Indexed. */
    public List<String> tags;

    public SequenceInfo(String title, String... manualTags) {
        this.id = null; // not yet saved
        this.title = title;
        this.manualTags = new ArrayList<String>(Arrays.asList(manualTags));
        this.tags = new ArrayList<String>(this.manualTags);
    }
    // no-arg constructor for GWT serializability
    SequenceInfo() {
        this("Untitled");
    }
}
