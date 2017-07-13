package me.bramhaag.bcf;

import me.bramhaag.bcf.annotations.CommandBase;
import me.bramhaag.bcf.annotations.CommandFlags;
import me.bramhaag.bcf.annotations.Flag;
import me.bramhaag.bcf.resolver.ArgumentsResolver;
import me.bramhaag.bcf.resolver.ArgumentResolver;
import me.bramhaag.bcf.resolver.resolvers.JDAArgumentResolver;
import me.bramhaag.bcf.resolver.resolvers.JavaArgumentResolver;
import net.dv8tion.jda.core.JDA;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.stream.Collectors;

public class BCF {

    @NotNull
    private CommandRegisterer registerer;

    @NotNull
    private CommandListener listener;

    @NotNull
    private ArgumentsResolver resolvers = new ArgumentsResolver();

    @Nullable
    private Runnable commandNotFound;

    /**
     * Creates an empty BCF class. Please set a prefix using {@link BCF#setPrefix(String)}
     * @param jda instance of {@link JDA}
     */
    public BCF(@NotNull JDA jda) {
        this.registerer = new CommandRegisterer();
        this.listener = new CommandListener(this,"", registerer);

        jda.addEventListener(listener);

        new JavaArgumentResolver().register();
        new JDAArgumentResolver(jda).register();
    }

    /**
     * Set the prefix
     * @param prefix prefix
     * @return the {@link BCF} instance. Useful for chaining.
     */
    public BCF setPrefix(@NotNull String prefix) {
        listener.setPrefix(prefix);

        return this;
    }

    /**
     * Add an {@link ArgumentResolver}
     * @param type type to resolve
     * @param resolver resolver executed when resolving {@code type}
     * @param <T> type to resolve
     * @return the {@link BCF} instance. Useful for chaining.
     */
    public <T> BCF addResolver(@NotNull Class<T> type, @NotNull ArgumentResolver<T> resolver) {
        resolvers.registerResolver(type, resolver);

        return this;
    }

    /**
     * Register a command
     * @param executor instance of the command's class
     * @return the {@link BCF} instance. Useful for chaining.
     */
    public BCF register(@NotNull Object executor) {
        registerer.register(executor);

        return this;
    }

    public BCF onCommandNotFound(Runnable runnable) {
        this.commandNotFound = runnable;

        return this;
    }

    /**
     * Get {@link CommandRegisterer} which can be used for registering commands.
     * Consider using the {@link BCF#register(Object)} shortcut when registering commands.
     * @return CommandRegisterer
     */
    @NotNull
    public CommandRegisterer getRegisterer() {
        return registerer;
    }

    public void onCommandNotFound() {
        if(commandNotFound != null)
            commandNotFound.run();
    }
}
