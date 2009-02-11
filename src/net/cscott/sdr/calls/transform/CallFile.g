// Grammar for call definition files (*.calls)
/**
 * Parser for call definition files (<code>*.calls</code>).
 * @doc.test Trivial call file:
 *  js> new CallFileParser("program: basic").calllist().getTree().toStringTree()
 *  (CALLLIST (program basic))
 *  js> new CallFileParser("program: basic\ndef: _courtesy turn 4/4\n  in:8\n  call: wheelaround").calllist().getTree().toStringTree()
 *  (CALLLIST (program basic (def (APPLY (ITEM _courtesy turn 4/4)) (in 8 (SEQ (call (APPLY (ITEM wheelaround))))))))
 * @doc.test The 'and' concept is applied to successive calls joined by commas:
 *  js> new CallFileParser("program: basic\ndef: foo\n call: bar,bat").calllist().getTree().toStringTree()
 *  (CALLLIST (program basic (def (APPLY (ITEM foo)) (SEQ (call (APPLY (ITEM and) (APPLY (ITEM bar)) (APPLY (ITEM bat))))))))
 * @doc.test Order of optional/spoken is normalized:
 *  js> function cp(s) { return new CallFileParser(s).def().getTree().toStringTree() }
 *  js> cp("def: foo\n optional: REVERSE\n spoken: [10] foo\n call: bar")
 *  (def (APPLY (ITEM foo)) (optional REVERSE) (spoken 10 foo) (SEQ (call (APPLY (ITEM bar)))))
 *  js> cp("def: foo\n spoken: [10] foo\n optional: REVERSE\n call: bar")
 *  (def (APPLY (ITEM foo)) (optional REVERSE) (spoken 10 foo) (SEQ (call (APPLY (ITEM bar)))))
 * @doc.test Example and figure clauses:
 *  js> function cp(s) { return new CallFileParser(s).def().getTree().toStringTree() }
 *  js> cp("def: foo\n call: bar\n example: foo\n  before:\n  ! diagram here\n  after:\n  ! more diagram")
 *  (def (APPLY (ITEM foo)) (example (APPLY (ITEM foo)) before  diagram here
 *   after  more diagram
 *  ) (SEQ (call (APPLY (ITEM bar)))))
 * @doc.test Longer example clause:
 *  js> function cp(s) { return new CallFileParser(s).def().getTree().toStringTree() }
 *  js> cp('def: ferris wheel\n'+
 *    >    '  call: stretch(wheel and deal)\n'+
 *    >    '  example: ferris wheel\n'+
 *    >    '    before:\n'+
 *    >    '    !  ^ ^\n'+
 *    >    '    !  A a c C\n'+
 *    >    '    !  ^ ^ v v\n'+
 *    >    '    !  B b d D\n'+
 *    >    '    !      v v\n'+
 *    >    '    after:\n'+
 *    >    '    !  a A\n'+
 *    >    '    !  v v\n'+
 *    >    '    !  b B\n'+
 *    >    '    !  v v\n'+
 *    >    '    !  ^ ^\n'+
 *    >    '    !  C c\n'+
 *    >    '    !  ^ ^\n'+
 *    >    '    !  D d\n')
 *  (def (APPLY (ITEM ferris wheel)) (example (APPLY (ITEM ferris wheel)) before   ^ ^
 *    A a c C
 *    ^ ^ v v
 *    B b d D
 *        v v
 *   after   a A
 *    v v
 *    b B
 *    v v
 *    ^ ^
 *    C c
 *    ^ ^
 *    D d
 *  ) (SEQ (call (APPLY (ITEM stretch) (APPLY (ITEM wheel and deal))))))
 * @doc.test Grammar precedence 1: INs bind tightly, FROMs do not:
 *  js> function cp(s) { return new CallFileParser(s).def().getTree().toStringTree() }
 *  js> cp("def:foo\n in: 8\n in: 4\n call: bar")
 *  (def (APPLY (ITEM foo)) (in 8 (in 4 (SEQ (call (APPLY (ITEM bar)))))))
 *  js> cp("def:foo\n in: 4\n from: RH_BOX\n  call: bar\n from: LH_BOX\n  call: bat")
 *  (def (APPLY (ITEM foo)) (in 4 (OPT (from (simple body (ITEM RH_BOX)) (SEQ (call (APPLY (ITEM bar))))) (from (simple body (ITEM LH_BOX)) (SEQ (call (APPLY (ITEM bat))))))))
 * @doc.test Grammar precedence: SEQs bind least tightly:
 *  js> function cp(s) { return new CallFileParser(s).def().getTree().toStringTree() }
 *  js> cp("def:foo\n in: 4\n from: RH_MINIWAVE\n call: trade\n from: RH_BOX\n call: bar")
 *  (def (APPLY (ITEM foo)) (in 4 (OPT (from (simple body (ITEM RH_MINIWAVE)) (SEQ (call (APPLY (ITEM trade))))) (from (simple body (ITEM RH_BOX)) (SEQ (call (APPLY (ITEM bar))))))))
 * @doc.test FROM(CONDITION..) requires indentation.
 *  js> function cp(s) { return new CallFileParser(s).def().getTree().toStringTree() }
 *  js> cfp=new CallFileParser("def:foo\n in:4\n from:RH_BOX\n condition:true\n call: bar")
*   net.cscott.sdr.calls.transform.CallFileParser@12a0f6c
 *  js> cfp.def() ; undefined
 *  js> cfp.getNumberOfSyntaxErrors()
 *  1
 * @doc.test PRIMs with numbers:
 *  js> function cp(s) { return new CallFileParser(s).def().getTree().toStringTree() }
 *  js> cp("def:foo\n prim: 1 1/2, 1/2, left")
 *  (def (APPLY (ITEM foo)) (SEQ (prim 1 1/2 1/2 left ATTRIBS)))
 *  js> cp("def:foo\n prim: 1 1/2, 1/2, left, arc left")
 *  (def (APPLY (ITEM foo)) (SEQ (prim 1 1/2 1/2 left (ATTRIBS arc left))))
 *  js> cp("def:foo\n prim: -1 1/2, -1/2, left, left")
 *  (def (APPLY (ITEM foo)) (SEQ (prim -1 1/2 -1/2 left (ATTRIBS left))))
 * @doc.test Spoken language grammar rules, w/ precedence:
 *  js> function g(s) { return new CallFileParser(s).grm_rule().getTree().toStringTree() }
 *  js> g("foo bar|bat? baz")
 *  (| (ADJ foo bar) (ADJ (? bat) baz))
 *  js> g("two <sel=genders> chain")
 *  (ADJ two (REF genders sel) chain)
 *  js> g("two <1=genders> chain")
 *  (ADJ two (REF genders 1) chain)
 *  js> g("two <genders> chain")
 *  (ADJ two (REF genders) chain)
 *  js> g("square thru <n=number> (hands (around|round)?)?")
 *  (ADJ square thru (REF number n) (? (ADJ hands (? (| around round)))))
 */
