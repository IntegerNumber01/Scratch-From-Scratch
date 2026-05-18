package backend;

import java.io.File;
import java.util.* ; 

public class Sprite {
    private String name;
    private int x;
    private int y;
    private int size;
    private int dir;
    private ArrayList<File> costumes;
    private File currentCostume ; 
    // TODO:
    // everything is attached to Program since Sprite and Program go together as a pair
    // should variables go to Program as well?
    private HashMap<String, ScratchValue> variables;

    public Sprite(String name, int x, int y, int size, ArrayList<File> costumes) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.size = size;
        dir = 0;
        this.costumes = costumes ; 
        this.currentCostume = costumes.get(0);
    }

    public Sprite(String name, ArrayList<File> costumes) {
        this(name, 0, 0, 100, costumes);
    }

    public String getName()
    {
        return name ;
    }

    public int getX()
    {
        return x ;
    }

    public int getY()
    {
        return y ;
    }

    public int getSize()
    {
        return size ;
    }

    public int getDir()
    {
        return dir ;
    }

    public void move(int steps) {
        // implement moving in direction facing
        x += (int) Math.round(steps * Math.cos(Math.toRadians(90-dir)));
        y += (int) Math.round(steps * Math.sin(Math.toRadians(90-dir)));
    }

    public void goTo(int myX, int myY) {
        x = myX;
        y = myY;
    }

    public void goToRandomPosition() { // bounds are 480,360)
        x = (int)Math.random() *(481);
        y = (int)Math.random()*(361);
    }

    public void turnLeft(int deg) {
        dir -= deg;
    }

    public void turnRight(int deg) {
        dir += deg;
    }

    public void pointInDirection(int deg) {
        dir = deg;
    }

    public void changeX(int myX) {
        x += myX;
    }

    public void changeY(int myY) {
        y += myY;
    }

    public void setX(int x)
    {
        this.x = x ; 
    }

    public void setY(int y)
    {
        this.y = y ; 
    }

    public ScratchValue getVariableValue(String varName) {
        return variables.get(varName);
    }

    public void setVariableValue(String varName, ScratchValue value) {
        variables.put(varName, value);
    }

    public void addCostume(File file) 
    {
        costumes.add(file);
    }

    public File getCurrentCostume()
    {
        return currentCostume ; 
    }

    public String toString() {
        return "SPRITE[" + name + ", " + x + ", " + y + ", " + size + ", " + dir + "]";
    }

    public String say(String text) {
        return text;
    }

    public void sayForTime(String text, int time) {
        say(text);
        try {
            Thread.sleep(time * 1000L); // use L to create a long variable instead of an integer and use milliseconds instead of seconds
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // restore previous state if sleep is interrupted 
        }
        say(""); // clear once time is over
    }

    public String think(String text) {
        return text;
    }

    public void thinkForTime(String text, int time) { // exact same code as previous sayForTime()
        think(text);
        try {
            Thread.sleep(time * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        think("");
    }

}
