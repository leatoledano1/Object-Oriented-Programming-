package ex5.main;

import java.util.HashSet;
import java.util.Set;

/**stores all method names declared in an s-Java file
 * Used during verification to detect duplicate method
 * declarations and to validate method calls.
 */
public class MethodTable {

    /**
     * Set containing the names of all already declared methods
     */
    private final Set<String> methods = new HashSet<>();

    /**
     * adds a new method name to the table
     * @param name the method name to add
     * @throws IllegalArgumentException if the method already exists
     */
    public void add(String name) {
        if (methods.contains(name)) {
            throw new IllegalArgumentException();
        }
        methods.add(name);
    }

    /**checks whether a method with the given name exists
     * @param name the method name to check
     * @return true if the method exists, false otherwise
     */
    public boolean exists(String name) {
        return methods.contains(name);
    }
}
