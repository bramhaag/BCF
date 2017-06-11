package me.bramhaag.bcf.context.contexts;

import me.bramhaag.bcf.context.CommandContext;
import me.bramhaag.bcf.context.CommandContexts;
import me.bramhaag.bcf.context.ContextUtil;

import java.util.Arrays;

public class JavaCommandContexts extends CommandContexts {

    public JavaCommandContexts() {
        registerPrimitiveContext(byte.class,   Byte.class,    c -> ContextUtil.parseNumber(c.pop()).byteValue());
        registerPrimitiveContext(short.class,  Short.class,   c -> ContextUtil.parseNumber(c.pop()).shortValue());
        registerPrimitiveContext(int.class,    Integer.class, c -> ContextUtil.parseNumber(c.pop()).intValue());
        registerPrimitiveContext(long.class,   Long.class,    c -> ContextUtil.parseNumber(c.pop()).longValue());
        registerPrimitiveContext(float.class,  Float.class,   c -> ContextUtil.parseNumber(c.pop()).floatValue());
        registerPrimitiveContext(double.class, Double.class,  c -> ContextUtil.parseNumber(c.pop()).doubleValue());

        registerContext(Number.class,  c -> ContextUtil.parseNumber(c.pop()));

        registerContext(Character.class, c -> {
            String value = c.pop();
            if(value.length() > 1) {
                throw new IllegalArgumentException("Cannot convert String (length > 1) to a Character");
            }

            return value.charAt(0);
        });

        registerContext(String.class, CommandContext::pop);
        registerContext(Boolean.class, c -> Boolean.valueOf(c.pop()));
        registerContext(Object.class, CommandContext::pop);

        registerContext(String[].class, c -> {
            if(c.isLast()) {
                String[] value = c.getArgs().toArray(new String[c.getCommand().getMethod().getParameterCount()]);
                c.getArgs().clear();

                return value;
            }

            throw new IllegalArgumentException("String[] has to be last!");
        });
    }
}
