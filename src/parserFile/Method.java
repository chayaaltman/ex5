package parserFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Method {
    private String name;
    private List<Map<String, String>> parameters;
    private List<String> body;
    private List<Map<String, String>> localVariables;

    public Method(List<String> body) {
        this.parameters = new ArrayList<>();
        this.localVariables = new ArrayList<>();
        this.body = body;
    }

    public boolean methodDeclaration(String line){
        // Full regex for method declaration
        String methodDeclarationRegex =
                "^\\s*void\\s+([a-zA-Z][a-zA-Z0-9_]*)\\s*\\(\\s*((int|char|double|boolean|String)\\s+[a-zA-Z_][a-zA-Z0-9_]*(\\s*,\\s*(int|char|double|boolean|String)\\s+[a-zA-Z_][a-zA-Z0-9_]*)*)?\\s*\\)\\s*\\{\\s*$";

        // Match the regex
        if (!line.matches(methodDeclarationRegex)) {
            System.out.println("Invalid method declaration: " + line);
            return false;
        }
        String methodName = line.split("\\(")[0].trim().split("\\s+")[1];  // Extract method name
        this.name = methodName;
        // Extract the parameters (if any)

        String paramsPart = line.split("\\(")[1].split("\\)")[0].trim();  // Get everything between parentheses
        if (!paramsPart.isEmpty()) {
            String[] params = paramsPart.split(",");
            for (String param : params) {
                String[] paramParts = param.trim().split("\\s+");
                String paramType = paramParts[0].trim();
                String paramName = paramParts[1].trim();
                // Create a map for the parameter and its type
                Map<String, String> paramMap = new HashMap<>();
                paramMap.put("type", paramType);
                paramMap.put("name", paramName);

                // Add the parameter map to the list for this method
                this.parameters.add(paramMap);
            }
        }
        // check if the body end with "}"
        if (!body.get(body.size() - 1).equals("}")) {
            System.out.println("Invalid method declaration: " + line);
            return false;
        }

        System.out.println("Declared method: " + methodName);
        System.out.println("Parameters: " + parameters);
        return true;
    }

    private void handleBody(){
        for(String line : body){
            if (line.startsWith("int") || line.startsWith("char") || line.startsWith("String") || line.startsWith("double") || line.startsWith("boolean")) {
                handleVariables(line);
            }
            if (line.startsWith("if") || line.startsWith("while")) {
                handleIfWhile();
            }
            if (line.startsWith("return")) {
                // handle return
                handleReturn(line);
            }
            else{
                // handle method call
                handleMethodCall(line);
            }
        }
    }

    // add local vars: use the variables class. call it after
    private void handleVariables(String line){
        Variable variable = new Variable(line);
        boolean validDec = variable.checkLine(line);
        if (validDec) {
            Map<String, String> localVar = new HashMap<>();
            localVar.put("type", variable.getType());
            localVar.put("name", variable.getName());
            localVariables.add(localVar);
        }
    }

    // get the method name
    public String getName() {
        return name;
    }

    // get the parameters
    public List<Map<String, String>> getParameters() {
        return parameters;
    }

    private List<Map<String, String>> getLocalVariables() {
        return localVariables;
    }

    private void handleIfWhile(){
        for (String line : body) {
            if (line.startsWith("if") || line.startsWith("while")) {
                IfWhile ifWhile = new IfWhile(body);
                ifWhile.parserSubroutine();
            }
        }
    }
    // TODO: Implement the following methods
    private void handleReturn(String line){}

    private void handleMethodCall(String line){
        Parser parser = new Parser();
        String methodCallRegex = "^\\s*[a-zA-Z][a-zA-Z0-9_]*\\s*\\([^)]*\\)\\s*;$";
        if (!line.matches(methodCallRegex)) {
            System.out.println("Invalid method call: " + line);
        }
        String methodName = line.substring(0, line.indexOf('(')).trim();
        if (!parser.getAllMethods().contains(methodName)) {
            System.out.println("Error: Method not found - " + methodName);
        }
        // Extract the parameters (if any)
        String paramsPart = line.split("\\(")[1].split("\\)")[0].trim();  // Get everything between parentheses
        if (!paramsPart.isEmpty()) {
            String[] params = paramsPart.split(",");
//            List<Map<String, String>> paramList = parser.getAllMethods()
            /// / ****TODO : GET THE PARAMETERS FROM THE ALL METHODS !!!!



        }
    }



}
