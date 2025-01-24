package parserFile;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Variable {
    private static final int TYPE_GROUP_NUM=2;
    private static final int IS_FINAL_GROUP_NUM=1;
    private static final int VARS_GROUP_NUM=3;
    public static final String valNameRegex= "[a-zA-Z][a-zA-Z0-9_]*|_[a-zA-Z0-9][a-zA-Z0-9_]*" ;
    public static final String intNumRegex= "[+-]?[0-9]+";
    public static final String doubleNumRegex= "[+-]?([0-9]+(\\.[0-9]*)?|\\.[0-9]+)";
    public static final String stringRegex= "\"[^\\\\'\",]+\"";
    public static final String charRegex= "'[^\\\\'\",]'";
    public static final String booleanRegex= "true|false|"+intNumRegex+"|"+doubleNumRegex;
    public static final String allValueRegex= booleanRegex+"|"+stringRegex+"|"+charRegex;
    public static final String VARIABLE_BODY_REGEX= valNameRegex+"(?: *= *(\\S.*))?(?:, *("+valNameRegex+")(?: *= *(\\S.*))?)* *; *$";
    private static final String GENERAL_VAR_REGEX= "^(final +)?(int|String|double|char|boolean) +("+VARIABLE_BODY_REGEX+")";
    private String REGEX    = "^(final +)?(int|String|double|char|boolean) +(([a-zA-Z][a-zA-Z0-9_]*|_[a-zA-Z0-9][a-zA-Z0-9_])*(?: *= *(\\S.*))?(?:, *([a-zA-Z][a-zA-Z0-9_]*|_[a-zA-Z0-9][a-zA-Z0-9_]*)(?: *= *(\\S.*))?)*) *; *$";
    private static HashMap<Type, ArrayList<HashMap<String, HashMap<varProperties,Boolean>>>>globalValMap;
    private HashMap<Type, ArrayList<HashMap<String,HashMap<varProperties,Boolean>>>> localValMap;

    public Variable(){
        globalValMap= new HashMap<>();
        localValMap= new HashMap<>();
    }

    public void checkLine (String line , varProperties scope) throws Exception {
        boolean is_final;
        String type;
        String body;
        Pattern pattern = Pattern.compile(REGEX);
        Matcher matcher = pattern.matcher(line);
        //checks if the line is valid syntax
        if (matcher.matches()){
            is_final= matcher.group(IS_FINAL_GROUP_NUM) != null;
            type = matcher.group(TYPE_GROUP_NUM);
            body = matcher.group(VARS_GROUP_NUM);
            checkBody(type,body,scope,is_final);

        }
        else{
            throw new Exception("invalid syntax for sjava variables ");
        }

    }
    private void checkBody(String type, String body, varProperties scope, Boolean is_final) throws Exception {
        // Initialize a map to track variables and their properties
        HashMap<String, HashMap<varProperties, Boolean>> variableValueMap = new HashMap<>();

        // Regex pattern to match individual variables and their optional values
        Pattern pattern = Pattern.compile("("+valNameRegex+")" + "(?: *= *(" + allValueRegex + "))?");
        Matcher matcher = pattern.matcher(body);
//IM HERE THE VALUE THINKS ITS NULL EVEN THOUGH ITS NOT THE PROBLEM IS THEB GROUPS OF REGEX
        while (matcher.find()) {
            System.out.println(body);
            String variableName = matcher.group(1);// Variable2 name
            System.out.println("var name "+variableName);
            String value = matcher.group(2); // Optional value, can be null// THE PRIBLEM IS HERE
            boolean hasValue = value != null;

            // Check if the value matches the Type
            boolean isValidValue = switch (type) {
                case "int" -> value == null || value.matches(intNumRegex) || value.matches(valNameRegex);
                case "double" ->
                        value == null || value.matches(doubleNumRegex) || value.matches(valNameRegex);
                case "String" -> value == null || value.matches(stringRegex) || value.matches(valNameRegex);
                case "char" -> value == null || value.matches(charRegex) || value.matches(valNameRegex);
                case "boolean" -> value == null || value.matches(booleanRegex) || value.matches(valNameRegex);
                default -> false;
            };

            if (!isValidValue) {
                throw new Exception("Invalid value for variable " + variableName + " of Type " + type);
            }
            if (value==null&&is_final){
                throw new Exception("final but variable was not assigned");
            }
            // checks if variable exists in map as a different Type
            if (isValUsed(Type.valueOf(type.toUpperCase()), variableName, scope))
            {
                throw new Exception(("variable is already used as a different Type"));
            }
            System.out.println("value"+value);

            if (hasValue){

                if (value.matches(valNameRegex)){
                    System.out.println("HEREbleName");
                    // checks that the value is the same Type as variable and is assigned
                    if (!isVarAssignedToType(scope, value, Type.valueOf(type.toUpperCase()))){
                        throw  new Exception("variable is not assigned to right Type");
                    }
                }
            }

            // Add the variable to the map with its properties
            HashMap<varProperties, Boolean> properties = new HashMap<>();
            properties.put(varProperties.IS_FINAL, is_final);
            properties.put(varProperties.IS_ASSIGNED, hasValue);
            variableValueMap.put(variableName, properties);
        }

        // Store the variables in the appropriate map (global or local based on scope)
        if (scope == varProperties.GLOBAL) {
            globalValMap.computeIfAbsent(Type.valueOf(type.toUpperCase()), k -> new ArrayList<>()).add(variableValueMap);
        } else {
            localValMap.computeIfAbsent(Type.valueOf(type.toUpperCase()), k -> new ArrayList<>()).add(variableValueMap);
        }
    }

    private static boolean isValInList(ArrayList<HashMap<String, HashMap<varProperties,Boolean>>> lst, String val) {
        for (HashMap<String, HashMap<varProperties, Boolean>> map : lst) {
            if (map.containsKey(val)) {
                return true; // The value exists in one of the maps
            }
        }
        return false; // The value is not found
    }

    private static boolean isVarAssigend(ArrayList<HashMap<String, HashMap<varProperties,Boolean>>> lst, String var){
        for (HashMap<String, HashMap<varProperties, Boolean>> map : lst) {
            if (map.containsKey(var)) {
                return map.get(var).get(varProperties.IS_ASSIGNED);// The value exists in one of the maps
            }
        }
        return false;
    }


    //checks if the val is in the  of other types  and
    private  boolean isValUsed(Type valType, String valName, varProperties scope) {
        if (scope==varProperties.GLOBAL){
            for (Type type : globalValMap.keySet()) {
                if (valType!=type) {
                    if (isValInList(globalValMap.get(type), valName)){
                        return true;
                    }
                }
            }
        }
        else if(scope==varProperties.LOCAL){
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


    public boolean isVarAssignedToType(varProperties scope, String var, Type my_type){
        if (scope==varProperties.GLOBAL){
            for (Type type : globalValMap.keySet()) {
                if (my_type==type) {
                    if (isValInList(globalValMap.get(type), var)){
                        return isVarAssigend(globalValMap.get(type), var);
                    }
                }
            }
        }
        else if (scope==varProperties.LOCAL){
            for (Type type : localValMap.keySet()) {
                if (my_type==type) {
                    if (isValInList(localValMap.get(type), var))
                    {
                        return isVarAssigend(localValMap.get(type), var);
                    }
                }
            }
        }
        return  false;
    }

// checks if the variable in the condition is assigned so it can be used
    public boolean isValidVariableinCondition(String var, varProperties scope) {
        if (isVarAssignedToType(scope, var, Type.INT))
            return true;
        if (isVarAssignedToType(scope, var, Type.DOUBLE))
            return true;
        return isVarAssignedToType(scope, var, Type.BOOLEAN);

    }
}