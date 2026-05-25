package backend;

import java.io.File;
import java.util.* ;

/**
 * Represents a single sprite in the Scratch-like environment.
 * A sprite has a position, direction, size, a set of costumes, and text state (say/think).
 * Sprites can be cloned — each clone gets a unique instance ID but shares the same name.
 * All coordinates use Scratch convention: origin at center, positive Y upward,
 * with bounds roughly -240 to 240 on X and -180 to 180 on Y.
 */
public class Sprite {
    private static int nextInstanceId = 1;

    private final int instanceId;
    private String name;
    private int x;
    private int y;
    private int size;
    private int dir;
    private ArrayList<File> costumes;
    private File currentCostume;
    private int currentCostumeIndex = 0;
    private boolean hidden = false;
    private String sayText = "";
    private String thinkText = "";
    private double costumeWidth = 0;
    private double costumeHeight = 0;

    /**
     * Constructs a fully specified Sprite with a name, position, size, and costumes.
     * The first costume in the list becomes the active costume.
     * Direction defaults to 0 (facing right).
     *
     * @param name     the display name of this sprite
     * @param x        initial X position in Scratch coordinates
     * @param y        initial Y position in Scratch coordinates
     * @param size     initial size as a percentage (100 = normal size)
     * @param costumes list of costume image files; must not be null or empty
     */
    public Sprite(String name, int x, int y, int size, ArrayList<File> costumes) 
    {
        this.instanceId = nextInstanceId++;
        this.name = name;
        this.x = x;
        this.y = y;
        this.size = size;
        dir = 0;
        this.costumes = costumes;
        if (costumes != null && !costumes.isEmpty()) 
            {
            this.currentCostume = costumes.get(0);
        }
    }

    /**
     * Constructs a Sprite at the origin (0, 0) with default size 100.
     *
     * @param name     the display name of this sprite
     * @param costumes list of costume image files
     */
    public Sprite(String name, ArrayList<File> costumes) 
    {
        this(name, 0, 0, 100, costumes);
    }

    /**
     * Copy constructor. Creates a deep copy of another sprite with a new unique instance ID.
     * Used when creating clones — the clone shares the same name but is otherwise independent.
     *
     * @param other the Sprite to copy
     */
    public Sprite(Sprite other) 
    {
        this.instanceId = nextInstanceId++;
        this.name = other.name;
        this.x = other.x;
        this.y = other.y;
        this.size = other.size;
        this.dir = other.dir;
        this.costumes = new ArrayList<>(other.costumes);
        this.currentCostume = other.currentCostume;
        this.currentCostumeIndex = other.currentCostumeIndex;
        this.hidden = other.hidden;
        this.sayText = other.sayText;
        this.thinkText = other.thinkText;
        this.costumeWidth = other.costumeWidth;
        this.costumeHeight = other.costumeHeight;
    }

    /**
     * Returns the display name of this sprite.
     * Clones share the same name as their source sprite.
     *
     * @return the sprite's name
     */
    public String getName() 
    {
        return name;
    }

    /**
     * Returns the unique instance ID of this sprite.
     * Every sprite and clone gets a distinct ID assigned at construction time,
     * which is used to tell clones apart from each other even when they share a name.
     *
     * @return the instance ID
     */
    public int getInstanceId() 
    {
        return instanceId;
    }

    /**
     * Returns the sprite's current X position in Scratch coordinates.
     * (0 = center, positive = right, range roughly -240 to 240)
     *
     * @return X position
     */
    public int getX() 
    {
        return x;
    }

    /**
     * Returns the sprite's current Y position in Scratch coordinates.
     * (0 = center, positive = up, range roughly -180 to 180)
     *
     * @return Y position
     */
    public int getY() 
    {
        return y;
    }

    /**
     * Returns the sprite's current size as a percentage of its natural size.
     * 100 means normal size, 200 means double, 50 means half.
     *
     * @return size percentage
     */
    public int getSize() 
    {
        return size;
    }

