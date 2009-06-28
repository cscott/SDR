package net.cscott.sdr.webapp.client;

import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("storage")
public interface SequenceStorageService extends RemoteService {
    public Long save(SequenceInfo info, Sequence sequence)
        throws NotLoggedInException;
    public Sequence load(Long id)
        throws NotLoggedInException;
    /** Return a list of sequences saved by this user.  If {@code earlierThan}
     * is not null, then only sequences strictly older than the given Date
     * are returned.
     */
    public List<SequenceInfo> list(Date earlierThan, int maxItems,
                                   List<String> requiredTags)
        throws NotLoggedInException;
}
