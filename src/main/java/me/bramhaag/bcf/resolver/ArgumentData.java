package me.bramhaag.bcf.resolver;

import lombok.Data;
import me.bramhaag.bcf.CommandData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Parameter;
import java.util.List;

@Data
public class ArgumentData {

    private final int index;

    @NotNull private final CommandData command;
    @NotNull private final List<String> args;

    @Nullable
    public String pop() {
        return !args.isEmpty() ? args.remove(0) : null;
    }

    public boolean isLast() {
        return command.getMethod().getParameters().length - 1 == index;
    }

    @NotNull
    public Parameter getParameter() {
        return command.getMethod().getParameters()[index];
    }
}
