package backend;


import java.util.*;


public class Command
{
    private String name;
    private ArrayList<String> args;

    public Command(String name, ArrayList<String> args) {
        this.name = name;
        this.args = args;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getArgs() {
        return args;
    }
}
