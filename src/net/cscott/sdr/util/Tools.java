package net.cscott.sdr.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.cscott.jutil.PairMapEntry;

/**
 * Utility functions to facilitate map and list creation.
 * @author C. Scott Ananian
 */
public abstract class Tools {
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
     *  js> map = Tools.m(Tools.p(1,2), Tools.p(3,4))
     *  {1.0=2.0, 3.0=4.0}
     *  js> map.get(3)
     *  4.0
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
}
