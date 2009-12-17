package net.cscott.sdr.calls.grm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.regex.Pattern;

/**
 * Post-process an ANTLR-generated grammar to avoid "code too large" errors.
 */
public class AntlrPostProcessor {

    /**
     * Search-and-replace an ANTLR-generated grammar to move large static
     * initializers into their own class files, to avoid "code too large"
     * errors.
     */
    public static void main(String[] args) throws IOException {
        File f = new File(args[0].replaceFirst("\\.g$", "Parser.java"));
        Reader r=new InputStreamReader(new FileInputStream(f), "utf-8");
        String contents = readAll(r);
        // ok, look for the big initializers
        String nContents = p.matcher(contents).replaceAll(replacement);
        if (!contents.equals(nContents)) {
            System.err.println("Rewriting: "+f.getName());
            Writer fw = new OutputStreamWriter(new FileOutputStream(f), "utf-8");
            fw.write(nContents);
            fw.close();
        }
    }
    private static Pattern p = Pattern.compile
        ("^(\\s*?)static final String\\[\\] (DFA[0-9]+_transitionS) = \\{(.*?)\\};$",
         Pattern.MULTILINE | Pattern.DOTALL);
    private static String replacement =
        "$1private static class $2Class {\n"+
        "$1    static final String[] value = {$3};\n" +
        "$1}\n"+
        "$1static final String[] $2 = $2Class.value;";

    private static String readAll(Reader r) throws IOException {
        StringBuilder sb = new StringBuilder();
        char[] buf = new char[65536];
        while(true) {
            int n = r.read(buf);
            if (n<0) break;
            sb.append(buf, 0, n);
        }
        return sb.toString();
    }
}
