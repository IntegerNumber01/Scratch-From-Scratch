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
        Parser parser = new Parser();
        Program program = null;

        try {
            System.out.println("[START] PROGRAM BUILDER OUTPUT --------------");
            program = parser.buildProgram(new File("SBGame/Cat/script.scratch"));
            System.out.println("[END] PROGRAM BUILDER OUTPUT --------------");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Sprite cat = new Sprite("cat");
        System.out.println(cat.toString());
        System.out.println();
        Interpreter interpreter = new Interpreter(world);
        interpreter.addProgram(cat, program);

        interpreter.execute();
        System.out.println(cat.toString());
    }
}