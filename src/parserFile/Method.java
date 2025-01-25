package parserFile;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Method {

//    /**
//     * Regexes for the different types of variables
//     */
//    private final String regexInt = Variable.intNumRegex;
//    private final String regexDouble = Variable.doubleNumRegex;
//    private final String regexBoolean = Variable.booleanRegex;
//    private final String regexString = Variable.stringRegex;
//    private final String regexChar = Variable.charRegex;
    /**
    * The regex for the method declaration "void methodName(int a, double b, String c...) {
     **/
    private static final String METHOD_NAME_REGEX="[a-zA-Z][a-zA-Z]*|[a-zA-Z0-9][a-zA-Z0-9_]*";
    private static final String methodDeclarationRegex ="^ *void +("+METHOD_NAME_REGEX+") *\\(( *(int|char|double|" +
            "boolean|String) +"+Variable.valNameRegex+" *(, *(int|char|double|boolean|String) +"+Variable.valNameRegex+")*)?" +
            " *\\) *\\{ *$";
    private static String methodRegex = "^(\\w+)\\s+(\\w+)\\s*\\((.*)\\)$";

    /**
     * The regex for the return statement "return;"
     */

    private static final String returnRegex = "^\\s*return\\s*;\\s*$";
    /**
     * The regex for the method call "methodName(a, b, c...);"
     */
    public static final String METHOD_CALL_REGEX = "^\\s*(" + METHOD_NAME_REGEX + ")\\s*\\(\\s*(("
            + Variable.allValueRegex + "|" + Variable.valNameRegex + ")(\\s*,\\s*("
            + Variable.allValueRegex + "|" + Variable.valNameRegex + "))*)?\\s*\\)\\s*;$";


    private String name=null;
    private Variable variable;
    private List<Map<String, String>> parameters;
    private List<String> body;
    private static List<Map<String, List<Map<String, String>>>> allMethods = new ArrayList<>(); // a static list that holds all of the methods and their parameters

    public Method(List<String> body) {
        this.parameters = new ArrayList<>();
        this.body = body;
        this.variable = new Variable();
    }

    public void handleMethod(String methodName) throws Exception {
        try {
            //methodDeclaration(body.get(0));
            this.name=methodName;
            handleBody(methodName);
        } catch (Exception e) {
            throw new Exception("Error in method: " + e.getMessage());
        }
    }

    public void methodDeclaration(String line) throws Exception {
        line = line.trim();
        if (!line.endsWith("{")) {
            throw new Exception("Invalid method declaration: " + line);
        }

        line = line.substring(0, line.length() - 1).trim();
        Pattern pattern = Pattern.compile(methodRegex);
        Matcher matcher = pattern.matcher(line);

        if (!matcher.matches()) {
            throw new Exception("Invalid method declaration: " + line);
        }
        String returnType = matcher.group(1);
        String methodName = matcher.group(2);
        this.name=methodName;
        String params = matcher.group(3);

        if (!returnType.equals("void")) {
            throw new Exception("Invalid return type: " + returnType);
        }

        if (!methodName.matches(METHOD_NAME_REGEX)) {
            throw new Exception("Invalid method name: " + methodName);
        }

        if (isMethodExists(methodName)) {
            throw new Exception(methodName + " already is used as a different method");
        }

        if (params != null && !params.isEmpty()) {

            String[] paramsArr = params.split(",");
            for (String param : paramsArr) {
                String[] paramParts = param.trim().split("\\s+");
                if (paramParts.length != 2) {
                    throw new Exception("Invalid parameter: " + param);
                }
                String paramType = paramParts[0].trim();
                String paramName = paramParts[1].trim();
                if (!paramType.matches("int|double|boolean|String|char")) {
                    throw new Exception("Invalid parameter type: " + paramType);
                }
                Map<String, String> paramMap = new HashMap<>();
                paramMap.put("type", paramType);
                paramMap.put("name", paramName);
                this.parameters.add(paramMap);

            }

        }
        allMethods.add(Map.of(methodName, parameters));


    }

//    public void methodDeclaration(String line) throws Exception {
//        // Match the regex
//        if (!line.matches(methodDeclarationRegex)) {
//            throw new Exception("Invalid method declaration: " + line);
//        }
//        //String methodName = line.split("\\(")[0].trim().split("\\s+")[1];  // Extract method name
//        Pattern pattern = Pattern.compile(methodDeclarationRegex);
//        Matcher matcher = pattern.matcher(line);
//        System.out.println("line: "+line);
//        System.out.println("matcher: "+matcher);
//        /// / *********CHECK GROUPS*********
//        String methodName = matcher.group(1);
//        System.out.println("method name: "+methodName);
//        if (isMethodExists(methodName)){
//            throw new Exception(methodName+" already is used as a different method");
//        }
//        // Extract the parameters (if any)
//        String paramsPart = matcher.group(2);
//        System.out.println("paramsPart: "+paramsPart);
//        //String  paramsPart = line.split("\\(")[1].split("\\)")[0].trim();  // Get everything between parentheses
//        if (!paramsPart.isEmpty()) {
//            String[] params = paramsPart.split(",");
//            for (String param : params) {
//                String[] paramParts = param.trim().split("\\s+");
//                String paramType = paramParts[0].trim();
//                String paramName = paramParts[1].trim();
//                // Create a map for the parameter and its Type
//                Map<String, String> paramMap = new HashMap<>();
//                paramMap.put("type", paramType);
//                paramMap.put("name", paramName);
//                // Add the parameter map to the list for this method
//                Map<String, List<Map<String, String>>> methodMap = new HashMap<>();
//                // check if the method name is already in the list of methods names
//                // add the parameters to the list of parameters
//                this.parameters.add(paramMap);
//                // add the method name and the parameters to the method map
//                this.name = methodName;
//                methodMap.put(this.name, this.parameters);
//                allMethods.add(methodMap);
//            }
//        }
//        allMethods.add(Map.of(methodName, parameters)); // add the new method to the method names list
//    }

    public String getMethodName(){
        return this.name;
    }

    private  boolean isMethodExists(String methodName) {

        for (Map<String, List<Map<String, String>>> map : allMethods) {
            if (map.containsKey(methodName)) {
                return true;
            }
        }
        return false;
    }
    /**
     * Handles the body of the method
     * @throws Exception
     */
    private void handleBody(String methodName) throws Exception {
        boolean returnFlag = false; // Check if the method ends with a return statement
        System.out.println("body: "+body);
        for (int i = 1; i < body.size()-1; i++) {
            String line = body.get(i);
            System.out.println("line: "+line);
            if (isVariableDeclaration(line)) {
                handleVariables(line);
            } else if (line.matches(Parser.IF_WHILE_REGEX)) {
                handleIfWhileStatement(i);
            } else if (isReturnStatement(line, i)) {
                returnFlag = true;
            } else if (line.matches(Parser.METHOD_REGEX)) {
                throw new Exception("Method cannot contain another method");
            } else if (line.matches(METHOD_CALL_REGEX)) {
                throwFromMethodCall(line);
            }
            else if(!line.matches("^\\s*}\\s*$")&&!line.matches("\\s*")){
                throw new Exception("Invalid line syntax: " + line);

            }
        }
        if (!returnFlag) {
            throw new Exception("Method does not end with a return statement");
        }
    }

    public  boolean isVariableDeclaration(String line) {
        if (line.startsWith("int") || line.startsWith("double") || line.startsWith("boolean") ||
                line.startsWith("String") || line.startsWith("char") || line.startsWith("final")) {
            return true;
        }
        //return line.matches(Parser.VAR_DEC_REGEX) || line.matches(Variable.VARIABLE_BODY_REGEX);
        return false;
    }

    public void handleIfWhileStatement(int i) throws Exception {
        try {
            handleIfWhile(i);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public boolean isReturnStatement(String line, int index) {
        return line.matches(returnRegex) && index == body.size() - 2;
    }


    public  void throwFromMethodCall(String line) throws Exception {
        try {
            handleMethodCall(line);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public List<Map<String, String>> getMethodParameters(String methodName) throws Exception {
        for (Map<String, List<Map<String, String>>> methodMap : allMethods) {
            if (methodMap.containsKey(methodName)) {
                return methodMap.get(methodName);
            }
        }
        throw new Exception("Method " + methodName + " is not defined");
    }

    // add local vars: use the variables class. call it after
    public void handleVariables(String line) throws Exception {


        try {
            // Try checking the line as a local variable
            this.variable.checkLine(line, varProperties.LOCAL, allMethods.get(0).get(getMethodName()));
            // If it succeeds, exit the method and avoid checking the global context
            return;
        } catch (Exception localException) {
            // If the local check fails, attempt to check as a global variable
            try {

                this.variable.checkLine(line, varProperties.GLOBAL, allMethods.get(0).get(getMethodName()));
            } catch (Exception globalException) {
                // If both local and global checks fail, throw an exception
                throw new Exception("Error in variable declaration: " + globalException.getMessage());
            }
        }
    }

    public void handleIfWhile(int index) throws Exception {
        // Get the body of the if/while statement
        List<String> body = Parser.getIfWhileScope(index, this.body);
        // Create a new IfWhile object and parse the body
        Variable variableNew= new Variable();
        IfWhile ifWhile = new IfWhile(body, variableNew, null);
        // Recursive call for condition and body
        ifWhile.parserIfWhileScope();
    }

    public  void handleReturn(String line) throws Exception {
        if (!line.matches(returnRegex)) {
            throw new Exception("Invalid return statement: " + line);
        }
    }

    ///  ************************** HANDLE METHOD CALL **************************
    public void handleMethodCall(String line) throws Exception {

        // check if the method is defined
        String methodName = extractMethodName(line);
        if (!isMethodExists(methodName)) {

            throw new Exception("Method " + methodName + " is not defined");
        }
        // the original parameters in the declaration of the method
        List<Map<String, String>> originalParams = getMethodParameters(methodName);
        // the parameters in the method call
        String[] params = extractParameters(line);

        // check if the number of parameters is the same
        try{
           validateParameterCount(methodName, originalParams, params);
           // check if the parameters are the same Type
            validateParameterTypes(methodName, originalParams, params);
        }
        catch (Exception e){
            throw new Exception(e.getMessage());
        }

    }

    private  String extractMethodName(String line) {
        Pattern pattern = Pattern.compile(METHOD_CALL_REGEX);
        Matcher matcher = pattern.matcher(line);
        if(matcher.matches()) {
            return matcher.group(1);
        }
        return null;
    }

    private  String[] extractParameters(String line) throws Exception {

        String paramsPart = line.split("\\(")[1].split("\\)")[0].trim(); // Get content between parentheses
        if (paramsPart.isEmpty()) {
            return new String[0]; // No parameters
        }
        return paramsPart.split("\\s*,\\s*"); // Split by commas
    }

    private  void validateParameterCount(String methodName,
                                               List<Map<String, String>> originalParams,
                                               String[] params) throws Exception {
        if (originalParams.size() != params.length) {
            throw new Exception("Invalid number of parameters for method " + methodName);
        }
    }


    private  void validateParameterTypes(String methodName,
                                               List<Map<String, String>> originalParams,
                                               String[] params) throws Exception {
        validateParameterTypesRecursive(methodName, originalParams, params, 0);
    }

    private  void validateParameterTypesRecursive(String methodName,
                                                        List<Map<String, String>> originalParams,
                                                        String[] params,
                                                        int index) throws Exception {
        if (index >= params.length ) {
            // Base case: all parameters have been checked
            return;
        }
        String type = originalParams.get(index).get("type");
        String param = params[index];

        // Check if the parameter matches the expected Type
        boolean isValid = isValidParameter(type, param);

        if (!isValid) {
            // If the parameter is a variable, check if it is defined in any scope
            if (!isVariableDefinedInAnyScope(param)) {
                throw new Exception("Invalid parameter Type for method " + methodName);
            }
        }

        // Recursive call for the next parameter
        validateParameterTypesRecursive(methodName, originalParams, params, index + 1);
    }

    private boolean isVariableDefinedInAnyScope(String param) {
        // Check the local scope
        if (variable.isValidVariableinCondition(param, varProperties.LOCAL)) {
            return true;
        }
        // Check the global scope (or other scopes if applicable)
        if (variable.isValidVariableinCondition(param, varProperties.GLOBAL)) {
            return true;
        }
        // Add additional scope checks here if necessary
        return false;
    }




    private  boolean isValidParameter(String type, String param) {
        // if the Type is final, its still legal to call the method with a none final parameter
        if (type.startsWith("final ")) {
            type = type.substring(6); // Remove the "final " prefix
        }
        // Check if the parameter matches the expected Type
        return switch (type) {
            case "double" -> param.matches(Variable.doubleNumRegex) || param.matches(Variable.intNumRegex);
            case "boolean" ->
                    param.matches(Variable.booleanRegex) || param.matches(Variable.intNumRegex) || param.matches(Variable.doubleNumRegex);
            case "int" -> param.matches(Variable.intNumRegex);
            case "String" -> param.matches(Variable.stringRegex);
            case "char" -> param.matches(Variable.charRegex);
            default -> false;
        };
    }

}
