package net.cscott.sdr.recog;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import net.cscott.sdr.CommandInput;
import net.cscott.sdr.CommandInput.InputMode;
import net.cscott.sdr.CommandInput.PossibleCommand;
import net.cscott.sdr.calls.Program;
import edu.cmu.sphinx.decoder.search.Token;
import edu.cmu.sphinx.frontend.Data;
import edu.cmu.sphinx.frontend.FloatData;
import edu.cmu.sphinx.jsgf.JSGFGrammar;
import edu.cmu.sphinx.linguist.dflat.DynamicFlatLinguist;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.ConfigurationManager;
import edu.cmu.sphinx.util.props.PropertyException;

/**
 * Use the Sphinx-4 speech recognition engine to perform speech input
 * for SDR.  We use the Sphinx-4 endpointer,
 * which automatically segments incoming audio into utterances and silences.
 */
// XXX want to force an endpoint as soon as input.setMode() is called.
//     otherwise the first utterance will be parsed with the wrong grammar.
public class RecogThread extends Thread {
    private final CommandInput input;
    private final BlockingQueue<Control> rendezvous;
    private final BlockingQueue<InputMode> modeQueue =
        new LinkedBlockingQueue<InputMode>();

    public RecogThread(CommandInput input,
                       BlockingQueue<Control> rendezvous) {
        this.input = input;
        this.rendezvous = rendezvous;
        this.setDaemon(true);
    }
    public class Control {
        public final LevelMonitor levelMonitor;
        public final Microphone microphone;
        Control(LevelMonitor lm, Microphone m) {
            this.levelMonitor = lm;
            this.microphone = m;
        }
    }
    @Override
    public void run() {
        try {
            startSpeech();
        } catch (IOException e) {
            // TODO what do we do here?
            e.printStackTrace();
        } catch (PropertyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    private void startSpeech()
    throws IOException, PropertyException, InstantiationException {
        URL configUrl = RecogThread.class.getResource("sdr.config.xml");
        
        ConfigurationManager cm = new ConfigurationManager(configUrl);
        
        Recognizer recognizer = (Recognizer) cm.lookup("recognizer");
        Microphone microphone = (Microphone) cm.lookup("microphone");
        
        /* allocate the resources necessary for the recognizer */
        /* this also compiles the grammars, etc. */
        recognizer.allocate();
        
        // send the level monitor over on the rendezvous queue.
        LevelMonitor levelMonitor = (LevelMonitor) cm.lookup("levelMonitor");
        rendezvous.offer(new Control(levelMonitor, microphone));

        // get the SpeechInterrupter component.
        final SpeechInterrupter speechInterrupter =
            (SpeechInterrupter) cm.lookup("speechInterrupter");
        new Thread() {
            { setDaemon(true); }
            @Override
            public void run() {
                while (true) {
                    // block waiting for new input mode
                    InputMode mode = input.waitForMode();
                    // push it onto our non-blocking queue
                    modeQueue.add(mode);
                    // interrupt recognition in progress.
                    speechInterrupter.interrupt();
                }
            }
        }.start();

        /* get the JSGF grammar component */
        JSGFGrammar jsgfGrammar =
            (JSGFGrammar) cm.lookup("jsgfGrammar");
        //jsgfGrammar.dumpRandomSentences(10);
        
        /* the microphone will keep recording until the thread exits */
        InputMode mode = new InputMode() {
            @Override
            public boolean mainMenu() { return true; }
            @Override
            public Program program() { return null; }
        };
        while (true) {
            /* Check for a new input mode, and change grammars if necessary. */
            InputMode nMode = modeQueue.poll();
            if (nMode != null) {
                String grmName;
                if (nMode.mainMenu())
                    grmName = "menu";
                else
                    grmName = nMode.program().toTitleCase();
                System.err.println("SWITCHING GRAMMARS: "+grmName);
                jsgfGrammar.loadJSGF(grmName);
                // XXX work around bug in DynamicFlatLinguist
                ((DynamicFlatLinguist) cm.lookup("dflatLinguist")).allocate();
                mode = nMode;
            }
            /*
             * This method will return when the end of speech
             * is reached. Note that the endpointer will determine
             * the end of speech.
             */ 
            Result result = recognizer.recognize();
            if (result==null) {
                // XXX: HUD: "I couldn't hear you"
                input.addCommand(errorCmd(mode));
                continue;
            }
            
            /* Get all final result tokens. */
            List<Token> tokens = new ArrayList<Token>();
            for (Object t : result.getResultTokens())
                tokens.add((Token)t); // typecast; sphinx has a loose type
            if (tokens.isEmpty()) {
                // XXX: HUD: "I couldn't hear you"
                input.addCommand(errorCmd(mode));
                continue;
            }
            /* sort so the worst (lowest score) is first */
            Collections.sort(tokens, new Comparator<Token>() {
                public int compare(Token t1, Token t2) {
                    return Float.compare(t1.getScore(), t2.getScore());
                }
            });
            PossibleCommand pc = null;
            for (Token t : tokens) {
                // get the words in the result
                String resultText = t.getWordPathNoFiller();
                if (resultText.equals("<unk>"))
                    continue;
                System.err.println("HEARD: "+resultText);
                // find the start and end times for this result
                FloatData firstFeature = null, lastFeature = null;
                while (t!=null) {
                    Data feature = t.getData();
                    if (feature != null && feature instanceof FloatData) {
                        if (firstFeature==null) firstFeature=(FloatData)feature;
                        lastFeature=(FloatData)feature;
                    }
                    t = t.getPredecessor();
                }
                long startTime = firstFeature.getCollectTime();
                long endTime = lastFeature.getCollectTime();
                // note that we construct pc backwards from worst to best
                // so that the best ends up at the head.
                pc = spokenCmd(resultText, startTime, endTime, mode, pc);
            }
            if (pc==null) pc = errorCmd(mode);
            input.addCommand(pc);
            System.err.println("---");
        }
    }
    private static PossibleCommand errorCmd(final InputMode im) {
        final long time = new Date().getTime();
        return new PossibleCommand() {
            @Override
            public String getUserInput() { return UNCLEAR_UTTERANCE; }
            @Override
            public PossibleCommand next() { return null; }
            @Override
            public long getEndTime() { return time; }
            @Override
            public long getStartTime() { return time; }
            @Override
            public InputMode getMode() { return im; }
        };
    }
    /** Create a PossibleCommand from an unparsed user input, along with the
     * 'next worst' PossibleCommand.  Does the parsing lazily, so that we
     * don't parse unless the "better" PossibleCommands don't work out. */
    private static PossibleCommand spokenCmd(final String userInput,
                                             final long startTime,
                                             final long endTime,
                                             final InputMode mode,
                                             final PossibleCommand next) {
        return new PossibleCommand() {
            @Override
            public String getUserInput() { return userInput; }
            @Override
            public long getStartTime() { return startTime; }
            @Override
            public long getEndTime() { return endTime; }
            @Override
            public PossibleCommand next() { return next; }
            @Override
            public InputMode getMode() { return mode; }
        };
    }
}