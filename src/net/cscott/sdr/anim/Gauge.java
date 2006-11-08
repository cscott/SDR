package net.cscott.sdr.anim;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.image.BufferedImage;
import java.lang.reflect.Constructor;

import com.jme.app.SimpleGame;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.state.LightState;

/** A {@link Gauge} is a jme component that implements a thermometer-type
 * indicator.  Gauges are 128 pixels wide by 24 pixels tall by default,
 * and display a value between 0 and 1.  The origin of the gauge is at its
 * top-left corner.  The bar grows left-to-right, and '0' is at the left.
 * 
 * @author C. Scott Ananian
 * @version $Id: Gauge.java,v 1.1 2006-11-08 22:45:37 cananian Exp $
 */
public class Gauge extends Node {
    private final TexturedQuad quad;
    private final Paint paint;

    public Gauge(String nodeName, Color leftColor, Color rightColor) {
        this(nodeName, new Color[]{ leftColor, rightColor }, 0f, 1f );
    }
    public Gauge(String nodeName, Color[] colors, float... points) {
        super(nodeName);
        assert colors.length==points.length;
        assert points[0]==0f && points[points.length-1]==1f;
        this.quad = new TexturedQuad(nodeName+"/internal quad", 128);
        quad.setLocalTranslation(new Vector3f(64,-64,0));
        attachChild(quad);
        Paint p;
        try {
            Class c = Class.forName("java.awt.LinearGradientPaint");
            Constructor cc = c.getConstructor
            (float.class,float.class,float.class,float.class,float[].class,
                    Color[].class);
            p = (Paint) cc.newInstance(0f,0f,128f,0f,points,colors);
        } catch (Throwable t) {
            // we must be using java 1.5 (not 1.6); use a simple GradientPaint
            p = new GradientPaint(0,0,colors[0],128,0,colors[colors.length-1]);
        }
        this.paint = p;
    }
    /** Update the value displayed in this {@link Gauge}.  The value should
     * be between 0 (no bar displayed) and 1 (full bar displayed).
     */
    public void update(float value) {
        int v = Math.round(123*value);
        if (v==oldVal) return; // no change.
        oldVal=v;
        BufferedImage bi = quad.getTextureImage();
        Graphics2D g2 = bi.createGraphics();
        g2.translate(0,128);
        g2.scale(1,-1);
        g2.setColor(Color.gray);
        g2.draw3DRect(0,0,127,23,true);
        g2.draw3DRect(1,1,125,21,false);
        g2.setPaint(this.paint);
        g2.fillRect(2,2,v,20);
        g2.dispose();
        quad.updateTexture(bi);
    }
    private int oldVal=-1; // force first update

    /**
     * Simple test harness to exercise the features of this class.
     */
    public static void main(String[] args) throws Exception {
        SimpleGame game = new SimpleGame() {
            @Override
            protected void simpleInitGame() {
                setDialogBehaviour(SimpleGame.ALWAYS_SHOW_PROPS_DIALOG);
                // Set a green background, so we can see the transparency.
                display.getRenderer().setBackgroundColor( ColorRGBA.green );
                Gauge g = new Gauge("Test", Color.green, Color.red);
                g.setRenderQueueMode(Renderer.QUEUE_ORTHO);
                g.setLightCombineMode(LightState.OFF);
                g.setLocalTranslation(new Vector3f(display.getWidth()/2,display.getHeight()/2,0));
                g.update(.5f);
                rootNode.attachChild(g);
            }
        };
        game.start();
    }
}
