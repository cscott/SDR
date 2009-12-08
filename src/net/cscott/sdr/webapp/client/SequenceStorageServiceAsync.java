package net.cscott.sdr.webapp.client;

import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SequenceStorageServiceAsync {
    public void save(SequenceInfo info, Sequence sequence,
                     AsyncCallback<Long> async);
    public void load(Long id, AsyncCallback<Sequence> async);
    public void delete(Long id, AsyncCallback<Void> async);
    public void list(Date earlierThan, int maxItems,
            List<String> requiredTags, AsyncCallback<List<SequenceInfo>> async);
    public void logout(AsyncCallback<String> async);
}
