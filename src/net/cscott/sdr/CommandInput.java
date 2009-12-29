package net.cscott.sdr;

import net.cscott.jutil.UnmodifiableIterator;
import net.cscott.sdr.calls.BadCallException;
import net.cscott.sdr.calls.CallDB;
import net.cscott.sdr.calls.DanceProgram;
import net.cscott.sdr.calls.ast.Apply;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/** {@link CommandInput} implements the communication between some mechanism
 * for inputting commands (voice recognition, keyboard input, file on disk,
 * etc) and the rest of the SDR system.  In particular, we give the
 * {@code CommandInput} to the input mechanisms, and our {@link App}
 * class will deal with polling it for commands and feeding them to the
 * choreography engine and then to the animation system.
 * <p>
 * Note that a command is actually a lazy list of <i>possible</i> commands,
 * ordered from most-likely to least-likely.  We'll try each possibility
 * and take the first one that works.
 * @author C. Scott Ananian
 * @version $Id: CommandInput.java,v 1.4 2006-11-10 15:24:20 cananian Exp $
 */
public class CommandInput {
    /** Create a CommandInput object to synchronize communication between
     * input routines and the choreography engine. */
    public CommandInput() { }
    
    /** This is the actual cmdQueue we'll use for synchronization. */
    private final BlockingQueue<PossibleCommand> cmdQueue =
        new LinkedBlockingQueue<PossibleCommand>();
    private final BlockingQueue<InputMode> modeQueue =
        new LinkedBlockingQueue<InputMode>();

    /**
     * Called by input methods (voice recognition, keyboard input,
     * filer readers, etc) when they have another (set of)
     * {@link PossibleCommand}(s) to issue.
     */
    public void addCommand(PossibleCommand c) {
        while (true)
            try {
                cmdQueue.put(c);
                return;
            } catch (InterruptedException e) {
                assert false : "should never happen";
            }
    }
    /** Called by the main code to get the next command from the input
     * method.  Blocks until a possible command is available.
     * @return the next possible command.
     */
    public PossibleCommand getNextCommand() throws InterruptedException {
        return cmdQueue.take();
    }

    /** Tell the input method that a new set of calls is valid.  If the
     *  {@code dp} parameter is null, then we are at the main menu, waiting
     *  for a "square up" command. */
    public void switchMode(InputMode mode) {
        while (true)
            try {
                modeQueue.put(mode);
                return;
            } catch (InterruptedException e) {
                assert false : "should never happen";
            }
    }

    /** Get a new input mode, if there is one; otherwise return null. */
    public InputMode getMode() {
        InputMode newMode = modeQueue.poll();
        return newMode;
    }

    /** A {@link PossibleCommand} is an {@link Apply} corresponding to
     * the most likely interpretation of the user's input.  If this
     * {@link Apply} is inapplicable to the given formation, we can
     * use the {@link PossibleCommand#next() next()} method to get the
     * next-most-likely interpretation of the input, and try that
     * before giving up and complaining to the user.
     */
    // XXX: is this really the interface we want?  seems cumbersome.
    public static abstract class PossibleCommand
    implements Iterable<PossibleCommand> {
        /** A distinguished string used to indicate an error condition. */
        public static final String UNCLEAR_UTTERANCE = "<unclear>";
        /** Return the raw user input, or {@link #UNCLEAR_UTTERANCE} if
         * the input mechanism wasn't able to decipher the input. */
        public abstract String getUserInput();
        /** Return the time the user input began, in milliseconds since the
         * epoch. */
        public abstract long getStartTime();
        /** Return the time the user input was complete, in milliseconds since
         * the epoch. */
        public abstract long getEndTime();
        /** Return the next possible command, or null if there are no more. */
        public abstract PossibleCommand next();

        /** Return the parsed command possibility; throwing
         *  {@link BadCallException} if there is a problem with the parse. */
        public final Apply getApply(DanceProgram dp) throws BadCallException {
            if (cache==null) {
                String userInput = getUserInput();
                if (userInput.equals(UNCLEAR_UTTERANCE))
                    throw new BadCallException("I can't hear you");
                cache = CallDB.INSTANCE.parse(dp.getProgram(), userInput);
            }
            return cache;
        }
        private transient Apply cache=null;

        public final Iterator<PossibleCommand> iterator() {
            return new PCIterator(this);
        };
        private static class PCIterator
        extends UnmodifiableIterator<PossibleCommand> {
            private PossibleCommand pc;
            PCIterator(PossibleCommand pc) { this.pc = pc; }
            @Override
            public boolean hasNext() { return pc!=null; }
            @Override
            public PossibleCommand next() {
                PossibleCommand npc=pc; pc=pc.next(); return npc;
            }
        }
    }
    /** Representation of different input modes.  Each mode recognizes a
     *  different set of commands.  At the moment modes correspond to dance
     *  programs, with the exception that the {@code null} dance program
     *  corresponds to "main menu" mode, where the "square up" and "exit"
     *  commands are recognized.
     */
    public abstract static class InputMode {
        public abstract DanceProgram danceProgram();
        public abstract boolean mainMenu();
    }
}
