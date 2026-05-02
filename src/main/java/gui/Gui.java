package src.main.java.gui;

import java.util.ArrayList;

import backend.Sprite;
import backend.World;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class Gui  
{
    private ArrayList<Sprites> sprites ;

    public Gui(World world)
    {
        sprites = world.getSprites(); 
    }

    public void refresh_and_draw(Stage stage)
    {   
        //makes the whole window that will be created accessible
        Pane pane = new Pane() ; 
        Scene scene = new Scene(pane,480,360) ; 
        stage.setScene(pane) ; 

        //runs the constant refreshes over and over 
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

    public void draw()
    {
        for(Sprite sprite: sprites)
        {
            drawSprite(pane, sprite) ; 
        }
    }

    public void drawSprite(Pane pane, Sprite sprite)
    {
        
    }
    
}