package net.cscott.sdr.webapp.client;

import java.util.List;
import java.util.Map;

import net.cscott.sdr.calls.Dancer;
import net.cscott.sdr.calls.DancerPath;
import net.cscott.sdr.util.Fraction;

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
    //public List<Fraction> timing;
    /** Total length of all valid calls in the {@link Sequence}'s call list.
     *  The sum of {@link #timing}; a convenience. */
    //public Fraction totalBeats;
}
