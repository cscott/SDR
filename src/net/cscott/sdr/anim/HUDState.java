package net.cscott.sdr.anim;

import java.awt.Color;
import java.text.DecimalFormat;

import net.cscott.sdr.Version;
import net.cscott.sdr.anim.TextureText.JustifyX;
import net.cscott.sdr.anim.TextureText.JustifyY;

import com.jme.input.MouseInput;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Quad;

public class HUDState extends BaseState {
    /** Source of values displayed */
    private final HUD hud;
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


    public HUDState(HUD hud) {
        super("HUD");
        this.hud = hud;
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

        this.scoreText = mkText("Score display: ", "0", 128, JustifyX.RIGHT, JustifyY.TOP, x(640-8),y(464), x(196),y(56));

        mkText("F1 for help, Esc to quit", 64, JustifyX.RIGHT, JustifyY.BOTTOM, x(640),y(0), x(200), y(14));

        this.callText = mkText("Call display: ", "Square thru 4 hands around", 128, JustifyX.LEFT, JustifyY.BOTTOM, x(32), 3, x(640-32), 36);
        this.callText.setCullMode(Spatial.CULL_ALWAYS);

        this.noticeText = mkText("Notice: ", "Last Sequence!", 128, JustifyX.CENTER, JustifyY.MIDDLE, x(320), y(240), x(640), y(26));
        this.noticeText.setCullMode(Spatial.CULL_ALWAYS);

        this.bonusText = mkText("Bonus: ", "Right-hand Columns", 128, JustifyX.LEFT, JustifyY.TOP, x(490), y(410), x(150), y(25));
        this.bonusText.setColor(new ColorRGBA(1,1,0,1));
        this.bonusText.setCullMode(Spatial.CULL_ALWAYS);

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

    private TextureText mkTopTitle(String title, float x, float y) {
        return mkText("Top Title: ", title, 64, JustifyX.LEFT, JustifyY.BASELINE, x, y, x(132), y(16));
    }

    private String lastNotice = null;
    private void updateNotice(final String notice) {
        if (notice == lastNotice) return;
        if (notice == null || notice.length() == 0) {
            noticeText.setCullMode(Spatial.CULL_ALWAYS);
            noticeShade.setCullMode(Spatial.CULL_ALWAYS);
        } else {
            noticeText.setText(notice);
            noticeShade.resize(noticeText.getWidth()+x(20),
                               noticeText.getHeight()+y(20));
            noticeText.setCullMode(Spatial.CULL_NEVER);
            noticeShade.setCullMode(Spatial.CULL_NEVER);
        }
        lastNotice = notice;
    }

    private int lastScore = 0;
    private void updateScore(int score) {
        if (score == lastScore) return;
        // format the score prettily
        this.scoreText.setText(scoreFormat.format(score).toString());
        lastScore = score;
    }
    private static final DecimalFormat scoreFormat =new DecimalFormat("#,###");

    private String lastCall = null;
    private void updateCall(String call) {
        if (call == lastCall) return;
        if (call == null || call.length() == 0) {
            this.callText.setCullMode(Spatial.CULL_ALWAYS);
        } else {
            this.callText.setText(call);
            this.callText.setCullMode(Spatial.CULL_NEVER);
        }
        lastCall = call;
    }

    private String lastBonus = null;
    private void updateBonus(String bonus) {
        if (bonus == lastBonus) return;
        if (bonus == null || bonus.length() == 0) {
            this.bonusText.setCullMode(Spatial.CULL_ALWAYS);
        } else {
            this.bonusText.setText(bonus);
            this.bonusText.setCullMode(Spatial.CULL_NEVER);
        }
        lastBonus = bonus;
    }

    @Override
    protected void onActivate() {
        display.setTitle(Version.PACKAGE_STRING+": Let's Play!");
        MouseInput.get().setCursorVisible(false); // hide cursor during game
    }
    @Override
    protected void stateUpdate(float tpf) {
        updateNotice(hud.getNotice());
        updateScore(hud.getScore());
        updateCall(hud.getCurrentCall());
        updateBonus(hud.getBonus());

        timeFlowGauge.update(hud.getFlow());
        origGauge.update(hud.getOriginality());
        seqLenGauge.update(hud.getSequenceLength());

        rootNode.updateGeometricState(tpf, true);
    }
}
