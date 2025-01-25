package parserFile;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;

public class IfWhile {
    /**
     * Regex to match and extract the condition from an if/while statement
     */
    private static final String conditionRegex = "^\\s*if\\s*\\(([^)]*)\\)\\s*\\{$\n";
    /**
     * Regex to match a valid variable or constant (int/double or boolean) inside the condition
     */

    private static final String validCondSimpl = "true|false|\\d+(\\.\\d+)?|[a-zA-Z_][a-zA-Z_0-9]*";
    /**
     * Regex to match multiple conditions separated by || or &&
     */
    private static final String multipleConditionRegex = validCondSimpl + "^ *(\\s*(\\|\\||&&)\\s*" +
            validCondSimpl+")*?$";

    private int scope;
    private final List<String> body;
    private final Variable variable;
    private final List<Variable> variableScopeList = new ArrayList<>();
    private final Method methodFuncs= new Method(null);

    public IfWhile(List<String> body, Variable variable, List<Variable> variableScopeList) {
        this.body = body;
        this.variable = variable;
        if (variableScopeList != null) {
            for (Variable v : variableScopeList) {
                if (v != null) {
                    this.variableScopeList.add(v);
                }
            }
        }
        this.variableScopeList.add(variable);
    }


    public void parserIfWhileScope() throws Exception {
        try {
            extractCondition(body.get(0));
            handleBody(); // parser the body of the if/while
        } catch (Exception e) {
            throw new Exception(e.getMessage());

        }
    }

    private void extractCondition(String line) throws Exception {
        // Regex to match and extract the condition
        // Matches "if(condition) {"

        Pattern pattern = Pattern.compile(Parser.IF_WHILE_REGEX);
        Matcher matcher = pattern.matcher(line);
        // Check if the line matches the pattern
        if (matcher.matches()) {

            // Extract the condition inside the parentheses
            String condition = matcher.group(2);
            boolean parserCond;
            try {
                 parserCondition(condition);

            }
            catch (Exception e) {
                throw new Exception(e.getMessage());
            }
        }
        else {
            throw new Exception("Invalid if/while statement: Doesnt match the if/while line " + line);
        }
    }



    private void handleBody () throws Exception {
            for (int i = 1; i < body.size(); i++) {
                String line = body.get(i);
                // Check if the line is a valid statement
                if (methodFuncs.isVariableDeclaration(line)) {
                    try {
                        // check if the var is in the local or global list:
                        isVariableDefinedInAnyScope(line);
                    } catch (Exception e) {
                        throw new Exception("var declaration error: " + e.getMessage());
                    }
                }
                else if(!line.endsWith(";") && !line.endsWith("{") && !line.endsWith("}")) {
                    throw new Exception("invalid end of line");
                }

                // check if its a if/while statement
                else if (line.matches(Parser.IF_WHILE_REGEX)) {
                    List<String> body = Parser.getIfWhileScope(i,this.body);
                    IfWhile ifWhile = new IfWhile(body, new Variable(), variableScopeList);
                    try {
                        ifWhile.parserIfWhileScope();
                    }
                    catch (Exception e) {
                        throw new Exception(e.getMessage());
                    }
                }
                else if (line.matches(Parser.METHOD_REGEX)) {
                    throw new Exception("Method cannot contain another method");
                }
                // check if it is last in the method: if all of the lines after are
                else if (line.matches(Parser.RETURN_REGEX)) {
                    continue;
                }
                else if (line.matches(Method.METHOD_CALL_REGEX)) {
                    methodFuncs.throwFromMethodCall(line);
                }
                else if(!line.matches("^\\s*}\\s*$")&&!line.matches("\\s*")){
                    throw new Exception("Invalid line syntax: " + line);
                }
            }
        }

        private void isVariableDefinedInAnyScope (String line) throws Exception {
            boolean foundInScope = false;

            // Check the local scopes from innermost to outermost
            for (int i = variableScopeList.size() - 1; i >= 0; i--) {
                try {
                    variableScopeList.get(i).checkLine(line, varProperties.LOCAL,null);
                    foundInScope = true; // Found in a local scope
                    break; // Exit the loop as soon as it is found
                } catch (Exception ignored) {
                    // Continue checking other scopes if this one fails
                }
            }
            if (!foundInScope) {
                try {
                    // Check the global scope if not found in local scopes
                    variable.checkLine(line, varProperties.GLOBAL,null);
                    foundInScope = true; // Found in global scope
                } catch (Exception ignored) {
                    // If global scope also fails, do nothing here (handled below)
                }
            }
            // If not found in any scope, throw an exception
            if (!foundInScope) {
                throw new Exception("Variable is not defined in any scope: " + line);
            }
        }

    private void parserCondition(String condition) throws Exception {
        // Trim whitespace around the condition
        if (condition.isEmpty()) {
            throw new Exception("Empty condition");
        }
        // parser boolean condition: Regex for a valid variable or constant (int/double or boolean)
        // Check for single condition
        // Regex for multiple conditions (no nested parentheses allowed)
        if (!condition.matches(multipleConditionRegex)&& !condition.matches(validCondSimpl)) {
            throw new Exception("Invalid condition: " + condition);
        }

        Pattern pattern1 = Pattern.compile(validCondSimpl);
        Matcher matcher1 = pattern1.matcher(condition);

        if (matcher1.matches()) {
            boolean isValid = condition.equals("true") || condition.equals("false");
            if (!isValidValue(condition)) {
                    isValid = true;
                }
        if(!isValid) {
            throw new Exception("Invalid condition: " + condition);
        }

        }
        Pattern pattern2 = Pattern.compile(multipleConditionRegex);
        Matcher matcher2 = pattern2.matcher(condition);

        if (matcher2.matches()) {
            String[] values = condition.split(" *\\|\\|&&"); // Extract the matched value (variable, constant, or boolean)
            // Validate the value (example: check if variables are declared/initialized)
            for (String val :values) {
                if (!isValidValue(val)) {
                    throw new Exception("Invalid condition: " + condition);
                }
            }
        }
    }

    // check if the variable inside the condition is valid: it might be one of the 3:
    // 1. a variable declared in the method
    // 2. a variable declared in the global scope
    // 3. a constant
    private boolean isValidValue(String value) {
        // CHECK IF THERE IS A VARIABLE IN THE LOCAL OR GLOBAL LIST
        for (int i = variableScopeList.size() - 1; i >= 0; i--){
            if (variableScopeList.get(i).isValidVariableinCondition(value, varProperties.LOCAL)){
                return true;
            }
        }
        if(variable.isValidVariableinCondition(value, varProperties.GLOBAL)){
            return true;
        }
        else if (isConstant(value)){
            return true;
        }

        return false;
    }

    private boolean isConstant(String value) {
        // Add logic to check if the value is a constant
        // For example, check if the value is an integer, double, or boolean
        return value.matches(Variable.intNumRegex) || value.matches(Variable.doubleNumRegex) ||
                value.matches(Variable.booleanRegex);
    }

}
