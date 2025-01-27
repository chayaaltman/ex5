package parserFile.methodPackage;

import parserFile.Parser;
import parserFile.VarProperties;
import parserFile.varaibalePackage.Variable;
import parserFile.varaibalePackage.VariableException;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;

/**
 * This class is responsible for parsing if/while statements
 * It checks the condition and the body of the if/while statement
 * It also checks the scope of the variables inside the if/while statement
 * It throws an exception if the if/while statement is invalid
 *
 */
public class IfWhile {

    private static final String BOOL_REGEX = "(true|false)";
    /**
     * Regex to match a valid variable or constant (int/double or boolean) inside the condition
     */

    private static final String SIMPLE_COND_REGEX = "true|false|\\d+(\\.\\d+)?|[a-zA-Z_][a-zA-Z_0-9]*";
    /**
     * Regex to match multiple conditions separated by || or &&
     */
    private static final String SEPARATE_REGEX="\\s*\\|\\||&&\\s*";
    private static final String MULTIPLE_CONDITION_REGEX = "^ *(true|false|\\d+(\\.\\d+)?|[a-zA-Z_][a-zA-Z_0" +
            "-9]*)(\\s*(\\|\\||&&)\\s*(true|false|\\d+(\\.\\d+)?|[a-zA-Z_][a-zA-Z_0-9]*))*\\s*$";
    /**
     * Group index for the condition inside the parentheses
     */
    private static final int CONDITION_GROUP = 2;
    /**
     * Line index for the condition inside the if/while statement
     */
    private static final int CONDITION_LINE = 0;

    private final List<String> body; // The body of the if/while statement
    private final Variable variable;
    private final List<Variable> variableScopeList = new ArrayList<>(); // List of variables in the scope
    private final Method methodFunc = new Method(null); // Method object to handle method calls

    /**
     * Constructor for the IfWhile class
     * @param body
     * @param variable
     * @param variableScopeList
     */
    public IfWhile(List<String> body, Variable variable, List<Variable> variableScopeList) {
        this.body = body;
        this.variable = variable;
        // Add the variables from the outer scope to the inner scope
        if (variableScopeList != null) {
            for (Variable var : variableScopeList) {
                if (var != null) {
                    this.variableScopeList.add(var);
                }
            }
        }
        this.variableScopeList.add(variable); // Add the current variable to the scope
    }

