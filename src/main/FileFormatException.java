package main;

import java.io.IOException;

public class FileFormatException extends IOException {
    public FileFormatException() {
        super("wrong file format (not sjava)");

    }
}
