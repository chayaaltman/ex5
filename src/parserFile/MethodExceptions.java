package parserFile;

public class MethodExceptions {

    public static class MethodDeclarationException extends Exception {
        public MethodDeclarationException() {
            super("Method declaration is invalid");
        }
    }

    public static class ReturnStatementException extends Exception {
        public ReturnStatementException() {
            super("Return statement is invalid");
        }
    }

    public static class ReturnTypesException extends Exception {
        public ReturnTypesException() {
            super("Return type is invalid");
        }
    }

    public static class MethodExistException extends Exception {
        public MethodExistException() {
            super("Method already exists");
        }
    }

    public static class ParameterException extends Exception {
        public ParameterException() {
            super("Parameter is invalid");
        }
    }

    public static class NewMethodException extends Exception {
        public NewMethodException() {
            super("Nested methods are invalid");
        }
    }

    public static class SyntaxException extends Exception {
        public SyntaxException() {
            super("Syntax is invalid");
        }
        public SyntaxException(String line) {
            super("Syntax is invalid: " + line);
        }
    }

    public static class MethodCallException extends Exception {
        public MethodCallException() {
            super("Method call is invalid");
        }
    }

    public static class ParameterTypeException extends Exception {
        public ParameterTypeException() {
            super("Parameter type is invalid");
        }
    }

    public static class ParameterNumberException extends Exception {
        public ParameterNumberException() {
            super("Invalid number of parameters for method call");
        }
    }

}
