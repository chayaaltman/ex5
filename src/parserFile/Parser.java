package parserFile;
import parserFile.methodPackage.Method;
import parserFile.varaibalePackage.Variable;

import java.io.*;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static parserFile.varaibalePackage.Variable.VARIABLE_BODY_REGEX;

/**
 * Parser class that reads a file and parses it according to the Sjava language rules.
 */
public class Parser {
    /**
     * Regular expressions for comments and illegal comments.
     */
    private static final String COMMENT_REGEX = "^//.*$";
    /**
     * Regular expression for illegal comments.
     */
    private static final String ILLEGAL_COMMENT_REGEX =  "^(/\\*.*)|(.*\\*/)$";
    /**
     * Regular expressions for variable declarations, if/while statements, and method declarations.
     */
    public static final String VAR_DEC_REGEX ="^\\s*(final +)?(int|String|double|char|boolean)\\b" ;

    /**
     * Regular expression for if/while statements.
     */
    public static final String IF_WHILE_REGEX = "^\\s*(if|while) *\\(( *.*)\\) *\\{ *$";
    /**
     * Regular expression for method declarations.
     */
    public static final String METHOD_REGEX = "^\\s*void";
    /**
     * Regular expression for return statements.
     */
    private static final String RETURN_REGEX = "^return";
    /**
     * Error message for reading a file.
     */
    private static final String READ_FILE_ERR_MSG="An error occurred while reading the file: ";
    /**
     * Constants for brackets.
     */
    public static final String OPEN_BRACKET="{";
    public static final String CLOSE_BRACKET="}"; // Regular expression for the end of a scope
    private static final List<String> lines = new ArrayList<>(); // Stores lines read from the file
    private final Map<String, List<String>> methodScopes = new HashMap<>();
    /**
     * Constructor for the Parser class.
     */
    public Parser() {
    }

    /**
     * Reads a file line by line and prints each line.
     * @param filename
     * @throws IOException
     */
    public void readFile(String filename) throws IOException {
        try (FileReader fileReader = new FileReader(filename); // Open the file
             BufferedReader br = new BufferedReader(fileReader)) {
            String line;
            while ((line = br.readLine()) != null) {
                // Process each line (print it in this example)
                lines.add(line);
            }
        } catch (IOException e) {
            // Handle file-related errors
            throw new IOException( READ_FILE_ERR_MSG+ e.getMessage());
        }
    }

    /**
     * Parses the file line by line and checks for syntax errors.
     * @throws Exception
     */
    public void parseFile() throws Exception {
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isEmpty()) {
                continue;
            }
            //  handle comments
            else if (line.matches(COMMENT_REGEX)) {
                continue;
            }
            // a method call
            Pattern pattern = Pattern.compile(METHOD_REGEX);
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                List<String> methodScope = getMethodScope(i);
                Method method = new Method(methodScope);
                try {
                    method.methodDeclaration(line);
                } catch (Exception e) {
                    throw new Exception(e.getMessage());
                }
                methodScopes.put(method.getMethodName(), methodScope);
                i = methodScope.size()+i-1;
            }
            else {
                handleLine(line);
            }
        }
        handleMethod();
    }

    /**
     * Handles a line of code and checks for syntax errors.
     * @param line
     * @throws Exception
     */
    private void handleLine(String line) throws Exception {
        if (line.matches(ILLEGAL_COMMENT_REGEX)) { // Check if the comment is illegal
            throw new parseException(parseException.ErrorType.COMMENT_TYPE);
        }
        // check if the line is a variable declaration
        Pattern pattern = Pattern.compile(VAR_DEC_REGEX);
        Pattern assign_pat = Pattern.compile(VARIABLE_BODY_REGEX);
        Matcher matcher = pattern.matcher(line);
        Matcher assign_matcher = assign_pat.matcher(line);
        if (matcher.find()||assign_matcher.find() ) {
            Variable variable = new Variable();
            try {
                variable.checkLine(line, VarProperties.GLOBAL, null);
            } catch (Exception e) {
                throw new Exception(e.getMessage());
            }}
        // Example: Check if the line ends with ';', '{', or '}'
        else if(!line.endsWith(";")&&!line.endsWith(CLOSE_BRACKET)&&!line.endsWith(OPEN_BRACKET)) {
            throw new parseException(parseException.ErrorType.END_OF_LINE);
        }
        // starts with if or while
        else if (line.matches(IF_WHILE_REGEX)) {
            throw new parseException(parseException.ErrorType.IF_WHILE);
        }
        // for example: calling in the global scope for a method is illegal! throw an error
        else if (line.matches(RETURN_REGEX)) {
            throw new parseException(parseException.ErrorType.RETURN);
        }
        else {
            // if there is a method call, it should be handled in the method class, so throw exception
            throw new parseException(parseException.ErrorType.SYNTAX ,line);
        }
    }

    /**
     * Handles the methods in the file.
     * @throws Exception
     */
    private void handleMethod() throws Exception {
        for (String methodName : methodScopes.keySet()){
            // handle the method
            Method method = new Method(methodScopes.get(methodName));
            try {
                method.handleMethod(methodName);
            }
            catch (Exception e){
                throw new Exception(e.getMessage());
            }
        }
    }

    /**
     * Returns the scope of the if/while statement. the logic is to count the number of brackets,
     * and when the count is 0, the scope ends.
     * @param index
     * @param body
     * @return
     */
    private static List<String> getScope(int index, List<String> body) {
        List<String> scope = new ArrayList<>();
        int braceCount = 0; // count the number of brackets
        boolean insideIfWhile = false;
        for (int i=index; i<body.size(); i++) { // iterate over the body
            if (body.get(i).contains(OPEN_BRACKET)) { // if there is an open bracket, increase the count
                braceCount++;
                insideIfWhile = true; // we are inside the if/while scope
            }
            if (insideIfWhile) {
                scope.add(body.get(i));
            }
            if (body.get(i).contains(CLOSE_BRACKET)) { // if there is a close bracket, decrease the count
                braceCount--;
                if (braceCount == 0) { // if the count is 0, the scope ends
                    insideIfWhile = false;
                    break;
                }
            }
        }
        return scope;
    }


    /**
     * Returns the method scope from index to the end of the method.
     * @param index
     * @return
     */
    private List<String> getMethodScope(int index) {
        return getScope(index, lines);
    }

    /**
     * Returns the scope of the if/while statement. from index to the end of the scope.
     * @param index
     * @param body
     * @return
     */
    public static List<String> getIfWhileScope(int index , List<String> body) {
        return getScope(index,body);
    }
}
