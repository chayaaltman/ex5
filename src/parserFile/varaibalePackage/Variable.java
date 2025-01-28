package parserFile.varaibalePackage;

import parserFile.Type;
import parserFile.VarProperties;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class for the variables in the program.
 */
public class Variable {

    /**
     * Regular expressions for the variable types.
     */
    public static final String VAL_NAME_REGEX = "\\s*[a-zA-Z][a-zA-Z0-9_]*|_[a-zA-Z0-9][a-zA-Z0-9_]*\\s*" ;
    /**
     * Regular expressions for the variable values.
     */
    public static final String INT_NUM_REGEX = "\\s*[+-]?[0-9]+";
    /**
     * Regular expressions for the variable values.
     */
    public static final String DOUBLE_NUM_REGEX = "\\s*[+-]?([0-9]+(\\.[0-9]*)?|\\.[0-9]+)";
    /**
     * Regular expressions for the variable values.
     */
    public static final String STRING_REGEX = "\\s*\"[^\\\\'\",]+\"";
    /**
     * Regular expressions for the variable values.
     */
    public static final String CHAR_REGEX = "\\s*'[^\\\\'\",]'";
    /**
     * Regular expressions for the variable values.
     */
    public static final String BOOLEAN_REGEX = "\\s*true|false|"+ INT_NUM_REGEX +"|"+ DOUBLE_NUM_REGEX;
    /**
     * Regular expressions for the variable values.
     */
    public static final String ALL_VALUE_REGEX = BOOLEAN_REGEX +"|"+ STRING_REGEX +"|"+ CHAR_REGEX;
    /**
     * Regular expressions for the variable values.
     */

    public static final String VARIABLE_BODY_REGEX= "^(\\s*([a-zA-Z_][a-zA-Z0-9_]*\\s*=\\s*\\S.*)(\\s*," +
            "\\s*[a-zA-Z_][a-zA-Z0-9_]*\\s*=\\s*\\S.*)*\\s*);\\s*$";
    /**
     * Regular expressions for the variable values.
     */
    private static final String DECLARE_REGEX = "^ *(final +)?(int|String|double|char|boolean) +" +
            "(([a-zA-Z][a-zA" +
            "-Z0-9_]*|_[a-zA-Z0-9][a-zA-Z0-9_])\\s*(?: *=\\s*(\\S.*))?(?:, *([a-zA-Z][a-zA-Z0-9_]*|" +
            "_[a-zA-Z0-9][a-zA-Z0-9_]*)(?: *= *(\\S.*))?)*) *; *$";
    /**
    * Group numbers in the regex
     */
    private static final int TYPE_GROUP_NUM=2;
    private static final int IS_FINAL_GROUP_NUM=1;
    private static final int VARS_GROUP_NUM=3;
    /**
     * Some finals for the split function
     */
    private final static String SPLIT_REGEX = "\\s*=\\s*";
    private final static String SPLIT_COMMA = "\\s*,\\s*";

    private final static int BODY_INDEX = 2;
    /**
     * Some final strings for the types
     */
    private final static String DOUBLE = "double";
    private final static String BOOLEAN = "boolean";
    private final static String INT = "int";
    private final static String STRING = "String";
    private final static String CHAR = "char";
    private final static String NAME = "name";
    /**
     * Global map for the variables.
     */
    private static HashMap<Type, ArrayList<HashMap<String, HashMap<VarProperties,Boolean>>>>
            globalValMap=new HashMap<>();;
    /**
     * Local map for the variables.
      */
    private HashMap<Type, ArrayList<HashMap<String,HashMap<VarProperties,Boolean>>>> localValMap;

    /**
     * Constructor for the `Variable` class.
     */
    public Variable(){
        localValMap= new HashMap<>();
    }

    /**
     * Adds a variable to the map, based on its scope. global or local.
     * @param scope
     * @param type
     * @param var
     * @param propertiesMap
     */
    private void addToMap(VarProperties scope, Type type, String var, HashMap<VarProperties, Boolean>
            propertiesMap){
        if (scope== VarProperties.GLOBAL){ // add to global map
            addToScopeMap(type, var, propertiesMap, globalValMap);
        }
        else if (scope== VarProperties.LOCAL){ // add to local map
            addToScopeMap(type, var, propertiesMap, localValMap);
        }
    }


