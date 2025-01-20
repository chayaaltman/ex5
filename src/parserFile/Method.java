package parserFile;
import java.util.*;

public class Method {

    private final String regexInt = Variable.intNumRegex;
    private final String regexDouble = Variable.doubleNumRegex;
    private final String regexBoolean = Variable.booleanRegex;
    private final String regexString = Variable.stringRegex;
    private final String regexChar = Variable.charRegex;
    private static final String methodDeclarationRegex ="^\\s*void\\s+([a-zA-Z][a-zA-Z0-9_]*)\\s*\\(\\s*" +
            "((int|char|double|boolean|String)\\s+[a-zA-Z_][a-zA-Z0-9_]*(\\s*,\\s*(int|char|double|boolean|String)" +
            "\\s+[a-zA-Z_][a-zA-Z0-9_]*)*)?\\s*\\)\\s*\\{\\s*$";
    private static final String returnRegex = "^\\s*return\\s*;\\s*$";
    private static final String methodCallRegex = "^\\s*[a-zA-Z][a-zA-Z0-9_]*\\s*\\([^)]*\\)\\s*;$";



    private String name;
    private List<Map<String, String>> parameters;
    private List<String> body;
    private List<Map<String, String>> localVariables;
    private static List<Map<String, List<Map<String, String>>>> allMethods = new ArrayList<>(); // a static list that holds all of the methods and their parameters

    public Method(List<String> body) {
        this.parameters = new ArrayList<>();
        this.localVariables = new ArrayList<>();
        this.body = body;
    }

    public void handleMethod() throws Exception {
        try {
            methodDeclaration(body.get(0));
            handleBody();
        } catch (Exception e) {
            throw new Exception("Error in method: " + e.getMessage());
        }
    }

    private void methodDeclaration(String line) throws Exception {
        // Match the regex
        if (!line.matches(methodDeclarationRegex)) {
            throw new Exception("Invalid method declaration: " + line);
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
                Map<String, List<Map<String, String>>> methodMap = new HashMap<>();
                methodMap.put(this.name, this.parameters);
                allMethods.add(methodMap);
            }
        }
        allMethods.add(Map.of(methodName, parameters)); // add the new method to the method names list
    }

    private void handleBody() throws Exception {
        boolean returnFlag = false;
        for (int i = 0; i < body.size(); i++) {
            String line = body.get(i);
            if (type.startsWithType(line)){
                handleVariables(line);
            } else if (line.startsWith("if") || line.startsWith("while")) {
                handleIfWhile();
            } else if (line.startsWith("return")) {
                if (i == body.size() - 1) {
                    returnFlag = true;
                }
                handleReturn(line);
            } else {
                handleMethodCall(line);
            }
        }
        if (!returnFlag) {
            throw new Exception("Method does not end with a return statement");
        }
    }

    private List<Map<String, String>> getMethodParameters(String methodName) throws Exception {
        for (Map<String, List<Map<String, String>>> methodMap : allMethods) {
            if (methodMap.containsKey(methodName)) {
                return methodMap.get(methodName);
            }
        }
        throw new Exception("Method " + methodName + " is not defined");
    }

    // add local vars: use the variables class. call it after
    private void handleVariables(String line) throws Exception {
        Variable variable = new Variable(line);
        try{
            variable.checkLine(line, varProperties.LOCAL);
            Map<String, String> localVar = new HashMap<>();
            localVar.put("type", variable.getType());
            localVar.put("name", variable.getName());
            localVariables.add(localVar);
        }
        catch (Exception e) {
            throw new Exception("Error in variable declaration: " + e.getMessage());
        }
    }

    private void handleIfWhile(){
        for (String line : body) {
            if (line.startsWith("if") || line.startsWith("while")) {
                IfWhile ifWhile = new IfWhile(body);
                ifWhile.parserSubroutine();
            }
        }
    }

    public static void handleReturn(String line) throws Exception {
        if (!line.matches(returnRegex)) {
            throw new Exception("Invalid return statement: " + line);
        }
    }

