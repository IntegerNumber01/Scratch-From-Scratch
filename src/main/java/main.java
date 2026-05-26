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
 * Each sprite gets its own Interpreter instance, and the GUI advances them on a shared timer.
 */
public class main extends Application {

    private static final String SB_GAME_FOLDER_NAME = "SBGame";

    private World world;
    private Gui gui;

    // Each sprite gets its own interpreter
    private List<Interpreter> interpreters = new ArrayList<>();

    // Maps folder (sprite/system) names to their associated asset files
    private Map<String, List<File>> sbGameFiles;

    @Override
    public void start(Stage stage) {
        setupBackend();
        testParser();         // Parses scripts and populates sprites into the world
        testSpritesLoaded();  // Diagnostic printout of loaded sprites
        launchGui(stage);     // Creates GUI
        runInterpreter();     // GUI owns the tick loop
    }

    // ==================================================
    // BACKEND SETUP
    // ==================================================
    public void setupBackend() {
        // System.out.println("\n=== BACKEND SETUP ===");
        world = new World();
        sbGameFiles = getSBGameFolderFiles(SB_GAME_FOLDER_NAME);

        try {
            File backdropFile = new File(SB_GAME_FOLDER_NAME + "/backdrop.scratch");
            HashMap<String, String> globalVariables = Parser.parseBackdrop(backdropFile);
            world.addGlobalVariables(globalVariables);
        } catch (FileNotFoundException e) {
            System.err.println("Failed to parse backdrop globals.");
            e.printStackTrace();
        }

        // System.out.println("Loaded folders: " + sbGameFiles.keySet());
    }

    // ==================================================
    // GUI SETUP
    // ==================================================
    public void launchGui(Stage stage) {
        gui = new Gui(world);
        stage.setTitle("Scratch-From-Scratch");
        gui.refresh_and_draw(stage);
    }

    // ==================================================
    // PARSER TEST
    // ==================================================
    public void testParser() {
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

                Program program = Parser.buildProgram(scriptFile);
                // System.out.println("RUNNING " + scriptFile.toString());

                // Create sprite with its discovered assets
                Sprite sprite = new Sprite(folderName, new ArrayList<>(sbGameFiles.get(folderName)));
                world.addSprite(sprite);
                // System.out.println("Costume count: " + sprite.getCostumes().size());
                // for (File f : sprite.getCostumes()) {
                //     System.out.println("  Costume: " + f.getName() + " exists=" + f.exists());
                // }

                // Each sprite gets its own interpreter
                Interpreter spriteInterpreter = new Interpreter(world);
                spriteInterpreter.addProgram(sprite, program);
                interpreters.add(spriteInterpreter);

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
        System.out.println("Sprite count: " + world.getSprites().size());

        // for (Sprite sprite : world.getSprites()) {
        //     System.out.println("----------------");
        //     System.out.println("Name: " + sprite.getName());
        //     System.out.println("X: " + sprite.getX());
        //     System.out.println("Y: " + sprite.getY());
        //     System.out.println("Direction: " + sprite.getDir());
        //     System.out.println("Costume count: " + sprite.getCostumes().size());
        // }
    }

    // ==================================================
    // INTERPRETER - GUI drives each interpreter at a fixed tick rate
    // ==================================================
    public void runInterpreter() {
        // System.out.println("Registering " + interpreters.size() + " interpreter(s) with GUI...");
        Interpreter.setInterpreterRegistry(interpreters);
        gui.setInterpreters(interpreters);
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
