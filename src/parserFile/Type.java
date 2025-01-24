package parserFile;

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

     public String getType() {
          return type;
     }
}