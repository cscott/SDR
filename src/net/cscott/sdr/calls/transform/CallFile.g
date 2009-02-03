header {
	package net.cscott.sdr.calls.transform;
	import java.io.*;
	import java.util.ArrayList;
	import java.util.List;
	import antlr.collections.AST;
	import antlr.debug.misc.ASTFrame;
	import antlr.CommonToken;
}
// @@parser
//-----------------------------------------------------------------------------
// Define a Parser, calling it CallFileParser
//-----------------------------------------------------------------------------
class CallFileParser extends Parser;
options {
  k=2;
  buildAST=true;
  importVocab = Ast;
}
tokens {
	CALLLIST;
	BODY;
	ITEM;
	NUMBER;
	IPART;
	OUT;
	RIGHT;
	LEFT;
	NONE;
	ARC;
	ATTRIBS;
	REF;
	ADJ;
}

// the following tag is used to find the start of the rules section for
//   automated chunk-grabbing when displaying the page
// @@startrules

// start production for call list.
calllist
    : ( program )*
      EOF! // end-of-file
	{ #calllist = #([CALLLIST, "call list"], #calllist); }
    ;
// another start production for parsing grammar rules
grammar_start
	: grm_rule
	;

program
	: PROGRAM^ COLON! IDENT ( def )*
	;

def
    : DEF^ COLON! call_body INDENT! (os)? pieces ( example )* DEDENT!
    ;
os	: optional (spoken)?
	| s:spoken (o:optional)? { #os = #(o,s); }
	;
optional
	: OPTIONAL^ COLON! IDENT ( COMMA! IDENT )*
	;
spoken
	: SPOKEN^ COLON! (priority)? grm_rule
	;
priority
	: LBRACK! number RBRACK!
	;
example
    : EXAMPLE^ COLON! call_body_seq 
      INDENT! BEFORE COLON! figure AFTER COLON! figure DEDENT!
    ;
figure
    : ( f:FIGURE )+
       { StringBuilder sb = new StringBuilder();
         for (AST a = #figure.getFirstChild(); a!=null; a=a.getNextSibling()) {
           sb.append(a.getText()); sb.append('\n');
         }
         #figure = #([FIGURE, sb.toString()]);
       }
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
    |! CONDITION COLON c:cond_body p:pieces
	{ #res = #([IF, "if"], c, p); }
    ;

// informational
// XXX not yet integrated into grammar probably safest to treat as a
// 'res' like IN, but then ENDS IN should come *first* (we'd rather
// like it to come last, as a suffix to a one_opt, but that causes
// ambiguities).  ASSERT is very similar to CONDITION except that it
// indicates a programmer error, rather than a simple "can't do that
// from here"
endsin
    : ENDS^ IN! COLON! simple_body
    ;
assertion
    : ASSERT^ COLON! cond_body
    ;

// options (exactly one of the list must be selected)
opt
    : (options {greedy=true;} : one_opt)+
	{ #opt = #([OPT, "opt"], #opt); }
    ;
protected one_opt
    : FROM^ COLON! simple_body pieces //( endsin! )? //XXX see above
    ;

seq
    : (options {greedy=true;} : one_seq)+ 
	{ #seq = #([SEQ, "seq"], #seq); }
    ;
protected one_seq
	: PRIM^ COLON! prim_body
	| CALL^ COLON! call_body_seq
	| PART^ COLON! pieces
	| IPART^ COLON! pieces
	;

par
    : (options {greedy=true;} : one_par)+
	{ #par = #([PAR, "par"], #par); }
    ;

protected one_par
    : SELECT^ COLON! simple_ref_body pieces
	;

simple_word
	: IDENT
	| number
	;
simple_words
	: (simple_word)+
	{ #simple_words = #([ITEM, "item"], #simple_words); }
	;
simple_body
	: simple_words (COMMA! simple_words)*
	{ #simple_body = #([BODY, "simple body"], #simple_body); }
	;
simple_ref_body
	: words_or_ref (COMMA! words_or_ref)*
	{ #simple_ref_body = #([BODY, "simple ref body"], #simple_ref_body); }
	;
words_or_ref
	: simple_words
	| ref
	;
ref!
	: LBRACK i:IDENT RBRACK
	{ #ref = #([REF, i.getText()]); }
	;
call_body!
	: w:words_or_ref ( LPAREN! a:call_args RPAREN! )?
	{ #call_body = #([APPLY, "apply"], w, a); }
	;
call_args
	: call_arg (COMMA! call_arg)*
	;
call_arg
	: call_body
	| LPAREN! call_body_seq RPAREN!
	;
call_body_seq!
	: c1:call_body (COMMA! c2:call_body)*
	{ #call_body_seq = (#c2==null) ? #c1 :
		#([APPLY,"apply"], #([ITEM,"item"],[IDENT,"and"]), c1, c2); }
	;
cond_body!
	: w:words_or_ref ( LPAREN! a:cond_args RPAREN! )?
	{ #cond_body = #([CONDITION, "cond"], w, a); }
	;
cond_args
	: cond_body (COMMA! cond_body)*
	;
prim_body
	: in_out_num COMMA! in_out_num COMMA! (IN | OUT | RIGHT | LEFT | NONE) opt_prim_attrib
	;
in_out_num
	: (IN | OUT)? number
	;
opt_prim_attrib!
	: COMMA! a:prim_attribs
	{ #opt_prim_attrib = #([ATTRIBS, "attribs"], a); }
	|
	{ #opt_prim_attrib = #([ATTRIBS, "attribs"]); }
	;
prim_attribs
	: ARC ( prim_attribs )?
	| LEFT ( prim_attribs )?
	;
number
	{ String s; }
	: ( opt_sign (INTEGER)? INTEGER SLASH INTEGER ) =>
	  s=opt_sign (p:INTEGER)? n:INTEGER SLASH d:INTEGER
	{ AST ast = astFactory.create(NUMBER, s+(p==null?"":(p.getText()+" "))+n.getText()+"/"+d.getText());
	  #number = #(ast); }
	| s=opt_sign i:INTEGER
	{ AST ast = astFactory.create(NUMBER, s+i.getText());
	  #number = #(ast); }
	;
opt_sign returns [String s=""]
	: MINUS {s="-";}
	| PLUS {s="+";}
	| /* nothing */;

// Rule Grammar forms, from highest to lowest precedence
// <bar> <foo=bar>
// x? x+ x*
// x x x
// x | y
// ( x )

grm_rule
	: grm_term ( VBAR! g:grm_term )*
	{ if (#g!=null) #grm_rule = #([VBAR,"|"], #grm_rule); }
	;
grm_term
	: grm_factor ( g:grm_factor )*
	{ if (#g!=null) #grm_term = #([ADJ,"adj"], #grm_term); }
	;
grm_factor!
	: e:grm_exp (m:grm_mult)?
	{ #grm_factor= (#m==null) ? #e : #(m,e); }
	;
grm_exp
	: LPAREN! grm_rule RPAREN!
	| IDENT
	|! LANGLE ( id:ref_or_int EQUALS )? r:IDENT RANGLE
	{ #grm_exp = #([REF,"ref"], r, id); } 
	;
ref_or_int
	: IDENT | INTEGER ;
grm_mult
	: PLUS | QUESTION | STAR ;

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
	private boolean beforeColon=true; //have we seen a colon on this line?
	private boolean afterPrim=false; // is this a 'prim' operation?
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
    // set the lexer state to begin parsing a grammar rule
    public void setToRuleStart() {
		afterIndent=true; beforeColon=false; afterPrim=false; done=false;
    }
	/////////////////////////////////////////////////
  // Inner class: a token stream filter to implement
  // INDENT/DEDENT processing.
  public static class IndentProcessor
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
  : ('0'..'9')+
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
      { newline();
		this.afterIndent=false; this.beforeColon=true; this.afterPrim=false;
      	$setType(Token.SKIP); }
  ;
// whitespace at start of line used for INDENT processing
INITIAL_WS
	: {getColumn()==1 && !this.afterIndent}? // at start of line.
	( ' ' | '\t' )*
    { this.afterIndent=true; }
    ;

// Parse formation figure
FIGURE
  : '!'! (~('\n'|'\r'))*
  ;

IDENT
  : {this.afterIndent||getColumn()!=1}?
    ('_'|'a'..'z'|'A'..'Z') ('_'|'a'..'z'|'A'..'Z'|'0'..'9'|'-')*
    { String id = ($getText).toLowerCase().intern();
   	  if (this.afterIndent && this.beforeColon) {
    	if (id=="def") $setType(DEF);
    	else if (id=="from") $setType(FROM);
    	else if (id=="in") $setType(IN);
    	else if (id=="select") $setType(SELECT);
    	else if (id=="condition") $setType(CONDITION);
    	else if (id=="call") $setType(CALL);
    	else if (id=="part") $setType(PART);
    	else if (id=="ipart") $setType(IPART);
    	else if (id=="prim") { $setType(PRIM); afterPrim=true; }
    	else if (id=="program") $setType(PROGRAM);
    	else if (id=="optional") $setType(OPTIONAL);
    	else if (id=="spoken") $setType(SPOKEN);
        else if (id=="example") $setType(EXAMPLE);
        else if (id=="before") $setType(BEFORE);
        else if (id=="after") $setType(AFTER);
        else if (id=="ends") $setType(ENDS);
        else if (id=="assert") $setType(ASSERT);
      } else if (this.afterPrim) {
      	if (id=="in") $setType(IN);
      	else if (id=="out") $setType(OUT);
      	else if (id=="left") $setType(LEFT);
      	else if (id=="right") $setType(RIGHT);
      	else if (id=="none") $setType(NONE);
      	else if (id=="arc") $setType(ARC);
   	  }
    }
  ;
  
// Operators
COMMA      : ','   ;
COLON      : ':' { this.beforeColon=false; }  ;
LPAREN     : '('   ;
RPAREN     : ')'   ;
LBRACK     : '['   ;
RBRACK     : ']'   ;
SLASH      : '/'   ;
QUESTION   : '?'   ;
LANGLE     : '<'   ;
RANGLE     : '>'   ;
EQUALS     : '='   ;
VBAR       : '|'   ;
PLUS       : '+'   ;
MINUS      : '-'   ;
STAR       : '*'   ;
// @@endrules

// @@endscanner



