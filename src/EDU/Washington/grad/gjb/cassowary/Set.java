// $Id: Set.java,v 1.1 2008/08/12 22:32:43 larrymelia Exp $
//
// Cassowary Incremental Constraint Solver
// Original Smalltalk Implementation by Alan Borning
// This Java Implementation by Greg J. Badros, <gjb@cs.washington.edu>
// http://www.cs.washington.edu/homes/gjb
// (C) 1998, 1999 Greg J. Badros and Alan Borning
// See ../LICENSE for legal details regarding this software
//
// Set
// Encapsulate a mathematical "Set" ADT using java's
// hash table.  Just a convenience wrapper of the java.util.Hashtable class.

package EDU.Washington.grad.gjb.cassowary;

import java.util.*;

class Set<T> implements Iterable<T> {
    public Set() {
        hash = new java.util.HashSet<T>();
    }

    public Set(int i) {
        this();
    }

    public Set(int i, float f) {
        this();
    }

    private Set(java.util.Set<T> h) {
        hash = h;
    }

    public boolean contains(Object o) {
        return hash.contains(o);
    }

    public boolean add(T o) {
        return hash.add(o);
        //return hash.put(o, o) == null ? true : false;
    }

    public boolean remove(Object o) {
        return hash.remove(o);
        //return hash.remove(o) == null ? true : false;
    }

    public void clear() {
        hash.clear();
    }

    public int size() {
        return hash.size();
    }

    public boolean isEmpty() {
        return hash.isEmpty();
    }

    @SuppressWarnings("unchecked")
    public Set<T> clone() {
        return new Set<T>(new java.util.HashSet<T>(this.hash));
        //return new Set<T>((Hashtable<Object, Object>) hash.clone());
    }

    @SuppressWarnings("unchecked")
    public Enumeration<T> elements() {
        return Collections.enumeration(hash);
    }
    public Iterator<T> iterator() { return hash.iterator(); }

    public String toString() {
        StringBuffer bstr = new StringBuffer("{ ");
        Enumeration<T> e = elements();
        if (e.hasMoreElements())
            bstr.append(e.nextElement().toString());
        while (e.hasMoreElements()) {
            bstr.append(", " + e.nextElement());
        }
        bstr.append(" }\n");
        return bstr.toString();
    }

    private java.util.Set<T> hash;
}
