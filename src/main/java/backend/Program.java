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

    public ArrayList<Script> getFunctions() {
        return functions;
    }

    /*
    Given a String name, check if this function exists
    */
    public boolean isFunction(String name) {
        for (Script f : functions) {
            if (f.getName().equals(name)) {
                return true;
            }
        }

        return false;
    }

    /*
    Given name, return the script of the function

    Returns null if function doesn't exist
    */
    public Script getFunctionByName(String name) {
        for (Script f : functions) {
            if (f.getName().equals(name)) {
                return f;
            }
        }

        return null;
    }

    public String getName() {
        return name;
    }
}
