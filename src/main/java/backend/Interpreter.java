package backend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Runs a sprite's program by stepping through its scripts and commands.
 * It also manages clone startup, variable resolution, and block execution.
 */
public class Interpreter
{
    private static final String DEFAULT_START_EVENT = "when_flag_clicked";
    private static final String CLONE_START_EVENT = "when_i_start_as_clone";

    private HashMap<Sprite, Program> programs;
    private static HashMap<Integer, Program> programsByInstanceId = new HashMap<>();
    private static List<Interpreter> interpreterRegistry = new ArrayList<>();
    private static World world;

    private Sprite currentSprite;
    private Program currentProgram;
    private int currentScriptIndex;
    private boolean initialized;
    private boolean finished;
    private ArrayList<ExecutionFrame> executionStack;
    private Sprite assignedSprite;
    private Program assignedProgram;
    private String startEventName;

    /**
     * Creates an interpreter for the given world.
     *
     * @param world world that stores sprites, inputs, and globals
     */
    public Interpreter(World world) {
        this.world = world;
        this.programs = new HashMap<Sprite, Program>();
        this.executionStack = new ArrayList<ExecutionFrame>();
        this.startEventName = DEFAULT_START_EVENT;
    }

    /**
     * Assigns a sprite and program to this interpreter.
     *
     * @param sprite sprite to run
     * @param program parsed program for the sprite
     */
    public void addProgram(Sprite sprite, Program program) {
        validateProgramVariables(program);
        programs.put(sprite, program);
        programsByInstanceId.put(sprite.getInstanceId(), program);
        assignedSprite = sprite;   // store directly
        assignedProgram = program;
    }


    /**
     * Checks that program variables and function arguments do not use invalid names.
     *
     * @param program program to validate
     */
    private void validateProgramVariables(Program program) {
        HashSet<String> localVariables = new HashSet<>();

        for (Script script : program.getScripts()) {
            validateVariableNamesInCommands(script.getCommands(), localVariables);
        }

        for (Script function : program.getFunctions()) {
            validateVariableNamesInCommands(function.getCommands(), localVariables);
        }

        for (Script function : program.getFunctions()) {
            for (String arg : function.getArgs()) {
                if (LanguageConfig.isReservedVariableName(arg)) {
                    ScratchError.throwError(function.getLineNumber(), "Function argument " + arg + " uses a reserved Scratch variable name.");
                }

                if (LanguageConfig.isReservedWord(arg)) {
                    ScratchError.throwError(function.getLineNumber(), "Function argument " + arg + " uses a reserved language word.");
                }

                if (world.hasGlobalVariable(arg)) {
                    ScratchError.throwError(function.getLineNumber(), "Function argument " + arg + " has the same name as a global variable.");
                }

                if (localVariables.contains(arg)) {
                    ScratchError.throwError(function.getLineNumber(), "Function argument " + arg + " has the same name as a variable.");
                }
            }
        }
    }

    /**
     * Recursively checks variable assignments inside a list of commands.
     *
     * @param commands commands to scan
     * @param localVariables set of discovered local variable names
     */
    private void validateVariableNamesInCommands(ArrayList<Command> commands, HashSet<String> localVariables) {
        for (Command command : commands) {
            if (command.isPrivate()) {
                String varName = command.getArgs().get(0);

                if (LanguageConfig.isReservedVariableName(varName)) {
                    ScratchError.throwError(command.getLineNumber(), "Variable " + varName + " uses a reserved Scratch variable name.");
                }

                if (LanguageConfig.isReservedWord(varName)) {
                    ScratchError.throwError(command.getLineNumber(), "Variable " + varName + " uses a reserved language word.");
                }

                if (!world.hasGlobalVariable(varName)) {
                    localVariables.add(varName);
                }
            }

            validateVariableNamesInCommands(command.getChildren(), localVariables);
            validateVariableNamesInCommands(command.getElseChildren(), localVariables);
        }
    }

