package backend;


import java.util.*;

/*
    This class represents a command in the Scratch language. It has a name, a list of arguments, and a list of child commands if it is a block command. For example, an "if" command would have child commands that represent the commands inside the "if" block.
*/
public class Command
{
    private String name;
    private ArrayList<String> args;
    private ArrayList<Command> children;
    private boolean isBlock;

    public Command(String name, ArrayList<String> args, boolean isBlock) {
        this.name = name;
        this.args = args;
        this.isBlock = isBlock;
        children = new ArrayList<Command>();
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getArgs() {
        return args;
    }

    public boolean isBlock() {
        return isBlock;
    }

    public boolean hasChildren() {
        return !children.isEmpty();
    }

    /*
    Returns true if addChild worked. False otherwise.
    Only works if the command is deemed as a block type
    */
    public boolean addChild(Command child) {
        if (isBlock) {
            children.add(child);
            return true;
        }

        return false;
    }

    public String toString() {
        String ans = "COMMAND[" + name + ", " + args.toString() +"]\n";

        for (Command child : children) {
            ans += "    " + child.toString();
        }

        return ans;
    }
}
