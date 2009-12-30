package net.cscott.sdr.recog;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import edu.cmu.sphinx.frontend.*;

/**
 * {@link LevelMonitor} tracks the raw data coming from the microphone
 * and keeps a record of the maximum level of each data packet.  This
 * is used by the graphical front end to display current input levels.
 * @author C. Scott Ananian
 */
public class LevelMonitor extends BaseDataProcessor {
    /** A volume measurement of an audio frame. */
    public static class LevelMeasurement {
        /** Time, in milliseconds since midnight, January 1, 1970, when
         *  the data for this level measurement was taken. */
        public final long when;
        /** Level at that time; max is 1.0 */
        public final double level;
        LevelMeasurement(long when, double level) {
            this.when = when;
            this.level = level;
        }
    }
    private final BlockingQueue<LevelMeasurement> levels =
        new LinkedBlockingQueue<LevelMeasurement>();
    private void addData(DoubleData dd) {
        long when = dd.getCollectTime();
        double[] data = dd.getValues();
        // find max absolute value of (signed) signal.
        double max = 0;
        for (double sample: data) {
            double abs = Math.abs(sample);
            if (abs > max) max = abs;
        }
        // note that data is by default signed, but we don't know what
        // the max value is... unless we can get access to the Microphone
        // assume 16 bits per sample, so max value is 32768/-32767
        // scale so that max is one.
        max = max / 32768;
        // add this to our list.
        levels.add(new LevelMeasurement(when, max));
        // don't grow w/o bound
        if (levels.size() > 4096)
            levels.poll(); // throw oldest measurement away
    }
    /** Add all the level measurements taken since the last time this
     * method was called (up to a maximum of 4096 measurements) to the
     * given list. This method is thread-safe. */
    public void getLevels(List<LevelMeasurement> l) {
        levels.drainTo(l);
    }
    
    /* (non-Javadoc)
     * @see edu.cmu.sphinx.frontend.BaseDataProcessor#getData()
     */
    @Override
    public Data getData() throws DataProcessingException {
        Data audio = getPredecessor().getData();
        if (audio!=null && audio instanceof DoubleData) {
            DoubleData dd = (DoubleData) audio;
            addData(dd);
        }
        return audio;
    }
}
