package net.cscott.sdr.anim;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.SharedNode;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Cylinder;
import com.jme.scene.shape.Quad;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.TextureState;
import com.jme.util.LoggingSystem;
import com.jme.util.TextureManager;

import java.net.URL;

public class JmeDemo extends SimpleGame {

  private Node[] checker = new Node[8];

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
    cam.lookAt(new Vector3f(0,0,0), new Vector3f(0,0,1));

    // create floor.
    Quad q = new Quad("floor", 10, 10);
    q.setLocalTranslation(q.getCenter().negate());
    q.setModelBound(new BoundingBox());
    q.updateModelBound();
    rootNode.attachChild(q);

    TextureState ts = display.getRenderer().createTextureState();
    ts.setEnabled(true);
    ts.setTexture
	(TextureManager.loadTexture
	 (JmeDemo.class.getClassLoader().getResource
	  ("net/cscott/sdr/floor.png"),
	  Texture.MM_LINEAR_LINEAR,
	  Texture.FM_LINEAR));
    q.setRenderState(ts);
    MaterialState ms = display.getRenderer().createMaterialState();
    ms.setAmbient(new ColorRGBA(1,1,1,1));
    q.setRenderState(ms);

    // create materials for the couples
    MaterialState[] mats = new MaterialState[4];
    ColorRGBA[] colors = new ColorRGBA[] {
	new ColorRGBA(1,10/255f,18/255f,1),
	new ColorRGBA(0,135/255f,250/255f,1),
	new ColorRGBA(240/255f,1,69/255f,1),
	new ColorRGBA(151/255f,1,84/255f,1),
    };
    for (int i=0; i<mats.length; i++) {
	mats[i] = display.getRenderer().createMaterialState();
	mats[i].setAmbient(colors[i]);
    }
    // create nodes for the couples.
    int[] chx = new int[] { -1, 1, 3, 3, 1, -1, -3, -3 };
    int[] chy = new int[] { -3, -3, -1, 1, 3, 3, 1, -1 };
    for (int i=0; i<8; i++) {
      checker[i] = new Node("checker"+i);
      checker[i].setLocalTranslation(new Vector3f(chx[i],chy[i],0));
      checker[i].setLocalRotation(new Quaternion(new float[] {0,0,(float)((i/2)*Math.PI/2) }));
      checker[i].updateModelBound();
      checker[i].setTextureCombineMode(TextureState.COMBINE_FIRST);
      rootNode.attachChild(checker[i]);
    }
      
    // now create boys.
    for (int i=0; i<checker.length/2; i++) {
	Box b = new Box("boy"+i,
			new Vector3f(-0.7f, -0.7f, 0f),
			new Vector3f(0.7f, 0.7f, 0.26f));
	b.setLocalTranslation(new Vector3f(0,0,.01f));
	b.setModelBound(new BoundingBox());
	b.updateModelBound();
	b.setRenderState(mats[i]);
	checker[i*2].attachChild(b);
    }
    // and girls
    for (int i=0; i<checker.length/2; i++) {
	Cylinder c = new Cylinder((i+1)+"girl", 16, 16, 0.7f, 0.26f, true);
	c.setLocalTranslation(new Vector3f(0,0,0.14f));
	c.setModelBound(new BoundingBox());
	c.updateModelBound();
	c.setRenderState(mats[i]);
	checker[i*2+1].attachChild(c);
    }
    // and label them.
    TextureState arrowTS = display.getRenderer().createTextureState();
    arrowTS.setEnabled(true);
    arrowTS.setTexture(TextureManager.loadTexture
		       (JmeDemo.class.getClassLoader().getResource
			("net/cscott/sdr/arrow.png"),
			Texture.MM_LINEAR_LINEAR,
			Texture.FM_LINEAR));
    TextureState[] numTS = new TextureState[4];
    for (int i=0; i<numTS.length; i++) {
      numTS[i] = display.getRenderer().createTextureState();
      numTS[i].setEnabled(true);
      numTS[i].setTexture(TextureManager.loadTexture
			  (JmeDemo.class.getClassLoader().getResource
			   ("net/cscott/sdr/"+(i+1)+".png"),
			   Texture.MM_LINEAR_LINEAR,
			   Texture.FM_LINEAR));
    }
    AlphaState as = display.getRenderer().createAlphaState();
    as.setBlendEnabled(true);
    as.setSrcFunction(AlphaState.SB_SRC_ALPHA);
    as.setDstFunction(AlphaState.SB_ONE_MINUS_SRC_ALPHA);
    as.setTestEnabled(true);
    as.setTestFunction(AlphaState.TF_GREATER);
    
    for (int i=0; i<8; i++) {
      boolean isBoy = (0==(i%2));
      // arrow
      Quad qq = new Quad("arrow"+i, 1.3f, 1.3f);
      qq.setLocalTranslation(new Vector3f(0,isBoy?0f:.1f,.3f));
      qq.setRenderState(arrowTS);
      qq.setRenderState(as);
      qq.updateModelBound();
      checker[i].attachChild(qq);
      // number
      qq = new Quad("num"+i, .4f, .4f);
      qq.setLocalTranslation(new Vector3f(0,0,.32f));
      qq.setRenderState(numTS[i/2]);
      qq.setRenderState(as);
      qq.updateModelBound();
      checker[i].attachChild(qq);
    }
  }
}
