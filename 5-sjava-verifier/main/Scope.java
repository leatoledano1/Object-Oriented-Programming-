package ex5.main;

import java.util.HashMap;
import java.util.Map;

/**represents a scope in the s-Java program.
 * Each scope holds its own variables and may have a parent scope.
 * Used to handle variable visibility and shadowing.
 */
public class Scope {
    /**
     * The "parent" scope or null if this is the global scope
     */
    private final Scope parent;

    /**
     * Map of variable names to their information in this scope
     */
    private final Map<String, VarInfo> vars = new HashMap<>();

    /**creates a new scope with an optional parent scope
     * @param parent the parent scope or null for global scope
     */
    public Scope(Scope parent) {
        this.parent = parent;
    }

    /**declares a new variable in the current scope.
     * @param name the variable name
     * @param info the variable information
     * @return true if the declaration succeeded, false if the name already exists
     */
    public boolean declare(String name, VarInfo info) {
        if (vars.containsKey(name)) return false;
        vars.put(name, info);
        return true;
    }

    /**Looks up a variable by name
     * Searches in the current scope first and then continues
     * to parent scopes if needed.
     * @param name the variable name
     * @return the variable information or null if not found
     */
    public VarInfo lookup(String name) {
        if (vars.containsKey(name)) return vars.get(name);
        return parent != null ? parent.lookup(name) : null;
    }

    /**
     * Marks a variable as initialized
     * Only affects variables declared in this scope
     */
    public void assign(String name) {
        VarInfo v = vars.get(name);
        if (v != null) {
            v.markInitialized();
        }
    }
}
