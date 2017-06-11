package me.bramhaag.bcf;

import lombok.Data;
import lombok.NonNull;
import me.bramhaag.bcf.annotations.Default;
import me.bramhaag.bcf.annotations.Optional;
import me.bramhaag.bcf.context.CommandContext;
import me.bramhaag.bcf.context.CommandContexts;
import me.bramhaag.bcf.context.ContextResolver;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
public class CommandData {

    @NonNull
    private final String name;
    private final List<String> aliases;
    private final String permission;
    private final String syntax;

    @NonNull
    private BaseCommand executor;

    @NonNull
    private Method method;

    private ContextResolver<?>[] resolvers;

    private final int requiredResolvers;
    private final int optionalResolvers;

    public CommandData(String name, List<String> aliases, String permission, String syntax, BaseCommand executor, Method method) {
        this.name = name;
        this.aliases = aliases;
        this.permission = permission;
        this.syntax = syntax;
        this.executor = executor;
        this.method = method;

        int requiredResolvers = 0;
        int optionalResolvers = 0;

        resolvers = new ContextResolver[method.getParameterCount()];
        for (int i = 0; i < method.getParameterCount(); i++) {
            final Parameter parameter = method.getParameters()[i];
            final Class<?> type = parameter.getType();

            final ContextResolver<?> resolver = CommandContexts.getContextMap().get(type);

            if (resolver != null) {
                resolvers[i] = resolver;
                    if (parameter.getAnnotation(Optional.class) != null || parameter.getAnnotation(Default.class) != null) {
                        optionalResolvers++;
                    } else {
                        requiredResolvers++;
                    }
            } else {
                //TODO
                System.err.println("No resolver for class " + type.getName());
            }
        }

        this.requiredResolvers = requiredResolvers;
        this.optionalResolvers = optionalResolvers;
    }

    public Map<String, Object> resolve(List<String> args) {
        Map<String, Object> resolvedArgs = new LinkedHashMap<>();

        int remainingRequired = requiredResolvers;

        for(int i = 0; i < method.getParameterCount(); i++) {
            Parameter parameter = method.getParameters()[i];

            ContextResolver<?> resolver = resolvers[i];
            if(!(parameter.getAnnotation(Optional.class) != null || parameter.getAnnotation(Default.class) != null)) {
                remainingRequired--;
            }

            boolean isLast = i == method.getParameterCount() - 1;
            boolean allowOptional = remainingRequired == 0;

            if(args.isEmpty() && !(isLast && parameter.getType() == String[].class)) {
                Default def = parameter.getAnnotation(Default.class);
                Optional opt = parameter.getAnnotation(Optional.class);

                if(allowOptional && def != null) {
                    args.add(def.value());
                }
                else if(allowOptional && opt != null) {
                    resolvedArgs.put(parameter.getName(), resolver.getContext(new CommandContext(i, this, args)));
                    continue;
                }
            }

            resolvedArgs.put(parameter.getName(), resolver.getContext(new CommandContext(i, this, args)));
        }

        return resolvedArgs;
    }
}
