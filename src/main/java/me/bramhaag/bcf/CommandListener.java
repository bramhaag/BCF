package me.bramhaag.bcf;

import me.bramhaag.bcf.context.Context;
import me.bramhaag.bcf.context.DefaultContext;
import me.bramhaag.bcf.exceptions.CommandExecutionException;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        if(!e.getMessage().getRawContent().startsWith(prefix)) return;
        
        String[] parts = e.getMessage().getRawContent().split(" ");
        String name = parts[0].substring(prefix.length());
        
        CommandData command = registerer.getCommands().keySet().stream().filter(c -> c.getName().equalsIgnoreCase(name) || c.getAliases().contains(name.toLowerCase())).findFirst().orElse(null);
        
        if(command == null) {
            bcf.onCommandNotFound();
            return;
        }
        
        String[] args = parts.length > 1 ? Arrays.copyOfRange(parts, 1, parts.length) : new String[0];
        
        if(args.length == 0 && command.getRequiredResolvers() == 0) {
            execute(e, command.getExecutor(), command.getMethod());
            return;
        }
        
        CommandData subcommand = registerer.getCommands().get(command).stream().filter(sc -> sc.getName().equalsIgnoreCase(args[0]) || sc.getAliases().contains(args[0].toLowerCase())).findFirst().orElse(null);
        if(subcommand != null) {
            execute(e, command.getExecutor(), subcommand.getMethod(), subcommand.resolve(args.length > 1 ? Arrays.copyOfRange(args, 1, args.length) : new String[0]).values().toArray());
            return;
        }
        
        try {
            execute(e, command.getExecutor(), command.getMethod(), command.resolve(args).values().toArray());
        } catch (CommandExecutionException ex) {
            if(ex.getCause().getClass() != IllegalArgumentException.class) throw ex;
            
            bcf.onCommandNotFound();
        }
    }
    
    private void execute(@NotNull MessageReceivedEvent e, @NotNull Object executor, @NotNull Method method,
                         @NotNull Object... args) {
        try {
            Context context = bcf.getContext() == null
                    ? new DefaultContext(e.getGuild(), e.getJDA(), e.getAuthor(), e.getMember(), e.getChannel())
                    : bcf.getContext();
            
            List<Object> listArgs = new ArrayList<>(Arrays.asList(args));
            listArgs.add(0, context);
            method.invoke(executor, listArgs.toArray());
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException ex) {
            throw new CommandExecutionException("An error occurred while executing " + executor.getClass().getSimpleName(), ex);
        }
    }
    
    @NotNull
    public String getPrefix() {
        return prefix;
    }
    
    public void setPrefix(@NotNull String prefix) {
        this.prefix = prefix;
    }
}
