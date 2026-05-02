package backend;
import java.util.*;

public class World {
    private ArrayList<Sprite> sprites = new ArrayList<Sprite>();

    public World() {
        
    }

    //gives the list of sprites to gui 
    public ArrayList<Sprite> getSprites()
    {
        return sprites ; 
    }
}
