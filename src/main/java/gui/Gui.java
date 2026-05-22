package gui;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import backend.Interpreter;
import backend.Sprite;
import backend.World;

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

    private World world;
    private Pane pane;
    private List<Interpreter> interpreters;

    // Image cache (prevents reloading every frame)
    private HashMap<String, Image> imageCache = new HashMap<>();

    public Gui(World world) 
    {
        this.world = world;
        this.interpreters = new ArrayList<Interpreter>();
    }

    public void refresh_and_draw(Stage stage) 
    {

        pane = new Pane();
        Scene scene = new Scene(pane, 480, 360);

        stage.setScene(scene);
        stage.show();

        // ensures keyboard input always works
        scene.getRoot().requestFocus();

        
        // keyboard inputs
        //How it works:
        //addEventFilter -- Writes a method for when the event passed happens
        //KeyEvent.KEY_PRESSED -- Specifies the desired event
        //new EventHandler<KeyEvent>() -- Means when key is pressed call this method
        //public void handle(KeyEvent e) -- runs the code that should happen when a key is pressed
        //world.setKeyPressed() -- sets the key pressed in world's keysPressed to true 
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

        Timeline interpreterTimeline = new Timeline(new KeyFrame(Duration.millis(50), event -> {
            for (Interpreter interpreter : interpreters) {
                interpreter.tick();
            }
        }));
        interpreterTimeline.setCycleCount(Timeline.INDEFINITE);
        interpreterTimeline.play();

        Timeline renderTimeline = new Timeline(new KeyFrame(Duration.millis(16), event -> draw(pane)));
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
    }

    public void setInterpreters(List<Interpreter> interpreters) {
        this.interpreters = interpreters;
    }

    public void drawSprite(Pane pane, Sprite sprite) 
    {

        File costume = sprite.getCurrentCostume();

        if(costume == null)
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

        view.setFitWidth(200);
        view.setFitHeight(200);

        pane.getChildren().add(view);
    }
}
