package parserFile.varaibalePackage;

/**
 * Exception class for variable errors.
 */
public class VariableException extends Exception {
        private final ErrorType errorType;

    /**
     * String finals for the error messages.
     */
    private static final String SYNTAX_ERROR = "Syntax is invalid";
    private static final String VARIABLE_NAME_ERROR = "Variable name is invalid";
    private static final String VARIABLE_USED_ERROR = "Variable is already used";
    private static final String VALUE_TYPE_ERROR = "Value type is invalid";
    private static final String FINAL_VARIABLE_ERROR = "Wrong use of final keyword";
    private static final String ASSIGN_VALUE_ERROR = "Wrong assignment of value";
    private static final String VAR_NOT_DECLARED_ERROR = "Variable is not declared";

    /**
     * Enum for the type of error.
     */
    public enum ErrorType {
            VARIABLE_SYNTAX(SYNTAX_ERROR), // Error message for invalid syntax
            VARIABLE_NAME(VARIABLE_NAME_ERROR), // Error message for invalid variable name
            VARIABLE_USED(VARIABLE_USED_ERROR), // Error message for variable already used
            VALUE_TYPE(VALUE_TYPE_ERROR), // Error message for invalid value type
            FINAL_VARIABLE(FINAL_VARIABLE_ERROR),   // Error message for invalid final variable
            ASSIGN_VALUE(ASSIGN_VALUE_ERROR), // Error message for invalid assignment
            VAR_NOT_DECLARED(VAR_NOT_DECLARED_ERROR); // Error message for variable not declared

            private final String message;

        /**
         * Constructor for the ErrorType enum.
         * @param message
         */
        ErrorType(String message) {
                this.message = message;
            }

        /**
         * Getter for the message.
         * @return
         */
        public String getMessage() {
                return message;
            }
        }

    /**
     * Constructor for the VariableException class.
     * @param errorType
     */
    public VariableException(ErrorType errorType) {
            super(errorType.getMessage());
            this.errorType = errorType;
        }

    /**
     * Constructor for the VariableException class.
     * @param errorType
     * @param detail
     */
    public VariableException(ErrorType errorType, String detail) {
            super(errorType.getMessage() + ": " + detail);
            this.errorType = errorType;
        }
    }


