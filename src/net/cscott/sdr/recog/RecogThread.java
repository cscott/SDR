package net.cscott.sdr.recog;

import edu.cmu.sphinx.frontend.util.Microphone;
import edu.cmu.sphinx.jsapi.JSGFGrammar;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.ConfigurationManager;
import edu.cmu.sphinx.util.props.PropertyException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.BlockingQueue;

import net.cscott.sdr.CommandInput;


/**
 * Use the Sphinx-4 speech recognition engine to perform speech input
 * for SDR.  We use the Sphinx-4 endpointer,
 * which automatically segments incoming audio into utterances and silences.
 */
public class RecogThread extends Thread {
    private final CommandInput input;
    private final BlockingQueue<LevelMonitor> rendezvous;

    public RecogThread(CommandInput input, BlockingQueue<LevelMonitor> rendezvous) {
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
    private void startSpeech() throws IOException, PropertyException, InstantiationException {
        URL configUrl = RecogThread.class.getResource("sdr.config.xml");
        
        ConfigurationManager cm = new ConfigurationManager(configUrl);
        
        Recognizer recognizer = (Recognizer) cm.lookup("recognizer");
        Microphone microphone = (Microphone) cm.lookup("microphone");
        // send the level monitor over on the rendezvous queue.
        rendezvous.offer((LevelMonitor)cm.lookup("levelMonitor"));
        
        /* allocate the resource necessary for the recognizer */
        recognizer.allocate();
        
        /* get the JSGF grammar component */
        JSGFGrammar jsgfGrammar =
            (JSGFGrammar) cm.lookup("jsgfGrammar");
        jsgfGrammar.dumpRandomSentences(100);
        
        if (!microphone.startRecording()) {
            recognizer.deallocate();
            throw new RuntimeException("Can't start microphone");
        }
        /* the microphone will keep recording until the thread exits */
        
        System.out.println
        ("Give a two-couple Mainstream call.");
        
        while (true) {
            System.out.println
            ("Start speaking. Press Ctrl-C to quit.\n");
            
            /*
             * This method will return when the end of speech
             * is reached. Note that the endpointer will determine
             * the end of speech.
             */ 
            Result result = recognizer.recognize();
            
            if (result != null) {
                // we can use result.getResults() to get N possible
                // results.  (See source code for
                // Result.getBestFinalResultNoFiller() for details).
                String resultText = result.getBestFinalResultNoFiller();
                System.out.println("You said: " + resultText + "\n");
            } else {
                System.out.println("I can't hear what you said.\n");
            }
        }
    }
}
