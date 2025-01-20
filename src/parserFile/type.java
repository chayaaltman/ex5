package parserFile;

public enum type {
     INT("int"),
     CHAR("char"),
     STRING("String"),
     DOUBLE("double"),
     BOOLEAN("boolean");

     private final String type;

     type(String type) {
          this.type = type;
     }

     public String getType() {
          return type;
     }

     public static boolean startsWithType(String line) {
          for (type variableType : values()) {
               if (line.startsWith(variableType.getType())) {
                    return true;
               }
          }
          return false;
     }
}