    /**
     * Adds a variable to the map.
     * @param type
     * @param var
     * @param propertiesMap
     * @param varMap
     */
    private void addToScopeMap(Type type, String var, HashMap<VarProperties, Boolean> propertiesMap,
                               HashMap<Type, ArrayList<HashMap<String, HashMap<VarProperties, Boolean>>>>
                                       varMap) {
        if(!varMap.containsKey(type)){ // if the type is not in the map
            varMap.put(type, new ArrayList<>());
            HashMap<String,HashMap<VarProperties,Boolean> > newMap = new HashMap<>();
            newMap.put(var, propertiesMap);
            varMap.get(type).add(newMap);
        }
        else{ // if the type is in the map
            HashMap<String,HashMap<VarProperties,Boolean> > newMap = new HashMap<>();
            newMap.put(var, propertiesMap);
            varMap.get(type).add(newMap);
        }
    }

    /**
     * Checks the line for variable declaration.
     * @param line
     * @param scope
     * @param methodParameters
     * @throws Exception
     */
    public void checkLine (String line , VarProperties scope, List<Map<String, String>> methodParameters)
            throws Exception {
        boolean is_final;
        String type,body;
        Pattern pattern = Pattern.compile(DECLARE_REGEX);// compile the pattern
        Pattern assignmentPattern = Pattern.compile(VARIABLE_BODY_REGEX);
        Matcher matcher = pattern.matcher(line);
        Matcher assignmentMatcher = assignmentPattern.matcher(line);
        //checks if the line is valid syntax
        if (matcher.matches()){
            is_final= matcher.group(IS_FINAL_GROUP_NUM) != null; // check if the variable is final
            type = matcher.group(TYPE_GROUP_NUM); // get the type
            body = matcher.group(VARS_GROUP_NUM); // get the body
            checkBody(type,body,scope,is_final, methodParameters); // check the body
        }
        else if (assignmentMatcher.matches()){
            checkAssignment(scope, assignmentMatcher.group(1), methodParameters);
        }
        else{
            // if the line is not valid syntax
            throw new VariableException(VariableException.ErrorType.VARIABLE_SYNTAX, line);
        }
    }
    /**
     * Validates and processes variable assignments within a given line of code.
     *
     * @param scope            The current variable scope, represented by a `VarProperties` object,
     *                         which stores information about variables and their properties.
     * @param line             The line of code containing the variable assignments, where assignments
     *                         are expected to be separated by commas (e.g., "a=5, b=6").
     * @param methodParameters A list of method parameters represented as maps, where each map contains
     *                         key-value pairs describing parameter attributes (e.g., name, type).
     * @throws VariableException If the assignment violates variable rules, such as assigning to
     *                           an undeclared variable or a variable declared as `final`.
     */

    private void checkAssignment(VarProperties scope, String line, List<Map<String, String>> methodParameters
    ) throws VariableException {
        String[] pairs = line.split(SPLIT_COMMA);
        for (String pair : pairs) {
            String[] array = pair.split(SPLIT_REGEX);
            String variableName = array[0];
            if (!isVariableExistsNotFinal(scope, variableName)){
                throw new VariableException(VariableException.ErrorType.VARIABLE_NAME);
            }
            String type = getType(scope, variableName);
            assert type != null;
            processVariableAssignment(type.toLowerCase(),array,scope,false,methodParameters,
                    variableName);

        }

    }



    /**
     * Checks the body of the variable declaration.
     * @param type gives type of string
     * @param body
     * @param scope
     * @param isFinal
     * @param methodParameters
     * @throws Exception
     */
    private void checkBody(String type, String body, VarProperties scope, Boolean isFinal, List<Map<String,
            String>> methodParameters) throws Exception {
        // Split the input by commas
        String[] pairs = body.split(SPLIT_COMMA);
        for (String pair : pairs) {
            String[] array = pair.split(SPLIT_REGEX);
            String variableName = array[0];
            validateVariableName(variableName);
            checkVariableUsage(type, variableName, scope, methodParameters);
            if (array.length > 1 ) {
                processVariableAssignment(type, array, scope, isFinal, methodParameters, variableName);
            }
            else if (isFinal) {
                throw new VariableException(VariableException.ErrorType.FINAL_VARIABLE);
            }
        }

    }

    /**
     * Validates the variable name.
     * @param variableName
     * @throws VariableException
     */
    private void validateVariableName(String variableName) throws VariableException {
        try {
            variableName.matches(VAL_NAME_REGEX);
        } catch (Exception e) {
            throw new VariableException(VariableException.ErrorType.VARIABLE_NAME);
        }
    }

