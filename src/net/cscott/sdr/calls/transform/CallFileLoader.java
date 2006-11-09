package net.cscott.sdr.calls.transform;

import java.net.URL;
import java.util.Map;

import net.cscott.sdr.calls.Call;

/** This class contains the code to parse and load a call list. */
public abstract class CallFileLoader {
    // This does the load
    public static void load(URL file, Map<String,Call> db) {
        try {
            // Create a scanner that reads from the input stream passed to us
            CallFileLexer lexer = new CallFileLexer(file.openStream());
            CallFileLexer.IndentProcessor ip =
                new CallFileLexer.IndentProcessor(lexer);
            // Create a parser that reads from the scanner
            CallFileParser parser = new CallFileParser(ip);
            // start parsing at the calllist rule
            parser.calllist();
            // now build a proper AST.
            CallFileBuilder builder = new CallFileBuilder();
            builder.calllist(parser.getAST());
            for (Call c : builder.getList()) {
                assert !db.containsKey(c.getName()) :
                    "duplicate call: "+c.getName();
                db.put(c.getName(), c);
            }
        }
        catch (Exception e) {
            System.err.println("parser exception loading "+file);
            e.printStackTrace();   // so we can get stack trace             
        }
    }
}
