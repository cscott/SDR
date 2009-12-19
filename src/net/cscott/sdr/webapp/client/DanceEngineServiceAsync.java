package net.cscott.sdr.webapp.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface DanceEngineServiceAsync {
    /**
     * Attempt to dance the given sequence, and return the dancer paths
     * which result.  The returned EngineResults will have a sequenceNumber
     * matching the one provided.
     */
    public void dance(Sequence s, int sequenceNumber,
                      AsyncCallback<EngineResults> async);
    /**
     * Reload the dance engine's call definitions.  Used for debugging after
     * local edits to the call definitions have been made.
     */
    public void reloadDB(AsyncCallback<Void> async);

    // eventually there may be other dance-engine tasks, like 'resolve'
    // or 'classify formation' (at home, allemande left, dixie grand, etc)
}
