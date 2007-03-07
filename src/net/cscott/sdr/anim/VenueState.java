package net.cscott.sdr.anim;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import net.cscott.sdr.BeatTimer;
import net.cscott.sdr.Version;
import net.cscott.sdr.calls.Dancer;
import net.cscott.sdr.calls.ExactRotation;
import net.cscott.sdr.calls.Formation;
import net.cscott.sdr.calls.FormationMapper;
import net.cscott.sdr.calls.Position;
import net.cscott.sdr.calls.StandardDancer;
import net.cscott.sdr.util.Fraction;

import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.light.PointLight;
import com.jme.math.FastMath;
import com.jme.math.Matrix3f;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Skybox;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.LightState;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.GameTaskQueueManager;
import com.jme.util.TextureManager;
import com.jme.util.Timer;
import com.jmex.game.state.CameraGameState;

/** The {@link VenueState} is in charge of showing the background venue
 *  and controlling the camera. (Maybe separate the camera into a separate
 *  game state?).  Eventually we may be able to instantiate multiple
 *  {@link VenueState}s and cross-fade between them to change venues.
 *  {@link VenueState} does not process any keyboard or mouse input.
 * @author C. Scott Ananian
 * @version $Id: VenueState.java,v 1.6 2007-03-07 19:17:20 cananian Exp $
 */
public class VenueState extends CameraGameState {
    private Skybox skybox;
    private DisplaySystem display;

    private List<AnimDancer> dancers = new ArrayList<AnimDancer>(8);
    private final static Vector3f camCaller = new Vector3f(0, -8.5f, 9.4f);
    private final static Vector3f camStartup = new Vector3f(8,20,50);
    private final static Vector3f camCeiling = new Vector3f(0, -.1f, 11);
    private Vector3f camTarget = camCaller;
    private Quaternion oldCamDirection=new Quaternion();
    private Vector3f oldCamLocation=new Vector3f();
    private float oldCamTime = 0;
    private static final float SLEW_TIME = 2.0f; /* seconds */

    /** High resolution timer for tempo-independent animation. */
    protected Timer timer;
    /** Music-synced timer. */
    protected BeatTimer beatTimer;

    public VenueState(BeatTimer beatTimer) {
        super("Venue");
        this.beatTimer = beatTimer;
        initState();
    }
    private void initState() {
        /* Get local copy of the Display System. */
        display = DisplaySystem.getDisplaySystem();
        /* Get a high resolution timer for FPS updates. */
        timer = Timer.getTimer();
        
        /** Set up a basic, default light. */
        PointLight light = new PointLight();
        light.setDiffuse( new ColorRGBA( 0.75f, 0.75f, 0.75f, 0.75f ) );
        light.setAmbient( new ColorRGBA( 0.5f, 0.5f, 0.5f, 1.0f ) );
        light.setLocation( new Vector3f( 100, 100, 100 ) );
        light.setEnabled( true );
        
        /** Attach the light to a lightState and the lightState to rootNode. */
        LightState lightState = display.getRenderer().createLightState();
        lightState.setEnabled( true );
        lightState.attach( light );
        rootNode.setRenderState( lightState );
        
        // setup camera
        GameTaskQueueManager.getManager().update(new Callable<Void>() {
            public Void call() throws Exception {
                cam.setFrustumPerspective( 45.0f, (float) display.getWidth()
                        / (float) display.getHeight(), 1, 1000 );
                cam.setParallelProjection( false );
                cam.setLocation(camStartup);
                cam.lookAt(new Vector3f(0,0,0), new Vector3f(0,0,1));
                onCamera(); // initialize camera tracking.
                return null;
            }
        });
        
        // pretty sky box
        skybox = new Skybox("skybox", 10, 10, 10);
        
        Texture north = TextureManager.loadTexture(
                SdrGame.class.getClassLoader().getResource(
                "net/cscott/sdr/anim/north.jpg"),
                Texture.MM_LINEAR,
                Texture.FM_LINEAR);
        Texture south = TextureManager.loadTexture(
                SdrGame.class.getClassLoader().getResource(
                "net/cscott/sdr/anim/south.jpg"),
                Texture.MM_LINEAR,
                Texture.FM_LINEAR);
        Texture east = TextureManager.loadTexture(
                SdrGame.class.getClassLoader().getResource(
                "net/cscott/sdr/anim/east.jpg"),
                Texture.MM_LINEAR,
                Texture.FM_LINEAR);
        Texture west = TextureManager.loadTexture(
                SdrGame.class.getClassLoader().getResource(
                "net/cscott/sdr/anim/west.jpg"),
                Texture.MM_LINEAR,
                Texture.FM_LINEAR);
        Texture up = TextureManager.loadTexture(
                SdrGame.class.getClassLoader().getResource(
                "net/cscott/sdr/anim/top.jpg"),
                Texture.MM_LINEAR,
                Texture.FM_LINEAR);
        Texture down = TextureManager.loadTexture(
                SdrGame.class.getClassLoader().getResource(
                "net/cscott/sdr/anim/bottom.jpg"),
                Texture.MM_LINEAR,
                Texture.FM_LINEAR);
        
        skybox.setTexture(Skybox.NORTH, north); //up
        skybox.setTexture(Skybox.WEST, west); //east
        skybox.setTexture(Skybox.SOUTH, south);//down
        skybox.setTexture(Skybox.EAST, east);//west
        skybox.setTexture(Skybox.UP, up);//north
        skybox.setTexture(Skybox.DOWN, down);
        GameTaskQueueManager.getManager().update(new Callable<Void>() {
            public Void call() throws Exception {
		skybox.preloadTextures();
		return null;
	    }
	});
        Matrix3f skyRot = new Matrix3f();
        skyRot.fromAngleNormalAxis(FastMath.PI/2,new Vector3f(1,0,0));
        skybox.setLocalRotation(skyRot);
        
        rootNode.attachChild(skybox);
        
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
                (SdrGame.class.getClassLoader().getResource
                        ("net/cscott/sdr/anim/floor.png"),
                        Texture.MM_LINEAR_LINEAR,
                        Texture.FM_LINEAR));
        q.setRenderState(ts);
        MaterialState ms = display.getRenderer().createMaterialState();
        ms.setAmbient(new ColorRGBA(1,1,1,1));
        q.setRenderState(ms);
        
