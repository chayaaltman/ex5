package parserFile;

public class VariablesExceptions {

    public static class VariableSyntaxException extends Exception {
        public VariableSyntaxException() {
            super("Variable syntax is invalid");
        }
    }

    public static class VariableNameException extends Exception {
        public VariableNameException() {
            super("Variable name is invalid");
        }
    }

    public static class VarIsUsedException extends Exception {
        public VarIsUsedException() {
            super("Variable is already used");
        }
    }

    public static class ValueTypeException extends Exception {
        public ValueTypeException() {
            super("Value type is invalid");
        }
    }

    public static class FinalVarException extends Exception {
        public FinalVarException() {
            super("Wrong use of final keyword");
        }
    }

    public static class AssignValueException extends Exception {
        public AssignValueException() {
            super("Wrong assignment of value");
        }
    }
}
