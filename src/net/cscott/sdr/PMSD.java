package net.cscott.sdr;

import java.io.PrintWriter;
import java.util.ArrayList;
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
 */
public class PMSD {
    private PMSD() {}
    /** Class holding properties accessible from the front-end. */
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
        // value of the formation.
        public String jsFunction_setFormation(Object val) {
            jsSet_formation(val);
            return jsGet_printFormation();
        }
        public String jsGet_printFormation() {
            return ds.currentFormation().toStringDiagram("| ");
        }
    }

    public static void main(String[] args) throws Exception {
        PrintWriter pw = new PrintWriter(System.out, true);
        // initialize JLine
        jline.ConsoleReader cr = new jline.ConsoleReader();
        // initialize Rhino
        Context cx = Context.enter();
        try {
            Global global = new Global();
            global.init(cx);
            cx.evaluateString(global, "importPackage(net.cscott.sdr.calls)",
                              "<init>", 0, null);
            cr.addCompletor(new JavascriptCompletor(global));
            // add the 'State' object to the scope chain
            ScriptableObject.defineClass(global, State.class);
            State s = (State) cx.newObject(global, "State");
            s.setParentScope(global);
            cr.addCompletor(new SDRCallCompletor(s));
            // main loop
            String sourceName = "<stdin>";
            int lineNum = 1;
            while (!s._isDone) {
                final int initialLineNum = lineNum;
                String line = cr.readLine("sdr> "); lineNum++;
                if (line==null) break;
                if (line.startsWith("/")) {
                    line = line.substring(1);
                    // accumulate until we've got a complete javascript statement
                    while (!cx.stringIsCompilableUnit(line)) {
                        line = line + "\n" + cr.readLine("   > ");
                        lineNum++;
                    }
                    try {
                        Object result = cx.evaluateString(s, line, sourceName,
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
                               cr.readLine("   > ");
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
        } finally {
            Context.exit();
            if (cr.getTerminal() instanceof jline.UnixTerminal)
                ((jline.UnixTerminal)cr.getTerminal()).restoreTerminal();
        }

    }
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