grammar CallFile;
options {
// parser options
  k=2;
  output=AST;
  //tokenVocab = Ast;
// lexer options
  //charVocabulary = '\0'..'\177'; // ascii only
  //testLiterals=false;    // don't automatically test for literals
  //k=2;                   // two characters of lookahead
}
tokens {
	CALLLIST;
	BODY;
	ITEM;
	NUMBER;
	ATTRIBS;
	REF;
	ADJ;
    IF;
    OPT;
    SEQ;
    PAR;
    APPLY;
}
@parser::header {
	package net.cscott.sdr.calls.transform;
	import java.io.*;
	import java.util.ArrayList;
	import java.util.List;
	import org.antlr.runtime.tree.Tree;
	import org.antlr.runtime.CommonToken;
}
@parser::members {
    public CallFileParser(String s) {
        this(new CommonTokenStream(new CallFileLexer(s)));
    }
}

@lexer::header {
	package net.cscott.sdr.calls.transform;
}
@lexer::members {
    /** Have we seen the line-initial whitespace yet? */
	private boolean afterIndent=false;
    /** Have we seen a colon on this line? */
	private boolean beforeColon=true;
    /** Are we inside a 'prim' operation? */
	private boolean afterPrim=false;
    /** Set the lexer state to begin parsing a grammar rule. */
    public void setToRuleStart() {
		afterIndent=true; beforeColon=false; afterPrim=false;
    }
    // this is a little bit sketchy!
    /** Override superclass' nextToken to insert the IndentProcessor */
    @Override
    public Token nextToken() { return indentProcessor.nextToken(); }
    protected Token superNextToken() { return super.nextToken(); }
    public final IndentProcessor indentProcessor;
    {
        indentProcessor = new IndentProcessor(new TokenSource() {
                @Override
                public String getSourceName() {
                    return CallFileLexer.this.getSourceName();
                }
                @Override
                public Token nextToken() {
                    return superNextToken();
                }
            });
    }
    // this constructor is primarily for test suite use, but it's a
    // convenient place to put the doctests.  Pity that ANTLR doesn't
    // let me generate a proper top-level lexer doc comment.
    /**
      * Call file lexer.
      * Call files are indent-sensitive, like Python.  An inner
      * IndentProcessor is used to convert leading whitespace into
      * synthetic INDENT/DEDENT tokens, where appropriate.
      * @doc.test Simple tokens:
      *  js> new CallFileLexer("program: basic").tokensToString()
      *  1.0: "program"
      *  1.7: ":"
      *  1.9: "basic"
      *  0.-1: <EOF>
      * @doc.test INITIAL_WS should trigger at the beginning of every line:
      *  js> new CallFileLexer("+", false).tokensToString() // length-1 match
      *  1.0: <INITIAL_WS>""
      *  1.0: "+"
      *  0.-1: <EOF>
      *  js> new CallFileLexer("// foo", false).tokensToString() // comments
      *  1.0: <INITIAL_WS>""
      *  0.-1: <EOF>
      *  js> new CallFileLexer("program: basic", false).tokensToString() //IDENT
      *  1.0: <INITIAL_WS>""
      *  1.0: "program"
      *  1.7: ":"
      *  1.9: "basic"
      *  0.-1: <EOF>
      * @doc.test Indent processing with spaces:
      *  js> new CallFileLexer("program: basic\n def: foo").tokensToString()
      *  1.0: "program"
      *  1.7: ":"
      *  1.9: "basic"
      *  2.1: "<indent>"
      *  2.1: "def"
      *  2.4: ":"
      *  2.6: "foo"
      *  0.-1: "<dedent>"
      *  0.-1: <EOF>
      * @doc.test Tab stops at 8-character boundaries:
      *  js> new CallFileLexer("foo\n \tbar\n        bat").tokensToString()
      *  1.0: "foo"
      *  2.2: "<indent>"
      *  2.2: "bar"
      *  3.8: "bat"
      *  0.-1: "<dedent>"
      *  0.-1: <EOF>
      * @doc.test Multiple indentation levels:
      *  js> new CallFileLexer("foo\n bar\n\tbat\nbaz").tokensToString()
      *  1.0: "foo"
      *  2.1: "<indent>"
      *  2.1: "bar"
      *  3.1: "<indent>"
      *  3.1: "bat"
      *  4.0: "<dedent>"
      *  4.0: "<dedent>"
      *  4.0: "baz"
      *  0.-1: <EOF>
      *  js> new CallFileLexer("foo\n bar\n\tbat\n baz").tokensToString()
      *  1.0: "foo"
      *  2.1: "<indent>"
      *  2.1: "bar"
      *  3.1: "<indent>"
      *  3.1: "bat"
      *  4.1: "<dedent>"
      *  4.1: "baz"
      *  0.-1: "<dedent>"
      *  0.-1: <EOF>
      * @doc.test Keywords only apply before colons:
      *  js> cl = new CallFileLexer("def: def")
      *  net.cscott.sdr.calls.transform.CallFileLexer@ce5b1c
      *  js> cl.nextToken().getType() == cl.DEF
      *  true
      *  js> cl.nextToken().getType() == cl.COLON
      *  true
      *  js> cl.nextToken().getType() == cl.IDENT
      *  true
      *  js> cl.nextToken().getType()
      *  -1
      * @doc.test Comment match is non-greedy:
      *  js> new CallFileLexer("/* foo *"+"/ bar /* bat *"+"/").tokensToString()
      *  1.10: "bar"
      *  0.-1: <EOF>
      * @doc.test Special keywords available only after 'prim':
      *  js> cl = new CallFileLexer("out", false)
      *  net.cscott.sdr.calls.transform.CallFileLexer@1f3aa07
      *  js> cl.nextToken().getType() == cl.INITIAL_WS
      *  true
      *  js> cl.nextToken().getType() == cl.IDENT
      *  true
      *  js> cl.nextToken().getType()
      *  -1
      *  js> cl = new CallFileLexer("prim: out", false)
      *  net.cscott.sdr.calls.transform.CallFileLexer@1fc2fb
      *  js> cl.nextToken().getType() == cl.INITIAL_WS
      *  true
      *  js> cl.nextToken().getType() == cl.PRIM
      *  true
      *  js> cl.nextToken().getType() == cl.COLON
      *  true
      *  js> cl.nextToken().getType() == cl.OUT
      *  true
      *  js> cl.nextToken().getType()
      *  -1
      */
    public CallFileLexer(String s) {
        this(s, true);
    }
    public CallFileLexer(String s, boolean useIndentProcessor) {
        this(new ANTLRStringStream(s));
        indentProcessor.disabled = !useIndentProcessor;
    }
    /** For testing. */
    public String tokensToString() {
        StringBuilder sb=new StringBuilder();
        Token t;
        do {
            if (sb.length() > 0) sb.append('\n');
            t = nextToken();
            sb.append(t.getLine());
            sb.append('.');
            sb.append(t.getCharPositionInLine());
            sb.append(": ");
            if (t==Token.EOF_TOKEN) sb.append("<EOF>");
            else {
                if (t.getType() == INITIAL_WS) sb.append("<INITIAL_WS>");
                sb.append('"');
                for (char c: t.getText().toCharArray())
                    switch (c) {
                    case '\n': sb.append("\\n"); break;
                    case '\r': sb.append("\\r"); break;
                    case '\t': sb.append("\\t"); break;
                    default: sb.append(c); break;
                    }
                sb.append('"');
            }
        } while (t != Token.EOF_TOKEN);
        return sb.toString();
    }

	/////////////////////////////////////////////////
    /** Inner class: a token stream filter to implement
     * INDENT/DEDENT processing. */
    public static class IndentProcessor
        implements TokenSource {
        protected TokenSource input;
        private final List<Token> pushBack = new ArrayList<Token>();
        private final List<Integer> stack = new ArrayList<Integer>();

        /** Stream to read tokens from */
        public IndentProcessor(TokenSource in) {
            input = in;
            pushTab(0);
        }
        /** Set to true to disable the IndentProcessor. */
        public boolean disabled = false;
        public String getSourceName() { return input.getSourceName(); }
        private Token pullToken() {
            if (pushBack.isEmpty()) {
                Token t;
                do { // skip hidden tokens from input source.
                    t = input.nextToken();
                } while (t.getChannel()==Token.HIDDEN_CHANNEL);
                return t;
            }
            else return pushBack.remove(pushBack.size()-1);
        }
        private void pushToken(Token t) { pushBack.add(t); }

        private void pushTab(int i) { stack.add(i); }
        private int peekTab() { return stack.get(stack.size()-1); }
        private int popTab() { return stack.remove(stack.size()-1); }

        /** This function implements tab expansion.  We use 8-space tabs. */
        private static int countWhiteSpace(String s) {
            int t = 8; // 8-space tabs.
            int cnt=0;
            for (char c : s.toCharArray())
                switch(c) {
                case '\t': // ma
                    cnt = ((cnt/t)+1)*t; break;
                default:
                    cnt++; break;
                }
            return cnt;
        }

        /** This makes us a {@link TokenSource}. */
        public Token nextToken() {
            Token t = pullToken();
            if (disabled) return t; // allow bypass for testing.
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
                int column = countWhiteSpace(tt.getText());
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
            if (t==Token.EOF_TOKEN && peekTab()>0) {
                // make sure we emit all necessary dedents
                pushToken(t);
                popTab();
                return buildToken(t, DEDENT);
            }
            return t; // "short circuit"
        }
        private Token buildToken(Token source, int type) {
            Token t = new CommonToken
                (type, (type==INDENT)?"<indent>":(type==DEDENT)?"<dedent>":
                       "<unk>");
            t.setCharPositionInLine(source.getCharPositionInLine());
            t.setLine(source.getLine());
            return t;
        }
    }
}

