package net.cscott.sdr;

import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import net.cscott.jdoctest.JDoctestRunner;
import net.cscott.sdr.calls.BadCallException;
import net.cscott.sdr.calls.CallDB;
import net.cscott.sdr.calls.DanceProgram;
import net.cscott.sdr.calls.DanceState;
import net.cscott.sdr.calls.Dancer;
import net.cscott.sdr.calls.Evaluator;
import net.cscott.sdr.calls.Formation;
import net.cscott.sdr.calls.Program;
import net.cscott.sdr.calls.StandardDancer;
import net.cscott.sdr.calls.ast.Comp;
import net.cscott.sdr.calls.ast.Seq;
import net.cscott.sdr.calls.grm.CompletionEngine;
import net.cscott.sdr.util.ListUtils;

import org.junit.runner.RunWith;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.EvaluatorException;
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
 *    >                         "sdr> do half of a u turn back",
 *    >                         "sdr> /setFormationWithDancers(FormationList.RH_COLUMN, 0, 1, 2, 3)"))
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
 *  |sdr> /setFormationWithDancers(FormationList.RH_COLUMN, 0, 1, 2, 3)
 *  || 1B^  1Gv
 *  || 
 *  || 2B^  2Gv
 *  || 
 *  || 4G^  4Bv
 *  || 
 *  || 3G^  3Bv
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
@RunWith(value=JDoctestRunner.class)
public class PMSD {
    private PMSD() {}
    /** Class holding properties accessible from the {@link PMSD} front-end. */
    public static class State extends ScriptableObject {
        private final Map<String,String> properties = new PropertyMap();
        public State() { reset(); }
        @Override
        public String getClassName() { return "State"; }

        // private/internal state
        DanceState ds;
        boolean _isDone = false;
        private void reset() {
            this.ds = new DanceState(new DanceProgram(Program.PLUS),
                                     Formation.SQUARED_SET,
                                     properties);
        }

        // javascript api.
        public Object jsGet_ds() {
            return Context.javaToJS(ds, this);
        }
        public Object jsGet_reset() {
            this.reset();
            return Context.getUndefinedValue();
        }
        public Object jsGet_exit() {
            // abuse the getter mechanism by using it to perform a side-effect
            _isDone = true;
            return Context.getUndefinedValue();
        }
        public Object jsGet_program() {
            return Context.javaToJS(ds.dance.getProgram(), this);
        }
        public void jsSet_program(Object val) {
            Program p = (Program) Context.jsToJava(val, Program.class);
            if (p==ds.dance.getProgram()) return;
            ds = new DanceState(new DanceProgram(p), ds.currentFormation(),
                                properties);
        }
        public void jsSet_formation(Object val) {
            Formation f = (Formation) Context.jsToJava(val, Formation.class);
            boolean hasStandard = false;
            for (Dancer d : f.dancers())
                if (d instanceof StandardDancer)
                    hasStandard = true;
            if (!hasStandard) f = f.mapStd();
            ds = new DanceState(ds.dance, f, properties);
        }
        // this is just a workaround to prevent javascript from echoing the
        // ugly toString() value of the formation.
        public String jsFunction_setFormation(Object val) {
            jsSet_formation(val);
            return printFormation();
        }
        // helper to invoke the javascript printFormation getter
        private String printFormation() {
            return Context.toString
                (ScriptableObject.getProperty(this, "printFormation"));
        }
        // overloaded method to allow easy substitution of real dancers into
        // abstract formations from FormationList.
        public String jsFunction_setFormationWithDancers(Object formation,
                                              Object dancer1, Object dancer2,
                                              Object dancer3, Object dancer4) {
            Formation f = (Formation) Context.jsToJava(formation, Formation.class);
            StandardDancer d1 = jsToDancer(dancer1);
            StandardDancer d2 = jsToDancer(dancer2);
            StandardDancer d3 = jsToDancer(dancer3);
            StandardDancer d4 = jsToDancer(dancer4);
            ds = new DanceState(ds.dance, f.mapStd(d1, d2, d3, d4), properties);
            return printFormation();
        }
        private StandardDancer jsToDancer(Object val) {
            // try to convert directly
            try {
                return (StandardDancer) Context.jsToJava(val, StandardDancer.class);
            } catch (EvaluatorException e) {
                // coerce to numeric arg
                double ordinal = Context.toNumber(val);
                return StandardDancer.values()[(int)ordinal];
            }
        }

