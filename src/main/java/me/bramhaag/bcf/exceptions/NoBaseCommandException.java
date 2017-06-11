package me.bramhaag.bcf.exceptions;

import me.bramhaag.bcf.annotations.CommandBase;

public class NoBaseCommandException extends RuntimeException {

    public NoBaseCommandException(Class<?> clazz) {
        super(String.format("Unable to locate annotation %s.%s for class %s.%s", CommandBase.class.getPackage(), CommandBase.class.getName(), clazz.getPackage(), clazz.getName()));
    }

    public NoBaseCommandException(String message) {
        super(message);
    }

    public NoBaseCommandException(String message, Throwable cause) {
        super(message, cause);
    }
}