// -------------------------------------------------------------------
// The parser
// -------------------------------------------------------------------

// start production for call list.
calllist
    : ( program )*
      EOF // end-of-file
        -> ^(CALLLIST program*)
    ;
// another start production for parsing grammar rules
grammar_start
	: grm_rule
	;

program
	: PROGRAM^ COLON! IDENT ( def )*
	;

def
    : DEF COLON call_body INDENT os? pieces example* DEDENT
        -> ^(DEF call_body os? example* pieces)
    ;
os	: optional (spoken)?
	| spoken (optional)?
        -> optional? spoken
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
    @init { StringBuilder sb=new StringBuilder(); }
    : ( FIGURE { sb.append($FIGURE.getText()); sb.append('\n'); } )+
       -> ^(FIGURE[sb.toString()])
    ;

// note that 'opt' productions (for example) can end with 'pieces', so
// there's an ambiguity here.  Given:
//  def: trade
//    from: COUPLE
//      in: 6
//      select: BEAU
//      ...
//    from: RH MINIWAVE, LH MINIWAVE
//      ...
// we want to parse this as
//   (DEF (APPLY trade) (OPT (FROM ...) (FROM ...)))
// not:
//   (DEF (APPLY trade) (OPT (FROM ... (OPT (FROM ...)))))
// But for 'res' productions such as 'in' and 'condition', we do want
//   (IN (SEQ ...))
// not:
//   (IN (SEQ ...)) (SEQ ...)
//
// We factor the grammar and use precedence to resolve the ambiguity.

