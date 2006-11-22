package net.cscott.sdr.sound;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sound.*;
import javax.sound.sampled.*;

public class JavaSoundTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // 16kHz 16-bit mono signed big-endian
        AudioFormat format = new AudioFormat(16000,16,1,true,true);
        Line.Info template = new DataLine.Info(TargetDataLine.class, format);
        for (Line.Info li : AudioSystem.getTargetLineInfo(template)) {
            System.out.println(li);
        }
        System.out.println("------");
        for (Mixer.Info mixInfo : AudioSystem.getMixerInfo()) {
            System.out.println(mixInfo.getName()+"; "+mixInfo.getDescription()+" [vendor: "+mixInfo.getVendor()+", version "+mixInfo.getVersion()+"] "+mixInfo.getClass());
            Mixer mixer = AudioSystem.getMixer(mixInfo);
            System.out.println(" SOURCES:");
            int i=0;
            for (Line.Info lInfo : mixer.getSourceLineInfo()) {
                System.out.println("  "+lInfo+" ("+lInfo.getClass()+")");
                if (lInfo instanceof Port.Info) {
                    Port.Info pInfo = (Port.Info) lInfo;
                    System.out.println("   NAME:"+pInfo.getName());
                }
            }
            System.out.println(" TARGETS:");
            for (Line.Info lInfo : mixer.getTargetLineInfo()) {
                System.out.println("  "+lInfo+" ("+lInfo.getClass()+")");
                if (lInfo instanceof DataLine.Info) {
                    DataLine.Info dli = (DataLine.Info) lInfo;
                    System.out.println(Arrays.asList(dli.getFormats()));
                }
            }
        }
        System.out.println("------");
        for (Mixer.Info mi : AudioSystem.getMixerInfo()) {
            processMixer(AudioSystem.getMixer(mi));
        }
    }
    static void processMixer(Mixer m) {
        // we're just looking for PortMixers, so throw away if any of the
        // sources or targets are not Ports.
        for (Line.Info li : m.getSourceLineInfo())
            if (!(li instanceof Port.Info)) return;
        for (Line.Info li : m.getTargetLineInfo())
            if (!(li instanceof Port.Info)) return;
        System.out.println("--- "+m.getMixerInfo()+" ---");
        // okay, these are all ports.  Let's look at controls.
        String s = "SOURCE";
        for (Line.Info[] lis : new Line.Info[][] { m.getSourceLineInfo(), m.getTargetLineInfo() }) {
            System.out.println(s); s = "TARGET";
            for (Line.Info li : lis) {
                Port.Info pi = (Port.Info) li;
                try {
                    Line l = m.getLine(pi);
                    System.out.println(" "+pi.getName());
                    l.open();
                    List<Control> lc = new ArrayList<Control>();
                    for (Control ctrl : l.getControls()) {
                        if (ctrl instanceof CompoundControl) {
                            lc.addAll(Arrays.asList(((CompoundControl)ctrl).getMemberControls()));
                        } else
                            lc.add(ctrl);
                    }
                    System.out.println("  "+lc);
                    l.close();
                } catch (LineUnavailableException e) {
                    // ignore
                }
            }
        }
    }
    // for SourceLine ports, we can deal with Volume, Balance, and Select controls
    
    
    static class HwMixedInput {
        HwMixedInput(Mixer digital, Mixer portMixer, Port which) {
            
        }
        Line open() {
            return null;
        }
        void close() {
        }
    }
    static class DedicatedInput {
        DedicatedInput(Mixer digital, Port which) {
            
        }
    }

    // OK, conclusions:
    // we just look for source ports with a 'volume'; possibly a 'select'.
    // current hardware we get three: Mic, Modem Speaker, and Capture
    // doesn't look like enough functionality is exposed to determine where the
    // 'capture' input comes from (although I suspect that the 'select' control
    // on the capture input corresponds to the checkbox for "line in" in the
    // ALSA mixer)
    // try to find the Direct Audio Mixer corresponding to the Port.
    // this is easy for the USB mic; I think capture corresponds to
    // plughw:0,0 for the built-in audio, although we also have
    // "MIC ADC" and "MIC2 ADC" and "ADC2" -- no idea what these
    // do. =(
}
