package net.cscott.sdr;

import net.cscott.sdr.CommandInput.InputMode;
import net.cscott.sdr.calls.Program;

/** The {@link Mode} object coordinates mode changes among different
 *  threads (and mode-change sources).
 */
public class Mode {
    private final CommandInput input;
    private GameMode mode;
    public Mode(CommandInput input) {
        this.input = input;
        // starts at "loading" screen
        this.mode = GameMode.LOADING;
    }

    // get/change the game mode.
    public enum GameMode {
        LOADING,
        MAIN_MENU,
        DANCING
        // XXX other menus, special challenge modes or whatever
    };
    private synchronized boolean setMode(GameMode gm) {
        if (gm==this.mode) return false;
        this.mode = gm;
        this.notifyAll();
        return true;
    }
    public void switchToMenu() {
        setMode(GameMode.MAIN_MENU);
        this.input.switchMode(new InputMode() {
            @Override
            public Program program() { return null; }
            @Override
            public boolean mainMenu() { return true; }
        });
    }
    public synchronized void switchToDancing(final Settings s) {
        setMode(GameMode.DANCING);
        this.input.switchMode(new InputMode() {
            @Override
            public Program program() { return s.getDanceLevel().program; }
            @Override
            public boolean mainMenu() { return false; }
        });
    }
    public synchronized GameMode getMode() {
        return this.mode;
    }
    public synchronized void waitForMode(GameMode gm) {
        while (this.mode != gm) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                /* wait some more */
            }
        }
    }

}
