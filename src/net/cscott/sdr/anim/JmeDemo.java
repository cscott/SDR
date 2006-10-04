package net.cscott.sdr.anim;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
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
    app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG, url);
    app.start();
  }

  protected void simpleUpdate() {
    if (tpf < 1) {
      angle = angle + (tpf * 1);
      if (angle > 360) {
        angle = 0;
      }
    }
    rotQuat.fromAngleAxis(angle, axis);
    s.setLocalRotation(rotQuat);
  }

  /**
   * builds the trimesh.
   * @see com.jme.app.SimpleGame#initGame()
   */
  protected void simpleInitGame() {
    display.setTitle("SDR - jME demo");

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

    rootNode.setRenderState(ts);
  }
}
