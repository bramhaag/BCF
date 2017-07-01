package me.bramhaag.bcf.resolver;

import java.util.HashMap;
import java.util.Map;

public class ArgumentsResolver {

    private static Map<Class<?>, ArgumentResolver<?>> resolverMap = new HashMap<>();

    public <T> void registerResolver(Class<T> primitive, Class<T> wrapper, ArgumentResolver<T> supplier) {
        registerResolver(primitive, supplier);
        registerResolver(wrapper, supplier);
    }

    public <T> void registerResolver(Class<T> context, ArgumentResolver<T> supplier) {
        resolverMap.put(context, supplier);
    }

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

    public static Map<Class<?>, ArgumentResolver<?>> getResolverMap() {
        return resolverMap;
    }

    public void register() {
        throw new UnsupportedOperationException("Register method not implemented!");
    }
}
