import backend.Interpreter;
import backend.Parser;
import backend.Program;
import backend.Sprite;
import backend.World;
import gui.Gui;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class main extends Application {

    private World world;
    private Gui gui;
    private Parser parser;
    private Interpreter interpreter;

    // folderName -> files
    private Map<String, ArrayList<File>> sbGameFiles;

@Override
public void start(Stage stage) {
    setupBackend();
    testSpritesLoaded();  
    launchGui(stage);     // gui + interpreter created here
    testParser();         // now interpreter exists, addProgram works
    runInterpreter();
}

    // ==================================================
    // BACKEND SETUP
    // ==================================================

public void setupBackend() {
    System.out.println("\n=== BACKEND SETUP ===");
    world = new World();
    parser = new Parser();
    // DON'T create interpreter here, gui is still null
    sbGameFiles = getSBGameFolderFiles("SBGame");
    System.out.println("Loaded folders:");
    System.out.println(sbGameFiles.keySet());
}

    // ==================================================
    // PARSER TEST
    // ==================================================

    public void testParser() {

        System.out.println("\n=== PARSER TEST ===");

        for (String folderName : sbGameFiles.keySet()) {

            // skip root folder
            if (folderName.equals("SBGame")) {
                continue;
            }

            try {

                File scriptFile = new File(
                        "SBGame/" + folderName + "/script.scratch"
                );

                if (!scriptFile.exists()) {
                    System.out.println("No script found for: " + folderName);
                    continue;
                }

                Program program = parser.buildProgram(scriptFile);
                System.out.println("RUNNING " + scriptFile.toString());

                Sprite sprite = new Sprite(
                        folderName,
                        sbGameFiles.get(folderName)
                );

                world.addSprite(sprite);

                interpreter.addProgram(sprite, program);

                System.out.println("Loaded sprite: " + folderName);

            } catch (FileNotFoundException e) {

                System.out.println("Failed to parse sprite: " + folderName);

                e.printStackTrace();
            }
        }
    }

    // ==================================================
    // SPRITE TEST
    // ==================================================

    public void testSpritesLoaded() {

        System.out.println("\n=== SPRITE TEST ===");

        System.out.println("Sprite count: " + world.getSprites().size());

        for (Sprite sprite : world.getSprites()) {

            System.out.println("----------------");

            System.out.println("Name: " + sprite.getName());

            System.out.println("X: " + sprite.getX());

            System.out.println("Y: " + sprite.getY());

            System.out.println("Direction: " + sprite.getDir());

            System.out.println("Costume count: "
                    + sprite.getCostumes().size());
        }
    }

    // ==================================================
    // GUI TEST
    // ==================================================

public void launchGui(Stage stage) {
    System.out.println("\n=== GUI TEST ===");
    gui = new Gui(world);
    stage.setTitle("Scratch-From-Scratch");
    gui.refresh_and_draw(stage);

    // create interpreter HERE after gui exists
    interpreter = new Interpreter(world, gui);

    System.out.println("GUI launched");
}

    // ==================================================
    // INTERPRETER TEST
    // ==================================================

    public void runInterpreter() {
    System.out.println("\n=== INTERPRETER TEST ===");
    new Thread(() -> {
        interpreter.execute();
    }).start();
}

    // ==================================================
    // FILE LOADER
    // ==================================================

    /**
     * Traverses the SBGame directory and maps each sprite folder
     * to an ArrayList of its files, excluding script.scratch
     */
    private static Map<String, ArrayList<File>> getSBGameFolderFiles(
            String sbGamePath
    ) {

        Map<String, ArrayList<File>> folderFilesMap =
                new HashMap<>();

        File sbGameDir = new File(sbGamePath);

        if (!sbGameDir.exists() || !sbGameDir.isDirectory()) {

            System.err.println(
                    "SBGame directory not found: "
                            + sbGamePath
            );

            return folderFilesMap;
        }

        File[] contents = sbGameDir.listFiles();

        if (contents == null) {
            return folderFilesMap;
        }

        ArrayList<File> rootFiles = new ArrayList<>();

        for (File item : contents) {

            if (item.isFile()
                    && !item.getName().equals("script.scratch")) {

                rootFiles.add(item);

            } else if (item.isDirectory()) {

                ArrayList<File> spriteFiles =
                        new ArrayList<>();

                File[] spriteContents = item.listFiles();

                if (spriteContents != null) {

                    for (File spriteFile : spriteContents) {

                        if (spriteFile.isFile()
                                && !spriteFile.getName()
                                .equals("script.scratch")) {

                            spriteFiles.add(spriteFile);
                        }
                    }
                }

                folderFilesMap.put(
                        item.getName(),
                        spriteFiles
                );
            }
        }

        folderFilesMap.put(
                sbGameDir.getName(),
                rootFiles
        );

        return folderFilesMap;
    }

    // ==================================================
    // MAIN
    // ==================================================

    public static void main(String[] args) {

        launch(args);
    }
}