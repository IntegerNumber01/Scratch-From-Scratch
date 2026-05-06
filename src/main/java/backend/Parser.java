package backend;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/*
    This class is responsible for parsing the Scratch code and creating Script objects
*/
public class Parser {
    private World world;

    public Parser(World world) {
        this.world = world;
    }

    public void readFile(File file) throws FileNotFoundException {
        Scanner scanner = new Scanner(file);

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            System.out.println(line); // prove file loading works
        }

        scanner.close();
    }
}
