package me.bramhaag.bcf;

import me.bramhaag.bcf.annotations.Command;
import me.bramhaag.bcf.annotations.CommandBase;
import me.bramhaag.bcf.annotations.Subcommand;
import me.bramhaag.bcf.exceptions.InvalidCommandException;
import me.bramhaag.bcf.exceptions.NoBaseCommandException;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CommandRegisterer {

    @NotNull
    private Map<CommandData, List<CommandData>> commands = new HashMap<>();

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

        Method baseMethod = Arrays.stream(commandClass.getMethods()).filter(m -> m.isAnnotationPresent(CommandBase.class)).findFirst().orElse(null);
        if(baseMethod == null) {
            throw new NoBaseCommandException(commandClass);
        }

        if(baseMethod.getParameters()[0].getType() != CommandContext.class) {
            throw new InvalidCommandException("Cannot register " + command.name() + "! Context is missing or not the first parameter in the base method!");
        }

        List<CommandData> subcommands = Arrays.stream(commandClass.getMethods()).filter(method -> method.isAnnotationPresent(Subcommand.class)).map(method -> {
            Subcommand subcommand = method.getAnnotation(Subcommand.class);

            if(method.getParameters()[0].getType() != CommandContext.class) {
                throw new InvalidCommandException("Cannot register " + command.name() + " " + subcommand.name() + "! Context is missing or not the first parameter in the base method!");
            }

            return new CommandData(
                    subcommand.name(),
                    Arrays.asList(subcommand.aliases()),
                    subcommand.usage(),
                    subcommand.description(),
                    executor, method
            );
        }).collect(Collectors.toList());

        commands.put(
                new CommandData(
                        command.name(),
                        Arrays.asList(command.aliases()),
                        command.usage(),
                        command.description(),
                        executor, baseMethod
                ), subcommands);
    }

    /**
     * Get all registered commands
     * @return all registered commands
     */
    @NotNull
    public Map<CommandData, List<CommandData>> getCommands() {
        return commands;
    }
}