pieces
    : res // restriction
    | opt
    | par
    | pieces_term
    ;
fragment pieces_term
    : seq
    | pieces_factor
    ;
fragment pieces_factor
    : INDENT! pieces DEDENT!
    ;

/// restrictions/timing
res
    : IN^ COLON! number pieces
    | CONDITION COLON cond_body pieces
        -> ^(IF cond_body pieces)
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
    : ( options {greedy=true;} : one_opt)+
        -> ^(OPT one_opt+)
    ;
fragment one_opt
    : FROM^ COLON! simple_body pieces_term //( endsin! )? //XXX see above
    ;

seq
    : ( options {greedy=true;} : one_seq)+
        -> ^(SEQ one_seq+)
    ;
fragment one_seq
	: PRIM^ COLON! prim_body
	| CALL^ COLON! call_body_seq
	| PART^ COLON! pieces_factor
	| IPART^ COLON! pieces_factor
	;

par
    : ( options {greedy=true;} : one_par)+
        -> ^(PAR one_par+)
    ;

fragment one_par
    : SELECT^ COLON! simple_ref_body pieces_term
	;

simple_word
	: IDENT
	| number
	;
simple_words
	: simple_word+
        -> ^(ITEM simple_word+)
	;
simple_body
	: simple_words (COMMA simple_words)*
        -> ^(BODY["simple body"] simple_words+)
	;
