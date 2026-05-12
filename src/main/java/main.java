import java.io.File;
import java.io.FileNotFoundException;

import backend.Parser;
import backend.World;
import gui.Gui;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class main extends Application {
    private World world;
    private Gui gui;
    private Parser parser;

    @Override
    public void start(Stage stage) {
        world = new World();
        gui = new Gui(world);
        parser = new Parser(world);

        try {
            parser.readFile(new File("SBGame/Sprite1/script.scratch"));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Pane root = new Pane();
        Scene scene = new Scene(root, 480, 360);
        stage.setTitle("Scratch-From-Scratch");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}