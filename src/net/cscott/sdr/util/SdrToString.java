package net.cscott.sdr.util;

import org.apache.commons.lang.builder.StandardToStringStyle;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Custom {@link ToStringStyle} which is similar to the
 * {@link ToStringStyle#MULTI_LINE_STYLE} but suppresses the identity hash
 * code component, which can cause problems with doctests.
 * @author C. Scott Ananian
 */
public class SdrToString extends StandardToStringStyle {
    public static final ToStringStyle STYLE=new SdrToString();
    private SdrToString() {
        // some basic settings taken from the MULTI_LINE style
        this.setContentStart("[");
        this.setFieldSeparator("\n  ");
        this.setFieldSeparatorAtStart(true);
        this.setContentEnd("\n]");
        // this is the key here:
        this.setUseIdentityHashCode(false);
    }
}
