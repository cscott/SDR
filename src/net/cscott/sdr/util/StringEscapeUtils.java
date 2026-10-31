package net.cscott.sdr.util;

/** A fixed cut-down version of
 * commons-lang's <code>StringEscapeUtils</code>.
 * @author C. Scott Ananian
 */
public abstract class StringEscapeUtils {
    /** Return the parameter as a properly-escaped Java string literal. */
    public static String escapeJava(String s) {
        StringBuilder sb = new StringBuilder();
            //sb.append('"');
            for (int i=0; i<s.length(); i++) {
                char c = s.charAt(i);
                if (c < 128 && Character.isJavaIdentifierPart(c))
                    sb.append(c); // ASCII and alphanumeric-ish
                else if (c==' ' || c=='/' || c=='-' || c=='+')
                    sb.append(c); // some specific safe characters
                else if (c<256) // this handles quotes, slashes, and other nasties
                    sb.append(String.format("\\%03o", (int) c));
                else // make the world safe for unicode
                    sb.append(String.format("\\"+"u%04x", (int) c));
            }
            //sb.append('"');
            return sb.toString();
    }
    /** Undo Java-style backslash escapes in the parameter, as
     *  commons-lang's <code>unescapeJava</code> did: unicode escapes and
     *  the usual one-letter escapes are decoded, and a backslash before
     *  any other character is dropped. */
    public static String unescapeJava(String s) {
        StringBuilder sb = new StringBuilder(s.length());
        for (int i=0; i<s.length(); i++) {
            char c = s.charAt(i);
            if (c != '\\' || i+1 == s.length()) {
                sb.append(c);
                continue;
            }
            c = s.charAt(++i);
            switch (c) {
            case 'b' -> sb.append('\b');
            case 'f' -> sb.append('\f');
            case 'n' -> sb.append('\n');
            case 'r' -> sb.append('\r');
            case 't' -> sb.append('\t');
            case 'u' -> {
                if (i+5 > s.length())
                    throw new IllegalArgumentException
                        ("Unable to parse unicode value: "+s.substring(i+1));
                String hex = s.substring(i+1, i+5);
                try {
                    sb.append((char) Integer.parseInt(hex, 16));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException
                        ("Unable to parse unicode value: "+hex);
                }
                i += 4;
            }
            default -> sb.append(c);
            }
        }
        return sb.toString();
    }
}
