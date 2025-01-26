package parserFile.ifWhilePackage;

public class InvalidCoditionException extends Exception {
    public InvalidCoditionException(String line) {
        super("Invalid if/while statement: Doesnt match the if/while line " + line);
    }
}
