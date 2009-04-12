package net.cscott.sdr.calls.grm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.cscott.sdr.calls.ast.Apply;
import net.cscott.sdr.calls.transform.CallFileBuilder;

/**
 * This class contains inner classes creating an AST for the 'natural language'
 * grammar of square dance calls and concepts.  This AST is transformed into
 * a Java Speech grammar for the Sphinx speech recognition engine, which then
 * generates text strings.  The AST is also transformed into an ANTLR v3
 * grammar parsing those text strings, which creates {@link Apply} trees.
 * The raw rules from the call file need to be processed to remove
 * left recursion and to disambiguate using precedence levels.  These
 * processed rules are written out as subclasses of {@link GrmDB} and used
 * to drive the {@link CompletionEngine}.
 * 
 * @author C. Scott Ananian
 * @version $Id: Grm.java,v 1.3 2006-10-22 15:46:06 cananian Exp $
 */
public abstract class Grm {
    public abstract int precedence();
    public abstract <T> T accept(GrmVisitor<T> v);
    public final String toString() {
        return accept(new ToStringVisitor());
    }
    /** Return a Java phrase to reconstruct this Grm. */
    public final String repr() {
        StringBuilder sb = new StringBuilder();
        this.repr(sb);
        return sb.toString();
    }
    protected abstract void repr(StringBuilder sb);
    
    /** Alternation: a|b. */
    public static class Alt extends Grm {
        public final List<Grm> alternates;
        public Alt(List<Grm> alternates) {
            this.alternates = Collections.unmodifiableList
            (Arrays.asList(alternates.toArray(new Grm[alternates.size()])));
        }
        public int precedence() { return 0; }
        @Override
        public <T> T accept(GrmVisitor<T> v) {
            return v.visit(this);
        }
        public void repr(StringBuilder sb) {
            sb.append("new Grm.Alt(Tools.<Grm>l(");
            for (Iterator<Grm> it = this.alternates.iterator(); ; ) {
                it.next().repr(sb);
                if (it.hasNext())
                    sb.append(",");
                else
                    break;
            }
            sb.append("))");
        }
    }
    /** Concatanation: a b. */
    public static class Concat extends Grm {
        public final List<Grm> sequence;
        public Concat(List<Grm> sequence) {
            this.sequence = Collections.unmodifiableList
            (Arrays.asList(sequence.toArray(new Grm[sequence.size()])));
        }
        public int precedence() { return 1; }
        @Override
        public <T> T accept(GrmVisitor<T> v) {
            return v.visit(this);
        }
        public void repr(StringBuilder sb) {
            sb.append("new Grm.Concat(Tools.<Grm>l(");
            for (Iterator<Grm> it = this.sequence.iterator(); ; ) {
                it.next().repr(sb);
                if (it.hasNext())
                    sb.append(",");
                else
                    break;
            }
            sb.append("))");
        }
    }
    /** Multiplicity marker: a*, a+, or a?. */
    public static class Mult extends Grm {
        public enum Type {
            STAR('*'), PLUS('+'), QUESTION('?');
            public final char value;
            Type(char value) { this.value = value; }
            public String toString() { return ""+value; }
        };
        public final Grm operand;
        public final Type type;
        public Mult(Grm operand, Type type) {
            this.operand=operand; this.type=type;
        }
        public int precedence() { return 2; }
        @Override
        public <T> T accept(GrmVisitor<T> v) {
            return v.visit(this);
        }
        public void repr(StringBuilder sb) {
            sb.append("new Grm.Mult(");
            this.operand.repr(sb);
            sb.append(",Grm.Mult.Type.");
            sb.append(this.type.name());
            sb.append(")");
        }
    }
    /** A nonterminal reference to an external rule. */
    public static class Nonterminal extends Grm {
        // Name of the grammar rule referenced.
        public final String ruleName;
        // "Pretty" name, for display to the user during call completion
        // May be null to indicate that you shouldn't show this to the user!
        public final String prettyName;
        // If not -1, which parameter should get the value of this rule
        public final int param;
        public Nonterminal(String ruleName, int param) {
            this(ruleName, ruleName, param);
        }
        public Nonterminal(String ruleName, String prettyName, int param) {
            this.ruleName=ruleName;
            this.prettyName=prettyName;
            this.param=param;
        }
        public int precedence() { return 3; }
        @Override
        public <T> T accept(GrmVisitor<T> v) {
            return v.visit(this);
        }
        public void repr(StringBuilder sb) {
            sb.append("new Grm.Nonterminal(");
            sb.append(str_escape(this.ruleName));
            sb.append(",");
            if (!this.ruleName.equals(this.prettyName)) {
                sb.append((this.prettyName==null) ? "null" :
                          str_escape(this.prettyName));
                sb.append(",");
            }
            sb.append(this.param);
            sb.append(")");
        }
        // we don't use the prettyName in the hashCode or equality computations
        public int hashCode() { return ruleName.hashCode() * (param+2); }
        public boolean equals(Object o) {
            if (!(o instanceof Nonterminal)) return false;
            Nonterminal nt = (Nonterminal) o;
            return this.ruleName.equals(nt.ruleName) && this.param == nt.param;
        }
    }
    /** A grammar terminal: a string literal to match. */
    public static class Terminal extends Grm {
        public final String literal;
        public Terminal(String literal) {
            this.literal=literal;
        }
        public int precedence() { return 3; }
        @Override
        public <T> T accept(GrmVisitor<T> v) {
            return v.visit(this);
        }
        public void repr(StringBuilder sb) {
            sb.append("new Grm.Terminal(");
            sb.append(str_escape(this.literal));
            sb.append(")");
        }
        public int hashCode() { return literal.hashCode() + 42; }
        public boolean equals(Object o) {
            if (!(o instanceof Terminal)) return false;
            Terminal t = (Terminal) o;
            return this.literal.equals(t.literal);
        }
    }

    // helper functions on Grm
    /** Make a {@link Grm} which will recognize the given
     * sequence of terminal symbols. */
    public static Grm mkGrm(String... terminals) {
        List<Grm> l = new ArrayList<Grm>(terminals.length);
        for (String s : terminals)
            l.add(new Terminal(s));
        return new Concat(l);
    }
    /**
     * Parse a string representing a {@link Grm}.  Parameter
     * references must be numerical. 
     * @throws IllegalArgumentException if the rule is malformed.
     * @doc.test Successful parse:
     *  js> Grm.parse("foo bar|bat? baz")
     *  foo bar|bat? baz
     * @doc.test Unsuccessful parse:
     *  js> try {
     *    >   Grm.parse("[abc]")
     *    > } catch (e) { print (e.javaException) }
     *  java.lang.IllegalArgumentException: bad grammar rule: [abc]
     */
    public static Grm parse(String rule) {
        try {
	    return CallFileBuilder.parseGrm(rule);
        } catch (Exception e) {
            throw new IllegalArgumentException("bad grammar rule: "+rule);
        }
    }
    /** Return the parameter as a properly-escaped Java string literal. */
    static String str_escape(String s) {
        StringBuilder sb = new StringBuilder();
        sb.append('"');
        for (int i=0; i<s.length(); i++) {
            char c = s.charAt(i);
            if (c < 128 && Character.isJavaIdentifierPart(c))
                sb.append(c); // ASCII and alphanumeric-ish
            else if (c<256) // this handles quotes, slashes, and other nasties
                sb.append(String.format("\\\\%03o", (int) c));
            else // make the world safe for unicode
                sb.append(String.format("\\\\"+"u%04x", (int) c));
        }
        sb.append('"');
        return sb.toString();
    }
}
