package net.cscott.sdr.anim;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Cylinder;
import com.jme.scene.shape.Quad;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.TextureState;
import com.jme.util.LoggingSystem;
import com.jme.util.TextureManager;

import java.net.URL;

public class JmeDemo extends SimpleGame {

  private Quaternion rotQuat = new Quaternion();
  private float angle = 0;
  private Vector3f axis = new Vector3f(1, 1, 0);
  private Sphere s;

  /**
   * Entry point for the test,
   * @param args
   */
  public static void main(String[] args) {
    LoggingSystem.getLogger().setLevel(java.util.logging.Level.OFF);
    JmeDemo app = new JmeDemo();
    URL url = JmeDemo.class.getClassLoader().getResource
	("net/cscott/sdr/splash.png");
    app.setDialogBehaviour(FIRSTRUN_OR_NOCONFIGFILE_SHOW_PROPS_DIALOG, url);
    app.start();
  }

  protected void simpleUpdate() {
      /*
    if (tpf < 1) {
      angle = angle + (tpf * 1);
      if (angle > 360) {
        angle = 0;
      }
    }
    rotQuat.fromAngleAxis(angle, axis);
    s.setLocalRotation(rotQuat);
      */
  }

  /**
   * builds the trimesh.
   * @see com.jme.app.SimpleGame#initGame()
   */
  protected void simpleInitGame() {
    display.setTitle("SDR - jME demo");
    cam.setLocation(new Vector3f(0, -8.5f, 9.4f));


    s = new Sphere("Sphere", 63, 50, 25);
    s.setLocalTranslation(new Vector3f(0,0,-40));
    s.setModelBound(new BoundingBox());
    s.updateModelBound();
    rootNode.attachChild(s);

    TextureState ts = display.getRenderer().createTextureState();
    ts.setEnabled(true);
    ts.setTexture(
        TextureManager.loadTexture(
        JmeDemo.class.getClassLoader().getResource(
        "net/cscott/sdr/splash.png"),
        Texture.MM_LINEAR_LINEAR,
        Texture.FM_LINEAR));
    s.setRenderState(ts);

    Quad q = new Quad("floor", 10, 10);
    q.setLocalTranslation(q.getCenter().negate());
    q.setModelBound(new BoundingBox());
    q.updateModelBound();
    rootNode.attachChild(q);

    ts = display.getRenderer().createTextureState();
    ts.setEnabled(true);
    ts.setTexture
	(TextureManager.loadTexture
	 (JmeDemo.class.getClassLoader().getResource
	  ("net/cscott/sdr/floor.png"),
	  Texture.MM_LINEAR_LINEAR,
	  Texture.FM_LINEAR));
    q.setRenderState(ts);

    Cylinder c = new Cylinder("1girl", 16, 16, 0.7f, 0.26f, true);
    c.setLocalTranslation(new Vector3f(1f,-3f,.13f));
    c.setModelBound(new BoundingBox());
    c.updateModelBound();
    rootNode.attachChild(c);

    ts = display.getRenderer().createTextureState();
    ts.setEnabled(true);
    ts.setTexture
	(TextureManager.loadTexture
	 (JmeDemo.class.getClassLoader().getResource
	  ("net/cscott/sdr/1girl.png"),
	  Texture.MM_LINEAR_LINEAR,
	  Texture.FM_LINEAR));
    c.setRenderState(ts);

    Box b = new Box("1boy",
		    new Vector3f(-0.7f, -0.7f, 0),
		    new Vector3f(0.7f, 0.7f, 0.26f));
    b.setLocalTranslation(new Vector3f(-1f,-3f,0));
    b.setModelBound(new BoundingBox());
    b.updateModelBound();
    rootNode.attachChild(b);

    ts = display.getRenderer().createTextureState();
    ts.setEnabled(true);
    ts.setTexture
	(TextureManager.loadTexture
	 (JmeDemo.class.getClassLoader().getResource
	  ("net/cscott/sdr/1boy.png"),
	  Texture.MM_LINEAR_LINEAR,
	  Texture.FM_LINEAR));
    b.setRenderState(ts);

  }
}
