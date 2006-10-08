header {
	package net.cscott.sdr.calls;
	import java.io.*;
}
// @@parser
//-----------------------------------------------------------------------------
// Define a Parser, calling it XLRecognizer
//-----------------------------------------------------------------------------
class CallFileParser extends Parser;
options {
  defaultErrorHandler = true;      // Don't generate parser error handlers
}


// Define some methods and variables to use in the generated parser.
{
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
        System.err.println("Usage: java CallFileParser <directory name>");

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
    else if ((f.getName().length()>5) &&
             f.getName().substring(f.getName().length()-3).equals(".xl")) {
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

      // Create a parser that reads from the scanner
      CallFileParser parser = new CallFileParser(lexer);

      // start parsing at the compilationUnit rule
      parser.calllist();
    }
    catch (Exception e) {
      System.err.println("parser exception: "+e);
      e.printStackTrace();   // so we can get stack trace		
    }
  }
}

// the following tag is used to find the start of the rules section for
//   automated chunk-grabbing when displaying the page
// @@startrules

calllist
    : ( def )*
      // end-of-file
    ;

def
    : DEF words
      ( pieces )+
    ;

pieces
    : INDENT pieces DEDENT
    | opt
    | seq
    | par
    | res // restriction
    ;

/// restrictions/timing
res
    : IN NUMBER (options {greedy=true;} : pieces)+
    | CONDITION body (options {greedy=true;} : pieces)+
    ;


// options (exactly one of the list must be selected)
opt
    : (options {greedy=true;} : one_opt)+
    ;
protected one_opt
    : FROM body (options {greedy=true;} : pieces)+
    ;

seq
    : (options {greedy=true;} : one_seq)+ 
    ;
protected one_seq
	: PRIM prim_body
	| CALL body
	| PART pieces
	;

par
    : (options {greedy=true;} : one_par)+
    ;

protected one_par
    : SELECT body (options {greedy=true;} : pieces)+
	;

body
	: words (COMMA words)*
	;
words
	: (word word)+
	;
word
	: IDENT
	| NUMBER
	| LPAREN body RPAREN
	;
prim_body
	: NUMBER COMMA NUMBER COMMA IDENT
	;

// @@endparser
// @@endrules

// @@scanner
//----------------------------------------------------------------------------
// The scanner
//----------------------------------------------------------------------------
class CallFileLexer extends Lexer;

options {
  charVocabulary = '\0'..'\177'; // ascii only
  testLiterals=false;    // don't automatically test for literals
  k=2;                   // two characters of lookahead
}

// @@startrules

// Single-line comments
COMMENT
  : "//" (~('\n'|'\r'))*
    { $setType(Token.SKIP); }
  ;


// Literals
protected DIGIT
  : '0'..'9'
  ;

INTLIT 
  : (DIGIT)+
  ;
  
CHARLIT
  : '\''! . '\''!
  ;

// string literals
STRING_LITERAL
  : '"'!
    ( '"' '"'!
    | ~('"'|'\n'|'\r')
    )*
    ( '"'!
    | // nothing -- write error message
    )
   ;

// Whitespace -- ignored
WS
  : ( ' '
    | '\t'
    | '\f'

    // handle newlines
    | ( "\r\n"  // DOS/Windows
      | '\r'    // Macintosh
      | '\n'    // Unix
      )
      // increment the line count in the scanner
      { newline(); }
    )
    { $setType(Token.SKIP); }
  ;


// an identifier.  Note that testLiterals is set to true!  This means
// that after we match the rule, we look in the literals table to see
// if it's a literal or really an identifer
IDENT
  options {testLiterals=true;}
  : ('a'..'z'|'A'..'Z') ('a'..'z'|'A'..'Z'|'0'..'9')*
  ;
  
// Operators
DOT        : '.'   ;
BECOMES    : ":="  ;
COLON      : ':'   ;
SEMI       : ';'   ;
COMMA      : ','   ;
EQUALS     : '='   ;
LBRACKET   : '['   ;
RBRACKET   : ']'   ;
DOTDOT     : ".."  ;
LPAREN     : '('   ;
RPAREN     : ')'   ;
NOT_EQUALS : "/="  ;
LT         : '<'   ;
LTE        : "<="  ;
GT         : '>'   ;
GTE        : ">="  ;
PLUS       : '+'   ;
MINUS      : '-'   ;
TIMES      : '*'   ;
DIV        : '/'   ;
// @@endrules

// @@endscanner



