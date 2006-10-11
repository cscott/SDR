package net.cscott.sdr.calls;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

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
    private CallDB() {
        // okay, first load the call definition lists.
        CallFileLoader.load(resource("basic"), db);
        // now load complex calls and concepts.
    }
    private static URL resource(String name) {
        return CallDB.class.getClassLoader().getResource("net/cscott/sdr/calls/lists/"+name+".calls");
    }
}
