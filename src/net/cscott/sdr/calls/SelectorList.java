package net.cscott.sdr.calls;

/** The selector list creates selectors for various formations. */
public abstract class SelectorList {
    public static final Selector NONE = new Selector() {
        public FormationMatch match(Formation f) throws NoMatchException {
            throw new NoMatchException();
        }
    };
}
