package me.bramhaag.bcf.context;

public interface ContextResolver<C> {
    C getContext(CommandContext c);
}
