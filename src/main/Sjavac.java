package main;

import parserFile.Parser;

import java.io.*;

public class Sjavac {
    private static final  int LEGAL =0;
    private static final int ILLEGAL=1;
    private static final int IO_ERROR =2;
    private static final String FILE_NOT_FOUND_ERROR_MSG="Invalid file name";

    private static void openFile(String filename) throws Exception{
            File file = new File(filename);
            Parser parser = new Parser();
            ///  check if the file is a .sjava file
            if (!filename.endsWith(".sjava")) {
                throw new FileFormatException();
            }
            /// check if the file exists
            if (file.exists()) {
                // File exists and is a regular file
                try{
                    parser.readFile(filename);
                    parser.parseFile();
                }
                catch (Exception e){
                    throw new Exception(e.getMessage());
                }
            }
            /// if the file does not exist
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

    private static void run (String fileName) throws Exception {
        try {
            openFile(fileName);
        }
        catch (FileNotFoundException e) {
            // Handle any unexpected errors
            System.out.println(IO_ERROR +" "+ e.getMessage());
        }
        catch (IOException e) {
            System.out.println(IO_ERROR +" "+ e.getMessage());
        }
        catch (Exception e){
            throw new Exception(e.getMessage());
        }

    }

    public static void main(String[] args) throws ArgumentAmountException {
        try{
            // if there is one argument, return the result of the run method
            if (args.length == 1) {
                try {
                    run(args[0]);
                    System.out.println(LEGAL);
                }
                catch (Exception e) {
                    System.out.println(ILLEGAL +  ": " +e.getMessage());
                }

            }
            // otherwise throw an exception
            else {
                throw new ArgumentAmountException();
            }
        }
        catch (ArgumentAmountException e){
            System.out.println(IO_ERROR +": " +e.getMessage());
        }
    }
}
