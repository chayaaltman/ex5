package main;

import parserFile.Parser;

import java.io.*;

/**
 * The `Sjavac` class is a simple static Java program for validating and parsing `.sjava` files.
 * It verifies the file format, checks for its existence, and processes it using a `Parser` class.
 */
public class Sjavac {
    /**
        Legal represents a legal file
     */
    private static final  int LEGAL =0;
    /**
     * Illegal represents an illegal file
     */
    private static final int ILLEGAL=1;
    /**
     * IO_ERROR represents an error in the file
     */
    private static final int IO_ERROR =2;
    /**
     * END_OF_FILE_REGEX represents the end of the file
     */
    private static final String END_OF_FILE_REGEX=".sjava";
    /**
     * FILE_NOT_FOUND_ERROR_MSG represents an error message for a file not found
     */
    private static final String FILE_NOT_FOUND_ERROR_MSG="Invalid file name";

    /**
     * Opens and validates a file. Checks if the file exists, is in the correct format, and parses it.
     *
     * @param filename The name of the file to open.
     * @throws FileFormatException If the file does not have the correct `.sjava` extension.
     * @throws FileNotFoundException If the file does not exist or is not a regular file.
     * @throws Exception If any error occurs during file parsing.
     */
    private static void openFile(String filename) throws Exception{
            File file = new File(filename);
            Parser parser = new Parser();
            // check if the file is a .sjava file
            if (!filename.endsWith(END_OF_FILE_REGEX)) {
                throw new FileFormatException();
            }
            // check if the file exists
            if (file.exists()) {
                try{
                    parser.readFile(filename);
                    parser.parseFile();
                }
                catch (Exception e){
                    throw new Exception(e.getMessage());
                }
            }// if the file does not exist
            else {
                throw new FileNotFoundException(FILE_NOT_FOUND_ERROR_MSG);
            }
    }

    /**
     * Runs the main logic for processing a file.
     *
     * @param fileName The name of the file to process.
     * @throws Exception If any error occurs during file validation or parsing.
     */
    private static void run (String fileName) throws Exception {
        try {
            openFile(fileName);
        }
        catch (IOException e) {
            throw new IOException(e.getMessage());
        }
        catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

    /**
     * The program's entry point. Validates the number of arguments and processes the provided file.
     *
     * @param args Command-line arguments. Expects exactly one argument (the file name).
     * @throws ArgumentAmountException If the number of arguments is not exactly one.
     */
    public static void main(String[] args) throws ArgumentAmountException {
        try{
            // if there is one argument, return the result of the run method
            if (args.length == 1) {
                try {
                    run(args[0]);
                    System.out.println(LEGAL); // if the file is legal
                }
                catch (IOException e){
                    // if there is an IO error, print the error message
                    System.out.println(IO_ERROR+": "+ e.getMessage());
                }
                catch (Exception e) {
                    // if there is an illegal file, print the error message
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
