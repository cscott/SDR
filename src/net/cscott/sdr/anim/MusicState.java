package net.cscott.sdr.anim;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import net.cscott.sdr.BeatTimer;
import net.cscott.sdr.recog.LevelMonitor;
import net.cscott.sdr.recog.LevelMonitor.LevelMeasurement;
import net.cscott.sdr.util.Fraction;

import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.TextureState;
import com.jme.util.GameTaskQueueManager;
import com.jme.util.TextureManager;
import com.jme.util.geom.BufferUtils;

/** {@link MusicState} handles the "scrolling notes" display at the bottom
 * of the screen.  This is shown in the {@link MenuState} as well, since it
 * helps debug the microphone issues.
 * @author C. Scott Ananian
 * @version $Id: MusicState.java,v 1.5 2006-11-22 20:56:55 cananian Exp $
 */
public class MusicState extends BaseState {
    /** What beat are we at? */
    private BeatTimer beatTimer;
    /** Monitor microphone levels. */
    private LevelMonitor levelMonitor;
    /** The notes texture, for animation. */
    private Texture notesTex=null;
    /** The "now" note. */
    private Quad nowNote;
    /** The "now" note texture. */
    private Texture nowNoteTex=null;
    /** The "music level" texture. */
    private TexturedQuad levelQuad;
    /** The texture image for the "music level" quad; we keep this live so that
     * we can update it, instead of drawing it from scratch every frame. */
    private BufferedImage levelImage;

    public MusicState(BeatTimer beatTimer, LevelMonitor levelMonitor) {
        super("Music");
        this.beatTimer = beatTimer;
        this.levelMonitor = levelMonitor;
        initHUD();

        rootNode.updateRenderState();
        rootNode.updateGeometricState(0, true);
    }
    private void initHUD() {
        // background shading at bottom
        mkShade("Call Shade", x(320), y(32), x(640), y(64));
        
        // scrolling notes.
        final Quad notes = new Quad("Scrolling notes",x(640),128);
        notes.setLocalTranslation(new Vector3f(x(320),y(64),0));
        notes.setRenderState(mkAlpha());
        // set texture coordinates. coordinates ccw from top-left
        FloatBuffer texCoords = BufferUtils.createVector2Buffer(4);
        texCoords.put(0).put(1); // top-left
        texCoords.put(0).put(0); // bottom-left
        texCoords.put(display.getWidth()/128).put(0); // bottom-right
        texCoords.put(display.getWidth()/128).put(1); // top-right
        notes.setTextureBuffer(0, texCoords);
        
        // the textured quad for the microphone level monitor
        levelQuad = new TexturedQuad("microphone levels",128);
        levelQuad.setLocalTranslation(new Vector3f(64, y(64), 0));
        levelImage = levelQuad.getTextureImage();
        
        // the "now" bar
        final Quad now = new Quad("now bar", 128, 128);
        now.setLocalTranslation(new Vector3f(64,y(64),0));
        now.setRenderState(mkAlpha());
        
        // highlight the active note
        nowNote = new Quad("now note", 128, 128);
        nowNote.setLocalTranslation(new Vector3f(64,y(64),0));
        nowNote.setRenderState(mkAlpha());
        nowNote.setCullMode(Spatial.CULL_ALWAYS);
        
        notesTex = TextureManager.loadTexture(
                HUDState.class.getClassLoader().getResource(
                "net/cscott/sdr/anim/measure.png"),
                Texture.MM_NONE,
                Texture.FM_NEAREST); // there will be no stretching
        notesTex.setWrap(Texture.WM_WRAP_S_CLAMP_T);
        TextureState ts = display.getRenderer().createTextureState();
        ts.setEnabled(true);
        ts.setTexture(notesTex);
        notes.setRenderState(ts);
        rootNode.attachChild(notes);
        notes.updateRenderState();
        
        nowNoteTex = TextureManager.loadTexture(
                HUDState.class.getClassLoader().getResource(
                "net/cscott/sdr/anim/measure-bang.png"),
                Texture.MM_NONE,
                Texture.FM_NEAREST); // there will be no stretching
        nowNoteTex.setWrap(Texture.WM_WRAP_S_CLAMP_T);
        ts = display.getRenderer().createTextureState();
        ts.setEnabled(true);
        ts.setTexture(nowNoteTex);
        nowNote.setRenderState(ts);
        rootNode.attachChild(nowNote);
        nowNote.updateRenderState();
        
        rootNode.attachChild(levelQuad);
        
        Texture nowTex = TextureManager.loadTexture(
                HUDState.class.getClassLoader().getResource(
                "net/cscott/sdr/anim/measure-now.png"),
                Texture.MM_NONE,
                Texture.FM_NEAREST); // there will be no stretching
        ts = display.getRenderer().createTextureState();
        ts.setEnabled(true);
        ts.setTexture(nowTex);
        now.setRenderState(ts);
        rootNode.attachChild(now);
        now.updateRenderState();
    }
    @Override
    protected void onActivate() {
        // do nothing
    }
    @Override
    protected void stateUpdate(float tpf) {
        Fraction currentBeat = beatTimer.getCurrentBeat();
        Fraction partialBeat = Fraction.valueOf
             (currentBeat.getProperNumerator(),currentBeat.getDenominator());
        // "now" bar right edge is at pixel 54 (of 128); second note center
        // is at pixel 66 (of 128), leading to the offset (14/128) below.
        noteTrans.set((partialBeat.floatValue()/2)+(12/128f),0,0);
        if (notesTex!=null) notesTex.setTranslation(noteTrans);
        if (nowNoteTex!=null) nowNoteTex.setTranslation(noteTrans);
        // flash 'now Note' for short period around beat.
        nowNote.setCullMode(partialBeat.compareTo(Fraction.ONE_HALF) < 0 ?
                Spatial.CULL_NEVER : Spatial.CULL_ALWAYS);
        // draw microphone levels.
        float bpf = currentBeat.floatValue()-lastBeat;
        if (levelMonitor!=null) drawLevels(bpf/*beats per frame*/);
        lastBeat = currentBeat.floatValue();
        
        rootNode.updateGeometricState(tpf, true);
    }
    private final Vector3f noteTrans = new Vector3f();
    private float lastBeat=0;

