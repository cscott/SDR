package net.cscott.sdr.anim;

import static net.cscott.sdr.anim.HUDState.mkAlpha;
import static net.cscott.sdr.anim.HUDState.mkShade;

import java.nio.FloatBuffer;
import java.util.concurrent.Callable;

import net.cscott.sdr.BeatTimer;
import net.cscott.sdr.util.Fraction;

import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.GameTaskQueueManager;
import com.jme.util.TextureManager;
import com.jme.util.geom.BufferUtils;
import com.jmex.game.state.StandardGameStateDefaultCamera;

/** {@link MusicState} handles the "scrolling notes" display at the bottom
 * of the screen.  This is shown in the {@link MenuState} as well, since it
 * helps debug the microphone issues.
 * @author C. Scott Ananian
 * @version $Id: MusicState.java,v 1.1 2006-11-08 23:42:12 cananian Exp $
 */
public class MusicState extends StandardGameStateDefaultCamera {
    /** Our display system. */
    private DisplaySystem display;
    /** What beat are we at? */
    private BeatTimer beatTimer;
    /** The notes texture, for animation. */
    private Texture notesTex=null;
    /** The "now" note. */
    private Quad nowNote;
    /** The "now" note texture. */
    private Texture nowNoteTex=null;

    public MusicState(BeatTimer beatTimer) {
        super("Music");
        this.display = DisplaySystem.getDisplaySystem();
        this.beatTimer = beatTimer;
        initHUD();

        rootNode.setLightCombineMode(LightState.OFF);
        rootNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);
        rootNode.updateRenderState();
        rootNode.updateGeometricState(0, true);
    }
    private void initHUD() {
        // background shading at bottom
        mkShade(rootNode, "Call Shade", x(320), y(32), x(640), y(64));
        
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
        
        // the "now" bar
        final Quad now = new Quad("now bar", 128, 128);
        now.setLocalTranslation(new Vector3f(64,y(64),0));
        now.setRenderState(mkAlpha());
        
        // highlight the active note
        nowNote = new Quad("now note", 128, 128);
        nowNote.setLocalTranslation(new Vector3f(64,y(64),0));
        nowNote.setRenderState(mkAlpha());
        nowNote.setCullMode(Spatial.CULL_ALWAYS);
        
        GameTaskQueueManager.getManager().update(new Callable<Void>() {
            public Void call() throws Exception {
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

                return null;
            }
        });
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
        
        rootNode.updateGeometricState(tpf, true);
    }
    private final Vector3f noteTrans = new Vector3f();

    //---------------------------------------------------------
    // convert 640x480-relative coordinates to appropriately scaled coords.
    private float x(int x) {
        return x*display.getWidth()/640f;
    }
    private float y(int y) {
        return y*display.getHeight()/480f;
    }
}
