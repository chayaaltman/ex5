package parserFile;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;

public class IfWhile {
    private final List<String> body;
    private String condition;

    public IfWhile(List<String> body) {
        this.body = body;
    }

    public void parserSubroutine(){
        parserConditionLine();
        parserBody();
    }

    private void parserBody(){
        for (String line : body) {
            // Check if the line is a valid statement
            if (line.startsWith("if") || line.startsWith("while")) {
                IfWhile ifWhile = new IfWhile(body);
                ifWhile.parserSubroutine();
            }
            else if (line.startsWith("int") || line.startsWith("char") || line.startsWith("String") || line.startsWith("double") || line.startsWith("boolean")) {
                Variable variable = new Variable(line);
                variable.checkLine(line);
            }
            else if (line.startsWith("return")) {
                // handle return
                handleReturn(line);
            }
        }
    }

    private boolean parserConditionLine(){
        List<String> conditionList = new ArrayList<>();
        String condition = null;
        // Regex to match and extract the condition
        String regex = "^if\\s+\\(\\s*([a-zA-Z0-9_!><=+/*-]+)\\s*\\)\\s*\\{$\n";
        // Matches "if(condition) {"
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(body.get(0));
        // Check if the line matches the pattern
        if (matcher.matches()) {
            // Extract the condition inside the parentheses
            this.condition = matcher.group(1).trim();
            boolean parserCond = parserCondition(condition);
            if (!parserCond) {
                return false;
            }
        }
        else {
            return false;
        }
        return true;
    }

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




}
