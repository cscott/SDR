package net.cscott.sdr.sound;

import javax.sound.midi.Sequencer;

import net.cscott.sdr.BeatTimer;
import net.cscott.sdr.util.Fraction;

/** Create a jME timer object using timing info from MIDI playback. */
public class MidiTimer implements BeatTimer {
    private final Sequencer sequencer;
    public MidiTimer(Sequencer sequencer) {
	this.sequencer = sequencer;
    }
    private long lastTime = 0;
    private long offset = 0;
    private long loopEnd = 0;
    private boolean first = true;
    public Fraction getCurrentBeat() {
        // a quarter note is a beat, so this is getTicks()/getResolution()
	return Fraction.valueOf((int)getTicks(), getResolution());
    }
    private synchronized long getTicks() {
        // check for roll-over.
        if (first) { loopEnd = getLoopEnd(); first=false; }
        long now = sequencer.getTickPosition() + offset;
        if (now < lastTime) { // we've rolled over.
            now += loopEnd;
            offset += loopEnd;
            loopEnd = getLoopEnd(); // new loop end?
        }
        lastTime = now;
        return now;
    }
    public void reset() {
	sequencer.setTickPosition(0);
    }
    private long getLoopEnd() {
	long end = sequencer.getLoopEndPoint();
	if (end!=-1) return end;
	return sequencer.getTickLength();
    }
    private int resolution = 0;
    public int getResolution() {
        if (resolution==0) {
            resolution = sequencer.getSequence().getResolution();
        }
        return resolution;
    }
}