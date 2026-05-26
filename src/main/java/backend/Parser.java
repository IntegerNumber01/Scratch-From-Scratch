package backend;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/*
    This class is responsible for parsing the Scratch code and creating Script objects
*/
public class Parser {
    /**
     * Private constructor to prevent instantiation of this class since all methods are static and it is just a utility class.
     */
    private Parser() {
    }

    /**
     * Finds the index of the top-level assignment operator in a line of code.
     * This is used to determine if a line of code is a variable assignment.
     * It ignores any assignment operators that are inside parentheses, since those would be part of an expression rather than a variable assignment.
     * @param line
     * @return int index of the top-level assignment operator, or -1 if there is no top-level assignment operator
     */
    private static int findTopLevelAssignmentIndex(String line) {
        int depth = 0;

        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);

            if (ch == '(') {
                depth++;
            } else if (ch == ')') {
                depth--;
            } else if (ch == '=' && depth == 0) {
                // checking for ==
                boolean isDoubleEqualsLeft = i > 0 && line.charAt(i - 1) == '=';
                boolean isDoubleEqualsRight = i + 1 < line.length() && line.charAt(i + 1) == '=';

                if (!isDoubleEqualsLeft && !isDoubleEqualsRight) { // no ==
                    return i;
                }
            }
        }

        return -1;
    }

    /**
     * Checks the indentation level of a line of code. Each indent level is determined by 4 spaces.
     * @param line
     * @return int indentation level of the line of code, where 0 is top-level, 1 is one indent, etc.
     */
    private static int checkIndentLevel(String line) {
        int indentLevel = 0;
        int spaces = 0;
        for (char c : line.toCharArray()) {
            if (c == ' ') {
                spaces++;
            } else {
                break;
            }
        }
        indentLevel = spaces / 4;
        return indentLevel;
    }

    /**
     * Splits a string of arguments into a list of individual arguments using commas.
     * @param argsString
     * @return ArrayList<String> list of individual arguments
     */
    private static ArrayList<String> splitArgs(String argsString) {
        ArrayList<String> args = new ArrayList<>();
        int depth = 0; // basically simulates a stack, but I don't like stack
        // using string builder since its more efficient
        StringBuilder current = new StringBuilder();

        for (int i = 0; i < argsString.length(); i++) {
            char ch = argsString.charAt(i);

            if (ch == '(') {
                depth++;
            } else if (ch == ')') {
                depth--;
            }

            if (ch == ',' && depth == 0) {
                args.add(current.toString().trim());
                current.setLength(0);
            } else {
                current.append(ch);
            }
        }

        if (current.length() > 0) {
            args.add(current.toString().trim());
        }

        return args;
    }

    /**
     * Strips comments from a line of code. In Scratch, comments start with a # and continue to the end of the line. Ignores any # that are inside parentheses.
     * @param line
     * @return String line of code with comments stripped
     */
    private static String stripComments(String line) {
        // just checks if the # is on an outer level, if it is, then strip
        int depth = 0;

        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);

            if (ch == '(') {
                depth++;
            } else if (ch == ')') {
                depth--;
            } else if (ch == '#' && depth == 0) {
                return line.substring(0, i);
            }
        }

        return line;
    }


    /**
     * Parses a line of code directly from the user .scratch file and converts it into a Command object with parameters. For example, "move(10)" would be converted into a Command with name "move" and args ["10"].
     * This method also handles variable assignment, which is determined by the presence of a top-level assignment operator (=) that is not part of an expression.
     * @param lineNumber
     * @return Command object representing the command in the line of code
     */
    public static Command parseCommand(String cmd, int lineNumber) {
        int assignmentIndex = findTopLevelAssignmentIndex(cmd);
        int openParen = cmd.indexOf('(');
        int closeParen = cmd.lastIndexOf(')');

        if (assignmentIndex != -1) { // variable assignment
            String varName = cmd.substring(0, assignmentIndex).trim();
            String value = cmd.substring(assignmentIndex + 1).trim();
            ArrayList<String> args = new ArrayList<>();
            args.add(varName);
            args.add(value);
            return new Command("assign", args, lineNumber, true);
        }

        if (openParen == -1 && closeParen == -1) {
            ScratchError.throwError(lineNumber, "Invalid command: '" + cmd + "'");
        } else {
            String cmdName = cmd.substring(0, openParen).trim().replaceAll("[^a-zA-Z_]", "");
            String argsString = cmd.substring(openParen + 1, closeParen);

            ArrayList<String> args = new ArrayList<>();

            if (!argsString.isEmpty()) {
                args = splitArgs(argsString);
            }

            return new Command(cmdName, args, lineNumber);
        }

        return null;
    }

    /**
     * Parses the backdrop.scratch file and returns a HashMap of global variable names to their values. The backdrop.scratch file is expected to only contain variable assignments, and any line that does not follow this format will result in an error.
     * @param file
     * @return HashMap of global variable names to their values
     * @throws FileNotFoundException
     */
    public static HashMap<String, String> parseBackdrop(File file) throws FileNotFoundException {
        HashMap<String, String> globalVariables = new HashMap<>();
        Scanner scanner = new Scanner(file);
        int lineNumber = 0;

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            int indentLevel = checkIndentLevel(line);
            line = stripComments(line);
            line = line.trim();
            lineNumber++;

            if (line.isEmpty()) {
                continue;
            }

            if (indentLevel != 0) {
                ScratchError.throwError(lineNumber, "backdrop.scratch only supports global variable assignments.");
            }

            Command command = parseCommand(line, lineNumber);

            if (!command.isPrivate()) {
                ScratchError.throwError(lineNumber, "backdrop.scratch only supports global variable assignments.");
            }

            String varName = command.getArgs().get(0);
            String value = command.getArgs().get(1);

            if (LanguageConfig.isReservedVariableName(varName)) {
                ScratchError.throwError(lineNumber, "Global variable '" + varName + "' uses a reserved Scratch variable name.");
            }

            if (LanguageConfig.isReservedWord(varName)) {
                ScratchError.throwError(lineNumber, "Global variable '" + varName + "' uses a reserved language word.");
            }

            if (globalVariables.containsKey(varName)) {
                ScratchError.throwError(lineNumber, "Duplicate global variable '" + varName + "'.");
            }

            globalVariables.put(varName, new String(value));
        }

        scanner.close();
        return globalVariables;
    }


    /**
     * Reads a .scratch file line by line and creates Script and Command objects based on the indentation and content of each line. The resulting Script objects are stored in a Program object.
     * @param file
     * @return Program object containing the scripts and functions defined in the .scratch file
     * @throws FileNotFoundException
     */
    public static Program buildProgram(File file) throws FileNotFoundException {
        ArrayList<Script> scripts = new ArrayList<Script>();
        Script currentScript = null;
        Command currentCommand = null;
        Command temp = null;

        Scanner scanner = new Scanner(file);
        int indentLevel = 0;
        int lineNumber = 0;
        int targetStackSize;
        // stores the latest command block at each indent level
        ArrayList<Command> blockStack = new ArrayList<Command>();
        ArrayList<Boolean> elseStack = new ArrayList<Boolean>();
        ArrayList<Script> functions = new ArrayList<Script>();

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();

            indentLevel = checkIndentLevel(line);
            line = stripComments(line);

            line = line.trim();

            lineNumber++;


            if (line.isEmpty()) {
                continue;
            }

            if (indentLevel == 0) { // event or function block
                if (currentScript != null) {
                    if (currentScript.isFunction()) {
                        functions.add(currentScript);
                    } else {
                        scripts.add(currentScript);
                    }
                }

                if (line.startsWith(LanguageConfig.DEFINE_KEYWORD)) {
                    // use the parseCommand to obtain name and args, but throw away the command itself
                    temp = parseCommand(line.substring(LanguageConfig.DEFINE_KEYWORD.length() + 1), lineNumber); // remove define

                    if (LanguageConfig.isReservedWord(temp.getName())) {
                        ScratchError.throwError(lineNumber, "Function name '" + temp.getName() + "' uses a reserved language word.");
                    }

                    line = temp.getName();

                    currentScript = new Script(line, temp.getArgs(), lineNumber);
                } else {
                    currentScript = new Script(line, lineNumber);
                }

                blockStack.clear();
                elseStack.clear();

            } else {
                boolean isElse = line.equals(LanguageConfig.ELSE_KEYWORD + ":");

                // remove blocks that are no longer active
                if (isElse) {
                    targetStackSize = indentLevel;
                } else {
                    targetStackSize = indentLevel - 1;
                }

                while (blockStack.size() > targetStackSize) {
                    blockStack.remove(blockStack.size() - 1);
                    elseStack.remove(elseStack.size() - 1);
                }

                if (isElse) {
                    if (blockStack.isEmpty() || !blockStack.get(blockStack.size() - 1).getName().equals("if")) {
                        ScratchError.throwError(lineNumber, "else without matching if");
                    }

                    elseStack.set(elseStack.size() - 1, true);
                    continue;
                }

                currentCommand = parseCommand(line, lineNumber);

                // top-level command
                if (indentLevel == 1) {

                    currentScript.addCommand(currentCommand);

                } else {

                    // nested command
                    if (indentLevel - 2 >= blockStack.size()) {
                        ScratchError.throwError(
                        lineNumber,
                        "Invalid indentation: no enclosing block"
                        );
                    }

                    Command parent = blockStack.get(indentLevel - 2);
                    if (elseStack.get(indentLevel - 2) && parent.getName().equals("if")) {
                        parent.addElseChild(currentCommand);
                    } else {
                        parent.addChild(currentCommand);
                    }
                }

                // remember this block if it opens a new scope
                if (currentCommand.isBlock()) {
                    blockStack.add(currentCommand);
                    elseStack.add(false);
                }
            }
        }

        if (currentScript != null) {
            if (currentScript.isFunction()) {
                functions.add(currentScript);
            } else {
                scripts.add(currentScript);
            }
        }

        scanner.close();

        Program program = new Program(file.getName());


        System.out.println("-- SCRIPTS --");
        for (Script s : scripts) {
            System.out.println(s.toString());
            program.addScript(s);
        }
        System.out.println();
        System.out.println("-- FUNCTIONS --");

        for (Script f : functions) {
            System.out.println(f.toString());
            program.addFunction(f);
        }

        return program;
    }
}
