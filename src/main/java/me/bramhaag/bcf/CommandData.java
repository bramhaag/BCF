package me.bramhaag.bcf;

import me.bramhaag.bcf.annotations.Default;
import me.bramhaag.bcf.annotations.Optional;
import me.bramhaag.bcf.resolver.ArgumentData;
import me.bramhaag.bcf.resolver.ArgumentsResolver;
import me.bramhaag.bcf.resolver.ArgumentResolver;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CommandData {

    private final String name;
    private final List<String> aliases;
    private final String permission;
    private final String syntax;

    private Object executor;

    private Method method;

    private ArgumentResolver<?>[] resolvers;

    private final int requiredResolvers;
    private final int optionalResolvers;

    public CommandData(String name, List<String> aliases, String permission, String syntax, Object executor, Method method) {
        this.name = name;
        this.aliases = aliases;
        this.permission = permission;
        this.syntax = syntax;
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

    public Map<String, Object> resolve(List<String> args) {
        Map<String, Object> resolvedArgs = new LinkedHashMap<>();

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

    public String getName() {
        return name;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public String getPermission() {
        return permission;
    }

    public String getSyntax() {
        return syntax;
    }

    public Object getExecutor() {
        return executor;
    }

    public Method getMethod() {
        return method;
    }

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
