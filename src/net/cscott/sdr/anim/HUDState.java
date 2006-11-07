package net.cscott.sdr.anim;

import java.awt.Font;
import java.net.URL;

import net.cscott.sdr.BeatTimer;
import net.cscott.sdr.Version;
import net.cscott.sdr.anim.TextureText.JustifyX;
import net.cscott.sdr.anim.TextureText.JustifyY;

import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.scene.state.LightState;
import com.jme.system.DisplaySystem;
import com.jmex.game.state.StandardGameStateDefaultCamera;

public class HUDState extends StandardGameStateDefaultCamera {
    /** Our display system. */
    private DisplaySystem display;
    /** What beat are we at? */
    private BeatTimer beatTimer;
    /** The Score label. */
    private TextureText score;
    /** The "current call" label. */
    private TextureText call;
    /** The font to use for the HUD. */
    private static Font font;
    static { // initialize the font.
        URL url = TextureText.class.getClassLoader().getResource      
        ("net/cscott/sdr/fonts/bluebold.ttf");
        try {
            font=Font.createFont(Font.TRUETYPE_FONT, url.openStream());
        } catch (Exception e) { assert false : e; }
    }


    public HUDState(BeatTimer beatTimer) {
        super("HUD");
        this.display = DisplaySystem.getDisplaySystem();
        this.beatTimer = beatTimer;
        initInput();
        initHUD();

        rootNode.setLightCombineMode(LightState.OFF);
        rootNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);
        rootNode.updateRenderState();
        rootNode.updateGeometricState(0, true);
    }

    private void initInput() { }
    private void initHUD() {
        // labels.
        TextureText ttOrig = mkTopTitle("ORIGINALITY", x(16+0*148), y(466));
        TextureText ttTimF = mkTopTitle("TIMING & FLOW", x(16+1*148), y(466));
        TextureText ttSeqL = mkTopTitle("SEQUENCE LENGTH",x(16+2*148), y(466));
        TextureText ttScor = mkTopTitle("SCORE", x(490), y(466));
        
        this.score = mkText("Score display", "150,000", 128, JustifyX.RIGHT, JustifyY.TOP, x(640),y(464), x(196),y(40));

        TextureText ttHelp = mkText("F1 for help, Esc to quit", 64, JustifyX.RIGHT, JustifyY.BOTTOM, x(640),y(0), x(200), y(12));
        
        this.call = mkText("Call display", "Square thru 4 hands around", 128, JustifyX.LEFT, JustifyY.BOTTOM, x(32), y(10), x(640-32), y(24));
        
        TextureText ttNotice = mkText("Notice", "Last Sequence!", 128, JustifyX.CENTER, JustifyY.MIDDLE, x(320), y(240), x(640), y(26));
    }
    private TextureText mkTopTitle(String title, float x, float y) {
        return mkText("Top Title: ", title, 64, JustifyX.LEFT, JustifyY.BOTTOM, x, y, x(132), y(12));
    }
    private TextureText mkText(String text, int textureSize, JustifyX alignX, JustifyY alignY, float x, float y, float width, float height) {
        return mkText("Text: ", text, textureSize, alignX, alignY, x, y, width, height);
    }
    private TextureText mkText(String nodePrefix, String text, int textureSize, JustifyX alignX, JustifyY alignY, float x, float y, float width, float height) {
        TextureText tt = new TextureText(nodePrefix+text, font, textureSize);
        tt.setAlign(alignX, alignY);
        tt.setMaxSize(width,height);
        tt.setLocalTranslation(new Vector3f(x,y,0));
        tt.setText(text);
        rootNode.attachChild(tt);
        return tt;
    }
    
    // convert 640x480-relative coordinates to appropriately scaled coords.
    private float x(int x) {
        return x*display.getWidth()/640f;
    }
    private float y(int y) {
        return y*display.getHeight()/480f;
    }
    
    @Override
    protected void onActivate() {
        display.setTitle(Version.PACKAGE_STRING+": Let's Play!");
    }
    @Override
    protected void stateUpdate(float tpf) {
        score.setText("f#"+(counter++)); // for debugging
        rootNode.updateGeometricState(tpf, true);
    }
    private int counter=0;
}
