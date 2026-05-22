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
        "change_y",
        "go_to_random_position",
        "set_x",
        "set_y",
        "switch_costume",
        "next_costume",
        "say",
        "say_for_time",
        "think",
        "think_for_time",
        "set_size",
        "change_size"
    );

    public static final Set<String> BLOCK_COMMANDS = Set.of(
        "if",
        "repeat",
        "repeat_until",
        "forever"
    );

    public static final Set<String> EVENTS = Set.of(
        "when_flag_clicked",
        "when_clicked"
    );

    public static final Set<Character> MATH_OPERATORS = Set.of(
        '*',
        '/',
        '+',
        '-'
    );

    public static final Set<String> COMPARISON_OPERATORS = Set.of(
        ">",
        "<",
        "=="
    );

    public static final Set<String> BOOL_OPERATORS = Set.of(
        // and has higher precedence
        "and",
        "or",
        "not"
    );

    public static final Set<String> FUNCTION_OPERATORS = Set.of(
        "key_pressed",

        "pick_random",
        "join",
        "letter_of",
        "length_of",
        "round",
        "mod",
        // math time
        "abs",
        "floor",
        "ceiling",
        "sqrt",
        "sin",
        "cos",
        "tan",
        "asin",
        "acos",
        "atan",
        "ln",
        "log",
        "e^",
        "10^"
    );

    public static boolean isMathOperator(char c) {
        return MATH_OPERATORS.contains(c);
    }

    public static boolean isBoolOperator(String c) {
        return BOOL_OPERATORS.contains(c);
    }

    public static boolean isComparisonOperator(String c) {
        return COMPARISON_OPERATORS.contains(c);
    }

    public static boolean isComparisonExpression(String expression) {
        for (String op : COMPARISON_OPERATORS) {
            if (expression.contains(op)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isBooleanExpression(String expression) {
        for (String op : BOOL_OPERATORS) {
            if (expression.contains(op)) {
                return true;
            }
        }

        return false;
    }

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