package net.cscott.sdr.sound;

import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;

/** Create a jME timer object using timing info from MIDI playback. */
public class MidiTimer extends com.jme.util.Timer {
    private final Sequencer sequencer;
    public MidiTimer(Sequencer sequencer) {
	this.sequencer = sequencer;
    }
    public float getFrameRate() {
	return 1f/getTimePerFrame();
    }
    private float getResolutionFloat() {
	// get microseconds per quarter note
	float MPQ = 
	    sequencer.getTempoInMPQ() / sequencer.getTempoFactor();
	// now divide this by the number of ticks per quarter note
	Sequence seq = sequencer.getSequence();
	assert seq.getDivisionType() == seq.PPQ :
	    "Don't know how to deal with non-PPQ midi files";
	float MPT = MPQ / seq.getResolution(); // uS per tick
	// the answer we want is the reciprocal
	return 1000000f / MPT;
    }
    public long getResolution() {
	return Math.round(getResolutionFloat());
    }
    private long lastTime = 0;
    private long offset = 0;
    private long loopEnd = 0;
    private boolean first = true;
    public synchronized long getTime() {
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
    public float getTimePerFrame() {
	return frameTime / getResolutionFloat();
    }
    public void reset() {
	sequencer.setTickPosition(0);
    }
    private long lastFrame = 0;
    private long frameTime = 768/30; // 30 fps initially
    public synchronized void update() {
	long thisFrame = getTime();
	this.frameTime = thisFrame - lastFrame;
	this.lastFrame = thisFrame;
    }
    private long getLoopEnd() {
	long end = sequencer.getLoopEndPoint();
	if (end!=-1) return end;
	return sequencer.getTickLength();
    }
}
