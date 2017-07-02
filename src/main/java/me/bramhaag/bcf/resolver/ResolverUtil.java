package me.bramhaag.bcf.resolver;

import org.jetbrains.annotations.NotNull;

public class ResolverUtil {

    @NotNull
    public static Number parseNumber(@NotNull String number) {
        return Double.parseDouble(number);
    }
}