    /**
     * Runs this interpreter until no more work remains.
     */
    public void execute() {
        System.out.println("Executing programs...");
        while (tick()) {
            // Keep advancing until this interpreter runs out of work.
        }
        System.out.println("Finished executing programs.");
    }

    /**
     * Sets the event name this interpreter should start from.
     *
     * @param eventName event block name
     */
    public void setStartEvent(String eventName) {
        startEventName = normalizeScriptName(eventName);
    }

    /**
     * Stores the shared list of active interpreters.
     *
     * @param interpreters active interpreters in the world
     */
    public static void setInterpreterRegistry(List<Interpreter> interpreters) {
        interpreterRegistry = interpreters;
    }

    /**
     * Advances execution by a small amount.
     *
     * @return true if there is still work to do, false otherwise
     */
    public boolean tick() {
        initializeExecution();

        if (finished || currentSprite == null || currentProgram == null) {
            return false;
        }

        boolean executedLeafCommand = false;
        int steps = 0;

        while (steps++ < 100) { // just a random big number to prevent infinite loops
            if (executionStack.isEmpty()) {
                if (!pushNextScript()) {
                    finished = true;
                    return false;
                }
            }

            ExecutionFrame frame = peekFrame();

            if(frame.waitUntil > 0)
            {
                if(System.nanoTime() < frame.waitUntil)
                {
                    return true ;
                }

                else
                {
                    frame.waitUntil = 0 ;
                    if("say".equals(frame.clearAfterWait))
                    {
                        currentSprite.say("") ;
                    }
                    else if("think".equals(frame.clearAfterWait))
                    {
                        currentSprite.think("") ;
                    }
                    frame.clearAfterWait = null ;
                }
            }

            if (frame.index >= frame.commands.size()) {
                if (completeFrame(frame, executedLeafCommand)) {
                    return true;
                }
                continue;
            }

            Command command = frame.commands.get(frame.index++);

            // keep walking block structure until we hit a leaf command or need to yield
            if (command.isBlock()) {
                enterBlock(command);
                continue;
            }

            executeLeafCommand(command, currentSprite);
            executedLeafCommand = true;
            return true;
        }

        return true;
    }

    /**
     * Executes every command in a script immediately.
     *
     * @param script script to execute
     * @param sprite sprite the script is running on
     */
    public void executeScript(Script script, Sprite sprite) {
        for (Command command : script.getCommands()) {
            executeCommand(command, sprite);
        }
    }

    /**
     * Executes a user-defined function with the given arguments.
     *
     * @param function function definition
     * @param newArgs argument values
     * @param sprite sprite running the function
     * @return updated sprite
     */
    public Sprite executeFunction(Script function, ArrayList<String> newArgs, Sprite sprite) {
        // check if any function args are the same name as variables. if so, thrown an error since that isn't allowd
        for (String arg : function.getArgs()) {
            if (programs.get(sprite).getVariables().containsKey(arg)) {
                // System.out.println("ERROR: Function argument " + arg + " has the same name as a variable. This is not allowed.");
                ScratchError.throwError(function.getLineNumber(), "Function argument " + arg + " has the same name as a variable.");
                return sprite;
            }
        }

        if (function.getArgs().size() != newArgs.size()) {
            ScratchError.throwError(
                function.getLineNumber(),
                "Function " + function.getName() + " expected " + function.getArgs().size()
                    + " argument(s) but got " + newArgs.size() + "."
            );
        }

        // since we mutate the function by changing all the args, first we must copy it, then change it
        // then execute it, then destroy it
        // this preserves the original function.

        Script functionCopy = new Script(function); // deep copy the function

        for (Command c : functionCopy.getCommands()) {
            substituteFunctionArgs(c, functionCopy.getArgs(), newArgs);
            sprite = executeCommand(c, sprite);
        }

        return sprite;
    }

    /**
     * Checks whether a character can appear in an identifier.
     *
     * @param ch character to test
     * @return true if the character is part of an identifier
     */
    private static boolean isIdentifierChar(char ch) {
        return Character.isLetterOrDigit(ch) || ch == '_';
    }

