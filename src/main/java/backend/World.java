package backend;

import java.util.*;

public class World {
    private ArrayList<Sprite> sprites;
    private boolean isRunning;

    private HashMap<String, String> globalVariables;
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
    public void setGlobalVariable(String name, String value) {
        globalVariables.put(name, value);
    }

    public void addGlobalVariables(HashMap<String, String> variables) {
        globalVariables.putAll(variables);
    }

    public HashMap<String, String> getGlobalVariables() {
        return globalVariables;
    }

    public boolean hasGlobalVariable(String name) {
        return globalVariables.containsKey(name);
    }

    public String getGlobalVariable(String name) {
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

    public List<Sprite[]> isTouching() {
        List<Sprite[]> touching = new ArrayList<>();

        for (int i = 0; i < sprites.size(); i++) {
            for (int j = i + 1; j < sprites.size(); j++) {
                Sprite a = sprites.get(i);
                Sprite b = sprites.get(j);

                if (spritesOverlap(a, b)) {
                    touching.add(new Sprite[]{a, b});
                }
            }
        }

        return touching;
    }

    private boolean spritesOverlap(Sprite a, Sprite b) {
        double aScale = a.getSize() / 100.0;
        double bScale = b.getSize() / 100.0;

        int aW = (int)(200 * aScale);
        int aH = (int)(200 * aScale);
        int bW = (int)(200 * bScale);
        int bH = (int)(200 * bScale);

        // find overlapping rectangle
        int overlapX1 = (int) Math.max(a.getX(), b.getX());
        int overlapY1 = (int) Math.max(a.getY(), b.getY());
        int overlapX2 = (int) Math.min(a.getX() + aW, b.getX() + bW);
        int overlapY2 = (int) Math.min(a.getY() + aH, b.getY() + bH);

        // no overlap at all
        if (overlapX1 >= overlapX2 || overlapY1 >= overlapY2) return false;

        // load pixel data
        javafx.scene.image.Image imgA = new javafx.scene.image.Image(a.getCurrentCostume().toURI().toString(), aW, aH, false, false);
        javafx.scene.image.Image imgB = new javafx.scene.image.Image(b.getCurrentCostume().toURI().toString(), bW, bH, false, false);

        javafx.scene.image.PixelReader readerA = imgA.getPixelReader();
        javafx.scene.image.PixelReader readerB = imgB.getPixelReader();

        // check every pixel in the overlapping region
        for (int y = overlapY1; y < overlapY2; y++) {
            for (int x = overlapX1; x < overlapX2; x++) {
                int ax = x - (int) a.getX();
                int ay = y - (int) a.getY();
                int bx = x - (int) b.getX();
                int by = y - (int) b.getY();

                // if both pixels are non-transparent, they are touching
                if (readerA.getArgb(ax, ay) >> 24 != 0 &&
                    readerB.getArgb(bx, by) >> 24 != 0) {
                    return true;
                }
            }
        }

        return false;
    }
}
