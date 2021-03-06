package net.cscott.sdr.anim;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.nio.FloatBuffer;

import com.jme.app.SimpleGame;
import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.state.LightState;
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
 * @version $Id: TextureText.java,v 1.9 2006-11-22 20:56:55 cananian Exp $
 */
public class TextureText extends Node {
    /** An enumeration of horizontal justification options. */
    public static enum JustifyX { RIGHT, CENTER, LEFT; }
    /** An enumeration of vertical justification options. */
    public static enum JustifyY { TOP, MIDDLE, BASELINE, BOTTOM; }

    /** This is the internal {@link TexturedQuad} which is textured and translated. */
    private final TexturedQuad quad;
    /** The user's choice of horizontal alignments. */
    private JustifyX alignX;
    /** The user's choice of vertical alignments. */
    private JustifyY alignY;
    /** The user's maximum width and height restrictions. */
    private float maxWidth=0, maxHeight=0;
    /** The actual width and height of the text. */
    private float width=0, height=0;
    /** Baseline offset (from bottom) */
    private float baseline=0;
    /** The string to display, or null if the TextureText has not been
     *  completely initialized yet. */
    private String text=null;
    /** The desired texture size -- for a 64x64 texture, specify '64' here. */
    private final int textureSize;
    /** The {@link Font} to use to draw this text.  Use
     * {@link Font#createFont(int, java.io.InputStream)} to use a truetype
     * font from a resource file. */
    private final Font font;
    /** The color to display the text. */
    private final ColorRGBA color;

    /** Create a {@link TextureText} with the given node name (required to
     * be unique in the scene graph) which will display using the given
     * {@link Font} and use the given amount of texture memory.  For a
     * 64 texel by 64 texel texture, {@code textureSize} should be 64. */
    public TextureText(String nodeName, Font font, int textureSize) {
        super(nodeName);
        this.quad = new TexturedQuad(nodeName+": internal quad", textureSize);
        this.alignX = JustifyX.LEFT;
        this.alignY = JustifyY.BOTTOM;
        this.font = font;
        this.color = new ColorRGBA(1, 1, 1, 1); // white by default
        this.textureSize = textureSize;
        this.quad.setDefaultColor(this.color);
        attachChild(this.quad);
        
        this.quad.texture.setFilter(Texture.FM_LINEAR);
        this.quad.texture.setMipmapState(Texture.MM_LINEAR/*LINEAR_LINEAR*/);
        this.quad.texture.setWrap(Texture.WM_WRAP_S_WRAP_T);
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
        BufferedImage textureImage = this.quad.getTextureImage();
        // note: the textureImage will be completely transparent to start with
        Graphics2D g2 = textureImage.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g2.setColor(Color.white);

        Font f= this.font.deriveFont(maxHeight);
        g2.setFont(f);
        
        // okay, let's do our stuff.
        TextLayout layout = new TextLayout(this.text, f, g2.getFontRenderContext());
        Rectangle2D bounds = layout.getBounds();
        double bHeight = layout.getAscent() + layout.getDescent();
        double bWidth = bounds.getWidth()*1.04;// 2% padding
        
        int nStrips = (int) Math.ceil(Math.sqrt(bWidth / bHeight));
        int stripHeight = (int) Math.floor(textureSize/(double)nStrips);
        int stripWidth = nStrips*textureSize;

        this.height = this.maxHeight;
        this.width = (float) (bWidth * this.height / bHeight);
        this.baseline = (float) (layout.getDescent()*this.height / bHeight);
        if (this.width > this.maxWidth) {
            this.width = this.maxWidth;
            this.height = (float) (bHeight * this.width / bWidth);
            this.baseline =(float)(layout.getDescent() * this.width / bWidth);
        }
        
        float ox = (float)-bounds.getX() + 1/*padding*/, oy = layout.getAscent();
        double sx = stripWidth / bWidth;
        double sy = stripHeight / bHeight;
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
        texCoords.put(nStrips-.01f).put(1+(stripHeight/(float)textureSize)); // bottom-right
        texCoords.put(nStrips-.01f).put(1); // top-right

        // update texture.
        quad.updateTexture(textureImage);

        // set new texture coordinates
        quad.setTextureBuffer(0, texCoords);
        // rescale the quad.
        quad.resize(width, height);
        // relocate the quad.
        recenter();
        // update quad color
        quad.setDefaultColor(color);
        // since we updated the texture, update the render state
        quad.updateRenderState();
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
        case BASELINE: oy = this.baseline-this.height/2f; break;
        case BOTTOM: oy = -this.height/2f; break;
        }
        // 
        this.quad.setLocalTranslation(new Vector3f(-ox,-oy,0f));
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
                TextureText tt = new TextureText("Test",font,128);
                tt.setAlign(JustifyX.CENTER,JustifyY.MIDDLE);
                tt.setMaxSize(display.getWidth(),display.getHeight()/8f);
                tt.setColor(new ColorRGBA(1,0,0,1));//red
                tt.setText("aeuuo, worua");
                tt.setRenderQueueMode(Renderer.QUEUE_ORTHO);
                tt.setLightCombineMode(LightState.OFF);
                tt.setLocalTranslation(new Vector3f(display.getWidth()/2,display.getHeight()*4.5f/8f,0));
                tt.updateRenderState();
                rootNode.attachChild(tt);
                // check that size is consistent with a version with descenders
                tt = new TextureText("Test2",font,128);
                tt.setAlign(JustifyX.CENTER,JustifyY.MIDDLE);
                tt.setMaxSize(display.getWidth(),display.getHeight()/8f);
                tt.setColor(new ColorRGBA(1,0,0,1));//red
                tt.setText("Hello, daisy!");
                tt.setRenderQueueMode(Renderer.QUEUE_ORTHO);
                tt.setLightCombineMode(LightState.OFF);
                tt.setLocalTranslation(new Vector3f(display.getWidth()/2,display.getHeight()*3.5f/8f,0));
                tt.updateRenderState();
                rootNode.attachChild(tt);
            }
        };
        game.start();
    }
}
