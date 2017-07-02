package me.bramhaag.bcf.exceptions;

import org.jetbrains.annotations.NotNull;

public class InvalidCommandException extends RuntimeException {

    public InvalidCommandException(@NotNull String message) {
        super(message);
    }
}
