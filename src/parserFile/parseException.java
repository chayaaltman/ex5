package parserFile;

/**
 * Exception class for the parser.
 */
public class parseException extends RuntimeException {
    private final ErrorType errorType;
    /**
     * Finals string for the error message.
      */
    private static final String RETURN_ERROR = "Cant return in the global scope";
    private static final String IF_WHILE_ERROR = "If/While statements are not allowed in the global scope";
    private static final String END_OF_LINE_ERROR = "Invalid end of line";
    private static final String COMMENT_TYPE_ERROR = "Wrong comment Type";
    private static final String SYNTAX_ERROR = "Invalid line syntax";
    private static final String ASSIGN_VALUE_ERROR = "Wrong assignment of value";
    private static final String VAR_NOT_DECLARED_ERROR = "Variable is not declared";

    /**
     * enum for the error type.
     */
    public enum ErrorType {
        /**
         * Error messages for the exception.
         */
        RETURN(RETURN_ERROR), // Error message for invalid return
        IF_WHILE(IF_WHILE_ERROR), // Error message for invalid if/while
        END_OF_LINE(END_OF_LINE_ERROR), // Error message for invalid end of line
        COMMENT_TYPE(COMMENT_TYPE_ERROR), // Error message for invalid comment type
        SYNTAX(SYNTAX_ERROR), // Error message for invalid syntax
        ASSIGN_VALUE(ASSIGN_VALUE_ERROR),   // Error message for invalid assignment
        VAR_NOT_DECLARED(VAR_NOT_DECLARED_ERROR); // Error message for variable not declared

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
     * Constructor for the parse exception.
     * @param errorType
     */
    public parseException(ErrorType errorType) {
        super(errorType.getMessage());
        this.errorType = errorType;
    }

    /**
     * Constructor for the parse exception.
     * @param errorType
     * @param detail
     */
    public parseException(ErrorType errorType, String detail) {
        super(errorType.getMessage() + ": " + detail);
        this.errorType = errorType;
    }

}
