package me.bramhaag.bcf;

import me.bramhaag.bcf.annotations.Command;
import me.bramhaag.bcf.annotations.CommandBase;
import me.bramhaag.bcf.annotations.Permission;
import me.bramhaag.bcf.annotations.Subcommand;
import me.bramhaag.bcf.annotations.Syntax;
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

        Permission permission = commandClass.getAnnotation(Permission.class);
        Syntax syntax = commandClass.getAnnotation(Syntax.class);

        Method baseMethod = Arrays.stream(commandClass.getMethods()).filter(m -> m.isAnnotationPresent(CommandBase.class)).findFirst().orElse(null);
        if(baseMethod == null) {
            throw new NoBaseCommandException(commandClass);
        }

        if(baseMethod.getParameters()[0].getType() != CommandContext.class) {
            throw new InvalidCommandException("Cannot register " + parts[0] + "! CommandContext is missing or not the first parameter in the base method!");
        }

        List<CommandData> subcommands = Arrays.stream(commandClass.getMethods()).filter(method -> method.isAnnotationPresent(Subcommand.class)).map(method -> {
            Subcommand subcommand = method.getAnnotation(Subcommand.class);
            Permission scPermission = method.getAnnotation(Permission.class);
            Syntax scSyntax = method.getAnnotation(Syntax.class);

            String[] scParts = subcommand.value().split("\\|");

            if(method.getParameters()[0].getType() != CommandContext.class) {
                throw new InvalidCommandException("Cannot register " + parts[0] + " " + scParts[0] + "! CommandContext is missing or not the first parameter in the base method!");
            }

            return new CommandData(
                    scParts[0],
                    scParts.length > 1 ? Arrays.asList(Arrays.copyOfRange(scParts, 1, scParts.length)) : new ArrayList<>(),
                    scPermission == null ? null : scPermission.value(),
                    scSyntax == null ? null : scSyntax.value(),
                    executor, method
            );
        }).collect(Collectors.toList());

        commands.put(
                new CommandData(
                        parts[0],
                        parts.length > 1 ? Arrays.asList(Arrays.copyOfRange(parts, 1, parts.length)) : new ArrayList<>(),
                        permission == null ? null : permission.value(),
                        syntax == null ? null : syntax.value(),
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
