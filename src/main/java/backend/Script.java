    package backend;

    import java.util.*;
    /**
        This class holds scripts that start with an event block such as "when_flag_clicked"
    */
    public class Script
    {
        private String name;
        private ArrayList<Command> commands;
        private ArrayList<String> args;
        private int lineNumber; // for error reporting. Line number in the original .scratch file where this script was defined

        /**
         * Constructor for creating a new script.
         * @param name name of the script, which is typically the event that triggers the script such as "when_flag_clicked"
         * @param lineNumber line number in the original .scratch file where this script was defined
         */
        public Script(String name, int lineNumber) {
            this.name = name;
            this.args = null;
            this.lineNumber = lineNumber;
            commands = new ArrayList<Command>();
        }

        /**
         * Constructor for creating a new script that is a deep copy of another script.
         * @param other the script to copy
         */
        public Script(Script other) {
            this.name = other.name;
            this.args = other.args == null ? null : new ArrayList<>(other.args);
            this.lineNumber = other.lineNumber;
            this.commands = new ArrayList<>();

            for (Command command : other.commands) {
                this.commands.add(new Command(command));
            }
        }

        /**
         * Constructor for creating a new script that represents a function with arguments.
         * @param name name of the function
         * @param args list of argument names for the function
         * @param lineNumber line number in the original .scratch file where this script was defined
         */
        public Script(String name, ArrayList<String> args, int lineNumber) {
            this(name, lineNumber);
            this.args = args;
        }

        /**
         * Returns name of script.
         * @return String name of script
         */
        public String getName() {
            return name;
        }

        /**
         * Returns list of commands in the script.
         * @return ArrayList of commands in the script
         */
        public ArrayList<Command> getCommands() {
            return commands;
        }

        /**
         * Returns the list of arguments for the script.
         * @return ArrayList of argument names
         */
        public ArrayList<String> getArgs() {
            return args;
        }

        /**
         * Returns the line number of the script.
         * @return int line number
         */
        public int getLineNumber() {
            return lineNumber;
        }

        /**
         * Adds a command to the end of the script's command list.
         * @param command the command to add
         */
        public void addCommand(Command command) {
            commands.add(command);
        }

        /**
         * Returns true if this script is a function and false otherwise.
         * @return boolean indicating if this script is a function
         */
        public boolean isFunction() {
            return args != null;
        }

        /**
         * Returns a string representation of the script.
         * @return String representing the script
         */
        public String toString() {
            String ans;
            if (args == null)
                ans = "SCRIPT[\n\nname=" + name + "\n\n";
            else
                ans = "SCRIPT[\n\nname=" + name + "\nargs=" + args.toString() + "\n\n";

            for (Command command : commands) {
                ans += command.toString();
            }
            ans += "\n]";

            return ans;
        }
    }
