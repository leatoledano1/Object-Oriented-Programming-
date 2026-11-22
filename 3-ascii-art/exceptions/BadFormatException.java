package exceptions;

/**
 * exception for wrong user input format.
 */
public class BadFormatException extends Exception {
    /**
     * constructor for a new BadFormatException with a specific error message.
     * The message explains what part of the user input has an invalid format
     * @param msg the detail message describing the problem.
     */
    public BadFormatException(String msg) {
        super(msg);
    }
}
