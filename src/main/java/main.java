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
import java.util.List;
import java.util.Map;

/**
 * Main application class for the Scratch-From-Scratch engine.
 * Sets up the backend simulation world, parses assets/scripts, initializes
 * the JavaFX user interface, and executes the simulation loop.
 */
public class main extends Application {

    private static final String SB_GAME_FOLDER_NAME = "SBGame";

    private World world;
    private Gui gui;
    private Parser parser;
    private Interpreter interpreter;

    // Maps folder (sprite/system) names to their associated asset files
    private Map<String, List<File>> sbGameFiles;

    @Override
    public void start(Stage stage) {
        setupBackend();
        launchGui(stage);     // Creates GUI and configures interpreter
        testParser();         // Parses scripts and populates sprites into the world
        testSpritesLoaded();  // Diagnostic printout of loaded sprites
        runInterpreter();     // Launches execution thread
    }

    // ==================================================
    // BACKEND SETUP
    // ==================================================
    public void setupBackend() {
        System.out.println("\n=== BACKEND SETUP ===");
        world = new World();
        parser = new Parser();
        sbGameFiles = getSBGameFolderFiles(SB_GAME_FOLDER_NAME);
        System.out.println("Loaded folders: " + sbGameFiles.keySet());
    }

    // ==================================================
    // GUI SETUP
    // ==================================================
    public void launchGui(Stage stage) {
        System.out.println("\n=== GUI TEST ===");
        gui = new Gui(world);
        stage.setTitle("Scratch-From-Scratch");
        gui.refresh_and_draw(stage);

        // Created after GUI setup to ensure references are healthy
        interpreter = new Interpreter(world, gui);
        System.out.println("GUI launched");
    }

    // ==================================================
    // PARSER TEST
    // ==================================================
    public void testParser() {
        System.out.println("\n=== PARSER TEST ===");

        for (String folderName : sbGameFiles.keySet()) {
            // Skip the root folder directory
            if (folderName.equals(SB_GAME_FOLDER_NAME)) {
                continue;
            }

            try {
                File scriptFile = new File(SB_GAME_FOLDER_NAME + "/" + folderName + "/script.scratch");

                if (!scriptFile.exists()) {
                    System.out.println("No script found for: " + folderName);
                    continue;
                }

                Program program = parser.buildProgram(scriptFile);
                System.out.println("RUNNING " + scriptFile.toString());

                // Create and track sprite instance with its discovered assets
                Sprite sprite = new Sprite(folderName, new ArrayList<>(sbGameFiles.get(folderName)));
                world.addSprite(sprite);
                interpreter.addProgram(sprite, program);

                System.out.println("Loaded sprite: " + folderName);

            } catch (FileNotFoundException e) {
                System.err.println("Failed to parse sprite: " + folderName);
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
            System.out.println("Costume count: " + sprite.getCostumes().size());
        }
    }

    // ==================================================
    // INTERPRETER TEST
    // ==================================================
    public void runInterpreter() {
        System.out.println("\n=== INTERPRETER TEST ===");
        Thread interpreterThread = new Thread(() -> {
            // Note: Any visual updates or JavaFX node alterations done during 
            // execute() must be wrapped in Platform.runLater().
            interpreter.execute();
        });

        // Set the execution thread to daemon so that closing the GUI
        // terminates the background interpreter task automatically.
        interpreterThread.setDaemon(true);
        interpreterThread.start();
    }

    // ==================================================
    // FILE LOADER
    // ==================================================
    private static Map<String, List<File>> getSBGameFolderFiles(String sbGamePath) {
        Map<String, List<File>> folderFilesMap = new HashMap<>();
        File sbGameDir = new File(sbGamePath);

        if (!sbGameDir.exists() || !sbGameDir.isDirectory()) {
            System.err.println("SBGame directory not found: " + sbGamePath);
            return folderFilesMap;
        }

        File[] contents = sbGameDir.listFiles();
        if (contents == null) {
            return folderFilesMap;
        }

        List<File> rootFiles = new ArrayList<>();

        for (File item : contents) {
            if (item.isFile() && !item.getName().equals("script.scratch")) {
                rootFiles.add(item);
            } else if (item.isDirectory()) {
                List<File> spriteFiles = new ArrayList<>();
                File[] spriteContents = item.listFiles();

                if (spriteContents != null) {
                    for (File spriteFile : spriteContents) {
                        if (spriteFile.isFile() && !spriteFile.getName().equals("script.scratch")) {
                            spriteFiles.add(spriteFile);
                        }
                    }
                }
                folderFilesMap.put(item.getName(), spriteFiles);
            }
        }

        folderFilesMap.put(sbGameDir.getName(), rootFiles);
        return folderFilesMap;
    }

    public static void main(String[] args) {
        launch(args);
    }
}