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
    private int currentCostumeIndex = 0;

    public Sprite(String name, int x, int y, int size, ArrayList<File> costumes) {
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
        x = (int)(Math.random() * 481);
        y = (int)(Math.random()*361);
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

    //adds another costume to the sprite's list of costumes
    public void addCostume(File file) 
    {
        costumes.add(file);
    }
    
    //switches the current costumes to the another costume
    public void switchCostume(String filename) {
        for (int i = 0; i < costumes.size(); i++) {
            if (costumes.get(i).getName().equals(filename)) {
                currentCostumeIndex = i;
                currentCostume = costumes.get(i);
                return;
            }
        }
        System.out.println("WARNING: costume not found: " + filename);
    }

    // switches currentcostume to the costume after its index
    //Ex. currentcostume is the file at index 0 in arraylist, nextCostume() changes currentcostume to the file at index 1
    public void nextCostume() {
        currentCostumeIndex = (currentCostumeIndex + 1) % costumes.size();
        currentCostume = costumes.get(currentCostumeIndex);
    }

    //returns the current costume for drawing
    public File getCurrentCostume()
    {
        return currentCostume ; 
    }

    //returns all the costumes for the sprite
    public ArrayList<File> getCostumes()
    {
        return costumes;
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

    public void setSize(int size) {
        this.size = size;
    }

    public void changeSize(int size) {
        this.size += size;
            
    }

    public String toString() {
        return "SPRITE[" + name + ", " + x + ", " + y + ", " + size + ", " + dir + "]";
    }
    

}