//    private void handleMethodCall(String line) throws Exception {
//        String methodCallRegex = "^\\s*[a-zA-Z][a-zA-Z0-9_]*\\s*\\([^)]*\\)\\s*;$";
//        if (!line.matches(methodCallRegex)) {
//            throw new Exception("Invalid method call: " + line);
//        }
//        String methodName = line.substring(0, line.indexOf('(')).trim();
//        if (!allMethods.contains(methodName)) {
//            throw new Exception("Method " + methodName + " is not defined");
//        }
//        List<Map<String, String>> originalParams = getMethodParameters(methodName);
//        // Extract the parameters (if any)
//        String paramsPart = line.split("\\(")[1].split("\\)")[0].trim();  // Get everything between parentheses
//        if (!paramsPart.isEmpty()) {
//            String[] params = paramsPart.split(",");
//            // not have the same number of parameters:
//            if (originalParams.size() != params.length) {
//                throw new Exception("Invalid number of parameters for method " + methodName);
//            }
//            // the loop goes on the parameters and checks if they are the same type
//            for (int i=0; i<params.length; i++){
//                String type = originalParams.get(i).get("type");
//                if (Objects.equals(type, "double")){
//                    if (!params[i].matches(regexInt) && !params[i].matches(regexDouble)){
//                        throw new Exception("Invalid parameter type for method " + methodName);
//                    }
//                }
//                else if (Objects.equals(type, "boolean")){
//                    if (!params[i].matches(regexBoolean) && !params[i].matches(regexInt) && !params[i].matches(regexDouble)){
//                        throw new Exception("Invalid parameter type for method " + methodName);
//                    }
//                }
//                else if (Objects.equals(type, "int")){
//                    if (!params[i].matches(regexInt)){
//                        throw new Exception("Invalid parameter type for method " + methodName);
//                    }
//                }
//                else if (Objects.equals(type, "String")){
//                    if (!params[i].matches(regexString)){
//                        throw new Exception("Invalid parameter type for method " + methodName);
//                    }
//                }
//                else if (Objects.equals(type, "char")){
//                    if (!params[i].matches(regexChar)){
//                        throw new Exception("Invalid parameter type for method " + methodName);
//                    }
//                }
//                else if (Objects.equals(type, "final")){
//                    if (!params[i].matches(finalRegex)){
//                        throw new Exception("Invalid parameter type for method " + methodName);
//                    }
//                }
//            }
//        }
//    }

    ///  ************************** HANDLE METHOD CALL **************************
    public void handleMethodCall(String line) throws Exception {
        // check if the method calling is valid
        if (!isValidMethodCall(line)) {
            throw new Exception("Invalid method call: " + line);
        }
        // check if the method is defined
        String methodName = extractMethodName(line);
        if (!allMethods.contains(methodName)) {
            throw new Exception("Method " + methodName + " is not defined");
        }
        // the original parameters in the declaration of the method
        List<Map<String, String>> originalParams = getMethodParameters(methodName);
        // the parameters in the method call
        String[] params = extractParameters(line);
        // check if the number of parameters is the same
        validateParameterCount(methodName, originalParams, params);
        // check if the parameters are the same type
        validateParameterTypes(methodName, originalParams, params);
    }

    private boolean isValidMethodCall(String line) {
        return line.matches(methodCallRegex);
    }

    private String extractMethodName(String line) {
        return line.substring(0, line.indexOf('(')).trim();
    }

    private String[] extractParameters(String line) throws Exception {
        String paramsPart = line.split("\\(")[1].split("\\)")[0].trim(); // Get content between parentheses
        if (paramsPart.isEmpty()) {
            return new String[0]; // No parameters
        }
        return paramsPart.split("\\s*,\\s*"); // Split by commas
    }

    private void validateParameterCount(String methodName,
                                        List<Map<String, String>> originalParams,
                                        String[] params) throws Exception {
        if (originalParams.size() != params.length) {
            throw new Exception("Invalid number of parameters for method " + methodName);
        }
    }

    private void validateParameterTypes(String methodName,
                                        List<Map<String, String>> originalParams,
                                        String[] params) throws Exception {
        for (int i = 0; i < params.length; i++) {
            String type = originalParams.get(i).get("type");
            String param = params[i];
            if (!isValidParameter(type, param)) {
                throw new Exception("Invalid parameter type for method " + methodName);
            }
        }
    }

    private boolean isValidParameter(String type, String param) {
        if (type.equals("double")) {
            return param.matches(regexInt) || param.matches(regexDouble);
        } else if (type.equals("boolean")) {
            return param.matches(regexBoolean) || param.matches(regexInt) || param.matches(regexDouble);
        } else if (type.equals("int")) {
            return param.matches(regexInt);
        } else if (type.equals("String")) {
            return param.matches(regexString);
        } else if (type.equals("char")) {
            return param.matches(regexChar);
        } else if (type.equals("final")) { // if the type is final, its still legal to call the method with a non final parameter
            return true;
        }
        return false; // Unknown type
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

    public boolean isVarInLocalAndAssigned(String varName) {
        for (Map<String, String> localVar : localVariables) {
            if (localVar.get("name").equals(varName)) {
                /// / TODO: check if the variable is assigned
                if (Variable.isAssigned(localVar)) {
                    return true;
                }
            }
        }
        return false;
    }
}
