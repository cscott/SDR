package net.cscott.sdr.sound;
/*
 *	Derived from LoopingMidiPlayer15.java, from jsresources.org
 *      Requires JDK1.5 or later.
 */

/* for our test files, tempo is 120bpm, and resolution is 384 PPQ.  This
 * means that a midi tick is 1/768s, which is more than adequate resolution
 * for (say) 15-30fps animation. */

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Transmitter;

import net.cscott.sdr.BeatTimer;
import net.cscott.sdr.anim.SilentBeatTimer;

public class MidiThread extends Thread
{
    private final BlockingQueue<BeatTimer> rendezvousBT;
    public MidiThread(BlockingQueue<BeatTimer> rendezvousBT) {
        this.rendezvousBT = rendezvousBT;
    }
    @Override
    public void run() {
        BeatTimer beatTimer;
        try {
            beatTimer = playMidi();
        } catch (Exception e) {
            // no music. =(
            beatTimer = new SilentBeatTimer();
        }
        // send the beat timer to the rest of the system.
        rendezvousBT.add(beatTimer);
    }
    public BeatTimer playMidi() 
    throws MidiUnavailableException, InvalidMidiDataException, IOException
    {
        final Sequencer sequencer;
        final Synthesizer synthesizer;
        
        /* Use high-quality soundbank. */
        Soundbank soundbank = MidiSystem.getSoundbank
        (MidiThread.class.getClassLoader().getResource
                ("net/cscott/sdr/sound/soundbank-deluxe.gm"));
        soundbank=null;
        
        /* We read in the MIDI file to a Sequence object.  This object
         * is set at the Sequencer later.
         */
        Sequence sequence = MidiSystem.getSequence
        (MidiThread.class.getClassLoader().getResource
                ("net/cscott/sdr/sound/saturday-night.midi"));
        
        // print out some info about timing resolution.
        System.out.println("Division type: "+sequence.getDivisionType());
        System.out.println("Resolution: "+sequence.getResolution());
        assert sequence.getDivisionType()==Sequence.PPQ :
            "don't know how to sync non-PPQ tracks";
        @SuppressWarnings("unused") int ticksPerBeat = sequence.getResolution();
        
        /* Now, we need a Sequencer to play the sequence.  Here, we
         * simply request the default sequencer without an implicitly
         * connected synthesizer
         */
        sequencer = MidiSystem.getSequencer(false);
        
        /* The Sequencer is still a dead object.  We have to open() it
         * to become live.  This is necessary to allocate some
         * ressources in the native part.
         */
        sequencer.open();
        
        /* Next step is to tell the Sequencer which Sequence it has to
         * play. In this case, we set it as the Sequence object
         * created above.
         */
        sequencer.setSequence(sequence);
        // XXX: at this point, it would be safe to create/return the BeatTimer

        // Mississippi Sawyer is *half note*=120; Java doesn't seem to grok
        // this.  Something like the following is needed for these cases:
        // sequencer.setTempoFactor(2f);
        
        /* We try to get the default synthesizer, open() it and chain
         * it to the sequencer with a Transmitter-Receiver pair.
         */
        synthesizer = MidiSystem.getSynthesizer();
        synthesizer.open();
        boolean loaded = false;
        if (soundbank!=null)
            loaded = synthesizer.loadAllInstruments(soundbank);
        System.out.println("Instruments "+(loaded?"":"not ")+"loaded.");
        Receiver	synthReceiver = synthesizer.getReceiver();
        Transmitter	seqTransmitter = sequencer.getTransmitter();
        seqTransmitter.setReceiver(synthReceiver);
        
        /* To free system resources, it is recommended to close the
         * synthesizer and sequencer properly.
         *
         * To accomplish this, we register a Listener to the
         * Sequencer. It is called when there are "meta" events. Meta
         * event 47 is end of track.
         *
         * Thanks to Espen Riskedal for finding this trick.
         */
        sequencer.addMetaEventListener(new MetaEventListener()
                {
            public void meta(MetaMessage event)
            {
                if (event.getType() == 47)
                {
                    sequencer.close();
                    if (synthesizer != null)
                    {
                        synthesizer.close();
                    }
                    //System.exit(0);
                }
            }
                });
        
        /* Here, we set the loop points to loop over the whole
         * sequence. Setting the loop end point to -1 means using the
         * last tick of the sequence as end point of the loop.
         *
         * Furthermore, we set the number of loops to loop infinitely.
         */
        sequencer.setLoopStartPoint(0);
        sequencer.setLoopEndPoint(-1);
        sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
        
        /* WORKAROUND for BUG in jdk 1.6 (at least): loop control has an
         * off-by-one error and doesn't play the (usually note off) messages
         * in the last tick of the song; it just jumps to tick 0 and plays
         * the note-on messages there.  Workaround this by manually kludging
         * in ALL_NOTE_OFF messages on all channels one tick before the end
         * of the song.  Sigh. */
        for (int i=0; i<16; i++) {
            javax.sound.midi.ShortMessage notesOff =
                new javax.sound.midi.ShortMessage();
            notesOff.setMessage(ShortMessage.CONTROL_CHANGE+i,123,0);
            for (javax.sound.midi.Track t : sequence.getTracks()) {
                long end = (t.ticks()<=0)?0L:(t.ticks()-1);
                t.add(new javax.sound.midi.MidiEvent(notesOff, end));
            }
        }
        
        /* Ok, play the music!
         */
        sequencer.start();
        
        return new MidiTimer(sequencer);
    }
}