    /**
     * Returns the sprite's current direction in degrees.
     * 0 = right, 90 = up, 180 = left, 270 = down (Scratch convention).
     *
     * @return direction in degrees
     */
    public int getDir() 
    {
        return dir;
    }

    /**
     * Hides the sprite so it is not rendered and does not participate in collision detection.
     */
    public void hide() 
    {
        hidden = true;
    }

    /**
     * Makes the sprite visible again after being hidden.
     */
    public void show() 
    {
        hidden = false;
    }

    /**
     * Returns whether this sprite is currently hidden.
     *
     * @return true if hidden, false if visible
     */
    public boolean isHidden() 
    {
        return hidden;
    }

    /**
     * Moves the sprite forward by the given number of steps in the direction it is currently facing.
     * Uses trigonometry to decompose the movement into X and Y components.
     * Scratch's direction 0 = right (east), so the angle is rotated 90 degrees from standard math.
     *
     * @param steps number of steps to move
     */
    public void move(int steps) 
    {
        x += (int) Math.round(steps * Math.cos(Math.toRadians(90 - dir)));
        y += (int) Math.round(steps * Math.sin(Math.toRadians(90 - dir)));
    }

    /**
     * Instantly moves the sprite to the given Scratch coordinates.
     *
     * @param myX target X position in Scratch coordinates
     * @param myY target Y position in Scratch coordinates
     */
    public void goTo(int myX, int myY) 
    {
        x = myX;
        y = myY;
    }

    /**
     * Moves the sprite to a random position within the Scratch canvas bounds.
     * X is randomly chosen from -240 to 240, Y from -180 to 180.
     */
    public void goToRandomPosition() 
    {
        x = (int)(Math.random() * 481) - 240;
        y = (int)(Math.random() * 361) - 180;
    }

    /**
     * Rotates the sprite counterclockwise by the given number of degrees.
     *
     * @param deg degrees to rotate left
     */
    public void turnLeft(int deg) 
    {
        dir -= deg;
    }

    /**
     * Rotates the sprite clockwise by the given number of degrees.
     *
     * @param deg degrees to rotate right
     */
    public void turnRight(int deg) 
    {
        dir += deg;
    }

    /**
     * Sets the sprite's direction to an exact angle in degrees.
     * 0 = right, 90 = up, 180 = left, 270 = down.
     *
     * @param deg the target direction in degrees
     */
    public void pointInDirection(int deg) 
    {
        dir = deg;
    }

    /**
     * Changes the sprite's X position by the given amount.
     *
     * @param myX the amount to add to X (can be negative)
     */
    public void changeX(int myX) 
    {
        x += myX;
    }

    /**
     * Changes the sprite's Y position by the given amount.
     *
     * @param myY the amount to add to Y (can be negative)
     */
    public void changeY(int myY) 
    {
        y += myY;
    }

    /**
     * Sets the sprite's X position to an exact value in Scratch coordinates.
     *
     * @param x the new X position
     */
    public void setX(int x) 
    {
        this.x = x;
    }

    /**
     * Sets the sprite's Y position to an exact value in Scratch coordinates.
     *
     * @param y the new Y position
     */
    public void setY(int y) 
    {
        this.y = y;
    }

    /**
     * Adds a new costume image file to this sprite's costume list.
     *
     * @param file the costume image file to add
     */
    public void addCostume(File file) 
    {
        costumes.add(file);
    }

    /**
     * Switches the active costume to the one with the given filename.
     * If no costume with that filename exists, the current costume is unchanged.
     *
     * @param filename the filename of the costume to switch to (e.g. "cat.png")
     */
    public void switchCostume(String filename) 
    {
        for (int i = 0; i < costumes.size(); i++) 
            {
            if (costumes.get(i).getName().equals(filename)) 
            {
                currentCostumeIndex = i;
                currentCostume = costumes.get(i);
                return;
            }
        }
    }

    /**
     * Advances to the next costume in the list, wrapping back to the first
     * costume after the last one. Used for animation.
     */
    public void nextCostume() 
    {
        currentCostumeIndex = (currentCostumeIndex + 1) % costumes.size();
        currentCostume = costumes.get(currentCostumeIndex);
    }

