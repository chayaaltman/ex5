package parserFile.methodPackage;
import parserFile.*;
import parserFile.ifWhilePackage.IfWhile;
import parserFile.varaibalePackage.Variable;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is responsible for parsing methods
 * It checks the method declaration, the body of the method, and the method call
 * It also checks the scope of the variables inside the method
 */
public class Method {

    /**
    * The regex for the method declaration "void methodName(int a, double b, String c...) {
     **/
    private static final String METHOD_NAME_REGEX="[a-zA-Z][a-zA-Z]*|[a-zA-Z0-9][a-zA-Z0-9_]*";
//    private static final String methodDeclarationRegex ="^ *void +("+METHOD_NAME_REGEX+") *\\(( *(int|char|double|" +
//            "boolean|String) +"+ Variable.valNameRegex+" *(, *(int|char|double|boolean|String) +"+Variable.valNameRegex+")*)?" +
//            " *\\) *\\{ *$";
    private static final String METHOD_REGEX = "^(\\w+)\\s+(\\w+)\\s*\\((.*)\\)\\s*\\{\\s*$";
    private static final String RETURN_TYPE_REGEX = "void";
    private static final String PARAM_TYPE_REGEX = "int|double|boolean|String|char";

    private static final int RETURN_TYPE_GROUP = 1;
    private static final int METHOD_NAME_GROUP = 2;


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
    // a static list that holds all of the methods and their parameters
    private static List<Map<String, List<Map<String, String>>>> allMethods = new ArrayList<>();

    public Method(List<String> body) {
        this.parameters = new ArrayList<>();
        this.body = body;
        this.variable = new Variable();
    }

    public void handleMethod(String methodName) throws Exception {
        try {
            this.name=methodName;
            handleBody();
        } catch (Exception e) {
            throw new Exception("Error in method: " + e.getMessage());
        }
    }

    public void methodDeclaration(String line) throws Exception {
        line = line.trim();
        Pattern pattern = Pattern.compile(METHOD_REGEX);
        Matcher matcher = pattern.matcher(line);
        if (!matcher.matches()) {
            throw new MethodException(MethodException.ErrorType.METHOD_DECLARATION, line);
        }
        String returnType = matcher.group(RETURN_TYPE_GROUP);
        String methodName = matcher.group(METHOD_NAME_GROUP);
        this.name=methodName;
        String params = matcher.group(3);

        if (!returnType.equals(RETURN_TYPE_REGEX)) {
            throw new MethodException(MethodException.ErrorType.RETURN_TYPE, returnType);
        }

        if (!methodName.matches(METHOD_NAME_REGEX)) {
            throw new MethodException(MethodException.ErrorType.METHOD_DECLARATION,methodName);
        }

        if (isMethodExists(methodName)) {
            throw  new MethodException(MethodException.ErrorType.METHOD_EXISTS,methodName);
        }

        if (params != null && !params.isEmpty()) {

            String[] paramsArr = params.split(",");
            for (String param : paramsArr) {
                Map<String, String> paramMap = getStringStringMap(param);
                this.parameters.add(paramMap);
            }
        }
        allMethods.add(Map.of(methodName, parameters));


    }

    private static Map<String, String> getStringStringMap(String param) throws MethodException {
        String[] paramParts = param.trim().split("\\s+");
        if (paramParts.length != 2) {
            throw  new MethodException(MethodException.ErrorType.PARAMETER);

        }
        String paramType = paramParts[0].trim();
        String paramName = paramParts[1].trim();
        if (!paramType.matches(PARAM_TYPE_REGEX)) {
            throw  new MethodException(MethodException.ErrorType.PARAMETER, paramType);
        }
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("type", paramType);
        paramMap.put("name", paramName);
        return paramMap;
    }

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
    private void handleBody() throws Exception {
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
                throw new MethodException(MethodException.ErrorType.NEW_METHOD, line);
            } else if (line.matches(METHOD_CALL_REGEX)) {
                throwFromMethodCall(line);
            }
            else if(!line.matches("^\\s*}\\s*$")&&!line.matches("\\s*")){
                throw new MethodException(MethodException.ErrorType.SYNTAX, line);
            }
        }
        if (!returnFlag) {
            throw new MethodException(MethodException.ErrorType.NO_RETURN_STATEMENT);
        }
    }

    public  boolean isVariableDeclaration(String line) {
        return line.startsWith(PARAM_TYPE_REGEX) || line.startsWith("final");
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
        throw new MethodException(MethodException.ErrorType.METHOD_CALL);
    }

    // add local vars: use the variables class. call it after
    public void handleVariables(String line) throws Exception {
        try {
            // Try checking the line as a local variable
            this.variable.checkLine(line, VarProperties.LOCAL, allMethods.get(0).get(getMethodName()));
            // If it succeeds, exit the method and avoid checking the global context
            return;
        } catch (Exception localException) {
            // If the local check fails, attempt to check as a global variable
            try {

                this.variable.checkLine(line, VarProperties.GLOBAL, allMethods.get(0).get(getMethodName()));
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


    ///  ************************** HANDLE METHOD CALL **************************
    public void handleMethodCall(String line) throws Exception {

        // check if the method is defined
        String methodName = extractMethodName(line);
        if (!isMethodExists(methodName)) {
            throw new MethodException(MethodException.ErrorType.METHOD_CALL,methodName);
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
            throw new MethodException(MethodException.ErrorType.PARAMETER_NUMBER,methodName);
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
                throw new MethodException(MethodException.ErrorType.PARAMETER_TYPE, methodName);
            }
        }

        // Recursive call for the next parameter
        validateParameterTypesRecursive(methodName, originalParams, params, index + 1);
    }

    private boolean isVariableDefinedInAnyScope(String param) {
        // Check the local scope
        if (variable.isValidVariableinCondition(param, VarProperties.LOCAL)) {
            return true;
        }
        // Check the global scope (or other scopes if applicable)
        return variable.isValidVariableinCondition(param, VarProperties.GLOBAL);
        // Add additional scope checks here if necessary
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
