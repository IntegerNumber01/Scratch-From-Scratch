package gui;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import backend.* ;

import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Scene;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.event.EventHandler;
import javafx.util.Duration;
import javafx.scene.control.Label;

/**
 * The GUI class is responsible for all visual output and user input in the Scratch-From-Scratch engine.
 * It creates the JavaFX window, listens for keyboard and mouse events, drives the interpreter
 * tick loop, and renders all sprites and variables onto the screen each frame.
 *
 * Coordinate system: The Scratch canvas is 480x360 with the origin at the center.
 * Positive X goes right, positive Y goes up. JavaFX uses a top-left origin with Y going down,
 * so all rendering and mouse input converts between the two systems:
 * screenX = 240 + scratchX, screenY = 180 - scratchY.
 */
public class Gui
{
    /**
     * Maximum time in nanoseconds the interpreter is allowed to run per timer tick (10ms).
     * Prevents the interpreter from blocking the JavaFX thread for too long on heavy scripts.
     */
    private static final long INTERPRETER_BUDGET_NANOS = 10_000_000L;

    /** Holds the shared game state: all sprites, global variables, and input state. */
    private World world;

    /** The JavaFX Pane that sprites and labels are drawn onto each frame. */
    private Pane pane;

    /** One Interpreter per sprite (plus additional ones for clones added at runtime). */
    private List<Interpreter> interpreters;

    /**
     * Cache of loaded JavaFX Image objects keyed by file URI.
     * Prevents reloading the same image file from disk every render frame.
     */
    private HashMap<String, Image> imageCache = new HashMap<>();

    /**
     * Constructs the GUI with a reference to the shared World.
     * The interpreter list starts empty and must be populated via setInterpreters().
     *
     * @param world the World object holding all sprites and global state
     */
    public Gui(World world)
    {
        this.world = world;
        this.interpreters = new ArrayList<Interpreter>();
    }

    /**
     * Sets up the JavaFX window, registers all input event handlers, and starts
     * the interpreter and render timelines. The window is fixed at 480x360 pixels
     * to match the Scratch canvas size.
     *
     * Input handling uses addEventFilter() so events are captured before any child
     * node consumes them. Keyboard events update the World's key state. Mouse events
     * convert JavaFX screen coordinates to Scratch coordinates before storing them in the World.
     *
     * Two JavaFX Timeline objects drive the main loop. The interpreterTimeline fires every 50ms
     * and repeatedly calls Interpreter.tick() for each interpreter until it finishes or the
     * time budget (INTERPRETER_BUDGET_NANOS) runs out, which prevents forever loops from
     * blocking the UI thread. The renderTimeline fires every 100ms and calls draw() to
     * redraw all sprites and variable displays.
     *
     * @param stage the primary JavaFX Stage provided by the application entry point
     */
    public void refresh_and_draw(Stage stage)
    {
        pane = new Pane();
        Scene scene = new Scene(pane, 480, 360);
        stage.setScene(scene);
        stage.show();

        // Ensure keyboard events are received even without a focused child node.
        scene.getRoot().requestFocus();

        // KEY PRESSED — mark key as held down in the World.
        scene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>()
        {
            @Override
            public void handle(KeyEvent e)
            {
                world.setKeyPressed(e.getCode().toString().toLowerCase(), true);
            }
        });

        // KEY RELEASED — mark key as no longer held in the World.
        scene.addEventFilter(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>()
        {
            @Override
            public void handle(KeyEvent e)
            {
                world.setKeyPressed(e.getCode().toString().toLowerCase(), false);
            }
        });

