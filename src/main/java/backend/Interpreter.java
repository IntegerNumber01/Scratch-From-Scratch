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

    public void execute() {

        for (Sprite sprite : programs.keySet()) {
            Program program = programs.get(sprite);

            for (Script script : program.getScripts()) {
                for (Command command : script.getCommands()) {
                    sprite = executeCommand(command, sprite);
                }
            }
        }
    }


    public Sprite executeBlockCommand(Command command, Sprite sprite) {
        if (command.getName().equals("repeat")) {
            int times = Integer.parseInt(command.getArgs().get(0)); // num of times to repeat

            for (int i = 0; i < times; i++) {
                for (Command child : command.getChildren()) {
                    sprite = executeCommand(child, sprite);
                }
            }
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

    /*
    Take a single command and execute corresponding Scratch functions from Sprite
    */
    public Sprite exectuteActionCommand(Command command, Sprite sprite) {
        // command is action command
        String name = command.getName();
        ArrayList<String> args = command.getArgs();

        switch (name) {
            case "move":
                sprite.move(Integer.parseInt(args.get(0)));
                break;
            case "go_to":
                sprite.goTo(Integer.parseInt(args.get(0)), Integer.parseInt(args.get(1)));
                break;
            case "turn_left":
                sprite.turnLeft(Integer.parseInt(args.get(0)));
                break;
            case "turn_right":
                sprite.turnRight(Integer.parseInt(args.get(0)));
                break;
        }

        return sprite;
    }
}
