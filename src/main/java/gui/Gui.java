package gui;

import java.util.ArrayList;

import backend.Sprite;
import backend.World;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Scene;
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
    private ArrayList<Sprite> sprites ;
    private World world_of_sprites ;

    public Gui(World world)
    {
        world_of_sprites = world; 
        sprites = world.getSprites(); 
    }

    //use the other helper methods to make the whole gui work
    public void refresh_and_draw(Stage stage)
    {   
        //makes the whole window that will be created accessible
        Pane pane = new Pane() ; 
        Scene scene = new Scene(pane,480,360) ; 
        stage.setScene(scene) ; 

        //runs the constant refreshes over and over 
        AnimationTimer timer = new AnimationTimer() 
        {
            @Override
            public void handle(long time) 
            {
                if(world_of_sprites.end)
                {
                    timer.stop() ; 
                    return ; 
                }
                draw(pane);
            }
        };
        timer.start();
    }

    //draws all sprites
    public void draw(Pane pane)
    {
        //erases pane then calls the draw one sprite method for every sprite in the world obj
        pane.getChildren().clear() ; 
        for(Sprite sprite: sprites)
        {
            drawSprite(pane, sprite) ; 
        }
    }

    // draws one sprite
    public void drawSprite(Pane pane, Sprite sprite)
    {
        //makes an image based off the file name then prepares it for drawing
        Image image = new Image(sprite.getName()) ; 
        ImageView image_drawn = new ImageView(image) ; 

        //sets x and y to sprites given x and y
        image_drawn.setX(sprite.getX()) ; 
        image_drawn.setY(sprite.getY()) ; 

        //adds it onto pane 
        pane.getChildren().add(image_drawn) ; 
        
    }
    
}