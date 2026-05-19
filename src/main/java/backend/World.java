package backend;
import java.util.*;

public class World {
    private ArrayList<Sprite> sprites;
    private boolean isRunning;
    private HashMap<String, ScratchValue> globalVariables;
    private HashMap<String, Object> guiState;

    public World() {
        sprites = new ArrayList<Sprite>();
        isRunning = true;
        globalVariables = new HashMap<String, ScratchValue>();
        guiState = new HashMap<String, Object>();

        guiState.put("keyPressed", "");
        guiState.put("mouseDown", false);
        guiState.put("mouseX", 0);
        guiState.put("mouseY", 0);
    }

    public void addSprite(Sprite sprite) {
        sprites.add(sprite);
    }

    public ArrayList<Sprite> getSprites() {
        return sprites;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void stopProgram() {
        isRunning = false;
    }

    public void setGlobalVariable(String name, ScratchValue value) {
        globalVariables.put(name, value);
    }

    public ScratchValue getGlobalVariable(String name) {
        return globalVariables.get(name);
    }

    public void setGuiState(String key, Object value) {
        guiState.put(key, value);
    }

    public Object getGuiState(String key) {
        return guiState.get(key);
    }
}