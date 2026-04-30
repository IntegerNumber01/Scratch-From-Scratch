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

    public void move(int steps) {
        // implement moving in direction facing
    }

}
