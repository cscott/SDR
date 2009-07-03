package net.cscott.sdr.webapp.server;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import net.cscott.sdr.webapp.client.Sequence;
import net.cscott.sdr.webapp.client.SequenceInfo;

import com.google.appengine.api.users.User;

@SuppressWarnings("serial")
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class SequenceInfoJDO implements Serializable {
    /** Primary key, non-null for sequences already saved. */
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    public Long id;
    /** Owner of the sequence. */
    @Persistent
    public User user;
    /** The rest of the information about the sequence. */
    @Persistent
    public String title;
    /** Tags for the sequence */
    @Persistent
    public List<String> tags;
    @Persistent
    public Date lastModified;
    /** Rest of the information */
    @Persistent(serialized="true")
    public SequenceInfo info;
    /** The actual sequence (a separate entity in the datastore) */
    public Sequence sequence;

    public SequenceInfoJDO(User user, SequenceInfo info, Sequence sequence) {
        this.user = user;
        this.id = info.id;
        this.updateInfo(info);
        this.updateSequence(sequence);
        this.updateModified();
    }
    void updateInfo(SequenceInfo info) {
        this.info = info;
        this.title = info.title;
        this.tags = info.tags;
    }
    void updateSequence(Sequence sequence) {
        if (this.sequence!=null && this.sequence.key!=null)
            sequence.key = this.sequence.key;
        this.sequence = sequence;
    }
    void updateModified() {
        this.lastModified = new Date();
    }
}
