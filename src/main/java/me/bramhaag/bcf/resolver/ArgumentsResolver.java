package me.bramhaag.bcf.resolver;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ArgumentsResolver {

    @NotNull private static Map<Class<?>, ArgumentResolver<?>> resolverMap = new HashMap<>();

    public <T> void registerResolver(@NotNull Class<T> primitive, @NotNull Class<T> wrapper, @NotNull ArgumentResolver<T> supplier) {
        registerResolver(primitive, supplier);
        registerResolver(wrapper, supplier);
    }

    public <T> void registerResolver(@NotNull Class<T> context, @NotNull ArgumentResolver<T> supplier) {
        resolverMap.put(context, supplier);
    }

    @Nullable
    public ArgumentResolver<?> getResolver(Class<?> type) {
        Class<?> rootType = type;
        do {
            if (type == Object.class) {
                break;
            }

            final ArgumentResolver<?> resolver = resolverMap.get(type);
            if (resolver != null) {
                return resolver;
            }
        } while ((type = type.getSuperclass()) != null);

        //TODO
        System.err.println("No resolver resolver defined for " + rootType.getName());
        return null;
    }

    @NotNull
    public static Map<Class<?>, ArgumentResolver<?>> getResolverMap() {
        return resolverMap;
    }

    public void register() {
        throw new UnsupportedOperationException("Register method not implemented!");
    }
}
