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
public class parserVal {

    private static HashMap<String, ArrayList<String>> valMap;
    private static final String valNameRegex= "(?:[a-zA-Z][a-zA-Z0-9_]*|_[a-zA-Z0-9][a-zA-Z0-9_]*)" ;
    private static final String intNumRegex= "[+-]?[1-9][0-9]*|0";
    private static final String doubleNumRegex= "[+-]?([0-9]+(\\.[0-9]*)?|\\.[0-9]+)";
    //int- works
    private static final String intRegex = "^(int) +("+valNameRegex+") *(= *("+intNumRegex+") *)?;$";
    //double works
    private static final  String doubleRegex = "^(double) +("+valNameRegex+") *(= *("+doubleNumRegex+"))? *;$";
    private static final String booleanRegex = "^(boolean) +("+valNameRegex+") *(= *(true|false|"+intNumRegex+doubleNumRegex+"))? *;";
    //string works
    private static final String stringRegex ="^(String) +("+valNameRegex+") *(= *\"[^\\'\",]+\")? *;$";

    private static final String charRegex = "^(char) +("+valNameRegex+") *(= *'[^\\'\",]')? *;$";


    private final String line;

    public parserVal(String line ) {
        this.line = line;
        valMap= new HashMap<>();
        valMap.put("String", new ArrayList<>());
        valMap.put("int", new ArrayList<>());
        valMap.put("double", new ArrayList<>());
        valMap.put("boolean", new ArrayList<>());
        valMap.put("char", new ArrayList<>());
    }

    public static boolean checkLine(String line) {
        if (isValidDeclaration(line,intRegex))
            return true;
        else if (isValidDeclaration(line,doubleRegex))
            return true;
        else if (isValidDeclaration(line,booleanRegex))
            return true;
        else if (isValidDeclaration(line,charRegex))
            return true;
        else if (isValidDeclaration(line,stringRegex))
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
        valMap.get(valType).add(valName);
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
        for (String type : valMap.keySet()) {
            if (!Objects.equals(valType, type)) {
                if (isValInList(valMap.get(type), valName)){
                    return true;
                }
            }
        }
        return false;
    }




}
