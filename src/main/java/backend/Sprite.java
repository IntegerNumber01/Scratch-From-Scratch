package backend;

import java.io.File;
import java.util.* ;

public class Sprite {
    private static int nextInstanceId = 1;

    private final int instanceId;
    private String name;
    private int x;
    private int y;
    private int size;
    private int dir;
    private ArrayList<File> costumes;
    private File currentCostume ;
    private int currentCostumeIndex = 0;
    private boolean hidden = false;
    private String sayText = "" ;
    private String thinkText = "";
    private double costumeWidth = 0 ;
    private double costumeHeight = 0 ;


    /**
     *
     * @param name
     * @param x
     * @param y
     * @param size
     * @param costumes
     *
     * Sets the name, x, y, size, and the costumes of the Sprite
     */
    public Sprite(String name, int x, int y, int size, ArrayList<File> costumes) {
        this.instanceId = nextInstanceId++;
        this.name = name;
        this.x = x;
        this.y = y;
        this.size = size;
        dir = 0;
        //sets the arraylist of costumes for the spriteand the current costume of the sprite
        this.costumes = costumes ;
        if(costumes != null && !costumes.isEmpty())
        {
            this.currentCostume = costumes.get(0);
        }
    }

    /**
     *
     * @param name
     * @param costumes
     *
     * Sets the name of the sprite and its costumes
     * Defaults other things
     */
    public Sprite(String name, ArrayList<File> costumes) {
        this(name, 0, 0, 100, costumes);
    }

    /**
     *
     * @param other
     * Copy constructor for sprites
     */
    public Sprite(Sprite other) {
        this.instanceId = nextInstanceId++; //Easy differentiate clones
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
        costumeWidth = other.costumeWidth ;
        costumeHeight = other.costumeHeight ;
    }

    /**
     *
     * @return String of the Sprite's name
     */
    public String getName()
    {
        return name ;
    }

    /**
     *
     * @return int clones instanceId
     */
    public int getInstanceId()
    {
        return instanceId;
    }

    /**
     *
     * @return int Sprite's X coordinate
     */
    public int getX()
    {
        return x ;
    }

    /**
     *
     * @return int Sprite's Y coordinate
     */
    public int getY()
    {
        return y ;
    }

    /**
     *
     * @return int Sprite's size
     */
    public int getSize()
    {
        return size ;
    }

    /**
     *
     * @return int Sprite's dir via angles
     */
    public int getDir()
    {
        return dir ;
    }

    /**
     * sets the Sprite's hidden factor
     */
    public void hide() {
        hidden = true;
    }

    /**
     * resets the Sprite's hidden factor
     */
    public void show() {
        hidden = false;
    }

    /**
     *
     * @return boolean of if the sprite should be hidden
     */
    public boolean isHidden() {
        return hidden;
    }

    /**
     *
     * @param steps
     * does math to change the
     */
    public void move(int steps) {
        // implement moving in direction facing
        x += (int) Math.round(steps * Math.cos(Math.toRadians(90-dir)));
        y += (int) Math.round(steps * Math.sin(Math.toRadians(90-dir)));
    }

    /**
     *
     * @param myX
     * @param myY
     * Goes to the x,y coordinates provided
     */
    public void goTo(int myX, int myY) {
        x = myX;
        y = myY;
    }

    /**
     * goes to a random position in the dimensions of the window
     */
    public void goToRandomPosition() { // bounds are 480,360)
        x = (int)(Math.random() * 481) - 240;
        y = (int)(Math.random()*361) - 180;
    }

    /**
     *
     * @param deg
     * Changes the Sprite's direction
     */
    public void turnLeft(int deg) {
        dir -= deg;
    }

    /**
     *
     * @param deg
     * Changes the Sprite's direction
     */
    public void turnRight(int deg) {
        dir += deg;
    }

    /**
     *
     * @param deg
     * Sets the Sprite's direction to the specified degree
     */
    public void pointInDirection(int deg) {
        dir = deg;
    }

