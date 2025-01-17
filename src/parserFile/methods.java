package parserFile;

/**
 * Call method only when the line is a dec line
 */
public class methods {
    // constructor
    public methods(){

    }
    // method declaration
    private boolean methodDeclaration(String line){
        // split the line to return value, method name, parameters list
        String[] decList = line.split(" ");
        while(decList.length > 0){
            // dealing first arg: return void only
            if (!decList[0].equals("void")){
                return false;
            }
            String methodNameRegex = "^[a-zA-Z][a-zA-Z0-9_]*$";
            if (!decList[1].matches(methodNameRegex)){
                return false;
            }
            String parameterListRegex = "^\s*(int|char|double|boolean|String)\s+[a-zA-Z_][a-zA-Z0-9_]*" +
                    "(\s*,\s*(int|char|double|boolean|String)\s+[a-zA-Z_][a-zA-Z0-9_]*)*\s*$";
            if (!decList[2].matches(parameterListRegex)){
                return false;
            }
            // check the line ends with "{"

        }
        return true;
    }

    private void handleCode(){

    }
}