simple_ref_body
	: words_or_ref (COMMA words_or_ref)*
        -> ^(BODY["simple ref body"] words_or_ref+)
	;
words_or_ref
	: simple_words
	| ref
	;
ref!
	: LBRACK IDENT RBRACK
        -> ^(REF[$IDENT.getText()])
	;
call_body!
	: words_or_ref ( LPAREN call_args RPAREN )?
        -> ^(APPLY words_or_ref call_args?)
	;
call_args
	: call_arg (COMMA! call_arg)*
	;
call_arg
	: call_body
	| LPAREN! call_body_seq RPAREN!
	;
call_body_seq
    : (call_body COMMA call_body) =>
       call_body (COMMA call_body)+
        -> ^(APPLY ^(ITEM IDENT["and"]) call_body+)
    | call_body
	;
cond_body
	: words_or_ref ( LPAREN cond_args RPAREN )?
        -> ^(CONDITION words_or_ref cond_args?)
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
	: COMMA prim_attribs
        -> ^(ATTRIBS prim_attribs)
    |
        -> ^(ATTRIBS)
	;
prim_attribs
	: ARC ( prim_attribs )?
	| LEFT ( prim_attribs )?
	;
number
	@init { String nstr=null; }
	: ( opt_sign (INTEGER)? INTEGER SLASH INTEGER ) =>
	  s=opt_sign (p=INTEGER)? n=INTEGER SLASH d=INTEGER
        {nstr = s.s+(p==null?"":(p.getText()+" "))+n.getText()+"/"+d.getText();}
        -> ^(NUMBER[nstr])
	| s=opt_sign i=INTEGER
        { nstr = s.s+i.getText(); }
        -> ^(NUMBER[nstr])
	;
