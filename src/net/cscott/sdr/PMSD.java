package net.cscott.sdr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import net.cscott.sdr.calls.BadCallException;
import net.cscott.sdr.calls.CallDB;
import net.cscott.sdr.calls.DanceProgram;
import net.cscott.sdr.calls.DanceState;
import net.cscott.sdr.calls.Formation;
import net.cscott.sdr.calls.Program;
import net.cscott.sdr.calls.ast.Comp;
import net.cscott.sdr.calls.ast.Seq;
import net.cscott.sdr.calls.grm.CompletionEngine;
import net.cscott.sdr.calls.transform.Evaluator;
import net.cscott.sdr.util.ListUtils;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.tools.shell.Global;

/**
 * Poor Man's SD is a very simple text-based front-end for debugging
 * and testing.
 * @author C. Scott Ananian
 * @doc.test Perform basic calls
 *  js> PMSD.scrub(PMSD.runTest("<stdio>",
 *    >                         "Comment or title lines at the top are ignored",
 *    >                         "sdr> /setFormation(Formation.SQUARED_SET)",
 *    >                         "sdr> u turn back",
 *    >                         "sdr> /program = Program.PLUS; setFormation(Formation.SQUARED_SET)",
 *    >                         "sdr> do half of a u turn back"))
 *  |sdr> /setFormation(Formation.SQUARED_SET)
 *  ||      3Gv  3Bv
 *  || 
 *  || 4B>            2G<
 *  || 
 *  || 4G>            2B<
 *  || 
 *  ||      1B^  1G^
 *  |sdr> u turn back
 *  ||      3G^  3B^
 *  || 
 *  || 4B<            2G>
 *  || 
 *  || 4G<            2B>
 *  || 
 *  ||      1Bv  1Gv
 *  |sdr> /program = Program.PLUS; setFormation(Formation.SQUARED_SET)
 *  ||      3Gv  3Bv
 *  || 
 *  || 4B>            2G<
 *  || 
 *  || 4G>            2B<
 *  || 
 *  ||      1B^  1G^
 *  |sdr> do half of a u turn back
 *  ||      3G>  3B<
 *  || 
 *  || 4Bv            2Gv
 *  || 
 *  || 4G^            2B^
 *  || 
 *  ||      1B>  1G<
 * @doc.test Special slash commands to access dance state:
 *  js> PMSD.scrub(PMSD.runTest("<stdio>", "sdr> /printFormation"))
 *  |sdr> /printFormation
 *  ||      3Gv  3Bv
 *  || 
 *  || 4B>            2G<
 *  || 
 *  || 4G>            2B<
 *  || 
 *  ||      1B^  1G^
 *  js> PMSD.scrub(PMSD.runTest("<stdio>", "sdr> /ds.currentTime()"))
 *  |sdr> /ds.currentTime()
 *  |0/1
 * @doc.test Slash commands can actually be any javascript statement:
 *  js> PMSD.scrub(PMSD.runTest("<stdio>", "sdr> /1+2"))
 *  |sdr> /1+2
 *  |3
 *  js> PMSD.scrub(PMSD.runTest("<stdio>", "sdr> /function f(x) { return x*2 } ; f(4)"))
 *  |sdr> /function f(x) { return x*2 } ; f(4)
 *  |8
 * @doc.test We can even nest invocations of PMSD:
 *  js> PMSD.scrub(PMSD.runTest("<outer>",
 *    >                         "sdr> /const PMSD=net.cscott.sdr.PMSD;",
 *    >                         "sdr> /PMSD.scrub(PMSD.runTest('<inner>',"+
 *    >                                                       "'sdr> /\"whee!\"'))"))
 *  |sdr> /const PMSD=net.cscott.sdr.PMSD;
 *  |sdr> /PMSD.scrub(PMSD.runTest('<inner>','sdr> /"whee!"'))
 *  ||sdr> /"whee!"
 *  ||whee!
 */
public class PMSD {
    private PMSD() {}
    /** Class holding properties accessible from the {@link PMSD} front-end. */
    public static class State extends ScriptableObject {
        // rhino bookkeeping.
        public State() {}
        @Override
        public String getClassName() { return "State"; }

        // private/internal state
        DanceState ds = new DanceState(new DanceProgram(Program.PLUS), Formation.SQUARED_SET);
        boolean _isDone = false;

