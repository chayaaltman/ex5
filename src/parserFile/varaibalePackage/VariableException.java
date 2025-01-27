package parserFile.varaibalePackage;

public class VariableException extends Exception {
        private final ErrorType errorType;

        public enum ErrorType {
            VARIABLE_SYNTAX("Syntax is invalid"),
            VARIABLE_NAME("Variable name is invalid"),
            VARIABLE_USED("Variable is already used"),
            VALUE_TYPE("Value type is invalid"),
            FINAL_VARIABLE("Wrong use of final keyword"),
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

        public VariableException(ErrorType errorType) {
            super(errorType.getMessage());
            this.errorType = errorType;
        }

        public VariableException(ErrorType errorType, String detail) {
            super(errorType.getMessage() + ": " + detail);
            this.errorType = errorType;
        }

        public ErrorType getErrorType() {
            return errorType;
        }
    }


