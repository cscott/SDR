package net.cscott.sdr.util;

/**
 * How a {@link ToStringBuilder} formats an object: whether to show its
 * class name and field names, and what to put around and between the
 * fields.
 * @author C. Scott Ananian
 */
public record ToStringStyle(boolean useClassName, boolean useShortClassName,
                            boolean useFieldNames, String contentStart,
                            String fieldSeparator,
                            boolean fieldSeparatorAtStart,
                            String contentEnd) {
    /** Just the values, separated by commas: <code>1,2,e</code>. */
    public static final ToStringStyle SIMPLE_STYLE =
        new ToStringStyle(false, false, false, "", ",", false, "");
    /** The short class name, then the fields:
     *  <code>DancerPath[from=...,to=...]</code>. */
    public static final ToStringStyle SHORT_PREFIX_STYLE =
        new ToStringStyle(true, true, true, "[", ",", false, "]");
    /** The full class name, then one field per line.  Unlike the
     *  commons-lang multi-line style, this omits the identity hash code,
     *  which would cause problems with doctests. */
    public static final ToStringStyle MULTI_LINE_STYLE =
        new ToStringStyle(true, false, true, "[", "\n  ", true, "\n]");
}
