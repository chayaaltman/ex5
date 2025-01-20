package parserFile;
import java.io.*;
import java.util.*;

public class Parser {
    private static List<String> lines = new ArrayList<>(); // Stores lines read from the file
    private List<List<String>> ifWhileScopes = new ArrayList<>();
    private List<List<String>> methodScopes = new ArrayList<>();
    public Parser() {
    }

    public void readFile(String filename) throws IOException {
        FileReader fileReader = null;
        BufferedReader br = null;
        try {
            fileReader = new FileReader(filename); // Open the file
            br = new BufferedReader(fileReader);
            String line;
            while ((line = br.readLine()) != null) {
                // Process each line (print it in this example)
                lines.add(line.trim()); // Trim to remove extra spaces
                System.out.println(line);
            }

            // close the file
            fileReader.close();
        } catch (IOException e) {
            // Handle file-related errors
            System.out.println("An error occurred while reading the file: " + e.getMessage());
        } finally {
            // Ensure resources are closed
            try {
                if (br != null) {
                    br.close();
                }
                if (fileReader != null) {
                    fileReader.close();
                }
            } catch (IOException e) {
                System.out.println("An error occurred while closing the file: " + e.getMessage());
            }
        }
    }


    public void parseFile() throws Exception {
        boolean isBlockComment = false;
        for (int i=0; i<lines.size(); i++) {
            String line = lines.get(i);
            ///  handle comments
            System.out.println("Parsing line: " + line);
            if (line.isEmpty()){
                continue;
            }
            if (line.startsWith("//")){
                continue;
            }
            if (line.startsWith("/*")){
                isBlockComment = true;
            }
            while(isBlockComment){
                if (line.endsWith("*/")){
                    isBlockComment = false;
                }
                break;
            }
            if(isBlockComment){
                continue;
            }
            /// // handle comments

            // Example: Check if the line ends with ';', '{', or '}'
            if (!line.endsWith(";") && !line.endsWith("{") && !line.endsWith("}")) {
                throw new Exception("Invalid line syntax: " + line);
            }
            // Check if the line is a variable declaration
            if (type.startsWithType(line) || line.startsWith("final")){
                Variable.checkLine(line, varProperties.GLOBAL);
            }
            // starts with if or while
            else if (line.startsWith("if") || line.startsWith("while")){
                List<String> ifWhileScope = getIfWhileScope(i);
                IfWhile ifWhile = new IfWhile(ifWhileScope);
                ifWhile.parserSubroutine();

                // add scope to the list of scopes
                ifWhileScopes.add(ifWhileScope);
            }
            // a method call
            else if (line.startsWith("void")){
                List<String> methodScope = getMethodScope(i);
                Method method = new Method(methodScope);
                method.handleMethod();

                // add scope to the list of scopes
                methodScopes.add(methodScope);
            }
            else{
                throw new Exception("Invalid line syntax: " + line);
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

    public static List<String> getIfWhileScope(int index) {
        List<String> ifWhileScope = new ArrayList<>();
        int braceCount = 0;
        boolean insideIfWhile = false;
        for (int i=index; i<lines.size(); i++) {
            if (lines.get(i).contains("{")) {
                braceCount++;
                insideIfWhile = true;
            }
            if (insideIfWhile) {
                ifWhileScope.add(lines.get(i));
            }
            if (lines.get(i).contains("}")) {
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