        // javascript api.
        public Object jsGet_ds() {
            return Context.javaToJS(ds, this);
        }
        public Object jsGet_exit() {
            // abuse the getter mechanism by using it to perform a side-effect
            _isDone = true;
            return Context.getUndefinedValue();
        }
        public Object jsGet_program() {
            return Context.javaToJS(ds.dance.getProgram(), this);
            //return ds.dance.getProgram().toString();
        }
        public void jsSet_program(Object val) {
            Program p;
            if (val instanceof String)
                p = Program.valueOf((String)val);
            else
                p = (Program) Context.jsToJava(val, Program.class);
            if (p==ds.dance.getProgram()) return;
            ds = new DanceState(new DanceProgram(p), ds.currentFormation());
        }
        public void jsSet_formation(Object val) {
            Formation f = (Formation) Context.jsToJava(val, Formation.class);
            ds = new DanceState(ds.dance, f);
        }
        // this is just a workaround to prevent javascript from echoing the
        // ugly toString() value of the formation.
        public String jsFunction_setFormation(Object val) {
            jsSet_formation(val);
            return jsGet_printFormation();
        }
        public String jsGet_printFormation() {
            return ds.currentFormation().toStringDiagram("| ");
        }

	public String jsFunction_runTest(String testName) throws IOException {
            List<String> testCase =
                readLines(PMSD.class.getResourceAsStream("tests/"+testName));
            if (testCase==null)
		return "* " + testName + " not found";
            // execute it!
            return runTest(testName, testCase);
	}

	public String jsGet_runAllTests() {
	    try {
		return runAllTests();
	    } catch (IOException e) {
		return e.getMessage();
	    }
	}

        // special helper to list test cases
        public String jsFunction_listTests(String basedir) {
            StringBuffer sb = new StringBuffer();
            List<String> files = new ArrayList<String>
                (Arrays.asList(new File(basedir).list()));
            Collections.sort(files);
            for (String f : files) {
                if (f.endsWith("~")) continue; // very simple filter
                if (f.equals("index")) continue; // don't include self
                sb.append(f);
                sb.append("\n");
            }
            return sb.toString();
        }
    }

    /** Abstract class to allow using repl look with console or file input. */
    static abstract class ReaderWriter {
        abstract String sourceName();
        abstract String readLine(State s, String prompt) throws IOException;
        abstract PrintWriter writer();
    }
    /** Run all tests from the {@code net.cscott.sdr.tests.index} resource. */
    public static String runAllTests() throws IOException {
        List<String> failedTests = new ArrayList<String>();
        List<String> resources =
            readLines(PMSD.class.getResourceAsStream("tests/index"));
        if (resources==null)
            throw new IOException("index resource not found");
        boolean seenSDR = false;
        for (String resource : resources) {
            resource = resource.trim();
            if (resource.startsWith("sdr>")) { seenSDR=true; continue; }
            if (resource.length()==0 || !seenSDR) continue;
            // okay, read the given test case
            List<String> testCase =
                readLines(PMSD.class.getResourceAsStream("tests/"+resource));
            if (testCase==null) {
                failedTests.add(" "+resource+" not found");
                continue;
            }
            // execute it!
            String testResult = runTest(resource, testCase);
            // resplit and compare.
            int i = 0, mismatch = -1;
            for ( ; i < testCase.size(); i++)
                // we ignore input in the test case before the first sdr> prompt
                if (testCase.get(i).trim().startsWith("sdr>"))
                    break;
            for (String outLine: testResult.split("(\\r\\n?|\\n)")) {
                String inLine = (i < testCase.size()) ? testCase.get(i) : "";
                if (!inLine.trim().equals(outLine.trim())) {
                    mismatch = i;
                    break;
                }
                i++;
            }
            for ( ; mismatch<0 && i < testCase.size(); i++)
                if (testCase.get(i).trim().length() != 0)
                    mismatch = i;

            if (mismatch != -1)
                failedTests.add(" "+resource+" at line "+(mismatch+1));
        }
        if (failedTests.isEmpty()) return "";
        // oops, something failed
        return "FAILED TESTS:\n"+ListUtils.join(failedTests, "\n");
    }
    /** Read a file fully, returning as an list of lines. */
    private static List<String> readLines(InputStream is) throws IOException {
        if (is == null) return null; // pass up the error
        List<String> result = new ArrayList<String>();
        Reader r = new InputStreamReader(is, "utf-8");
        BufferedReader br = new BufferedReader(r);
        while (true) {
            String line = br.readLine();
            if (line==null) break;
            result.add(line);
        }
        br.close();
        return result;
    }
    /** Run a test transcript, returning the output. Helper class for use from
     *  JavaScript. */
    public static String runTest(String sourceName, String... transcript) {
        return runTest(sourceName, Arrays.asList(transcript));
    }
    /** Helper function from doctests to tweak output of {@link #runTest}. */
    public static String scrub(String input) {
        StringBuffer result = new StringBuffer();
        boolean seenSDR = false;
        for (String line : input.split("(\\r\\n?|\\n)")) {
            // drop lines before the first sdr>
            if (line.trim().startsWith("sdr>"))
                seenSDR = true;
            if (seenSDR) {
                result.append("|");
                result.append(line);
                result.append("\n");
            }
        }
        return result.toString().trim();
    }
    /** Run a test transcript, returning the output. */
    public static String runTest(final String sourceName, List<String> lines) {
        StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        // very simple regexp to find input lines
        List<String> input = new ArrayList<String>();
        for (String line: lines) {
            if (line.startsWith("sdr> ") || line.startsWith("   > "))
                input.add(line.substring(5));
        }
        final Iterator<String> inputIterator = input.iterator();
        ReaderWriter rw = new ReaderWriter() {
            @Override
            String readLine(State s, String prompt) throws IOException {
                if (!inputIterator.hasNext()) return null;
                String line = inputIterator.next();
                pw.print(prompt);
                pw.println(line);
                return line;
            }
            @Override
            String sourceName() { return sourceName; }
            @Override
            PrintWriter writer() { return pw; }
        };
        try {
            repl(rw);
        } catch (IOException ioe) {
            pw.println("UNEXPECTED ERROR: "+ioe.getMessage());
        }
        pw.flush();
        return sw.toString().trim();
    }

