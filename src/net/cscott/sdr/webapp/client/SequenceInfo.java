package net.cscott.sdr.webapp.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Meta information about a saved sequence.  Title and tags.
 * @author C. Scott Ananian
 */
@SuppressWarnings("serial")
public class SequenceInfo implements Serializable {
    /** Title of the sequence. */
    public String title;
    /** Tags manually applied to the sequence. */
    public List<String> manualTags;
    /** Tags automatically applied to the sequence, based on sequence program,
     *  resolution status, etc. */
    public List<String> automaticTags;

    public SequenceInfo(String title, String... tags) {
        this.title = title;
        this.manualTags = new ArrayList<String>(Arrays.asList(tags));
        this.automaticTags = new ArrayList<String>();
    }
}
