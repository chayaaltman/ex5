package main;

import java.io.IOException;
/**
 * Custom exception class that extends {@link IOException}.
 * This exception is thrown when the number of arguments provided is invalid.
 * Specifically, it indicates that only one argument is expected.
 */
public class ArgumentAmountException extends IOException {
   /**
     * Message for the exception.
     */
    private static final String MESSAGE_EXCEPTION = "Invalid amount of arguments. should only have one.";
    /**
     * Default constructor for the ArgumentAmountException.
     * Initializes the exception with a predefined error message:
     * "Invalid amount of arguments should only have one."
     */
    public ArgumentAmountException() {
        super(MESSAGE_EXCEPTION); // call the super constructor
    }
}
