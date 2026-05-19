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

    // this command name is called "assign". If the user has a 
    private boolean isPrivate; // only true for variable assigment commands

    public Command(String name, ArrayList<String> args) {
        this.name = name;
        this.args = args;
        this.isBlock = LanguageConfig.isBlockCommand(name);
        children = new ArrayList<Command>();
        this.isPrivate = false;
    }

    public Command(String name, ArrayList<String> args, boolean isPrivate) {
        this(name, args);
        this.isPrivate = isPrivate;
    }

    // use this constructor when deep copying a command
    public Command(Command other) {
        this.name = other.name;
        this.args = new ArrayList<>(other.args);
        this.isBlock = other.isBlock;
        this.children = new ArrayList<>();
        this.isPrivate = other.isPrivate;

        for (Command child : other.children) {
            this.children.add(new Command(child));
        }
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

    public boolean isPrivate() {
        return isPrivate;
    }

    /*
    Replaces the VALUE of an arg by name with a new value
    Can replace the arg in an expression as well.

    oldArg holds the name of the var
    newArg holds the value to replace with
    */
    public void replaceArg(String oldArg, String newArg) {
        System.out.println("inside replace ARG in command " + name + " with args " + args);
        for (int i = 0; i < args.size(); i++) {
            args.set(i, args.get(i).replace(oldArg, newArg));
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
