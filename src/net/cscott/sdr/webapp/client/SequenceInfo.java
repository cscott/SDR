package net.cscott.sdr.webapp.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Meta information about a saved sequence.  Title and tags.
 * @author C. Scott Ananian
 */
@SuppressWarnings("serial")
public class SequenceInfo implements Serializable {
    /** String used as the title of new sequences. */
    public static final String UNTITLED = "Untitled";
    /** Key of this sequence in the {@link SequenceStore}, or null if the
     *  sequence has never been saved. */
    public Long id;
    /** When this sequence was last saved, or null if it never has been. */
    public Date lastModified;
    /** Title of the sequence. */
    public String title;
    /** Manually-applied tags for the sequence.  Not indexed. */
    public List<String> manualTags;
    /** All tags for the sequence.  This includes manually-applied tags, as
     * well as automatically-applied tags based on sequence program,
     *  resolution status, etc. */
    public List<String> tags;

    public SequenceInfo(String title, String... manualTags) {
        this.id = null; // not yet saved
        this.title = title;
        this.manualTags = new ArrayList<String>(Arrays.asList(manualTags));
        this.tags = new ArrayList<String>(this.manualTags);
    }
    // no-arg constructor for GWT serializability
    SequenceInfo() {
        this(UNTITLED);
    }
}
