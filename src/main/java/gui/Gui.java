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

public class Gui
{
    private static final long INTERPRETER_BUDGET_NANOS = 1_000_000L;

    private World world; //holds the game states: sprites, inputs, etc
    private Pane pane; //for the gui so JavaFx knows where to draw the sprites
    private List<Interpreter> interpreters; //List of script runners, one per sprite
    private HashMap<String, Image> imageCache = new HashMap<>(); // Image cache (prevents reloading every frame)

    public Gui(World world)
    {
        this.world = world;
        this.interpreters = new ArrayList<Interpreter>();
    }

    public void refresh_and_draw(Stage stage)
    {

        //the window setup
        //sets window to 480x360
        pane = new Pane(); //for drawing sprites
        Scene scene = new Scene(pane, 480, 360); //takes in pane as parameter to know what it can display and also takes in width and height
        stage.setScene(scene); //stage makes scene a window
        stage.show();

        // ensures keyboard input always works
        scene.getRoot().requestFocus(); //.getRoot() just gets the thing scene is displaying then .requestFocus() just says this is the thing recieving the inputs

        //How it works:
        //addEventFilter -- Writes a method for when the event passed happens
        //KeyEvent.KEY_PRESSED -- Specifies the desired event
        //new EventHandler<KeyEvent>() -- Means when key is pressed call this method
        //public void handle(KeyEvent e) -- runs the code that should happen when a key is pressed
        //world.setKeyPressed() -- sets the key pressed in world's keysPressed to true
        //world.setMouseDown/X/Y -- sets mouses state to whatever needs to be done

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

        // MOUSE INPUTS
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
                world.setMouseX(e.getX());
                world.setMouseY(e.getY());
            }
        });

        Timeline interpreterTimeline = new Timeline(new KeyFrame(Duration.millis(50), event -> {
            for (Interpreter interpreter : interpreters) {
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

    // DRAWING - Calls drawSprite all the time
    public void draw(Pane pane)
    {
        pane.getChildren().clear();

        for (Sprite sprite : world.getSprites())
        {
            drawSprite(pane, sprite);
        }
        drawVariables(pane) ; 
    }

    public void setInterpreters(List<Interpreter> interpreters) {
        this.interpreters = interpreters;
    }

    public void drawVariables(Pane pane)
    {
        int yOffset = 10 ; 
        for(Interpreter interpreter : interpreters)
        {
            Program program = interpreter.getProgram() ; 
            if(program == null) continue ; 

            System.out.println("Visible variables: "+program.getVisibleVariables()) ; 
            
            for(String varName: program.getVisibleVariables())
            {
                String value = program.getVariableValue(varName) ;
                System.out.println("Drawing: "+varName + " = " + value) ; 
                if(value == null) continue ;
                
                String spriteName = interpreter.getSprite().getName() ; 
                javafx.scene.control.Label label = new javafx.scene.control.Label(spriteName + ": " + varName + "  " + value) ; 
                label.setLayoutX(10) ; 
                label.setLayoutY(yOffset) ; 
                label.setStyle("-fx-background-color: orange; -fx-text-fill: white; -fx-padding: 2 6;") ; 
                pane.getChildren().add(label) ; 
                yOffset += 25; 
            }
        }
    }

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

        view.setX(sprite.getX());
        view.setY(sprite.getY());

        double scale = sprite.getSize()/100.0 ;

        view.setFitWidth(200*scale);
        view.setFitHeight(200*scale);
        view.setRotate(sprite.getDir());

        pane.getChildren().add(view);

        if (!sprite.getSayText().isEmpty()) 
        {
            javafx.scene.control.Label label = new javafx.scene.control.Label(sprite.getSayText());
            label.setLayoutX(sprite.getX());
            label.setLayoutY(sprite.getY() - 30);
            label.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-padding: 4;");
            pane.getChildren().add(label);
        }

        if (!sprite.getThinkText().isEmpty()) 
        {
            javafx.scene.control.Label label = new javafx.scene.control.Label(sprite.getThinkText());
            label.setLayoutX(sprite.getX());
            label.setLayoutY(sprite.getY() - 30);
            label.setStyle("-fx-background-color: white; -fx-border-color: gray; -fx-border-style: dashed; -fx-padding: 4;");
            pane.getChildren().add(label);
        }
    }
}
