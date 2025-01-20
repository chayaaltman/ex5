package parserFile;


//gets a line that has a decleration of a valuble

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser the variables in each scope: global vars and scope vars
 */

public class Variable {
    private static final int TYPE_GROUP_NUM=2;
    private static final int IS_FINAL_GROUP_NUM=1;
    private static final int VARS_GROUP_NUM=3;


    private static HashMap<String, ArrayList<HashMap<String, String>>>globalValMap;
    private HashMap<String, ArrayList<HashMap<String,HashMap<String,Boolean>>>> localValMap;
    private static final String valNameRegex= "?:[a-zA-Z][a-zA-Z0-9_]*|_[a-zA-Z0-9][a-zA-Z0-9_]*" ;
    private static final String intNumRegex= "[+-]?[0-9]+";
    private static final String doubleNumRegex= "[+-]?([0-9]+(\\.[0-9]*)?|\\.[0-9]+)";
    private static final String stringRegex= "\"[^\\\\'\\\",]+\"";
    private static final String charRegex= "'[^\\\\'\\\",]'";
    private static final String booleanRegex= "true|false|"+intNumRegex+"|"+doubleNumRegex;
    private static final String finalRegex= "(final)? +";


    //private static final String intRegex = "^(int) +("+valNameRegex+") *(= *("+intNumRegex+"))?" +
         //   " *(, *("+valNameRegex+") *(= *("+intNumRegex+"))? *)*;$";
    private static final String fullIntRegex= "^"+finalRegex+" (int) +("+valNameRegex+"(?: *= *"+intNumRegex+")?(?:, *("+valNameRegex+")(?: *= *"+intNumRegex+")?)*) *;$";
    private static final String fullDoubleRegex= "^"+finalRegex+"(double) +("+valNameRegex+"(?: *= *"+doubleNumRegex+")?(?:, *("+valNameRegex+")(?: *= *"+doubleNumRegex+")?)*) *;$";


    //private static final  String doubleRegex = "^(double) +("+valNameRegex+") *(= *("+doubleNumRegex+"))?" +
          //  " *(, *("+valNameRegex+") *(= *("+doubleNumRegex+"))? *)*;$";
    private static final String fullBooleanRegex= "^"+finalRegex+"(boolean) +("+valNameRegex+"(?: *= *"+booleanRegex+")?(?:, *("+valNameRegex+")(?: *= *"+booleanRegex+")?)*) *;$";

    //private static final String fullBooleanRegex = "^(boolean) +("+valNameRegex+") *(= *("+booleanRegex+"))?" +
        //    " *(, *("+valNameRegex+") *(= *("+booleanRegex+"))? *)*;$";
    private static final String fullStringRegex= "^"+finalRegex+"(String) +("+valNameRegex+"(?: *= *"+stringRegex+")?(?:, *("+valNameRegex+")(?: *= *"+stringRegex+")?)*) *;$";

    //private static final String fullStringRegex ="^(String) +("+valNameRegex+") *(= *"+stringRegex+")?" +
         //   " *(, *("+valNameRegex+") *(= *"+stringRegex+")? *)*;$";
    private static final String fullCharRegex= "^"+finalRegex+"(char) +("+valNameRegex+"(?: *= *"+charRegex+")?(?:, *("+valNameRegex+")(?: *= *"+charRegex+")?)*) *;$";

    //private static final String fullCharRegex = "^(char) +("+valNameRegex+") *(= *"+charRegex+")?" +
         //   " *(, *("+valNameRegex+") *(= *"+charRegex+")? *)*;$";
    private static final String allValueRegex= booleanRegex+"|"+stringRegex+"|"+charRegex;
    private static final String VARIABLE_REGEX_HASAMA= valNameRegex+"(?: *= *"+allValueRegex+")?(?:, *("+valNameRegex+")(?: *= *"+allValueRegex+")?)*) *;$";

    private final String line;


    public Variable(String line ) {
        this.line = line;
        globalValMap= new HashMap<>();
        globalValMap.put("String", new ArrayList<>());
        globalValMap.put("int", new ArrayList<>());
        globalValMap.put("double", new ArrayList<>());
        globalValMap.put("boolean", new ArrayList<>());
        globalValMap.put("char", new ArrayList<>());
    }

    public  void checkLine(String line, String scope) throws Exception {
        isValidDeclaration(line,fullIntRegex, scope);
        isValidDeclaration(line,fullDoubleRegex,scope);
        isValidDeclaration(line,fullBooleanRegex,scope);
        isValidDeclaration(line,fullCharRegex,scope);
        isValidDeclaration(line,fullStringRegex,scope);
        isValidDeclaration(line,VARIABLE_REGEX_HASAMA,scope);
    }

    private  void isValidDeclaration(String line, String regex, String scope) throws Exception {
        boolean isUsed ;

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(line);

        if (matcher.matches()){
            //checks if val is used as a dif type
            Map<String, Boolean> varMaP= createVarMap(matcher.group(VARS_GROUP_NUM));

            //checks if num is final
            boolean isFinal = matcher.group(IS_FINAL_GROUP_NUM) != null;

            for (String var : varMaP.keySet()){
                if (isFinal&&!varMaP.get(var)){
                    // if statment is final checks if has a value for all the vars
                        throw new Exception("is a final statement and doesnt have a value for the variable");
                    }
                isUsed = isValUsed(matcher.group(TYPE_GROUP_NUM), var);
                if (isUsed){
                    throw new Exception("this var name is used as a different type ");
                }
                else{
                    if (Objects.equals(scope, "global")){
                        addValToMapGlobal(matcher.group(TYPE_GROUP_NUM),var);

                    }
                    else if (Objects.equals(scope, "local")){
                        addValToMapLocal(matcher.group(TYPE_GROUP_NUM),var);
                    }
                }
            }
        }
        throw new Exception("is not a valid declaration of a variable");
    }

    private Map<String, Boolean> createVarMap(String line) {
        // Regex to capture variable name and optional value
        String regex = "(?:[a-zA-Z][a-zA-Z0-9_]*|_[a-zA-Z0-9][a-zA-Z0-9_]*)(?: *= *[+-]?[0-9]+)?"; //only for int
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(line);
        Map<String, Boolean> variableValueMap = new LinkedHashMap<>();

        while (matcher.find()) {
            String match = matcher.group();
            String[] parts = match.split("=");
            String variableName = parts[0].trim(); // Variable name
            boolean hasValue = parts.length > 1;// Check if a value exists
            variableValueMap.put(variableName, hasValue);
        }
        return variableValueMap;
    }




    private static void addValToMapGlobal(String valType, String valName) {
        globalValMap.get(valType).add(valName);
    }

    private  void addValToMapLocal(String valType, String valName) {
        localValMap.get(valType).add(valName);
    }

    // val in list returns true - val not in list returns false
    private static boolean isValInList(List<String> lst, String val) {
        for (String valName : lst) {
            if (Objects.equals(val, valName)) {
                return true;
            }
        }
        return false;
    }
    //checks if the val is in the  of other types  and
    private static boolean isValUsed( String valType, String valName) {
        for (String type : globalValMap.keySet()) {
            if (!Objects.equals(valType, type)) {
                if (isValInList(globalValMap.get(type), valName)){
                    return true;
                }
            }
        }
        return false;
    }

    public static HashMap<String, ArrayList<String>> getValMap() {
        return globalValMap;
    }

    /// ///***** TODO: implement the following methods
    public String getName() {
        return "";
    }

    public String getType() {
        return "";

    }




}
