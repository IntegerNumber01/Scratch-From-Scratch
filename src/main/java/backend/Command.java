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

    // only applies to if blocks since they can have else.
    private ArrayList<Command> elseChildren;
    private boolean hasElse;

    private int lineNumber; // for error reporting. Line number in the original .scratch file where this command was defined

    // this command name is called "assign". If the user has a
    private boolean isPrivate; // only true for variable assigment commands

    /**
     * Creates a new Command with the given name, arguments, and line number. The isPrivate field is set to false by default because it refers to variable assignment.
     * @param name
     * @param args
     * @param lineNumber
     */
    public Command(String name, ArrayList<String> args, int lineNumber) {
        this.name = name;
        this.args = args;
        this.isBlock = LanguageConfig.isBlockCommand(name);
        this.lineNumber = lineNumber;
        this.children = new ArrayList<Command>();
        this.isPrivate = false;
        this.elseChildren = new ArrayList<Command>();
        this.hasElse = false;
    }

    /**
     * Creates a new Command with the given name, arguments, line number, and isPrivate field. This constructor should only be used for variable assignment commands, which are the only commands that should have isPrivate set to true.
     * @param name
     * @param args
     * @param lineNumber
     * @param isPrivate
     */
    public Command(String name, ArrayList<String> args, int lineNumber, boolean isPrivate) {
        this(name, args, lineNumber);
        this.isPrivate = isPrivate;
    }

    /**
     * Creates a new Command that is a deep copy of the given Command.
     * @param other
     */
    public Command(Command other) {
        this.name = other.name;
        this.args = new ArrayList<>(other.args);
        this.isBlock = other.isBlock;
        this.children = new ArrayList<>();
        this.isPrivate = other.isPrivate;
        this.lineNumber = other.lineNumber;
        this.hasElse = other.hasElse;

        // copy over else children
        this.elseChildren = new ArrayList<>();
        for (Command child : other.elseChildren) {
            this.elseChildren.add(new Command(child));
        }

        // copy normal children
        for (Command child : other.children) {
            this.children.add(new Command(child));
        }
    }

    /**
     * Returns name of command
     * @return String name of command
     */
    public String getName() {
        return name;
    }

    /**
     * Returns list of arguments for this command.
     * @return ArrayList<String> arguments for this command
     */
    public ArrayList<String> getArgs() {
        return args;
    }

    /**
     * Returns true if this command is a block command.
     * @return boolean true if this command is a block command
     */
    public boolean isBlock() {
        return isBlock;
    }

    /**
     * Returns true if this command has child commands.
     * @return boolean true if this command has child commands
     */
    public boolean hasChildren() {
        return !children.isEmpty();
    }

    /**
     * Returns true if this command has else child commands.
     * @return boolean true if this command has else child commands
     */
    public boolean hasElseChildren() {
        return !elseChildren.isEmpty();
    }

    /**
     * Returns true if this command has an else block.
     * @return boolean true if this command has an else block
     */
    public boolean hasElse() {
        return hasElse;
    }

    /**
     * Returns list of child commands for this command. If this command is not a block command, this method will return an empty list.
     * @return ArrayList<Command> child commands for this command
     */
    public ArrayList<Command> getElseChildren() {
        return elseChildren;
    }

    /**
     * Returns list of child commands for this command. If this command is not a block command, this method will return an empty list.
     * @return ArrayList<Command> child commands for this command
     */
    public ArrayList<Command> getChildren() {
        return children;
    }

    /**
     * Returns the line number in the original .scratch file where this command was defined. This is used for error reporting.
     * @return int line number of this command
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * Returns true if this command is private which means this command is variable assignment.
     * @return boolean true if this command is private which means this command is variable assignment
     */
    public boolean isPrivate() {
        return isPrivate;
    }


    /**
     * Replaces the VALUE of an arg by name with a new value. Can replace the arg in an expression as well.
     * This is used for variable assignment, where we want to replace all instances of a variable with its new value.
     * @param oldArg
     * @param newArg
     */
    public void replaceArg(String oldArg, String newArg) {
        for (int i = 0; i < args.size(); i++) {
            args.set(i, args.get(i).replace(oldArg, newArg));
        }

        for (Command child : children) {
            child.replaceArg(oldArg, newArg);
        }
    }

    /**
     * Sets the argument at the given index to the new value. This is used for function argument replacement, where we want to replace the argument with the value passed in by the user.
     * @param index
     * @param newValue
     */
    public void setArg(int index, String newValue) {
        args.set(index, newValue);
    }

    /**
     * Returns a list of all commands in this command's subtree, including itself.
     * It is used for function argument replacement.
     * @return ArrayList<Command> list of all commands in the subtree
     */
    public ArrayList<Command> fullExpansion() {
        ArrayList<Command> ans = new ArrayList<>();
        ans.add(this);

        for (Command child : children) {
            ans.addAll(child.fullExpansion());
        }

        return ans;
    }

    /**
     * Adds a child command to this command. This is only allowed if this command is a block command, which is determined by the LanguageConfig.
     * If this command is not a block command, this method will print an error message and return false.
     * @param child
     * @return boolean true if addChild worked. False otherwise. Only works if the command is deemed as a block type
     */
    public boolean addChild(Command child) {
        if (isBlock) {
            children.add(child);
            return true;
        }

        System.out.println("ERROR: Attempted to add child command to non-block command " + name);
        return false;
    }

   /**
    * Adds an else child command to this command. This is only allowed if this command is a block command and is an "if" command, which is determined by the LanguageConfig.
    * If this command is not an "if" block command, this method will print an error message and return false.
    * @param child
    * @return boolean true if addElseChild worked. False otherwise. Only works if the command is deemed as an "if" block type
    */
    public boolean addElseChild(Command child) {
        hasElse = true;

        if (isBlock) {
            if (elseChildren == null) {
                elseChildren = new ArrayList<Command>();
            }
            elseChildren.add(child);
            return true;
        }

        System.out.println("ERROR: Attempted to add child command to non-block command " + name);
        return false;
    }


    /**
     * Returns a string representation of this command and its children. This is used for debugging and testing. The string representation is in the format:
     * @return String string representation of this command and its children
     */
    public String toString() {
        return toStringHelper(0);
    }

    /**
     * Helper method for toString that takes in the current depth of the command in the command tree. This is used for indentation in the string representation.
     * @param depth
     * @return String string representation of this command and its children with indentation based on depth
     */
    private String toStringHelper(int depth) {
        String indent = "    ".repeat(depth);

        String ans = indent + "COMMAND[" + name + ", " + args + "]\n";

        for (Command child : children) {
            ans += child.toStringHelper(depth + 1);
        }

        for (Command child : elseChildren) {
            ans += indent + "ELSE\n";
            ans += child.toStringHelper(depth + 1);
        }

        return ans;
    }
}
