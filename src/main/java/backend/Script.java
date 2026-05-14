package backend;

import java.util.*;
/*
    This class holds scripts that start with an event block such as "when_flag_clicked"
*/
public class Script
{
    private String name;
    private ArrayList<Command> commands;

    public Script(String name) {
        this.name = name;
        commands = new ArrayList<Command>();
    }

    public String getName() {
        return name;
    }

    public ArrayList<Command> getCommands() {
        return commands;
    }

    public void addCommand(Command command) {
        commands.add(command);
    }

    public String toString() {
        String ans = "SCRIPT[\n\n" + name + "\n\n";

        for (Command command : commands) {
            ans += command.toString();
        }
        ans += "\n]";

        return ans;
}
}
