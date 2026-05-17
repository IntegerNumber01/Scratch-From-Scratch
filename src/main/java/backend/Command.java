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

    public Command(String name, ArrayList<String> args) {
        this.name = name;
        this.args = args;
        this.isBlock = LanguageConfig.isBlockCommand(name);
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

    public ArrayList<Command> getChildren() {
        return children;
    }

    /*
    Replaces the VALUE of an arg by name with a new value
    */
    public void replaceArg(String oldArg, String newArg) {
        for (int i = 0; i < args.size(); i++) {
            if (args.get(i).equals(oldArg)) {
                args.set(i, newArg);
            }
        }

        for (Command child : children) {
            child.replaceArg(oldArg, newArg);
        }
    }

    /*
    Returns a list of all commands in this command's subtree, including itself.
    Used for function argument replacement
    */
    public ArrayList<Command> fullExpansion() {
        ArrayList<Command> ans = new ArrayList<>();
        ans.add(this);

        for (Command child : children) {
            ans.addAll(child.fullExpansion());
        }

        return ans;
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

        System.out.println("ERROR: Attempted to add child command to non-block command " + name);
        return false;
    }

    public String toString() {
        return toStringHelper(0);
    }

    private String toStringHelper(int depth) {
        String indent = "    ".repeat(depth);

        String ans = indent + "COMMAND[" + name + ", " + args + "]\n";

        for (Command child : children) {
            ans += child.toStringHelper(depth + 1);
        }

        return ans;
    }
}
