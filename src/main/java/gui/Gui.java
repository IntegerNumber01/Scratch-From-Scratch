package gui;

import java.util.*;
import java.io.*;

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

public class Gui {
    private ArrayList<Sprite> sprites;
    private World world_of_sprites;
    private AnimationTimer timer;

    public Gui(World world) {
        world_of_sprites = world;
        sprites = world.getSprites();
    }

    public void refresh_and_draw(Stage stage) {
        Pane pane = new Pane();
        Scene scene = new Scene(pane, 480, 360);
        stage.setScene(scene);

        // Key listeners
        scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            world_of_sprites.setGuiState("keyPressed", e.getCode().toString().toLowerCase());
        });

        scene.addEventFilter(KeyEvent.KEY_RELEASED, e -> {
            world_of_sprites.setGuiState("keyPressed", "");
        });

        // Mouse listeners
        scene.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
            world_of_sprites.setGuiState("mouseDown", true);
        });

        scene.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> {
            world_of_sprites.setGuiState("mouseDown", false);
        });

        scene.addEventFilter(MouseEvent.MOUSE_MOVED, e -> {
            world_of_sprites.setGuiState("mouseX", (int) e.getX());
            world_of_sprites.setGuiState("mouseY", (int) e.getY());
        });

        timer = new AnimationTimer() {
            @Override
            public void handle(long time) {
                // if (!world_of_sprites.isRunning()) {
                //     timer.stop();
                //     return;
                // }
                draw(pane);
            }
        };
        timer.start();
    }

    public void draw(Pane pane) {
        pane.getChildren().clear();
        for (Sprite sprite : sprites) {
            drawSprite(pane, sprite);
        }
    }

    public void drawSprite(Pane pane, Sprite sprite) {
    File costume = sprite.getCurrentCostume();
    if (costume == null) return;

    Image image = new Image(costume.toURI().toString());

    System.out.println(image.getWidth());
    System.out.println(image.getHeight());

    ImageView image_drawn = new ImageView(image);

    image_drawn.setX(100);
    image_drawn.setY(100);

    image_drawn.setFitWidth(200);
    image_drawn.setFitHeight(200);

    pane.getChildren().add(image_drawn);

    System.out.println("sprite added");
}
}