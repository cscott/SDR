/**
 * Simple lisp-like grammar for printing/scanning {@link AstNode} trees.
 * If this were simpler and more lisp-like, I wouldn't need ANTLR to parse it!
 * @doc.test Simple conversion:
 *  js> new AstParser("(Seq (Prim -1, in 1, none, 1))").ast()
 *  (Seq (Prim -1, in 1, none, 1))
 * @doc.test White space is ignored:
 *  js> new AstParser("( Seq\n (Prim\tin\r-1 ,  1 , none  ,1 ) ) ").ast()
 *  (Seq (Prim in -1, 1, none, 1))
 * @doc.test Keywords ought to be ignored in call names, etc.
 *  js> new AstParser("(Condition \"Condition\" (Condition \"If\") (Condition \"Prim\"))").ast()
 *  (Condition Condition (Condition If) (Condition Prim))
 */
grammar Ast;
@parser::header {
package net.cscott.sdr.calls.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.cscott.sdr.calls.ExactRotation;
import net.cscott.sdr.calls.Predicate;
import net.cscott.sdr.calls.Selector;
import net.cscott.sdr.calls.TaggedFormation.Tag;
import net.cscott.sdr.calls.Warp;

import net.cscott.sdr.util.Fraction;
}
@parser::members {
    public AstParser(String s) {
        this(new CommonTokenStream(new AstLexer(s)));
    }
}
@lexer::header {
package net.cscott.sdr.calls.ast;
}
@lexer::members {
    public AstLexer(String s) {
        this(new ANTLRStringStream(s));
    }
}
// parser rules
// the ast production mirrors the java inheritance hierarchy.
ast returns [AstNode r]
    : comp { $r=$comp.r; }
    | condition { $r=$condition.r; }
    | optcall { $r=$optcall.r; }
    | parcall { $r=$parcall.r; }
    | seqcall { $r=$seqcall.r; }
    ;
comp returns [Comp r]
    : if_ { $r=$if_.r; }
    | in { $r=$in.r; }
    | opt { $r=$opt.r; }
    | par { $r=$par.r; }
    | seq { $r=$seq.r; }
    | warped { $r=$warped.r; }
    ;
condition returns [Condition r]
@init { List<Condition> args = new ArrayList<Condition>(); }
    : '(' 'Condition' predicate (cc=condition {args.add(cc);})* ')'
        { $r=new Condition($predicate.r, args); }
    ;
predicate returns [String r]
    : simple_words { $r = $simple_words.r; }
    | STRING { $r = $STRING.text; }
    ;
optcall returns [OptCall r]
    : '(' 'OptCall' selectors child=comp ')'
        { $r=new OptCall($selectors.r, $child.r); }
    ;
parcall returns [ParCall r]
    : '(' 'ParCall' tags child=comp ')'
        { $r=new ParCall($tags.r, $child.r); }
    ;
seqcall returns [SeqCall r]
    : apply { $r=$apply.r; }
    | part  { $r=$part.r; }
    | prim  { $r=$prim.r; }
    ;
apply returns [Apply r]
@init { List<Apply> args = new ArrayList<Apply>(); }
    : '(' 'Apply' callname=simple_words (aa=apply {args.add(aa);})* ')'
        { $r = new Apply($callname.r, args); }
    ;
part returns [Part r]
    : '(' 'Part' divisible=bool child=comp ')'
        { $r = new Part($divisible.r, $child.r); }
    ;
prim returns [Prim r]
@init { boolean passRight = true, forceArc = false; }
    : '(' 'Prim' x=in_out_num ',' y=in_out_num ',' rot=in_out_dir ','
        time=number
        (',' 'pass-left' {passRight=false;})?
        (',' 'force-arc' {forceArc=true;})? ')'
        { $r = new Prim($x.dir, $x.amt, $y.dir, $y.amt, $rot.dir, $rot.rot,
                        $time.r, passRight, forceArc); }
    ;
if_ returns [If r]
    : '(' 'If' condition child=comp ')'
        { $r = new If($condition.r, $child.r); }
    ;
in returns [In r]
    : '(' 'In' count=number child=comp ')'
        { $r = new In($count.r, $child.r); }
    ;
opt returns [Opt r]
@init { List<OptCall> oc = new ArrayList<OptCall>(); }
    : '(' 'Opt' (optcall { oc.add($optcall.r); })* ')'
        { $r = new Opt(oc); }
    ;
par returns [Par r]
@init { List<ParCall> pc = new ArrayList<ParCall>(); }
    : '(' 'Par' (parcall { pc.add($parcall.r); })* ')'
        { $r = new Par(pc); }
    ;
