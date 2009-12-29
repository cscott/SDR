package net.cscott.sdr.anim;

import net.cscott.sdr.anim.GameSettings.GameMode;

import com.jme.input.InputHandler;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.input.action.InputAction;
import com.jme.input.action.InputActionEvent;

public class MenuHandler extends InputHandler {
    private final Game game;

    public MenuHandler( Game game ) {
        setKeyBindings();
        this.game = game;
    }

    private void setKeyBindings() {
        KeyBindingManager.getKeyBindingManager().set("enter", KeyInput.KEY_RETURN);
        addAction( new EnterAction(), "enter", false );
    }

    private class EnterAction extends InputAction {
        public void performAction( InputActionEvent evt ) {
            game.settings.setMode(GameMode.DANCING);
        }
    }
}
