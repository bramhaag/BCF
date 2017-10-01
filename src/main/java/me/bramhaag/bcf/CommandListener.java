package me.bramhaag.bcf;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import me.bramhaag.bcf.annotations.CommandFlags;
import me.bramhaag.bcf.annotations.Flag;
import me.bramhaag.bcf.exceptions.CommandExecutionException;
import me.bramhaag.bcf.util.Triple;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

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
        Set<String> switches;
        
        String[] tempArgs = parts.length > 1 ? (Arrays.copyOfRange(parts, 1, parts.length)) : new String[0];
        
        if(command.getMethod().isAnnotationPresent(CommandFlags.class)) {
            Triple<Map<String, String>, Set<String>, List<String>> triple = separate(command.getMethod().getAnnotation(CommandFlags.class), tempArgs);
            flags = triple.getLeft();
            switches = triple.getMiddle();
            rawArgs = triple.getRight().toArray(new String[0]);
        } else {
            flags = new HashMap<>();
            switches = new HashSet<>();
            rawArgs = tempArgs;
        }
        
        if (bcf.getPreCommandTask() != null) bcf.getPreCommandTask().accept(e);
        
        if(rawArgs.length == 0 && command.getRequiredResolvers() == 0) {
            execute(e, command.getExecutor(), command.getMethod(), flags, switches);
            return;
        }
        
        CommandData subcommand = registerer.getCommands().get(command).stream().filter(sc -> sc.getName().equalsIgnoreCase(rawArgs[0]) || sc.getAliases().contains(rawArgs[0].toLowerCase())).findFirst().orElse(null);
        if(subcommand != null) {
            execute(e, command.getExecutor(), subcommand.getMethod(), flags, switches, subcommand.resolve(rawArgs.length > 1 ? Arrays.copyOfRange(rawArgs, 1, rawArgs.length) : new String[0]).values().toArray());
            return;
        }
        
        try {
            execute(e, command.getExecutor(), command.getMethod(), flags, switches, command.resolve(rawArgs).values().toArray());
        } catch (CommandExecutionException ex) {
            if(ex.getCause().getClass() != IllegalArgumentException.class) throw ex;
            
            bcf.onCommandNotFound();
        }
    }
    
    private void execute(@NotNull MessageReceivedEvent e, @NotNull Object executor, @NotNull Method method,
                         @NotNull Map<String, String> flags, @NotNull Set<String> switches, @NotNull Object... args) {
        try {
            List<Object> listArgs = new ArrayList<>(Arrays.asList(args));
            listArgs.add(0, new CommandContext(e.getJDA(), e.getAuthor(), e.getMessage(), e.getChannel(), e.getGuild(), flags, switches));
            method.invoke(executor, listArgs.toArray());
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException ex) {
            throw new CommandExecutionException("An error occurred while executing " + executor.getClass().getSimpleName(), ex);
        }
    }
    
    private Triple<Map<String, String>, Set<String>, List<String>> separate(CommandFlags commandFlags, String[] arguments) {
        OptionParser parser = new OptionParser();
        parser.allowsUnrecognizedOptions();
        
        Set<String> foundSwitches = new HashSet<>();
        Set<String> switches = new HashSet<>();
        
        for(Flag flag : commandFlags.value()) {
            if (flag.switchFlag()) {
                parser.accepts(flag.name());
                foundSwitches.add(flag.name());
            } else if(flag.nullable()) parser.accepts(flag.name());
            else parser.accepts(flag.name()).withRequiredArg();
        }
        
        OptionSet parse = parser.parse(arguments);
        Map<OptionSpec<?>, List<?>> map = parse.asMap();
        Map<String, String> flags = map.entrySet().stream()
                .filter(e -> e.getKey().options().get(0) != null)
                .collect(HashMap::new, (m, e) -> {
                    String key = e.getKey().options().get(0);
                    if (foundSwitches.contains(key) && parse.has(key)) switches.add(key);
                    else m.put(key, !e.getValue().isEmpty() && e.getValue().get(0) instanceof String ? (String)e.getValue().get(0) : null);
                }, HashMap::putAll);
        if(Arrays.stream(commandFlags.value()).filter(f -> f.required() && !flags.containsKey(f.name())).findAny().orElse(null) != null) {
            throw new IllegalStateException("Not enough flags!");
        }
        
        return new Triple<>(flags, switches, (List<String>)parse.nonOptionArguments());
    }
    
    @NotNull
    public String getPrefix() {
        return prefix;
    }
    
    public void setPrefix(@NotNull String prefix) {
        this.prefix = prefix;
    }
}
