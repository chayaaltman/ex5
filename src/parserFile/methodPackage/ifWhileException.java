package parserFile.methodPackage;

public class ifWhileException extends Exception {
    private final ErrorType errorType;

    public enum ErrorType {
        INVALID_CONDITION("Invalid if/while statement: Doesnt match the if/while line " ),
        SYNTAX("Syntax  in  condition is invalid");

        private final String message;

        ErrorType(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    public ifWhileException(ErrorType errorType) {
        super(errorType.getMessage());
        this.errorType = errorType;
    }

    public ifWhileException(ErrorType errorType, String detail) {
        super(errorType.getMessage() + ": " + detail);
        this.errorType = errorType;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

}
