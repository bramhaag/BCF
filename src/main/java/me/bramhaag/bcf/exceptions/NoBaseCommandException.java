package me.bramhaag.bcf.exceptions;

import me.bramhaag.bcf.annotations.CommandBase;
import org.jetbrains.annotations.NotNull;

public class NoBaseCommandException extends RuntimeException {

    public NoBaseCommandException(@NotNull Class<?> clazz) {
        super(String.format("Unable to locate annotation %s.%s for class %s.%s", CommandBase.class.getPackage(), CommandBase.class.getName(), clazz.getPackage(), clazz.getName()));
    }
}
