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
import javafx.scene.control.Label ; 

public class Gui
{
    private static final long INTERPRETER_BUDGET_NANOS = 1_000_000L;

    private World world; //holds the game states: sprites, inputs, etc
    private Pane pane; //for the gui so JavaFx knows where to draw the sprites
    private List<Interpreter> interpreters; //List of script runners, one per sprite
    private HashMap<String, Image> imageCache = new HashMap<>(); // Image cache (prevents reloading every frame)

    /**
     * 
     * @param world
     * Constructor that takes in a World object to get all the sprites
     * Also initializes the interpreters for the sprites
     */
    public Gui(World world)
    {
        this.world = world;
        this.interpreters = new ArrayList<Interpreter>();
    }

    /**
     * 
     * @param stage
     * Does the window setup (480x360)
     * 
     * Calls every mouse input and keyboard input 
     * How it works:
     * addEventFilter -- Writes a method for when the event passed happens
     * KeyEvent.KEY_PRESSED -- Specifies the desired event
     * new EventHandler<KeyEvent>() -- Means when key is pressed call this method
     * public void handle(KeyEvent e) -- runs the code that should happen when a key is pressed
     * world.setKeyPressed() -- sets the key pressed in world's keysPressed to true
     * world.setMouseDown/X/Y -- sets mouses state to whatever needs to be done
     * 
     * 
     * Also has a TimeLine feature to keep the game at a slow rate 
     * TimeLine interpreterTimeLine
     *  Creates a JavaFx timer that fires every 50ms. 
     *  Every 50ms the code inside the {} runs
     *  Makes a copy of the list of interpreters before running
     *  deadline makes sure that interpreter runs very fast for each 50ms so that way forever loops and other thing wont slow it down
     *  calls interpreter.tick() so that interpreter doesnt run all the commands at once it does it step by step for every deadline
     * 
     * TimeLine renderTimeLine
     *  Every 100ms calls draw(pane)
     *  setCycleCount runs the TimeLine object to run forever
     *  .play() stats the timer
     * 
     * Calls draw in this method which does the drawing of every sprite
     * 
     */
    public void refresh_and_draw(Stage stage)
    {

        //the window setup
        //sets window to 480x360
        pane = new Pane(); //for drawing sprites
        Scene scene = new Scene(pane, 480, 360); //takes in pane as parameter to know where it can display and also takes in width and height
        stage.setScene(scene); //stage makes scene a window
        stage.show();

        // ensures keyboard input always works
        //.getRoot() just gets the thing scene is displaying then .requestFocus() just says this is the thing recieving the inputs
        scene.getRoot().requestFocus(); 

        // keyboard inputs
        scene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>()
        {
            @Override
            public void handle(KeyEvent e)
            {
                world.setKeyPressed(e.getCode().toString().toLowerCase(),true);
            }
        });

        scene.addEventFilter(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>()
        {
            @Override
            public void handle(KeyEvent e)
            {
                world.setKeyPressed(e.getCode().toString().toLowerCase(),false);
            }
        });

        // mouse inputs
        scene.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent e)
            {
                world.setMouseDown(true);
            }
        });

        scene.addEventFilter(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent e)
            {
                world.setMouseDown(false);
            }
        });

        scene.addEventFilter(MouseEvent.MOUSE_MOVED, new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent e)
            {
                world.setMouseX(e.getX());
                world.setMouseY(e.getY());
            }
        });

        scene.addEventFilter(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent e)
            {
                world.setMouseX(e.getX() - 240);
                world.setMouseY(180 - e.getY());
            }
        });

        
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

        Timeline renderTimeline = new Timeline(new KeyFrame(Duration.millis(100), event -> draw(pane)));
        renderTimeline.setCycleCount(Timeline.INDEFINITE);
        renderTimeline.play();
    }

    /**
     * 
     * @param pane
     * Takes in a pane object so it knows where to draw
     * Clears the entire pane first
     * Calls drawSprite so every sprite in the world object is drawn
     * Calls drawVariables so it draws the variables onto pane
     */
    public void draw(Pane pane)
    {
        pane.getChildren().clear();

        for (Sprite sprite : world.getSprites())
        {
            drawSprite(pane, sprite);
        }
        drawVariables(pane) ;
    }

    /**
     * 
     * @param interpreters
     * Sets the list of interpreters for the sprites
     */
    public void setInterpreters(List<Interpreter> interpreters) {
        this.interpreters = interpreters;
    }

    /**
     * 
     * @param pane
     * Loops through the interpreters
     * Gets the sprites programs
     * Loops over the visible variables contained in a HashSet in each sprites program, gets the value
     * If the value isnt null then creates a string to put onto the gui
     * Positions it accordingly 
     * Styles it to the color orange 
     */
    public void drawVariables(Pane pane)
    {
        int yOffset = 10 ;
        for(Interpreter interpreter : interpreters)
        {
            Program program = interpreter.getProgram() ;
            if(program == null) continue ;

            for(String varName: program.getVisibleVariables())
            {
                String value = program.getVariableValue(varName) ;
                if(value == null) continue ;

                String spriteName = interpreter.getSprite().getName() ;
                Label label = new Label(spriteName + ": " + varName + "  " + value) ;
                label.setLayoutX(10) ;
                label.setLayoutY(yOffset) ;
                label.setStyle("-fx-background-color: orange; -fx-text-fill: white; -fx-padding: 2 6;") ;
                pane.getChildren().add(label) ;
                yOffset += 25;
            }
        }
    }

    /**
     * 
     * @param pane
     * @param sprite
     * Takes in a pane and a sprite
     * Gets the sprites costume checks to see if its a real costume or not
     * Creates path to the file through the project. Ex. SBgame/Cat/airplane.png
     * Puts it into the image cache if already not present 
     * Creates an ImageView object for the image so it can actually be shown
     * Creates a scale according to the users desired size for the sprite object
     * Translates into java coordinates and sets the images dinmensions properly
     * Does the sayText and thinkText dialogue and sets its coordinates properly
     * Sets its color to black and grey and the boxes to different outlines so its easy to differentiate
     */
    public void drawSprite(Pane pane, Sprite sprite)
    {

        File costume = sprite.getCurrentCostume();

        if(costume == null||sprite.isHidden())
        {
            return ;
        }

        //Path to png file
        String path = costume.toURI().toString();

        // caches image
        Image image = imageCache.get(path);

        //caches image
        if (image == null)
        {
            image = new Image(path);
            imageCache.put(path, image);
        }

        ImageView view = new ImageView(image);

        double scale = sprite.getSize()/100.0 ;

        view.setX(240 + sprite.getX() - 100 * scale);
        view.setY(180 - sprite.getY() - 100 * scale);


        view.setFitWidth(200*scale);
        view.setFitHeight(200*scale);
        view.setRotate(sprite.getDir());

        pane.getChildren().add(view);

        if (!sprite.getSayText().isEmpty())
        {
            Label label = new Label(sprite.getSayText());
            label.setLayoutX(240 + sprite.getX() - 100 * scale);
            label.setLayoutY(180 - sprite.getY() - 130 * scale);
            label.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-padding: 4;");
            pane.getChildren().add(label);
        }

        if (!sprite.getThinkText().isEmpty())
        {
            Label label = new Label(sprite.getThinkText());
            label.setLayoutX(240 + sprite.getX() - 100 * scale);
            label.setLayoutY(180 - sprite.getY() - 130 * scale);
            label.setStyle("-fx-background-color: white; -fx-border-color: gray; -fx-border-style: dashed; -fx-padding: 4;");
            pane.getChildren().add(label);
        }
    }
}