seq returns [Seq r]
@init { List<SeqCall> sc = new ArrayList<SeqCall>(); }
    : '(' 'Seq' (seqcall {sc.add($seqcall.r); })* ')'
        { $r = new Seq(sc); }
    ;
warped returns [Warped r]
    : '(' 'Warped' warp child=comp ')'
        { $r = new Warped($warp.r, $child.r); }
    ;

selectors returns [List<Selector> r]
    : string_list
        { $r = OptCall.parseFormations($string_list.r); }
    ;
tags returns [Set<Tag> r]
    : string_list
        { $r = ParCall.parseTags($string_list.r); }
    ;

fragment
string_list returns [List<String> r]
@init { $r = new ArrayList<String>(); }
    : '[' e1=list_elem { $r.add($e1.r); }
        ( ',' e2=list_elem { $r.add($e2.r); } )* ']'
    ;
fragment
list_elem returns [String r]
    : simple_words { $r = $simple_words.r; }
    | STRING { $r = $STRING.text; }
    ;

// pieces
fragment
number returns [Fraction r]
    : opt_sign integer fraction
        {
            $r = Fraction.valueOf($integer.r);
            $r=$r.add($fraction.r);
            if ($opt_sign.negate) $r = $r.negate();
        }
    | opt_sign integer
        {
            $r = Fraction.valueOf($integer.r);
            if ($opt_sign.negate) $r = $r.negate();
        }
    | opt_sign fraction
        {
            $r = $fraction.r;
            if ($opt_sign.negate) $r = $r.negate();
        }
    ;
fragment
opt_sign returns [boolean negate]
    : '-' { negate=true; }
    | '+' { negate=false; }
    | /* nothing */ { negate=false; }
    ;
fragment
fraction returns [Fraction r]
    : n=integer '/' d=integer
        { $r = Fraction.valueOf($n.r, $d.r); }
    ;
fragment
integer returns [Integer r]
    : INT
        { $r=Integer.valueOf($INT.text); }
    ;
fragment
in_out_num returns [Prim.Direction dir, Fraction amt]
    : in_out? number
       {
           $amt=$number.r;
           if ($in_out.in || $in_out.out) {
               $dir = Prim.Direction.IN;
               if ($in_out.out) $amt = $amt.negate();
           } else
               $dir = Prim.Direction.ASIS;
       }
    ;
fragment
in_out_dir returns [Prim.Direction dir, ExactRotation rot]
    : in_out? rotation
        {
            $rot = ExactRotation.fromRelativeString($rotation.text);
            if ($in_out.in || $in_out.out) {
                $dir = Prim.Direction.IN;
                if ($in_out.out) $rot = $rot.negate();
            } else
                $dir = Prim.Direction.ASIS;
        }
    ;
fragment
rotation
    : 'right' | 'left' | 'none' | fraction ;

fragment
in_out returns [boolean in, boolean out]
@init { $in=false; $out=false; }
    : 'in' { $in=true; }
    | 'out' { $out=true; }
    ;
fragment
bool returns [Boolean r]
    : 'true' { $r = Boolean.TRUE; }
    | 'false' { $r = Boolean.FALSE; }
    ;
fragment
simple_word returns [String r]
    : IDENT { $r=$IDENT.text; }
    | INT { $r=$INT.text; }
    ;
fragment
simple_words returns [String r]
@init { StringBuilder sb=new StringBuilder(); }
    : w1=simple_word { sb.append($w1.r); }
        (w2=simple_word { sb.append(' '); sb.append($w2.r); } )*
        { $r = sb.toString(); }
    ;

warp returns [Warp r]
    : 'xyzzy' // XXX UNIMPLEMENTED
    ;

// lexer rules
INT
    : ('0'..'9')+
    ;
STRING
    : '"' STRING_BODY '"'
        { setText($STRING_BODY.text); }
    ;
fragment
STRING_BODY
    : CHAR*
    ;
fragment
CHAR
    : '\\n' { setText("\n"); }
    | '\\r' { setText("\r"); }
    | '\\t' { setText("\t"); }
    | '\\\"' { setText("\""); }
    | '\\\\' { setText("\\"); }
    | ~('/'|'"')
    ;
IDENT
    : ('A'..'Z'|'a'..'z'|'_')('A'..'Z'|'a'..'z'|'_'|'0'..'9')*
    ;

WS: ( ' ' | '\t' | '\r' | '\n' )+ { skip(); };

