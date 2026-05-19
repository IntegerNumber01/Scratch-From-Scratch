import backend.World;
import backend.Sprite;
import backend.ScratchValue;
import gui.Gui;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.File;
import java.util.*;

public class GuiTest extends Application {

    private World world;
    private ArrayList<File> costumes;

    @Override
    public void start(Stage stage) {

        setup();

        testGuiStateDefaults();

        testSetGuiState();

        testKeyReleased();

        testIsRunning();

        testSprites();

        testSpriteMovement();

        testSpriteDirection();

        testGlobalVariables();

        testGuiRendering(stage);
    }

    public void setup() {

        world = new World();

        costumes = new ArrayList<File>();

        File airplane = new File("SBgame/Cat/airplane.png");

        System.out.println("Image exists: " + airplane.exists());
        System.out.println("Absolute path: " + airplane.getAbsolutePath());

        costumes.add(airplane);
    }

    public void testGuiStateDefaults() {

        System.out.println("\n=== Test 1: guiState defaults ===");

        System.out.println("keyPressed: '" + world.getGuiState("keyPressed") + "' (expected: '')");

        System.out.println("mouseDown: " + world.getGuiState("mouseDown") + " (expected: false)");

        System.out.println("mouseX: " + world.getGuiState("mouseX") + " (expected: 0)");

        System.out.println("mouseY: " + world.getGuiState("mouseY") + " (expected: 0)");
    }

    public void testSetGuiState() {

        System.out.println("\n=== Test 2: setGuiState / getGuiState ===");

        world.setGuiState("keyPressed", "a");

        System.out.println("keyPressed: '" + world.getGuiState("keyPressed") + "' (expected: 'a')");

        world.setGuiState("mouseDown", true);

        System.out.println("mouseDown: " + world.getGuiState("mouseDown") + " (expected: true)");

        world.setGuiState("mouseX", 240);

        world.setGuiState("mouseY", 180);

        System.out.println("mouseX: " + world.getGuiState("mouseX") + " (expected: 240)");

        System.out.println("mouseY: " + world.getGuiState("mouseY") + " (expected: 180)");
    }

    public void testKeyReleased() {

        System.out.println("\n=== Test 3: keyPressed clears on release ===");

        world.setGuiState("keyPressed", "");

        System.out.println("keyPressed: '" + world.getGuiState("keyPressed") + "' (expected: '')");
    }

    public void testIsRunning() {

        System.out.println("\n=== Test 4: isRunning ===");

        System.out.println("isRunning: " + world.isRunning() + " (expected: true)");

        world.stopProgram();

        System.out.println("isRunning after stop: " + world.isRunning() + " (expected: false)");
    }

    public void testSprites() {

        System.out.println("\n=== Test 5: addSprite / getSprites ===");

        Sprite cat = new Sprite("Cat", costumes);

        world.addSprite(cat);

        System.out.println("Sprite count: " + world.getSprites().size() + " (expected: 1)");

        System.out.println("Sprite 0: " + world.getSprites().get(0).getName() + " (expected: Cat)");
    }

    public void testSpriteMovement() {

        System.out.println("\n=== Test 6: Sprite position and movement ===");

        Sprite sprite = new Sprite("Cat", 50, 75, 100, costumes);

        System.out.println("x: " + sprite.getX() + " (expected: 50)");

        System.out.println("y: " + sprite.getY() + " (expected: 75)");

        sprite.setX(200);

        sprite.setY(300);

        System.out.println("x after setX: " + sprite.getX() + " (expected: 200)");

        System.out.println("y after setY: " + sprite.getY() + " (expected: 300)");

        sprite.changeX(10);

        sprite.changeY(20);

        System.out.println("x after changeX(10): " + sprite.getX() + " (expected: 210)");

        System.out.println("y after changeY(20): " + sprite.getY() + " (expected: 320)");

        sprite.goTo(0, 0);

        System.out.println("x after goTo(0,0): " + sprite.getX() + " (expected: 0)");

        System.out.println("y after goTo(0,0): " + sprite.getY() + " (expected: 0)");
    }

    public void testSpriteDirection() {

        System.out.println("\n=== Test 7: Sprite direction ===");

        Sprite sprite = new Sprite("Cat", costumes);

        System.out.println("dir default: " + sprite.getDir() + " (expected: 0)");

        sprite.turnRight(90);

        System.out.println("dir after turnRight(90): " + sprite.getDir() + " (expected: 90)");

        sprite.turnLeft(45);

        System.out.println("dir after turnLeft(45): " + sprite.getDir() + " (expected: 45)");

        sprite.pointInDirection(180);

        System.out.println("dir after pointInDirection(180): " + sprite.getDir() + " (expected: 180)");
    }

    public void testGlobalVariables() {

        System.out.println("\n=== Test 8: globalVariables ===");

        World world2 = new World();

        world2.setGlobalVariable("score", new ScratchValue("42"));

        world2.setGlobalVariable("name", new ScratchValue("player"));

        System.out.println("score: " + world2.getGlobalVariable("score") + " (expected: 42)");

        System.out.println("name: " + world2.getGlobalVariable("name") + " (expected: player)");
    }

    public void testGuiRendering(Stage stage) {

        System.out.println("\n=== Test 9: Gui rendering ===");

        Sprite cat = new Sprite("Cat", costumes);

        cat.setX(100);

        cat.setY(100);

        world.addSprite(cat);

        Gui gui = new Gui(world);

        stage.setTitle("Gui Test");

        stage.show();

        gui.refresh_and_draw(stage);

        System.out.println("Gui launched successfully");
    }

    public static void main(String[] args) {
        launch(args);
    }
}