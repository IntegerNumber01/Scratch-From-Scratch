package backend;

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

    // public boolean evaluateBoolExpression(Expression left, String operator, Expression right) {

    // }

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
        if (command.isBlock()) {
            return executeBlockCommand(command, sprite);
        } else {
            return exectuteActionCommand(command, sprite);
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
            default:
                System.out.println("ERROR: Unrecognized action command " + name);
                break;
        }

        return sprite;
    }
}
