package main;

import java.io.IOException;
/**
 * Custom exception class that extends {@link IOException}.
 * This exception is thrown when a file format is incorrect, specifically when
 * the file is not in the expected "sjava" format.
 */
public class FileFormatException extends IOException {
    /**
     * Default constructor for the FileFormatException.
     * Initializes the exception with a predefined error message: "wrong file format (not sjava)".
     */
    private static final String MESSAGE_EXCEPTION = "wrong file format (not sjava)";
    public FileFormatException() {
        super(MESSAGE_EXCEPTION);

    }
}
