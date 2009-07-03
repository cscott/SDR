package net.cscott.sdr.webapp.client;

import java.util.Collections;
import java.util.List;

/**
 * This class encapsulates the returned data from the server, when given
 * a {@link Sequence}.
 * @author C. Scott Ananian
 */
@SuppressWarnings("serial")
public class EngineResults implements java.io.Serializable {
    /** The sequence number helps pair up responses with requests. */
    public int sequenceNumber;
    /** The index of the first invalid call in the {@link Sequence}'s call
     *  list: this is {@code Sequence.calls.size()} if all calls are valid. */
    public int firstInvalidCall;
    /** Error or warning messages for each call; may contains nulls if there
     *  are no errors or warnings.  Size should match list of calls in
     *  {@link Sequence}. */
    public List<String> messages;
    /** Movements for each {@link Dancer}, as an ordered list of
     *  {@link DancerPath}s. */
    //Map<Dancer,List<DancerPath>> movements;
    /** Duration of each valid call in the {@link Sequence}'s call list, in
     *  beats.  Length of list should match that of the call list; invalid
     *  calls should have duration 0. */
    public List<Double> timing;
    /** Total length of all valid calls in the {@link Sequence}'s call list.
     *  The sum of {@link #timing}; a convenience. */
    public double totalBeats;

    public EngineResults(int sequenceNumber, int firstInvalidCall,
                         List<String> messages, List<Double> timing,
                         double totalBeats) {
        this.sequenceNumber = sequenceNumber;
        this.firstInvalidCall = firstInvalidCall;
        this.messages = messages;
        this.timing = timing;
        this.totalBeats = totalBeats;
    }
    // needs no-arg constructor for GWT serializability
    public EngineResults() {
        this(0, 0, Collections.<String>emptyList(),
             Collections.<Double>emptyList(), 0);
    }
}