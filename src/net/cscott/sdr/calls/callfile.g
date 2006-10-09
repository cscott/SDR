header {
	package net.cscott.sdr.calls;
	import java.io.*;
	import java.util.ArrayList;
	import java.util.List;
	import antlr.debug.misc.ASTFrame;
	import antlr.BaseAST;
	import antlr.CommonToken;
}
// @@parser
//-----------------------------------------------------------------------------
// Define a Parser, calling it XLRecognizer
//-----------------------------------------------------------------------------
class CallFileParser extends Parser;
options {
  k=2;
  buildAST=true;
}
tokens {
	CALLLIST;
	BODY;
	ITEM;
	SEQ;
	PAR;
	OPT;
	NUMBER;
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
      IndentProcessor ip = new IndentProcessor(lexer);
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
	  for (String callName : builder.getMap().keySet()) {
		System.out.println(callName+": "+builder.getMap().get(callName).toStringList());
	  	//ASTFrame frame = new ASTFrame(callName, builder.getMap().get(callName));
  		//frame.setVisible(true);
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
  // Inner class: a token stream filter to implement
  // INDENT/DEDENT processing.
  static class IndentProcessor
	implements TokenStream {
  	protected TokenStream input;
	private final List<Token> pushBack = new ArrayList<Token>();
  	private final List<Integer> stack = new ArrayList<Integer>();

  	/** Stream to read tokens from */
  	public IndentProcessor(TokenStream in) {
    	input = in;
    	pushTab(1);
  	}
  	private Token pullToken() throws TokenStreamException {
  		if (pushBack.isEmpty()) return input.nextToken();
  		else return pushBack.remove(pushBack.size()-1);
  	}
  	private void pushToken(Token t) { pushBack.add(t); }

  	private void pushTab(int i) { stack.add(i); }
  	private int peekTab() { return stack.get(stack.size()-1); }
  	private int popTab() { return stack.remove(stack.size()-1); }

  	/** This makes us a stream */
  	public Token nextToken() throws TokenStreamException {
  		Token t = pullToken();
  		if (t.getType()==INITIAL_WS) {
  			Token tt = t;
  			// replace this with the appropriate INDENT/DEDENT
  			// token.
			t = pullToken(); // use the column of next.
			if (t.getType()==INITIAL_WS) {
				// discard original token: it was an empty line
				pushToken(t); // try this one again
				return nextToken(); // by tail-recursing
			}
			int column = t.getColumn();
			if (column < peekTab()) {
				pushToken(t); pushToken(tt);
				popTab();
				return buildToken(t, DEDENT);
			}
			if (column > peekTab()) {
				pushToken(t); pushToken(tt);
				pushTab(column);
				return buildToken(t, INDENT);
			}
  		}
  		if (t.getType()==Token.EOF_TYPE && peekTab()>1) {
  			// make sure we emit all necessary dedents
			pushToken(t);
			popTab();
			return buildToken(t, DEDENT);
  		}
    	return t; // "short circuit"
  	}
  	private Token buildToken(Token source, int type) {
		Token t = new CommonToken
		  (type, (type==INDENT)?"<indent>":(type==DEDENT)?"<dedent>":"<unk>");
		t.setColumn(source.getColumn());
		t.setLine(source.getLine());
		return t; 
  	}
  }
}

// the following tag is used to find the start of the rules section for
//   automated chunk-grabbing when displaying the page
// @@startrules

calllist
    : ( def )*
      EOF! // end-of-file
	{ #calllist = #([CALLLIST, "call list"], #calllist); }
    ;

def
    : DEF^ COLON! words pieces
    ;

pieces
    :! INDENT p:pieces DEDENT { #pieces = #p; }
    | opt
    | seq
    | par
    | res // restriction
    ;

/// restrictions/timing
res
    : IN^ COLON! number pieces
    | CONDITION^ COLON! body pieces
    ;


// options (exactly one of the list must be selected)
opt
    : (options {greedy=true;} : one_opt)+
	{ #opt = #([OPT, "opt"], #opt); }
    ;
protected one_opt
    : FROM^ COLON! body pieces
    ;

seq
    : (options {greedy=true;} : one_seq)+ 
	{ #seq = #([SEQ, "seq"], #seq); }
    ;
protected one_seq
	: PRIM^ COLON! prim_body
	| CALL^ COLON! body
	| PART^ COLON! pieces
	;

par
    : (options {greedy=true;} : one_par)+
	{ #par = #([PAR, "par"], #par); }
    ;

protected one_par
    : SELECT^ COLON! body pieces
	;

body
	: words (COMMA! words)*
    { #body = #([BODY, "body"], #body); }
	;
words
	: (word)+
	{ #words = #([ITEM, "item"], #words); }
	;
word
	: IDENT
	| number
	| LPAREN! body RPAREN!
	;
prim_body
	: number COMMA! number COMMA! IDENT
	;
number
	: ( (INTEGER)? INTEGER SLASH INTEGER ) =>
	  (p:INTEGER)? n:INTEGER SLASH d:INTEGER
	{ AST ast = astFactory.create(NUMBER, (p==null?"":(p.getText()+" "))+n.getText()+"/"+d.getText());
	  #number = #(ast); }
	| i:INTEGER
	{ AST ast = astFactory.create(NUMBER, i.getText());
	  #number = #(ast); }
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
      ( '\r' (options {greedy=true;} :'\n')? // DOS/Windows / Macintosh
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
  : {this.afterIndent||getColumn()!=1}?
    ('a'..'z'|'A'..'Z') ('a'..'z'|'A'..'Z'|'0'..'9')*
    { if (this.afterIndent) {
    	if ($getText.equals("def")) $setType(DEF);
    	else if ($getText.equals("from")) $setType(FROM);
    	else if ($getText.equals("in")) $setType(IN);
    	else if ($getText.equals("select")) $setType(SELECT);
    	else if ($getText.equals("condition")) $setType(CONDITION);
    	else if ($getText.equals("call")) $setType(CALL);
    	else if ($getText.equals("part")) $setType(PART);
    	else if ($getText.equals("prim")) $setType(PRIM);
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



