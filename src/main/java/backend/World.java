package backend;
import java.util.*;

public class World {
    private ArrayList<Sprite> sprites;

    public World() {
        sprites = new ArrayList<Sprite>();
    }

    //gives the list of sprites to gui 
    public ArrayList<Sprite> getSprites()
    {
        return sprites ; 
    }
}
