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
     * Regular expressions for the different types of lines
     */
    private static final String COMMENT_REGEX = "^//.*$";
    private static final String ILLEGAL_COMMENT_REGEX =  "^(/\\*.*)|(.*\\*/)$";
    public static final String VAR_DEC_REGEX ="^\\s*(final +)?(int|String|double|char|boolean)\\b" ;
    public static final String IF_WHILE_REGEX = "^\\s*(if|while) *\\(( *.*)\\) *\\{ *$";
    public static final String METHOD_REGEX = "^\\s*void";
    private static final String RETURN_REGEX = "^return";
    private static final String READ_FILE_ERR_MSG="An error occurred while reading the file: ";
    public static final String OPEN_BRACKET="{";
    public static final String CLOSE_BRACKET="}";

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
            ///  handle comments
            if (line.isEmpty()) {
                continue;
            } else if (line.matches(COMMENT_REGEX)) {
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

    private void handleLine(String line) throws Exception {
        if (line.matches(ILLEGAL_COMMENT_REGEX)) {
            throw new parseException(parseException.ErrorType.COMMENT_TYPE);
        }
        Pattern pattern = Pattern.compile(VAR_DEC_REGEX);
        Matcher matcher = pattern.matcher(line);
        if (matcher.find() ) {
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

    private void handleMethod() throws Exception {
        for (String methodName : methodScopes.keySet()){
            Method method = new Method(methodScopes.get(methodName));
            try {
                method.handleMethod(methodName);
            }
            catch (Exception e){
                throw new Exception(e.getMessage());
            }
        }
    }

    private static List<String> getScope(int index, List<String> body) {
        List<String> scope = new ArrayList<>();
        int braceCount = 0;
        boolean insideIfWhile = false;
        for (int i=index; i<body.size(); i++) {
            if (body.get(i).contains(OPEN_BRACKET)) {
                braceCount++;
                insideIfWhile = true;
            }
            if (insideIfWhile) {
                scope.add(body.get(i));
            }
            if (body.get(i).contains(CLOSE_BRACKET)) {
                braceCount--;
                if (braceCount == 0) {
                    insideIfWhile = false;
                    break;
                }
            }
        }
        return scope;
    }


    private List<String> getMethodScope(int index) {
        return getScope(index, lines);
    }

    public static List<String> getIfWhileScope(int index , List<String> body) {
        return getScope(index,body);
    }
}
