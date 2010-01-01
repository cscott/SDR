package net.cscott.sdr;

import net.cscott.sdr.calls.Formation;
import net.cscott.sdr.calls.Program;
import net.cscott.sdr.calls.ast.Apply;

/**
 * This interface is the means of communication between the
 * choreography engine and the game score mechanism.  Information about
 * flow, formation variety, and call repetition will be communicated
 * through calls to an object implementing this interface.  That object
 * is responsible for (a) assigning numerical weights to the different
 * scoring events/criteria, and (b) communicating these events/criteria
 * to the player (for example, by updating score meters or displaying
 * messages).
 * @author C. Scott Ananian
 * @version $Id: ScoreAccumulator.java,v 1.2 2006-11-10 15:24:20 cananian Exp $
 */
public class ScoreAccumulator {
    private final HUD hud;
    private int score = 0;
    ScoreAccumulator(HUD hud) {
        this.hud = hud;
        resetScore(Program.PLUS); // XXX should be done by ChoreoEngine
    }
    /** Called at the beginning of a player's turn. */
    public void resetScore(Program program) {
        this.score = 0;
        // XXX just get something up on the HUD for now.
        this.hud.setScore(1234567);
        this.hud.setNotice("Let's go!", 5000);
        this.hud.setBonus("Right-hand Columns");
        this.hud.setMessage("Speak a "+program.toTitleCase()+" call!",
                            HUD.MessageType.ADVICE);
        this.hud.setFlow(0.1f);
        this.hud.setOriginality(0.1f);
        this.hud.setSequenceLength(0.1f);
    }
    // XXX: this should take something like the beatTime or a difference
    // from ideal beatTime, but at the moment we're just giving the raw
    // wall clock time (in ms since the epoch).
    // ending formation is used to assign bonuses.
    public void goodCallGiven(Apply theCall, Formation endingFormation, long startTime, long endTime) {
        // XXX check whether formation matches bonus, time out bonus, make new bonus, etc
        addToScore(100);
    }

    public void illegalCallGiven(String theBadCall, String message) {
        addToScore(-100);
    }
    private void addToScore(int amount) {
        score += amount;
        if (score < 0) score = 0;
        this.hud.setScore(score);
    }
}
