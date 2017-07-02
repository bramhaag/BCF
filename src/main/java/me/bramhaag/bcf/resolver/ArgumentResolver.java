package me.bramhaag.bcf.resolver;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface ArgumentResolver<C> {
    C getResolver(@NotNull ArgumentData c);
}
