package net.cscott.sdr.anim;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.nio.FloatBuffer;
import java.util.concurrent.Callable;

import com.jme.app.SimpleGame;
import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.GameTaskQueue;
import com.jme.util.GameTaskQueueManager;
import com.jme.util.TextureManager;
import com.jme.util.geom.BufferUtils;

/** {@link TextureText} renders text to a textured quad.  This allows great
 * flexibility in choice of font & size, albeit by burning texture memory.
 * {@link TextureText} stripes the text across a square texture to (hopefully)
 * optimally use the texels, allowing the use of very small texture sizes.
 * In my experiments, a 64x64 texture yields only-slightly-fuzzy text for
 * a 400-pixel-wide string.  The origin of the {@link TextureText} {@link Node}
 * is at the specified justification point of the text string, allowing easier
 * placement.
 * @author C. Scott Ananian
 * @version $Id: TextureText.java,v 1.4 2006-11-07 23:07:25 cananian Exp $
 */
public class TextureText extends Node {
    /** An enumeration of horizontal justification options. */
    public static enum JustifyX { RIGHT, CENTER, LEFT; }
    /** An enumeration of vertical justification options. */
    public static enum JustifyY { TOP, MIDDLE, BOTTOM; }

    /** This is the internal {@link Quad} which is textured and translated. */
    private final Quad quad;
    /** The user's choice of horizontal alignments. */
    private JustifyX alignX;
    /** The user's choice of vertical alignments. */
    private JustifyY alignY;
    /** The user's maximum width and height restrictions. */
    private float maxWidth=0, maxHeight=0;
    /** The actual width and height of the text. */
    private float width=0, height=0;
    /** The string to display, or null if the TextureText has not been
     *  completely initialized yet. */
    private String text=null;
    /** The desired texture size -- for a 64x64 texture, specify '64' here. */
    private final int textureSize;
    /** An image buffer for drawing the texture; this is a soft reference to
     *  allow reclamation in mostly-static {@link TextureText}s, but hopefully
     *  the gc will be smart enough to keep enough around that we don't need
     *  to be doing frequent allocation in {@link #update()}. */
    private SoftReference<BufferedImage> textureImageRef = null;
    /** The {@link Font} to use to draw this text.  Use
     * {@link Font#createFont(int, java.io.InputStream)} to use a truetype
     * font from a resource file. */
    private final Font font;
    /** The {@link Texture} we will ultimately generate with this text. */
    private final Texture texture;
    /** The {@link TextureState} (we need to invalidate this from time to 
     * time). */
    private TextureState textureState = null;
    /** The color to display the text. */
    private final ColorRGBA color;

