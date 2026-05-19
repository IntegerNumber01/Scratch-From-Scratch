package gui;

import java.util.*;
import java.io.File;

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

    private World world;

    public Gui(World world) {
        this.world = world;
    }

    public void refresh_and_draw(Stage stage) {
        Pane pane = new Pane();
        Scene scene = new Scene(pane, 480, 360);
        stage.setScene(scene);

        scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            world.setGuiState("keyPressed", e.getCode().toString().toLowerCase());
        });

        scene.addEventFilter(KeyEvent.KEY_RELEASED, e -> {
            world.setGuiState("keyPressed", "");
        });

        scene.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
            world.setGuiState("mouseDown", true);
        });

        scene.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> {
            world.setGuiState("mouseDown", false);
        });

        scene.addEventFilter(MouseEvent.MOUSE_MOVED, e -> {
            world.setGuiState("mouseX", (int) e.getX());
            world.setGuiState("mouseY", (int) e.getY());
        });

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                draw(pane);
            }
        };

        timer.start();
    }

    public void draw(Pane pane)
    {
        pane.getChildren().clear();

        for (Sprite sprite : world.getSprites()) {
            drawSprite(pane, sprite);
        }
    }

    public void drawSprite(Pane pane, Sprite sprite) {
        File costume = sprite.getCurrentCostume();
        if (costume == null) return;

        Image image = new Image(costume.toURI().toString());
        ImageView view = new ImageView(image);

        view.setX(sprite.getX());
        view.setY(sprite.getY());

        view.setFitWidth(200);
        view.setFitHeight(200);

        pane.getChildren().add(view);
    }
}