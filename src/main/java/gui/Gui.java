package gui;

import java.io.File;
import java.util.HashMap;

import backend.Sprite;
import backend.World;

import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Scene;
import javafx.animation.AnimationTimer;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.event.EventHandler;

public class Gui 
{

    private World world;

    // Image cache (prevents reloading every frame)
    private HashMap<String, Image> imageCache = new HashMap<>();

    public Gui(World world) 
    {
        this.world = world;
    }

    public void refresh_and_draw(Stage stage) 
    {

        Pane pane = new Pane();
        Scene scene = new Scene(pane, 480, 360);

        stage.setScene(scene);
        stage.show();

        // IMPORTANT: ensures keyboard input always works
        scene.getRoot().requestFocus();

        
        // KEYBOARD INPUTS 
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

        // GAME LOOP
        AnimationTimer timer = new AnimationTimer() 
        {
            @Override
            public void handle(long now) 
            {
                draw(pane);
            }
        };

        timer.start();
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

    public void drawSprite(Pane pane, Sprite sprite) 
    {

        File costume = sprite.getCurrentCostume();

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