package net.cscott.sdr.calls.grm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.regex.Matcher;
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
        // ok, look for the big initializers and break them up
        String nContents = replaceBigInitializers(contents);
        if (!contents.equals(nContents)) {
            System.err.println("Rewriting: "+f.getName());
            Writer fw = new OutputStreamWriter(new FileOutputStream(f), "utf-8");
            fw.write(nContents);
            fw.close();
        }
    }
    /* this size limit is an underestimate, since each piece is string-escaped
     * and each element contains leading whitespace.  still, this is a
     * reasonable limit. */
    private static final int STRING_SIZE_LIMIT = 65536;
    private static String replaceBigInitializers(String contents) {
        Matcher m = p.matcher(contents);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, "");
            String ind = m.group(1);
            String fld = m.group(2);
            String value = m.group(3);
            /* now split the string array into smaller pieces */
            String[] pieces = elemSep.split(value);
            StringBuilder buf = new StringBuilder();
            int n = 0;
            for (int i=0; i<pieces.length; i++) {
                buf.append(pieces[i]);
                if (buf.length() > STRING_SIZE_LIMIT || i==(pieces.length-1)) {
                    /* emit piece */
                    sb.append(ind+"private static class "+fld+"Class"+n+" {\n");
                    sb.append(ind+"    static final String[] value = {");
                    sb.append(buf);
                    sb.append("};\n");
                    sb.append(ind+"}\n");
                    buf.setLength(0);
                    n++;
                } else {
                    buf.append(",");
                }
            }
            /* now put all the pieces together */
            sb.append(ind+"static final String[] "+fld+";\n");
            sb.append(ind+"static {\n");
            sb.append(ind+"    java.util.List<String> tmp = ");
            sb.append(               "new java.util.ArrayList<String>();\n");
            for (int i=0; i<n; i++) {
                sb.append(ind+"    tmp.addAll(java.util.Arrays.asList("+
                                     fld+"Class"+i+".value));\n");
            }
            sb.append(ind+"    "+fld+" = ");
            sb.append(               "tmp.toArray(new String[tmp.size()]);\n");
            sb.append(ind+"}");
        }
        m.appendTail(sb);
        return sb.toString();
    }
    private static Pattern p = Pattern.compile
        ("^(\\s*?)static final String\\[\\] (DFA[0-9]+_transitionS) = \\{(.*?)\\};$",
         Pattern.MULTILINE | Pattern.DOTALL);
    private static Pattern elemSep = Pattern.compile
        ("(?<=\"),$", Pattern.MULTILINE);

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
