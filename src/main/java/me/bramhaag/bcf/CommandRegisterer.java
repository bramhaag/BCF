package me.bramhaag.bcf;

import lombok.Getter;
import me.bramhaag.bcf.annotations.Command;
import me.bramhaag.bcf.annotations.CommandBase;
import me.bramhaag.bcf.annotations.Permission;
import me.bramhaag.bcf.annotations.Subcommand;
import me.bramhaag.bcf.annotations.Syntax;
import me.bramhaag.bcf.exceptions.NoBaseCommandException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class CommandRegisterer {

    @Getter
    private HashMap<CommandData, List<CommandData>> commands = new HashMap<>();

    public void register(BaseCommand executor) {
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

        List<CommandData> subcommands = Arrays.stream(commandClass.getMethods()).filter(method -> method.isAnnotationPresent(Subcommand.class)).map(method -> {
            Subcommand subcommand = method.getAnnotation(Subcommand.class);
            Permission scPermission = method.getAnnotation(Permission.class);
            Syntax scSyntax = method.getAnnotation(Syntax.class);

            String[] scParts = subcommand.value().split("\\|");

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
}
