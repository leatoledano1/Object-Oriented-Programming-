package ex5.main;

/**
 * Represent all possible error types that can occur
 * during the compilation and verification of an s-Java file
 * Each error type contains:An error message to be printed to standard error
 * An exit code that should be printed to standard output
 */
public enum ErrorType {
    /**
     * Indicates that no error occurred during verification
     */
    NONE(null, 0),
    /**
     * Indicates an input/output related error,
     * such as a missing or unreadable file.
     */
    IO_ERROR("File does not exist or is not readable", 2),
    /**
     * Indicates that a method name was declared more than once.
     */
    DUPLICATE_METHOD("duplicate method name", 1),
    /**
     * Indicates an illegal method parameter declaration
     */
    ILLEGAL_METHOD_PARAMETER("illegal method parameter", 1),
    /**
     * Indicates a duplicate parameter name in a method declaration
     */
    DUPLICATE_PARAMETER("duplicate parameter name", 1),
    /**
     * Indicates an illegal variable declaration
     */
    ILLEGAL_VAR_DECLARATION("illegal variable declaration", 1),
    /**
     * Indicates an illegal assignment to a variable
     * such as assigning to a final variable.
     */
    ILLEGAL_ASSIGNMENT("illegal assignment to variable", 1),
    /**
     * Indicates an assignment between incompatible types.
     */
    INCOMPATIBLE_ASSIGNMENT("incompatible assignment", 1),
    /**
     * Indicates an illegal condition in an if or while statement
     */
    ILLEGAL_CONDITION("illegal condition", 1),
    /**
     * Indicates a call to a method that was not declared
     */
    UNDEFINED_METHOD("call to undefined method", 1),
    /**
     * Indicates an illegal statement in the global scope
     */
    ILLEGAL_GLOBAL_STATEMENT("illegal statement in global scope", 1),
    /**
     * Indicates an illegal statement inside a method body.
     */
    ILLEGAL_METHOD_STATEMENT("illegal statement in method body", 1),
    /**
     * Indicates the use of an illegal comment.
     */
    ILLEGAL_COMMENT("illegal comment", 1),
    /**
     * Indicates an unmatched opening brace.
     */
    UNMATCHED_OPENING_BRACE("unmatched opening brace", 1),
    /**
     * Indicates an unmatched closing brace
     */
    UNMATCHED_CLOSING_BRACE("unmatched closing brace", 1);
    /**
     * The error message associated with this error type
     */
    private final String message;
    /**
     * The exit code associated with this error type
     */
    private final int exitCode;
    /**
     * constructs a new ErrorType
     * @param message  the error message to print
     * @param exitCode the exit code to return
     */
    ErrorType(String message, int exitCode) {
        this.message = message;
        this.exitCode = exitCode;
    }

    /**returns the error message associated with this error type
     * @return the error message, or null if there is none
     */
    public String getMessage() {
        return message;
    }

    /**returns the exit code associated with this error type.
     * @return the exit code
     */
    public int getExitCode() {
        return exitCode;
    }
}