    /** How wide the "levels" display should be. */
    private static final int LEVELS_WIDTH=52;
    private static final int LEVELS_HEIGHT=45;
    private void drawLevels(float bpf) {
        levelMonitor.getLevels(levels);
        // 128 pixels = 2 beats.
        // compute how many pixels to fill from # of beats since last frame
        int nCols = Math.min(LEVELS_WIDTH,Math.round(128*bpf/2));
        // shift the image this # of columns
        Graphics2D g2 = levelImage.createGraphics();
        g2.setBackground(new Color(0f, 0f, 0f, 0f));
        Composite comp = g2.getComposite();
        g2.setComposite(AlphaComposite.Src);
        g2.copyArea(nCols, 0, LEVELS_WIDTH+1, 128, -nCols, 0);
        // (as long as LEVELS_WIDTH<64, we don't have to clear the new space)
        assert LEVELS_WIDTH < 64;
        // draw the new levels.
        g2.setComposite(comp);
        for (int i=0; i<nCols; i++) {
            int x=LEVELS_WIDTH-nCols+i+1;
            // find max level in this bucket.
            double max=0;// so log doesn't freak
            int jmin=i*levels.size()/nCols, jmax=(i+1)*levels.size()/nCols;
            for (int j=jmin; j<jmax; j++)
                max = Math.max(max, levels.get(j).level);
            // draw line corresponding to this bucket
            g2.setColor(levelColor((float)max));
            int disp = (int) Math.round(max*LEVELS_HEIGHT);
            g2.drawLine(x,64-disp,x,64+disp+1);
        }
        // update the texture!
        levelQuad.updateTexture(levelImage);
        // clean up.
        g2.dispose();
        levels.clear();
    }
    private final List<LevelMeasurement> levels =
        new ArrayList<LevelMeasurement>(128);
    /** Find a color for the given level 0-1.  Interpolate green->yellow->red. */
    private Color levelColor(float val) {
        float[] start = c1, end = c2; 
        if (val>.5f) { start=c2; end=c3; val-=.5f; }
        val*=2;
        return new Color(
                start[0]*(1-val) + end[0]*(val),
                start[1]*(1-val) + end[1]*(val),
                start[2]*(1-val) + end[2]*(val),
                start[3]*(1-val) + end[3]*(val));
    }
    private static final float[] c1 = Color.GREEN.getRGBComponents(null);
    private static final float[] c2 = Color.YELLOW.getRGBComponents(null);
    private static final float[] c3 = Color.RED.getRGBComponents(null);
    static { c2[3]=0.5f; /* add some alpha */ }
}