    /** Console front end entry point. */
    public static void main(String[] args) throws IOException {
        final PrintWriter pw = new PrintWriter(System.out, true);
        if (args.length > 0) {
            // ooh, got an argument!
            String result =
                runTest(args[0], readLines(new FileInputStream(args[0])));
            pw.print(result);
            pw.flush();
            return;
        }
        // console i/o
        pw.println("Welcome to "+Version.PACKAGE_STRING);
        repl(new ReaderWriter() {
            jline.ConsoleReader cr = null;
            @Override
            String sourceName() { return "<stdin>"; }
            @Override
            String readLine(State state, String prompt) throws IOException {
                if (cr==null) {
                    // initialize JLine!
                    cr = new jline.ConsoleReader();
                    cr.addCompletor(new SDRCallCompletor(state));
                    cr.addCompletor(new JavascriptCompletor(state));
                }
                return cr.readLine(prompt);
            }
            @Override
            PrintWriter writer() { return pw; }
            });
    }
    /** The main read-eval-print loop. */
    public static void repl(ReaderWriter rw) throws IOException {
        PrintWriter pw = rw.writer();
        // initialize Rhino
        Context cx = Context.enter();
        try {
            Global global = new Global();
            global.init(cx);
            cx.evaluateString(global, "importPackage(net.cscott.sdr.calls)",
                              "<init>", 0, null);
            // add the 'State' object to the scope chain
            ScriptableObject.defineClass(global, State.class);
            State s = (State) cx.newObject(global, "State");
            s.setParentScope(global);
            // main loop
            int lineNum = 1;
            while (!s._isDone) {
                final int initialLineNum = lineNum;
                String line = rw.readLine(s, "sdr> "); lineNum++;
                if (line==null) break;
                if (line.startsWith("/")) {
                    line = line.substring(1);
                    // accumulate until we've got a complete javascript statement
                    while (!cx.stringIsCompilableUnit(line)) {
                        line = line + "\n" + rw.readLine(s, "   > ");
                        lineNum++;
                    }
                    try {
                        Object result = cx.evaluateString(s, line,
                                                          rw.sourceName(),
                                                          initialLineNum, null);
                        if (result != Context.getUndefinedValue() &&
                                !(result instanceof Function &&
                                        line.trim().startsWith("function"))) {
                            pw.println(Context.toString(result));
                        }
                    } catch (RhinoException rex) {
                        pw.println("* Javascript exception: "+rex.getMessage());
                    }
                } else {
                    // accumulate while there's a trailing backslash
                    while (line.trim().endsWith("\\")) {
                        line = line.replaceFirst("\\\\", "") +
                               rw.readLine(s, "   > ");
                        lineNum++;
                    }
                    try {
                        Comp c = new Seq(CallDB.INSTANCE.parse(s.ds.dance.getProgram(), line));
                        Evaluator.breathedEval(s.ds.currentFormation(), c).evaluateAll(s.ds);
                        pw.println(s.ds.currentFormation().toStringDiagram("| "));
                    } catch (BadCallException bce) {
                        pw.println("* "+bce.getMessage());
                    } catch (Throwable t) {
                        pw.println("* "+t.getMessage());
                        t.printStackTrace(pw);
                    }
                }
            }
        } catch (Throwable t) {
            pw.println("* Unhandled exception: "+t.getMessage());
        } finally {
            Context.exit();
        }

    }
    /** JLine completion engine for call names. */
    static class SDRCallCompletor implements jline.Completor {
        final State state;
        SDRCallCompletor(State state) { this.state = state; }

