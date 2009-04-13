package net.cscott.sdr.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Utility functions to facilitate map and list creation.
 * @author C. Scott Ananian
 */
public abstract class Tools {
    /** Don't try to instantiate this class. */
    private Tools() { }

    /**
     * Convenience constructor for maps, specified as a list of pairs.
     * @see #p
     * @doc.test
     *  js> map = Tools.m()
     *  {}
     *  js> map.isEmpty()
     *  true
     *  js> map = Tools.m(Tools.p(1,2), Tools.p(3,4), Tools.p(5,6))
     *  {1.0=2.0, 3.0=4.0, 5.0=6.0}
     *  js> map.get(3)
     *  4.0
     * @doc.test Only the last entry is kept for duplicate keys:
     *  js> map = Tools.m(Tools.p(1,2), Tools.p(1,3), Tools.p(1,4))
     *  {1.0=4.0}
     */
    public static <A,B> Map<A,B> m(Map.Entry<A,B>... items) {
        if (items.length==0) return Collections.emptyMap();
        LinkedHashMap<A,B> result = new LinkedHashMap<A,B>(items.length);
        for (Map.Entry<A,B> e : items)
            result.put(e.getKey(), e.getValue());
        return result;
    }
    /**
     * Convenience constructor for lists.
     * @doc.test
     *  js> Tools.l()
     *  []
     *  js> ll = Tools.l()
     *  []
     *  js> ll.isEmpty()
     *  true
     *  js> ll = Tools.l('foo')
     *  [foo]
     *  js> ll.get(0)
     *  foo
     *  js> ll = Tools.l(1,2,3,2,1)
     *  [1.0, 2.0, 3.0, 2.0, 1.0]
     *  js> ll.size()
     *  5
     */
    public static <A> List<A> l(A... items) {
        switch(items.length) {
        case 0: return Collections.emptyList();
        case 1: return Collections.singletonList(items[0]);
        default:return Arrays.asList(items);
        }
    }
    // suppress harmless "unchecked cast in varargs" for common uses
    /** @see #l(Object[]) */
    public static <A> List<A> l() {
        @SuppressWarnings("unchecked")
        A[] items = (A[]) new Object[0];
        return l(items);
    }
    /** @see #l(Object[]) */
    public static <A> List<A> l(A item1) {
        @SuppressWarnings("unchecked")
        A[] items = (A[]) new Object[] { item1 };
        return l(items);
    }
    /** @see #l(Object[]) */
    public static <A> List<A> l(A item1, A item2) {
        @SuppressWarnings("unchecked")
        A[] items = (A[]) new Object[] { item1, item2 };
        return l(items);
    }
    /** @see #l(Object[]) */
    public static <A> List<A> l(A item1, A item2, A item3) {
        @SuppressWarnings("unchecked")
        A[] items = (A[]) new Object[] { item1, item2, item3 };
        return l(items);
    }
    /**
     * Convenience constructor for sets.
     * @doc.test
     *  js> Tools.s()
     *  []
     *  js> ss = Tools.s()
     *  []
     *  js> ss.isEmpty()
     *  true
     *  js> ss = Tools.s('foo')
     *  [foo]
     *  js> ss.contains('foo')
     *  true
     *  js> ss = Tools.s(1,2,3,2,1)
     *  [1.0, 2.0, 3.0]
     *  js> ss.size()
     *  3
     */
    public static <A> Set<A> s(A... items) {
        switch(items.length) {
        case 0: return Collections.emptySet();
        case 1: return Collections.singleton(items[0]);
        default:return new LinkedHashSet<A>(Arrays.asList(items));
        }
    }
    // suppress harmless "unchecked cast in varargs" for common uses
    /** @see #s(Object[]) */
    public static <A> Set<A> s() {
        @SuppressWarnings("unchecked")
        A[] items = (A[]) new Object[0];
        return s(items);
    }
    /** @see #s(Object[]) */
    public static <A> Set<A> s(A item1) {
        @SuppressWarnings("unchecked")
        A[] items = (A[]) new Object[] { item1 };
        return s(items);
    }
    /** @see #s(Object[]) */
    public static <A> Set<A> s(A item1, A item2) {
        @SuppressWarnings("unchecked")
        A[] items = (A[]) new Object[] { item1, item2 };
        return s(items);
    }
    /** @see #s(Object[]) */
    public static <A> Set<A> s(A item1, A item2, A item3) {
        @SuppressWarnings("unchecked")
        A[] items = (A[]) new Object[] { item1, item2, item3 };
        return s(items);
    }
}
