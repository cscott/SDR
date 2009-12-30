package net.cscott.sdr.anim;

import java.util.Random;

import net.cscott.sdr.calls.StandardDancer;
import net.cscott.sdr.calls.TimedPosition;
import net.cscott.sdr.util.Bezier;
import net.cscott.sdr.util.Fraction;

import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.math.FastMath;
import com.jme.math.Matrix3f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Cylinder;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;

/**
 * {@link CheckerDancer} is an {@link AnimDancer} which displays a simple
 * "square dance checker" model.
 * @author C. Scott Ananian
 * @version $Id: CheckerDancer.java,v 1.4 2006-11-05 16:08:40 cananian Exp $
 */
public class CheckerDancer extends AnimDancer {
    private final Fraction footOffset;

    public CheckerDancer(DisplaySystem display, StandardDancer dancer) {
        super(dancer);
        init(display); // set up dancer.
        // have each dancer be slightly off the beat, for more realism.
        footOffset = true ? Fraction.ZERO : Fraction.valueOf(new Random().nextInt(10)-5,40);
    }

    private void init(DisplaySystem display) {
        float offset = dancer.ordinal()/1000.f;
        Node n = new Node("_"+dancer.toString());
        n.setLocalTranslation(new Vector3f(offset,offset,offset));
        this.node.attachChild(n);
        this.node.setActiveChild(0);
        // create material for the checker
        MaterialState mat = getMaterial(display, dancer);
        n.setTextureCombineMode(TextureState.COMBINE_FIRST);
        n.updateModelBound();

        // is this a boy?
        if (dancer.isBoy()) {
            Box b = new Box("boy"+dancer.coupleNumber(),
                            new Vector3f(-0.7f, -0.7f, 0f),
                            new Vector3f(0.7f, 0.7f, 0.26f));
            b.setLocalTranslation(new Vector3f(0,0,.01f));
            b.setModelBound(new BoundingBox());
            b.updateModelBound();
            b.setRenderState(mat);
            n.attachChild(b);
        } else { // ...or a girl?
            Cylinder c = new Cylinder("girl"+dancer.coupleNumber(),
                                      16, 16, 0.7f, 0.26f, true);
            c.setLocalTranslation(new Vector3f(0,0,0.14f));
            c.setModelBound(new BoundingBox());
            c.updateModelBound();
            c.setRenderState(mat);
            n.attachChild(c);
        }
        // label the checker.
    
        // arrow
        Quad qq = new Quad("arrow"+dancer.ordinal(), 1.2f, 1.2f);
        qq.setLocalTranslation(new Vector3f(0,dancer.isBoy()?0f:.1f,.3f));
        TextureState ts = display.getRenderer().createTextureState();
        ts.setEnabled(true);
        ts.setTexture(getTextureNumber(dancer), 0);
        qq.setRenderState(ts);
        qq.setRenderState(getAlphaState(display));
        qq.updateModelBound();
        n.attachChild(qq);
    }
    static MaterialState[] materials = new MaterialState[4];
    static MaterialState getMaterial(DisplaySystem display, StandardDancer d) {
        int i = d.coupleNumber() - 1;
        if (materials[i]==null) {
            ColorRGBA color;
            switch (i) {
            case 0:
                color = new ColorRGBA(1,10/255f,18/255f,1); break;
            case 1:
                color = new ColorRGBA(0,135/255f,250/255f,1); break;
            case 2:
                color = new ColorRGBA(240/255f,1,69/255f,1); break;
            case 3:
                color = new ColorRGBA(151/255f,1,84/255f,1); break;
            default:
                color = null;
                assert false : "Bad couple number";
            }
            materials[i] = display.getRenderer().createMaterialState();
            materials[i].setAmbient(color);
        }
        return materials[i];
    }
    static Texture[] texNumber = new Texture[4];
    static Texture getTextureNumber(StandardDancer d) {
        int i = d.coupleNumber() - 1;
        if (texNumber[i]==null) {
            texNumber[i] = TextureManager.loadTexture
            (Game.class.getClassLoader().getResource
                    ("net/cscott/sdr/anim/"+(i+1)+".png"),
                    Texture.MM_LINEAR_LINEAR,
                    Texture.FM_LINEAR);
        }
        return texNumber[i];
    }
    static AlphaState alpha = null;
    static AlphaState getAlphaState(DisplaySystem display) {
        if (alpha == null) {
            alpha = display.getRenderer().createAlphaState();
            alpha.setBlendEnabled(true);
            alpha.setSrcFunction(AlphaState.SB_SRC_ALPHA);
            alpha.setDstFunction(AlphaState.SB_ONE_MINUS_SRC_ALPHA);
            alpha.setTestEnabled(true);
            alpha.setTestFunction(AlphaState.TF_GREATER);
        }
        return alpha;
    }
    
