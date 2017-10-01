package me.bramhaag.bcf.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Subcommand {
    String name();
    String usage() default "";
    String description() default "No description provided";
    String[] aliases() default {};
}