    /**
     * Checks if the variable is used in the code.
     * @param type
     * @param variableName
     * @param scope
     * @param methodParameters
     * @throws VariableException
     */
    private void checkVariableUsage(String type, String variableName, VarProperties scope, List<Map<String,
            String>> methodParameters) throws VariableException {
        if (isValUsed(Type.valueOf(type.toUpperCase()), variableName, scope, methodParameters)) {
            throw new VariableException(VariableException.ErrorType.VARIABLE_USED);
        }
    }

    /**
     * Processes the variable assignment.
     * @param type
     * @param array
     * @param scope
     * @param isFinal
     * @param methodParameters
     * @param variableName
     * @throws VariableException
     */
    private void processVariableAssignment(String type, String[] array, VarProperties scope, Boolean isFinal,
                                           List<Map<String, String>> methodParameters, String variableName )
            throws VariableException {
        String value = null;
        boolean hasValue = false;
        for (int i = BODY_INDEX-1; i < array.length; i++) { // iterate over the array
            value = array[i];
            hasValue = true;
            validateValue(type, value,scope);
            checkVariableUsage(type, variableName, scope, methodParameters); // check if the variable is used
            addVariableToMap(scope, type, variableName, isFinal, hasValue);
        }
    }

    /**
     * Validates the value of the variable using case switch.
     * @param type
     * @param value
     * @throws VariableException
     */
    private void validateValue(String type, String value, VarProperties scope) throws VariableException {
        boolean isValidValue = switch (type) {
            case INT -> value == null || value.matches(INT_NUM_REGEX) ;
            case DOUBLE -> value == null || value.matches(DOUBLE_NUM_REGEX) ;
            case STRING -> value == null || value.matches(STRING_REGEX) ;
            case  CHAR -> value == null || value.matches(CHAR_REGEX);
            case BOOLEAN -> value == null || value.matches(BOOLEAN_REGEX) ;
            default -> false;
        };
        assert value != null;
        if (value.matches(VAL_NAME_REGEX)){
            if (!isVarAssignedToType(scope, value,
                    Type.valueOf(type.toUpperCase()))) {
                throw new VariableException(VariableException.ErrorType.ASSIGN_VALUE);
            }
            else{
                isValidValue = true;
            }
        }
        // if the value is not valid throw an exception
        if (!isValidValue) {

            throw new VariableException(VariableException.ErrorType.ASSIGN_VALUE);
        }
    }

    /**
     * Adds a variable to the map.
     * @param scope
     * @param type
     * @param variableName
     * @param isFinal
     * @param hasValue
     */
    private void addVariableToMap(VarProperties scope, String type, String variableName, Boolean isFinal,
                                  boolean hasValue) {
        HashMap<VarProperties, Boolean> properties = new HashMap<>();
        // add the properties to the map
        properties.put(VarProperties.IS_FINAL, isFinal);
        properties.put(VarProperties.IS_ASSIGNED, hasValue);
        addToMap(scope, Type.valueOf(type.toUpperCase()), variableName, properties);
    }

    /**
     * Checks if the value is in the list.
     * @param lst
     * @param val
     * @return
     */
    private static boolean isValInList(ArrayList<HashMap<String, HashMap<VarProperties,Boolean>>> lst,
                                       String val) {
        for (HashMap<String, HashMap<VarProperties, Boolean>> map : lst) {
            if (map.containsKey(val)) {
                return true; // The value exists in one of the maps
            }
        }
        return false; // The value is not found
    }

    /**
     * Checks if the variable is assigned.
     * @param lst
     * @param var
     * @return
     */
    private static boolean isVarAssigned(ArrayList<HashMap<String, HashMap<VarProperties,Boolean>>> lst,
                                         String var){
        for (HashMap<String, HashMap<VarProperties, Boolean>> map : lst) {
            if (map.containsKey(var)) {
                return map.get(var).get(VarProperties.IS_ASSIGNED);// The value exists in one of the maps
            }
        }
        return false;
    }

    /**
     * Checks if the value is used in the code.
     * @param valType
     * @param valName
     * @param scope
     * @param methodParameters
     * @return
     */
    private  boolean isValUsed(Type valType, String valName, VarProperties scope, List<Map<String,
            String>> methodParameters) {
        if(methodParameters!=null){
            for (Map<String,String> map : methodParameters){
                if (map.containsKey(NAME)){
                    if(Objects.equals(map.get(NAME), valName)){
                        return true;
                    }
                }
            }
        }
        return isValUsedInScope(valType, valName, scope);
    }

