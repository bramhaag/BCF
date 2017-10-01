package me.bramhaag.bcf;

import me.bramhaag.bcf.annotations.Default;
import me.bramhaag.bcf.annotations.Optional;
import me.bramhaag.bcf.resolver.ArgumentData;
import me.bramhaag.bcf.resolver.ArgumentResolver;
import me.bramhaag.bcf.resolver.ArgumentsResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class CommandData {

    @NotNull private final String name;
    @NotNull private final List<String> aliases;
    @Nullable private final String usage;
    @Nullable private final String description;

    @NotNull private Object executor;

    @NotNull private Method method;

    @NotNull private ArgumentResolver<?>[] resolvers;

    private final int requiredResolvers;
    private final int optionalResolvers;

    public CommandData(@NotNull String name, @NotNull List<String> aliases, @Nullable String usage, @Nullable String description, @NotNull Object executor, @NotNull Method method) {
        this.name = name;
        this.aliases = aliases;
        this.usage = usage;
        this.description = description;
        this.executor = executor;
        this.method = method;

        int requiredResolvers = 0;
        int optionalResolvers = 0;

        resolvers = new ArgumentResolver[method.getParameterCount() - 1];
        for (int i = 0; i < method.getParameterCount(); i++) {
            if(i == 0) {
                continue;
            }

            final Parameter parameter = method.getParameters()[i];
            final Class<?> type = parameter.getType();

            final ArgumentResolver<?> resolver = ArgumentsResolver.getResolverMap().get(type);

            if (resolver != null) {
                resolvers[i - 1] = resolver;
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

    @NotNull
    public Map<String, Object> resolve(@NotNull String[] argsArray) {
        Map<String, Object> resolvedArgs = new LinkedHashMap<>();
        List<String> args = new ArrayList<>(Arrays.asList(argsArray));

        int remainingRequired = requiredResolvers;

        for(int i = 0; i < method.getParameterCount(); i++) {
            if(i == 0) {
                continue;
            }

            Parameter parameter = method.getParameters()[i];

            ArgumentResolver<?> resolver = resolvers[i - 1];
            if(!(parameter.getAnnotation(Optional.class) != null || parameter.getAnnotation(Default.class) != null)) {
                remainingRequired--;
            }

            boolean isLast = i - 1 == method.getParameterCount() - 1;
            boolean allowOptional = remainingRequired == 0;

            if(args.isEmpty() && !(isLast && parameter.getType() == String[].class)) {
                Default def = parameter.getAnnotation(Default.class);
                Optional opt = parameter.getAnnotation(Optional.class);

                if(allowOptional && def != null) {
                    args.add(def.value());
                }
                else if(allowOptional && opt != null) {
                    resolvedArgs.put(parameter.getName(), resolver.getResolver(new ArgumentData(i, this, args)));
                    continue;
                }
            }

            resolvedArgs.put(parameter.getName(), resolver.getResolver(new ArgumentData(i, this, args)));
        }

        return resolvedArgs;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public List<String> getAliases() {
        return aliases;
    }

    @Nullable
    public String getUsage() {
        return usage;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    @NotNull
    public Object getExecutor() {
        return executor;
    }

    @NotNull
    public Method getMethod() {
        return method;
    }

    @NotNull
    public ArgumentResolver<?>[] getResolvers() {
        return resolvers;
    }

    public int getRequiredResolvers() {
        return requiredResolvers;
    }

    public int getOptionalResolvers() {
        return optionalResolvers;
    }
}
