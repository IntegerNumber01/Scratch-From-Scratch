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


    private Command parseCommand(String cmd) {
        int openParen = cmd.indexOf('(');
        int closeParen = cmd.indexOf(')');

        String cmdName = cmd.substring(0, openParen);
        String argsString = cmd.substring(openParen + 1, closeParen);

        String[] argsList = argsString.split(",");

        // clean all whitespace
        for (int i = 0; i < argsList.length; i++) {
            argsList[i] = argsList[i].trim();
        }

        return new Command(cmdName, new ArrayList<>(Arrays.asList(argsList)), LanguageConfig.isBlockCommand(cmdName));
    }

    public void readFile(File file) throws FileNotFoundException {
        Program program = new Program(file.getName());
        ArrayList<Script> scripts = new ArrayList<Script>();
        Script currentScript = null;
        Command currentCommand = null;

        Scanner scanner = new Scanner(file);
        int indentLevel = 0;
        int expectedIndentLevel = 0; // this is updated when we expect a new indent after a repeat/if statemetn
        int prevIndentLevel = -1;
        // stores the latest command block at each indent level
        ArrayList<Command> blockStack = new ArrayList<Command>();
        

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            // System.out.println(line); // prove file loading works

            prevIndentLevel = indentLevel;
            indentLevel = checkIndentLevel(line);

            line = line.trim();

            if (indentLevel == 0) { // event block
                if (currentScript != null) {
                    scripts.add(currentScript);
                }
                currentScript = new Script(line);
                expectedIndentLevel = 1;
                // scripts.add(new Script(line)); // should we substring the colon?
            } else if (indentLevel == expectedIndentLevel) { // commands or conditions/statements
                currentCommand = parseCommand(line);
                currentScript.addCommand(currentCommand);
                blockStack.get(indentLevel).addChild(currentCommand); // add this command to the latest block command

                if (currentCommand.isBlock()) { // if a block command
                    expectedIndentLevel += 1;
                }
            } else if (indentLevel == prevIndentLevel - 1) { // exited block
                currentScript.addCommand(currentCommand);
                expectedIndentLevel -= 1;
                blockStack.set(prevIndentLevel, null);
            }
        }

        if (currentScript != null) {
            scripts.add(currentScript);
        }

        scanner.close();
    }
}
