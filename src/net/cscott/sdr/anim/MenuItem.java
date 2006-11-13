package net.cscott.sdr.anim;

import net.cscott.sdr.anim.TextureText.JustifyX;
import net.cscott.sdr.anim.TextureText.JustifyY;

import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;

/**
 * A {@link MenuItem} is one row of the {@link MenuState}.  It is in charge
 * of its own highlight state and input processing.
 * @author C. Scott Ananian
 * @version $Id: MenuItem.java,v 1.1 2006-11-13 05:07:33 cananian Exp $
 */
public class MenuItem extends Node {
    private final TextureText label, value;
    private final MenuArrow leftArrow, rightArrow;
    private final String[] valueText;
    private int which = 0;
    public MenuItem(String nodeName, String labelText, BaseState st, String... valueText) {
        super(nodeName+"/Node");
        this.valueText = valueText;
        // menu label
        this.label = st._mkText(nodeName+"/label:", labelText, 128,
                JustifyX.LEFT, JustifyY.MIDDLE, st.x(83-320),st.y(0),st.x(280),st.y(44));
        label.setColor(new ColorRGBA(.95f,.95f,.95f,1));
        this.attachChild(label);
        // menu arrows: height 44, width 24

        this.leftArrow = new MenuArrow
        (nodeName+"/arrow/left", st, true);
        leftArrow.getLocalTranslation().set(st.x(468-75-1-320),st.y(0),0);
        this.attachChild(leftArrow);

        this.rightArrow = new MenuArrow
        (nodeName+"/arrow/right", st, false);
        rightArrow.getLocalTranslation().set(st.x(468+75+1-320),st.y(0),0);
        this.attachChild(rightArrow);

        // menu values
        this.value = st._mkText(nodeName+"/value:", getValue(which), 128,
                JustifyX.CENTER, JustifyY.MIDDLE, st.x(468-320), 0, st.x(150),st.y(24));
        value.setColor(new ColorRGBA(1,1,0,1));
        this.attachChild(value);
    }
    public void setSelected(boolean isSelected) {
        float l = isSelected ? 1f : .95f;
        this.label.setColor(new ColorRGBA(l, l, l, 1));
        this.value.setColor(new ColorRGBA(1,1,isSelected?.5f:0,1));
        // XXX this is wrong.
        leftArrow.setSelected(isSelected);
        rightArrow.setSelected(isSelected);
    }
    protected String getValue(int which) { return valueText[which]; }
    private void update() {
        this.value.setText(getValue(which));
        onChange(which);
    }
    public void inc() {
        if (which<valueText.length-1) { which++; update(); }
    }
    public void dec() {
        if (which>0) { which--; update(); }
    }
    /** Subclasses can override this method to get notification of state
     * changes. */
    protected void onChange(int which) { }
}
