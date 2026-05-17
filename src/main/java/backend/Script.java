    package backend;

    import java.util.*;
    /*
        This class holds scripts that start with an event block such as "when_flag_clicked"
    */
    public class Script
    {
        private String name;
        private ArrayList<Command> commands;
        private ArrayList<String> args;

        public Script(String name) {
            this.name = name;
            this.args = null;
            commands = new ArrayList<Command>();
        }

        // use this contructor when deep copying a script
        public Script(Script other) {
            this.name = other.name;
            this.args = other.args == null ? null : new ArrayList<>(other.args);
            this.commands = new ArrayList<>();

            for (Command command : other.commands) {
                this.commands.add(new Command(command));
            }
        }

        // only for functions because functions can have arguments
        public Script(String name, ArrayList<String> args) {
            this(name);
            this.args = args;
        }

        public String getName() {
            return name;
        }

        public ArrayList<Command> getCommands() {
            return commands;
        }

        public ArrayList<String> getArgs() {
            return args;
        }

        public void addCommand(Command command) {
            commands.add(command);
        }

        public boolean isFunction() {
            return args != null;
        }

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
