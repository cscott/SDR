package net.cscott.sdr;

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

    // [0-1]
    private float flow = 0;
    public synchronized void setFlow(float flow) { this.flow = flow; }
    public synchronized float getFlow() { return this.flow; }

    // [0-1]
    private float originality = 0;
    public synchronized void setOriginality(float originality) { this.originality = originality; }
    public synchronized float getOriginality() { return originality; }

    // [0-1]
    private float sequenceLength = 0;
    public synchronized void setSequenceLength(float sequenceLength) { this.sequenceLength=sequenceLength; }
    public synchronized float getSequenceLength() { return sequenceLength; }

    // [0-1000]
    private int score = 0;
    public synchronized void setScore(int score) { this.score = score; }
    public synchronized void addToScore(int amount) { this.score += amount; }
    public synchronized int getScore() { return this.score; }

    private String notice = null;
    private long noticeUntil = 0;
    public synchronized String getNotice() {
        long time = System.currentTimeMillis();
        if (time < noticeUntil)
            return notice;
        return null; // timed out.
    }
    public synchronized void setNotice(String text, long timeout) {
        this.notice = text;
        this.noticeUntil = System.currentTimeMillis() + timeout;
    }

    private String currentCall = null;
    public synchronized String getCurrentCall() { return this.currentCall; }
    public synchronized void setCurrentCall(String currentCall) { this.currentCall = currentCall; }

    private String bonus = null;
    public synchronized String getBonus() { return this.bonus; }
    public synchronized void setBonus(String bonus) { this.bonus = bonus; }
}
