package backend;

import java.util.ArrayList;

public class ExecutionFrame {
    public static final String SCRIPT = "script";
    public static final String IF_BRANCH = "if_branch";
    public static final String REPEAT = "repeat";
    public static final String REPEAT_UNTIL = "repeat_until";
    public static final String FOREVER = "forever";
    public long waitUntil = 0 ;
    public String clearAfterWait = null;

    public ArrayList<Command> commands;
    public String type;
    public int index;
    public int remainingIterations;
    public String conditionExpression;

    /**
     * Creates a new ExecutionFrame with the given list of commands and type. The index is initialized to 0, remainingIterations is initialized to 0, and conditionExpression is initialized to null.
     * @param commands
     * @param type
     */
    public ExecutionFrame(ArrayList<Command> commands, String type) {
        this.commands = commands;
        this.type = type;
        this.index = 0;
        this.remainingIterations = 0;
        this.conditionExpression = null;
    }
}
