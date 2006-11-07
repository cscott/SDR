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

public class TextureText extends Node {
    public static enum JustifyX { RIGHT, CENTER, LEFT; }
    public static enum JustifyY { TOP, MIDDLE, BOTTOM; }

    private final Quad quad;
    private JustifyX alignX;
    private JustifyY alignY;
    private float maxWidth=0, maxHeight=0;
    private float width=0, height=0;
    private String text=null;
    private final BufferedImage textureImage;
    private final Font font;
    private final Texture texture;
    
    public TextureText(String nodeName, Font font, int textureSize) {
        super(nodeName);
        this.quad = new Quad(nodeName+": internal quad", 1, 1);
        this.alignX = JustifyX.LEFT;
        this.alignY = JustifyY.BOTTOM;
        this.font = font;
        this.textureImage = new BufferedImage
        (textureSize,textureSize,BufferedImage.TYPE_4BYTE_ABGR);
        attachChild(this.quad);
        
        this.texture = new Texture(); 
        texture.setApply(Texture.AM_MODULATE);
        texture.setBlendColor(new ColorRGBA(1, 1, 1, 1));
        texture.setCorrection(Texture.CM_AFFINE);
        texture.setFilter(Texture.FM_LINEAR);
        texture.setMipmapState(Texture.MM_NONE);
        texture.setWrap(Texture.WM_WRAP_S_WRAP_T);
       
        // in update thread.
        GameTaskQueueManager.getManager().getQueue(GameTaskQueue.UPDATE)
        .enqueue(new Callable<Void>() {
            public Void call() throws Exception {
                DisplaySystem display = DisplaySystem.getDisplaySystem();
                TextureState ts1 = display.getRenderer().createTextureState();
                ts1.setTexture(texture);
                ts1.setEnabled(true);
                quad.setRenderState(ts1);

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
        this.alignX = alignX; this.alignY = alignY;
        recenter();
    }
    /** Set the maximum width/height of this node. The generated text is
     *  guaranteed not to exceed this size. */
    public void setMaxSize(float maxWidth, float maxHeight) {
        this.maxWidth = maxWidth; this.maxHeight = maxHeight;
        if (maxWidth < width || maxHeight < height)
            update();
    }
    public void setText(String text) {
        if (this.text!=null && this.text.equals(text)) return;
        this.text = text;
        update();
    }

    private void update() {
        if (text==null) return; // not initialized yet.
        Graphics2D g2 = this.textureImage.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g2.setColor(Color.white);

        Font f= this.font.deriveFont(maxHeight);
        g2.setFont(f);
        // clear the old background
        int textureSize = this.textureImage.getHeight();
        assert textureSize == this.textureImage.getWidth();
        g2.setBackground(new Color(0f, 0f, 0f, 0f));
        g2.clearRect(0, 0, textureSize, textureSize);
        
        // okay, let's do our stuff.
        TextLayout layout = new TextLayout(this.text, f, g2.getFontRenderContext());
        Rectangle2D bounds = layout.getBounds();
        int nStrips = (int) Math.ceil(Math.sqrt(bounds.getWidth() / bounds.getHeight()));
        int stripHeight = (int) Math.floor(textureSize/(double)nStrips);
        int stripWidth = nStrips*textureSize;

        // the given bounds are usually slightly too small: grow them by
        // 10% in the Y direction and 2% in the X direction
        bounds = new Rectangle2D.Double
               (bounds.getX()-.01*bounds.getWidth(),
                bounds.getY()-.05*bounds.getHeight(),
                bounds.getWidth()*1.02,
                bounds.getHeight()*1.1);
        
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
                // set new texture coordinates
                quad.setTextureBuffer(0, texCoords);
                // rescale the quad.
                quad.resize(width, height);
                // relocate the quad.
                recenter();
                return null;
            }
        });
    }
    
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
        case TOP: oy = -this.height/2f; break;
        case MIDDLE: oy = 0; break;
        default:
        case BOTTOM: oy = this.height/2f; break;
        }
        // 
        this.quad.setLocalTranslation(new Vector3f(-ox,-oy,0f));
    }
    
    @Override
    public void finalize() {
        TextureManager.releaseTexture(this.texture);
    }
    
    /**
     * Simple test harness.
     */
    public static void main(String[] args) throws Exception {
        URL url = TextureText.class.getClassLoader().getResource      
        ("net/cscott/sdr/fonts/exprswy_free.ttf");
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