    /**
     * Checks if the value is used in the scope.
     * @param valType
     * @param valName
     * @param scope
     * @return
     */
    private Boolean isValUsedInScope(Type valType, String valName, VarProperties scope){
        if (scope== VarProperties.GLOBAL){
            for (Type type : globalValMap.keySet()) {
                if (valType!=type) {
                    if (isValInList(globalValMap.get(type), valName)){
                        return true;
                    }
                }
            }
        }
        else if(scope== VarProperties.LOCAL){
            for (Type type : localValMap.keySet()) {
                if (valType!=type) {
                    if (isValInList(localValMap.get(type), valName)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Checks if the variable is assigned to the correct type.
     * @param scope
     * @param var
     * @param my_type
     * @return
     */
    private boolean isVarAssignedToType(VarProperties scope, String var, Type my_type){
        if (scope== VarProperties.GLOBAL){
            for (Type type : globalValMap.keySet()) {
                if (my_type==type) {
                    if (isValInList(globalValMap.get(type), var)){
                        return isVarAssigned(globalValMap.get(type), var);
                    }
                }
            }
        }
        else if (scope== VarProperties.LOCAL){
            for (Type type : localValMap.keySet()) {
                if (my_type==type) {
                    if (isValInList(localValMap.get(type), var))
                    {
                        return isVarAssigned(localValMap.get(type), var);
                    }
                }
            }
        }
        return  false;
    }

    /**
     * Checks if the variable is valid in the condition.
     * @param var
     * @param scope
     * @return
     */
    public boolean isValidVariableInCondition(String var, VarProperties scope) {
        if (isVarAssignedToType(scope, var, Type.INT))
            return true;
        if (isVarAssignedToType(scope, var, Type.DOUBLE))
            return true;
        return isVarAssignedToType(scope, var, Type.BOOLEAN);

    }
    private boolean isVariableExistsNotFinal(VarProperties scope, String var) {
        if (scope== VarProperties.GLOBAL){
            for (Type type : globalValMap.keySet()) {
                    if (isValInList(globalValMap.get(type), var)){
                        return isVarNotFinal(globalValMap.get(type), var);
                }
            }
        }
        else if (scope== VarProperties.LOCAL){
            for (Type type : localValMap.keySet()) {
                    if (isValInList(localValMap.get(type), var)) {
                        return isVarNotFinal(localValMap.get(type), var);
                    }
                }
            }
        return false;
    }
    /**
     * Retrieves the type of a variable within the specified scope.
     *
     * @param scope The scope of the variable, which can be `GLOBAL` or `LOCAL`, represented by
     *              the `VarProperties` enum.
     * @param var   The name of the variable whose type is to be determined.
     * @return The type of the variable as a `String` if the variable exists in the specified scope;
     *         `null` if the variable does not exist in the provided scope.
     */
    private String getType(VarProperties scope, String var){
        if (scope== VarProperties.GLOBAL){
            for (Type type : globalValMap.keySet()) {
                if (isValInList(globalValMap.get(type), var)){
                    return type.toString();
                }
            }
        }
        else if (scope== VarProperties.LOCAL){
            for (Type type : localValMap.keySet()) {
                if (isValInList(localValMap.get(type), var)) {
                    return type.toString();
                }
            }
        }
        return null;
    }
/**
 * Checks if a given variable is **not** declared as `final` in the provided list of type maps.
 *
 * @param typeList A list of maps where:
 *                 - Each map has variable names as keys (e.g., `String`).
 *                 - The value for each key is another map, where:
 *                   - The key is a property of the variable (e.g., `IS_FINAL`).
 *                   - The value is a `Boolean` indicating the property state.
 * @param var      The name of the variable to check.
 * @return `true` if the variable exists in the `typeList` and is **not** declared as `final`;
 *         `false` otherwise.
 */
    private boolean isVarNotFinal(ArrayList<HashMap<String, HashMap<VarProperties, Boolean>>>
                                       typeList, String var) {
        for (HashMap<String, HashMap<VarProperties, Boolean>> map : typeList){
            if (map.containsKey(var)) {
                return !map.get(var).get(VarProperties.IS_FINAL);
            }
        }
        return false;
    }

}