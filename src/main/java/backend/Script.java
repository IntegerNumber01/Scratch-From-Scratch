package backend;

import java.util.*;
/*
    This class holds scripts that start with an event block such as "when_flag_clicked"
*/
public class Script
{
    private String name;
    private ArrayList<String> args;
    private ArrayList<Command> commands;

    public Script(String name) {
        this.name = name;
        args = new ArrayList<String>();
        commands = new ArrayList<Command>();
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getArgs() {
        return args;
    }

    public ArrayList<Command> getCommands() {
        return commands;
    }

    public void addArg(String arg) {
        args.add(arg);
    }

    public void addCommand(Command command) {
        commands.add(command);
    }

    public String toString() {
        String ans = "SCRIPT[" + name + ", " + args + "]\n";

        for (Command command : commands) {
            ans += command.toString();  // REMOVE extra indentation here
        }

        return ans;
}
}
