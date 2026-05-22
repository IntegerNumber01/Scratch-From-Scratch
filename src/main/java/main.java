import backend.*;
import gui.Gui;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class main extends Application {

    static int passed = 0;
    static int failed = 0;

    public static void main(String[] args) {
        runTests();
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        World world = new World();
        List<Interpreter> interpreters = new ArrayList<>();
        Parser parser = new Parser();

        File gameDir = new File("SBgame");
        File[] spriteFolders = gameDir.listFiles(File::isDirectory);

        if (spriteFolders == null || spriteFolders.length == 0) {
            System.out.println("No sprite folders found in SBgame/");
            return;
        }

        for (File folder : spriteFolders) {
            String spriteName = folder.getName();

            ArrayList<File> costumes = new ArrayList<>();
            File[] pngs = folder.listFiles(f -> f.getName().endsWith(".png"));
            if (pngs != null) for (File f : pngs) costumes.add(f);

            Sprite sprite = new Sprite(spriteName, costumes);
            world.addSprite(sprite);

            File scriptFile = new File(folder, "script.scratch");
            if (!scriptFile.exists()) {
                System.out.println("No script.scratch for " + spriteName + ", skipping.");
                continue;
            }

            Program program = parser.buildProgram(scriptFile);
            Interpreter interp = new Interpreter(world);
            interp.addProgram(sprite, program);
            interpreters.add(interp);

            // run the script fully before opening GUI so sprites are at correct positions
            interp.execute();

            System.out.println("Loaded: " + spriteName + " → x=" + sprite.getX() + " y=" + sprite.getY());
        }

        Gui gui = new Gui(world);
        gui.setInterpreters(interpreters);
        stage.setTitle("Scratch From Scratch");
        gui.refresh_and_draw(stage);
    }

    // ════════════════════════════════════════════════════════════════
    // ALL TESTS
    // ════════════════════════════════════════════════════════════════
    static void runTests() {
        System.out.println("=== BACKEND TESTS ===");

        testGoTo();
        testChangeXY();
        testSetXY();
        testDirection();
        testMove();
        testSize();
        testCostumes();
        testNextCostumeWraps();
        testSwitchCostume();
        testEmptyCostumes();
        testNullCostumes();
        testRandomPosition();
        testToString();

        testNewWorldEmpty();
        testAddSprite();
        testStopProgram();
        testKeyPress();
        testKeyRelease();
        testKeyCaseInsensitive();
        testUnknownKey();
        testNullKey();
        testDigitKeysStartFalse();
        testMouseDown();
        testMousePosition();
        testGlobalVariables();

        testParseGoTo();
        testParseVariableAssignment();
        testParseRepeatBlock();
        testParseNoArgCommand();
        testParseCatScriptFile();
        testParseSprite2ScriptFile();

        testCatScript();
        testSprite2Script();

        System.out.println("\n==============================");
        System.out.println("PASSED: " + passed);
        System.out.println("FAILED: " + failed);
        System.out.println("==============================\n");
    }

    // ════════════════════════════════════════════════════════════════
    // SPRITE TESTS
    // ════════════════════════════════════════════════════════════════

    static void testGoTo() {
        Sprite s = makeSprite();
        s.goTo(50, 80);
        check("goTo sets X", s.getX() == 50);
        check("goTo sets Y", s.getY() == 80);
    }

    static void testChangeXY() {
        Sprite s = makeSprite();
        s.goTo(10, 5);
        s.changeX(3);
        check("changeX adds to X", s.getX() == 13);
        s.changeY(-2);
        check("changeY adds to Y", s.getY() == 3);
    }

    static void testSetXY() {
        Sprite s = makeSprite();
        s.setX(99); s.setY(77);
        check("setX sets X", s.getX() == 99);
        check("setY sets Y", s.getY() == 77);
    }

    static void testDirection() {
        Sprite s = makeSprite();
        s.pointInDirection(90);
        check("pointInDirection sets dir", s.getDir() == 90);
        s.turnRight(45);
        check("turnRight adds to dir", s.getDir() == 135);
        s.turnLeft(60);
        check("turnLeft subtracts from dir", s.getDir() == 75);
    }

    static void testMove() {
        Sprite s = makeSprite();
        s.pointInDirection(90);
        s.move(10);
        check("move east advances X by 10", s.getX() == 10);
        check("move east does not change Y", s.getY() == 0);
    }

    static void testSize() {
        Sprite s = makeSprite();
        check("default size is 100", s.getSize() == 100);
        s.setSize(200);
        check("setSize updates size", s.getSize() == 200);
        s.setSize(100);
        s.changeSize(50);
        check("changeSize adds to size", s.getSize() == 150);
    }

    static void testCostumes() {
        Sprite s = makeSprite();
        check("first costume set on construction", s.getCurrentCostume() != null);
    }

    static void testNextCostumeWraps() {
        File a = new File("a.png"), b = new File("b.png");
        ArrayList<File> two = new ArrayList<>();
        two.add(a); two.add(b);
        Sprite s = new Sprite("X", two);
        s.nextCostume();
        check("nextCostume advances to second", s.getCurrentCostume().equals(b));
        s.nextCostume();
        check("nextCostume wraps back to first", s.getCurrentCostume().equals(a));
    }

    static void testSwitchCostume() {
        File a = new File("a.png"), b = new File("b.png");
        ArrayList<File> two = new ArrayList<>();
        two.add(a); two.add(b);
        Sprite s = new Sprite("X", two);
        s.switchCostume(b);
        check("switchCostume sets costume directly", s.getCurrentCostume().equals(b));
    }

    static void testEmptyCostumes() {
        Sprite s = new Sprite("Empty", new ArrayList<>());
        check("empty costume list → null currentCostume", s.getCurrentCostume() == null);
    }

    static void testNullCostumes() {
        Sprite s = new Sprite("Null", 0, 0, 100, null);
        check("null costume list → null currentCostume", s.getCurrentCostume() == null);
    }

    static void testRandomPosition() {
        boolean inBounds = true;
        for (int i = 0; i < 50; i++) {
            Sprite s = makeSprite();
            s.goToRandomPosition();
            if (s.getX() < 0 || s.getX() > 480 || s.getY() < 0 || s.getY() > 360) {
                inBounds = false; break;
            }
        }
        check("goToRandomPosition stays in bounds (50 tries)", inBounds);
    }

    static void testToString() {
        Sprite s = makeSprite();
        s.goTo(1, 2);
        check("toString contains name", s.toString().contains("Cat"));
        check("toString contains coords", s.toString().contains("1") && s.toString().contains("2"));
    }

    // ════════════════════════════════════════════════════════════════
    // WORLD TESTS
    // ════════════════════════════════════════════════════════════════

    static void testNewWorldEmpty() {
        World w = new World();
        check("new world has no sprites", w.getSprites().isEmpty());
        check("new world isRunning = true", w.isRunning());
    }

    static void testAddSprite() {
        World w = new World();
        w.addSprite(makeSprite());
        check("addSprite adds to list", w.getSprites().size() == 1);
    }

    static void testStopProgram() {
        World w = new World();
        w.stopProgram();
        check("stopProgram sets isRunning false", !w.isRunning());
    }

    static void testKeyPress() {
        World w = new World();
        w.setKeyPressed("a", true);
        check("key press registers true", w.getKeyPressed("a"));
    }

    static void testKeyRelease() {
        World w = new World();
        w.setKeyPressed("a", true);
        w.setKeyPressed("a", false);
        check("key release registers false", !w.getKeyPressed("a"));
    }

    static void testKeyCaseInsensitive() {
        World w = new World();
        w.setKeyPressed("A", true);
        check("key press is case insensitive", w.getKeyPressed("a"));
    }

    static void testUnknownKey() {
        World w = new World();
        check("unknown key returns false", !w.getKeyPressed("f1"));
    }

    static void testNullKey() {
        World w = new World();
        w.setKeyPressed(null, true);
        check("null key setKeyPressed does not crash", true);
        check("null key getKeyPressed returns false", !w.getKeyPressed(null));
    }

    static void testDigitKeysStartFalse() {
        World w = new World();
        boolean allFalse = true;
        for (char c = '0'; c <= '9'; c++)
            if (w.getKeyPressed(String.valueOf(c))) allFalse = false;
        check("all digit keys start false", allFalse);
    }

    static void testMouseDown() {
        World w = new World();
        check("mouse initially not down", !w.isMouseDown());
        w.setMouseDown(true);
        check("setMouseDown true", w.isMouseDown());
        w.setMouseDown(false);
        check("setMouseDown false", !w.isMouseDown());
    }

    static void testMousePosition() {
        World w = new World();
        w.setMouseX(123.4);
        check("setMouseX stores value", w.getMouseX() == 123.4);
        w.setMouseY(55.5);
        check("setMouseY stores value", w.getMouseY() == 55.5);
    }

    static void testGlobalVariables() {
        World w = new World();
        w.setGlobalVariable("score", new ScratchValue("42"));
        check("setGlobalVariable stores value", w.getGlobalVariable("score") != null);
        check("getGlobalVariable returns correct value", w.getGlobalVariable("score").toNumber() == 42.0);
        check("getGlobalVariable missing key returns null", w.getGlobalVariable("missing") == null);
    }

    // ════════════════════════════════════════════════════════════════
    // PARSER TESTS
    // ════════════════════════════════════════════════════════════════

    static void testParseGoTo() {
        Command c = Parser.parseCommand("go_to(0, 0)", 1);
        check("parseCommand: name is go_to", c.getName().equals("go_to"));
        check("parseCommand: has 2 args", c.getArgs().size() == 2);
        check("parseCommand: first arg is 0", c.getArgs().get(0).equals("0"));
    }

    static void testParseVariableAssignment() {
        Command c = Parser.parseCommand("x = 100", 1);
        check("parseCommand: assignment name is assign", c.getName().equals("assign"));
        check("parseCommand: assignment is private", c.isPrivate());
        check("parseCommand: LHS is x", c.getArgs().get(0).equals("x"));
        check("parseCommand: RHS is 100", c.getArgs().get(1).trim().equals("100"));
    }

    static void testParseRepeatBlock() {
        Command c = Parser.parseCommand("repeat(10)", 1);
        check("parseCommand: repeat is block", c.isBlock());
        check("parseCommand: repeat arg is 10", c.getArgs().get(0).equals("10"));
    }

    static void testParseNoArgCommand() {
        Command c = Parser.parseCommand("next_costume()", 1);
        check("parseCommand: next_costume has no args", c.getArgs().isEmpty());
    }

    static void testParseCatScriptFile() {
        try {
            Program prog = new Parser().buildProgram(new File("SBgame/Cat/script.scratch"));
            check("Cat script: 1 script block", prog.getScripts().size() == 1);
            check("Cat script: event is when_flag_clicked",
                prog.getScripts().get(0).getName().equals("when_flag_clicked:"));
            check("Cat script: has commands", !prog.getScripts().get(0).getCommands().isEmpty());
        } catch (Exception e) {
            check("Cat script parsed without error: " + e.getMessage(), false);
        }
    }

    static void testParseSprite2ScriptFile() {
        try {
            Program prog = new Parser().buildProgram(new File("SBgame/Sprite2/script.scratch"));
            check("Sprite2 script: 1 script block", prog.getScripts().size() == 1);
            check("Sprite2 script: has commands", !prog.getScripts().get(0).getCommands().isEmpty());
        } catch (Exception e) {
            check("Sprite2 script parsed without error: " + e.getMessage(), false);
        }
    }

    // ════════════════════════════════════════════════════════════════
    // INTERPRETER TESTS
    // ════════════════════════════════════════════════════════════════

    static void testCatScript() {
        System.out.println("\n── Cat Script ───────────────────────────────────");
        try {
            World world = new World();
            Sprite cat = new Sprite("Cat", loadCostumes("SBgame/Cat"));
            world.addSprite(cat);

            Program program = new Parser().buildProgram(new File("SBgame/Cat/script.scratch"));
            Interpreter interp = new Interpreter(world);
            interp.addProgram(cat, program);
            interp.execute();

            check("Cat: x=100 after go_to(0,0) + repeat(100) change_x(1)", cat.getX() == 100);
            check("Cat: y=0 unchanged", cat.getY() == 0);
            check("Cat: variable x stored as 100", "100".equals(program.getVariableValue("x")));
            String yVal = program.getVariableValue("y");
            check("Cat: variable y = x+10 = 110",
                yVal != null && Double.parseDouble(yVal) == 110.0);
        } catch (Exception e) {
            check("Cat script ran without error: " + e.getMessage(), false);
        }
    }

    static void testSprite2Script() {
        System.out.println("\n── Sprite2 Script ───────────────────────────────");
        try {
            World world = new World();
            Sprite sprite2 = new Sprite("Sprite2", loadCostumes("SBgame/Sprite2"));
            world.addSprite(sprite2);

            Program program = new Parser().buildProgram(new File("SBgame/Sprite2/script.scratch"));
            Interpreter interp = new Interpreter(world);
            interp.addProgram(sprite2, program);
            interp.execute();

            check("Sprite2: x=350 after set_x(250) + repeat(10) go_to(x+10,0)", sprite2.getX() == 350);
            check("Sprite2: y=0 throughout", sprite2.getY() == 0);
        } catch (Exception e) {
            check("Sprite2 script ran without error: " + e.getMessage(), false);
        }
    }

    // ════════════════════════════════════════════════════════════════
    // HELPERS
    // ════════════════════════════════════════════════════════════════

    static Sprite makeSprite() {
        ArrayList<File> costumes = new ArrayList<>();
        costumes.add(new File("SBgame/Cat/airplane.png"));
        return new Sprite("Cat", 0, 0, 100, costumes);
    }

    static ArrayList<File> loadCostumes(String folderPath) {
        ArrayList<File> costumes = new ArrayList<>();
        File[] pngs = new File(folderPath).listFiles(f -> f.getName().endsWith(".png"));
        if (pngs != null) for (File f : pngs) costumes.add(f);
        return costumes;
    }

    static void check(String name, boolean condition) {
        if (condition) {
            System.out.println("  PASS: " + name);
            passed++;
        } else {
            System.out.println("  FAIL: " + name);
            failed++;
        }
    }
}