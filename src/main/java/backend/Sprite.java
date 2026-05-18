package backend;

import java.util.HashMap;

public class Sprite {
    private String name;
    private int x;
    private int y;
    private int size;
    private int dir;
    // TODO:
    // everything is attached to Program since Sprite and Program go together as a pair
    // should variables go to Program as well?
    private HashMap<String, ScratchValue> variables;

    public Sprite(String name, int x, int y, int size) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.size = size;
        dir = 0;
    }

    public Sprite(String name) {
        this(name, 0, 0, 100);
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

    public void goToRandomPosition() {
        x = Math.random() *
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

    public String toString() {
        return "SPRITE[" + name + ", " + x + ", " + y + ", " + size + ", " + dir + "]";
    }
}
