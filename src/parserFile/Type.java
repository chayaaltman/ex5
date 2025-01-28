package parserFile;

/**
 * Enum that represents the types of variables that can be used in the program.
 */
public enum Type {
     /**
      * Types of variables.
      */
     INT("int"), // Integer type
     CHAR("char"), // Character type
     STRING("String"), // String type
     DOUBLE("double"), // Double type
     BOOLEAN("boolean"); // Boolean type

     private final String type;

     /**
      *   Constructor for the Type enum.
      * @param type
      */
     Type(String type) {
          this.type = type;
     }
}