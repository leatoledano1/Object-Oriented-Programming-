package ex5.main;

/**
 * Holds information about a variable in s-Java
 * Stores the variable type, whether it is final or not,
 * and whether it has been initialized or not
 */
public class VarInfo {
    /** the declared type of the variable */
    private final String type;

    /** indicates whether the variable is final */
    private final boolean isFinal;

    /** indicates whether the variable has been initialized */
    private boolean isInitialized;

    /**creates a new variable info object
     *
     * @param t the variable type
     * @param f true if the variable is final
     * @param i true if the variable is initialized
     */
    public VarInfo(String t, boolean f, boolean i) {
        type = t;
        isFinal = f;
        isInitialized = i;
    }

    /** Getter for the type, for safety*/
    public String getType() {
        return type;
    }

    /** Getter for the boolean is final, for safety*/
    public boolean isFinal() {
        return isFinal;
    }

    /** Getter for the boolean is initialized, for safety*/
    public boolean isInitialized() {
        return isInitialized;
    }

    /**a function that make isInitialized true*/
    public void markInitialized() {
        isInitialized = true;
    }

}
