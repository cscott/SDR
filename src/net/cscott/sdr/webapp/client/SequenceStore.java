package net.cscott.sdr.webapp.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.cscott.sdr.calls.Program;
import net.cscott.sdr.webapp.client.Sequence.StartingFormationType;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.storage.client.Storage;

/**
 * Saves sequences in the browser's local storage.  Nothing is stored on
 * the server, so there is no login: each browser has its own collection
 * of sequences, which {@link #exportAll()} and {@link #importAll(String)}
 * can copy to and from a file.
 * <p>
 * Each sequence is a JSON object under the key
 * <code>sdr.sequence.<i>id</i></code>; the export file is a JSON object
 * holding a list of the same objects.
 *
 * @author C. Scott Ananian
 */
public class SequenceStore {
    private static final String KEY_PREFIX = "sdr.sequence.";
    private static final String NEXT_ID_KEY = "sdr.nextId";
    private static final String EXPORT_FORMAT = "sdr-sequences";
    private static final int EXPORT_VERSION = 1;

    private final Storage storage = Storage.getLocalStorageIfSupported();

    /** Thrown when a sequence can't be saved, loaded or imported. */
    @SuppressWarnings("serial")
    public static class StoreException extends Exception {
        public StoreException(String message) { super(message); }
    }

    /** Return true if this browser has local storage we can use. */
    public boolean isSupported() { return storage != null; }

    /** Return the saved sequences, most recently saved first. */
    public List<SequenceInfo> list() {
        List<SequenceInfo> result = new ArrayList<SequenceInfo>();
        if (storage == null) return result;
        for (int i = 0; i < storage.getLength(); i++) {
            String key = storage.key(i);
            if (key == null || !key.startsWith(KEY_PREFIX)) continue;
            try {
                result.add(infoFromJson(parse(storage.getItem(key))));
            } catch (StoreException e) {
                // skip entries we can't read
            }
        }
        result.sort((a, b) -> Long.compare(time(b), time(a)));
        return result;
    }

    /** Load the sequence saved under the given id. */
    public Sequence load(long id) throws StoreException {
        String json = (storage == null) ? null : storage.getItem(KEY_PREFIX+id);
        if (json == null)
            throw new StoreException("Sequence not found");
        return sequenceFromJson(parse(json));
    }

    /** Save the given sequence, setting <code>info.id</code> (if this is a
     *  new sequence) and <code>info.lastModified</code>. */
    public void save(SequenceInfo info, Sequence sequence)
        throws StoreException {
        Date oldModified = info.lastModified;
        info.lastModified = new Date();
        try {
            put(info, sequence);
        } catch (StoreException e) {
            info.lastModified = oldModified;
            throw e;
        }
    }

    /** Write the given sequence to storage, allocating an id for it if
     *  <code>info.id</code> is null. */
    private void put(SequenceInfo info, Sequence sequence)
        throws StoreException {
        checkSupported();
        Long oldId = info.id;
        if (info.id == null) info.id = allocateId();
        try {
            storage.setItem(KEY_PREFIX+info.id,
                            toJson(info, sequence, true).toString());
        } catch (RuntimeException e) {
            // most likely the storage quota has been exceeded
            info.id = oldId;
            throw new StoreException("Could not save: "+e.getMessage());
        }
    }

    /** Delete the sequence saved under the given id. */
    public void delete(long id) {
        if (storage != null) storage.removeItem(KEY_PREFIX+id);
    }

    /** Return every saved sequence, as a JSON string. */
    public String exportAll() throws StoreException {
        JSONArray sequences = new JSONArray();
        for (SequenceInfo info : list())
            sequences.set(sequences.size(),
                          toJson(info, load(info.id), false));
        JSONObject result = new JSONObject();
        result.put("format", new JSONString(EXPORT_FORMAT));
        result.put("version", new JSONNumber(EXPORT_VERSION));
        result.put("sequences", sequences);
        return result.toString();
    }

    /** Save every sequence in the given JSON string (as written by
     *  {@link #exportAll()}) as a new sequence, and return how many there
     *  were.  Nothing is saved unless every sequence can be read. */
    public int importAll(String json) throws StoreException {
        checkSupported();
        JSONObject obj = parse(json);
        if (!EXPORT_FORMAT.equals(string(obj, "format")))
            throw new StoreException("Not a file of exported sequences");
        if (number(obj, "version") > EXPORT_VERSION)
            throw new StoreException("File is from a newer version of SDR");
        JSONArray sequences = get(obj, "sequences").isArray();
        if (sequences == null)
            throw new StoreException("No sequences in file");
        List<SequenceInfo> infos = new ArrayList<SequenceInfo>();
        List<Sequence> seqs = new ArrayList<Sequence>();
        for (int i = 0; i < sequences.size(); i++) {
            JSONObject s = sequences.get(i).isObject();
            if (s == null) throw new StoreException("Malformed sequence");
            SequenceInfo info = infoFromJson(s);
            info.id = null; // always save as a new sequence
            infos.add(info);
            seqs.add(sequenceFromJson(s));
        }
        for (int i = 0; i < infos.size(); i++) {
            // keep the original modification time
            if (infos.get(i).lastModified == null)
                infos.get(i).lastModified = new Date();
            put(infos.get(i), seqs.get(i));
        }
        return infos.size();
    }

