package backend;
import java.util.*;

public class World {
    private ArrayList<Sprite> sprites;
    private boolean isRunning;
    private HashMap<String, ScratchValue> globalVariables;

    public World() {
        sprites = new ArrayList<Sprite>();
        isRunning = true ;
        globalVariables = new HashMap<String, ScratchValue>();
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

    public void setGlobalVariable(String name, ScratchValue value) {
        globalVariables.put(name, value);
    }

    public ScratchValue getGlobalVariable(String name) {
        return globalVariables.get(name);
    }
}
