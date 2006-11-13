package net.cscott.sdr.anim;

import java.awt.Font;
import java.net.URL;

import net.cscott.sdr.anim.TextureText.JustifyX;
import net.cscott.sdr.anim.TextureText.JustifyY;

import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.LightState;
import com.jme.system.DisplaySystem;
import com.jmex.game.state.StandardGameStateDefaultCamera;

abstract class BaseState extends StandardGameStateDefaultCamera {
    /** Our display system. */
    final DisplaySystem display;
    /** Default font for our app. */
    static Font font;
    static { // initialize the font.
        URL url = TextureText.class.getClassLoader().getResource      
        ("net/cscott/sdr/fonts/bluebold.ttf");
        try {
            font=Font.createFont(Font.TRUETYPE_FONT, url.openStream());
        } catch (Exception e) { assert false : e; }
    }
    BaseState(String name) {
        super(name);
        this.display = DisplaySystem.getDisplaySystem();
        rootNode.setLightCombineMode(LightState.OFF);
        rootNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);
    }
    //  UTILITY METHODS
    static AlphaState mkAlpha() {
        if (sharedAlpha==null) {
            sharedAlpha = DisplaySystem.getDisplaySystem()
            .getRenderer().createAlphaState();
            sharedAlpha.setBlendEnabled(true);
            sharedAlpha.setSrcFunction(AlphaState.SB_SRC_ALPHA);
            sharedAlpha.setDstFunction(AlphaState.DB_ONE_MINUS_SRC_ALPHA);
            sharedAlpha.setTestEnabled(false);
            sharedAlpha.setEnabled(true);
        }
        return sharedAlpha;
    }
    private static AlphaState sharedAlpha = null;
    
    Quad mkShade(String nodeName, float x, float y, float width, float height) {
        Quad q = new Quad(nodeName, width, height);
        q.setDefaultColor(new ColorRGBA(0,0,0,.55f));
        q.setLocalTranslation(new Vector3f(x,y,0));
        q.setRenderState(mkAlpha());
        rootNode.attachChild(q);
        q.updateRenderState();
        return q;
    }
    TextureText mkText(String text, int textureSize, JustifyX alignX, JustifyY alignY, float x, float y, float width, float height) {
        return mkText("Text: ", text, textureSize, alignX, alignY, x, y, width, height);
    }
    TextureText mkText(String nodePrefix, String text, int textureSize, JustifyX alignX, JustifyY alignY, float x, float y, float width, float height) {
        TextureText tt = _mkText(nodePrefix, text, textureSize, alignX, alignY, x, y, width, height);
        rootNode.attachChild(tt);
        return tt;
    }
    TextureText _mkText(String nodePrefix, String text, int textureSize, JustifyX alignX, JustifyY alignY, float x, float y, float width, float height) {
        TextureText tt = new TextureText(nodePrefix+text, font, textureSize);
        tt.setAlign(alignX, alignY);
        tt.setMaxSize(width,height);
        tt.setLocalTranslation(new Vector3f(x,y,0));
        tt.setText(text);
        return tt;
    }

    //---------------------------------------------------------
    // convert 640x480-relative coordinates to appropriately scaled coords.
    final float x(int x) {
        return x*display.getWidth()/640f;
    }
    final float y(int y) {
        return y*display.getHeight()/480f;
    }
}
