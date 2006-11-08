package net.cscott.sdr.anim;

import com.jme.input.InputHandler;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.input.action.InputAction;
import com.jme.input.action.InputActionEvent;
import com.jmex.game.state.GameState;
import com.jmex.game.state.GameStateManager;

public class MenuHandler extends InputHandler {
    private final Game game;

    public MenuHandler( Game game ) {
        setKeyBindings();
        this.game = game;
    }

    private void setKeyBindings() {
        KeyBindingManager.getKeyBindingManager().set("exit", KeyInput.KEY_ESCAPE);
        addAction( new ExitAction(), "exit", false );

        KeyBindingManager.getKeyBindingManager().set("enter", KeyInput.KEY_RETURN);
        addAction( new EnterAction(), "enter", false );
    }

    private static class ExitAction extends InputAction {
        public void performAction( InputActionEvent evt ) {
            System.exit(0); // XXX:
            //TestGameStateSystem.exit();
        }
    }

    private class EnterAction extends InputAction {
        public void performAction( InputActionEvent evt ) {
            game.hudState.setActive(true);
            game.menuState.setActive(false);
        }
    }
}
