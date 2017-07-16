package me.bramhaag.bcf.resolver.resolvers;

import me.bramhaag.bcf.resolver.ArgumentData;
import me.bramhaag.bcf.resolver.ArgumentsResolver;
import me.bramhaag.bcf.resolver.ResolverUtil;

public class JavaArgumentResolver extends ArgumentsResolver {

    @Override
    public void register() {
        registerResolver(byte.class, Byte.class, c -> ResolverUtil.parseNumber(c.pop()).byteValue());
        registerResolver(short.class, Short.class, c -> ResolverUtil.parseNumber(c.pop()).shortValue());
        registerResolver(int.class, Integer.class, c -> ResolverUtil.parseNumber(c.pop()).intValue());
        registerResolver(long.class, Long.class, c -> ResolverUtil.parseNumber(c.pop()).longValue());
        registerResolver(float.class, Float.class, c -> ResolverUtil.parseNumber(c.pop()).floatValue());
        registerResolver(double.class, Double.class, c -> ResolverUtil.parseNumber(c.pop()).doubleValue());
        registerResolver(boolean.class, Boolean.class, c -> Boolean.valueOf(c.pop()));
        registerResolver(char.class, Character.class, c -> {
            String value = c.pop();
            if(value.length() > 1) {
                throw new IllegalArgumentException("Cannot convert String (length > 1) to a Character");
            }

            return value.charAt(0);
        });

        registerResolver(Number.class,  c -> ResolverUtil.parseNumber(c.pop()));
        registerResolver(String.class, ArgumentData::pop);
        registerResolver(Object.class, ArgumentData::pop);
        registerResolver(String[].class, c -> {
            if(c.isLast()) {
                String[] value = c.getArgs().toArray(new String[0]);
                c.getArgs().clear();

                return value;
            }

            throw new IllegalArgumentException("String[] has to be last!");
        });
    }
}
