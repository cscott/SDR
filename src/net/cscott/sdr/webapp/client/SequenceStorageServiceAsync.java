package net.cscott.sdr.webapp.client;

import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SequenceStorageServiceAsync {
    public void save(SequenceInfo info, Sequence sequence,
                     AsyncCallback<Long> async);
    public void load(Long info, AsyncCallback<Sequence> async);
    public void list(Date earlierThan, int maxItems,
            List<String> requiredTags, AsyncCallback<List<SequenceInfo>> async);
}