    /** Create a {@link TextureText} with the given node name (required to
     * be unique in the scene graph) which will display using the given
     * {@link Font} and use the given amount of texture memory.  For a
     * 64 texel by 64 texel texture, {@code textureSize} should be 64. */
    public TextureText(String nodeName, Font font, int textureSize) {
        super(nodeName);
        this.quad = new Quad(nodeName+": internal quad", 1, 1);
        this.alignX = JustifyX.LEFT;
        this.alignY = JustifyY.BOTTOM;
        this.font = font;
        this.color = new ColorRGBA(1, 1, 1, 1); // white by default
        this.textureSize = textureSize;
        this.quad.setColorBuffer(0,null);//use default color
        this.quad.setDefaultColor(this.color);
        attachChild(this.quad);
        
        this.texture = new Texture(); 
        texture.setApply(Texture.AM_MODULATE);
        texture.setCorrection(Texture.CM_AFFINE);
        texture.setFilter(Texture.FM_LINEAR);
        texture.setMipmapState(Texture.MM_LINEAR/*LINEAR_LINEAR*/);
        texture.setWrap(Texture.WM_WRAP_S_WRAP_T);
       
        // in update thread. (we're thread-safe!)
        GameTaskQueueManager.getManager().getQueue(GameTaskQueue.UPDATE)
        .enqueue(new Callable<Void>() {
            public Void call() throws Exception {
                DisplaySystem display = DisplaySystem.getDisplaySystem();
                textureState = display.getRenderer().createTextureState();
                textureState.setTexture(texture);
                textureState.setEnabled(true);
                quad.setRenderState(textureState);

                AlphaState as = display.getRenderer().createAlphaState();
                as.setBlendEnabled(true);
                as.setSrcFunction(AlphaState.SB_SRC_ALPHA);
                as.setDstFunction(AlphaState.DB_ONE_MINUS_SRC_ALPHA);
                as.setTestEnabled(false);
                as.setEnabled(true);
                quad.setRenderState(as);
                
                quad.updateRenderState();
                return null;
            }
        });
    }
    /** Set the desired alignment of this text node.  The node's local
     * origin will be at the specified alignment point of the text.
     */
    public void setAlign(JustifyX alignX, JustifyY alignY) {
        if (this.alignX==alignX && this.alignY==alignY) return;
        this.alignX = alignX; this.alignY = alignY;
        recenter();
    }
    /** Set the maximum width/height of this node. The generated text is
     *  guaranteed not to exceed this size. */
    public void setMaxSize(float maxWidth, float maxHeight) {
        if (this.maxWidth==maxWidth && this.maxHeight==maxHeight) return;
        this.maxWidth = maxWidth; this.maxHeight = maxHeight;
        if (maxWidth < width || maxHeight < height)
            update();
    }
    /** Set the text to display.  This method is thread-safe, but rather
     * expensive.  As long as you're not updating the text here in every frame,
     * you should be alright. */
    public void setText(String text) {
        if (this.text!=null && this.text.equals(text)) return;
        this.text = text;
        update();
    }
    /** Set the foreground color in which to display the text. The text always
     * has a transparent background. */
    public void setColor(ColorRGBA color) {
        if (this.color.equals(color)) return;
        this.color.set(color);
        update();
    }
    /** Return the actual height of this textured text object. */
    public float getHeight() {
        return height;
    }
    /** Return the actual width of this textured text object. */
    public float getWidth() {
        return width;
    }
    /** Private method: create a texture image with the given text, and then
     * tweak the quad to have the proper texture, size, and local translation.
     */
    private void update() {
        if (text==null) return; // not initialized yet.
        // Create BufferedImage (or reuse old one if it hasn't been gc'ed yet)
        BufferedImage ti =
            textureImageRef==null ? null : textureImageRef.get();
        if (ti==null) {
            ti = new BufferedImage
                     (textureSize,textureSize,BufferedImage.TYPE_4BYTE_ABGR);
            textureImageRef = new SoftReference<BufferedImage>(ti);
        }
        final BufferedImage textureImage = ti; // make this final; used below
        Graphics2D g2 = textureImage.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g2.setColor(Color.white);

        Font f= this.font.deriveFont(maxHeight);
        g2.setFont(f);
        // clear the old background
        int textureSize = textureImage.getHeight();
        assert textureSize == textureImage.getWidth();
        g2.setBackground(new Color(0f, 0f, 0f, 0f));
        g2.clearRect(0, 0, textureSize, textureSize);
        
        // okay, let's do our stuff.
        TextLayout layout = new TextLayout(this.text, f, g2.getFontRenderContext());
        Rectangle2D bounds = layout.getBounds();
        int nStrips = (int) Math.ceil(Math.sqrt(bounds.getWidth() / bounds.getHeight()));
        int stripHeight = (int) Math.floor(textureSize/(double)nStrips);
        int stripWidth = nStrips*textureSize;

        // the given bounds are usually slightly too small: grow them by
        // 10% in the Y direction and 5% in the X direction
        bounds = new Rectangle2D.Double
               (bounds.getX()-.025*bounds.getWidth(),
                bounds.getY()-.050*bounds.getHeight(),
                bounds.getWidth()*1.05,
                bounds.getHeight()*1.10);
        
        this.height = this.maxHeight;
        this.width = (float) (bounds.getWidth() * this.height / bounds.getHeight());
        if (this.width > this.maxWidth) {
            this.width = this.maxWidth;
            this.height = (float) (bounds.getHeight() * this.width / bounds.getWidth());
        }
        
        float ox = (float)-bounds.getX(), oy = (float)-bounds.getY();
        double sx = stripWidth / bounds.getWidth();
        double sy = stripHeight / bounds.getHeight();
        // Write the text in strips to our image.
        for (int i=0; i<=nStrips; i++) {
            AffineTransform oat = g2.getTransform();
            if (i<nStrips)
                g2.translate(-i*textureSize,0);
            else
                g2.translate(-(i-1)*textureSize,-textureSize);
            g2.shear(0,stripHeight/(double)textureSize);
            g2.scale(sx,sy);
            g2.drawString(text, ox, oy);
            g2.setTransform(oat);
        }
        g2.dispose();
        
        // set texture coordinates. coordinates ccw from top-left
        final FloatBuffer texCoords = BufferUtils.createVector2Buffer(4);
        texCoords.put(0).put(0); // top-left
        texCoords.put(0).put(stripHeight/(float)textureSize); // bottom-left
        texCoords.put(nStrips).put(1+(stripHeight/(float)textureSize)); // bottom-right
        texCoords.put(nStrips).put(1); // top-right

        // in update thread.
        GameTaskQueueManager.getManager().getQueue(GameTaskQueue.UPDATE)
        .enqueue(new Callable<Void>() {
            public Void call() throws Exception {
                // set new texture
                texture.setImage(TextureManager.loadImage(textureImage,false));
                // refresh texture state
                textureState.load();
                // set new texture coordinates
                quad.setTextureBuffer(0, texCoords);
                // rescale the quad.
                quad.resize(width, height);
                // relocate the quad.
                recenter();
                // update quad color
                quad.setDefaultColor(color);
                return null;
            }
        });
    }
    
