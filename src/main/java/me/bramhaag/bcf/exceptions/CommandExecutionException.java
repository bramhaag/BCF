package me.bramhaag.bcf.exceptions;

public class CommandExecutionException extends RuntimeException {

    public CommandExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
