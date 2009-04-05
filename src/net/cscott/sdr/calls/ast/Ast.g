/**
 * Simple lisp-like grammar for printing/scanning {@link AstNode} trees.
 * If this were simpler and more lisp-like, I wouldn't need ANTLR to parse it!
 * @doc.test Simple conversion:
 *  js> new AstParser("(Seq (Prim -1, in 1, none, 1))").ast()
 *  (Seq (Prim -1, in 1, none, 1))
 * @doc.test White space is ignored:
 *  js> new AstParser("( Seq\n (Prim\tin\r-1 ,  1 , out  1  / 4  ,1 ) ) ").ast()
 *  (Seq (Prim in -1, 1, in -1/4, 1))
 * @doc.test Call names, predicates, formations, etc can be quoted:
 *  js> new AstParser("(Condition \"Condition\" (Condition \"If\") (Condition \"Prim\"))").ast()
 *  (Condition Condition (Condition If) (Condition Prim))
 * @doc.test Keywords ought to be ignored in call names, etc.
 *  js> new AstParser("(Condition Condition (Condition If) (Condition Prim))").ast()
 *  (Condition Condition (Condition If) (Condition Prim))
 * @doc.test Parsing complicated Prims:
 *  js> new AstParser("(Seq (Prim 1 1/2, 1/2, left, 1, PASS_LEFT, FORCE_ARC, FORCE_ROLL_RIGHT))").ast()
 *  (Seq (Prim 1 1/2, 1/2, left, 1, PASS_LEFT, FORCE_ARC, FORCE_ROLL_RIGHT))
 */
grammar Ast;
@parser::header {
package net.cscott.sdr.calls.ast;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import net.cscott.sdr.calls.ExactRotation;
import net.cscott.sdr.calls.Predicate;
import net.cscott.sdr.calls.Selector;
import net.cscott.sdr.calls.TaggedFormation.Tag;

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

start returns [AstNode r]
    : ast EOF { $r=$ast.r; }
    ;

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
    ;
condition returns [Condition r]
@init { List<Condition> args = new ArrayList<Condition>(); }
    : {input.LT(2).getText().equalsIgnoreCase("Condition")}?
        '(' IDENT predicate (cc=condition {args.add(cc);})* ')'
        { $r=new Condition($predicate.r, args); }
    ;
optcall returns [OptCall r]
    : {input.LT(2).getText().equalsIgnoreCase("OptCall")}?
        '(' IDENT selectors child=comp ')'
        { $r=new OptCall($selectors.r, $child.r); }
    ;
parcall returns [ParCall r]
    : {input.LT(2).getText().equalsIgnoreCase("ParCall")}?
        '(' IDENT tags child=comp ')'
        { $r=new ParCall($tags.r, $child.r); }
    ;
seqcall returns [SeqCall r]
    : apply { $r=$apply.r; }
    | part  { $r=$part.r; }
    | prim  { $r=$prim.r; }
    ;
apply returns [Apply r]
@init { List<Apply> args = new ArrayList<Apply>(); }
    : {input.LT(2).getText().equalsIgnoreCase("Apply")}?
        '(' IDENT callname=simple_words (aa=apply {args.add(aa);})* ')'
        { $r = new Apply($callname.r, args); }
    ;
part returns [Part r]
    : {input.LT(2).getText().equalsIgnoreCase("Part")}?
        '(' IDENT divisible=bool child=comp ')'
        { $r = new Part($divisible.r, $child.r); }
    ;
prim returns [Prim r]
@init { boolean passRight = true, forceArc = false; }
    : {input.LT(2).getText().equalsIgnoreCase("Prim")}?
        '(' IDENT x=in_out_num ',' y=in_out_num ',' rot=in_out_dir ','
        time=number flags=prim_flags ')'
        { $r = new Prim($x.dir, $x.amt, $y.dir, $y.amt, $rot.dir, $rot.rot,
                        $time.r,
                        $flags.r.toArray(new Prim.Flag[$flags.r.size()])); }
    ;
fragment
prim_flags returns [Set<Prim.Flag> r]
@init { r = EnumSet.noneOf(Prim.Flag.class); }
    : ( ',' prim_flag { $r.add($prim_flag.r); } )*
    ;
fragment
prim_flag returns [Prim.Flag r]
    : {Prim.Flag.contains(Prim.Flag.canon(input.LT(1).getText()))}?
        IDENT { $r = Prim.Flag.valueOf(Prim.Flag.canon($IDENT.text)); }
    ;

if_ returns [If r]
    : {input.LT(2).getText().equalsIgnoreCase("If")}?
        '(' IDENT condition child=comp ')'
        { $r = new If($condition.r, $child.r); }
    ;
in returns [In r]
    : {input.LT(2).getText().equalsIgnoreCase("In")}?
        '(' IDENT count=number child=comp ')'
        { $r = new In($count.r, $child.r); }
    ;
opt returns [Opt r]
@init { List<OptCall> oc = new ArrayList<OptCall>(); }
    : {input.LT(2).getText().equalsIgnoreCase("Opt")}?
        '(' IDENT (optcall { oc.add($optcall.r); })* ')'
        { $r = new Opt(oc); }
    ;
par returns [Par r]
@init { List<ParCall> pc = new ArrayList<ParCall>(); }
    : {input.LT(2).getText().equalsIgnoreCase("Par")}?
        '(' IDENT (parcall { pc.add($parcall.r); })* ')'
        { $r = new Par(pc); }
    ;
seq returns [Seq r]
@init { List<SeqCall> sc = new ArrayList<SeqCall>(); }
    : {input.LT(2).getText().equalsIgnoreCase("Seq")}?
        '(' IDENT (seqcall {sc.add($seqcall.r); })* ')'
        { $r = new Seq(sc); }
    ;

predicate returns [String r]
    : list_elem
        { $r = $list_elem.r; }
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
    : { input.LT(1).getText().equalsIgnoreCase("right") ||
        input.LT(1).getText().equalsIgnoreCase("left") ||
        input.LT(1).getText().equalsIgnoreCase("none") }?
        IDENT
    | fraction ;

fragment
in_out returns [boolean in, boolean out]
@init { $in=false; $out=false; }
    : {input.LT(1).getText().equalsIgnoreCase("in")}?
        IDENT { $in=true; }
    | {input.LT(1).getText().equalsIgnoreCase("out")}?
        IDENT { $out=true; }
    ;
fragment
bool returns [Boolean r]
    : {input.LT(1).getText().equalsIgnoreCase("true")}?
        IDENT { $r = Boolean.TRUE; }
    | {input.LT(1).getText().equalsIgnoreCase("false")}?
        IDENT { $r = Boolean.FALSE; }
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

// lexer rules
INT
    : ('0'..'9')+
    ;
STRING
    : '"' .* '"'
        { setText(getText().substring(1,getText().length()-1)); }
    ;
IDENT
    : ('A'..'Z'|'a'..'z'|'_')('A'..'Z'|'a'..'z'|'_'|'0'..'9')*
    ;

WS: ( ' ' | '\t' | '\r' | '\n' )+ { skip(); };