        @SuppressWarnings("unchecked")
        public int complete(String buffer, int cursor, List candidates) {
            String start = (buffer == null) ? "" : buffer;
            // javascript commands start with "/"
            if (start.startsWith("/")) return -1;
            // trim candidates here
            // XXX: really want set of maximal completions, so that if all
            //      suffixes start with "pass thru" then we return the one
            //      string "pass thru"
            SortedSet<String> results = new TreeSet<String>();
            Program p = state.ds.dance.getProgram();
            for (String s : CompletionEngine.complete(p, start, 50)) {
                // XXX: do we need to filter out the <foo> nonterminals?
                //s = s.replaceFirst("<.*", "");
                results.add(s);
            }
            candidates.addAll(results);
            return results.isEmpty() ? -1 : 0;
        }
    }
    /** This is borrowed from ShellLine.FlexibleCompletor in Rhino. */
    static class JavascriptCompletor implements jline.Completor {
        Scriptable global;
        JavascriptCompletor(Scriptable global) { this.global = global; }
        @SuppressWarnings("unchecked")
        public int complete(String buffer, int cursor, List candidates) {
            // only try to complete strings starting with "/"
            if (buffer==null || !buffer.startsWith("/")) return -1;
            List<String> clist = new ArrayList<String>();
            int v = _complete(buffer.substring(1), cursor-1, clist);
            if (v<0) return -1;
            candidates.addAll(clist);
            return v+1;
        }
        int _complete(String buffer, int cursor, List<String> candidates) {
            // Starting from "cursor" at the end of the buffer, look backward
            // and collect a list of identifiers separated by (possibly zero)
            // dots. Then look up each identifier in turn until getting to the
            // last, presumably incomplete fragment. Then enumerate all the
            // properties of the last object and find any that have the
            // fragment as a prefix and return those for autocompletion.
            int m = cursor - 1;
            while (m >= 0) {
                char c = buffer.charAt(m--);
                if (!Character.isJavaIdentifierPart(c) && c != '.')
                    break;
            }
            String namesAndDots = buffer.substring(m+1, cursor);
            String[] names = namesAndDots.split("\\.");
            Scriptable obj = this.global;
            for (int i=0; i < names.length - 1; i++) {
                Object val = obj.get(names[i], global);
                if (val instanceof Scriptable)
                    obj = (Scriptable) val;
                else {
                    return buffer.length(); // no matches
                }
            }
            Object[] ids = (obj instanceof ScriptableObject)
                           ? ((ScriptableObject)obj).getAllIds()
                           : obj.getIds();
            String lastPart = names[names.length-1];
            for (int i=0; i < ids.length; i++) {
                if (!(ids[i] instanceof String))
                    continue;
                String id = (String)ids[i];
                if (id.startsWith(lastPart)) {
                    if (obj.get(id, obj) instanceof Function)
                        id += "(";
                    candidates.add(id);
                }
            }
            return buffer.length() - lastPart.length();
        }
    }
}
