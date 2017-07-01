package me.bramhaag.bcf.resolver;

import lombok.Data;
import lombok.NonNull;
import me.bramhaag.bcf.CommandData;

import java.lang.reflect.Parameter;
import java.util.List;

@Data
public class ArgumentData {

    private final int index;
    @NonNull
    private final CommandData command;
    @NonNull
    private final List<String> args;

    public String pop() {
        return !args.isEmpty() ? args.remove(0) : null;
    }

    public boolean isLast() {
        return command.getMethod().getParameters().length - 1 == index;
    }

    public Parameter getParameter() {
        return command.getMethod().getParameters()[index];
    }
}
