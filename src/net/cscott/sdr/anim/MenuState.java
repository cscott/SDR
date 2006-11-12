package net.cscott.sdr.anim;

import net.cscott.sdr.Version;
import net.cscott.sdr.anim.TextureText.JustifyX;
import net.cscott.sdr.anim.TextureText.JustifyY;

import com.jme.image.Texture;
import com.jme.input.AbsoluteMouse;
import com.jme.input.InputHandler;
import com.jme.input.Mouse;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jmex.game.state.GameState;
import com.jmex.game.state.StandardGameStateDefaultCamera;

/** The {@link MenuState} displays a cursor on the screen and an appropriate
 *  menu of options.  It uses ORTHO mode and does not reset the camera,
 *  so some other camera-controlling state should also be active for
 *  background visuals.
 * @author C. Scott Ananian
 * @version $Id: MenuState.java,v 1.6 2006-11-12 20:25:06 cananian Exp $
 */
public class MenuState extends StandardGameStateDefaultCamera {

    public MenuState(Game game) {
        super(Version.PACKAGE_NAME+" Menu");

        display = DisplaySystem.getDisplaySystem();
        initInput(game);
        initStar();
        initMenus();
        initCursor();

        rootNode.setLightCombineMode(LightState.OFF);
        rootNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);
        rootNode.updateRenderState();
        rootNode.updateGeometricState(0, true);
    }
        
    /** The cursor node which holds the mouse gotten from input. */
    private Node cursor;
        
    /** Our display system. */
    private DisplaySystem display;

    private TextureText text;
    
    private InputHandler input;
    private Mouse mouse;

    private Star star;
    private float starAngle = 0;
    private Quaternion starRot = new Quaternion();
    
    /**
     * @see com.jmex.game.state.StandardGameState#onActivate()
     */
    public void onActivate() {
        display.setTitle(Version.PACKAGE_STRING+" Main Menu");
    }
        
    /**
     * Inits the input handler we will use for navigation of the menu.
     */
    protected void initInput(Game game) {
        input = new MenuHandler( game );

        mouse = new AbsoluteMouse("Mouse Input", display.getWidth(),
                display.getHeight());
        mouse.registerWithInputHandler( input );
    }

    /**
     * Create a star slowly rotating in the background.
     */
    private void initStar() {
        int HUD_SPACE = 40; // pixels down at the bottom for note display
        star = new Star("menu/star", 5, display.getHeight()-HUD_SPACE);
        star.getLocalTranslation().set
        (x(320), display.getHeight()/2 + (HUD_SPACE/4), 0);
        starAngle = 0;
        starRot.fromAngleAxis(starAngle,Vector3f.UNIT_Z);
        star.setLocalRotation(starRot);
        
        Texture texture =
            TextureManager.loadTexture(
                    MenuState.class.getClassLoader().getResource(
                    "net/cscott/sdr/anim/star-grad.png"),
                    Texture.MM_NONE,
                    Texture.FM_LINEAR);
        
        TextureState ts = display.getRenderer().createTextureState();
        ts.setEnabled(true);
        ts.setTexture(texture);
        star.setRenderState(ts);
        
        AlphaState alpha = display.getRenderer().createAlphaState();
        alpha.setBlendEnabled(true);
        alpha.setSrcFunction(AlphaState.SB_SRC_ALPHA);
        alpha.setDstFunction(AlphaState.DB_ONE_MINUS_SRC_ALPHA);
        alpha.setTestEnabled(true);
        alpha.setTestFunction(AlphaState.TF_GREATER);
        alpha.setEnabled(true);
        star.setRenderState(alpha);
        star.updateRenderState();

        rootNode.attachChild(star);
    }
    
    /**
     * Creates a pretty cursor.
     */
    private void initCursor() {             
        Texture texture =
            TextureManager.loadTexture(
                    MenuState.class.getClassLoader().getResource(
                    "net/cscott/sdr/anim/cursor1.png"),
                    Texture.MM_LINEAR_LINEAR,
                    Texture.FM_LINEAR);
        
        TextureState ts = display.getRenderer().createTextureState();
        ts.setEnabled(true);
        ts.setTexture(texture);
        
        AlphaState alpha = display.getRenderer().createAlphaState();
        alpha.setBlendEnabled(true);
        alpha.setSrcFunction(AlphaState.SB_SRC_ALPHA);
        alpha.setDstFunction(AlphaState.DB_ONE);
        alpha.setTestEnabled(true);
        alpha.setTestFunction(AlphaState.TF_GREATER);
        alpha.setEnabled(true);
        
        mouse.setRenderState(ts);
        mouse.setRenderState(alpha);
        mouse.setLocalScale(new Vector3f(1, 1, 1));
        
        cursor = new Node("Cursor");
        cursor.attachChild( mouse );
        
        rootNode.attachChild(cursor);
    }
    
    /**
     * Inits the button placed at the center of the screen.
     */
    private void initMenus() {
        text = new TextureText("menu/text", HUDState.font, 128); 
        text.setAlign(JustifyX.CENTER, JustifyY.BOTTOM);
        text.setMaxSize(x(620),36);
        text.setText("Say \"Square Up\" or press Enter to start, Esc to quit.");
        text.getLocalTranslation().set
        ( x(320), 3, 0 );
        
        rootNode.attachChild( text );
        
        // top one at y center 446
        // bottom one y center 127
        // 6 ovals.  64 pixels between. bottom at 127
        String[] labels = { "Judging Difficulty", "Dancers", "Venue", "Dance Level", "Music", "Microphone" };
        String[] values = { "Moderate", "Checkers", "Mountains", "4-dancer Plus", "Music Off", "Line Input" };
        for (int i=0; i<6; i++) {
            float y = y(127+64*i);
            RedOval ro = new RedOval("menu/oval", x(509),y(50));
            ro.getLocalTranslation().set(x(320),y,0);
            rootNode.attachChild(ro);
            TextureText tt = new TextureText
            ("menu/menu text "+i, HUDState.font, 128);
            tt.setAlign(JustifyX.LEFT, JustifyY.MIDDLE);
            tt.setMaxSize(x(280),y(44));
            tt.setText(labels[i]);
            tt.setColor(new ColorRGBA(.95f,.95f,.95f,1));
            tt.getLocalTranslation().set(x(83),y,0);
            rootNode.attachChild(tt);
            tt = new TextureText
            ("menu/menu value "+i, HUDState.font, 128);
            tt.setAlign(JustifyX.CENTER, JustifyY.MIDDLE);
            tt.setMaxSize(x(150), y(24));
            tt.setText(values[i]);
            tt.setColor(new ColorRGBA(1,1,0,1));
            tt.getLocalTranslation().set(x(468),y,0);
            rootNode.attachChild(tt);
        }
    }
    
    /**
     * Updates input and button.
     * 
     * @param tpf The time since last frame.
     * @see GameState#update(float)
     */
    protected void stateUpdate(float tpf) {
        // rotate the star
        starAngle+=.005f; // slowly rotate the star
        starRot.fromAngleAxis(starAngle,Vector3f.UNIT_Z);
        star.setLocalRotation(starRot);
        // update the input handler
        input.update(tpf);
        // Check if the button has been pressed.
        rootNode.updateGeometricState(tpf, true);
    }
    
    //---------------------------------------------------------
    // convert 640x480-relative coordinates to appropriately scaled coords.
    private float x(int x) {
        return x*display.getWidth()/640f;
    }
    private float y(int y) {
        return y*display.getHeight()/480f;
    }
}
