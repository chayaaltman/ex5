package parserFile;

import parserFile.varaibalePackage.VariableException;

public class parseException extends RuntimeException {
    private final ErrorType errorType;

    public enum ErrorType {
        RETURN("Cant return in the global scope"),
        IF_WHILE("If/While statements are not allowed in the global scope"),
        END_OF_LINE("Invalid end of line"),
        COMMENT_TYPE("Wrong comment Type"),
        SYNTAX("Invalid line syntax"),
        ASSIGN_VALUE("Wrong assignment of value"),
        VAR_NOT_DECLARED("Variable is not declared");

        private final String message;

        ErrorType(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    public parseException(ErrorType errorType) {
        super(errorType.getMessage());
        this.errorType = errorType;
    }

    public parseException(ErrorType errorType, String detail) {
        super(errorType.getMessage() + ": " + detail);
        this.errorType = errorType;
    }

}
