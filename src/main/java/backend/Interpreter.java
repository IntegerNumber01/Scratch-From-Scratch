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

    }

    /*
    Take a single command and execute corresponding Scratch functions from Sprite
    */
    public void mapCommandToSprite(Command command) {

    }
}
