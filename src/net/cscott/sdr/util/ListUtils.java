package net.cscott.sdr.util;

import java.util.Collection;
import java.util.Iterator;

import net.cscott.jdoctest.JDoctestRunner;

import org.junit.runner.RunWith;

/** Convenience functions for lists and strings. */
@RunWith(value=JDoctestRunner.class)
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
}
