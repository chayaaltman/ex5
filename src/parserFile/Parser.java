package parserFile;
import parserFile.methodPackage.Method;
import parserFile.varaibalePackage.Variable;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static parserFile.varaibalePackage.Variable.VARIABLE_BODY_REGEX;

public class Parser {
    public static final String COMMENT_REGEX = "^//.*$";
    public static final String ILLEGAL_COMMENT_REGEX =  "^(/\\*.*)|(.*\\*/)$";
    public static final String LEGAL_END_LINE_REGEX = "^(.*[;{]|})$";
    public static final String VAR_DEC_REGEX ="^(final +)?(int|String|double|char|boolean)" ;
    public static final String IF_WHILE_REGEX = "^\\s*(if|while) *\\(( *.*)\\) *\\{ *$";
    public static final String METHOD_REGEX = "^\\s*void";
    public static final String RETURN_REGEX = "^return";

    private static List<String> lines = new ArrayList<>(); // Stores lines read from the file
    private Map<String, List<String>> methodScopes = new HashMap();
    public Parser() {
    }

    public void readFile(String filename) throws IOException {
        try (FileReader fileReader = new FileReader(filename); // Open the file
             BufferedReader br = new BufferedReader(fileReader)) {
            String line;
            while ((line = br.readLine()) != null) {
                // Process each line (print it in this example)
                lines.add(line);
                System.out.println(line);
            }
        } catch (IOException e) {
            // Handle file-related errors
            throw new IOException("An error occurred while reading the file: " + e.getMessage());
        }
    }

    public void parseFile() throws Exception {
        boolean isBlockComment = false;
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            ///  handle comments
            System.out.println("Parsing line: " + line);
            Pattern pattern = Pattern.compile(VAR_DEC_REGEX);
            Matcher matcher = pattern.matcher(line);
            Pattern pattern2 = Pattern.compile(VARIABLE_BODY_REGEX);
            Matcher matcher2 = pattern2.matcher(line);
            if (line.isEmpty()) {
                continue;
            } else if (line.matches(COMMENT_REGEX)) {
                continue;
            } else if (line.matches(ILLEGAL_COMMENT_REGEX)) {
                throw new Exception("Wrong comment Type");
            }
            // Example: Check if the line ends with ';', '{', or '}'
            else if(!line.endsWith(";") && !line.endsWith("{") && !line.endsWith("}")) {
                throw new Exception("invalid end of line");
            }
//            else if (!line.matches(LEGAL_END_LINE_REGEX)) {
//                throw new Exception("invalid end of line");
//            }
            // Check if the line is a variable declaration
            else if (line.startsWith("final") || line.startsWith("int") || line.startsWith("String") ||
                    line.startsWith("double") || line.startsWith("char") || line.startsWith("boolean")) {
                Variable variable = new Variable();
                try {
                    variable.checkLine(line, VarProperties.GLOBAL, null);
                } catch (Exception e) {
                    throw new Exception(e.getMessage());
                }
            }

            // starts with if or while
            else if (line.matches(IF_WHILE_REGEX)) {
                throw new Exception("If/While statements are not allowed in the global scope");
            }
            // a method call
            else if (line.startsWith("void")) {
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
            // for example: calling in the global scope for a method is illegal! throw an error
            else if (line.matches(RETURN_REGEX)) {
                throw new Exception("Cant return in the global scope");
            }
            else {
                // if there is a method call, it should be handled in the method class, so throw exception
                throw new Exception("Invalid line syntax: " + line);
            }
        }
        for (String methodName : methodScopes.keySet()){
            System.out.println("method "+methodName);
            Method method = new Method(methodScopes.get(methodName));
            try {
                method.handleMethod(methodName);
            }
            catch (Exception e){
                throw new Exception(e.getMessage());
            }
        }
    }


    public List<String> getMethodScope(int index) {
        List<String> methodScope = new ArrayList<>();
        int braceCount = 0;
        boolean insideMethod = false;
        for (int i=index; i<lines.size(); i++) {
            if (lines.get(i).contains("{")) {
                braceCount++;
                insideMethod = true;
            }
            if (insideMethod) {
                methodScope.add(lines.get(i));
            }
            if (lines.get(i).contains("}")) {
                braceCount--;
                if (braceCount == 0) {
                    insideMethod = false;
                    break;
                }
            }
        }
        return methodScope;
    }

    public static List<String> getIfWhileScope(int index , List<String> body) {
        List<String> ifWhileScope = new ArrayList<>();
        int braceCount = 0;
        boolean insideIfWhile = false;
        for (int i=index; i<body.size(); i++) {
            if (body.get(i).contains("{")) {
                braceCount++;
                insideIfWhile = true;
            }
            if (insideIfWhile) {
                ifWhileScope.add(body.get(i));
            }
            if (body.get(i).contains("}")) {
                braceCount--;
                if (braceCount == 0) {
                    insideIfWhile = false;
                    break;
                }
            }
        }
        return ifWhileScope;
    }


}
