package parserFile;


//gets a line that has a decleration of a valuble

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser the variables in each scope: global vars and scope vars
 */

public class Variable {

    private static HashMap<String, ArrayList<String>> globalValMap;
    private static final String valNameRegex= "?:[a-zA-Z][a-zA-Z0-9_]*|_[a-zA-Z0-9][a-zA-Z0-9_]*" ;
    private static final String intNumRegex= "[+-]?[1-9][0-9]*|0";
    private static final String doubleNumRegex= "[+-]?([0-9]+(\\.[0-9]*)?|\\.[0-9]+)";
    private static final String stringRegex= "\"[^\\\\'\\\",]+\"";
    private static final String charRegex= "'[^\\\\'\\\",]'";
    private static final String booleanRegex= "true|false|"+intNumRegex+doubleNumRegex;

    private static final String intRegex = "^(int) +("+valNameRegex+") *(= *("+intNumRegex+"))?" +
            " *(, *("+valNameRegex+") *(= *("+intNumRegex+"))? *)*;$";

    private static final  String doubleRegex = "^(double) +("+valNameRegex+") *(= *("+doubleNumRegex+"))?" +
            " *(, *("+valNameRegex+") *(= *("+doubleNumRegex+"))? *)*;$";

    private static final String fullBooleanRegex = "^(boolean) +("+valNameRegex+") *(= *("+booleanRegex+"))?" +
            " *(, *("+valNameRegex+") *(= *("+booleanRegex+"))? *)*;$";

    private static final String fullStringRegex ="^(String) +("+valNameRegex+") *(= *"+stringRegex+")?" +
            " *(, *("+valNameRegex+") *(= *"+stringRegex+")? *)*;$";

    private static final String fullCharRegex = "^(char) +("+valNameRegex+") *(= *"+charRegex+")?" +
            " *(, *("+valNameRegex+") *(= *"+charRegex+")? *)*;$";

 String h = "^(int) +(?:[a-zA-Z][a-zA-Z0-9_]*|_[a-zA-Z0-9][a-zA-Z0-9_]*) *(= *([+-]?[1-9][0-9]*|0))? *(, *(?:[a-zA-Z][a-zA-Z0-9_]*|_[a-zA-Z0-9][a-zA-Z0-9_]*) *(= *([+-]?[1-9][0-9]*|0))? *)*;$";
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

    public static boolean checkLine(String line) {
        if (isValidDeclaration(line,intRegex))
            return true;
        else if (isValidDeclaration(line,doubleRegex))
            return true;
        else if (isValidDeclaration(line,fullBooleanRegex))
            return true;
        else if (isValidDeclaration(line,fullCharRegex))
            return true;
        else if (isValidDeclaration(line,fullStringRegex))
            return true;
        else
            return false;
    }

    private static boolean isValidDeclaration(String line, String regex) {
        boolean isUsed ;
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(line);
        if (matcher.matches()){
            //checks if val is used as a dif type
            isUsed = isValUsed(matcher.group(1), matcher.group(2));
            if (isUsed){
                return false;
            }
            else{
                addValToMap(matcher.group(1), matcher.group(2));
                return true;
            }
        }
        return false;
    }

    private static void addValToMap(String valType, String valName) {
        globalValMap.get(valType).add(valName);
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

    }

    public String getType() {

    }




}
