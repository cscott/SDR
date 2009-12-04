package net.cscott.sdr.calls.parser;

import java.io.*;
import java.util.Collections;

import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;

import net.cscott.sdr.calls.Call;
import net.cscott.sdr.calls.ast.Apply;
import net.cscott.sdr.calls.ast.Expr;
import net.cscott.sdr.calls.transform.*;

/** Simple parser driver to syntax-check call lists. */
public abstract class TestParser {
    /** Invoke this driver as:
     * <pre>java TestParser &lt;file or directory name&gt;</pre>
     */
    public static void main(String[] args) {
      // Use a try/catch block for parser exceptions
      try {
        // if we have at least one command-line argument
        if (args.length > 0 ) {
          System.err.println("Parsing...");

          // for each directory/file specified on the command line
          for(int i=0; i< args.length;i++)
            doFile(new File(args[i])); // parse it
        }
        else
          System.err.println("Usage: java TestParser <file or directory name>");

      }
      catch(Exception e) {
        System.err.println("exception: "+e);
        e.printStackTrace(System.err);   // so we can get stack trace
      }
    }


    // This method decides what action to take based on the type of
    //   file we are looking at
    public static void doFile(File f) throws Exception {
      // If this is a directory, walk each file/dir in that directory
      if (f.isDirectory()) {
        String files[] = f.list();
        for(int i=0; i < files.length; i++)
          doFile(new File(f, files[i]));
      }

      // otherwise, if this is a .calls file, parse it!
      else if ((f.getName().length()>6) &&
               f.getName().substring(f.getName().length()-6).equals(".calls")) {
        System.err.println("-------------------------------------------");
        System.err.println(f.getAbsolutePath());
        parseFile(new FileInputStream(f));
      }
    }

    // Here's where we do the real work...
    public static void parseFile(InputStream s) throws Exception {
      Reader r = new InputStreamReader(s, "utf-8");
      try {
        // Create a scanner that reads from the input stream passed to us
	  CallFileLexer lexer = new CallFileLexer(new ANTLRReaderStream(r));
        if (true) {
        // Create a parser that reads from the scanner
	    CallFileParser parser = new CallFileParser(new CommonTokenStream(lexer));

        // start parsing at the calllist rule
        parser.calllist();
	Tree t = (Tree) parser.calllist().getTree();
        System.out.println(t.toStringTree());
        
        // now build a proper AST.
        CallFileBuilder builder = new CallFileBuilder(new CommonTreeNodeStream(t));
        builder.calllist();
          for (Call call : builder.getList()) {
              if (call.getMinNumberOfArguments()==0)
                System.out.println(call.getName()+": "+call.getEvaluator(null, Collections.<Expr>emptyList()).simpleExpansion());
              if (call.getRule()!=null)
                  System.out.println(call.getRule());
          }
        }else {
	    lexer.indentProcessor.disabled = true; // false.
                Token t;
                do {
		    t = lexer.nextToken();
		    System.out.println(t);
                } while(t!=Token.EOF_TOKEN);
        }
      }
      catch (Exception e) {
        System.err.println("parser exception: "+e);
        e.printStackTrace();   // so we can get stack trace             
      }
    }
}
