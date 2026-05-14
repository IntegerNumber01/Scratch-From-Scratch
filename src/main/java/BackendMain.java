import java.io.File;
import java.io.FileNotFoundException;

import backend.Interpreter;
import backend.Parser;
import backend.Program;
import backend.Sprite;
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

        Sprite cat = new Sprite("cat");
        System.out.println(cat.toString());
        Interpreter interpreter = new Interpreter(world);
        interpreter.addProgram(cat, program);

        interpreter.execute();
        System.out.println(cat.toString());
    }
}
