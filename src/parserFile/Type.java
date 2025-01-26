package parserFile;

/**
 * Enum that represents the types of variables that can be used in the program.
 */
public enum Type {
     INT("int"),
     CHAR("char"),
     STRING("string"),
     DOUBLE("double"),
     BOOLEAN("boolean");

     private final String type;

     Type(String type) {
          this.type = type;
     }
}