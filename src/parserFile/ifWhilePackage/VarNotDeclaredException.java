package parserFile.ifWhilePackage;

public class VarNotDeclaredException extends RuntimeException {
    public VarNotDeclaredException(String e) {
        super("var declaration error: " + e);
    }
}
