package net.cscott.sdr;

import java.util.concurrent.CountDownLatch;

import net.cscott.sdr.CommandInput.InputMode;
import net.cscott.sdr.calls.Program;

/** The {@link Mode} object coordinates mode changes among different
 *  threads (and mode-change sources).
 */
public class Mode {
    private final CommandInput input;
    private final ChoreoEngine choreo;
    private GameMode mode;
    private Settings settings;
    public Mode(CommandInput input, ChoreoEngine choreo) {
        this.input = input;
        this.choreo = choreo;
        // starts at "loading" screen
        this.mode = GameMode.LOADING;
    }
    public synchronized void setSettings(Settings s) {
        this.settings = s;
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
        if (gm==GameMode.DANCING && settings==null)
            return false;
        this.mode = gm;
        this.notifyAll();
        return true;
    }
    public synchronized void switchToMenu() {
        if (!setMode(GameMode.MAIN_MENU)) return; // nope!
        CountDownLatch done = this.choreo.switchToMenu();
        this.input.switchMode(new InputMode() {
            @Override
            public Program program() { return null; }
            @Override
            public boolean mainMenu() { return true; }
        });
    }
    public synchronized void switchToDancing() {
        final Program p = settings.getDanceLevel().program;
        if (!setMode(GameMode.DANCING)) return; // nope!
        CountDownLatch done = this.choreo.switchToDancing();
        this.input.switchMode(new InputMode() {
            @Override
            public Program program() { return p; }
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