    /**
     * Replaces an identifier in an expression without touching larger names.
     *
     * @param expression expression to update
     * @param target identifier to replace
     * @param replacement replacement text
     * @return updated expression
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

    /**
     * Replaces function argument names throughout a command tree.
     *
     * @param command root command to update
     * @param functionArgs original function argument names
     * @param newArgs argument values to substitute
     */
    private void substituteFunctionArgs(Command command, ArrayList<String> functionArgs, ArrayList<String> newArgs) {
        ArrayList<String> commandArgs = command.getArgs();

        for (int argIndex = 0; argIndex < commandArgs.size(); argIndex++) {
            String resolvedArg = commandArgs.get(argIndex);

            for (int functionArgIndex = 0; functionArgIndex < functionArgs.size(); functionArgIndex++) {
                resolvedArg = replaceIdentifier(
                    resolvedArg,
                    functionArgs.get(functionArgIndex),
                    newArgs.get(functionArgIndex)
                );
            }

            command.setArg(argIndex, resolvedArg);
        }

        for (Command child : command.getChildren()) {
            substituteFunctionArgs(child, functionArgs, newArgs);
        }

        for (Command elseChild : command.getElseChildren()) {
            substituteFunctionArgs(elseChild, functionArgs, newArgs);
        }
    }

    /**
     * Replaces known variable names in an expression with their current values.
     *
     * @param expression expression to resolve
     * @param sprite sprite whose variables are used
     * @return resolved expression
     */
    private String resolveVariables(String expression, Sprite sprite) {
        String resolved = expression;
        ArrayList<String> variables = new ArrayList<>(programs.get(sprite).getVariables().keySet());
        variables.sort((a, b) -> Integer.compare(b.length(), a.length()));

        for (String variable : variables) {
            resolved = replaceIdentifier(resolved, variable, programs.get(sprite).getVariableValue(variable));
        }

        return resolved;
    }

    /**
     * Finds the matching closing parenthesis for an opening parenthesis.
     *
     * @param expression expression to search
     * @param openParenIndex index of the opening parenthesis
     * @return index of the matching closing parenthesis, or -1 if not found
     */
    private int findMatchingCloseParen(String expression, int openParenIndex) {
        int depth = 0;

        for (int i = openParenIndex; i < expression.length(); i++) {
            if (expression.charAt(i) == '(') {
                depth++;
            } else if (expression.charAt(i) == ')') {
                depth--;

                if (depth == 0) {
                    return i;
                }
            }
        }

        return -1;
    }

    /**
     * Temporarily hides attribute_of_sprite arguments during variable replacement.
     *
     * @param expression expression to protect
     * @param protectedArgs map storing placeholder tokens and original values
     * @return protected expression
     */
    private String protectAttributeOfSpriteArgs(String expression, HashMap<String, String> protectedArgs) {
        String functionName = "attribute_of_sprite";
        int searchIndex = 0;

        while (true) {
            int startIndex = expression.indexOf(functionName + "(", searchIndex);

            if (startIndex == -1) {
                break;
            }

            int openParenIndex = startIndex + functionName.length();
            int closeParenIndex = findMatchingCloseParen(expression, openParenIndex);

            if (closeParenIndex == -1) {
                break;
            }

            String functionString = expression.substring(startIndex, closeParenIndex + 1);
            Command command = Parser.parseCommand(functionString, -1);

            if (command != null && command.getArgs().size() == 2) {
                ArrayList<String> args = command.getArgs();

                for (int i = 0; i < args.size(); i++) {
                    String token = "__ATTRIBUTE_OF_SPRITE_ARG_" + protectedArgs.size() + "__";
                    protectedArgs.put(token, args.get(i));
                    args.set(i, token);
                }

                String rebuiltFunction = functionName + "(" + args.get(0) + ", " + args.get(1) + ")";
                expression = expression.substring(0, startIndex) + rebuiltFunction + expression.substring(closeParenIndex + 1);
                searchIndex = startIndex + rebuiltFunction.length();
            } else {
                searchIndex = closeParenIndex + 1;
            }
        }

        return expression;
    }

