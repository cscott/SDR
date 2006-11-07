package net.cscott.sdr.anim;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.logging.Level;

import net.cscott.sdr.calls.*;

import com.jme.app.FixedFramerateGame;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.input.MouseInput;
import com.jme.input.joystick.JoystickInput;
import com.jme.renderer.ColorRGBA;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.GameTaskQueue;
import com.jme.util.GameTaskQueueManager;
import com.jme.util.LoggingSystem;
import com.jme.util.TextureManager;
import com.jme.util.Timer;
import com.jmex.game.state.GameState;
import com.jmex.game.state.GameStateManager;
import com.jmex.game.state.load.TransitionGameState;

public class Game extends FixedFramerateGame {
    private Timer timer;
    MenuState menuState;
    VenueState venueState;
    HUDState hudState;

    public Game() {
        //LoggingSystem.getLogger().setLevel(java.util.logging.Level.OFF);
        URL url = SdrGame.class.getClassLoader().getResource      
            ("net/cscott/sdr/anim/splash.png");
        this.setDialogBehaviour
            (FIRSTRUN_OR_NOCONFIGFILE_SHOW_PROPS_DIALOG, url);
    }
    /** Creates display, sets up camera, and binds keys. */
    protected void initSystem() {
        LoggingSystem.getLogger().log( Level.INFO, getVersion());
        try {
            /** Get a DisplaySystem according to startup prefs. */
            display = DisplaySystem.getDisplaySystem(properties.getRenderer());
            LoggingSystem.getLogger().log
                ( Level.INFO, "Running on: "+display.getAdapter()+"\n"+
                  "Driver version: "+display.getDriverVersion());
            /** Create a window with the startup box's information. */
            display.setTitle("SDR");
            display.createWindow(properties.getWidth(), properties.getHeight(),
                                 properties.getDepth(), properties.getFreq(),
                                 properties.getFullscreen() );
        } catch ( JmeException e ) {
            /**
             * If the displaysystem can't be initialized correctly, exit
             * instantly.
             */
            e.printStackTrace();
            System.exit( 1 );
        }
        /** Set a green background (nicer during initial load) */
        display.getRenderer().setBackgroundColor( ColorRGBA.green );
        // global "quit now" command.
        KeyBindingManager.getKeyBindingManager().set( "exit",
                KeyInput.KEY_ESCAPE );

        /** Close the splash screen, if not already closed. */
        try {
            Class c = Class.forName("java.awt.SplashScreen");
            Method m = c.getMethod("getSplashScreen");
            Object o = m.invoke(null); // SplashScreen instance.
            if (o!=null) {
                m = c.getMethod("close");
                m.invoke(o);  // SplashScreen.close().
            }
        } catch (Exception ce) {
            /* ignore: must be running on pre-1.6 jdk */
        }
    }
 

    @Override
    protected void initGame() {
        setFrameRate(20); // limit frame rate: save our CPU for speech
        display.setTitle(net.cscott.sdr.Version.PACKAGE_STRING);
        /** Get a high resolution timer for FPS updates. */
        timer = Timer.getTimer();

        // Creates the GameStateManager. Only needs to be called once.
        GameStateManager.create();
        // Adds a new GameState to the GameStateManager. In order for it to get
        // processed (rendered and updated) it needs to get activated.

        URL url = SdrGame.class.getClassLoader().getResource      
        ("net/cscott/sdr/anim/loading.png");
        final TransitionGameState loading = new TransitionGameState(7, url);
        loading.setActive(true);
        GameStateManager.getInstance().attachChild(loading);

        final GameTaskQueue updateQueue =
            GameTaskQueueManager.getManager().getQueue(GameTaskQueue.UPDATE);
        new Thread() {
            public void run() {
                inc("Loading menus...");
                menuState = new MenuState();
                attach(menuState, false);
                
                inc("Loading venue...");
                venueState = new VenueState();
                attach(venueState,true);

                inc("Loading formations...");
                // load formations
                try { Class.forName(FormationList.class.getName());
                } catch (ClassNotFoundException e) { /* ignore */ }
                inc("Loading call list...");
                // load call database
                try { Class.forName(CallDB.class.getName());
                } catch (ClassNotFoundException e) { /* ignore */ }

                inc("Creating HUD...");
                hudState = new HUDState(null);
                attach(hudState,true);
                
                inc("Creating menus...");
                updateQueue.enqueue(new Callable<Void>() {
                    public Void call() throws Exception {
                        menuState.setActive(true);
                        return null;
                    }
                });
                inc("Loading complete.");
            }
            private void attach(final GameState state,final boolean active){
                updateQueue.enqueue(new Callable<Void>() {
                    public Void call() throws Exception {
                        state.setActive(active);
                        GameStateManager.getInstance().attachChild(state);
                        return null;
                    }
                });
            }
            private void inc(final String msg) {
                updateQueue.enqueue(new Callable<Void>() {
                    public Void call() throws Exception {
                        loading.increment(msg); return null;
                    }
                });
            }
        }.start();
    }
    private float tpf=0;
    @Override
    protected final void update(float _unused) {
        timer.update();
        tpf = timer.getTimePerFrame();
        /* Execute anything on the update queue. */
        GameTaskQueueManager.getManager().getQueue(GameTaskQueue.UPDATE)
            .execute();
        // Update the current game state.
        GameStateManager.getInstance().update(tpf);
    }
    @Override
    protected final void render(float _unused) {
        // Clears the previously rendered information.
        display.getRenderer().clearBuffers();
        /* Execute anything on the render queue. */
        GameTaskQueueManager.getManager().getQueue(GameTaskQueue.RENDER)
        .execute();
        // Render the current game state.
        GameStateManager.getInstance().render(tpf);
        // global quit.
        if (KeyBindingManager.getKeyBindingManager()
                .isValidCommand( "exit", false ) ) {
                finish();
            }
    }
    @Override
    protected void cleanup() {
        LoggingSystem.getLogger().log(Level.INFO, "Cleaning up resources.");
        
        // Performs cleanup on all loaded game states.
        GameStateManager.getInstance().cleanup();

        TextureManager.doTextureCleanup();
        KeyInput.destroyIfInitalized();
        MouseInput.destroyIfInitalized();
        JoystickInput.destroyIfInitalized();
    }
    /** Calls the quit of BaseGame to clean up the display and then
     * closes the JVM. */
    @Override
    protected void quit() {
        super.quit();
        System.exit( 0 );
    }

    // test
    public static void main(String... args) {
        Game game = new Game();
        game.start();
    }
    @Override
    protected void reinit() {
        // do nothing
    }
}
