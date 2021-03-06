package net.cscott.sdr.calls;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.cscott.jdoctest.JDoctestRunner;
import net.cscott.sdr.DevSettings;
import net.cscott.sdr.calls.ast.Apply;
import net.cscott.sdr.calls.ast.Expr;
import net.cscott.sdr.calls.lists.A1List;
import net.cscott.sdr.calls.lists.A2List;
import net.cscott.sdr.calls.lists.BasicList;
import net.cscott.sdr.calls.lists.C1List;
import net.cscott.sdr.calls.lists.C2List;
import net.cscott.sdr.calls.lists.C3aList;
import net.cscott.sdr.calls.lists.C3bList;
import net.cscott.sdr.calls.lists.C4List;
import net.cscott.sdr.calls.lists.MainstreamList;
import net.cscott.sdr.calls.lists.PlusList;
import net.cscott.sdr.calls.transform.CallFileLoader;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.Lexer;
import org.antlr.runtime.Parser;
import org.antlr.runtime.TokenStream;
import org.junit.runner.RunWith;

/** CallDB holds all the calls and concepts we know about.
 * It is a singleton class; its static constructor loads
 * all the call definitions from files and other classes.
 * @author C. Scott Ananian
 */
@RunWith(value=JDoctestRunner.class)
public class CallDB {
    public static final CallDB INSTANCE = new CallDB();
    private Map<String, Call> db = new HashMap<String,Call>();
    /** Lookup a call in the database.
     * @throws IllegalArgumentException if the call name is
     * unknown.
     * @doc.test
     *  Basic lookup test:
     *  js> db = CallDB.INSTANCE
     *  net.cscott.sdr.calls.CallDB@1d66e22
     *  js> db.lookup("square thru")
     *  square thru[basic]
     * @doc.test
     *  Check that exceptions are properly thrown for bogus calls:
     *  js> try {
     *    >   CallDB.INSTANCE.lookup("foobar bat")
     *    > } catch (e) {
     *    >   print(e.javaException)
     *    > }
     *  java.lang.IllegalArgumentException: Unknown call: foobar bat
     */
    public Call lookup(String name) {
        if (!db.containsKey(name))
            throw new IllegalArgumentException("Unknown call: "+name);
        return db.get(name);
    }
    public final Collection<Call> allCalls = 
        Collections.unmodifiableCollection(db.values());