    /**
     * Restores temporarily hidden attribute_of_sprite arguments.
     *
     * @param expression expression with placeholders
     * @param protectedArgs map of placeholder tokens to original values
     * @return restored expression
     */
    private String restoreProtectedArgs(String expression, HashMap<String, String> protectedArgs) {
        for (Map.Entry<String, String> entry : protectedArgs.entrySet()) {
            expression = expression.replace(entry.getKey(), entry.getValue());
        }

        return expression;
    }

    /**
     * Resolves variables and operator functions inside one expression argument.
     *
     * @param expression raw expression
     * @param sprite current sprite
     * @return resolved value as a string
     */
    private String resolveExpressionArg(String expression, Sprite sprite) {
        recordSpriteVariables(sprite);
        HashMap<String, String> protectedArgs = new HashMap<>();
        String protectedExpression = protectAttributeOfSpriteArgs(expression, protectedArgs);
        String resolvedExpression = resolveVariables(protectedExpression, sprite);
        resolvedExpression = restoreProtectedArgs(resolvedExpression, protectedArgs);
        return OperatorFunctionExpression.evaluate(resolvedExpression, world, sprite);
    }

    /**
     * Resolves all arguments for a command before execution.
     *
     * @param command command to update
     * @param sprite current sprite
     */
    private void resolveCommandArgs(Command command, Sprite sprite) {

        if(command.getName().equals("show_variable")||command.getName().equals("hide_variable"))
        {
            return ;
        }

        for (int i = 0; i < command.getArgs().size(); i++) {
            if (command.isPrivate() && i == 0) { // variable assignment & LHS
                continue;
            }

            command.setArg(i, resolveExpressionArg(command.getArgs().get(i), sprite));
        }
    }

    /**
     * Records built-in sprite values for the current sprite.
     *
     * @param sprite sprite whose values should be recorded
     */
    private void recordSpriteVariables(Sprite sprite) {
        recordSpriteVariables(sprite, programs.get(sprite));
    }

    /**
     * Writes global variables and built-in sprite values into a program.
     *
     * @param sprite sprite whose state is being recorded
     * @param program program receiving the values
     */
    private static void recordSpriteVariables(Sprite sprite, Program program) {
        for (Map.Entry<String, String> entry : world.getGlobalVariables().entrySet()) {
            program.setVariableValue(entry.getKey(), entry.getValue());
        }

        // handle all the variables Scratch provides that actively monitor sprite state
        program.setVariableValue("x_position", sprite.getX() + "");
        program.setVariableValue("y_position", sprite.getY() + "");
        program.setVariableValue("direction", sprite.getDir() + "");
        program.setVariableValue("size", sprite.getSize() + "");

        // handle world variables
        program.setVariableValue("mouse_x", getMouseXValue());
        program.setVariableValue("mouse_y", getMouseYValue());
    }

    /**
     * Returns a built-in attribute value from a named sprite.
     *
     * @param variableName attribute name
     * @param spriteName sprite name
     * @param lineNumber source line for error reporting
     * @return attribute value as a string
     */
    public static String getSpriteAttribute(String variableName, String spriteName, int lineNumber) {
        for (Sprite sprite : world.getSprites()) {
            if (sprite.getName().equals(spriteName)) {
                Program program = programsByInstanceId.get(sprite.getInstanceId());

                if (program == null) {
                    ScratchError.throwError(lineNumber, "Sprite " + spriteName + " has no program.");
                }

                recordSpriteVariables(sprite, program);

                if (program.getVariableValue(variableName) == null) {
                    ScratchError.throwError(lineNumber, "Sprite " + spriteName + " has no attribute " + variableName + ".");
                }

                return program.getVariableValue(variableName);
            }
        }

        ScratchError.throwError(lineNumber, "Unknown sprite " + spriteName + ".");
        return "";
    }

