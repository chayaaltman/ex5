package parserFile;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;

/**
 * This class will manage the If statements: every scope that begins with If
 */
public class parserIf
{
    private final String line;

    public parserIf(String line) {
        this.line = line;
    }

    private String parserConditionLine(String line){
        List<String> conditionList = new ArrayList<>();
        String condition = null;
        // Regex to match and extract the condition
        String regex = "^if\\s+\\(\\s*([a-zA-Z0-9_!><=+/*-]+)\\s*\\)\\s*\\{$\n";
        // Matches "if(condition) {"
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(line);
        // Check if the line matches the pattern
        if (matcher.matches()) {
            // Extract the condition inside the parentheses
            condition = matcher.group(1).trim();
            boolean parserCond = parserCondition(condition);
            if (!parserCond) {
                throw new IllegalArgumentException("invalid condition");
            }
        }
        else {
            throw new IllegalArgumentException("Invalid line format: " + line);
        }
        return condition;
    }

    /**
     * Parser the condition inside the (). it must be a boolean value as true or false
     * options:
     * true/ false
     * initialized boolean (double or int)
     * double or int constant or value like 5,3..
     * @param condition
     * @return
     */
    private boolean parserCondition(String condition){
        // Trim whitespace around the condition
        condition = condition.trim();
        if (condition == null || condition.isEmpty()) {
            return false;
        }
        // parser boolean condition: Regex for a valid variable or constant (int/double or boolean)
        String validValue = "true|false|\\d+(\\.\\d+)?|[a-zA-Z_][a-zA-Z_0-9]*";
        // Check for single condition
        if (!condition.matches(validValue)) {
            return false;
        }
        while (matcher.find()) {
            String value = matcher.group(); // Extract the matched value (variable, constant, or boolean)
            // Validate the value (example: check if variables are declared/initialized)
            if (!isValidValue(value)) {
                return false; // If any value is invalid, return false
            }
        }
        // Regex for multiple conditions (no nested parentheses allowed)
        String multipleConditionRegex = validValue + "\\s*(\\|\\||&&)\\s*" + validValue;
        if (!condition.matches(multipleConditionRegex)) {
            return false;
        }
        return true;
    }

    private boolean isValidValue(String value){
        // check if the variable is inside the vars table
        // if its not, return false
        return true;
    }

    private void compileSubroutine(String subroutine){
        /// call the parserVar and parserMethod classes functions to deal with it
    }







}