    private void checkSupported() throws StoreException {
        if (storage == null)
            throw new StoreException
                ("This browser does not support local storage");
    }

    private long allocateId() {
        long id = 1;
        String next = storage.getItem(NEXT_ID_KEY);
        if (next != null) {
            try {
                id = Long.parseLong(next);
            } catch (NumberFormatException e) { /* start over */ }
        }
        // never reuse the id of an existing sequence
        while (storage.getItem(KEY_PREFIX+id) != null)
            id++;
        storage.setItem(NEXT_ID_KEY, Long.toString(id+1));
        return id;
    }

    private static long time(SequenceInfo info) {
        return (info.lastModified == null) ? 0 : info.lastModified.getTime();
    }

    // --- JSON conversion ---

    private static JSONObject toJson(SequenceInfo info, Sequence sequence,
                                     boolean includeId) {
        JSONObject obj = new JSONObject();
        if (includeId)
            obj.put("id", new JSONNumber(info.id));
        obj.put("title", new JSONString(info.title));
        obj.put("manualTags", toJson(info.manualTags));
        obj.put("tags", toJson(info.tags));
        if (info.lastModified != null)
            obj.put("lastModified",
                    new JSONNumber(info.lastModified.getTime()));
        obj.put("program", new JSONString(sequence.program.name()));
        obj.put("startingFormation",
                new JSONString(sequence.startingFormation.name()));
        obj.put("calls", toJson(sequence.calls));
        return obj;
    }
    private static JSONArray toJson(List<String> strings) {
        JSONArray array = new JSONArray();
        for (String s : strings)
            array.set(array.size(), new JSONString(s));
        return array;
    }

    private static SequenceInfo infoFromJson(JSONObject obj)
        throws StoreException {
        SequenceInfo info = new SequenceInfo(string(obj, "title"));
        if (obj.containsKey("id"))
            info.id = (long) number(obj, "id");
        if (obj.containsKey("lastModified"))
            info.lastModified = new Date((long) number(obj, "lastModified"));
        info.manualTags = strings(obj, "manualTags");
        info.tags = strings(obj, "tags");
        return info;
    }
    private static Sequence sequenceFromJson(JSONObject obj)
        throws StoreException {
        Sequence sequence = new Sequence();
        try {
            sequence.program = Program.valueOf(string(obj, "program"));
            sequence.startingFormation =
                StartingFormationType.valueOf(string(obj,"startingFormation"));
        } catch (IllegalArgumentException e) {
            throw new StoreException("Unknown program or formation");
        }
        sequence.calls = strings(obj, "calls");
        return sequence;
    }

    private static JSONObject parse(String json) throws StoreException {
        JSONValue value;
        try {
            value = JSONParser.parseStrict(json);
        } catch (RuntimeException e) {
            throw new StoreException("Malformed JSON");
        }
        JSONObject obj = value.isObject();
        if (obj == null) throw new StoreException("Malformed JSON");
        return obj;
    }
    private static JSONValue get(JSONObject obj, String key)
        throws StoreException {
        JSONValue value = obj.get(key);
        if (value == null) throw new StoreException("Missing "+key);
        return value;
    }
    private static String string(JSONObject obj, String key)
        throws StoreException {
        JSONString s = get(obj, key).isString();
        if (s == null) throw new StoreException("Malformed "+key);
        return s.stringValue();
    }
    private static double number(JSONObject obj, String key)
        throws StoreException {
        JSONNumber n = get(obj, key).isNumber();
        if (n == null) throw new StoreException("Malformed "+key);
        return n.doubleValue();
    }
    private static List<String> strings(JSONObject obj, String key)
        throws StoreException {
        JSONArray array = get(obj, key).isArray();
        if (array == null) throw new StoreException("Malformed "+key);
        List<String> result = new ArrayList<String>(array.size());
        for (int i = 0; i < array.size(); i++) {
            JSONString s = array.get(i).isString();
            if (s == null) throw new StoreException("Malformed "+key);
            result.add(s.stringValue());
        }
        return result;
    }
}
