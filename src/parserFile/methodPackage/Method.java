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
    /**
     * The regex for the method declaration "void methodName(int a, double b, String c...) {"
     */
    private static final String METHOD_REGEX = "^(\\w+)\\s+(\\w+)\\s*\\((.*)\\)\\s*\\{\\s*$";
    /**
     * The regex for the return type "void"
     */
    private static final String RETURN_TYPE_REGEX = "void";
    /**
     * The regex for the parameter type "int|double|boolean|String|char"
     */
    private static final String PARAM_TYPE_REGEX = "int|double|boolean|String|char";
    /**
     * The regex for the return statement "return;"
     */
    private static final String RETURN_REGEX = "^\\s*return\\s*;\\s*$";
    /**
     * The regex for the method call "methodName(a, b, c...);"
     */
    public static final String METHOD_CALL_REGEX = "^\\s*(" + METHOD_NAME_REGEX + ")\\s*\\(\\s*(("
            + Variable.allValueRegex + "|" + Variable.valNameRegex + ")(\\s*,\\s*("
            + Variable.allValueRegex + "|" + Variable.valNameRegex + "))*)?\\s*\\)\\s*;$";

    private static final String CLOSE_LINE_REGEX= "^\\s*}\\s*$";
    private static final String EMPTY_LINE_REGEX= "\\S*";
    private static final String PARAM_SPLIT_SPACE= "\\s+";
    /**
     * The groups index
     */
    private static final int RETURN_TYPE_GROUP = 1;
    private static final int METHOD_NAME_GROUP = 2;
    private static final int PARAMS_GROUP = 3;

    private static final int NUM_OF_PARAMS_PARTS= 2;
    private final static int BEGIN_INDEX_FINAL = 6;
    /**
     * Some finals strings
     */
    private static final String TYPE = "type";
    private static final String NAME = "name";
    private static final String PARAMS_SPLIT= ",";
    private static final String RETURN_BEGIN = "return";
    private static final String FINAL_REGEX = "final";
    private final static String DOUBLE = "double";
    private final static String BOOLEAN = "boolean";
    private final static String INT = "int";
    private final static String STRING = "String";
    private final static String CHAR = "char";

    /**
     * The fields of the Method class
     */
    private String name=null; // the name of the method
    private Variable variable;
    private List<Map<String, String>> parameters;
    private List<String> body; // a list that holds the body of the method
    // a static list that holds all the methods and their parameters
    private static List<Map<String, List<Map<String, String>>>> allMethods = new ArrayList<>();

    /**
     * Constructor for the Method class
     * @param body
     */
    public Method(List<String> body) {
        this.parameters = new ArrayList<>();
        this.body = body;
        this.variable = new Variable();
    }

    /**
     * Handles the method declaration
     * @param methodName
     * @throws Exception
     */
    public void handleMethod(String methodName) throws Exception {
        try {
            this.name=methodName;
            handleBody(); // Handle the body of the method
        } catch (Exception e) {
            throw new MethodException(MethodException.ErrorType.METHOD_ERROR, e.getMessage());
        }
    }

    /**
     * Handles the method declaration line
     * @param line
     * @throws Exception
     */
    public void methodDeclaration(String line) throws Exception {
        line = line.trim(); // Remove leading and trailing whitespaces
        Matcher matcher = getMatcher(line); // Get the matcher for the method declaration
        validateMethodDeclaration(line, matcher); // Validate the method declaration
        extractMethodDetails(matcher); // Extract the method details
    }

    /**
     * Get the matcher for the method declaration
     * @param line
     * @return
     */
    private Matcher getMatcher(String line) {
        Pattern pattern = Pattern.compile(METHOD_REGEX);
        return pattern.matcher(line);
    }

    /**
     * Validate the method declaration.
     * Check if the return type and method name are valid
     * @param line
     * @param matcher
     * @throws MethodException
     */
    private void validateMethodDeclaration(String line, Matcher matcher) throws MethodException {
        // Check if the method declaration matches the regex
        if (!matcher.matches()) {
            throw new MethodException(MethodException.ErrorType.METHOD_DECLARATION, line);
        }
        // Extract the return type and method name
        String returnType = matcher.group(RETURN_TYPE_GROUP);
        String methodName = matcher.group(METHOD_NAME_GROUP);
        // Check if the return type and method name are valid
        if (!returnType.equals(RETURN_TYPE_REGEX)) {
            throw new MethodException(MethodException.ErrorType.RETURN_TYPE, returnType);
        }
        // Check if the method name is valid
        if (!methodName.matches(METHOD_NAME_REGEX)) {
            throw new MethodException(MethodException.ErrorType.METHOD_DECLARATION, methodName);
        }
        // Check if the method already exists
        if (isMethodExists(methodName)) {
            throw new MethodException(MethodException.ErrorType.METHOD_EXISTS, methodName);
        }
    }

    /**
     * Extract the method details
     * @param matcher
     * @throws MethodException
     */
    private void extractMethodDetails(Matcher matcher) throws MethodException {
        this.name = matcher.group(METHOD_NAME_GROUP); // set the name of the method
        String params = matcher.group(PARAMS_GROUP); // Get the parameters
        // Check if the parameters are valid
        if (params != null && !params.isEmpty()) {
            String[] paramsArr = params.split(PARAMS_SPLIT);
            for (String param : paramsArr) { // Iterate over the parameters
                Map<String, String> paramMap = getStringStringMap(param);
                this.parameters.add(paramMap); // Add the parameter to the list
            }
        }
        allMethods.add(Map.of(this.name, parameters)); // Add the method to the list
    }

    /**
     * Extract the parameter details
     * @param param
     * @return
     * @throws MethodException
     */
    private static Map<String, String> getStringStringMap(String param) throws MethodException {
        String[] paramParts = param.trim().split(PARAM_SPLIT_SPACE);
        // Check if the parameter list is valid
        if (paramParts.length != NUM_OF_PARAMS_PARTS) {
            throw  new MethodException(MethodException.ErrorType.PARAMETER);
        }
        // Extract the parameter type and name
        String paramType = paramParts[0].trim();
        String paramName = paramParts[1].trim();
        // Check if the parameter type is valid
        if (!paramType.matches(PARAM_TYPE_REGEX)) {
            throw  new MethodException(MethodException.ErrorType.PARAMETER, paramType);
        }
        // put the parameter type and name in a map
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put(TYPE, paramType);
        paramMap.put(NAME, paramName);
        return paramMap;
    }

    /**
     * Get the name of the method
     * @return
     */
    public String getMethodName(){
        return this.name;
    }

    /**
     * Check if the method exists in the list
     * @param methodName
     * @return
     */
    private boolean isMethodExists(String methodName) {
        // Iterate over the list of methods
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
        // move over the body of the method: from the first { to the last }(exclude)
        for (int i = 1; i < body.size()-1; i++) {
            String line = body.get(i);
            // Check the type of the line
            if (isVariableDeclaration(line)) {
                handleVariables(line);
            // if the line is an if/while statement
            } else if (line.matches(Parser.IF_WHILE_REGEX)) {
                handleIfWhileStatement(i);
            // if the line is a return statement
            } else if (isReturnStatement(line, i)) {
                returnFlag = true;}
            else if(line.startsWith(RETURN_BEGIN) && !line.matches(RETURN_REGEX)){
                throw new MethodException(MethodException.ErrorType.RETURN_STATEMENT, line);
            // if the line is a new method declaration- throw an exception
            } else if (line.matches(Parser.METHOD_REGEX)) {
                throw new MethodException(MethodException.ErrorType.NEW_METHOD, line);
            // if the line is a method call
            } else if (line.matches(METHOD_CALL_REGEX)) {
                throwFromMethodCall(line);
            }
            // if the line is not empty and not a closing bracket, throw an exception
            else if(!line.matches(CLOSE_LINE_REGEX)&&!line.matches(EMPTY_LINE_REGEX)){
                throw new MethodException(MethodException.ErrorType.SYNTAX, line);
            }
        }
        // if there is not a return statement in the last line of the method, throw an exception
        if (!returnFlag) {
            throw new MethodException(MethodException.ErrorType.NO_RETURN_STATEMENT);
        }
    }

    /**
     * Check if the line is a variable declaration
     * @param line
     * @return
     */
    public  boolean isVariableDeclaration(String line) {
        return line.startsWith(PARAM_TYPE_REGEX) || line.startsWith(FINAL_REGEX);
    }

    /**
     * Handle the if/while statement
     * @param i
     * @throws Exception
     */
    public void handleIfWhileStatement(int i) throws Exception {
        try {
            handleIfWhile(i);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Check if the line is a return statement
     * @param line
     * @param index
     * @return
     */
    public boolean isReturnStatement(String line, int index) {
        int LAST_LINE_OF_CODE = body.size() -2;
        return line.matches(RETURN_REGEX) && index == body.size() - LAST_LINE_OF_CODE;
    }


    /**
     * Handle the method call
     * @param line
     * @throws Exception
     */
    public  void throwFromMethodCall(String line) throws Exception {
        try {
            handleMethodCall(line);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * return the parameters of the method
     * @param methodName
     * @return
     * @throws Exception
     */
    public List<Map<String, String>> getMethodParameters(String methodName) throws Exception {
        for (Map<String, List<Map<String, String>>> methodMap : allMethods) {
            if (methodMap.containsKey(methodName)) {
                return methodMap.get(methodName);
            }
        }
        throw new MethodException(MethodException.ErrorType.METHOD_CALL);
    }

    /**
     * Handle the variables
     * @param line
     * @throws Exception
     */
    public void handleVariables(String line) throws Exception {
        try {
            // Try checking the line as a local variable
            this.variable.checkLine(line, VarProperties.LOCAL, allMethods.get(0).get(getMethodName()));
            // If it succeeds, exit the method and avoid checking the global context
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

    /**
     * Handle the if/while statement
     * @param index
     * @throws Exception
     */
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
    /**
     * Handle the method call
     * Check if the method is defined, if the number of parameters is the same, and if the parameters
     * are the same Type
     * @param line
     * @throws Exception
     */
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

    /**
     * Extract the method name from the method call
     * @param line
     * @return
     */
    private  String extractMethodName(String line) {
        Pattern pattern = Pattern.compile(METHOD_CALL_REGEX);
        Matcher matcher = pattern.matcher(line);
        if(matcher.matches()) {
            return matcher.group(RETURN_TYPE_GROUP);
        }
        return null;
    }

    /**
     * Extract the parameters from the method call
     * @param line
     * @return
     * @throws Exception
     */
    private String[] extractParameters(String line) throws Exception {
        String paramsPart = line.split("\\(")[1].split("\\)")[0].trim(); // Get content between parentheses
        if (paramsPart.isEmpty()) {
            return new String[0]; // No parameters
        }
        return paramsPart.split("\\s*,\\s*"); // Split by commas
    }

    /**
     * Validate the number of parameters: that the call has the same number of parameters as the method declaration
     * @param methodName
     * @param originalParams
     * @param params
     * @throws Exception
     */
    private  void validateParameterCount(String methodName,
                                               List<Map<String, String>> originalParams,
                                               String[] params) throws Exception {
        if (originalParams.size() != params.length) {
            throw new MethodException(MethodException.ErrorType.PARAMETER_NUMBER,methodName);
        }
    }


    /**
     * Validate the parameters: that the call has the same Type as the method declaration
     * @param methodName
     * @param originalParams
     * @param params
     * @throws Exception
     */
    private void validateParameterTypes(String methodName,
                                               List<Map<String, String>> originalParams,
                                               String[] params) throws Exception {
        validateParameterTypesRecursive(methodName, originalParams, params, 0);
    }

    /**
     * Validate the parameters recursively
     * @param methodName
     * @param originalParams
     * @param params
     * @param index
     * @throws Exception
     */
    private void validateParameterTypesRecursive(String methodName,
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

    /**
     * Check if the variable is defined in any scope
     * @param param
     * @return
     */
    private boolean isVariableDefinedInAnyScope(String param) {
        // Check the local scope
        if (variable.isValidVariableInCondition(param, VarProperties.LOCAL)) {
            return true;
        }
        // Check the global scope (or other scopes if applicable)
        return variable.isValidVariableInCondition(param, VarProperties.GLOBAL);
        // Add additional scope checks here if necessary
    }

    /**
     * Check if the parameter is valid for the expected Type
     * @param type
     * @param param
     * @return
     */
    private  boolean isValidParameter(String type, String param) {
        // if the Type is final, it's still legal to call the method with a none final parameter
        String finalReg = FINAL_REGEX+ " ";
        if (type.startsWith(finalReg)) {
            type = type.substring(BEGIN_INDEX_FINAL); // Remove the "final " prefix
        }
        // Check if the parameter matches the expected Type
        return switch (type) {
            case DOUBLE -> param.matches(Variable.doubleNumRegex) || param.matches(Variable.intNumRegex);
            case BOOLEAN ->
                    param.matches(Variable.booleanRegex) || param.matches(Variable.intNumRegex) || param.matches(Variable.doubleNumRegex);
            case INT -> param.matches(Variable.intNumRegex);
            case STRING -> param.matches(Variable.stringRegex);
            case CHAR -> param.matches(Variable.charRegex);
            default -> false;
        };
    }

}
