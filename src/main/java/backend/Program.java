package backend;

import java.util.ArrayList;

/*
    This class holds the parsed user code
*/
public class Program
{
    private String name;
    private ArrayList<Script> scripts;
    private ArrayList<Script> functions;

    public Program(String name) {
        this.name = name;
        scripts = new ArrayList<>();
        functions = new ArrayList<>();
    }

    public void addScript(Script script) {
        scripts.add(script);
    }

    public void addFunction(Script script) {
        functions.add(script);
    }

    public ArrayList<Script> getScripts() {
        return scripts;
    }

    public String getName() {
        return name;
    }
}
