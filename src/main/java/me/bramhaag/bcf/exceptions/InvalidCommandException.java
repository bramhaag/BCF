package me.bramhaag.bcf.exceptions;

public class InvalidCommandException extends RuntimeException {

    public InvalidCommandException(String message) {
        super(message);
    }
}