    /**
     * Executes one command, including nested blocks if needed.
     *
     * @param command command to execute
     * @param sprite sprite running the command
     * @return updated sprite
     */
    public Sprite executeCommand(Command command, Sprite sprite) {
        // replace all the args through all children
        // make a new command using deep copy and use that to execute.
        Command commandCopy = new Command(command);

        if (commandCopy.isBlock()) {
            sprite = executeBlockCommand(commandCopy, sprite);
        } else {
            resolveCommandArgs(commandCopy, sprite);
            sprite = exectuteActionCommand(commandCopy, sprite);
        }

        return sprite;
    }

    /**
     * Executes a block command immediately.
     *
     * @param command block command
     * @param sprite sprite running the block
     * @return updated sprite
     */
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
                for (Command child : command.getChildren()) {
                    sprite = executeCommand(child, sprite);
                }
                break;

            case "if":
                String condition = resolveExpressionArg(command.getArgs().get(0), sprite);
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


    /**
     * Evaluates a list of expression arguments.
     *
     * @param args raw arguments
     * @return evaluated arguments
     */
    private ArrayList<String> evalArgs(ArrayList<String> args) {
        ArrayList<String> ans = new ArrayList<>();

        for (int i = 0; i < args.size(); i++) {
            ans.add(Expression.evaluate(args.get(i)));
        }

        return ans;
    }

    /**
     * Executes one non-block command on a sprite.
     *
     * @param command command to execute
     * @param sprite sprite running the command
     * @return updated sprite
     */
    public Sprite exectuteActionCommand(Command command, Sprite sprite) {
        // command is action command
        String name = command.getName();
        ArrayList<String> args = command.getArgs();

        if (command.isPrivate()) { // variable assignment
            // eval only second arg into single string value
            args.set(1, Expression.evaluate(args.get(1)));

            if (world.hasGlobalVariable(args.get(0))) {
                world.setGlobalVariable(args.get(0), args.get(1));
            }

            programs.get(sprite).setVariableValue(args.get(0), args.get(1));
            return sprite;
        }

        // switch_costume takes a raw filename, not a numeric/string expression
        if (!name.equals("switch_costume") && !name.equals("show_variable") && !name.equals("hide_variable")) {
            args = evalArgs(args);
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
            case "set_x":
                sprite.setX((int) Double.parseDouble(args.get(0)));
                break;
            case "set_y":
                sprite.setY((int) Double.parseDouble(args.get(0)));
                break;
            case "switch_costume":
                sprite.switchCostume(args.get(0));
                break;
            case "next_costume":
                sprite.nextCostume();
                break;
            case "say":
                sprite.say(args.get(0));
                break;
            case "say_for_time":
                sprite.sayForTime(args.get(0), (int) Double.parseDouble(args.get(1)));
                peekFrame().waitUntil = System.nanoTime() + (long)(Double.parseDouble(args.get(1))*1_000_000_000L) ;
                peekFrame().clearAfterWait = "say" ;
                break;
            case "think":
                sprite.think(args.get(0));
                break;
            case "think_for_time":
                sprite.thinkForTime(args.get(0), (int) Double.parseDouble(args.get(1)));
                peekFrame().waitUntil = System.nanoTime() +  (long)(Double.parseDouble(args.get(1))*1_000_000_000L) ;
                peekFrame().clearAfterWait = "think" ;
                break;
            case "create_clone":
                createClone(sprite);
                break;
            case "set_size":
                sprite.setSize((int) Double.parseDouble(args.get(0)));
                break;
            case "change_size":
                sprite.changeSize((int) Double.parseDouble(args.get(0)));
                break;
            case "hide":
                sprite.hide();
                break;
            case "show":
                sprite.show();
                break;
            case "go_to_random_position":
                sprite.goToRandomPosition();
                break;
            case "point_in_direction":
                sprite.pointInDirection((int) Double.parseDouble(args.get(0)));
                break;
            case "show_variable":
                programs.get(sprite).showVariable(args.get(0)) ;
                break ;
            case "hide_variable":
                programs.get(sprite).hideVariable(args.get(0)) ;
                break ;
            default:
                ScratchError.throwError(command.getLineNumber(), "Unrecognized command " + "'" + name + "'");
                break;
        }

        return sprite;
    }

