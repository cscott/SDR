package net.cscott.sdr.anim;

import java.util.List;

import net.cscott.sdr.CommandInput;
import net.cscott.sdr.Settings;
import net.cscott.sdr.CommandInput.InputMode;
import net.cscott.sdr.calls.Program;
import net.cscott.sdr.recog.Microphone;
import net.cscott.sdr.recog.RecogThread;
import net.cscott.sdr.recog.Microphone.NameAndLine;

/** This subclass of {@link Settings} contains the Game UI-specific parts of
 *  the settings.  When a setting is changed, this class ensures that the
 *  proper parts of the UI are updated.
 */
public class GameSettings extends Settings {
    private final Game game;
    private final CommandInput input;
    private Microphone microphone;

    GameSettings(Game game, CommandInput input) {
        super(game.getName());
        this.game = game;
        this.input = input;
    }
    void finishInit(RecogThread.Control control) {
        this.microphone = control.microphone;
        setMicrophone(getMicrophone());
    }
    @Override
    public boolean setMode(GameMode gm) {
        if (!super.setMode(gm)) return false; // no change
        this.game.hudState.setActive(gm==GameMode.DANCING);
        this.game.menuState.setActive(gm==GameMode.MAIN_MENU);
        if (gm==GameMode.DANCING) {
            // XXX call ScoreAccumulator.resetScore()?
            this.game.hud.setNotice("Let's go!", 5000);
            this.input.switchMode(new InputMode() {
                @Override
                public Program program() { return getDanceLevel().program; }
                @Override
                public boolean mainMenu() { return false; }
            });
        } else {
            this.input.switchMode(new InputMode() {
                @Override
                public Program program() { return null; }
                @Override
                public boolean mainMenu() { return true; }
            });
        }
        return true;
    }

    @Override
    public void setMicrophone(int which) {
        List<NameAndLine> avail = getAvailableMicrophones();
        if (which < 0 || which >= avail.size()) which=0;
        super.setMicrophone(which);
        if (which < avail.size())
            this.microphone.switchMixer(avail.get(which));
    }
    public List<NameAndLine> getAvailableMicrophones() {
        return this.microphone.availableMixers();
    }
}
