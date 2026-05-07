import java.io.File;
import java.io.FileNotFoundException;

import backend.Parser;
import backend.World;

public class BackendMain {
    

    public static void main(String[] args) {
        World world = new World();
        Parser parser = new Parser(world);

        try {
            parser.readFile(new File("SBGame/Sprite1/script.scratch"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
