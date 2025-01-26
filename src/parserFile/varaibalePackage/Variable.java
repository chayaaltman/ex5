package parserFile.varaibalePackage;

import parserFile.Type;
import parserFile.VarProperties;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Variable {
    private static final int TYPE_GROUP_NUM=2;
    private static final int IS_FINAL_GROUP_NUM=1;
    private static final int VARS_GROUP_NUM=3;
    public static final String valNameRegex= "[a-zA-Z][a-zA-Z0-9_]*|_[a-zA-Z0-9][a-zA-Z0-9_]*" ;
    //public static final String valNameRegex = "[a-zA-Z][a-zA-Z0-9_]*|_[a-zA-Z0-9][a-zA-Z0-9_]*";
    public static final String intNumRegex= "[+-]?[0-9]+";
    public static final String doubleNumRegex= "[+-]?([0-9]+(\\.[0-9]*)?|\\.[0-9]+)";
    public static final String stringRegex= "\"[^\\\\'\",]+\"";
    public static final String charRegex= "'[^\\\\'\",]'";
    public static final String booleanRegex= "true|false|"+intNumRegex+"|"+doubleNumRegex;
    public static final String allValueRegex= booleanRegex+"|"+stringRegex+"|"+charRegex;
    public static final String VARIABLE_BODY_REGEX= valNameRegex+"(?: *= *(\\S.*))?(?:, *("+valNameRegex+")(?: *= *(\\S.*))?)* *; *$";
    private static final String GENERAL_VAR_REGEX= "^(final +)?(int|String|double|char|boolean) +("+VARIABLE_BODY_REGEX+")";
    private String REGEX    = "^(final +)?(int|String|double|char|boolean) +(([a-zA-Z][a-zA-Z0-9_]*|_[a-zA-Z0-9][a-zA-Z0-9_])*(?: *= *(\\S.*))?(?:, *([a-zA-Z][a-zA-Z0-9_]*|_[a-zA-Z0-9][a-zA-Z0-9_]*)(?: *= *(\\S.*))?)*) *; *$";
    private static HashMap<Type, ArrayList<HashMap<String, HashMap<VarProperties,Boolean>>>>globalValMap=new HashMap<>();;
    private HashMap<Type, ArrayList<HashMap<String,HashMap<VarProperties,Boolean>>>> localValMap;

    public Variable(){
        localValMap= new HashMap<>();
    }
    private void addToMap(VarProperties scope, Type type, String var, HashMap<VarProperties, Boolean> propertiesMap){
        if (scope== VarProperties.GLOBAL){
            addToScopeMap(type, var, propertiesMap, globalValMap);
        }
        else if (scope== VarProperties.LOCAL){
            addToScopeMap(type, var, propertiesMap, localValMap);
        }
    }


    private void addToScopeMap(Type type, String var, HashMap<VarProperties, Boolean> propertiesMap, HashMap<Type, ArrayList<HashMap<String, HashMap<VarProperties, Boolean>>>> varMap) {
        if(!varMap.containsKey(type)){
            varMap.put(type, new ArrayList<>());
            HashMap<String,HashMap<VarProperties,Boolean> > newMap = new HashMap<>();
            newMap.put(var, propertiesMap);
            varMap.get(type).add(newMap);

        }
        else{
            HashMap<String,HashMap<VarProperties,Boolean> > newMap = new HashMap<>();
            newMap.put(var, propertiesMap);
            varMap.get(type).add(newMap);
        }
    }

    public void checkLine (String line , VarProperties scope, List<Map<String, String>> methodParameters) throws Exception {
        boolean is_final;
        String type;
        String body;
        Pattern pattern = Pattern.compile(REGEX);
        Matcher matcher = pattern.matcher(line);
        //checks if the line is valid syntax
        System.out.println(line);
        if (matcher.matches()){
            is_final= matcher.group(IS_FINAL_GROUP_NUM) != null;
            type = matcher.group(TYPE_GROUP_NUM);
            body = matcher.group(VARS_GROUP_NUM);
            checkBody(type,body,scope,is_final, methodParameters);

        }
        else{
            throw new VariableException(VariableException.ErrorType.VARIABLE_SYNTAX);
        }

    }

    private void checkBody(String type, String body, VarProperties scope, Boolean isFinal, List<Map<String, String>> methodParameters ) throws Exception{
        //HashMap<String, HashMap<VarProperties, Boolean>> variableValueMap = new HashMap<>();
        String[] array = body.split("\\s+");
        System.out.println("array "+array[0]);
        String variableName = array[0];
        try {
            variableName.matches(valNameRegex);
        } catch (Exception e) {
            throw new VariableException(VariableException.ErrorType.VARIABLE_NAME);
        }
        String value = null;
        if (isValUsed(Type.valueOf(type.toUpperCase()), variableName, scope, methodParameters)) {
            throw new VariableException(VariableException.ErrorType.VARIABLE_USED);
        }
        boolean hasValue = false;
        if (array.length > 1){
        if (array[1].equals("=")) {
            for (int i = 2; i < array.length; i++) {
                value = array[i];
                System.out.println("value "+value);

                hasValue = true;
                boolean isValidValue = switch (type) {
                    case "int" -> value == null || value.matches(intNumRegex) || value.matches(valNameRegex);
                    case "double" ->
                            value == null || value.matches(doubleNumRegex) || value.matches(valNameRegex);
                    case "String" ->
                            value == null || value.matches(stringRegex) || value.matches(valNameRegex);
                    case "char" -> value == null || value.matches(charRegex) || value.matches(valNameRegex);
                    case "boolean" ->
                            value == null || value.matches(booleanRegex) || value.matches(valNameRegex);
                    default -> false;
                };

                if (!isValidValue) {
                    throw new VariableException(VariableException.ErrorType.ASSIGN_VALUE);
                }
                if (value == null && isFinal) {
                    throw new VariableException(VariableException.ErrorType.FINAL_VARIABLE);
                }
                if (isValUsed(Type.valueOf(type.toUpperCase()), variableName, scope, methodParameters)) {
                    throw new VariableException(VariableException.ErrorType.VARIABLE_USED);
                }
                assert value != null;
                if (value.matches(valNameRegex)) {
                    if (!isVarAssignedToType(scope, value, Type.valueOf(type.toUpperCase()))) {
                        throw new VariableException(VariableException.ErrorType.ASSIGN_VALUE);
                    }
                }

                HashMap<VarProperties, Boolean> properties = new HashMap<>();
                properties.put(VarProperties.IS_FINAL, isFinal);
                properties.put(VarProperties.IS_ASSIGNED, hasValue);
                addToMap(scope,Type.valueOf(type.toUpperCase()),variableName,properties);
            }
        }
        }
    }

    private static boolean isValInList(ArrayList<HashMap<String, HashMap<VarProperties,Boolean>>> lst, String val) {
        for (HashMap<String, HashMap<VarProperties, Boolean>> map : lst) {
            if (map.containsKey(val)) {
                return true; // The value exists in one of the maps
            }
        }
        return false; // The value is not found
    }

    private static boolean isVarAssigned(ArrayList<HashMap<String, HashMap<VarProperties,Boolean>>> lst, String var){
        for (HashMap<String, HashMap<VarProperties, Boolean>> map : lst) {
            if (map.containsKey(var)) {
                return map.get(var).get(VarProperties.IS_ASSIGNED);// The value exists in one of the maps
            }
        }
        return false;
    }


    //checks if the val is in the  of other types  and
    private  boolean isValUsed(Type valType, String valName, VarProperties scope, List<Map<String, String>> methodParameters) {
        if(methodParameters!=null){
            for (Map<String,String> map : methodParameters){
                if (map.containsKey("name")){
                    if(Objects.equals(map.get("name"), valName)){
                        return true;
                    }
                }
            }
        }

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


    public boolean isVarAssignedToType(VarProperties scope, String var, Type my_type){
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

// checks if the variable in the condition is assigned so it can be used
    public boolean isValidVariableinCondition(String var, VarProperties scope) {
        if (isVarAssignedToType(scope, var, Type.INT))
            return true;
        if (isVarAssignedToType(scope, var, Type.DOUBLE))
            return true;
        return isVarAssignedToType(scope, var, Type.BOOLEAN);

    }
}