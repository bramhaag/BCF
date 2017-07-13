package me.bramhaag.bcf.annotations;

public @interface Flag {
    String name();
    boolean nullable() default false;
    boolean required() default false;
}
