package exceptions;

/**
 * basic exception for incorrect command.
 * message will be printed by the shell run() method.
 */
public class BadCommandException extends Exception {
    /**
     * constructs a new BadCommandException with a specific error message.
     * The message describes why the command is not valid,
     * and will later be printed by the Shell's run() method.
     * @param msg the detail message explaining the reason for the error.
     */
    public BadCommandException(String msg) {
        super(msg);
    }
}
