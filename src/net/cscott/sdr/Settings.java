package net.cscott.sdr;

import java.util.prefs.Preferences;

import net.cscott.sdr.calls.Program;

/** The {@link Settings} class manages the menu system for adjusting the
 *  various game settings, and tracks the current mode of play.
 */
public abstract class Settings {
    private final Preferences p;

    /** Create a new Settings, with default values taken from the user
     *  preferences. */
    protected Settings(String prefName) {
        // read defaults from persistent properties
        this.p = Preferences.userRoot().node(prefName);
    }

    // settings adjustable at the start menu

    /** Which microphone to use. */
    public void setMicrophone(int which) {
        p.putInt("microphone", which);
    }
    public int getMicrophone() {
        return p.getInt("microphone", 0/* "default" */);
    }

    /** What type of music (if any) to play. */
    public enum MusicSetting {
        // XXX mississippi sawyer, etc
        NONE("None"), SATURDAY_NIGHT("Saturday Night");
        MusicSetting(String name) { this.name = name; }
        private final String name;
        @Override
        public String toString() { return name; }
    };
    public void setMusic(MusicSetting m) {
        setPref(p, "music", m);
    }
    public MusicSetting getMusic() {
        return getPref(p, "music", MusicSetting.class,
                       MusicSetting.SATURDAY_NIGHT);
    }

    /** How stringent should scoring be? */
    public enum DifficultySetting {
        EASY("Easy"), MODERATE("Moderate"), HARD("Hard");
        DifficultySetting(String name) { this.name = name; }
        private final String name;
        @Override
        public String toString() { return name; }
    };
    public void setDifficulty(DifficultySetting d) {
        setPref(p, "difficulty", d);
    }
    public DifficultySetting getDifficulty() {
        return getPref(p, "difficulty", DifficultySetting.class,
                       DifficultySetting.EASY);
    }

    /** Background environment for dancers. */
    public enum VenueSetting {
        MOUNTAINS("Mountains");  // XXX Baypath barn, etc.
        VenueSetting(String name) { this.name = name; }
        private final String name;
        @Override
        public String toString() { return name; }
    };
    public void setVenue(VenueSetting v) {
        setPref(p, "venue", v);
    }
    public VenueSetting getVenue() {
        return getPref(p, "venue", VenueSetting.class,
                       VenueSetting.MOUNTAINS);
    }

    /** Style with which to draw dancers. */
    public enum DancerStyleSetting {
        CHECKERS("Checkers"); // XXX realistic people, bears, etc.
        DancerStyleSetting(String name) { this.name = name; }
        private final String name;
        @Override
        public String toString() { return name; }
    }
    public void setDancerStyle(DancerStyleSetting d) {
        setPref(p, "dancerStyle", d);
    }
    public DancerStyleSetting getDancerStyle() {
        return getPref(p, "dancerStyle", DancerStyleSetting.class,
                       DancerStyleSetting.CHECKERS);
    }

    /** What calls should be accepted for the dancers, and what their starting
     *  formation should be. */
    public enum DanceLevelSetting {
        BASIC_4("2-couple Basic", Program.BASIC, 4),
        MAINSTREAM_4("2-couple Mainstream", Program.MAINSTREAM, 4),
        PLUS_4("2-couple Plus", Program.PLUS, 4),
        BASIC_8("Basic", Program.BASIC, 8),
        MAINSTREAM_8("Mainstream", Program.MAINSTREAM, 8),
        PLUS_8("Plus", Program.PLUS, 8),
        A2_8("Advanced", Program.A2, 8),
        C4_8("Challenge", Program.C4, 8),
        PLUS12("Hex Plus", Program.PLUS, 12);
        public final String name;
        public final Program program;
        public final int numDancers;
        DanceLevelSetting(String name, Program program, int numDancers) {
            this.name = name;
            this.program = program;
            this.numDancers = numDancers;
        }
        @Override
        public String toString() { return name; }
    }
    public void setDanceLevel(DanceLevelSetting dl) {
        setPref(p, "danceLevel", dl);
    }
    public DanceLevelSetting getDanceLevel() {
        return getPref(p, "danceLevel", DanceLevelSetting.class,
                       DanceLevelSetting.MAINSTREAM_8);
    }

    // helper methods to get/set enumeration preferences.
    private static <T extends Enum<T>> T getPref(Preferences p, String key,
                                         Class<T> enumType, T defaultValue) {
        T result = defaultValue;
        String v = p.get(key, null);
        if (v!=null)
            try {
                result = Enum.valueOf(enumType, v);
            } catch (IllegalArgumentException e) {
                // ignore the bad pref.
            }
        return result;
    }
    private static <T extends Enum<T>> void setPref(Preferences p, String key, T value) {
        p.put(key, value.name());
    }
}
