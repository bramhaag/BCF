package me.bramhaag.bcf;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CommandListener extends ListenerAdapter {

    private BCF bcf;
    @Getter @Setter
    private String prefix;
    private CommandRegisterer registerer;

    public CommandListener(BCF bcf, String prefix, CommandRegisterer registerer) {
        this.bcf = bcf;
        this.prefix = prefix;
        this.registerer = registerer;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        if(!e.getMessage().getRawContent().startsWith(prefix)) {
            return;
        }
        String[] parts = e.getMessage().getRawContent().split(" ");

        String name = parts[0].substring(prefix.length());
        CommandData command = registerer.getCommands().keySet().stream().filter(c -> c.getName().equalsIgnoreCase(name) || c.getAliases().contains(name.toLowerCase())).findFirst().orElse(null);

        if(command == null) {
            //TODO
            e.getChannel().sendMessage("Command not found!");
            return;
        }

        String[] rawArgs = parts.length > 1 ? Arrays.copyOfRange(parts, 1, parts.length) : new String[0];
        if(command.getMethod().getParameters().length >= 2) {
            execute(e, command.getExecutor(), command.getMethod(), new Object[] { rawArgs });
        }
        else if(rawArgs.length == 0) {
            execute(e, command.getExecutor(), command.getMethod());
        }
        else {
            CommandData subcommand = registerer.getCommands().get(command).stream().filter(sc -> sc.getName().equalsIgnoreCase(rawArgs[0]) || sc.getAliases().contains(rawArgs[0].toLowerCase())).findFirst().orElse(null);
            if(subcommand == null) {
                System.err.println("Not found :<");
                return;
            }

            execute(e, command.getExecutor(), subcommand.getMethod(), subcommand.resolve(new ArrayList<>(Arrays.asList(rawArgs.length > 1 ? Arrays.copyOfRange(rawArgs, 1, rawArgs.length) : new String[0]))).values().toArray());
        }
    }

    private void execute(MessageReceivedEvent e, Object executor, Method method, Object... args) {
        try {
            List<Object> listArgs = new ArrayList<>(Arrays.asList(args));
            listArgs.add(0, new CommandContext(e.getJDA(), e.getAuthor(), e.getMessage(), e.getChannel(), e.getGuild()));
            method.invoke(executor, listArgs.toArray());
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException ex) {
            System.out.println(ex.getClass().getSimpleName() + ": " + ex.getMessage());
        }
    }
}
