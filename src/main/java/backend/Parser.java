package backend;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/*
    This class is responsible for parsing the Scratch code and creating Script objects
*/
public class Parser {
    private World world;

    public Parser(World world) {
        this.world = world;
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
    private Command parseCommand(String cmd) {
        int openParen = cmd.indexOf('(');
        int closeParen = cmd.indexOf(')');

        String cmdName = cmd.substring(0, openParen).trim().replaceAll("[^a-zA-Z]", "");
        String argsString = cmd.substring(openParen + 1, closeParen);

        ArrayList<String> args = new ArrayList<>();

        if (!argsString.isEmpty()) {
            String[] argsList = argsString.split(",");
            // clean all whitespace
            for (int i = 0; i < argsList.length; i++) {
                args.add(argsList[i].trim());
            }
        }

        return new Command(cmdName, args);
    }

    /*
    DOES NOT WORK YET

    Reads a .scratch file line by line and creates Script and Command objects based on the indentation and content of each line. The resulting Script objects are stored in a Program object.
    */
    public void readFile(File file) throws FileNotFoundException {
        Program program = new Program(file.getName());
        ArrayList<Script> scripts = new ArrayList<Script>();
        Script currentScript = null;
        Command currentCommand = null;

        Scanner scanner = new Scanner(file);
        int indentLevel = 0;
        // stores the latest command block at each indent level
        ArrayList<Command> blockStack = new ArrayList<Command>();


        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();

            indentLevel = checkIndentLevel(line);

            line = line.trim();

            if (indentLevel == 0) { // event block
                if (currentScript != null) {
                    scripts.add(currentScript);
                }
                currentScript = new Script(line);
                blockStack.clear();
            } else {

                currentCommand = parseCommand(line);

                // remove blocks that are no longer active
                while (blockStack.size() > indentLevel - 1) {
                    blockStack.remove(blockStack.size() - 1);
                }

                // top-level command
                if (indentLevel == 1) {

                    currentScript.addCommand(currentCommand);

                } else {

                    // nested command
                    Command parent = blockStack.get(indentLevel - 2);
                    parent.addChild(currentCommand);
                }

                // remember this block if it opens a new scope
                if (currentCommand.isBlock()) {
                    blockStack.add(currentCommand);
                }
            }
        }

        if (currentScript != null) {
            scripts.add(currentScript);
        }

        scanner.close();

        for (Script s : scripts) {
            System.out.println(s.toString());
        }
    }
}
