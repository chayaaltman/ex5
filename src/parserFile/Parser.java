package parserFile;

import java.io.*;
import java.util.*;

public class Parser {
    private final List<String> lines; // Stores lines read from the file
    FileReader fileReader = null;
    BufferedReader br = null;

    public Parser() {
        this.lines = new ArrayList<>();
        //allMethods = new ArrayList<>();
//        this.fileReader = new FileReader(filename);
//        this.br = new BufferedReader(fileReader);
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

    public boolean validateFile() {
        for (String line : lines) {
            if (!validateLine(line)) {
                return false;
            }
        }
        return true;
    }

    private boolean validateLine(String line) {
        // Add regex or other validation logic here
        // For now, just ignoring empty/comment lines as an example
        if (line.isEmpty()) {
            return true;
        }
        // handle comments:
        if (line.startsWith("//")){
            return true;

        }

        // Example: Check if the line ends with ';', '{', or '}'
        if (!line.endsWith(";") && !line.endsWith("{") && !line.endsWith("}")) {
            return false;
        }

        if (line.startsWith("if") || line.startsWith("while")) {
            IfWhile ifWhile = new IfWhile(lines);
            ifWhile.parserSubroutine();
        }
        else if (line.startsWith("int") || line.startsWith("char") || line.startsWith("String") ||
                line.startsWith("double") || line.startsWith("boolean")) {
            Variable variable = new Variable(line);
            variable.checkLine(line);
        }
        else if (line.startsWith("void")) {
            List<String> methodLines = new ArrayList<>();
            methodLines.add(line);
            ///  TODO sent to method parser all of the method lines
            for (String methodLine : lines) {
                if (Objects.equals(methodLine, "}")) {
                    methodLines.add(methodLine);
                    break;
                }
                methodLines.add(methodLine);
            }
            Method method = new Method(methodLines);
            method.methodDeclaration(line);
            method.handleBody();
        }
        return true;
    }

    public List<String> getLines() {
        return lines;
    }
}
