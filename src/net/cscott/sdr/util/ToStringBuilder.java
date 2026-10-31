package net.cscott.sdr.util;

import java.lang.reflect.Array;

/**
 * Builds a {@link Object#toString()} result from a list of fields, in
 * the format of the given {@link ToStringStyle}.  This replaces the
 * commons-lang class of the same name, and formats its output the same
 * way.
 * @author C. Scott Ananian
 */
public class ToStringBuilder {
    private final ToStringStyle style;
    private final StringBuilder sb = new StringBuilder();

    public ToStringBuilder(Object object, ToStringStyle style) {
        this.style = style;
        if (style.useClassName())
            sb.append(className(object.getClass()));
        sb.append(style.contentStart());
        if (style.fieldSeparatorAtStart())
            sb.append(style.fieldSeparator());
    }

    /** Append a field with the given name and value. */
    public ToStringBuilder append(String fieldName, Object value) {
        if (style.useFieldNames() && fieldName != null)
            sb.append(fieldName).append('=');
        appendValue(value);
        sb.append(style.fieldSeparator());
        return this;
    }

    /** Append the fields from the given result of a superclass's
     *  {@link Object#toString()}, which used the same style. */
    public ToStringBuilder appendSuper(String superToString) {
        if (superToString == null) return this;
        int start = superToString.indexOf(style.contentStart()) +
            style.contentStart().length();
        int end = superToString.lastIndexOf(style.contentEnd());
        if (start != end && start >= 0 && end >= 0) {
            if (style.fieldSeparatorAtStart())
                removeLastFieldSeparator();
            sb.append(superToString, start, end);
            sb.append(style.fieldSeparator());
        }
        return this;
    }

    @Override
    public String toString() {
        removeLastFieldSeparator();
        return sb + style.contentEnd();
    }

    private void removeLastFieldSeparator() {
        String sep = style.fieldSeparator();
        int len = sb.length() - sep.length();
        if (len >= 0 && sb.indexOf(sep, len) == len)
            sb.setLength(len);
    }

    private void appendValue(Object value) {
        if (value == null) {
            sb.append("<null>");
        } else if (value.getClass().isArray()) {
            sb.append('{');
            for (int i = 0; i < Array.getLength(value); i++) {
                if (i > 0) sb.append(',');
                appendValue(Array.get(value, i));
            }
            sb.append('}');
        } else {
            sb.append(value);
        }
    }

    private String className(Class<?> c) {
        String name = c.getName();
        if (!style.useShortClassName()) return name;
        return name.substring(name.lastIndexOf('.') + 1).replace('$', '.');
    }
}