        // create dancers
        Formation f = Formation.SQUARED_SET;
        //for (Dancer d : f.dancers()) {
        for (Dancer d : StandardDancer.values()) {
            AnimDancer ad = new CheckerDancer(display, (StandardDancer) d);
            dancers.add(ad);
            rootNode.attachChild(ad.node);
            ad.addPosition(Fraction.ZERO, f.location(d));
        }
        if (false) {
            // dancer[5] is COUPLE 3 GIRL, starting at (-1,3,1/2)
            for (int i=0; i<40; i+=8) {
                dancers.get(5).addPosition(Fraction.valueOf(i+2), new Position(Fraction.valueOf(-3), Fraction.valueOf(1), ExactRotation.THREE_QUARTERS));
                dancers.get(5).addPosition(Fraction.valueOf(i+4), new Position(Fraction.valueOf(-4), Fraction.valueOf(3), ExactRotation.ZERO));
                dancers.get(5).addPosition(Fraction.valueOf(i+6), new Position(Fraction.valueOf(-3), Fraction.valueOf(4), ExactRotation.ONE_QUARTER));
                dancers.get(5).addPosition(Fraction.valueOf(i+8), new Position(Fraction.valueOf(-1), Fraction.valueOf(3), ExactRotation.ONE_HALF));
            }
        } else {
            // help debug FormationMapper
            FormationMapper.main(null);
            for (AnimDancer ad: dancers) {
                ad.addPosition(Fraction.valueOf( 5), FormationMapper.test1.location(ad.dancer));
                ad.addPosition(Fraction.valueOf(10), FormationMapper.test1.location(ad.dancer));
                ad.addPosition(Fraction.valueOf(15), FormationMapper.test2.location(ad.dancer));
                ad.addPosition(Fraction.valueOf(20), FormationMapper.test2.location(ad.dancer));
                ad.addPosition(Fraction.valueOf(25), FormationMapper.test1.location(ad.dancer));
            }
        }
        
        rootNode.updateGeometricState( 0.0f, true );
        rootNode.updateRenderState();
        
        // key bindings.
        
        // Assign F5 to the command "caller camera"
        KeyBindingManager.getKeyBindingManager().set
        ("camCaller", KeyInput.KEY_F5);
        // Assign F6 to the command "ceiling camera"
        KeyBindingManager.getKeyBindingManager().set
        ("camCeiling", KeyInput.KEY_F6);
    }
    public void onCamera() {
        oldCamLocation.set(cam.getLocation());
        oldCamDirection.fromAxes(cam.getLeft(), cam.getUp(), cam.getDirection());
        oldCamTime = timer.getTimeInSeconds();
    }

    @Override
    public void stateUpdate(float tpf) {
        // check for camera movement commands.
        if (KeyBindingManager.getKeyBindingManager().isValidCommand
            ("camCaller",true)) {
            camTarget = camCaller;
            onCamera();
        }
        if (KeyBindingManager.getKeyBindingManager().isValidCommand
            ("camCeiling",true)) {
            camTarget = camCeiling;
            onCamera();
        }
    
        // move cam towards target /////////////

        // allow more time for the move if the target is far away.
        float dist = oldCamLocation.distance(camTarget);
        float slew = SLEW_TIME*Math.min(4,Math.max(Math.abs(dist/5), 1));
        // move towards desired cam location
        float interp = Math.min((timer.getTimeInSeconds()-oldCamTime)/slew, 1);
        // now apply acceleration factor.
        interp = (interp<.5f)?(2*interp*interp):(interp*(4-2*interp)-1);
        
        m_location.set(oldCamLocation);
        m_location.interpolate(camTarget,interp);
        cam.setLocation(m_location);
        //System.err.println("CAMERA: "+m_location);
        // keep skybox with cam.
        skybox.setLocalTranslation(m_location);
            
        // target direction is from current location towards 0,0,0
        // with upvector 0,0,1 (maybe should slew from current location?)
        m_rotation.lookAt(Vector3f.ZERO.subtract(camTarget), Vector3f.UNIT_Z);
        m_rotation.slerp(oldCamDirection,1-interp);
        cam.setAxes(m_rotation);
        
        // update dancer locations.
        Fraction beats = beatTimer.getCurrentBeat();
        for (AnimDancer ad : dancers)
            ad.update(beats);
    }
    private final Vector3f m_location = new Vector3f();
    private final Quaternion m_rotation = new Quaternion();

    @Override
    protected void onActivate() {
        display.setTitle(Version.PACKAGE_STRING);
        super.onActivate();
    }
    protected void initCamera() {
        try {
            GameTaskQueueManager.getManager().update(new Callable<Void>() {
                public Void call() throws Exception {
                    _initCamera(); return null;
                }
            })/*.get()*/;
        } catch (Exception e) { /* this is fatal */ assert false : e; }
    }
    void _initCamera() { super.initCamera(); }
}
