package backend;
import java.util.*;

public class World {
    private ArrayList<Sprite> sprites;
    private boolean isRunning;
    public World() {
        sprites = new ArrayList<Sprite>();
        isRunning = true ;
    }

    public void addSprite(Sprite sprite)
    {
        sprites.add(sprite) ; 
    }

    //gives the list of sprites to gui 
    public ArrayList<Sprite> getSprites()
    {
        return sprites ; 
    }

    public boolean isRunning() {
        return isRunning ;
    }

    public void stopProgram()
    {
        isRunning = false ;  
    }
}