    /**
     * Creates a clone of a sprite and registers a new interpreter for it.
     *
     * @param sourceSprite sprite being cloned
     */
    private void createClone(Sprite sourceSprite) {
        Program sourceProgram = programs.get(sourceSprite);

        if (sourceProgram == null) {
            ScratchError.throwError(-1, "Cannot clone sprite without a program.");
        }

        Sprite cloneSprite = new Sprite(sourceSprite);
        Program cloneProgram = new Program(sourceProgram);
        Interpreter cloneInterpreter = new Interpreter(world);

        cloneInterpreter.setStartEvent(CLONE_START_EVENT);
        cloneInterpreter.addProgram(cloneSprite, cloneProgram);

        world.addSprite(cloneSprite);
        interpreterRegistry.add(cloneInterpreter);
    }

    /**
     * Returns the current mouse button state.
     *
     * @return "true" if the mouse is down, otherwise "false"
     */
    public static String getMouseDownValue() {
        return world.isMouseDown() ? "true" : "false";
    }

    /**
     * Returns the current mouse x position.
     *
     * @return mouse x position as a string
     */
    public static String getMouseXValue() {
        return world.getMouseX() + "";
    }

    /**
     * Returns the current mouse y position.
     *
     * @return mouse y position as a string
     */
    public static String getMouseYValue() {
        return world.getMouseY() + "";
    }

    /**
     * Performs one-time setup before execution starts.
     */
    private void initializeExecution() {
        if (initialized) return;
        initialized = true;
        finished = assignedSprite == null || assignedProgram == null;
        if (finished) return;

        currentSprite = assignedSprite;
        currentProgram = assignedProgram;
        currentScriptIndex = 0;
    }

    /**
     * Normalizes a script or event name by trimming it and removing a trailing colon.
     *
     * @param scriptName raw script name
     * @return normalized script name
     */
    private String normalizeScriptName(String scriptName) {
        if (scriptName == null) {
            return "";
        }

        scriptName = scriptName.trim();

        if (scriptName.endsWith(":")) {
            scriptName = scriptName.substring(0, scriptName.length() - 1).trim();
        }

        return scriptName;
    }

    /**
     * Checks whether a script should run for this interpreter's start event.
     *
     * @param script script to test
     * @return true if the script matches the active start event
     */
    private boolean shouldRunScript(Script script) {
        return normalizeScriptName(script.getName()).equals(startEventName);
    }

    /**
     * Pushes the next matching script onto the execution stack.
     *
     * @return true if a script was pushed, false otherwise
     */
    private boolean pushNextScript() {
        while (currentScriptIndex < currentProgram.getScripts().size()) {
            Script script = currentProgram.getScripts().get(currentScriptIndex++);
            if (shouldRunScript(script) && !script.getCommands().isEmpty()) {
                pushFrame(new ExecutionFrame(script.getCommands(), ExecutionFrame.SCRIPT));
                return true;
            }
        }

        return false;
    }

    /**
     * Executes one leaf command after resolving its arguments.
     *
     * @param command command to execute
     * @param sprite sprite running the command
     * @return updated sprite
     */
    private Sprite executeLeafCommand(Command command, Sprite sprite) {
        Command commandCopy = new Command(command);
        resolveCommandArgs(commandCopy, sprite);
        return exectuteActionCommand(commandCopy, sprite);
    }