    /**
     * Returns the currently active costume file, used by the GUI for rendering.
     *
     * @return the current costume as a File
     */
    public File getCurrentCostume() 
    {
        return currentCostume;
    }

    /**
     * Returns all costume files belonging to this sprite.
     *
     * @return an ArrayList of all costume Files
     */
    public ArrayList<File> getCostumes() 
    {
        return costumes;
    }

    /**
     * Returns whether the mouse cursor is currently over this sprite.
     * Uses the sprite's scaled bounding box for the check. Returns false
     * if the sprite is hidden or costume dimensions have not been set.
     *
     * @param mouseX mouse X position in Scratch coordinates
     * @param mouseY mouse Y position in Scratch coordinates
     * @return true if the mouse is within the sprite's bounding box, false otherwise
     */
    public boolean isTouchingMouse(double mouseX, double mouseY) 
    {
        if (hidden || costumeWidth == 0) 
        {
            return false;
        }
        double scale = size / 100.0;
        double halfW = (costumeWidth * scale) / 2;
        double halfH = (costumeHeight * scale) / 2;
        return Math.abs(mouseX - x) <= halfW && Math.abs(mouseY - y) <= halfH;
    }

    /**
     * Makes the sprite display a speech bubble with the given text.
     * Clears any active think bubble.
     *
     * @param text the text to say; pass an empty string to clear the bubble
     */
    public void say(String text) 
    {
        sayText = text;
        thinkText = "";
    }

    /**
     * Returns the text currently shown in this sprite's speech bubble.
     *
     * @return the say text, or an empty string if none
     */
    public String getSayText() 
    {
        return sayText;
    }

    /**
     * Makes the sprite display a speech bubble for a set duration.
     * The timing is handled by the Interpreter via waitUntil — this method
     * just sets the text immediately; the Interpreter clears it after the delay.
     *
     * @param text    the text to display
     * @param seconds how long to display it (handled by the Interpreter)
     */
    public void sayForTime(String text, int seconds) 
    {
        say(text);
    }

    /**
     * Returns the text currently shown in this sprite's think bubble.
     *
     * @return the think text, or an empty string if none
     */
    public String getThinkText() 
    {
        return thinkText;
    }

    /**
     * Makes the sprite display a think bubble with the given text.
     * Clears any active speech bubble.
     *
     * @param text the text to think; pass an empty string to clear the bubble
     */
    public void think(String text) 
    {
        thinkText = text;
        sayText = "";
    }

    /**
     * Makes the sprite display a think bubble for a set duration.
     * The timing is handled by the Interpreter via waitUntil — this method
     * just sets the text immediately; the Interpreter clears it after the delay.
     *
     * @param text    the text to display
     * @param seconds how long to display it (handled by the Interpreter)
     */
    public void thinkForTime(String text, int seconds) 
    {
        think(text);
    }

    /**
     * Sets the sprite's size to an exact percentage of its natural size.
     * 100 = normal, 200 = double, 50 = half.
     *
     * @param size the new size percentage
     */
    public void setSize(int size) 
    {
        this.size = size;
    }

    /**
     * Changes the sprite's size by the given amount.
     * For example, changeSize(10) on a size-100 sprite results in size 110.
     *
     * @param size the amount to add to the current size (can be negative)
     */
    public void changeSize(int size) 
    {
        this.size += size;
    }

    /**
     * Stores the natural (unscaled) dimensions of the current costume image.
     * Called by the GUI after loading the image so that methods like
     * {@link #isTouchingMouse} can compute accurate bounding boxes.
     *
     * @param width  the costume's natural width in pixels
     * @param height the costume's natural height in pixels
     */
    public void setCostumeDimensions(double width, double height) 
    {
        costumeWidth = width;
        costumeHeight = height;
    }

    /**
     * Returns a debug string showing the sprite's name, instance ID, position, size, and direction.
     *
     * @return a human-readable summary of this sprite's state
     */
    public String toString() {
        return "SPRITE[" + name + "#" + instanceId + ", " + x + ", " + y + ", " + size + ", " + dir + "]";
    }
}