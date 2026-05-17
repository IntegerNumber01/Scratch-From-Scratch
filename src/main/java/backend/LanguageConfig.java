package backend;

import java.util.Set;

/*
This class holds the configuration for the language, such as which commands are action commands, block commands, and events. It also has helper functions to check if a command is an action command, block command, or event.
*/
public final class LanguageConfig {
    private LanguageConfig() {
    }

    public static final Set<String> ACTION_COMMANDS = Set.of(
        "move",
        "go_to",
        "turn_right",
        "turn_left",
        "point_in_direction",
        "change_x",
        "change_y"
    );

    public static final Set<String> BLOCK_COMMANDS = Set.of(
        "if",
        "repeat",
        "forever"
    );

    public static final Set<String> EVENTS = Set.of(
        "when_flag_clicked",
        "when_clicked"
    );

    public static boolean isActionCommand(String name) {
        return ACTION_COMMANDS.contains(name);
    }

    public static boolean isBlockCommand(String name) {
        if (name == null) return false;

        name = name.trim().toLowerCase();

        return BLOCK_COMMANDS.contains(name);
    }

    public static boolean isEvent(String name) {
        if (name == null) return false;

        name = name.trim().toLowerCase();

        return EVENTS.contains(name);
    }

    public static boolean isValid(String name) {
        return isActionCommand(name) || isBlockCommand(name) || isEvent(name);
    }
}