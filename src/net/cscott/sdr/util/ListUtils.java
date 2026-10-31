package net.cscott.sdr.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.cscott.jdoctest.Doctests;

/** Convenience functions for lists and strings. */
@Doctests
public final class ListUtils {
    private ListUtils() { /* hide the constructor */ }

    /**
     * Return the string created by inserting {@code insert} between
     * the members of {@code c}.
     *
     * @doc.test Note that non-string types are converted to Strings:
     * js> ListUtils.join(Tools.l(1), " + ")
     * 1.0
     * js> ListUtils.join(Tools.l(1, 2), " + ")
     * 1.0 + 2.0
     * js> ListUtils.join(Tools.l(1, 2, 3), " + ")
     * 1.0 + 2.0 + 3.0
     */
    public static String join(Collection<?> c, String insert) {
        return join(c, insert, insert);
    }
    /**
     * Return the string created by inserting {@code insert} between
     * the members of {@code c}, and {@code preFinal} before the last one.
     *
     * @doc.test
     * js> ListUtils.join(Tools.l("apples"), ", ", " or ")
     * apples
     * js> ListUtils.join(Tools.l("apples", "oranges"), ", ", " or ")
     * apples or oranges
     * js> ListUtils.join(Tools.l("apples", "oranges", "pears"), ", ", " or ")
     * apples, oranges or pears
     * @doc.test Non-string types are converted to strings:
     * js> ListUtils.join(Tools.l(1, 2, 3), " + ", " - ")
     * 1.0 + 2.0 - 3.0
     */
    public static String join(Collection<?> c, String insert, String preFinal) {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Iterator<?> it = c.iterator(); it.hasNext(); first=false) {
            String s = it.next().toString();
            if (!first) {
                result.append(it.hasNext() ? insert : preFinal);
            }
            result.append(s);
        }
        return result.toString();
    }

    /**
     * Return an array of the remaining elements of {@code it}.  JavaScript
     * can't iterate over a Java {@link Iterator}, but it can iterate over
     * the array, and call {@code map()} or {@code every()} on it.
     * Use {@code Array.from()} to print it.
     *
     * @doc.test
     * js> it = Tools.l("a", "b", "c").iterator(); it.next()
     * a
     * js> ListUtils.toArray(it).map(s => s.toUpperCase())
     * B,C
     * js> ListUtils.toArray(it).length
     * 0
     */
    public static Object[] toArray(Iterator<?> it) {
        List<Object> result = new ArrayList<Object>();
        while (it.hasNext())
            result.add(it.next());
        return result.toArray();
    }
}
