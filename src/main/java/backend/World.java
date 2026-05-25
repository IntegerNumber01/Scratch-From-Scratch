package backend;

import java.util.*;

/**
 * Represents the shared game world that all sprites and interpreters operate in.
 * Holds the global state of the program including sprites, global variables,
 * keyboard input, and mouse input. Acts as the central source of truth for
 * anything that needs to be shared across sprites.
 */
public class World {
    private ArrayList<Sprite> sprites;
    private boolean isRunning;

    private HashMap<String, String> globalVariables;
    private HashMap<String, Boolean> keysPressed;

    private boolean mouseDown;
    private double mouseX;
    private double mouseY;

    /**
     * Constructs a new World with default state.
     * All sprites start empty, the program starts running, all keys start unpressed,
     * and the mouse starts at (0, 0) and unpressed.
     */
    public World() 
    {
        sprites = new ArrayList<>();
        isRunning = true;

        globalVariables = new HashMap<>();

        keysPressed = new HashMap<>();
        initKeys();

        mouseDown = false;
        mouseX = 0;
        mouseY = 0;
    }

    /**
     * Initializes the keysPressed map with all supported keys set to false (not pressed).
     * Supports lowercase letters a–z and digits 0–9.
     */
    private void initKeys() 
    {
        for (char c = 'a'; c <= 'z'; c++) {
            keysPressed.put(String.valueOf(c), false);
        }

        for (char c = '0'; c <= '9'; c++) {
            keysPressed.put(String.valueOf(c), false);
        }
    }

    /**
     * Adds a sprite to the world so it will be rendered and can participate in
     * collision detection.
     *
     * @param sprite the Sprite to add
     */
    public void addSprite(Sprite sprite) 
    {
        sprites.add(sprite);
    }

    /**
     * Returns all sprites currently in the world, including clones.
     *
     * @return an ArrayList of all Sprite objects
     */
    public ArrayList<Sprite> getSprites() 
    {
        return sprites;
    }

    /**
     * Returns whether the program is currently running.
     * When false, forever loops and other continuous blocks should stop.
     *
     * @return true if the program is running, false if it has been stopped
     */
    public boolean isRunning() 
    {
        return isRunning;
    }

    /**
     * Stops the program by setting the running state to false.
     * This causes forever loops to exit on their next iteration check.
     */
    public void stopProgram() 
    {
        isRunning = false;
    }

    

    /**
     * Sets a global variable by name. If the variable already exists it is overwritten.
     * Global variables are shared across all sprites.
     *
     * @param name  the variable name
     * @param value the value to assign, as a String
     */
    public void setGlobalVariable(String name, String value) 
    {
        globalVariables.put(name, value);
    }

    /**
     * Merges an entire map of variables into the global variable store.
     * Existing keys are overwritten if they conflict.
     *
     * @param variables a HashMap of variable names to values to add
     */
    public void addGlobalVariables(HashMap<String, String> variables) 
    {
        globalVariables.putAll(variables);
    }

    /**
     * Returns the full map of global variables.
     *
     * @return a HashMap mapping variable names to their current String values
     */
    public HashMap<String, String> getGlobalVariables() 
    {
        return globalVariables;
    }

    /**
     * Returns whether a global variable with the given name exists.
     *
     * @param name the variable name to check
     * @return true if the variable exists in the global store, false otherwise
     */
    public boolean hasGlobalVariable(String name) 
    {
        return globalVariables.containsKey(name);
    }

    /**
     * Returns the value of a global variable by name, or null if it does not exist.
     *
     * @param name the variable name to look up
     * @return the variable's value as a String, or null if not found
     */
    public String getGlobalVariable(String name) 
    {
        return globalVariables.get(name);
    }

    // ======================
    // KEYBOARD INPUT
    // ======================

    /**
     * Updates the pressed state of a key. Only keys that were registered during
     * initialization (a–z, 0–9) can be tracked. Unknown keys are silently ignored.
     *
     * @param key   the key to update, case-insensitive (e.g. "a", "1")
     * @param value true if the key is now pressed, false if released
     */
    public void setKeyPressed(String key, boolean value) 
    {
        if (key == null) return;
        key = key.toLowerCase();
        if (keysPressed.containsKey(key)) {
            keysPressed.put(key, value);
        }
    }

    /**
     * Returns whether the given key is currently pressed.
     *
     * @param key the key to check, case-insensitive (e.g. "a", "1")
     * @return true if the key is pressed, false if not pressed or unrecognized
     */
    public boolean getKeyPressed(String key) 
    {
        if (key == null) 
        {
            return false;
        }
        key = key.toLowerCase();
        return keysPressed.getOrDefault(key, false);
    }

    /**
     * Sets whether the mouse button is currently held down.
     *
     * @param value true if the mouse is pressed, false if released
     */
    public void setMouseDown(boolean value) 
    {
        mouseDown = value;
    }

    /**
     * Returns whether the mouse button is currently held down.
     *
     * @return true if the mouse is pressed, false otherwise
     */
    public boolean isMouseDown() 
    {
        return mouseDown;
    }

    /**
     * Sets the current mouse X position in Scratch coordinates
     * (origin at center, positive X to the right, range -240 to 240).
     *
     * @param x the mouse X position in Scratch coordinates
     */
    public void setMouseX(double x) 
    {
        mouseX = x;
    }

    /**
     * Sets the current mouse Y position in Scratch coordinates
     * (origin at center, positive Y upward, range -180 to 180).
     *
     * @param y the mouse Y position in Scratch coordinates
     */
    public void setMouseY(double y) 
    {
        mouseY = y;
    }

