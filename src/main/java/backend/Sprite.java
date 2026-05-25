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

    public Sprite(String name, ArrayList<File> costumes) {
        this(name, 0, 0, 100, costumes);
    }

    public Sprite(Sprite other) {
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
    }

    public String getName()
    {
        return name ;
    }

    public int getInstanceId()
    {
        return instanceId;
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

    public void hide() {
        hidden = true;
    }

    public void show() {
        hidden = false;
    }

    public boolean isHidden() {
        return hidden;
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
        x = (int)(Math.random() * 481) - 240;
        y = (int)(Math.random()*361) - 180;
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

    public void say(String text)
    {
        sayText = text ;
        thinkText = "" ;
    }

    public String getSayText()
    {
        return sayText ;
    }

    public void sayForTime(String text, int seconds) {
        say(text);
    }

    public String getThinkText()
    {
        return thinkText;
    }

    public void think(String text)
    {
        thinkText = text ;
        sayText = "" ;
    }

    public void thinkForTime(String text, int seconds) {
        think(text);
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void changeSize(int size) {
        this.size += size;

    }

    public String toString() {
        return "SPRITE[" + name + "#" + instanceId + ", " + x + ", " + y + ", " + size + ", " + dir + "]";
    }
}
