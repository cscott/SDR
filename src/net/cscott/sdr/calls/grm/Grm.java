package net.cscott.sdr.calls.grm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.junit.runner.RunWith;

import net.cscott.jdoctest.JDoctestRunner;
import net.cscott.sdr.DevSettings;
import net.cscott.sdr.calls.Program;
import net.cscott.sdr.calls.ast.Apply;
import net.cscott.sdr.calls.transform.CallFileBuilder;
import net.cscott.sdr.util.Tools;

/**
 * This class contains inner classes creating an AST for the 'natural language'
 * grammar of square dance calls and concepts.  This AST is transformed into
 * a Java Speech grammar for the Sphinx speech recognition engine, which then
 * generates text strings.  The AST is also transformed into an ANTLR v3
 * grammar parsing those text strings, which creates {@link Apply} trees.
 * The raw rules from the call file need to be processed to remove
 * left recursion and to disambiguate using precedence levels.  These
 * processed rules are written out as the
 * {@link net.cscott.sdr.calls.lists.AllGrm} class, accessed via the
 * {@link Grm#grammar(Program)} method, and used to drive the
 * {@link CompletionEngine}.
 * 
 * @author C. Scott Ananian
 * @version $Id: Grm.java,v 1.3 2006-10-22 15:46:06 cananian Exp $
 */
@RunWith(value=JDoctestRunner.class)
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
    /*---- intern support: omit this in GWT version --- */
    /* Return a string describing the type of this Grm, for equality testing */
    protected abstract String getName();
    /* Return a list of operands for this Grm, for equality and hashcode */
    protected abstract List<Grm> getOperands();
    /* Create an new Grm like this, but with intern'ed operands */
    protected abstract Grm buildIntern();
    /* Return a Grm for which == is the same as equals()
     * @doc.test
     *  js> g1 = Grm.parse("a b|c d+ e* f?")
     *  a b|c d+ e* f?
     *  js> g2 = Grm.parse("a b|c d+ e* f?")
     *  a b|c d+ e* f?
     *  js> g1===g2
     *  false
     *  js> g1.intern()===g2.intern()
     *  true
     */
    public Grm intern() {
        if (!internMap.containsKey(this)) {
            Grm g = buildIntern();
            internMap.put(g, g);
        }
        return internMap.get(this);
    }
    private static final Map<Grm,Grm> internMap = new WeakHashMap<Grm,Grm>();
    public int hashCode() {
        if (hashCache == 0) {
            hashCache = getName().hashCode();
            for (Grm g : getOperands())
                hashCache = (hashCache*7)+g.hashCode();
        }
        return hashCache;
    }
    private transient int hashCache = 0;
    public boolean equals(Object o) {
        if (this==o) return true;
        if (!(o instanceof Grm)) return false;
        Grm g = (Grm) o;
        if (!this.getName().equals(g.getName())) return false;
        List<Grm> a = this.getOperands();
        List<Grm> b = g.getOperands();
        if (a.size()!=b.size()) return false;
        for (int i=0; i<a.size(); i++)
            if (a.get(i).intern() != b.get(i).intern())
                return false;
        return true;
    }
    /*--- end intern support --*/
    
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
        @Override
        protected Alt buildIntern() {
            List<Grm> l = new ArrayList<Grm>(this.alternates.size());
            for (Grm g : this.alternates)
                l.add(g.intern());
            return new Alt(l) {
                @Override
                public Alt intern() { return this; }
            };
        }
        @Override
        protected String getName() { return "Alt"; }
        @Override
        protected List<Grm> getOperands() { return this.alternates; }
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
        @Override
        protected Concat buildIntern() {
            List<Grm> l = new ArrayList<Grm>(this.sequence.size());
            for (Grm g : this.sequence)
                l.add(g.intern());
            return new Concat(l) {
                @Override
                public Concat intern() { return this; }
            };
        }
        @Override
        protected String getName() { return "Concat"; }
        @Override
        protected List<Grm> getOperands() { return this.sequence; }
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
        @Override
        protected Mult buildIntern() {
            return new Mult(this.operand.intern(), this.type) {
                @Override
                public Mult intern() { return this; }
            };
        }
        @Override
        protected String getName() { return this.type.name(); }
        @Override
        protected List<Grm> getOperands() { return Tools.l(this.operand); }
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
        @Override
        protected Nonterminal buildIntern() {
            return new Nonterminal
            (this.ruleName.intern(),
             (this.prettyName==null) ? null : this.prettyName.intern(),
             this.param) {
                @Override
                public Nonterminal intern() { return this; }
            };
        }
        @Override
        protected String getName() { return "NT/"+this.ruleName+"/"+this.param; }
        @Override
        protected List<Grm> getOperands() { return Tools.l(); }
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
        @Override
        protected Terminal buildIntern() {
            return new Terminal(this.literal.intern()) {
                @Override
                public Terminal intern() { return this; }
            };
        }
        @Override
        protected String getName() { return "T/"+this.literal; }
        @Override
        protected List<Grm> getOperands() { return Tools.l(); }
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
     * Return a natural language grammar for the given square dance program.
     * The grammar is expressed as a map from nonterminal names to
     * {@link Grm}s. The start production is a nonterminal named 'start'.
     * This grammar is generated by the {@link EmitJava} class,
     * invoked from {@link BuildGrammars}.*/
    @SuppressWarnings("unchecked")
    public static Map<String,Grm> grammar(Program p) {
        if (p!=Program.C4 && DevSettings.ONLY_C4_GRAMMAR) p=Program.C4;
        // use reflection to avoid a bootstrapping problem.
        try {
            return (Map<String,Grm>)
                Class.forName("net.cscott.sdr.calls.lists.AllGrm")
                .getField(p.name()).get(null);
        } catch (ClassNotFoundException e) {
        } catch (SecurityException e) {
        } catch (NoSuchFieldException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        }
        assert false : "grammars not generated yet?";
        return null;
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
