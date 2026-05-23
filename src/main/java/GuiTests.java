import backend.World;
import backend.Sprite;
import gui.Gui;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.File;
import java.util.*;

public class GuiTests extends Application {

    private World world;
    private ArrayList<File> costumes;

    @Override
    public void start(Stage stage) {

        setup();

        testInputDefaults();

        testKeyboardInput();

        testMouseInput();

        testIsRunning();

        testSprites();

        testSpriteMovement();

        testSpriteDirection();

        // testGlobalVariables();

        testGuiRendering(stage);

        testEraseWorks(stage);
    }

    // ======================
    // SETUP
    // ======================
    public void setup() {

        world = new World();
        costumes = new ArrayList<>();

        File airplane = new File("SBgame/Cat/airplane.png");

        System.out.println("Image exists: " + airplane.exists());
        System.out.println("Absolute path: " + airplane.getAbsolutePath());

        costumes.add(airplane);
    }

    // ======================
    // INPUT TESTS
    // ======================

    public void testInputDefaults() {

        System.out.println("\n=== Test 1: Input defaults ===");

        System.out.println("key 'a': " + world.getKeyPressed("a") + " (expected: false)");
        System.out.println("mouseDown: " + world.isMouseDown() + " (expected: false)");
        System.out.println("mouseX: " + world.getMouseX() + " (expected: 0)");
        System.out.println("mouseY: " + world.getMouseY() + " (expected: 0)");
    }

    public void testKeyboardInput() {

        System.out.println("\n=== Test 2: Keyboard input ===");

        world.setKeyPressed("a", true);
        System.out.println("key 'a' pressed: " + world.getKeyPressed("a") + " (expected: true)");

        world.setKeyPressed("a", false);
        System.out.println("key 'a' released: " + world.getKeyPressed("a") + " (expected: false)");
    }

    public void testMouseInput() {

        System.out.println("\n=== Test 3: Mouse input ===");

        world.setMouseDown(true);
        System.out.println("mouseDown: " + world.isMouseDown() + " (expected: true)");

        world.setMouseX(240);
        world.setMouseY(180);

        System.out.println("mouseX: " + world.getMouseX() + " (expected: 240)");
        System.out.println("mouseY: " + world.getMouseY() + " (expected: 180)");

        world.setMouseDown(false);
        System.out.println("mouseDown after release: " + world.isMouseDown() + " (expected: false)");
    }

    // ======================
    // GAME STATE
    // ======================

    public void testIsRunning() {

        System.out.println("\n=== Test 4: isRunning ===");

        System.out.println("isRunning: " + world.isRunning() + " (expected: true)");

        world.stopProgram();

        System.out.println("isRunning after stop: " + world.isRunning() + " (expected: false)");
    }

    // ======================
    // SPRITES
    // ======================

    public void testSprites() {

        System.out.println("\n=== Test 5: Sprites ===");

        Sprite cat = new Sprite("Cat", costumes);

        world.addSprite(cat);

        System.out.println("Sprite count: " + world.getSprites().size() + " (expected: 1)");
        System.out.println("Sprite name: " + world.getSprites().get(0).getName() + " (expected: Cat)");
    }

    // ======================
    // MOVEMENT
    // ======================

    public void testSpriteMovement() {

        System.out.println("\n=== Test 6: Sprite movement ===");

        Sprite sprite = new Sprite("Cat", 50, 75, 100, costumes);

        System.out.println("x: " + sprite.getX() + " (expected: 50)");
        System.out.println("y: " + sprite.getY() + " (expected: 75)");

        sprite.setX(200);
        sprite.setY(300);

        System.out.println("x after setX: " + sprite.getX() + " (expected: 200)");
        System.out.println("y after setY: " + sprite.getY() + " (expected: 300)");

        sprite.changeX(10);
        sprite.changeY(20);

        System.out.println("x after changeX: " + sprite.getX() + " (expected: 210)");
        System.out.println("y after changeY: " + sprite.getY() + " (expected: 320)");

        sprite.goTo(0, 0);

        System.out.println("x after goTo: " + sprite.getX() + " (expected: 0)");
        System.out.println("y after goTo: " + sprite.getY() + " (expected: 0)");
    }

    // ======================
    // DIRECTION
    // ======================

    public void testSpriteDirection() {

        System.out.println("\n=== Test 7: Sprite direction ===");

        Sprite sprite = new Sprite("Cat", costumes);

        System.out.println("default dir: " + sprite.getDir() + " (expected: 0)");

        sprite.turnRight(90);
        System.out.println("after turnRight: " + sprite.getDir() + " (expected: 90)");

        sprite.turnLeft(45);
        System.out.println("after turnLeft: " + sprite.getDir() + " (expected: 45)");

        sprite.pointInDirection(180);
        System.out.println("after pointInDirection: " + sprite.getDir() + " (expected: 180)");
    }

    // ======================
    // VARIABLES
    // ======================

    // public void testGlobalVariables() {

    //     System.out.println("\n=== Test 8: Global variables ===");

    //     World w = new World();

    //     w.setGlobalVariable("score", new ScratchValue("42"));
    //     w.setGlobalVariable("name", new ScratchValue("player"));

    //     System.out.println("score: " + w.getGlobalVariable("score") + " (expected: 42)");
    //     System.out.println("name: " + w.getGlobalVariable("name") + " (expected: player)");
    // }

    // ======================
    // GUI TESTS
    // ======================

    public void testGuiRendering(Stage stage) {

        System.out.println("\n=== Test 9: GUI rendering ===");

        Sprite cat = new Sprite("Cat", costumes);

        cat.setX(100);
        cat.setY(100);

        world.addSprite(cat);

        Gui gui = new Gui(world);

        stage.setTitle("Gui Test");
        gui.refresh_and_draw(stage);

        System.out.println("GUI launched successfully");
    }

    public void testEraseWorks(Stage stage) {

        System.out.println("\n=== Test 10: Movement animation ===");

        world = new World();

        Sprite cat = new Sprite("Cat", costumes);

        cat.setX(0);
        cat.setY(100);

        world.addSprite(cat);

        Gui gui = new Gui(world);

        stage.setTitle("Movement Test");
        gui.refresh_and_draw(stage);

        new Thread(() -> {
            try {
                while (cat.getX() < 400) {
                    Thread.sleep(100);
                    cat.changeX(20);
                    System.out.println("Cat X: " + cat.getX());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}