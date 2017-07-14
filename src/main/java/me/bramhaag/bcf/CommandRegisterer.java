package me.bramhaag.bcf;

import me.bramhaag.bcf.annotations.Command;
import me.bramhaag.bcf.annotations.CommandBase;
import me.bramhaag.bcf.annotations.CommandFlags;
import me.bramhaag.bcf.annotations.Subcommand;
import me.bramhaag.bcf.annotations.CommandMeta;
import me.bramhaag.bcf.exceptions.InvalidCommandException;
import me.bramhaag.bcf.exceptions.NoBaseCommandException;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class CommandRegisterer {

    @NotNull
    private HashMap<CommandData, List<CommandData>> commands = new HashMap<>();

    /**
     * Register a command
     * @param executor instance of the command class
     */
    public void register(@NotNull Object executor) {
        Class<?> commandClass = executor.getClass();
        Command command = commandClass.getAnnotation(Command.class);
        if(command == null) {
            return;
        }

        String[] parts = command.value().split("\\|");

        Method baseMethod = Arrays.stream(commandClass.getMethods()).filter(m -> m.isAnnotationPresent(CommandBase.class)).findFirst().orElse(null);
        if(baseMethod == null) {
            throw new NoBaseCommandException(commandClass);
        }

        CommandFlags flags = baseMethod.getAnnotation(CommandFlags.class);
        CommandMeta meta = baseMethod.getAnnotation(CommandMeta.class);

        if(baseMethod.getParameters()[0].getType() != CommandContext.class) {
            throw new InvalidCommandException("Cannot register " + parts[0] + "! CommandContext is missing or not the first parameter in the base method!");
        }

        List<CommandData> subcommands = Arrays.stream(commandClass.getMethods()).filter(method -> method.isAnnotationPresent(Subcommand.class)).map(method -> {
            Subcommand subcommand = method.getAnnotation(Subcommand.class);
            CommandFlags scFlags = commandClass.getAnnotation(CommandFlags.class);
            CommandMeta scMeta = commandClass.getAnnotation(CommandMeta.class);

            String[] scParts = subcommand.value().split("\\|");

            if(method.getParameters()[0].getType() != CommandContext.class) {
                throw new InvalidCommandException("Cannot register " + parts[0] + " " + scParts[0] + "! CommandContext is missing or not the first parameter in the base method!");
            }

            return new CommandData(
                    scParts[0],
                    scParts.length > 1 ? Arrays.asList(Arrays.copyOfRange(scParts, 1, scParts.length)) : new ArrayList<>(),
                    scFlags == null ? null : scFlags.value(),
                    scMeta == null ? null : scMeta.usage(),
                    scMeta == null ? null : scMeta.description(),
                    executor, method
            );
        }).collect(Collectors.toList());

        commands.put(
                new CommandData(
                        parts[0],
                        parts.length > 1 ? Arrays.asList(Arrays.copyOfRange(parts, 1, parts.length)) : new ArrayList<>(),
                        flags == null ? null : flags.value(),
                        meta == null ? null : meta.usage(),
                        meta == null ? null : meta.description(),
                        executor, baseMethod
                ), subcommands);
    }

    /**
     * Get all registered commands
     * @return all registered commands
     */
    @NotNull
    public HashMap<CommandData, List<CommandData>> getCommands() {
        return commands;
    }
}
