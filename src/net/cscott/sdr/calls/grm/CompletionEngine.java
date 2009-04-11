package net.cscott.sdr.calls.grm;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import net.cscott.sdr.calls.Program;
import net.cscott.sdr.calls.grm.Grm.Alt;
import net.cscott.sdr.calls.grm.Grm.Concat;
import net.cscott.sdr.calls.grm.Grm.Mult;
import net.cscott.sdr.calls.grm.Grm.Nonterminal;
import net.cscott.sdr.calls.grm.Grm.Terminal;
import net.cscott.sdr.util.LL;

/**
 * Uses a {@link GrmDB} to compute possible completions for a partially-input
 * call.
 */
public class CompletionEngine {
    public static class Completions {
        List<String> options;
        List<Integer> lastState;
    }
    public static Completions complete(String input, int limit) {
        return null;//complete(input, new ArrayList<Integer>());
    }

    private enum TokenType {
        FRAGMENT, STRING, FRACTION;
    }
    private static class Token {
        final String text;
        final TokenType type;
        Token(String text, TokenType type) { this.text=text; this.type=type; }
        boolean matches(String literal) {
            switch(type) {
            case STRING:
                return literal.equalsIgnoreCase(text);
            case FRAGMENT:
                return literal.toLowerCase().startsWith(text.toLowerCase());
            case FRACTION:
                return false;
            }
            assert false : "unmatched input";
            return false;
        }
        public String toString() {
            return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE)
                .append("text",this.text)
                .append("type",this.type)
                .toString();
        }
    }
    public static void main(String[] args) {
        LL<Token> partialInput = LL.create
        /*
        (new Token("trade", TokenType.STRING),
         new Token("a", TokenType.FRAGMENT));
         */
        /*
        (new Token("tra", TokenType.FRAGMENT));
        */
        /*
        (new Token("trade", TokenType.STRING),
                new Token("and", TokenType.STRING),
                //new Token("roll", TokenType.STRING),
                new Token("r", TokenType.FRAGMENT));
         */
        (new Token("sq", TokenType.FRAGMENT));
        CompleteVisitor cv = new CompleteVisitor(GrmDB.dbFor(Program.C4), null);
        boolean found;
        LL<Integer> lastState = LL.NULL();
        do {
        CompleteState cs = new CompleteState(partialInput, lastState,
                                             LL.<Integer>NULL(),
                                             LL.<String>NULL(), false);
        cv.cs = cs;
        Grm g = cv.rules.grammar().get("start");
        found = g.accept(cv);
        System.err.println("FOUND: "+found);
        System.err.println("STATE: "+cv.cs.nextState);
        System.err.println("COMPLETION: "+cv.cs.completion.reverse());
        lastState = cv.cs.nextState;
        } while(found);
    }

    private static class CompleteState implements Cloneable {
        LL<Token> partialInput;
        LL<Integer> lastState;
        LL<Integer> nextState;
        LL<String> completion;
        boolean matchedTerminal;
        CompleteState(LL<Token> partialInput,
                      LL<Integer> lastState, LL<Integer> nextState,
                      LL<String> completion, boolean matchedTerminal) {
            this.partialInput = partialInput;
            this.lastState = lastState;
            this.nextState = nextState;
            this.completion = completion;
            this.matchedTerminal = matchedTerminal;
        }
        public CompleteState clone() {
            try { return (CompleteState) super.clone(); }
            catch (CloneNotSupportedException e) {
                assert false : "can never happen";
                return null;
            }
        }
        void popInput() {
            if (!partialInput.isEmpty())
                partialInput = partialInput.pop();
        }
        int popState() {
            if (lastState.isEmpty()) return 0;
            int v = lastState.head;
            lastState = lastState.pop();
            // increment the state by one if this is the last state, that
            // ensures that our invocation returns the 'next' completion, not
            // the same completion as last time.
            return (lastState.isEmpty()) ? (v+1) : v;
        }
        void pushState(int v) {
            this.nextState = this.nextState.push(v);
        }
        void pushCompletion(String s) {
            this.completion = this.completion.push(s);
        }
    }
    static final class CompleteVisitor extends GrmVisitor<Boolean> {
        final GrmDB rules;
        CompleteState cs;
        CompleteVisitor(GrmDB rules, CompleteState cs) {
            this.rules = rules;
            this.cs = cs;
        }

        @Override
        public Boolean visit(Alt alt) {
            // try choices in order
            int i=cs.popState();
            CompleteState saved = this.cs.clone();
            saved.lastState = LL.NULL(); // if we fail, don't try to use the
                                         // rest of the saved state.
            for (Grm g : alt.alternates.subList(i, alt.alternates.size())) {
                if (g.accept(this)) {
                    cs.pushState(i); // record where we were
                    return true; // got one!
                }
                this.cs = saved.clone();
                i++;
            }
            // none matched.
            return false;
        }
        @Override
        public Boolean visit(Concat concat) {
            CompleteState saved = this.cs.clone();
            List<Integer> n = new ArrayList<Integer>();
            assert this.cs.nextState.isEmpty();
            // no choices here, either we match or we don't.
            AGAIN: while (true) {
                for (Grm g : concat.sequence) {
                    if (g.accept(this)) {
                        // add nextState to n; clear n for next g
                        for (Integer i: this.cs.nextState)
                            n.add(i);
                        cs.nextState = LL.NULL();
                    } else {
                        this.cs = saved.clone();
                        this.cs.lastState = LL.create(n);
                        n.clear();
                        if (!this.cs.lastState.isEmpty())
                            // retry a different alternative
                            continue AGAIN;
                        // no chance this will work
                        return false;
                    }
                }
                // well, i guess this worked!
                this.cs.nextState = LL.create(n);
                return true;
            }
        }
        //   and what about a ( b c? ) c
        //   matched against input "abc"
        //   currently we'll greedily try matching the question mark
        //   and then fail when we get back to the top-level concat.
        // XXX: when concat fails and there's a lastState stack we should
        //      retry with that stack
        @Override
        public Boolean visit(Mult mult) { return visit(mult, cs.partialInput.isEmpty()); }
        public Boolean visit(Mult mult, boolean doneOnce) {
            CompleteState saved = this.cs.clone();
            saved.lastState = LL.NULL();
            if (mult.type==Mult.Type.PLUS) {
                // match one, and then fall through to * processing
                if (!mult.operand.accept(this)) {
                    this.cs = saved; // restore old state
                    return false;
                }
            }
            int i = cs.popState();
            saved.popState(); // pop from the saved state, too.
            switch (i) {
            case 0: // try no match
                cs.pushState(i);
                return true;
            case 1: // okay, that didn't work.  Try matching once.
                if (mult.operand.accept(this)) {
                    if (mult.type==Mult.Type.QUESTION) {
                        cs.pushState(i); // record where we were
                        return true; // we're done! got one!
                    }
                    // plus or mult: recurse to match again
                    // but ONLY if we either have: unmatched partialInput OR
                    // we've only done this *once*
                    if ((!cs.partialInput.isEmpty()) || (!doneOnce)) {
                        assert this.cs.nextState.isEmpty();
                        if (!visit(mult, cs.partialInput.isEmpty())) {
                            this.cs = saved;
                            return false;
                        }
                    }
                    cs.pushState(i); // record where we were
                    return true;
                }
                // fall thru
            default: // no way to match this
                this.cs = saved;
                return false;
            }
        }
        @Override
        public Boolean visit(Nonterminal nonterm) {
            // special match for <digit>, <EOF> (others?)
            if (nonterm.ruleName.equals("EOF"))
                // <EOF> matches iff we've grabbed all the partial input.
                return cs.partialInput.isEmpty();
            // if "no terminals past partialInput yet" then we'll grab the
            // nt from the GrmDB and recurse; otherwise we'll return
            // "<"+nonterm.prettyname+">" in the completion string & true.
            if (cs.matchedTerminal) {
                cs.pushCompletion("<"+nonterm.ruleName+">"); //XXXpretty
                return true;
            }
            Grm g = rules.grammar().get(nonterm.ruleName);
            if (g==null) return false; // XXX MISSING RULE
            return g.accept(this);
        }
        @Override
        public Boolean visit(Terminal term) {
            // do we match the nonterminal, or not?
            if (cs.partialInput.isEmpty() ||
                cs.partialInput.head.matches(term.literal)) {
                // okay, we match.
                if (cs.partialInput.isEmpty()) cs.matchedTerminal=true;
                cs.popInput(); // safe even if cs.partialInput.isEmpty()
                cs.pushCompletion(term.literal);
                return true;
            }
            // not a match
            return false;
        }
    }
}
