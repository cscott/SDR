package net.cscott.sdr.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.cscott.jutil.UnmodifiableIterator;

/** Persistent Linked List. */
public class LL<T> implements Iterable<T> {
    /** Element at the start of the list */
    public final T head;
    /** Remainder of the elements in the list. */
    public final LL<T> tail;
    /** Add an element to the front of <code>tail</code>. */
    public LL(T head, LL<T> tail) { this.head=head; this.tail=tail; }
    /**
     * Factory: create a list.
     * @doc.test
     *  js> l = LL.create("a","b","c","d");
     *  [a, b, c, d]
     */
    public static <T> LL<T> create(T... elements) {
        LL<T> result = NULL();
        for (T t: elements)
            result = result.push(t);
        return result.reverse();
    }
    /**
     * Factory: create a list.
     * @doc.test
     *  js> l = LL.create(java.util.Arrays.asList("a","b","c","d"));
     *  [a, b, c, d]
     */
    public static <T> LL<T> create(List<T> elements) {
        LL<T> result = NULL();
        for (T t: elements)
            result = result.push(t);
        return result.reverse();
    }
    /**
     * Add the given element to the start of the list.
     * @doc.test
     *  js> l = LL.create("a","b","c","d");
     *  [a, b, c, d]
     *  js> l = l.push("e");
     *  [e, a, b, c, d]
     *  js> l = l.push("f");
     *  [f, e, a, b, c, d]
     */
    public LL<T> push(T head) {
        return new LL<T>(head, this);
    }
    /**
     * Remove the head of the list.
     * @doc.test
     *  js> l = LL.create("a","b","c","d");
     *  [a, b, c, d]
     *  js> l = l.pop();
     *  [b, c, d]
     *  js> l = l.pop();
     *  [c, d]
     *  js> l = l.pop();
     *  [d]
     *  js> l = l.pop();
     *  []
     */
    public LL<T> pop() {
        assert !this.isEmpty();
        return this.tail;
    }
    /**
     * Are there any elements in this list?
     * @doc.test
     *  js> l = LL.create("a","b","c","d");
     *  [a, b, c, d]
     *  js> l.isEmpty()
     *  false
     *  js> LL.NULL().isEmpty()
     *  true
     */
    public boolean isEmpty() {
        return this==NULL();
    }
    /**
     * Return the number of items in this list.
     * @doc.test
     *  js> l = LL.create("a","b","c","d");
     *  [a, b, c, d]
     *  js> l.size()
     *  4
     *  js> LL.NULL().size()
     *  0
     */
    public int size() {
        int sz = 0;
        for (LL<T> l = this; l!=NULL(); l=l.tail)
            sz += 1;
        return sz;
    }
    /**
     * Iterate over all the elements in this list from head to tail.
     * @doc.test
     *  js> l = LL.create("a","b","c","d");
     *  [a, b, c, d]
     *  js> [x for (x in Iterator(l))]
     *  a,b,c,d
     *  js> [x for (x in Iterator(LL.NULL()))].length
     *  0
     */
    public Iterator<T> iterator() {
        return new LLIterator<T>(this);
    }
    /** Helper class to implement the {@link Iterable} interface. */
    private static class LLIterator<T> extends UnmodifiableIterator<T> {
        LL<T> next;
        LLIterator(LL<T> ll) { this.next = ll; }
        @Override
        public boolean hasNext() { return next!=NULL(); }
        @Override
        public T next() {
            assert hasNext();
            T retval = next.head;
            next = next.tail;
            return retval;
        }
    }
    @SuppressWarnings("unchecked")
    private static final LL NULL = new LL<Object>(null,null);
    /**
     * Return a new empty list.
     * @doc.test
     *  js> LL.NULL();
     *  []
     *  js> LL.NULL().isEmpty();
     *  true
     */
    @SuppressWarnings("unchecked")
    public static <T> LL<T> NULL() { return (LL<T>) NULL; }
    /**
     * Return a list with elements in reverse order.
     * @doc.test
     *  js> l = LL.create("asda","bar","foo");
     *  [asda, bar, foo]
     *  js> l.reverse();
     *  [foo, bar, asda]
     *  js> l = LL.NULL();
     *  []
     *  js> l.reverse();
     *  []
     */
    public LL<T> reverse() {
        LL<T> result = NULL();
        for(LL<T> l = this; l!=NULL; l=l.tail)
            result = new LL<T>(l.head, result);
        return result;
    }
    @Override
    public String toString() {
        return toList().toString();
    }
    /* REMOVED for GWT compatibility (GWT doesn't have java.lang.reflect.Array)
    @SuppressWarnings("unchecked")
    public T[] toArray(Class<T> type) {
        T[] result =(T[])java.lang.reflect.Array.newInstance(type, this.size());
        int i=0;
        for (T t : this)
            result[i++] = t;
        return result;
    }
    * END REMOVAL */
    public List<T> toList() {
        List<T> result = new ArrayList<T>(this.size());
        for (T t : this)
            result.add(t);
        return result;
    }
}