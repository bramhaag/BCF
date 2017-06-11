package me.bramhaag.bcf.context;

import java.util.HashMap;
import java.util.Map;

public class CommandContexts {

    private static Map<Class<?>, ContextResolver<?>> contextMap = new HashMap<>();

    public <T> void registerPrimitiveContext(Class<T> primitiveContext, Class<T> context, ContextResolver<T> supplier) {
        registerContext(primitiveContext, supplier);
        registerContext(context, supplier);
    }

    public <T> void registerContext(Class<T> context, ContextResolver<T> supplier) {
        contextMap.put(context, supplier);
    }

    public ContextResolver<?> getResolver(Class<?> type) {
        Class<?> rootType = type;
        do {
            if (type == Object.class) {
                break;
            }

            final ContextResolver<?> resolver = contextMap.get(type);
            if (resolver != null) {
                return resolver;
            }
        } while ((type = type.getSuperclass()) != null);

        //TODO
        System.err.println("No context resolver defined for " + rootType.getName());
        return null;
    }

    public static Map<Class<?>, ContextResolver<?>> getContextMap() {
        return contextMap;
    }
}
