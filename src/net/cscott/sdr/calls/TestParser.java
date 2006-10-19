package net.cscott.sdr.calls;

import java.io.*;

import antlr.Token;
import antlr.debug.misc.ASTFrame;

import net.cscott.sdr.calls.ast.Apply;
import net.cscott.sdr.calls.transform.*;

public abstract class TestParser {
    // Define a main
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
          System.err.println("Usage: java TestParser <directory name>");

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

      // otherwise, if this is a java file, parse it!
      else if ((f.getName().length()>6) &&
               f.getName().substring(f.getName().length()-6).equals(".calls")) {
        System.err.println("-------------------------------------------");
        System.err.println(f.getAbsolutePath());
        parseFile(new FileInputStream(f));
      }
    }

    // Here's where we do the real work...
    public static void parseFile(InputStream s) throws Exception {
      try {
        // Create a scanner that reads from the input stream passed to us
        CallFileLexer lexer = new CallFileLexer(s);
        CallFileLexer.IndentProcessor ip =
            new CallFileLexer.IndentProcessor(lexer);
        if (true) {
        // Create a parser that reads from the scanner
        CallFileParser parser = new CallFileParser(ip);

        // start parsing at the calllist rule
        parser.calllist();
        System.out.println(parser.getAST().toStringList());
          if (false) { // fancy gui
                ASTFrame frame = new ASTFrame("Call AST", parser.getAST());
                frame.setVisible(true);
          }
        
        // now build a proper AST.
        CallFileBuilder builder = new CallFileBuilder();
        builder.calllist(parser.getAST());
          for (Call call : builder.getList()) {
              if (call.getMinNumberOfArguments()==0)
                System.out.println(call.getName()+": "+call.apply(Apply.makeApply(call.getName())).toString());
          }

        }else {
                Token t;
                do {
                t = (false)?lexer.nextToken():ip.nextToken();
                System.out.println(t);
                } while(t.getType()!=Token.EOF_TYPE);
        }
      }
      catch (Exception e) {
        System.err.println("parser exception: "+e);
        e.printStackTrace();   // so we can get stack trace             
      }
    }
}