opt_sign returns [String s]
	: MINUS { $opt_sign.s="-";}
	| PLUS { $opt_sign.s="+"; }
	| /* nothing */ { $opt_sign.s=""; };

// Rule Grammar forms, from highest to lowest precedence
// <bar> <foo=bar>
// x? x+ x*
// x x x
// x | y
// ( x )

grm_rule
    : (grm_term VBAR grm_term) =>
	  grm_term ( VBAR grm_term )+
        -> ^(VBAR grm_term+)
    | grm_term
	;
grm_term
    : (grm_factor grm_factor) =>
      grm_factor ( grm_factor )+
        -> ^(ADJ grm_factor+)
    | grm_factor
	;
grm_factor
    : (grm_exp grm_mult) =>
	  grm_exp grm_mult
        -> ^(grm_mult grm_exp)
    | grm_exp
	;
grm_exp
	: LPAREN! grm_rule RPAREN!
	| IDENT
	| LANGLE ( ref_or_int EQUALS )? IDENT RANGLE
        -> ^(REF IDENT ref_or_int?)
	;
ref_or_int
	: IDENT | INTEGER ;
grm_mult
	: PLUS | QUESTION | STAR ;

//----------------------------------------------------------------------------
// The scanner
//----------------------------------------------------------------------------

// whitespace at start of line used for INDENT processing
// first rule; will always match if afterIndent is false.
INITIAL_WS
    : {!afterIndent}?=> // at start of line.
	( ' ' | '\t' )*
    { afterIndent=true; }
    ;

COMMENT
  // Single-line comments
  : {afterIndent}?=>
    '//' (~('\n'|'\r'))* { $channel=HIDDEN; }
  // Block comments
  | {afterIndent}?=>
    '/*' .* '*/' // NOTE: we're not calling NL here; & .* is magically ungreedy
    { $channel=HIDDEN; }
  ;

