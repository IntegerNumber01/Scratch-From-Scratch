import backend.World;
import backend.Sprite;
import backend.ScratchValue;
import gui.Gui;
import javafx.application.Application;
import javafx.stage.Stage;
import java.io.File;
import java.util.*;

public class GuiTest extends Application {

    @Override
    public void start(Stage stage) {
        World world = new World();

        // --- Test 1: guiState defaults ---
        System.out.println("=== Test 1: guiState defaults ===");
        System.out.println("keyPressed: '" + world.getGuiState("keyPressed") + "' (expected: '')");
        System.out.println("mouseDown: " + world.getGuiState("mouseDown") + " (expected: false)");
        System.out.println("mouseX: " + world.getGuiState("mouseX") + " (expected: 0)");
        System.out.println("mouseY: " + world.getGuiState("mouseY") + " (expected: 0)");

        // --- Test 2: setGuiState / getGuiState ---
        System.out.println("\n=== Test 2: setGuiState / getGuiState ===");
        world.setGuiState("keyPressed", "a");
        System.out.println("keyPressed: '" + world.getGuiState("keyPressed") + "' (expected: 'a')");
        world.setGuiState("mouseDown", true);
        System.out.println("mouseDown: " + world.getGuiState("mouseDown") + " (expected: true)");
        world.setGuiState("mouseX", 240);
        world.setGuiState("mouseY", 180);
        System.out.println("mouseX: " + world.getGuiState("mouseX") + " (expected: 240)");
        System.out.println("mouseY: " + world.getGuiState("mouseY") + " (expected: 180)");

        // --- Test 3: key released clears keyPressed ---
        System.out.println("\n=== Test 3: keyPressed clears on release ===");
        world.setGuiState("keyPressed", "");
        System.out.println("keyPressed: '" + world.getGuiState("keyPressed") + "' (expected: '')");

        // --- Test 4: isRunning default ---
        System.out.println("\n=== Test 4: isRunning ===");
        System.out.println("isRunning: " + world.isRunning() + " (expected: true)");
        world.stopProgram();
        System.out.println("isRunning after stop: " + world.isRunning() + " (expected: false)");

        // --- Test 5: addSprite / getSprites ---
        System.out.println("\n=== Test 5: sprites ===");
        ArrayList<File> a = new ArrayList<File>();
        a.add(new File("../../../SBgame/Cat/airplane.png")) ; 
        // ArrayList<File> b = new ArrayList<File>() ; 
        Sprite s1 = new Sprite("Cat", a);
        // Sprite s2 = new Sprite("Sprite2", b);
        world.addSprite(s1);
        // world.addSprite(s2);
        // System.out.println("Sprite count: " + world.getSprites().size() + " (expected: 2)");
        // System.out.println("Sprite 0: " + world.getSprites().get(0).getName() + " (expected: Cat)");
        // System.out.println("Sprite 1: " + world.getSprites().get(1).getName() + " (expected: Sprite2)");

        // --- Test 6: Sprite position and movement ---
        // System.out.println("\n=== Test 6: Sprite position and movement ===");
        // Sprite s3 = new Sprite("Cat", 50, 75, 100,a);
        // System.out.println("x: " + s3.getX() + " (expected: 50)");
        // System.out.println("y: " + s3.getY() + " (expected: 75)");
        // s3.setX(200);
        // s3.setY(300);
        // System.out.println("x after setX: " + s3.getX() + " (expected: 200)");
        // System.out.println("y after setY: " + s3.getY() + " (expected: 300)");
        // s3.changeX(10);
        // s3.changeY(20);
        // System.out.println("x after changeX(10): " + s3.getX() + " (expected: 210)");
        // System.out.println("y after changeY(20): " + s3.getY() + " (expected: 320)");
        // s3.goTo(0, 0);
        // System.out.println("x after goTo(0,0): " + s3.getX() + " (expected: 0)");
        // System.out.println("y after goTo(0,0): " + s3.getY() + " (expected: 0)");

        // --- Test 7: Sprite direction ---
        // System.out.println("\n=== Test 7: Sprite direction ===");
        // Sprite s4 = new Sprite("Cat",a);
        // System.out.println("dir default: " + s4.getDir() + " (expected: 0)");
        // s4.turnRight(90);
        // System.out.println("dir after turnRight(90): " + s4.getDir() + " (expected: 90)");
        // s4.turnLeft(45);
        // System.out.println("dir after turnLeft(45): " + s4.getDir() + " (expected: 45)");
        // s4.pointInDirection(180);
        // System.out.println("dir after pointInDirection(180): " + s4.getDir() + " (expected: 180)");

        // --- Test 8: globalVariables ---
        // System.out.println("\n=== Test 8: globalVariables ===");
        // World world2 = new World();
        // world2.setGlobalVariable("score", new ScratchValue("42"));
        // world2.setGlobalVariable("name", new ScratchValue("player"));
        // System.out.println("score: " + world2.getGlobalVariable("score") + " (expected: 42)");
        // System.out.println("name: " + world2.getGlobalVariable("name") + " (expected: player)");

        // --- Test 9: Gui renders Cat with airplane.png ---
        // System.out.println("\n=== Test 9: Gui renders with sprites ===");
        // Sprite cat = new Sprite("Cat",a);
        // cat.addCostume(new File("SBgame/Cat/airplane.png"));
        // cat.setX(100);
        // cat.setY(150);

        world.addSprite(s1);

        Gui gui = new Gui(world);
        stage.setTitle("GuiTest - Cat/airplane.png should appear");
        stage.show();
        gui.refresh_and_draw(stage);
        System.out.println("Gui launched successfully (expected: window visible with airplane costume)");
    }

    public static void main(String[] args) {
        launch(args);
    }
}