package net.cscott.sdr.webapp.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("dance")
public interface DanceEngineService extends RemoteService {
    /**
     * Attempt to dance the given sequence, and return the dancer paths
     * which result.  The returned EngineResults will have a sequenceNumber
     * matching the one provided.
     */
    public EngineResults dance(Sequence s, int sequenceNumber);

    // eventually there may be other dance-engine tasks, like 'resolve'
    // or 'classify formation' (at home, allemande left, dixie grand, etc)
}
