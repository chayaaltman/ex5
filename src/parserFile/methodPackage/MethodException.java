package parserFile.methodPackage;

public class MethodException extends Exception {

    private final ErrorType errorType;

    public enum ErrorType {
        METHOD_ERROR("Error in method"),
        METHOD_DECLARATION("Method declaration is invalid"),
        RETURN_TYPE("Return type is invalid"),
        METHOD_EXISTS("Method already exists"),
        PARAMETER("Parameter is invalid"),
        NEW_METHOD("Nested methods are invalid"),
        SYNTAX("Syntax is invalid"),
        METHOD_CALL("Method call is invalid"),
        PARAMETER_TYPE("Parameter type is invalid"),
        PARAMETER_NUMBER("Invalid number of parameters for method call"),
        NO_RETURN_STATEMENT("Problem with  return statement  ");


      private final String message;

      ErrorType(String message) {
        this.message = message;
      }

      public String getMessage() {
        return message;
      }
    }

    public MethodException(ErrorType errorType) {
      super(errorType.getMessage());
      this.errorType = errorType;
    }

    public MethodException(ErrorType errorType, String detail) {
      super(errorType.getMessage() + ": " + detail);
      this.errorType = errorType;
    }

    public ErrorType getErrorType() {
      return errorType;
    }
  }


