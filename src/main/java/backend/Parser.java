package backend;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/*
    This class is responsible for parsing the Scratch code and creating Script objects
*/
public class Parser {
    public Parser() {
    }

    // Assumes each indent is 4 spaces
    private int checkIndentLevel(String line) {
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


    /*
    Parses a line of code directly from the user .scratch file and converts it into a Command object with parameters. For example, "move(10)" would be converted into a Command with name "move" and args ["10"]
    */
    private Command parseCommand(String cmd, int lineNumber) {
        int openParen = cmd.indexOf('(');
        int closeParen = cmd.indexOf(')');

        if (openParen == -1 && closeParen == -1) {
            // could be variable assignement since commands with no args don't exist
            if (cmd.contains("=")) {
                String[] parts = cmd.split("=");
                String varName = parts[0].trim();
                String value = parts[1].trim();
                ArrayList<String> args = new ArrayList<>();
                args.add(varName);
                args.add(value);
                return new Command("assign", args, lineNumber, true);
            }
        } else {
            String cmdName = cmd.substring(0, openParen).trim().replaceAll("[^a-zA-Z_]", "");
            String argsString = cmd.substring(openParen + 1, closeParen);

            ArrayList<String> args = new ArrayList<>();

            if (!argsString.isEmpty()) {
                String[] argsList = argsString.split(",");
                // clean all whitespace
                for (int i = 0; i < argsList.length; i++) {
                    args.add(argsList[i].trim());
                }
            }

            return new Command(cmdName, args, lineNumber);
        }

        return null;
    }

    /*
    Reads a .scratch file line by line and creates Script and Command objects based on the indentation and content of each line. The resulting Script objects are stored in a Program object.
    */
    public Program buildProgram(File file) throws FileNotFoundException {
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

                if (line.startsWith("define")) {
                    // use the parseCommand to obtain name and args, but throw away the command itself
                    temp = parseCommand(line.substring(7), lineNumber); // remove define

                    line = temp.getName();

                    currentScript = new Script(line, temp.getArgs(), lineNumber);
                } else {
                    currentScript = new Script(line, lineNumber);
                }

                blockStack.clear();
                elseStack.clear();

            } else {
                boolean isElse = line.equals("else:");

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
            scripts.add(currentScript);
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
