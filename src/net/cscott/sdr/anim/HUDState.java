package net.cscott.sdr.anim;

import java.awt.Color;
import java.text.DecimalFormat;

import net.cscott.sdr.BeatTimer;
import net.cscott.sdr.Version;
import net.cscott.sdr.anim.TextureText.JustifyX;
import net.cscott.sdr.anim.TextureText.JustifyY;
import net.cscott.sdr.util.Fraction;

import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Quad;

public class HUDState extends BaseState {
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
    


    public HUDState(BeatTimer beatTimer) {
        super("HUD");
        this.beatTimer = beatTimer;
        initHUD();
        rootNode.updateRenderState();
        rootNode.updateGeometricState(0, true);
    }

    private void initHUD() {
        // first background shading:
        //   shading behind notice.
        this.noticeShade = mkShade("Notice Shade", x(320), y(240), x(20), y(20));
        this.noticeShade.setCullMode(Spatial.CULL_ALWAYS);
        
        // labels.
        mkTopTitle("ORIGINALITY", x(16+0*148), y(466));
        mkTopTitle("TIMING & FLOW", x(16+1*148), y(466));
        mkTopTitle("SEQUENCE LENGTH",x(16+2*148), y(466));
        mkTopTitle("SCORE", x(490), y(466));
        
        this.scoreText = mkText("Score display: ", "150,000", 128, JustifyX.RIGHT, JustifyY.TOP, x(640),y(464), x(196),y(56));

        mkText("F1 for help, Esc to quit", 64, JustifyX.RIGHT, JustifyY.BOTTOM, x(640),y(0), x(200), y(14));
        
        this.callText = mkText("Call display: ", "Square thru 4 hands around", 128, JustifyX.LEFT, JustifyY.BOTTOM, x(32), 3, x(640-32), 36);
        
        this.noticeText = mkText("Notice: ", "Last Sequence!", 128, JustifyX.CENTER, JustifyY.MIDDLE, x(320), y(240), x(640), y(26));
        this.noticeText.setCullMode(Spatial.CULL_ALWAYS);
        
        this.bonusText = mkText("Bonus: ", "Right-hand Columns", 128, JustifyX.LEFT, JustifyY.TOP, x(490), y(410), x(150), y(25));
        this.bonusText.setColor(new ColorRGBA(1,1,0,1));

        // gauges
        this.origGauge = new Gauge("originality gauge", false,
                new Color[] { Color.red, Color.yellow, Color.green }, .5f);
        origGauge.setLocalTranslation(new Vector3f(x(16+0*148),y(464),0));
        rootNode.attachChild(origGauge);

        this.timeFlowGauge = new Gauge("flow gauge", false,
                new Color[] { Color.red, Color.yellow, Color.green }, .3f);
        timeFlowGauge.setLocalTranslation(new Vector3f(x(16+1*148),y(464),0));
        rootNode.attachChild(timeFlowGauge);

        this.seqLenGauge = new Gauge("sequence length gauge", true,
                new Color[] { Color.red, Color.yellow, Color.green }, .5f);
        seqLenGauge.setLocalTranslation(new Vector3f(x(16+2*148),y(464),0));
        rootNode.attachChild(seqLenGauge);

        origGauge.update(0.3f);
        timeFlowGauge.update(0.5f);
        seqLenGauge.update(0.95f);
        
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
    private TextureText mkTopTitle(String title, float x, float y) {
        return mkText("Top Title: ", title, 64, JustifyX.LEFT, JustifyY.BASELINE, x, y, x(132), y(16));
    }
    
    public void updateScore(int score) {
        // format the score prettily
        this.scoreText.setText(scoreFormat.format(score).toString());
    }
    private static final DecimalFormat scoreFormat =new DecimalFormat("#,###");
    
    public void updateCall(String call) {
        this.callText.setText(call);
    }
    
    
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
}
