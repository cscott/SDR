package net.cscott.sdr.anim;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.lang.ref.SoftReference;
import java.util.concurrent.Callable;

import com.jme.image.Texture;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.GameTaskQueue;
import com.jme.util.GameTaskQueueManager;
import com.jme.util.TextureManager;

/** A "TexturedQuad" is textured with a {@link BufferedImage} which the client
 * can draw into.  This makes it easy to write subclasses to generate dynamic
 * HUD elements by drawing into the Image.
 * @author C. Scott Ananian
 * @version $Id: TexturedQuad.java,v 1.1 2006-11-08 21:20:08 cananian Exp $
 */
public class TexturedQuad extends Quad {
    /** An image buffer for drawing the texture; this is a soft reference to
     *  allow reclamation in mostly-static {@link TexturedQuad}s, but hopefully
     *  the gc will be smart enough to keep enough around that we don't need
     *  to be doing frequent allocations for dynamic {@code TexturedQuad}s. */
    private SoftReference<BufferedImage> textureImageRef = null;
    /** The {@link Texture} we will ultimately generate. */
    public final Texture texture;
    /** The {@link TextureState} (we need to invalidate this from time to 
     * time). */
    private TextureState textureState = null;
    /** The desired texture size -- for a 64x64 texture, specify '64' here. */
    private final int textureSize;

    /** Creates a {@link TexturedQuad} which is textured with a
     * {@link BufferedImage} of the specified size.  For example, to
     * use a 64x64 texture, pass in '64' as the {@code textureSize}.
     * The quad by default is the same size as the image; you can call
     * {@link #resize(float, float)} yourself if you want to scale it.
     * You may also want to change the {@link #texture} minification and
     * magnification filters in that case.
     * <p>
     * To update the texture, get the backing {@link BufferedImage} with
     * {@link #getTextureImage()}, draw on it, and then give it to
     * {@link #updateTexture(BufferedImage)}.  By default, the backing image
     * is held with a soft reference to allow it to be reclaimed by the
     * garbage collector if the texture is not "frequently" changed; if
     * you would like to use the previous contents of the backing image
     * to do an incremental update, you are responsible for keeping a hard
     * reference to it yourself. */
    public TexturedQuad(String nodeName, int textureSize) {
        super(nodeName, textureSize, textureSize);
        setColorBuffer(0,null);//use default color
        setDefaultColor(new ColorRGBA(1,1,1,1));//for AM_MODULATE to work right
        this.textureSize = textureSize;
        this.texture = new Texture();
        texture.setApply(Texture.AM_MODULATE);
        texture.setCorrection(Texture.CM_AFFINE);
        texture.setFilter(Texture.FM_NEAREST);
        texture.setMipmapState(Texture.MM_NONE);
        // in update thread. (we're thread-safe!)
        GameTaskQueueManager.getManager().update(new Callable<Void>() {
            public Void call() throws Exception {
                DisplaySystem display = DisplaySystem.getDisplaySystem();
                textureState = display.getRenderer().createTextureState();
                textureState.setTexture(texture);
                textureState.setEnabled(true);
                setRenderState(textureState);

                AlphaState as = display.getRenderer().createAlphaState();
                as.setBlendEnabled(true);
                as.setSrcFunction(AlphaState.SB_SRC_ALPHA);
                as.setDstFunction(AlphaState.DB_ONE_MINUS_SRC_ALPHA);
                as.setTestEnabled(false);
                as.setEnabled(true);
                setRenderState(as);
                
                updateRenderState();
                return null;
            }
        });
    }

    /** Return an appropriate {@link BufferedImage} in which to draw the
     * desired texture.  After you have drawn what you like, give this
     * {@link BufferedImage} to the {@link #updateTexture(BufferedImage)}
     * method.  Note that the returned image will always be completely
     * transparent, even if internally we're reusing an old reference.
     * @return
     */
    public BufferedImage getTextureImage() {
        // Create BufferedImage (or reuse old one if it hasn't been gc'ed yet)
        BufferedImage ti =
            textureImageRef==null ? null : textureImageRef.get();
        if (ti==null) {
            ti = new BufferedImage
                     (textureSize,textureSize,BufferedImage.TYPE_4BYTE_ABGR);
            textureImageRef = new SoftReference<BufferedImage>(ti);
        }
        // always clear the old (since we might have just recreated the image)
        Graphics2D g2 = ti.createGraphics();
        g2.setBackground(new Color(0f, 0f, 0f, 0f));
        g2.clearRect(0, 0, ti.getWidth(), ti.getHeight());
        g2.dispose();
        // okay, return this.
        return ti;
    }
    /** Update the texture on this quad.  This method adds the update
     * to the update queue, so it will not take effect immediately.
     */
    public void updateTexture(final BufferedImage textureImage) {
        GameTaskQueueManager.getManager().update(new Callable<Void>() {
            public Void call() throws Exception {
                // set new texture
                texture.setImage(TextureManager.loadImage(textureImage,false));
                // refresh texture state
                textureState.load();
                // make sure render state updates are noticed.
                updateRenderState();
                return null;
            }
        });
    }
    /** Release the texture when this {@link TexturedQuad} is no longer being
     *  used. */
    @Override
    public void finalize() {
        TextureManager.releaseTexture(this.texture);
    }
}
