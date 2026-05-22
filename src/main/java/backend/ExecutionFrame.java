package backend;

import java.util.ArrayList;

public class ExecutionFrame {
    public static final String SCRIPT = "script";
    public static final String IF_BRANCH = "if_branch";
    public static final String REPEAT = "repeat";
    public static final String REPEAT_UNTIL = "repeat_until";
    public static final String FOREVER = "forever";

    public ArrayList<Command> commands;
    public String type;
    public int index;
    public int remainingIterations;
    public String conditionExpression;

    public ExecutionFrame(ArrayList<Command> commands, String type) {
        this.commands = commands;
        this.type = type;
        this.index = 0;
        this.remainingIterations = 0;
        this.conditionExpression = null;
    }
}
