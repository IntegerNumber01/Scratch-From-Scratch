package backend;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/*
    Supposed to take in a Program object and execute the commands in each Script object based on the event blocks. For example, if a Script has the name "when_flag_clicked", then the commands in that Script would be executed when the user clicks the green flag in the GUI.
*/
public class Interpreter
{
    private HashMap<Sprite, Program> programs;
    private World world;

    public Interpreter(World world) {
        this.world = world;
        this.programs = new HashMap<Sprite, Program>();
    }

    public void addProgram(Sprite sprite, Program program) {
        programs.put(sprite, program);
    }

    public void execute() {

        System.out.println("Executing programs...");
        for (Sprite sprite : programs.keySet()) {
            Program program = programs.get(sprite);

            for (Script script : program.getScripts()) {
                sprite = executeScript(script, sprite);
            }
        }

        System.out.println("Finished executing programs.");
    }

    // nested functions??
    public Sprite executeScript(Script script, Sprite sprite) {
        for (Command command : script.getCommands()) {
            sprite = executeCommand(command, sprite);
        }

        return sprite;
    }

    public Sprite executeFunction(Script function, ArrayList<String> newArgs, Sprite sprite) {
        System.out.println("Executing function " + function.getName() + " with args " + newArgs);

        // check if any function args are the same name as variables. if so, thrown an error since that isn't allowd
        for (String arg : function.getArgs()) {
            if (programs.get(sprite).getVariables().containsKey(arg)) {
                System.out.println("ERROR: Function argument " + arg + " has the same name as a variable. This is not allowed.");
                return sprite;
            }
        }

        // since we mutate the function by changing all the args, first we must copy it, then change it
        // then execute it, then destroy it
        // this preserves the original function.

        Script functionCopy = new Script(function); // deep copy the function

        for (Command c : functionCopy.getCommands()) {
            // replace ALL args through ALL children
            for (Command command : c.fullExpansion())
                // find matching arg inside command & replace with value
                for (int i = 0; i < functionCopy.getArgs().size(); i++) {
                    String scriptArg = functionCopy.getArgs().get(i);

                    for (String commandArg : command.getArgs()) {
                        if (commandArg.contains(scriptArg)) {
                            // find the index of the arg by name using function args
                            // use that index on newArgs to get the value
                            command.replaceArg(functionCopy.getArgs().get(i), newArgs.get(i));
                        }
                    }
                }

            System.out.println("Executing command");
            System.out.println(c.toString());
            sprite = executeCommand(c, sprite);
        }

        return sprite;
    }

    public Sprite executeCommand(Command command, Sprite sprite) {
        // replace all the args through all children
        // make a new command using deep copy and use that to execute.

        Command commandCopy = new Command(command);

        for (Command c : commandCopy.fullExpansion()) {
            for (int i = 0; i < c.getArgs().size(); i++) {
                for (String var : programs.get(sprite).getVariables().keySet()) {
                    if (c.getArgs().get(i).contains(var)) {
                        c.replaceArg(var, programs.get(sprite).getVariableValue(var));
                    }
                }
            }
        }

        if (commandCopy.isBlock()) {
            return executeBlockCommand(commandCopy, sprite);
        } else {
            return exectuteActionCommand(commandCopy, sprite);
        }
    }

    public Sprite executeBlockCommand(Command command, Sprite sprite) {

        switch (command.getName()) {
            case "repeat":
                int times = (int) Double.parseDouble(command.getArgs().get(0)); // num of times to repeat

                for (int i = 0; i < times; i++) {
                    for (Command child : command.getChildren()) {
                        sprite = executeCommand(child, sprite);
                    }
                }

                break;

            case "forever":
                while (world.isRunning()) {
                    for (Command child : command.getChildren()) {
                        sprite = executeCommand(child, sprite);
                    }
                }
                break;
            case "if":
                System.out.println("Eval " + BooleanExpression.evaluate(command.getArgs().get(0)));
                if (BooleanExpression.evaluate(command.getArgs().get(0)).equals("true")) {
                    for (Command child : command.getChildren()) {
                        sprite = executeCommand(child, sprite);
                    }
                }
                break;
            default:
                System.out.println("ERROR: Unrecognized block command " + command.getName());
                break;
        }

        return sprite;
    }

    /*
    Take a single command and execute corresponding Scratch functions from Sprite
    */
    public Sprite exectuteActionCommand(Command command, Sprite sprite) {
        // command is action command
        String name = command.getName();
        ArrayList<String> args = command.getArgs();

        if (command.isPrivate()) {
            // eval only second arg into single string value
            args.set(1, Expression.evaluate(args.get(1)));

            programs.get(sprite).setVariableValue(args.get(0), args.get(1));
            return sprite;
        }

        // evalute all args into a single String value
        for (int i = 0; i < args.size(); i++) {
            args.set(i, Expression.evaluate(args.get(i)));
        }


        if (programs.get(sprite).isFunction(name)) { // the command is a function that is defined
            sprite = executeFunction(programs.get(sprite).getFunctionByName(name), args, sprite);
            return sprite;
        }

        switch (name) {
            case "move":
                sprite.move((int) Double.parseDouble(args.get(0)));
                break;
            case "go_to":
                sprite.goTo((int) Double.parseDouble(args.get(0)), (int) Double.parseDouble(args.get(1)));
                break;
            case "turn_left":
                sprite.turnLeft((int) Double.parseDouble(args.get(0)));
                break;
            case "turn_right":
                sprite.turnRight((int) Double.parseDouble(args.get(0)));
                break;
            case "change_x":
                sprite.changeX((int) Double.parseDouble(args.get(0)));
                break;
            case "change_y":
                sprite.changeY((int) Double.parseDouble(args.get(0)));
                break;
            case "switch_costume":
                sprite.switchCostume(new File(args.get(0)));
                break;
            case "next_costume":
                sprite.nextCostume();
                break;
            case "say":
                sprite.say(args.get(0));
                break;
            case "say_for_time":
                sprite.sayForTime(args.get(0), (int) Double.parseDouble(args.get(1)));
                break;
            case "think":
                sprite.think(args.get(0));
                break;
            case "think_for_time":
                sprite.thinkForTime(args.get(0), (int) Double.parseDouble(args.get(1)));
                break;
            case "set_size":
                sprite.setSize((int) Double.parseDouble(args.get(0)));
                break;
            case "change_size":
                sprite.changeSize((int) Double.parseDouble(args.get(0)));
                break;
            default:
                System.out.println("ERROR: Unrecognized action command " + name);
                break; 
        }

        return sprite;
    }
}
