package me.bramhaag.bcf;

import lombok.Getter;
import lombok.NonNull;
import me.bramhaag.bcf.resolver.ArgumentsResolver;
import me.bramhaag.bcf.resolver.ArgumentResolver;
import me.bramhaag.bcf.resolver.resolvers.JDAArgumentResolver;
import me.bramhaag.bcf.resolver.resolvers.JavaArgumentResolver;
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
    private ArgumentsResolver contexts = new ArgumentsResolver();

    public BCF(@NonNull JDA jda) {
        this.registerer = new CommandRegisterer();
        this.listener = new CommandListener(this, "", registerer);

        jda.addEventListener(listener);

        new JavaArgumentResolver().register();
        new JDAArgumentResolver(jda).register();

        System.out.println("Registered CommandContexts:");
        System.out.println(String.join(", ", ArgumentsResolver.getResolverMap().keySet().stream().map(Class::getName).collect(Collectors.toList())));
    }

    public BCF setPrefix(@NonNull String prefix) {
        listener.setPrefix(prefix);

        return this;
    }

    public <T> BCF addContext(Class<T> type, ArgumentResolver<T> resolver) {
        contexts.registerResolver(type, resolver);

        return this;
    }

    public BCF register(@NonNull Object executor) {
        registerer.register(executor);

        return this;
    }
}
