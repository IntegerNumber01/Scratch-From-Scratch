package backend;

import java.util.* ;

/**
 * Stores the parsed program state for a single sprite.
 * <p>
 * A program contains the sprite's top-level scripts, custom functions,
 * variable values, and the set of variables whose monitors should be visible
 * in the UI.
 */
public class Program
{
    private String name;
    private ArrayList<Script> scripts;
    private ArrayList<Script> functions;
    private HashMap<String, String> variables;
    private HashSet<String> visibleVariables = new HashSet<>() ;

    /**
     * Creates an empty program for a sprite.
     * @param name the sprite name associated with this program
     */
    public Program(String name) {
        this.name = name;
        scripts = new ArrayList<>();
        functions = new ArrayList<>();
        variables = new HashMap<String, String>();
    }

    /**
     * Creates a shallow copy of another program.
     * Scripts, functions, and variable values are copied into new collection
     * instances. Visible variable monitors are intentionally not copied, since
     * that UI state belongs only to the source sprite.
     *
     * @param other the program to copy
     */
    public Program(Program other) {
        this.name = other.name;
        this.scripts = new ArrayList<>(other.scripts);
        this.functions = new ArrayList<>(other.functions);
        this.variables = new HashMap<>(other.variables);
        // Variable monitor visibility belongs to the source sprite's UI state,
        // not to each clone created from that sprite.
        this.visibleVariables = new HashSet<>();
    }

    /**
     * Adds a top-level script to this program.
     * @param script the script to add
     */
    public void addScript(Script script) {
        scripts.add(script);
    }

    /**
     * Adds a custom function definition to this program.
     * @param script the function script to add
     */
    public void addFunction(Script script) {
        functions.add(script);
    }

    /**
     * Returns this program's top-level scripts.
     * @return the list of top-level scripts
     */
    public ArrayList<Script> getScripts() {
        return scripts;
    }

    /**
     * Returns this program's custom function definitions.
     * @return the list of function scripts
     */
    public ArrayList<Script> getFunctions() {
        return functions;
    }

    /**
     * Looks up the current value of a variable.
     * @param varName the variable name
     * @return the stored value, or {@code null} if the variable is not present
     */
    public String getVariableValue(String varName) {
        return variables.get(varName);
    }

    /**
     * Sets or replaces the value of a variable.
     * @param varName the variable name
     * @param value the value to store
     */
    public void setVariableValue(String varName, String value) {
        variables.put(varName, value);
    }

    /**
     * Returns the variable HashMap for this program.
     *
     * @return the HashMap of variable names to values
     */
    public HashMap<String, String> getVariables() {
        return variables;
    }

    /**
     * Marks a variable monitor as visible in the UI.
     * @param name the variable name
     */
    public void showVariable(String name)
    {
        visibleVariables.add(name) ;
    }

    /**
     * Marks a variable monitor as hidden in the UI.
     *
     * @param name the variable name
     */
    public void hideVariable(String name)
    {
        visibleVariables.remove(name) ;
    }

    /**
     * Returns the set of variables whose monitors are visible.
     *
     * @return the set of visible variable names
     */
    public HashSet<String> getVisibleVariables()
    {
        return visibleVariables ;
    }

    /**
     * Checks whether a custom function with the given name exists.
     *
     * @param name the function name
     * @return {@code true} if a matching function exists; otherwise {@code false}
     */
    public boolean isFunction(String name) {
        for (Script f : functions) {
            if (f.getName().equals(name)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns the function script with the given name.
     *
     * @param name the function name
     * @return the matching function script, or {@code null} if none exists
     */
    public Script getFunctionByName(String name) {
        for (Script f : functions) {
            if (f.getName().equals(name)) {
                return f;
            }
        }

        return null;
    }

    /**
     * Returns the sprite name associated with this program.
     *
     * @return the sprite name
     */
    public String getName() {
        return name;
    }
}
