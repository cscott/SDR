package net.cscott.sdr.anim;

import java.nio.FloatBuffer;

import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.TriMesh;
import com.jme.scene.batch.TriangleBatch;
import com.jme.util.geom.BufferUtils;

/**
 * {@link Star} defines a N-sided, two dimensional star.  The star
 * fits in a circle with diameter {@code size} units.  The center is at the
 * origin. Texture coordinates are assigned such that {@code i} ranges from 0
 * at the center of the star to 1/2 where the points meet, to 1 at the tip of
 * the point; and {@code j} ranges from 0 at the centerline of the point, to 1
 * at the edge of the point.  This texture assignment makes it easy to create
 * textures with radial gradients or outlines, but probably makes it hard to
 * overlay an undistorted image on the star.
 * @author C. Scott Ananian
 * @version $Id: Star.java,v 1.1 2006-11-12 18:18:17 cananian Exp $
 */
public class Star extends TriMesh {
    private static final float INNER_RATIO=.5f;

    /**
     * Constructor creates a new {@link Star} object with {@code nPoints}
     * points and which fits in a circle of the given {@code diameter}. 
     */
    public Star(String name, float nPoints, float diameter) {
        super(name);
        initialize(nPoints, diameter);
    }

    /**
     * {@link #initialize(float,float)} builds the data for the {@link Star}
     * object.
     */
    public void initialize(float nPoints, float diameter) {
        int pts = Math.round(nPoints);
        TriangleBatch batch = getBatch(0);
        batch.setVertexCount(1+pts*2);
        batch.setVertexBuffer(BufferUtils.createVector3Buffer(batch.getVertexCount()));
        batch.setNormalBuffer(BufferUtils.createVector3Buffer(batch.getVertexCount()));
        FloatBuffer tbuf = BufferUtils.createVector2Buffer(batch.getVertexCount());
        setTextureBuffer(0,tbuf);
        batch.setTriangleQuantity(2*pts);
        batch.setIndexBuffer(BufferUtils.createIntBuffer(batch.getTriangleCount() * 3));
        
        FloatBuffer vb = batch.getVertexBuffer();
        FloatBuffer nb = batch.getNormalBuffer();
        vb.put(0).put(0).put(0);
        nb.put(0).put(0).put(1);
        tbuf.put(1).put(0);
        for (int i=0; i<pts; i++) {
            boolean last = (i==pts-1);
            // point position
            float angle = 2*FastMath.PI * i / nPoints;
            float x = FastMath.sin(angle)*diameter/2;
            float y = FastMath.cos(angle)*diameter/2;
            vb.put(x).put(y).put(0);
            nb.put(0).put(0).put(1);
            tbuf.put(0).put(1);
            // inner point
            if (last)
                angle = (angle+2*FastMath.PI)/2;
            else
                angle += FastMath.PI / nPoints;
            x = FastMath.sin(angle)*diameter*INNER_RATIO/2;
            y = FastMath.cos(angle)*diameter*INNER_RATIO/2;
            vb.put(x).put(y).put(0);
            nb.put(0).put(0).put(1);
            tbuf.put(.5f).put(1);

            batch.getIndexBuffer().put(0);
            batch.getIndexBuffer().put(1+2*i);
            batch.getIndexBuffer().put(2+2*i);

            batch.getIndexBuffer().put(0);
            batch.getIndexBuffer().put(2+2*i);
            if (last)
                batch.getIndexBuffer().put(1);
            else
                batch.getIndexBuffer().put(3+2*i);
        }

        setDefaultColor(ColorRGBA.white);
    }
    
    /**
     * <code>getCenter</code> returns the center of the {@link Star}.
     * 
     * @return Vector3f the center of the {@code Star}.
     */
    public Vector3f getCenter() {
        return worldTranslation;
    }
}
