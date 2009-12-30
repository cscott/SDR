package net.cscott.sdr.recog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

import net.cscott.jutil.UniqueVector;
import net.cscott.sdr.util.ListUtils;
import edu.cmu.sphinx.frontend.BaseDataProcessor;
import edu.cmu.sphinx.frontend.Data;
import edu.cmu.sphinx.frontend.DataEndSignal;
import edu.cmu.sphinx.frontend.DataProcessingException;
import edu.cmu.sphinx.frontend.DataStartSignal;
import edu.cmu.sphinx.frontend.DoubleData;
import edu.cmu.sphinx.frontend.util.DataUtil;

/** {@link Microphone} allows on-the-fly selection of different Sphinx input
 *  sources.  We hardcode some of the adjustable properties of
 *  {@link edu.cmu.sphinx.frontend.util.Microphone}, but provide on-the-fly
 *  adjustability.
 */
public class Microphone extends BaseDataProcessor {
    private BlockingQueue<Data> audioQueue;
    private BlockingQueue<NameAndLine> switchQueue;
    /** Desired audio format. */
    private static final AudioFormat desiredFormat = new AudioFormat
        (16000/*Hz*/, 16/*bits*/, 1/*mono*/,
         true/*signed*/, true/*big endian*/);
    /** Buffer size, in bytes. */
    private static final int AUDIO_BUFFER_SIZE = 160000;
    /** Frame size: 10ms at our sample rate. */
    private static final int AUDIO_FRAME_SIZE =
        (desiredFormat.getSampleSizeInBits() / 8) *
        (int) (10 * desiredFormat.getSampleRate() / 1000);
    /** Should we emit a DataEndSignal when switching microphones. */
    private static final boolean END_WHEN_SWITCHING = false;

    public Microphone() {
        this.audioQueue = new ArrayBlockingQueue<Data>(4096);
        this.switchQueue = new LinkedBlockingQueue<NameAndLine>();
    }
    @Override
    public void initialize() {
        super.initialize();
        // start up the audio thread.
        new AudioThread().start();
    }
    /** Look for all mixers providing our desired input format. */
    public List<NameAndLine> availableMixers() {
        List<NameAndLine> result = new ArrayList<NameAndLine>();
        DataLine.Info info = new DataLine.Info
            (TargetDataLine.class, desiredFormat);
        // enumerate the mixers which can support this input format.
        Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
        for (int i=0; i<mixerInfo.length; i++) {
            Mixer m = AudioSystem.getMixer(mixerInfo[i]);
            // try no-conversion-required line.
            try {
                TargetDataLine line = (TargetDataLine) m.getLine(info);
                String name = nameFromInfo(m.getMixerInfo());
                result.add(new NameAndLine(name, line));
                continue;
            } catch (LineUnavailableException e) { // ignore
            } catch (IllegalArgumentException e) { // ignore
            }
            // XXX: try line-requiring-conversion
        }
        return result;
    }
    /** Switch the mixer in use by the audio thread. */
    public void switchMixer(NameAndLine nal) {
        switchQueue.add(nal);
    }

    public static class NameAndLine {
        public final String name;
        public final TargetDataLine line;
        NameAndLine(String name, TargetDataLine line) {
            this.name = name;
            this.line = line;
        }
        @Override
        public String toString() { return name; }
    }
    private static String nameFromInfo(Mixer.Info info) {
        String desc = info.getDescription();
        // remove leading "Direct Audio Device: " if present.
        desc = desc.replaceAll("^Direct Audio Device: ", "");
        // split at commas, keep only the unique items
        UniqueVector<String> uniq =
            new UniqueVector<String>(Arrays.asList(desc.split("\\s*,\\s*")));
        return ListUtils.join(uniq, ", ");
    }

    @Override
    public Data getData() throws DataProcessingException {
        getTimer().start();
        try {
            return audioQueue.take();
        } catch (InterruptedException ie) {
            throw new DataProcessingException("cannot take Data from audioList", ie);
        } finally {
            getTimer().stop();
        }
    }
    private class AudioThread extends Thread {
        boolean first = true;
        long totalSamplesRead = 0;
        AudioThread() { setDaemon(true); }
        @Override
        public void run() {
            while (true) {
                NameAndLine nal;
                // wait for a new line to be suggested.
                while (true) {
                    try {
                        nal = switchQueue.take();
                        break;
                    } catch (InterruptedException e) { /* hmm, wait some more */ }
                }
                // read some from that line, returning when there's a new one
                try {
                    openAndRead(nal);
                } catch (LineUnavailableException e) {
                    // bail, wait for new line to be selected.
                    System.err.println("Can't open "+nal.name+": "+e);
                }
            }
        }
        void openAndRead(NameAndLine nal) throws LineUnavailableException {
            if (!nal.line.isOpen()) {
                nal.line.open(desiredFormat, AUDIO_BUFFER_SIZE);
            }
            if (first || END_WHEN_SWITCHING) {
                audioQueue.add(new DataStartSignal((int) desiredFormat.getSampleRate()));
                first = false;
            }
            totalSamplesRead = 0;
            try {
                nal.line.start();
                while (switchQueue.isEmpty()) {
                    audioQueue.add(readData(nal.line));
                }
            } catch (IOException ioe) {
                // halt data collection, wait for new device to be selected.
                System.err.println("Exception reading from "+nal.name+": "+ioe);
            }
            long duration = (long)
                   (((double) totalSamplesRead /
                    (double) desiredFormat.getSampleRate()) * 1000.0);
            if (END_WHEN_SWITCHING)
                audioQueue.add(new DataEndSignal(duration));
            // now stop our streams.
            nal.line.stop();
            nal.line.flush();
            nal.line.close();
        }
        Data readData(TargetDataLine audioStream) throws IOException {
            long collectTime = System.currentTimeMillis();
            long firstSampleNumber = totalSamplesRead;

            int numBytesRead = audioStream.read(frame, 0, frame.length);
            if (numBytesRead <= 0)
                throw new IOException("No more data");
            int sampleSizeInBytes =
                    audioStream.getFormat().getSampleSizeInBits() / 8;
            totalSamplesRead += (numBytesRead / sampleSizeInBytes);

            if (numBytesRead != frame.length) {
                if (numBytesRead % sampleSizeInBytes != 0) {
                    throw new Error("Incomplete sample read.");
                }
            }
            double[] samples = DataUtil.bytesToValues
                (frame, 0, numBytesRead, sampleSizeInBytes, true/*signed*/);

            return (new DoubleData
                    (samples, (int) audioStream.getFormat().getSampleRate(),
                            collectTime, firstSampleNumber));
        }
        private final byte[] frame = new byte[AUDIO_FRAME_SIZE];
    }
}