    /**
     *
     * @param myX
     * Chnages the x by this much
     */
    public void changeX(int myX) {
        x += myX;
    }

    /**
     *
     * @param myY
     * Changes the y by this much
     */
    public void changeY(int myY) {
        y += myY;
    }

    /**
     *
     * @param x
     * Sets the x to this much
     */
    public void setX(int x)
    {
        this.x = x ;
    }

    /**
     *
     * @param y
     * Sets the y to this much
     */
    public void setY(int y)
    {
        this.y = y ;
    }

    /**
     *
     * @param file
     * adds another costume to the sprite's list of costumes
     */
    public void addCostume(File file)
    {
        costumes.add(file);
    }

    /**
     * switches the current costumes to the another costume
    */
    public void switchCostume(String filename) {
        for (int i = 0; i < costumes.size(); i++) {
            if (costumes.get(i).getName().equals(filename)) {
                currentCostumeIndex = i;
                currentCostume = costumes.get(i);
                return;
            }
        }
    }

    /**
     * switches currentcostume to the costume after its index
     * Ex. currentcostume is the file at index 0 in arraylist, nextCostume() changes currentcostume to the file at index 1
     */
    public void nextCostume() {
        currentCostumeIndex = (currentCostumeIndex + 1) % costumes.size();
        currentCostume = costumes.get(currentCostumeIndex);
    }

    /**
     *
     * @return gives the current costume for drawing
     */
    public File getCurrentCostume()
    {
        return currentCostume ;
    }

    /**
     *
     * @return all the costumes for the sprite
     */
    public ArrayList<File> getCostumes()
    {
        return costumes;
    }

    /**
     *
     * @param mouseX
     * @param mouseY
     * @return gives a boolean if the mouse is over the sprite or not
     */
    public boolean isTouchingMouse(double mouseX, double mouseY)
    {
        if (hidden || costumeWidth == 0) return false;
        double scale = size / 100.0;
        double halfW = (costumeWidth * scale) / 2;
        double halfH = (costumeHeight * scale) / 2;
        return Math.abs(mouseX - x) <= halfW && Math.abs(mouseY - y) <= halfH;
    }

    /**
     *
     * @param text
     * Sets the text for what the sprite to say
     */
    public void say(String text)
    {
        sayText = text ;
        thinkText = "" ;
    }

    /**
     *
     * @return
     * Gives the test for what the sprite to say
     */
    public String getSayText()
    {
        return sayText ;
    }

    /**
     *
     * @param text
     * @param seconds
     * Says the text for this amount of time
     * Will give the amount of time to say the text to interpreter
     */
    public void sayForTime(String text, int seconds) {
        say(text);
    }

    /**
     *
     * @return String text that the Sprite has to think
     */
    public String getThinkText()
    {
        return thinkText;
    }

    /**
     *
     * @param text
     * Sets the text for what the sprite to think
     */
    public void think(String text)
    {
        thinkText = text ;
        sayText = "" ;
    }

    /**
     *
     * @param text
     * @param seconds
     * Thinks the text for this amount of time
     * Will give the amount of seconds to think for seconds
     */
    public void thinkForTime(String text, int seconds) {
        think(text);
    }

    /**
     *
     * @param size
     * Sets the size to this amount
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     *
     * @param size
     * changes the size by this much
     */
    public void changeSize(int size) {
        this.size += size;

    }

    /**
     *
     * @param width
     * @param height
     *
     * sets costume dimensions to this
     */
    public void setCostumeDimensions(double width, double height)
    {
        costumeWidth = width ;
        costumeHeight = height ;
    }

    /**
     * @return sprite of all attributes of sprite
     * for debugging
     */
    public String toString() {
        return "SPRITE[" + name + "#" + instanceId + ", " + x + ", " + y + ", " + size + ", " + dir + "]";
    }
}
