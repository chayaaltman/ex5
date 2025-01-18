package main;

import parserFile.Parser;

import java.io.*;

public class Sjavac {
    private final  int LEGAL =0;
    private final int ILLEGAL=1;
    private static final int ERROR=2;
    private static final String FILE_NOT_FOUND_ERROR_MSG="Invalid file name";

    private static void openFile(String filename) throws IOException, FileNotFoundException {
            File file = new File(filename);
            Parser parser = new Parser();
            if (file.exists() && file.isFile()) {
                // File exists and is a regular file
                parser.readFile(filename);
            }
            else {
                // File does not exist or is not a regular file
                throw new FileNotFoundException(FILE_NOT_FOUND_ERROR_MSG);
            }
    }

//    private static void readFile(String filename) {
//        FileReader fileReader = null;
//        BufferedReader br = null;
//        try {
//            fileReader = new FileReader(filename); // Open the file
//            br = new BufferedReader(fileReader);
//
//            String line;
//            while ((line = br.readLine()) != null) {
//                // Process each line (print it in this example)
//                //check file
//                System.out.println(line);
//            }
//        } catch (IOException e) {
//            // Handle file-related errors
//            System.out.println("An error occurred while reading the file: " + e.getMessage());
//        } finally {
//            // Ensure resources are closed
//            try {
//                if (br != null) {
//                    br.close();
//                }
//                if (fileReader != null) {
//                    fileReader.close();
//                }
//            } catch (IOException e) {
//                System.out.println("An error occurred while closing the file: " + e.getMessage());
//            }
//        }
//    }

    private static int run (String fileName){
        try {
            openFile(fileName);
            return 0;
        }
        catch (FileNotFoundException e) {
            // Handle any unexpected errors
            System.out.println(ERROR+" "+ e.getMessage());
            return ERROR;
        }
        catch (IOException e) {
            System.out.println(ERROR+" "+ e.getMessage());
            return ERROR;
        }

    }

    public static void main(String[] args) throws ArgumentAmountException {
        try{
            if (args.length == 1) {
                int result = run(args[0]);
                System.exit(result);
            }
            else {
                throw new ArgumentAmountException();
            }
        }
        catch (ArgumentAmountException e){
            System.out.println(ERROR+":" +e.getMessage());
            System.exit(ERROR);
        }
    }
}
