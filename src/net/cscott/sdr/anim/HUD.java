package net.cscott.sdr.anim;

/** The {@link HUD} class encapsulates all the values displayed by the
 *  game's "heads up display".  It decouples these values from the code
 *  which displays them, and allows for thread-safe communication between
 *  the application threads updating the HUD values, and the rendering
 *  thread displaying them.
 * @author C. Scott Ananian
 * @version $Id: HUD.java,v 1.1 2006-11-07 23:08:42 cananian Exp $
 */
public class HUD {
    public HUD() { }

    private float volume;
    private float flow;
    private float originality;
    private int score;

}
