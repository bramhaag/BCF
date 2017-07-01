package me.bramhaag.bcf.resolver;

@FunctionalInterface
public interface ArgumentResolver<C> {
    C getResolver(ArgumentData c);
}
