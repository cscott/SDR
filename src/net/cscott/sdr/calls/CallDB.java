package net.cscott.sdr.calls;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;

import net.cscott.sdr.calls.ast.Apply;
import net.cscott.sdr.calls.lists.BasicList;
import net.cscott.sdr.calls.lists.C4Parser;
import net.cscott.sdr.calls.lists.C4ParserLexer;
import net.cscott.sdr.calls.lists.MainstreamList;
import net.cscott.sdr.calls.transform.CallFileLoader;

/** CallDB holds all the calls and concepts we know about.
 * It is a singleton class; its static constructor loads
 * all the call definitions from files and other classes.
 * @author C. Scott Ananian
 */
public class CallDB {
    public static final CallDB INSTANCE = new CallDB();
    private Map<String, Call> db = new HashMap<String,Call>();
    /** Lookup a call in the database.
     * @throws IllegalArgumentException if the call name is
     * unknown.
     */
    public Call lookup(String name) {
        if (!db.containsKey(name))
            throw new IllegalArgumentException("Unknown call: "+name);
        return db.get(name);
    }
    public final Collection<Call> allCalls = 
        Collections.unmodifiableCollection(db.values());

    private CallDB() {
        // okay, first load the call definition lists.
        CallFileLoader.load(resource("basic"), db);
        CallFileLoader.load(resource("mainstream"), db);
        CallFileLoader.load(resource("plus"), db);
        CallFileLoader.load(resource("a1"), db);
        CallFileLoader.load(resource("a2"), db);
        // now load complex calls and concepts.
        loadFromClass(BasicList.class);
        loadFromClass(MainstreamList.class);
    }
    private static URL resource(String name) {
        return CallDB.class.getClassLoader().getResource("net/cscott/sdr/calls/lists/"+name+".calls");
    }
    private void loadFromClass(Class c) {
        // iterate through all fields in class, and add fields of type 'Call'
        for (Field f : c.getFields()) {
            if (Call.class.isAssignableFrom(f.getType()) &&
                    Modifier.isStatic(f.getModifiers())) {
                try {
                    Call call = (Call) f.get(null);
                    db.put(call.getName(), call);
                } catch (IllegalAccessException e) {
                    assert false : e;
                }
            }
        }
    }
    ///////////////////////////////////////////////
    /** Parse a natural-language string of calls. */
    public Apply parse(Program program, String s) {
        // XXX: bail for now, and just parse as C4 regardless.
        C4ParserLexer lexer = new C4ParserLexer(new ANTLRStringStream(s));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        tokens.discardOffChannelTokens(true);
        C4Parser p = new C4Parser(tokens);
        try {
            return p.anything();
        } catch (RecognitionException re) {
            throw new BadCallException("Parsing error: "+re);
        }
    }
}
