import java.io.File;
import java.io.FileNotFoundException;

import backend.Interpreter;
import backend.Parser;
import backend.Program;
import backend.World;

public class BackendMain {
    

    public static void main(String[] args) {
        World world = new World();
        Parser parser = new Parser(world);
        Program program = null;

        try {
            program = parser.buildProgram(new File("SBGame/Sprite1/script.scratch"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Interpreter interpreter = new Interpreter(program);
    }
}
