package me.bramhaag.bcf;

import javafx.util.Pair;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import joptsimple.OptionSpecBuilder;
import me.bramhaag.bcf.annotations.CommandFlags;
import me.bramhaag.bcf.annotations.Flag;
import me.bramhaag.bcf.exceptions.CommandExecutionException;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class CommandListener extends ListenerAdapter {

    @NotNull private BCF bcf;

    @NotNull private String prefix;
    @NotNull private CommandRegisterer registerer;

    public CommandListener(@NotNull BCF bcf, @NotNull String prefix, @NotNull CommandRegisterer registerer) {
        this.bcf = bcf;
        this.prefix = prefix;
        this.registerer = registerer;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent e) {
        if(!e.getMessage().getRawContent().startsWith(prefix)) {
            return;
        }
        String[] parts = e.getMessage().getRawContent().split(" ");
        String name = parts[0].substring(prefix.length());

        CommandData command = registerer.getCommands().keySet().stream().filter(c -> c.getName().equalsIgnoreCase(name) || c.getAliases().contains(name.toLowerCase())).findFirst().orElse(null);

        if(command == null) {
            bcf.onCommandNotFound();
            return;
        }

        String[] rawArgs;
        Map<String, String> flags;

        String[] tempArgs = parts.length > 1 ? (Arrays.copyOfRange(parts, 1, parts.length)) : new String[0];

        if(command.getMethod().isAnnotationPresent(CommandFlags.class)) {
            Pair<Map<String, String>, List<String>> pair = separate(command.getMethod().getAnnotation(CommandFlags.class), tempArgs);
            flags = pair.getKey();
            rawArgs = pair.getValue().toArray(new String[0]);
        } else {
            flags = new HashMap<>();
            rawArgs = tempArgs;
        }

        if(rawArgs.length == 0 && command.getRequiredResolvers() == 0) {
            execute(e, command.getExecutor(), command.getMethod(), flags);
            return;
        }

        CommandData subcommand = registerer.getCommands().get(command).stream().filter(sc -> sc.getName().equalsIgnoreCase(rawArgs[0]) || sc.getAliases().contains(rawArgs[0].toLowerCase())).findFirst().orElse(null);
        if(subcommand != null) {
            execute(e, command.getExecutor(), subcommand.getMethod(), flags, subcommand.resolve(rawArgs.length > 1 ? Arrays.copyOfRange(rawArgs, 1, rawArgs.length) : new String[0]).values().toArray());
            return;
        }

        try {
            execute(e, command.getExecutor(), command.getMethod(), flags, command.resolve(rawArgs).values().toArray());
        } catch (CommandExecutionException ex) {
            if(ex.getCause().getClass() != IllegalArgumentException.class) throw ex;

            bcf.onCommandNotFound();
        }
    }

    private void execute(@NotNull MessageReceivedEvent e, @NotNull Object executor, @NotNull Method method, @NotNull Map<String, String> flags, @NotNull Object... args) {
        try {
            List<Object> listArgs = new ArrayList<>(Arrays.asList(args));
            listArgs.add(0, new CommandContext(e.getJDA(), e.getAuthor(), e.getMessage(), e.getChannel(), e.getGuild(), flags));
            method.invoke(executor, listArgs.toArray());
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException ex) {
            throw new CommandExecutionException("An error occurred while executing " + executor.getClass().getSimpleName(), ex);
        }
    }

    private Pair<Map<String, String>, List<String>> separate(CommandFlags commandFlags, String[] arguments) {
        OptionParser parser = new OptionParser();
        parser.allowsUnrecognizedOptions();

        for(Flag flag : commandFlags.value()) {
            ArgumentAcceptingOptionSpec<String> builder;

            if(flag.nullable()) builder = parser.accepts(flag.name()).withOptionalArg();
            else builder = parser.accepts(flag.name()).withRequiredArg();

            if(flag.required()) builder.required();
        }

        OptionSet parse = parser.parse(arguments);
        Map<OptionSpec<?>, List<?>> map = parse.asMap();
        Map<String, String> flags = map.entrySet().stream()
                .collect(Collectors.toMap(x -> x.getKey().options().get(0), x -> x.getValue().size() > 0 ? (String) x.getValue().get(0) : null));

        return new Pair<>(flags, (List<String>)parse.nonOptionArguments());
    }

    @NotNull
    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(@NotNull String prefix) {
        this.prefix = prefix;
    }
}
