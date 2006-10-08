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
  k=2;
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
	if (false) {
      // Create a parser that reads from the scanner
      CallFileParser parser = new CallFileParser(lexer);

      // start parsing at the compilationUnit rule
      parser.calllist();
	}else {
		Token t;
		do {
		t = lexer.nextToken();
		System.out.println(t+" "+t.getType());
		} while(t.getType()!=Token.EOF_TYPE);
	}
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
      EOF // end-of-file
    ;

def
    : DEF COLON words
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
    : IN COLON number (options {greedy=true;} : pieces)+
    | CONDITION COLON body (options {greedy=true;} : pieces)+
    ;


// options (exactly one of the list must be selected)
opt
    : (options {greedy=true;} : one_opt)+
    ;
protected one_opt
    : FROM COLON body (options {greedy=true;} : pieces)+
    ;

seq
    : (options {greedy=true;} : one_seq)+ 
    ;
protected one_seq
	: PRIM COLON prim_body
	| CALL COLON body
	| PART COLON pieces
	;

par
    : (options {greedy=true;} : one_par)+
    ;

protected one_par
    : SELECT COLON body (options {greedy=true;} : pieces)+
	;

body
	: words (COMMA words)*
	;
words
	: (word word)+ // proper fractions show up as two words
	;
word
	: IDENT
	| INTEGER (SLASH INTEGER)? // simple number
	| LPAREN body RPAREN
	;
prim_body
	: number COMMA number COMMA IDENT
	;
number
	: (INTEGER)? INTEGER SLASH INTEGER
	| INTEGER
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

{
	private boolean afterIndent=false;//have we seen the line-initial ws yet?
	// set tabs to 8, just round column up to next tab + 1
	public void tab() {
     	int t = 8;
        int c = getColumn();
        int nc = (((c-1)/t)+1)*t+1;
        setColumn( nc );
	}
	public boolean done = false;
	public void uponEOF()
        throws TokenStreamException, CharStreamException
    {
        done=true;
    }
}

// @@startrules

COMMENT
  // Single-line comments
  : "//" (~('\n'|'\r'))*
    { $setType(Token.SKIP); }
  // Block comments
  | "/*" (options {greedy=false;} : STUFF_INCL_NEWLINES)* "*/"
    { $setType(Token.SKIP); }
  ;
protected STUFF_INCL_NEWLINES
	: WSNL | ~('\n'|'\r');

// Literals

INTEGER 
  : ('-'|'+')? ('0'..'9')+
  ;
  
// Whitespace -- ignored
WS
  : {getColumn()!=1}? // not start-of-line whitespace
  ( ' ' | '\t' )
    { $setType(Token.SKIP); }
    ;
WSNL
  : // handle newlines
      ( "\r" ("\n")?  // DOS/Windows / Macintosh
      | '\n'    // Unix
      )
      // increment the line count in the scanner
      { newline(); this.afterIndent=false;
      	$setType(Token.SKIP); }
  ;
// whitespace at start of line used for INDENT processing
INITIAL_WS
	: {getColumn()==1 && !this.afterIndent}? // at start of line.
	( ' ' | '\t' )*
    { this.afterIndent=true; }
    ;

IDENT
  : ('a'..'z'|'A'..'Z') ('a'..'z'|'A'..'Z'|'0'..'9')*
    { if (this.afterIndent) {
    	if ($getText.equals("def")) $setType(DEF);
    	else if ($getText.equals("from")) $setType(FROM);
    	else if ($getText.equals("in")) $setType(IN);
    	else if ($getText.equals("select")) $setType(SELECT);
    	else if ($getText.equals("condition")) $setType(CONDITION);
    	else if ($getText.equals("call")) $setType(CALL);
    	else if ($getText.equals("part")) $setType(PART);
      }
    }
  ;
  
// Operators
COMMA      : ','   ;
COLON      : ':'   ;
LPAREN     : '('   ;
RPAREN     : ')'   ;
SLASH      : '/'   ;
// @@endrules

// @@endscanner



