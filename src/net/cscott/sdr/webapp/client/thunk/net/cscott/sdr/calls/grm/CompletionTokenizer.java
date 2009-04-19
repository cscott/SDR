package net.cscott.sdr.calls.grm;

import java.util.ArrayList;
import java.util.List;

import net.cscott.sdr.calls.grm.CompletionEngine.Token;
import net.cscott.sdr.calls.grm.CompletionEngine.Token.TokenType;

/** The tokenizer for CompletionEngine is broken out here into a separate type
 * to allow the GWT implementation to replace it with a version which doesn't
 * use regular expressions.
 * @author C. Scott Ananian
 */
abstract class CompletionTokenizer {
    private CompletionTokenizer() { /* don't ever create an instance */ }
    /** Return a list of {@link Token}s corresponding to the input string. */
    public static List<Token> tokenize(String input) {
        boolean lastFrag = !input.endsWith(" ");
        String[] bits = input.split("\\s+");
        List<Token> result = new ArrayList<Token>(bits.length);
        for (int i=0; i<bits.length; i++) {
            if (bits[i].matches("\\d+(/\\d+)?"))
                result.add(new Token(bits[i], TokenType.FRACTION));
            else if (i==(bits.length-1) && lastFrag)
                result.add(new Token(bits[i], TokenType.FRAGMENT));
            else
                result.add(new Token(bits[i], TokenType.STRING));
        }
        // XXX post-process to join bits of fractions
        return result;
    }
}