// Literals

INTEGER 
  : {afterIndent}?=>
    ('0'..'9')+
  ;
  
// newline processing
fragment NL
  : // handle newlines
      ( '\r\n' // DOS/Windows (greedy match, comes first)
      | '\r' // Macintosh
      | '\n' // Unix
      )
      // increment the line count in the scanner
      { this.afterIndent=false; this.beforeColon=true; this.afterPrim=false; }
  ;

// Whitespace -- ignored
WS
  : {afterIndent}?=> // not start-of-line whitespace
  ( ' ' | '\t' )
    { $channel=HIDDEN; }
    ;
WSNL
  : NL
    { $channel=HIDDEN; }
  ;

// Parse formation figure
FIGURE
  : {afterIndent}?=>
    '!' (~('\n'|'\r'))*
        { setText(getText().substring(1)); }
  ;

// keywords
DEF :      {afterIndent && beforeColon}?=> 'def' ;
FROM:      {afterIndent && beforeColon}?=> 'from' ;
IN:      {(afterIndent && beforeColon) || afterPrim}?=> 'in' ;
SELECT:    {afterIndent && beforeColon}?=> 'select' ;
CONDITION: {afterIndent && beforeColon}?=> 'condition' ;
CALL:      {afterIndent && beforeColon}?=> 'call' ;
PART:      {afterIndent && beforeColon}?=> 'part' ;
IPART:     {afterIndent && beforeColon}?=> 'ipart' ;
PRIM:      {afterIndent && beforeColon}?=> 'prim' { afterPrim=true;};
PROGRAM:   {afterIndent && beforeColon}?=> 'program' ;
OPTIONAL:  {afterIndent && beforeColon}?=> 'optional' ;
SPOKEN:    {afterIndent && beforeColon}?=> 'spoken' ;
EXAMPLE:   {afterIndent && beforeColon}?=> 'example' ;
BEFORE:    {afterIndent && beforeColon}?=> 'before' ;
AFTER:     {afterIndent && beforeColon}?=> 'after' ;
ENDS:      {afterIndent && beforeColon}?=> 'ends' ;
ASSERT:    {afterIndent && beforeColon}?=> 'assert' ;
// special keywords for PRIM statements
OUT:       {afterPrim}?=> 'out';
LEFT:      {afterPrim}?=> 'left';
RIGHT:     {afterPrim}?=> 'right';
NONE:      {afterPrim}?=> 'none';
ARC:       {afterPrim}?=> 'arc';

IDENT
  : {afterIndent}?=>
    ('_'|'a'..'z'|'A'..'Z') ('_'|'a'..'z'|'A'..'Z'|'0'..'9'|'-')*
  ;
  
// Operators
COMMA      : {afterIndent}?=> ','   ;
COLON      : {afterIndent}?=> ':' { this.beforeColon=false; }  ;
LPAREN     : {afterIndent}?=> '('   ;
RPAREN     : {afterIndent}?=> ')'   ;
LBRACK     : {afterIndent}?=> '['   ;
RBRACK     : {afterIndent}?=> ']'   ;
SLASH      : {afterIndent}?=> '/'   ;
QUESTION   : {afterIndent}?=> '?'   ;
LANGLE     : {afterIndent}?=> '<'   ;
RANGLE     : {afterIndent}?=> '>'   ;
EQUALS     : {afterIndent}?=> '='   ;
VBAR       : {afterIndent}?=> '|'   ;
PLUS       : {afterIndent}?=> '+'   ;
MINUS      : {afterIndent}?=> '-'   ;
STAR       : {afterIndent}?=> '*'   ;

// real (stub) definitions for imaginary lexer tokens (blah)
// synthetic tokens
fragment INDENT : ;
fragment DEDENT : ;

// that's all, folks!