    /**
     * Parses the if/while statement
     * @throws Exception if the if/while statement is invalid
     */
    public void parserIfWhileScope() throws Exception {
        try {
            // Extract the condition from the if/while statement
            extractCondition(body.get(CONDITION_LINE));
            // parser the body of the if/while
            handleBody();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Extracts the condition from the if/while statement
     * @param line the if/while statement
     * @throws Exception if the condition is invalid
     */
    private void extractCondition(String line) throws Exception {
        // Regex to match and extract the condition
        // Matches "if(condition) {"
        Pattern pattern = Pattern.compile(Parser.IF_WHILE_REGEX);
        Matcher matcher = pattern.matcher(line);
        // Check if the line matches the pattern
        if (matcher.matches()) {
            // Extract the condition inside the parentheses
            String condition = matcher.group(CONDITION_GROUP);
            try {
                 parserCondition(condition);
            }
            catch (Exception e) {
                throw new Exception(e.getMessage());
            }
        }
        else {
            throw new ifWhileException(ifWhileException.ErrorType.INVALID_CONDITION, line);
        }
    }

    /**
     * Handles the body of the if/while statement
     * @throws Exception if the body is invalid
     */
    private void handleBody() throws Exception {
        for (int i = 1; i < body.size(); i++) {
            String line = body.get(i);
            handleLine(line, i);
        }
    }

    /**
     * Handles each line in the body of the if/while statement
     * @param line
     * @param index
     * @throws Exception
     */
    private void handleLine(String line, int index) throws Exception {
        // Check if the line is a variable declaration
        if (methodFunc.isVariableDeclaration(line)) {
            handleVariableDeclaration(line);
        // if the line does not end with ; or { or }, throw an exception
        } else if (!line.endsWith(";")&&!line.endsWith("}")&&!line.endsWith("{")) {
            throw new MethodException(MethodException.ErrorType.SYNTAX, line);
        // Check if the line is an if/while statement
        } else if (line.matches(Parser.IF_WHILE_REGEX)) {
            handleIfWhileStatement(line, index);
        // Check if the line is a method declaration
        } else if (line.matches(Parser.METHOD_REGEX)) {
            throw new MethodException(MethodException.ErrorType.NEW_METHOD);
        // Check if the line is a method call
        } else if (line.matches(Method.METHOD_CALL_REGEX)) {
            methodFunc.throwFromMethodCall(line);
        } else if (!line.matches(Method.CLOSE_LINE_REGEX) && !line.matches(Method.EMPTY_LINE_REGEX)) {
            throw new ifWhileException(ifWhileException.ErrorType.SYNTAX, line);
        }
    }

    /**
     * Handles the variable declaration in the if/while statement
     * @param line
     * @throws Exception
     */
    private void handleVariableDeclaration(String line) throws  VariableException {
        try {
            // Check if the variable is declared in the current scope
            isVariableDefinedInAnyScope(line);
        } catch (Exception e) {
            // If the variable is not declared in any scope, throw an exception
            throw new VariableException(VariableException.ErrorType.VAR_NOT_DECLARED, line);
        }
    }

    private void handleIfWhileStatement(String line, int index) throws Exception {
        List<String> body = Parser.getIfWhileScope(index, this.body);
        IfWhile ifWhile = new IfWhile(body, new Variable(), variableScopeList);
        try {
            ifWhile.parserIfWhileScope();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    private void isVariableDefinedInAnyScope (String line) throws Exception {
            boolean foundInScope = false;
            // Check the local scopes from innermost to outermost
            for (int i = variableScopeList.size() - 1; i >= 0; i--) {
                try {
                    variableScopeList.get(i).checkLine(line, VarProperties.LOCAL,null);
                    foundInScope = true; // Found in a local scope
                    break; // Exit the loop as soon as it is found
                } catch (Exception ignored) {
                    // Continue checking other scopes if this one fails
                }
            }
            if (!foundInScope) {
                try {
                    // Check the global scope if not found in local scopes
                    variable.checkLine(line, VarProperties.GLOBAL,null);
                    foundInScope = true; // Found in global scope
                } catch (Exception ignored) {
                    // If global scope also fails, do nothing here (handled below)
                }
            }
            // If not found in any scope, throw an exception
            if (!foundInScope) {
                throw new VariableException(VariableException.ErrorType.VAR_NOT_DECLARED, line);
            }
        }

    private void parserCondition(String condition) throws ifWhileException {
        // Trim whitespace around the condition
        if (condition.isEmpty()) {
            throw new ifWhileException(ifWhileException.ErrorType.INVALID_CONDITION);
        }
        // Regex for multiple conditions (no nested parentheses allowed)
        if (!condition.matches(MULTIPLE_CONDITION_REGEX)&& !condition.matches(SIMPLE_COND_REGEX)) {
            throw new ifWhileException(ifWhileException.ErrorType.INVALID_CONDITION, condition);
        }
        try {
            parserSimpleCondition(condition);
        }
        catch(ifWhileException e){
            throw new ifWhileException(ifWhileException.ErrorType.INVALID_CONDITION, condition);
            }
        try {
            parserComplexCondition(condition);
        }
        catch(ifWhileException e){
            throw new ifWhileException(ifWhileException.ErrorType.INVALID_CONDITION, condition);
        }
    }

    private void parserSimpleCondition(String condition) throws ifWhileException {
        Pattern pattern1 = Pattern.compile(SIMPLE_COND_REGEX);
        Matcher matcher1 = pattern1.matcher(condition);
        if (matcher1.matches()) {
            boolean isValid = condition.matches(BOOL_REGEX);
            if (!isValidValue(condition)) {
                isValid = true;
            }
            if(!isValid) {
                throw new ifWhileException(ifWhileException.ErrorType.INVALID_CONDITION, condition);
            }
        }
    }

    private void parserComplexCondition(String condition) throws ifWhileException {
        Pattern pattern2 = Pattern.compile(MULTIPLE_CONDITION_REGEX);
        Matcher matcher2 = pattern2.matcher(condition);

        if (matcher2.matches()) {
            // Extract the matched value (variable, constant, or boolean)
            String[] values = condition.split(SEPARATE_REGEX);
            // Validate the value (example: check if variables are declared/initialized)
            for (String val :values) {
                if (!isValidValue(val)) {
                    throw new ifWhileException(ifWhileException.ErrorType.INVALID_CONDITION, condition);
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
            if (variableScopeList.get(i).isValidVariableInCondition(value, VarProperties.LOCAL)){
                return true;
            }
        }
        if(variable.isValidVariableInCondition(value, VarProperties.GLOBAL)){
            return true;
        }
        else return isConstant(value);
    }

    private boolean isConstant(String value) {
        // Add logic to check if the value is a constant
        // For example, check if the value is an integer, double, or boolean
        return value.matches(Variable.intNumRegex) || value.matches(Variable.doubleNumRegex) ||
                value.matches(Variable.booleanRegex);
    }

}
