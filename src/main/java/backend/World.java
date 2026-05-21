package backend;

import java.util.*;

public class World {

    private ArrayList<Sprite> sprites;
    private boolean isRunning;

    private HashMap<String, ScratchValue> globalVariables;
    private HashMap<String, Boolean> keysPressed;

    private boolean mouseDown;
    private double mouseX;
    private double mouseY;

    public World() {
        sprites = new ArrayList<>();
        isRunning = true;

        globalVariables = new HashMap<>();

        keysPressed = new HashMap<>();
        initKeys();

        mouseDown = false;
        mouseX = 0;
        mouseY = 0;
    }

    // sets the values of the HashMaps to the characters on the keyboard and false since they haven't been pressed yet
    private void initKeys() {

        // a - z
        for (char c = 'a'; c <= 'z'; c++) {
            keysPressed.put(String.valueOf(c), false);
        }

        // 0 - 9
        for (char c = '0'; c <= '9'; c++) {
            keysPressed.put(String.valueOf(c), false);
        }
    }

    // adds a new sprite object 
    public void addSprite(Sprite sprite) {
        sprites.add(sprite);
    }

    public ArrayList<Sprite> getSprites() {
        return sprites;
    }

    // returns the state of the gui
    public boolean isRunning() {
        return isRunning;
    }

    public void stopProgram() {
        isRunning = false;
    }

    // ======================
    // GLOBAL VARIABLES
    // ======================
    public void setGlobalVariable(String name, ScratchValue value) {
        globalVariables.put(name, value);
    }

    public ScratchValue getGlobalVariable(String name) {
        return globalVariables.get(name);
    }

    // keyboard inputs
    public void setKeyPressed(String key, boolean value) 
    {
        if (key == null) return;

        key = key.toLowerCase();

        if (keysPressed.containsKey(key)) 
        {
            keysPressed.put(key, value);
        }
    }

    public boolean getKeyPressed(String key) 
    {
        if (key == null) return false;

        key = key.toLowerCase();

        return keysPressed.getOrDefault(key, false);
    }

    // mouse inputs
    public void setMouseDown(boolean value) 
    {
        mouseDown = value;
    }

    public boolean isMouseDown() 
    {
        return mouseDown;
    }

    public void setMouseX(double x) 
    {
        mouseX = x;
    }

    public void setMouseY(double y) 
    {
        mouseY = y;
    }

    public double getMouseX() 
    {
        return mouseX;
    }

    public double getMouseY() 
    {
        return mouseY;
    }
}