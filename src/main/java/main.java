import backend.Parser;
import backend.World;
import gui.Gui;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class main extends Application {

    @Override
    public void start(Stage stage) {
        Pane root = new Pane();
        Scene scene = new Scene(root, 480, 360);
        stage.setTitle("Scratch-From-Scratch");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);

        World world = new World();
        Gui gui = new Gui(world);
        Parser parser = new Parser(world);

    }
}