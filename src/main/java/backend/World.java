package backend;
import java.util.*;

public class World {
    private ArrayList<Sprite> sprites;
    private boolean end ; 

    public World() {
        sprites = new ArrayList<Sprite>();
        end = false ; 
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

    public boolean getEnd() {
        return end ; 
    }

    public void setEnd()
    {
        end = true ;  
    }

    
}
