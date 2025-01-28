package parserFile.methodPackage;

/**
 * Exception class for method errors.
 */
public class MethodException extends Exception {

    private final ErrorType errorType;
    /**
     * Error messages for the exception.
     */
    private static final String METHOD_MSG = "Error in method";
    private static final String METHOD_DECLARATION_MSG = "Method declaration is invalid";
    private static final String RETURN_TYPE_MSG = "Return type is invalid";
    private static final String METHOD_EXISTS_MSG = "Method already exists";
    private static final String PARAMETER_MSG = "Parameter is invalid";
    private static final String NEW_METHOD_MSG = "Nested methods are invalid";
    private static final String SYNTAX_MSG = "Syntax is invalid";
    private static final String METHOD_CALL_MSG = "Method call is invalid";
    private static final String PARAMETER_TYPE_MSG = "Parameter type is invalid";
    private static final String PARAMETER_NUMBER_MSG = "Invalid number of parameters for method call";
    private static final String NO_RETURN_STATEMENT_MSG = "Problem with return statement  ";

    /**
     * Enum for the types of errors that can occur in a method.
     */
    public enum ErrorType {
        /**
         * Error messages for the exception.
         */
        METHOD_ERROR(METHOD_MSG), // Error message for invalid method
        METHOD_DECLARATION(METHOD_DECLARATION_MSG), // Error message for invalid method
        RETURN_TYPE(RETURN_TYPE_MSG), // Error message for invalid return type
        METHOD_EXISTS(METHOD_EXISTS_MSG), // Error message for method already existing
        PARAMETER(PARAMETER_MSG), // Error message for invalid parameter
        NEW_METHOD(NEW_METHOD_MSG), // Error message for nested methods
        SYNTAX(SYNTAX_MSG), // Error message for invalid syntax
        METHOD_CALL(METHOD_CALL_MSG), // Error message for invalid method call
        PARAMETER_TYPE(PARAMETER_TYPE_MSG), // Error message for invalid parameter type
        PARAMETER_NUMBER(PARAMETER_NUMBER_MSG), // Error message for
        NO_RETURN_STATEMENT(NO_RETURN_STATEMENT_MSG); // Error message for invalid number of parameters

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
     * Constructor for the MethodException class.
     * @param errorType
     */
    public MethodException(ErrorType errorType) {
      super(errorType.getMessage());
      this.errorType = errorType;
    }

    /**
     * Constructor for the MethodException class.
     * @param errorType
     * @param detail
     */
    public MethodException(ErrorType errorType, String detail) {
      super(errorType.getMessage() + ": " + detail);
      this.errorType = errorType;
    }
  }