    /** Private helper: just reset the local translation to reflect the
     * alignment settings. */
    private void recenter() {
        // the "native" center of the quad is at its center.
        float ox, oy;
        switch (this.alignX) {
        default:
        case LEFT: ox = -this.width/2f; break;
        case CENTER: ox = 0; break;
        case RIGHT: ox = this.width/2f; break;
        }
        switch (this.alignY) {
        case TOP: oy = this.height/2f; break;
        case MIDDLE: oy = 0; break;
        default:
        case BOTTOM: oy = -this.height/2f; break;
        }
        // 
        this.quad.setLocalTranslation(new Vector3f(-ox,-oy,0f));
    }
    
    /** Release the texture when this {@link TextureText} is no longer being
     *  used. */
    @Override
    public void finalize() {
        TextureManager.releaseTexture(this.texture);
    }
    
    /**
     * Simple test harness to exercise the features of this class.
     * Note that the texture size used below (64x64) is intentionally a little
     * low for the size of the text object (400 pixels wide), so that we can
     * see how the texture filtering works.  You'll get much better quality if
     * you increase the texture size to 128x128 in the TextureText constructor.
     */
    public static void main(String[] args) throws Exception {
        URL url = TextureText.class.getClassLoader().getResource      
        ("net/cscott/sdr/fonts/bluebold.ttf");
        final Font font=Font.createFont(Font.TRUETYPE_FONT, url.openStream());
        SimpleGame game = new SimpleGame() {
            @Override
            protected void simpleInitGame() {
                setDialogBehaviour(SimpleGame.ALWAYS_SHOW_PROPS_DIALOG);
                // Set a green background, so we can see the transparency.
                display.getRenderer().setBackgroundColor( ColorRGBA.green );
                TextureText tt = new TextureText("Test",font,64);
                tt.setAlign(JustifyX.CENTER,JustifyY.MIDDLE);
                tt.setMaxSize(400,400);
                tt.setColor(new ColorRGBA(1,0,0,1));//red
                tt.setText("Hello, world!");
                tt.setRenderQueueMode(Renderer.QUEUE_ORTHO);
                tt.setLightCombineMode(LightState.OFF);
                tt.setLocalTranslation(new Vector3f(display.getWidth()/2,display.getHeight()/2,0));
                tt.updateRenderState();
                rootNode.attachChild(tt);
            }
        };
        game.start();
    }
}
