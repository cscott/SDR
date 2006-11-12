package net.cscott.sdr.anim;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.jme.image.Texture;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.TriMesh;
import com.jme.scene.batch.TriangleBatch;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jme.util.geom.BufferUtils;

/**
 * {@link RedOval} is the background shape for the menu: a semi-transparent red
 * rectangle with rounded edges.
 * @author C. Scott Ananian
 * @version $Id: RedOval.java,v 1.1 2006-11-12 20:22:16 cananian Exp $
 */
public class RedOval extends TriMesh {

    public RedOval(String name, float width, float height) {
        super(name);
        initVertices(width, height);
        initTexture();
    }
    private void initVertices(float width, float height) {
        TriangleBatch batch = getBatch(0);
        batch.setVertexCount(6);
        batch.setVertexBuffer(BufferUtils.createVector3Buffer(batch.getVertexCount()));
        batch.setNormalBuffer(BufferUtils.createVector3Buffer(batch.getVertexCount()));
        FloatBuffer tb = BufferUtils.createVector2Buffer(batch.getVertexCount());
        setTextureBuffer(0,tb);
        batch.setTriangleQuantity(4);
        batch.setIndexBuffer(BufferUtils.createIntBuffer(batch.getTriangleCount() * 3));
        
        // Vertices
        // 0    1    2
        // +----+----+
        // |         |
        // +----+----+
        // 3    4    5
        FloatBuffer vb = batch.getVertexBuffer();
        vb.put(-width/2).put(height/2).put(0);
        vb.put(0).put(height/2).put(0);
        vb.put(width/2).put(height/2).put(0);
        vb.put(-width/2).put(-height/2).put(0);
        vb.put(0).put(-height/2).put(0);
        vb.put(width/2).put(-height/2).put(0);
        
        // Normals
        FloatBuffer nb = batch.getNormalBuffer();
        for (int i=0; i<6; i++)
            nb.put(0).put(0).put(1);
        
        // Texture Coords
        tb.put(0).put(50/64f);
        tb.put((width/2)/64f).put(50/64f);
        tb.put(0).put(50/64f);
        tb.put(0).put(0);
        tb.put((width/2)/64f).put(0);
        tb.put(0).put(0);
        
        // Triangles
        IntBuffer ib = batch.getIndexBuffer();
        ib.put(0).put(1).put(3);
        ib.put(1).put(3).put(4);
        ib.put(1).put(2).put(4);
        ib.put(2).put(4).put(5);

        setDefaultColor(ColorRGBA.white);
    }
    private void initTexture() {
        DisplaySystem display = DisplaySystem.getDisplaySystem();
        Texture texture =
            TextureManager.loadTexture(
                    MenuState.class.getClassLoader().getResource(
                    "net/cscott/sdr/anim/menu-oval.png"),
                    Texture.MM_NONE,
                    Texture.FM_LINEAR);
        
        TextureState ts = display.getRenderer().createTextureState();
        ts.setEnabled(true);
        ts.setTexture(texture);
        this.setRenderState(ts);
        
        AlphaState alpha = display.getRenderer().createAlphaState();
        alpha.setBlendEnabled(true);
        alpha.setSrcFunction(AlphaState.SB_SRC_ALPHA);
        alpha.setDstFunction(AlphaState.DB_ONE_MINUS_SRC_ALPHA);
        alpha.setTestEnabled(true);
        alpha.setTestFunction(AlphaState.TF_GREATER);
        alpha.setEnabled(true);
        this.setRenderState(alpha);
        this.updateRenderState();
    }
}
