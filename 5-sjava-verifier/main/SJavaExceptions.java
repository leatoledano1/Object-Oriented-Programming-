package ex5.main;

/**
 * specific exception used in the s-Java compiler
 * used to signal IO related errors such as
 * missing or unreadable input files (error 2)
 */
public class SJavaExceptions extends Exception {
    /**
     * Creates a new s-Java exception with a given message
     * @param message the error message
     */
    public SJavaExceptions(String message) {
        super(message);
    }
}
