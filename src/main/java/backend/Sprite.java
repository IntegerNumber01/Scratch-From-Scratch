package backend;

public class Sprite {
    private String name;
    private int x;
    private int y;
    private int size;
    private int dir;
    
    public Sprite(String name, int x, int y, int size) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.size = size;
        dir = 0;
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
    }

    public void goTo(int myX, int myY) {
        x = myX;
        y = myY;
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
        
    }

}
