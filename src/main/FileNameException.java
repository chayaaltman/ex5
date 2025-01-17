package main;

import java.io.IOException;

public class FileNameException extends IOException {
    public FileNameException() {
        super("Invalid file name");
    }
}