    /**
     * Enters a block command by pushing the correct execution frame.
     *
     * @param command block command to enter
     */
    private void enterBlock(Command command) {
        switch (command.getName()) {
            case "repeat":
                int times = (int) Double.parseDouble(Expression.evaluate(resolveExpressionArg(command.getArgs().get(0), currentSprite)));
                if (times > 0 && !command.getChildren().isEmpty()) {
                    ExecutionFrame repeatFrame = new ExecutionFrame(command.getChildren(), ExecutionFrame.REPEAT);
                    repeatFrame.remainingIterations = times;
                    pushFrame(repeatFrame);
                }
                break;
            case "repeat_until":
                String repeatUntilCondition = resolveExpressionArg(command.getArgs().get(0), currentSprite);
                if (BooleanExpression.evaluate(repeatUntilCondition).equals("false") && !command.getChildren().isEmpty()) {
                    ExecutionFrame repeatUntilFrame = new ExecutionFrame(command.getChildren(), ExecutionFrame.REPEAT_UNTIL);
                    repeatUntilFrame.conditionExpression = command.getArgs().get(0);
                    pushFrame(repeatUntilFrame);
                }
                break;
            case "forever":
                if (world.isRunning() && !command.getChildren().isEmpty()) {
                    pushFrame(new ExecutionFrame(command.getChildren(), ExecutionFrame.FOREVER));
                }
                break;
            case "if":
                String condition = resolveExpressionArg(command.getArgs().get(0), currentSprite);
                if (BooleanExpression.evaluate(condition).equals("true") && !command.getChildren().isEmpty()) {
                    pushFrame(new ExecutionFrame(command.getChildren(), ExecutionFrame.IF_BRANCH));
                } else if (command.hasElse() && !command.getElseChildren().isEmpty()) {
                    pushFrame(new ExecutionFrame(command.getElseChildren(), ExecutionFrame.IF_BRANCH));
                }
                break;
            default:
                ScratchError.throwError(command.getLineNumber(), "Unrecognized block command " + "'" + command.getName() + "'");
                break;
        }
    }

    /**
     * Finishes the current execution frame and decides whether to yield.
     *
     * @param frame frame being completed
     * @param executedLeafCommand whether this tick already executed a leaf command
     * @return true if tick should yield, false otherwise
     */
    private boolean completeFrame(ExecutionFrame frame, boolean executedLeafCommand) {
        switch (frame.type) {
            case ExecutionFrame.SCRIPT:
            case ExecutionFrame.IF_BRANCH:
                popFrame();
                return false;
            case ExecutionFrame.REPEAT:
                if (frame.remainingIterations > 1) {
                    frame.remainingIterations--;
                    frame.index = 0;
                    return !executedLeafCommand;
                } else {
                    popFrame();
                }
                return false;
            case ExecutionFrame.REPEAT_UNTIL:
                String condition = resolveExpressionArg(frame.conditionExpression, currentSprite);
                if (BooleanExpression.evaluate(condition).equals("false")) {
                    frame.index = 0;
                    return !executedLeafCommand;
                } else {
                    popFrame();
                }
                return false;
            case ExecutionFrame.FOREVER:
                if (world.isRunning()) {
                    frame.index = 0;
                    return !executedLeafCommand;
                } else {
                    popFrame();
                }
                return false;
            default:
                popFrame();
                return false;
        }
    }

    /**
     * Pushes a frame onto the execution stack.
     * @param frame frame to push
     */
    private void pushFrame(ExecutionFrame frame) {
        executionStack.add(frame);
    }

    /**
     * Returns the frame at the top of the execution stack.
     * @return current execution frame
     */
    private ExecutionFrame peekFrame() {
        return executionStack.get(executionStack.size() - 1);
    }

    /**
     * Removes the frame at the top of the execution stack.
     */
    private void popFrame() {
        executionStack.remove(executionStack.size() - 1);
    }

    /**
     * Returns the program assigned to this interpreter.
     * @return assigned program
     */
    public Program getProgram()
    {
        return assignedProgram ;
    }

    /**
     * Returns the sprite assigned to this interpreter.
     * @return assigned sprite
     */
    public Sprite getSprite()
    {
        return assignedSprite ;
    }
}