        /** Runs the test in this same context, so that we can (for example)
         *  set {@code /errorDetails=true} and then run the test to get more
         *  information about the failure.
         */
	public String jsFunction_runTest(String testName) throws IOException {
            List<String> testCase =
                readLines(PMSD.class.getResourceAsStream("tests/"+testName));
            if (testCase==null)
		return "* " + testName + " not found";
            // execute it!  (reusing current state)
            // (but reset formation and program, since tests expect that)
            this.reset();
            return runTest(this, testName, testCase);
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

        // reflect 'state.props' object as a map
        class PropertyMap extends AbstractMap<String,String> {
            @Override
            public String get(Object key) {
                String name = Context.toString(key);
                Object val = ScriptableObject.getProperty(props(), name);
                if (val == Scriptable.NOT_FOUND) return null; // not here!
                return Context.toString(val);
            }
            @Override
            public Set<Map.Entry<String, String>> entrySet() {
                Set<Map.Entry<String,String>> result =
                    new HashSet<Map.Entry<String,String>>();
                for (Object key : ScriptableObject.getPropertyIds(props())) {
                    if (!(key instanceof String)) continue;
                    String name = (String) key;
                    String value = get(name);
                    result.add(new SimpleImmutableEntry<String,String>
                                (name, value));
                }
                return Collections.unmodifiableSet(result);
            }
            private Scriptable props() {
                Object o = ScriptableObject.getProperty(State.this, "props");
                return (Scriptable) o; // must be a map.
            }
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
            // execute it! (each test in a new JavaScript context)
            String testResult = runTest(null, resource, testCase);
            // resplit and compare.
            int i = 0, mismatch = -1;
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
     *  JavaScript doctests. */
    public static String runTest(String sourceName, String... transcript) {
        // create new javascript context in which to run test
        return runTest(null, sourceName, Arrays.asList(transcript));
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
    /** Run a test transcript, returning the output.
     *  The context and state parameters can be null, in which case a new
     *  javascript context will be created.
     */
    public static String runTest(State s,
                                 final String sourceName, List<String> lines) {
        StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        // very simple regexp to find input lines
        List<String> title = new ArrayList<String>();
        List<String> input = new ArrayList<String>();
        for (String line: lines) {
            if (line.startsWith("sdr> ") || line.startsWith("   > "))
                input.add(line.substring(5));
            else if (input.isEmpty())
                // echo title lines preceding first command
                pw.println(line);
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
            if (s==null)
                repl(rw, false/* no welcome message */);
            else
                repl(Context.getCurrentContext(), s, rw);
        } catch (IOException ioe) {
            pw.println("* Unexpected IO error: "+ioe.getMessage());
        }
        pw.flush();
        return sw.toString().trim();
    }

    /** Console front end entry point. */
    public static void main(String[] args) throws IOException {
        if (args.length > 0) {
            PrintWriter pw = new PrintWriter(System.out, true);
            // ooh, got an argument!
            // run test in its own javascript context
            boolean success = true;
            for (String filename : args) {
                List<String> input =
                    readLines(new FileInputStream(filename));
                String testResult =
                    runTest(null, filename, input);
                // mismatch?
                if (!Arrays.asList(testResult.split("(\\r\\n?|\\n)"))
                    .equals(input)) {
                    success = false;
                }
                pw.println(testResult);
            }
            pw.flush();
            if (!success) System.exit(1); // indicate failing test
            return;
        }
        // console i/o
        final Console c = System.console();
        // create ReaderWriter
        ReaderWriter rw;
        try {
            // try to use JLine
            rw = new JLineReaderWriter(c!=null ? c.writer() :
                                       new PrintWriter(System.out, true));
        } catch (Throwable t) {
            if (c!=null) {
                // try to use the System console, if available.
                rw = new ReaderWriter() {
                    @Override
                    String sourceName() { return "<stdin>"; }
                    @Override
                    String readLine(State s, String prompt) throws IOException {
                        return c.readLine("%s", prompt);
                    }
                    @Override
                    PrintWriter writer() { return c.writer(); }
                };
            } else {
                // ok, give up: use the simplest possible thing that could work
                rw = new ReaderWriter() {
                    PrintWriter pw = new PrintWriter(System.out, true);
                    @Override
                    String sourceName() { return "<stdin>"; }
                    @Override
                    String readLine(State s, String prompt) throws IOException {
                        pw.print(prompt);
                        pw.flush();
                        StringBuilder sb = new StringBuilder();
                        do {
                            int c = System.in.read();
                            if (sb.length()==0) {
                                if (c==-1)
                                    return null; // EOF
                                if (c=='\n' || c=='\r')
                                    continue; // ignore.
                            } else if (c==-1 || c=='\n' || c=='\r')
                                return sb.toString();
                            sb.append((char)c);
                        } while(true);
                    }
                    @Override
                    PrintWriter writer() { return pw; }
                };
            }
        }
        repl(rw, true /* interactive */);
    }
    /** The main read-eval-print loop.
     *  Creates a new JavaScript score/context. */
    public static void repl(ReaderWriter rw, boolean interactive)
        throws IOException {
        // initialize Rhino
        Context cx = Context.enter();
        try {
            cx.setLanguageVersion(Context.VERSION_1_7); // js 1.7 by default
            Global global = new Global();
            global.init(cx);
            ScriptableObject.putProperty(global, "writer", rw.writer());
            // create the 'State' object
            ScriptableObject.defineClass(global, State.class);
            State s = (State) cx.newObject(global, "State");
            ScriptableObject.putProperty(global, "state", s);
            // run javascript startup code to flesh out the state object,
            // define commands, etc.
            Reader in = new InputStreamReader
                (PMSD.class.getResourceAsStream("pmsd.js"), "UTF-8");
            cx.evaluateReader(global, in, "<init>", 0, null);
            // add the 'State' object to the scope chain
            s.setParentScope(global);
            // print friendly welcome message!
            if (interactive)
                ScriptableObject.callMethod(global, "welcome", new Object[0]);
            // start REPL loop
            repl(cx, s, rw);
        } catch (Throwable t) {
            rw.writer().println("* Unhandled exception: "+t.getMessage());
        } finally {
            Context.exit();
        }

    }
    /** Read-evaluate-print loop reusing an existing context and state.
     * @param cx JavaScript context
     * @param s JavaScript scope and state
     * @param rw ReaderWriter object for interaction
     * @throws IOException if interaction object can't read/write
     */
    public static void repl(Context cx, State s, ReaderWriter rw)
            throws IOException {
        // main loop
        PrintWriter pw = rw.writer();
        int lineNum = 1;
        boolean wasDone = s._isDone;
        for (s._isDone = false; !s._isDone; ) {
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
                    // invoke printFormation on state object
                    pw.println(Context.toString(ScriptableObject.getProperty(s, "printFormation")));
                } catch (BadCallException bce) {
                    // invoke formatBCE on state object.
                    Object msg = ScriptableObject.callMethod(s, "formatBCE", new Object[] { bce });
                    pw.println("* "+Context.toString(msg));
                } catch (Throwable t) {
                    pw.println("* "+t.getMessage());
                    t.printStackTrace(pw);
                }
            }
        }
        s._isDone = wasDone; // support nested invocations.
    }
    /** Attempt to create a ReaderWriter based on JLine, without a
     *  compile-time dependency.  Will throw an exception if JLine is not
     *  available at runtime.
     */
    static class JLineReaderWriter extends ReaderWriter {
        PrintWriter pw;
        Class<?> consoleReaderClass;
        Class<?> completorClass;
        Method addCompletor;
        Method readLine;
        Object cr = null;

        JLineReaderWriter(PrintWriter pw) throws ClassNotFoundException, NoSuchMethodException {
            this.pw = pw;
            this.consoleReaderClass = Class.forName
                ("jline.ConsoleReader", false, PMSD.class.getClassLoader());
            this.completorClass = Class.forName
                ("jline.Completor", false, PMSD.class.getClassLoader());
            this.addCompletor = consoleReaderClass.getMethod
                ("addCompletor", completorClass);
            this.readLine = consoleReaderClass.getMethod
                ("readLine", String.class);
        }
        private Object initJLine(State state)
            throws InstantiationException, IllegalAccessException,
                   IllegalArgumentException, InvocationTargetException {
            // initialize JLine!
            Object consoleReader = consoleReaderClass.newInstance();
            // add JLineCompletors
            addCompletor(consoleReader, new SDRCallCompletor(state));
            addCompletor(consoleReader, new JavascriptCompletor(state));
            return consoleReader;
        }
        private void addCompletor(Object consoleReader,final JLineCompletor jlc)
            throws IllegalArgumentException, IllegalAccessException,
                   InvocationTargetException {
            // Wow, this is ugly!
            InvocationHandler handler = new InvocationHandler() {
                @SuppressWarnings("unchecked")
                public Object invoke(Object proxy, Method method, Object[] args)
                    throws Throwable {
                    assert args.length == 3;
                    return jlc.complete((String) args[0],
                            (Integer) args[1],
                            (List) args[2]);
                }
            };
            Object proxy = Proxy.newProxyInstance
                (PMSD.class.getClassLoader(),
                 new Class[] { completorClass },
                 handler);
            addCompletor.invoke(consoleReader, proxy);
        }
        @Override
        String sourceName() { return "<stdin>"; }
        @Override
        String readLine(State state, String prompt) throws IOException {
            try {
                if (cr==null) {
                    cr=initJLine(state);
                }
                return (String) readLine.invoke(cr, prompt);
            } catch (Throwable t) {
                throw new IOException("Couldn't read line using JLine", t);
            }
        }
        @Override
        PrintWriter writer() { return pw; }
    }
    /** This is our version of jline.Completor, defined here to avoid a
     *  compile-time (or run-time) dependency on jline. */
    static abstract class JLineCompletor {
        public abstract int complete(String buffer, int cursor,
                                     List<String> candidates);
    }
    /** JLine completion engine for call names. */
    static class SDRCallCompletor extends JLineCompletor {
        final State state;
        SDRCallCompletor(State state) { this.state = state; }

        public int complete(String buffer, int cursor, List<String> candidates) {
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
    static class JavascriptCompletor extends JLineCompletor {
        Scriptable global;
        JavascriptCompletor(Scriptable global) { this.global = global; }

        public int complete(String buffer, int cursor, List<String> candidates) {
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
            String[] names = namesAndDots.split("\\.", -1);
            Scriptable obj = this.global;
            boolean inScope = true;
            for (int i=0; i < names.length - 1; i++) {
                Object val = inScope ? getFromScope(obj, names[i]) :
                    ScriptableObject.getProperty(obj, names[i]);
                if (val instanceof Scriptable) {
                    obj = (Scriptable) val;
                    inScope = false;
                } else {
                    return buffer.length(); // no matches
                }
            }
            String lastPart = names[names.length-1];
            // zoom up the scope chain and down the prototype chain
            // enumerating all properties.
            List<String> idList = new ArrayList<String>();
            if (inScope)
                addIdsFromScope(obj, idList);
            else
                addIdsFromObjAndPrototypes(obj, idList);

            for (String id : idList) {
                if (id.startsWith(lastPart)) {
                    try {
                        // actually getting this property could have
                        // side-effects, including throwing exceptions!
                        if (obj.get(id, obj) instanceof Function)
                            id += "(";
                    } catch (Throwable t) { /* ignore! */ }
                    candidates.add(id);
                }
            }
            return buffer.length() - lastPart.length();
        }
        private static void addIdsFromObj(Scriptable obj, List<String> idList) {
            Object[] ids = (obj instanceof ScriptableObject)
                ? ((ScriptableObject)obj).getAllIds()
                : obj.getIds();
             for (Object o: ids) {
                 if (!(o instanceof String))
                     continue;
                 idList.add((String)o);
             }
        }
        private static void addIdsFromObjAndPrototypes(Scriptable obj, List<String> idList) {
            Scriptable parent = obj.getPrototype();
            if (parent != null)
                addIdsFromObjAndPrototypes(parent, idList);
            addIdsFromObj(obj, idList);
        }
        private static void addIdsFromScope(Scriptable scope, List<String> idList) {
            Scriptable parentScope = scope.getParentScope();
            if (parentScope!=null)
                addIdsFromScope(parentScope, idList);
            addIdsFromObjAndPrototypes(scope, idList);
        }
        private static Object getFromScope(Scriptable scope, String name) {
            // ScriptableObject.getProperty does the recursion through the
            // prototype chain; that leaves us to do the recursion up the
            // scope stack.
            for ( ; scope!=null ; scope=scope.getParentScope()) {
                Object var = ScriptableObject.getProperty(scope, name);
                if (var != Scriptable.NOT_FOUND)
                    return var;
            }
            return Scriptable.NOT_FOUND;
        }
    }
}
