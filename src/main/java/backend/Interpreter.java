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
    private static World world;

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
        // check if any function args are the same name as variables. if so, thrown an error since that isn't allowd
        for (String arg : function.getArgs()) {
            if (programs.get(sprite).getVariables().containsKey(arg)) {
                // System.out.println("ERROR: Function argument " + arg + " has the same name as a variable. This is not allowed.");
                ScratchError.throwError(function.getLineNumber(), "Function argument " + arg + " has the same name as a variable.");
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

    private static boolean isIdentifierChar(char ch) {
        return Character.isLetterOrDigit(ch) || ch == '_';
    }

    /*
        Replaces all instances of target in expression with replacement, but only when target is a standalone identifier (not part of another word). For example, if target is "x", then "x + 1" would become "replacement + 1", but "max + 1" would remain unchanged.
    */
    private String replaceIdentifier(String expression, String target, String replacement) {
        if (target == null || target.isEmpty()) {
            return expression;
        }

        StringBuilder result = new StringBuilder();
        int i = 0;

        while (i < expression.length()) {
            boolean matchesTarget = expression.startsWith(target, i);
            boolean leftBounded = i == 0 || !isIdentifierChar(expression.charAt(i - 1));
            int end = i + target.length();
            boolean rightBounded = end >= expression.length() || !isIdentifierChar(expression.charAt(end));

            if (matchesTarget && leftBounded && rightBounded) {
                result.append(replacement);
                i = end;
            } else {
                result.append(expression.charAt(i));
                i++;
            }
        }

        return result.toString();
    }

    private String resolveVariables(String expression, Sprite sprite) {
        String resolved = expression;
        ArrayList<String> variables = new ArrayList<>(programs.get(sprite).getVariables().keySet());
        variables.sort((a, b) -> Integer.compare(b.length(), a.length()));

        for (String variable : variables) {
            resolved = replaceIdentifier(resolved, variable, programs.get(sprite).getVariableValue(variable));
        }

        return resolved;
    }

    private String resolveExpressionArg(String expression, Sprite sprite) {
        recordSpriteVariables(sprite);
        return OperatorFunctionExpression.evaluate(resolveVariables(expression, sprite));
    }

    private void resolveCommandArgs(Command command, Sprite sprite) {
        for (int i = 0; i < command.getArgs().size(); i++) {
            if (command.isPrivate() && i == 0) { // variable assignment & LHS
                continue;
            }

            command.setArg(i, resolveExpressionArg(command.getArgs().get(i), sprite));
        }
    }

    private void recordSpriteVariables(Sprite sprite) {
        // handle all the variables Scratch provides that actively monitor sprite state
        programs.get(sprite).setVariableValue("x_position", sprite.getX() + "");
        programs.get(sprite).setVariableValue("y_position", sprite.getY() + "");
        programs.get(sprite).setVariableValue("direction", sprite.getDir() + "");

        programs.get(sprite).setVariableValue("size", sprite.getSize() + "");
    }

    public Sprite executeCommand(Command command, Sprite sprite) {
        // replace all the args through all children
        // make a new command using deep copy and use that to execute.
        Command commandCopy = new Command(command);

        if (commandCopy.isBlock()) {
            return executeBlockCommand(commandCopy, sprite);
        } else {
            resolveCommandArgs(commandCopy, sprite);
            return exectuteActionCommand(commandCopy, sprite);
        }
    }

    public Sprite executeBlockCommand(Command command, Sprite sprite) {

        switch (command.getName()) {
            case "repeat":
                int times = (int) Double.parseDouble(Expression.evaluate(resolveExpressionArg(command.getArgs().get(0), sprite))); // num of times to repeat

                for (int i = 0; i < times; i++) {
                    for (Command child : command.getChildren()) {
                        sprite = executeCommand(child, sprite);
                    }
                }

                break;

            case "repeat_until":
                while (BooleanExpression.evaluate(resolveExpressionArg(command.getArgs().get(0), sprite)).equals("false")) {
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
                String condition = resolveExpressionArg(command.getArgs().get(0), sprite);
                System.out.println("Eval " + BooleanExpression.evaluate(condition));
                if (BooleanExpression.evaluate(condition).equals("true")) {
                    for (Command child : command.getChildren()) {
                        sprite = executeCommand(child, sprite);
                    }
                } else if (command.hasElse()) { // execute the else block
                    for (Command child : command.getElseChildren()) {
                        sprite = executeCommand(child, sprite);
                    }
                }
                break;
            default:
                ScratchError.throwError(command.getLineNumber(), "Unrecognized block command " + "'" + command.getName() + "'");
                break;
        }

        return sprite;
    }


    /*
        evalute all args into a single String value in a ne list
    */
    private ArrayList<String> evalArgs(ArrayList<String> args) {
        ArrayList<String> ans = new ArrayList<>();

        for (int i = 0; i < args.size(); i++) {
            ans.add(Expression.evaluate(args.get(i)));
        }

        return ans;
    }

    /*
    Take a single command and execute corresponding Scratch functions from Sprite
    */
    public Sprite exectuteActionCommand(Command command, Sprite sprite) {
        // command is action command
        String name = command.getName();
        ArrayList<String> args = command.getArgs();

        if (command.isPrivate()) { // variable assignment
            // eval only second arg into single string value
            args.set(1, Expression.evaluate(args.get(1)));

            programs.get(sprite).setVariableValue(args.get(0), args.get(1));
            return sprite;
        }

        // evalute all args into a single String value
        args = evalArgs(args);

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
            case "set_x":
                sprite.setX((int) Double.parseDouble(args.get(0)));
                break;
            case "set_y":
                sprite.setY((int) Double.parseDouble(args.get(0)));
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
                ScratchError.throwError(command.getLineNumber(), "Unrecognized command " + "'" + name + "'");
                break;
        }

        return sprite;
    }

    public static String getMouseDownValue() {
        return world.isMouseDown() ? "true" : "false";
    }
}
