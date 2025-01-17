package main;

import java.io.IOException;

public class ArgumentAmountException extends IOException {
    public ArgumentAmountException() {
        super("Invalid amount of arguments should only have one.");
    }

}
