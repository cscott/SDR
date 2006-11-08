package net.cscott.sdr.anim;

import java.awt.Color;
import java.awt.Font;
import java.net.URL;
import java.text.DecimalFormat;

import net.cscott.sdr.BeatTimer;
import net.cscott.sdr.Version;
import net.cscott.sdr.anim.TextureText.JustifyX;
import net.cscott.sdr.anim.TextureText.JustifyY;
import net.cscott.sdr.util.Fraction;

import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.LightState;
import com.jme.system.DisplaySystem;
import com.jmex.game.state.StandardGameStateDefaultCamera;

public class HUDState extends StandardGameStateDefaultCamera {
    /** Our display system. */
    private DisplaySystem display;
    /** What beat are we at? */
    private BeatTimer beatTimer;
    /** The Score label. */
    private TextureText scoreText;
    /** The "current call" label. */
    private TextureText callText;
    /** A notice in the center of the screen. */
    private TextureText noticeText;
    /** The shade behind the notice. */
    private Quad noticeShade;
    /** A bonus in the top-right corner. */
    private TextureText bonusText;
    /** The "originality" gauge. */
    private Gauge origGauge;
    /** The "timing & flow" gauge. */
    private Gauge timeFlowGauge;
    /** The "sequence length" gauge. */
    private Gauge seqLenGauge;
    
    /** The font to use for the HUD. */
    static Font font;
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
        initHUD();

        rootNode.setLightCombineMode(LightState.OFF);
        rootNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);
        rootNode.updateRenderState();
        rootNode.updateGeometricState(0, true);
    }

    private void initHUD() {
        // first background shading:
        //   shading behind notice.
        this.noticeShade = mkShade(rootNode, "Notice Shade", x(320), y(240), x(20), y(20));
        this.noticeShade.setCullMode(Spatial.CULL_ALWAYS);
        
        // labels.
        mkTopTitle("ORIGINALITY", x(16+0*148), y(466));
        mkTopTitle("TIMING & FLOW", x(16+1*148), y(466));
        mkTopTitle("SEQUENCE LENGTH",x(16+2*148), y(466));
        mkTopTitle("SCORE", x(490), y(466));
        
        this.scoreText = mkText("Score display: ", "150,000", 128, JustifyX.RIGHT, JustifyY.TOP, x(640),y(464), x(196),y(40));

        mkText("F1 for help, Esc to quit", 64, JustifyX.RIGHT, JustifyY.BOTTOM, x(640),y(0), x(200), y(12));
        
        this.callText = mkText("Call display: ", "Square thru 4 hands around", 128, JustifyX.LEFT, JustifyY.BOTTOM, x(32), y(10), x(640-32), y(24));
        
        this.noticeText = mkText("Notice: ", "Last Sequence!", 128, JustifyX.CENTER, JustifyY.MIDDLE, x(320), y(240), x(640), y(26));
        this.noticeText.setCullMode(Spatial.CULL_ALWAYS);
        
        this.bonusText = mkText("Bonus: ", "Right-hand Columns", 128, JustifyX.LEFT, JustifyY.TOP, x(490), y(410), x(150), y(25));
        this.bonusText.setColor(new ColorRGBA(1,1,0,1));

        // gauges
        this.origGauge = new Gauge("originality gauge",
                new Color[] { Color.red, Color.yellow, Color.green },
                0f, .5f, 1f);
        origGauge.setLocalTranslation(new Vector3f(x(16+0*148),y(464),0));
        rootNode.attachChild(origGauge);

        this.timeFlowGauge = new Gauge("flow gauge",
                new Color[] { Color.red, Color.yellow, Color.green },
                0f, .3f, 1f);
        timeFlowGauge.setLocalTranslation(new Vector3f(x(16+1*148),y(464),0));
        rootNode.attachChild(timeFlowGauge);

        this.seqLenGauge = new Gauge("sequence length gauge",
                new Color[] { Color.red, Color.yellow, Color.green, Color.yellow, Color.red },
                0f, .2f, .5f, .8f, 1f);
        seqLenGauge.setLocalTranslation(new Vector3f(x(16+2*148),y(464),0));
        rootNode.attachChild(seqLenGauge);

        origGauge.update(0.3f);
        timeFlowGauge.update(0.5f);
        seqLenGauge.update(0.7f);
        
    }
    private void setNotice(final String notice) {
        if (notice == null) {
            noticeText.setCullMode(Spatial.CULL_ALWAYS);
            noticeShade.setCullMode(Spatial.CULL_ALWAYS);
        } else {
            noticeText.setText(notice);
            noticeShade.resize(noticeText.getWidth()+x(20),
                               noticeText.getHeight()+y(20));
            noticeText.setCullMode(Spatial.CULL_NEVER);
            noticeShade.setCullMode(Spatial.CULL_NEVER);
        }
    }
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
    static Quad mkShade(Node rootNode, String nodeName, float x, float y, float width, float height) {
        Quad q = new Quad(nodeName, width, height);
        q.setDefaultColor(new ColorRGBA(0,0,0,.55f));
        q.setLocalTranslation(new Vector3f(x,y,0));
        q.setRenderState(mkAlpha());
        rootNode.attachChild(q);
        q.updateRenderState();
        return q;
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
    
    private void updateScore(int score) {
        // format the score prettily
        this.scoreText.setText(scoreFormat.format(score).toString());
    }
    private static final DecimalFormat scoreFormat =new DecimalFormat("#,###");
    
    
    @Override
    protected void onActivate() {
        display.setTitle(Version.PACKAGE_STRING+": Let's Play!");
    }
    @Override
    protected void stateUpdate(float tpf) {
        // for debugging.
        if ((counter++ % 200) < 100) setNotice(null);
        else setNotice("Last sequence!");
        
        Fraction currentBeat = beatTimer.getCurrentBeat();
        Fraction partialBeat = Fraction.valueOf
             (currentBeat.getProperNumerator(),currentBeat.getDenominator());
        updateScore((int)Math.floor(1+partialBeat.floatValue()*4)); // debugging

        timeFlowGauge.update(partialBeat.floatValue());//debugging
        
        rootNode.updateGeometricState(tpf, true);
    }
    private int counter=0;
    
    //---------------------------------------------------------
    // convert 640x480-relative coordinates to appropriately scaled coords.
    private float x(int x) {
        return x*display.getWidth()/640f;
    }
    private float y(int y) {
        return y*display.getHeight()/480f;
    }
}
