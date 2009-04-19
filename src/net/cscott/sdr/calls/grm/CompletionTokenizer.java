package net.cscott.sdr.calls.grm;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        List<Token> result = new ArrayList<Token>();
        Matcher m = TOKPAT.matcher(input.replaceFirst("^\\s+", ""));
        while (m.lookingAt()) {
            if (m.group(1) != null) {
                result.add(new Token(m.group(1), TokenType.FRACTION));
            } else {
                assert m.group(2) != null;
                TokenType tt = (m.group(3).length()>0) ? TokenType.STRING :
                    TokenType.FRAGMENT;
                result.add(new Token(m.group(2), tt));
            }
            input = input.substring(m.end());
            m = TOKPAT.matcher(input);
        }
        return result;
    }
    /** Match tokens and trailing space.  Note that the fraction pattern
     *  precedes the word pattern, since digits count as word characters. */
    private static final Pattern TOKPAT = Pattern.compile
      ("^(?:(\\d+(?:\\s+\\d+\\s*/\\s*\\d+)?)|(\\w+))(\\s*)");
}
