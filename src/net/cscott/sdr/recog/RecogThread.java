package net.cscott.sdr.recog;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import net.cscott.sdr.CommandInput;
import net.cscott.sdr.CommandInput.InputMode;
import net.cscott.sdr.CommandInput.PossibleCommand;
import edu.cmu.sphinx.decoder.search.Token;
import edu.cmu.sphinx.frontend.Data;
import edu.cmu.sphinx.frontend.FloatData;
import edu.cmu.sphinx.frontend.util.Microphone;
import edu.cmu.sphinx.jsgf.JSGFGrammar;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.ConfigurationManager;
import edu.cmu.sphinx.util.props.PropertyException;

/**
 * Use the Sphinx-4 speech recognition engine to perform speech input
 * for SDR.  We use the Sphinx-4 endpointer,
 * which automatically segments incoming audio into utterances and silences.
 */
// XXX we'll want to provide some way to configure the microphone.
public class RecogThread extends Thread {
    private final CommandInput input;
    private final BlockingQueue<LevelMonitor> rendezvous;

    public RecogThread(CommandInput input,
                       BlockingQueue<LevelMonitor> rendezvous) {
        this.input = input;
        this.rendezvous = rendezvous;
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
        rendezvous.offer((LevelMonitor)cm.lookup("levelMonitor"));

        /* get the JSGF grammar component */
        JSGFGrammar jsgfGrammar =
            (JSGFGrammar) cm.lookup("jsgfGrammar");
        //jsgfGrammar.dumpRandomSentences(10);
        
        if (!microphone.startRecording()) {
            recognizer.deallocate();
            throw new RuntimeException("Can't start microphone");
        }
        /* the microphone will keep recording until the thread exits */
        while (true) {
            /* Check for a new input mode, and change grammars if necessary. */
            InputMode mode = input.getMode();
            if (mode!=null) {
                String grmName;
                if (mode.mainMenu())
                    grmName = "menu";
                else
                    grmName = mode.danceProgram().getProgram().toTitleCase();
                jsgfGrammar.loadJSGF(grmName);
            }
            /*
             * This method will return when the end of speech
             * is reached. Note that the endpointer will determine
             * the end of speech.
             */ 
            Result result = recognizer.recognize();
            if (result==null) {
                // XXX: HUD: "I couldn't hear you"
                input.addCommand(errorCmd());
                continue;
            }
            
            /* Get all final result tokens. */
            List<Token> tokens = new ArrayList<Token>();
            for (Object t : result.getResultTokens())
                tokens.add((Token)t); // typecast; sphinx has a loose type
            if (tokens.isEmpty()) {
                // XXX: HUD: "I couldn't hear you"
                input.addCommand(errorCmd());
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
                pc = spokenCmd(resultText, startTime, endTime, pc);
            }
            input.addCommand(pc);
            System.err.println("---");
        }
    }
    private static PossibleCommand errorCmd() {
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
        };
    }
    /** Create a PossibleCommand from an unparsed user input, along with the
     * 'next worst' PossibleCommand.  Does the parsing lazily, so that we
     * don't parse unless the "better" PossibleCommands don't work out. */
    private static PossibleCommand spokenCmd(final String userInput,
                                             final long startTime,
                                             final long endTime,
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
        };
    }
}