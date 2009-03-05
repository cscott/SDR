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

import net.cscott.jutil.Factories;
import net.cscott.jutil.FilterIterator;
import net.cscott.jutil.GenericMultiMap;
import net.cscott.jutil.PairMapEntry;
import net.cscott.jutil.MultiMap;

/**
 * Utility functions to facilitate map and list creation.
 * @author C. Scott Ananian
 */
public abstract class Tools {
    /** Don't try to instantiate this class. */
    private Tools() { }

    /** Convenience constructor for pairs.
     * @doc.test
     *  js> Tools.p(1,2)
     *  1.0=2.0
     *  js> pair=Tools.p('asd',4)
     *  asd=4.0
     *  js> pair.getKey()
     *  asd
     *  js> pair.getValue()
     *  4.0
     */
    public static <A,B> PairMapEntry<A,B> p(A a, B b) {
	return new PairMapEntry<A,B>(a, b);
    }
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
    // suppress harmless "unchecked cast in varargs" for common uses
    /** @see #m(java.util.Map.Entry[]) */
    public static <A,B> Map<A,B> m() {
        @SuppressWarnings("unchecked")
        Map.Entry<A,B>[] items = (Map.Entry<A,B>[]) new Map.Entry[0];
        return m(items);
    }
    /** @see #m(java.util.Map.Entry[]) */
    public static <A,B> Map<A,B> m(Map.Entry<A, B> item) {
        @SuppressWarnings("unchecked")
        Map.Entry<A,B>[] items = (Map.Entry<A,B>[]) new Map.Entry[] { item };
        return m(items);
    }
    /** @see #m(java.util.Map.Entry[]) */
    public static <A,B> Map<A,B> m(Map.Entry<A, B> item1, Map.Entry<A,B> item2) {
        @SuppressWarnings("unchecked")
        Map.Entry<A,B>[] items = (Map.Entry<A,B>[]) new Map.Entry[] { item1, item2 };
        return m(items);
    }
    /** @see #m(java.util.Map.Entry[]) */
    public static <A,B> Map<A,B> m(Map.Entry<A, B> item1, Map.Entry<A,B> item2, Map.Entry<A,B> item3) {
        @SuppressWarnings("unchecked")
        Map.Entry<A,B>[] items = (Map.Entry<A,B>[]) new Map.Entry[] { item1, item2, item3 };
        return m(items);
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
    /** Multimaps of sets.
     * @doc.test
     *  js> map = Tools.mms()
     *  {}
     *  js> map.isEmpty()
     *  true
     *  js> map = Tools.mms(Tools.p(1,2), Tools.p(3,4), Tools.p(5,6))
     *  {1.0=2.0, 3.0=4.0, 5.0=6.0}
     *  js> map.get(3)
     *  4.0
     *  js> map = Tools.mms(Tools.p(1,2), Tools.p(1,3), Tools.p(1,4))
     *  {1.0=[2.0, 3.0, 4.0]}
     * @doc.test The collection is a set; duplicate entries are ignored.
     *  js> map = Tools.mms(Tools.p(1,2), Tools.p(1,3), Tools.p(1,2))
     *  {1.0=[2.0, 3.0]}
     */
    public static <K,V> SetMultiMap<K,V> mms(Map.Entry<K,V>... items) {
	SetMultiMap<K,V> result = new GSMM<K,V>();
	for (Map.Entry<K,V> me : items)
	    result.add(me.getKey(), me.getValue());
	return result;
    }
    // suppress harmless "unchecked cast in varargs" for common uses
    /** @see #mml(java.util.Map.Entry[]) */
    public static <K,V> SetMultiMap<K,V> mms() {
        @SuppressWarnings("unchecked")
        Map.Entry<K,V>[] items = (Map.Entry<K,V>[]) new Map.Entry[0];
        return mms(items);
    }
    /** @see #mml(java.util.Map.Entry[]) */
    public static <K,V> SetMultiMap<K,V> mms(Map.Entry<K, V> item1) {
        @SuppressWarnings("unchecked")
        Map.Entry<K,V>[] items = (Map.Entry<K,V>[]) new Map.Entry[] { item1 };
        return mms(items);
    }
    /** @see #mml(java.util.Map.Entry[]) */
    public static <K,V> SetMultiMap<K,V> mms(Map.Entry<K, V> item1, Map.Entry<K,V> item2) {
        @SuppressWarnings("unchecked")
        Map.Entry<K,V>[] items = (Map.Entry<K,V>[]) new Map.Entry[] { item1, item2 };
        return mms(items);
    }

    /** Multimaps of lists.
     * @doc.test
     *  js> map = Tools.mml()
     *  {}
     *  js> map.isEmpty()
     *  true
     *  js> map = Tools.mml(Tools.p(1,2), Tools.p(3,4), Tools.p(5,6))
     *  {1.0=2.0, 3.0=4.0, 5.0=6.0}
     *  js> map.get(3)
     *  4.0
     *  js> map = Tools.mml(Tools.p(1,2), Tools.p(1,3), Tools.p(1,4))
     *  {1.0=[2.0, 3.0, 4.0]}
     * @doc.test The collection is a list; duplicate entries are kept:
     *  js> map = Tools.mml(Tools.p(1,2), Tools.p(1,3), Tools.p(1,2))
     *  {1.0=[2.0, 3.0, 2.0]}
     */
    public static <K,V> ListMultiMap<K,V> mml(Map.Entry<K,V>... items) {
	ListMultiMap<K,V> result = new GLMM<K,V>();
	for (Map.Entry<K,V> me : items)
	    result.add(me.getKey(), me.getValue());
	return result;
    }
    // suppress harmless "unchecked cast in varargs" for common uses
    /** @see #mml(java.util.Map.Entry[]) */
    public static <K,V> ListMultiMap<K,V> mml() {
        @SuppressWarnings("unchecked")
        Map.Entry<K,V>[] items = (Map.Entry<K,V>[]) new Map.Entry[0];
        return mml(items);
    }
    /** @see #mml(java.util.Map.Entry[]) */
    public static <K,V> ListMultiMap<K,V> mml(Map.Entry<K, V> item1) {
        @SuppressWarnings("unchecked")
        Map.Entry<K,V>[] items = (Map.Entry<K,V>[]) new Map.Entry[] { item1 };
        return mml(items);
    }
    /** @see #mml(java.util.Map.Entry[]) */
    public static <K,V> ListMultiMap<K,V> mml(Map.Entry<K, V> item1, Map.Entry<K,V> item2) {
        @SuppressWarnings("unchecked")
        Map.Entry<K,V>[] items = (Map.Entry<K,V>[]) new Map.Entry[] { item1, item2 };
        return mml(items);
    }


    /** An instance of a {@link net.cscott.jutil.MultiMap} where the
     * contained collections are {@link java.util.Set}s. */
    public static interface SetMultiMap<K,V> extends MultiMap<K,V> {
        Set<V> getValues(K key);
    }
    /**Implementation of {@link SetMultiMap} based on {@link GenericMultiMap}.*/
    private static class GSMM<K,V> extends GenericMultiMap<K,V> implements SetMultiMap<K,V> {
        public GSMM() {
            super(Factories.<K,Collection<V>>linkedHashMapFactory(),
                    Factories.<V>linkedHashSetFactory());
        }
        public Set<V> getValues(K key) { return (Set<V>) super.getValues(key); }
    }
    /** An instance of a {@link net.cscott.jutil.MultiMap} where the
     * contained collections are {@link java.util.List}s. */
    public static interface ListMultiMap<K,V> extends MultiMap<K,V> {
        List<V> getValues(K key);
    }
    /**Implementation of {@link ListMultiMap} based on {@link GenericMultiMap}.*/
    private static class GLMM<K,V> extends GenericMultiMap<K,V> implements ListMultiMap<K,V> {
        public GLMM() {
            super(Factories.<K,Collection<V>>linkedHashMapFactory(),
                    Factories.<V>arrayListFactory());
        }
        public List<V> getValues(K key) { return (List<V>) super.getValues(key); }
    }

    /** Quasi-list comprehension. */
    // xxx: possibly still too verbose to be useful
    public static <A,B> List<B> foreach(List<A> list, FilterIterator.Filter<A,B> filter) {
        List<B> result = new ArrayList<B>(list.size());
        Iterator<B> it = new FilterIterator<A,B>(list.iterator(), filter);
        while (it.hasNext())
            result.add(it.next());
        return result;
    }
    /** Convenience alias for {@link net.cscott.jutil.FilterIterator.Filter}. */
    public static abstract class F<A,B> extends FilterIterator.Filter<A,B> { }
}
