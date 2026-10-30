package net.cscott.sdr;

import java.io.*;
import java.util.regex.*;

public class AddDoctestRunner {
    public static void main(String[] args) throws IOException {
        for (String filename : args)
            processFile(filename);
    }
    public static void processFile(String filename) throws IOException {
        String cs = "utf-8"; // char set
        // read entire contents of this file into a String
        Reader r = new InputStreamReader(new FileInputStream(filename), cs);
        String input = readFully(r);
        r.close();
        // add "@Doctests" before "public class"
        String repl = "@Doctests\n";
        if (Pattern.compile('^' + Pattern.quote(repl), Pattern.MULTILINE)
            .matcher(input).find()) {
            return; // replacement already present, no changes needed
        }
        Pattern p = Pattern.compile("^public class ", Pattern.MULTILINE);
        // special handling for package-info.java
        Matcher m = Pattern.compile("package-info.java$").matcher(filename);
        if (m.find()) {
            filename = m.replaceFirst("package_info.java");
            p = Pattern.compile("(/[*][*].*[*]/)\\s*^(package[^;]*;)",
                                Pattern.MULTILINE|Pattern.DOTALL);
            repl = "$2" +
                Matcher.quoteReplacement
                ("\nimport net.cscott.jdoctest.Doctests;\n") +
                "$1\n" +
                Matcher.quoteReplacement
                (repl + "abstract class package_info {}");
        } else {
            repl = Matcher.quoteReplacement(repl) + "$0";
        }
        String output = p.matcher(input).replaceFirst(repl);
        Writer w = new OutputStreamWriter(new FileOutputStream(filename), cs);
        w.write(output);
        w.close();
    }
    private static String readFully(Reader r) throws IOException {
	StringBuilder sb = new StringBuilder();
	char buf[] = new char[4096];
	int n;
	while (true) {
	    n = r.read(buf);
	    if (n < 0) return sb.toString(); // done
	    sb.append(buf, 0, n);
	}
    }
}