        // MOUSE PRESSED — record mouse down and update position in Scratch coordinates.
        scene.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent e)
            {
                world.setMouseDown(true);
                world.setMouseX(e.getX() - 240);
                world.setMouseY(180 - e.getY());
            }
        });

        // MOUSE RELEASED — record mouse up.
        scene.addEventFilter(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent e)
            {
                world.setMouseDown(false);
            }
        });

        // MOUSE MOVED — update mouse position in Scratch coordinates (no button held).
        scene.addEventFilter(MouseEvent.MOUSE_MOVED, new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent e)
            {
                world.setMouseX(e.getX() - 240);
                world.setMouseY(180 - e.getY());
            }
        });

        // MOUSE DRAGGED — update mouse position in Scratch coordinates (button held).
        scene.addEventFilter(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent e)
            {
                world.setMouseX(e.getX() - 240);
                world.setMouseY(180 - e.getY());
            }
        });

        // Interpreter loop: runs every 50ms, advancing each interpreter within its time budget.
        Timeline interpreterTimeline = new Timeline(new KeyFrame(Duration.millis(50), event -> {
            for (Interpreter interpreter : new ArrayList<>(interpreters)) {
                long deadline = System.nanoTime() + INTERPRETER_BUDGET_NANOS;

                while (System.nanoTime() < deadline) {
                    if (!interpreter.tick()) {
                        break;
                    }
                }
            }
        }));
        interpreterTimeline.setCycleCount(Timeline.INDEFINITE);
        interpreterTimeline.play();

        // Render loop: redraws all sprites and variables every 100ms.
        Timeline renderTimeline = new Timeline(new KeyFrame(Duration.millis(100), event -> draw(pane)));
        renderTimeline.setCycleCount(Timeline.INDEFINITE);
        renderTimeline.play();
    }

    /**
     * Clears the pane and redraws every sprite and all visible variables.
     * Called by the render timeline every 100ms.
     *
     * @param pane the JavaFX Pane to draw onto
     */
    public void draw(Pane pane)
    {
        pane.getChildren().clear();

        for (Sprite sprite : world.getSprites())
        {
            drawSprite(pane, sprite);
        }

        drawVariables(pane);
    }

    /**
     * Replaces the current list of interpreters with the given list.
     * Should be called once during setup before the timelines start, and does not
     * need to be called again when clones are created since clone interpreters are
     * added directly to the same list at runtime.
     *
     * @param interpreters the list of Interpreter objects to run each tick
     */
    public void setInterpreters(List<Interpreter> interpreters) 
    {
        this.interpreters = interpreters;
    }

    /**
     * Draws all visible variable monitors onto the pane in the top-left corner.
     * Each variable is shown as an orange label displaying the sprite name,
     * variable name, and current value. Labels stack vertically with 25px spacing.
     * Variables are only shown if they have been marked visible via show_variable().
     *
     * @param pane the JavaFX Pane to add variable labels to
     */
    public void drawVariables(Pane pane)
    {
        int yOffset = 10;

        // Draw global variables from backdrop (no sprite name prefix)
        for (Interpreter interpreter : interpreters)
        {
            Sprite sprite = interpreter.getSprite();
            if (sprite.getName().equals("backdrop"))
            {
                Program program = interpreter.getProgram();
                if (program != null)
                {
                    for (String varName : program.getVisibleVariables())
                    {
                        String value = program.getVariableValue(varName);
                        if (value == null) continue;

                        Label label = new Label(varName + "  " + value);
                        label.setLayoutX(10);
                        label.setLayoutY(yOffset);
                        label.setStyle("-fx-background-color: orange; -fx-text-fill: white; -fx-padding: 2 6;");
                        pane.getChildren().add(label);
                        yOffset += 25;
                    }
                }
                break; // Only one backdrop
            }
        }

        // Draw sprite-local variables (with sprite name prefix)
        for (Interpreter interpreter : interpreters)
        {
            if (interpreter.getSprite().getName().equals("backdrop")) continue;

            Program program = interpreter.getProgram();
            if (program == null) continue;

            for (String varName : program.getVisibleVariables())
            {
                String value = program.getVariableValue(varName);
                if (value == null) continue;

                String spriteName = interpreter.getSprite().getName();
                Label label = new Label(spriteName + ": " + varName + "  " + value);
                label.setLayoutX(10);
                label.setLayoutY(yOffset);
                label.setStyle("-fx-background-color: orange; -fx-text-fill: white; -fx-padding: 2 6;");
                pane.getChildren().add(label);
                yOffset += 25;
            }
        }
    }

    /**
     * Renders a single sprite onto the pane, including its costume image and any
     * active say or think bubble. Hidden sprites are skipped entirely.
     *
     * Scratch coordinates are converted to JavaFX screen coordinates so the sprite
     * is centered on its (x, y) position using:
     * screenX = 240 + scratchX - (imageWidth * scale) / 2 and
     * screenY = 180 - scratchY - (imageHeight * scale) / 2.
     *
     * The sprite's natural image dimensions are passed back to the Sprite via
     * Sprite.setCostumeDimensions() so that mouse-touching checks can use
     * accurate bounding box sizes.
     *
     * Say bubbles are drawn with a solid black border; think bubbles use a
     * dashed gray border. Both appear just above the sprite's top-left corner.
     *
     * @param pane   the JavaFX Pane to add the sprite's ImageView and labels to
     * @param sprite the Sprite to render
     */
    public void drawSprite(Pane pane, Sprite sprite)
    {
        File costume = sprite.getCurrentCostume();

        if (costume == null || sprite.isHidden())
        {
            return;
        }

        String path = costume.toURI().toString();

        // Load from cache, or read from disk and cache for next frame.
        Image image = imageCache.get(path);
        if (image == null)
        {
            image = new Image(path);
            imageCache.put(path, image);
        }

        ImageView view = new ImageView(image);

        double scale = sprite.getSize() / 100.0;
        double imgWidth = image.getWidth();
        double imgHeight = image.getHeight();

        // Convert Scratch coords to screen coords, centering the image on the sprite's position.
        view.setX(240 + sprite.getX() - (imgWidth * scale) / 2);
        view.setY(180 - sprite.getY() - (imgHeight * scale) / 2);

        // Pass the natural image size back to the sprite for bounding-box calculations.
        sprite.setCostumeDimensions(imgWidth, imgHeight);

        view.setFitWidth(imgWidth * scale);
        view.setFitHeight(imgHeight * scale);
        view.setRotate(sprite.getDir());

        pane.getChildren().add(view);

        // Draw say bubble (solid black border) above the sprite.
        if (!sprite.getSayText().isEmpty())
        {
            Label label = new Label(sprite.getSayText());
            label.setLayoutX(240 + sprite.getX() - (imgWidth * scale) / 2);
            label.setLayoutY(180 - sprite.getY() - (imgHeight * scale) / 2 - 30);
            label.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-padding: 4;");
            pane.getChildren().add(label);
        }

        // Draw think bubble (dashed gray border) above the sprite.
        if (!sprite.getThinkText().isEmpty())
        {
            Label label = new Label(sprite.getThinkText());
            label.setLayoutX(240 + sprite.getX() - (imgWidth * scale) / 2);
            label.setLayoutY(180 - sprite.getY() - (imgHeight * scale) / 2 - 30);
            label.setStyle("-fx-background-color: white; -fx-border-color: gray; -fx-border-style: dashed; -fx-padding: 4;");
            pane.getChildren().add(label);
        }
    }
}