    private CallDB() {
        this.reload();
    }
    /** Reload call definitions from resource files and classes. */
    public void reload() {
        this.db.clear();
        // okay, first load the call definition lists.
        CallFileLoader.load(resource("basic"), db);
        CallFileLoader.load(resource("mainstream"), db);
        CallFileLoader.load(resource("plus"), db);
        CallFileLoader.load(resource("a1"), db);
        CallFileLoader.load(resource("a2"), db);
        CallFileLoader.load(resource("c1"), db);
        CallFileLoader.load(resource("c2"), db);
        CallFileLoader.load(resource("c3a"), db);
        CallFileLoader.load(resource("c3b"), db);
        CallFileLoader.load(resource("c4"), db);
        // now load complex calls and concepts.
        loadFromClass(BasicList.class);
        loadFromClass(MainstreamList.class);
        loadFromClass(PlusList.class);
        loadFromClass(A1List.class);
        loadFromClass(A2List.class);
        loadFromClass(C1List.class);
        loadFromClass(C2List.class);
        loadFromClass(C3aList.class);
        loadFromClass(C3bList.class);
        loadFromClass(C4List.class);
    }
    private static URL resource(String name) {
        return CallDB.class.getClassLoader().getResource("net/cscott/sdr/calls/lists/"+name+".calls");
    }
    private void loadFromClass(Class<?> c) {
        // iterate through all fields in class, and add fields of type 'Call'
        for (Field f : c.getFields()) {
            if (Call.class.isAssignableFrom(f.getType()) &&
                    Modifier.isStatic(f.getModifiers())) {
                try {
                    Call call = (Call) f.get(null);
                    assert !db.containsKey(call.getName()) :
                        "duplicate call: "+call.getName();
                    db.put(call.getName(), call);
                } catch (IllegalAccessException e) {
                    assert false : e;
                }
            }
        }
    }
    ///////////////////////////////////////////////
    /** Parse a natural-language string of calls.
     *
     * @doc.test Simple examples:
     *  js> db = CallDB.INSTANCE
     *  net.cscott.sdr.calls.CallDB@1d66e22
     *  js> db.parse(Program.BASIC, "double pass thru")
     *  (Apply 'double pass thru)
     *  js> db.parse(Program.BASIC, "square thru three and a half")
     *  (Apply (Expr square thru '3 1/2))
     * @doc.test As a convenience, we also allow numbers specified with digits
     *  (even though this is never produced by the spoken language recognizer):
     *  js> db = CallDB.INSTANCE ; undefined
     *  js> db.parse(Program.BASIC, "square thru 3 1/2")
     *  (Apply (Expr square thru '3 1/2))
     * @doc.test We use a precedence grammar to resolve some ambiguities:
     *  js> db = CallDB.INSTANCE ; undefined
     *  js> db.parse(Program.BASIC, "do one half of a trade")
     *  (Apply (Expr _fractional '1/2 'trade))
     *  js> db.parse(Program.PLUS, "do one half of a trade and roll")
     *  (Apply (Expr and roll (Expr _fractional '1/2 'trade)))
     *  js> db.parse(Program.PLUS, "trade twice and roll")
     *  (Apply (Expr and roll (Expr _fractional '2 'trade)))
     *  js> db.parse(Program.PLUS, "trade and roll twice")
     *  (Apply (Expr _fractional '2 (Expr and roll 'trade)))
     * @doc.test Semicolon-separated calls are also used in the typed
     *  (not the spoken) grammar:
     *  js> db = CallDB.INSTANCE ; undefined
     *  js> db.parse(Program.PLUS, "circulate; trade; u turn back")
     *  (Apply (Expr and 'circulate 'trade 'u turn back))
     * @doc.test Parentheses can be used in the typed (not spoken) grammar:
     *  js> db = CallDB.INSTANCE ; undefined
     *  js> db.parse(Program.PLUS, "do one half of a trade and roll")
     *  (Apply (Expr and roll (Expr _fractional '1/2 'trade)))
     *  js> db.parse(Program.PLUS, "do one half of a ( trade and roll )")
     *  (Apply (Expr _fractional '1/2 (Expr and roll 'trade)))
     * @doc.test Ensure we throw an exception if the call is unrecognized or
     *  not on level:
     *  js> db = CallDB.INSTANCE ; undefined
     *  js> try {
     *    >   db.parse(Program.C4, "@trade")
     *    > } catch (e) {
     *    >   print(e.javaException)
     *    > }
     *  net.cscott.sdr.calls.BadCallException: Not on list: @trade
     *  js> try {
     *    >   db.parse(Program.C4, "foobar bat")
     *    > } catch (e) {
     *    >   print(e.javaException)
     *    > }
     *  net.cscott.sdr.calls.BadCallException: Not on list: foobar bat
     *  js> try {
     *    >   db.parse(Program.MAINSTREAM, "trade and roll")
     *    > } catch (e) {
     *    >   print(e.javaException)
     *    > }
     *  net.cscott.sdr.calls.BadCallException: Not on list: trade and roll
     */
    public Apply parse(Program program, String s) {
        if (program!=Program.C4 && DevSettings.ONLY_C4_GRAMMAR)
            program=Program.C4; // speed up compilation during development
        String pkgName = "net.cscott.sdr.calls.lists.";
        String baseName = program.toTitleCase()+"Grammar";
        String parserName = baseName+"Parser";
        String lexerName = baseName+"Lexer";
	Expr result=null;

        try {
            Lexer lexer = (Lexer) Class.forName(pkgName+lexerName)
                .getConstructor(CharStream.class).newInstance
                (new ANTLRStringStream(s.replace('-',' ').toLowerCase()));
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            Parser parser = (Parser) Class.forName(pkgName+parserName)
                .getConstructor(TokenStream.class).newInstance(tokens);
            Method m = parser.getClass().getMethod("start");
            result = (Expr) m.invoke(parser);
            if (lexer.getNumberOfSyntaxErrors() > 0 ||
                parser.getNumberOfSyntaxErrors() > 0 ||
                result == null)
                throw new Exception();
        } catch (Exception e) {
            throw new BadCallException("Not on list: "+s);
        }
	return new Apply(result);
    }
}
