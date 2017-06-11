package me.bramhaag.bcf;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.bramhaag.bcf.context.CommandContexts;
import me.bramhaag.bcf.context.ContextResolver;
import me.bramhaag.bcf.context.contexts.JDACommandContexts;
import me.bramhaag.bcf.context.contexts.JavaCommandContexts;
import net.dv8tion.jda.core.JDA;

import java.util.stream.Collectors;

public class BCF {

    @Getter
    @NonNull
    private CommandRegisterer registerer;

    @NonNull
    private CommandListener listener;

    @Getter
    @NonNull
    private CommandContexts contexts = new CommandContexts();

    public BCF(@NonNull JDA jda) {
        this.registerer = new CommandRegisterer();
        this.listener = new CommandListener(this, "", registerer);

        jda.addEventListener(listener);

        new JavaCommandContexts();
        new JDACommandContexts(jda);

        System.out.println("Registered CommandContexts:");
        System.out.println(String.join(", ", CommandContexts.getContextMap().keySet().stream().map(Class::getName).collect(Collectors.toList())));
    }

    public BCF setPrefix(@NonNull String prefix) {
        listener.setPrefix(prefix);

        return this;
    }

    public <T> BCF addContext(Class<T> type, ContextResolver<T> resolver) {
        contexts.registerContext(type, resolver);

        return this;
    }

    public BCF register(@NonNull BaseCommand executor) {
        registerer.register(executor);

        return this;
    }
}
