package parserFile;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;

public class IfWhile {
    private static final String conditionRegex = "^if\\s+\\(\\s*([a-zA-Z0-9_!><=+/*-]+)\\s*\\)\\s*\\{$";
    private static final String validValueRegex = "true|false|\\d+(\\.\\d+)?|[a-zA-Z_][a-zA-Z_0-9]*";
    private static final String multipleConditionRegex = validValueRegex + "\\s*(\\|\\||&&)\\s*" + validValueRegex;


    private final List<String> body;
    private String condition;
    //private final Method method;

    public IfWhile(List<String> body) {
        this.body = body;
        //this.method = method;
        //this.condition = extractCondition(body.get(0));
    }

    public void parserSubroutine(){
        try{
            parserConditionLine(); // parser the first line of the if/while
            parserBody();
        }
        catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void parserBody() throws Exception {
        for (int i= 1; i<body.size(); i++){
            String line = this.body.get(i);
            // Check if the line is a valid statement
            if (line.startsWith("if") || line.startsWith("while")) {
                // TODO: SEND THE NEW BODY TO THE IF/WHILE PARSER
                List<String> body = Parser.getIfWhileScope(i);
                IfWhile ifWhile = new IfWhile(body);
                // recursive call for condition and body
                ifWhile.parserConditionLine();
                ifWhile.parserBody();
            }
            else if (line.startsWith("int") || line.startsWith("char") || line.startsWith("String") ||
                    line.startsWith("double") || line.startsWith("boolean")) {
                Variable variable = new Variable(line);
                try {
                    // check if the var is in the local or global list:
                    variable.checkLine(line, varProperties.LOCAL);
                    variable.checkLine(line, varProperties.GLOBAL);
                }
                catch (Exception e){
                    throw new Exception("var declaration error: " + e.getMessage());
                }
            }
            else if (line.startsWith("return")) {
                // handle return call inside the if/while
                Method.handleReturn(line);
            }
            // if its none of the above it might be a call to a method: it might throw an error
            else{
                // handle method call
                method.handleMethodCall(line);
            }
        }
    }



    private String extractCondition(String line) throws Exception {
        // Regex to match and extract the condition
        // Matches "if(condition) {"
        Pattern pattern = Pattern.compile(conditionRegex);
        Matcher matcher = pattern.matcher(line);
        // Check if the line matches the pattern
        if (matcher.matches()) {
            // Extract the condition inside the parentheses
            String condition = matcher.group(1).trim();
            boolean parserCond = parserCondition(condition);
            if (!parserCond) {
                throw new Exception("Invalid condition: " + condition);
            }
            return condition;
        } else {
            throw new Exception("Invalid if/while statement: Doesnt match the if/while line " + line);
        }
    }

    private void parserConditionLine() throws Exception {
        this.condition = extractCondition(body.get(0));
    }

    private boolean parserCondition(String condition){
        // Trim whitespace around the condition
        condition = condition.trim();
        if (condition == null || condition.isEmpty()) {
            return false;
        }
        // parser boolean condition: Regex for a valid variable or constant (int/double or boolean)
        // Check for single condition
        if (!condition.matches(validValueRegex)) {
            return false;
        }

        Pattern pattern = Pattern.compile(validValueRegex);
        Matcher matcher = pattern.matcher(condition);

        while (matcher.find()) {
            String value = matcher.group(); // Extract the matched value (variable, constant, or boolean)
            // Validate the value (example: check if variables are declared/initialized)
            if (!isValidValue(value)) {
                return false; // If any value is invalid, return false
            }
        }
        // Regex for multiple conditions (no nested parentheses allowed)
        if (!condition.matches(multipleConditionRegex)) {
            return false;
        }
        return true;
    }

    // check if the variable inside the condition is valid: it might be one of the 3:
    // 1. a variable declared in the method
    // 2. a variable declared in the global scope
    // 3. a constant
    private boolean isValidValue(String value) {
        // CHECK IF THERE IS A VARIABLE IN THE LOCAL OR GLOBAL LIST
        if (Method.isVarInLocalAndAssigned(value)){
            return true;
        }
        /// / TODO////
        else if (variable.isVarInGlobal(value)){
            if (variable.isAssigned(value)){
            return true;
            }
        }

        else if (isConstant(value)){
            return true;
        }

        return false;
    }

    private boolean isConstant(String value) {
        // Add logic to check if the value is a constant
        // For example, check if the value is an integer, double, or boolean
        if (value.matches(FINALVARAIBLE.intNumRegex) || value.matches(FINALVARAIBLE.doubleNumRegex) || value.matches(FINALVARAIBLE.booleanRegex)){
            return true;
        }
        return false;
    }


}
