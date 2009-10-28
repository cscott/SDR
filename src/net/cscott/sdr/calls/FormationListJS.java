package net.cscott.sdr.calls;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;

/** Javascript thunk to work around problem using Rhino to peer at
 *  {@link FormationListFast}/{@link FormationListSlow} via reflection.
 *  Since these are package-scope classes, Java's access control policy
 *  for security doesn't let us look at these fields, even if they
 *  are exported from the public {@link FormationList}.  However,
 *  code within the same package can look at them via reflection.  Thus
 *  this thunk class can take the place of {@link FormationList} and does
 *  the necessary reflection within package scope to avoid the access
 *  exceptions.
 * @doc.test
 *  js> FormationList = FormationListJS.initJS(this);
 *  [object FormationListJS]
 *  js> FormationList.SINGLE_DANCER
 *  net.cscott.sdr.calls.NamedTaggedFormation[
 *    name=SINGLE DANCER
 *    location={<phantom@7b>=0,0,n}
 *    selected=[<phantom@7b>]
 *    tags={}
 *  ]
 *  js> FormationList.all.size() > 0
 *  true
 */
public class FormationListJS implements Scriptable {
    /** The class to reflect, so we can compare {@link FormationListFast}
     *  with {@link FormationListSlow} within doctests. */
    private Class<?> formationList;
    public FormationListJS(Class<?> formationList) { this.formationList=formationList; }
    @SuppressWarnings("unchecked")
    private List<TaggedFormation> all() {
        try {
            return (List<TaggedFormation>) formationList.getField("all").get(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(String name) { /* no op */ }
    public void delete(int index) { /* no op */ }
    public void put(String name, Scriptable start, Object value) { /* noop*/ }
    public void put(int index, Scriptable start, Object value) { /* no op */ }

    public Object get(String name, Scriptable start) {
        try {
            return Context.javaToJS(FormationList.class.getField(name)
                                    .get(null), start);
        } catch (IllegalArgumentException e) {
        } catch (SecurityException e) {
        } catch (IllegalAccessException e) {
        } catch (NoSuchFieldException e) {
        }
        return Scriptable.NOT_FOUND;
    }

    public Object get(int index, Scriptable start) {
        return Context.javaToJS(all().get(index), start);
    }

    public String getClassName() {
        return "FormationListJS";
    }

    public Object getDefaultValue(Class<?> arg0) {
        return "[object FormationListJS]";
    }

    public Object[] getIds() {
        // can't get field names back from formation names via a simple
        // mapping, so use reflection on formationList here.
        List<String> idList = new ArrayList<String>();
        for (Field f : formationList.getFields()) {
            if (Modifier.isPublic(f.getModifiers()) &&
                Modifier.isStatic(f.getModifiers()) &&
                !f.getName().equals("all") /* 'all' is non-enumerable */)
            idList.add(f.getName());
        }
        return idList.toArray();
    }

    public boolean has(String name, Scriptable start) {
        return get(name, start) != NOT_FOUND;
    }

    public boolean has(int index, Scriptable start) {
        return (index>=0 && index < all().size());
    }

    public boolean hasInstance(Scriptable instance) {
        return ScriptRuntime.jsDelegatesTo(instance, this);
    }

    private Scriptable parentScope;
    public Scriptable getParentScope() { return parentScope; }
    public void setParentScope(Scriptable m) { parentScope=m; }

    private Scriptable prototype;
    public Scriptable getPrototype() { return prototype; }
    public void setPrototype(Scriptable m) { prototype=m; }

    public static Scriptable initJS(Scriptable scope) {
        return initJS(scope, FormationList.class);
    }
    public static Scriptable initJS(Scriptable scope, Class<?> formationList) {
        Scriptable s = new FormationListJS(formationList);
        s.setParentScope(scope);
        return s;
    }
}