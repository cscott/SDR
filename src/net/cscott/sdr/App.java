package net.cscott.sdr;

import net.cscott.sdr.anim.SdrGame;

/**
 * This is the main class of the SDR application.
 * It creates three main threads:
 * one to do voice recognition (in net.cscott.sdr.recog),
 * one to do dancer animation (in net.cscott.sdr.anim),
 * and one to play music (in net.cscott.sdr.sound).
 * 
 * @author C. Scott Ananian
 * @version $Id: App.java,v 1.2 2006-10-25 22:55:18 cananian Exp $
 */
public class App {
	/**
	 * The main entry point for the application.
	 * @param args unused
	 */
	public static void main(String[] args) {
		// TODO: create voice recognition thread
		// TODO: create music player thread
		SdrGame game = new SdrGame();
		game.start();
                
                // At all points during the game we have a current
                // TimedFormation.  We grab strings from the input
                // and parse them using the CommandInput, and apply them to the
                // current TimedFormation to get a time-sorted list of
                // additional TimedFormations.  We have to go through these
                // to extract TimedPositions for each dancer, which are
                // given to the AnimDancers.
	}
}
