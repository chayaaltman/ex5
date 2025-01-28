package parserFile.methodPackage;

/**
 * Exception class for if/while statements.
 */
public class ifWhileException extends Exception {
    private final ErrorType errorType;
    /**
     * Error messages for the exception.
     */
    private static final String INVALID_MESSAGE = "Invalid if/while statement: Doesnt match " +
            "the if/while line";
    private static final String SYNTAX_MESSAGE = "Syntax  in  condition is invalid";

    /**
     * Enum for the type of error.
     */
    public enum ErrorType {
        INVALID_CONDITION(INVALID_MESSAGE), // Error message for invalid if/while condition
        SYNTAX(SYNTAX_MESSAGE); // Error message for the syntax error

        private final String message;
        /**
         * Constructor for the error type.
         * @param message
         */
        ErrorType(String message) {
            this.message = message;
        }

        /**
         * Getter for the error message.
         * @return
         */
        public String getMessage() {
            return message;
        }
    }

    /**
     * Constructor for the exception.
     * @param errorType
     */
    public ifWhileException(ErrorType errorType) {
        super(errorType.getMessage());
        this.errorType = errorType;
    }

    /**
     * Constructor for the exception.
     * @param errorType
     * @param detail
     */
    public ifWhileException(ErrorType errorType, String detail) {
        super(errorType.getMessage() + ": " + detail);
        this.errorType = errorType;
    }

}