    @Override
    public void update(Fraction time) {
        TimedPosition nextPos = getNextPosition(time);
        TimedPosition lastPos = getLastPosition();
        if (lastPos==null) { this.node.disableAllChildren(); return; }
        this.node.setActiveChild(0);
        // interpolate translation and rotation based on bezier curve
        Vector3f translation;
        Matrix3f rotation = new Matrix3f();
        if (nextPos == null) {
            translation = new Vector3f
            (lastPos.position.x.floatValue(), lastPos.position.y.floatValue(), 0f);
            rotation.fromAngleNormalAxis
            (-lastPos.position.facing.amount.floatValue()*2*FastMath.PI, new Vector3f(0f,0f,1f));
        } else {
            // interpolate between nextPos and lastPos, based on time.
            float p0x = lastPos.position.x.floatValue(); // start
            float p0y = lastPos.position.y.floatValue();
            float p3x = nextPos.position.x.floatValue(); 
            float p3y = nextPos.position.y.floatValue(); 

            float speedFactor = BEZIER_FACTOR * FastMath.sqrt((p3x-p0x)*(p3x-p0x)+(p3y-p0y)*(p3y-p0y));
            float lastRot = lastPos.position.facing.amount.floatValue()*2*FastMath.PI;
            float p1x = p0x + speedFactor*FastMath.sin(lastRot);
            float p1y = p0y + speedFactor*FastMath.cos(lastRot);
            float nextRot = nextPos.position.facing.amount.floatValue()*2*FastMath.PI;
            float p2x = p3x - speedFactor*FastMath.sin(nextRot);
            float p2y = p3y - speedFactor*FastMath.cos(nextRot);

            float t = time.subtract(lastPos.time)
            .divide(nextPos.time.subtract(lastPos.time)).floatValue();
            
            float tx = Bezier.cubicInterp(p0x,p1x,p2x,p3x,t);
            float ty = Bezier.cubicInterp(p0y,p1y,p2y,p3y,t);
            translation = new Vector3f(tx,ty,0);
            float dx = Bezier.cubicDeriv(p0x,p1x,p2x,p3x,t);
            float dy = Bezier.cubicDeriv(p0y,p1y,p2y,p3y,t);
            float angle = FastMath.atan2(dx,dy);
            rotation.fromAngleNormalAxis(-angle,new Vector3f(0,0,1f));
        }
        // add in a bouncy footstep
        time = time.add(footOffset); // randomize slightly off beat
        float foot = Fraction.valueOf(time.getProperNumerator(),time.getDenominator()).floatValue();
        foot = 4f * foot * (1 - foot);
        // foot now ranges from 0-1-0 as fractional beat goes from 0-.5-1
        foot *= .2f; // this is the foot step size
        translation.addLocal(0f,0f,foot);
        // apply the translation/rotation to the node
        this.node.setLocalTranslation(translation);
        this.node.setLocalRotation(rotation);
    }
    // From http://en.wikipedia.org/wiki/Bezier_curve :
    // "Some curves that seem simple, such as the circle, cannot be described
    // exactly by a B?zier or piecewise B?zier curve (though a four-piece
    // B?zier curve can approximate a circle, with a maximum radial error of
    // less than one part in a thousand, when each inner control point is the
    // distance (4/3) * (sqrt (2) - 1) horizontally or vertically from an outer
    // control point on a unit circle)."
    // We're going to scale by the distance between start and end, which on
    // a unit circle is sqrt(2)
    private static final float BEZIER_FACTOR =
        (4f/3f) * (FastMath.sqrt(2) - 1) / FastMath.sqrt(2);
}