    /**
     * Returns the current mouse X position in Scratch coordinates.
     *
     * @return mouse X as a double
     */
    public double getMouseX() 
    {
        return mouseX;
    }

    /**
     * Returns the current mouse Y position in Scratch coordinates.
     *
     * @return mouse Y as a double
     */
    public double getMouseY() 
    {
        return mouseY;
    }

    // ======================
    // COLLISION DETECTION
    // ======================

    /**
     * Returns whether two sprites are currently touching each other,
     * using pixel-perfect collision detection.
     *
     * @param a the first Sprite
     * @param b the second Sprite
     * @return true if any non-transparent pixels of a and b overlap, false otherwise
     */
    public boolean isTouching(Sprite a, Sprite b) 
    {
        if (a == null || b == null) 
            {
            return false;
        }
        return spritesOverlap(a, b);
    }

    /**
     * Returns whether two sprites are touching, looked up by their names.
     * If either name does not match a sprite in the world, returns false.
     *
     * @param spriteNameA the name of the first sprite
     * @param spriteNameB the name of the second sprite
     * @return true if the two named sprites are touching, false otherwise
     */
    public boolean isTouching(String spriteNameA, String spriteNameB) 
    {
        Sprite spriteA = null;
        Sprite spriteB = null;

        for (Sprite sprite : sprites) 
        {
            if (sprite.getName().equals(spriteNameA)) 
            {
                spriteA = sprite;
            } 

            else if (sprite.getName().equals(spriteNameB)) 
            {
                spriteB = sprite;
            }
        }

        return isTouching(spriteA, spriteB);
    }

    /**
     * Returns whether the given sprite is touching any sprite with the given name.
     * A sprite is never considered to be touching itself.
     * Useful for clone collision since multiple sprites can share the same name.
     *
     * @param sprite          the sprite to check
     * @param otherSpriteName the name of the other sprite(s) to check against
     * @return true if the sprite touches any sprite with the given name, false otherwise
     */
    public boolean isTouching(Sprite sprite, String otherSpriteName) 
    {
        if (sprite == null || otherSpriteName == null) 
        {
            return false;
        }

        for (Sprite other : sprites)
        {
            if (other.getInstanceId() == sprite.getInstanceId()) 
            {
                continue;
            }

            if (other.getName().equals(otherSpriteName) && isTouching(sprite, other)) 
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns a list of all sprites currently touching the given sprite.
     * The sprite itself is excluded from the results.
     *
     * @param sprite the sprite to check collisions for
     * @return a List of Sprite objects that are currently overlapping the given sprite
     */
    public List<Sprite> getTouchingSprites(Sprite sprite) 
    {
        List<Sprite> touching = new ArrayList<>();

        for (Sprite other : sprites) 
        {
            if (other != sprite && isTouching(sprite, other)) 
            {
                touching.add(other);
            }
        }

        return touching;
    }

    /**
     * Performs pixel-perfect overlap detection between two sprites.
     * Converts each sprite's Scratch coordinates to screen coordinates,
     * finds the overlapping bounding rectangle, then checks whether any
     * pixel in that region is non-transparent in both sprites' images.
     *
     * Scratch coordinates are centered at (0, 0) with Y increasing upward.
     * Screen coordinates have (0, 0) at the top-left with Y increasing downward.
     * Conversion: screenX = 240 + scratchX, screenY = 180 - scratchY.
     *
     * @param a the first Sprite
     * @param b the second Sprite
     * @return true if any non-transparent pixels overlap, false otherwise
     */
    private boolean spritesOverlap(Sprite a, Sprite b) 
    {
        double aScale = a.getSize() / 100.0;
        double bScale = b.getSize() / 100.0;

        int aW = (int)(200 * aScale);
        int aH = (int)(200 * aScale);
        int bW = (int)(200 * bScale);
        int bH = (int)(200 * bScale);

        // convert Scratch coords to screen coords (top-left corner of sprite image)
        int aScreenX = 240 + a.getX() - aW / 2;
        int aScreenY = 180 - a.getY() - aH / 2;
        int bScreenX = 240 + b.getX() - bW / 2;
        int bScreenY = 180 - b.getY() - bH / 2;

        // find overlapping rectangle
        int overlapX1 = Math.max(aScreenX, bScreenX);
        int overlapY1 = Math.max(aScreenY, bScreenY);
        int overlapX2 = Math.min(aScreenX + aW, bScreenX + bW);
        int overlapY2 = Math.min(aScreenY + aH, bScreenY + bH);

        // no overlap at all
        if (overlapX1 >= overlapX2 || overlapY1 >= overlapY2) return false;

        // load pixel data at the scaled size
        javafx.scene.image.Image imgA = new javafx.scene.image.Image(a.getCurrentCostume().toURI().toString(), aW, aH, false, false);
        javafx.scene.image.Image imgB = new javafx.scene.image.Image(b.getCurrentCostume().toURI().toString(), bW, bH, false, false);

        javafx.scene.image.PixelReader readerA = imgA.getPixelReader();
        javafx.scene.image.PixelReader readerB = imgB.getPixelReader();

        // check every pixel in the overlapping region
        for (int y = overlapY1; y < overlapY2; y++) {
            for (int x = overlapX1; x < overlapX2; x++) {
                int ax = x - aScreenX;
                int ay = y - aScreenY;
                int bx = x - bScreenX;
                int by = y - bScreenY;

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