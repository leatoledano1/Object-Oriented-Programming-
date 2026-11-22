package exceptions;

/**
 * exception for resolution problems,
 * like go outside boundaries or wrong keywords.
 */
public class ResolutionException extends Exception {
    /**
     * constructor for a new ResolutionException with a specific error message.
     * the message indicates that a resolution change failed due to
     * invalid boundaries or not supported resolution keywords.
     * @param msg the detail message explaining the res error.
     */
    public ResolutionException(String msg) {
        super(msg);
    }
}
